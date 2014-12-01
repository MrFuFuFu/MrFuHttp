package com.mrfu.http.lib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.mrfu.http.AppApplication;
import com.mrfu.http.lib.NetworkStateReceiver.NetworkState;
import com.mrfu.http.lib.RequestParams.FileWrapper;
import com.mrfu.http.util.JSONUtils;
import com.mrfu.http.util.MD5Util;
import com.mrfu.http.util.RC4Util;
import com.mrfu.http.util.UpdateModel;

public class HttpRequestPost implements Runnable {

	// public static final String url = "http://192.168.0.127/service";
	// public static final String url = "http://192.168.0.105/service";
	// public static final String url = "http://122.225.114.19:28080/service";
//	public static String url = "http://122.225.114.30:44002/service";
//	public static String url = "http://122.225.8.212:44002/service";
//	public static String url = "http://192.168.10.40:6060/service";
//	public static String url = "http://192.168.10.82:8080/service";
	public static String url = "http://122.225.8.212:44002/service";
	
	// public static String url = "http://192.168.0.104:6060/service";
//	 public static String url = "http://api.nsjnqc.com/service";
//	 public static String url = "http://api2.nsjnqc.com/service";
	// public static String url = "http://api3.nsjnqc.com/service";
//	 public static String url = "http://122.225.114.20:28080/service"; 
	// public static String url = "http://122.225.114.19:28080/service";
	private static final String kTFCommandKey1 = "78d2204c48baa1c6e30fb3dc7ab61d1e2b414b6ec6f3fc3406566e90657453f6f4d5ea7f7a06a2d2a231f1bbf330445959dd6a0be8963ed5d8176f57992768be";
	private static final String kTFCommandKey2 = "32cfd07149b91cad149b189db024eb110258af8691f752fa842e42d3b57e43d5712115ec41ee4d0090fb47796bec5b70ba085f6a1723263151f571f6ae2c62ad";
	private static final int CONNECT_TRY_TIME = 2;
	private static final String RC4_KEY = "78d2204c48baa1c6e30fb3dc7ab61d1e2b414b6ec6f3fc3406566e90657453f6f4d5ea7f7a06a2d2a231f1bbf330445959dd6a0be8963ed5d8176f57992768be";

	protected static final String BOUNDARY = "--293iosfksdfkiowjksdf31jsiuwq003s02dsaffafass3qw";// ??��???????�线
	protected static final String ENTER = "\r\n";
	protected static final String DISPOSITION = "Content-Disposition: form-data; name=";
	protected static final String FILENAME = "; filename=";
	protected static final String CONTENTTYPE = "Content-Type: ";
	protected static final String TRANSFER = "Content-Transfer-Encoding: binary";

	public static final String REQUEST_ERROR = "网络不给力";
	private static Map<String, String> publicParams;
	private static Handler handler;
	private RequestParams params;
	private HttpPostCallback callback;
	private boolean needEncrypt;// 是否需要加密

	static {
		 System.setProperty("http.maxConnections", "15");
		// myX509TrustManager xtm = new myX509TrustManager();
		// myHostnameVerifier hnv = new myHostnameVerifier();
		// SSLContext sslContext = null;
		// try {
		// sslContext = SSLContext.getInstance("TLS"); // 或SSL
		// X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
		// sslContext.init(null, xtmArray, new java.security.SecureRandom());
		// } catch (GeneralSecurityException e) {
		// e.printStackTrace();
		// }
		// if (sslContext != null) {
		// HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
		// .getSocketFactory());
		// }
		// HttpsURLConnection.setDefaultHostnameVerifier(hnv);
		if (Build.VERSION.SDK_INT > 13) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());
		}
	}

	public HttpRequestPost(RequestParams requestParams,
			HttpPostCallback callback) {
		this.params = requestParams;
		this.callback = callback;
		if (handler == null) {
			handler = new Handler(Looper.getMainLooper());
		}
	}

	public HttpRequestPost(RequestParams requestParams,
			HttpPostCallback callback, boolean needEncrypt) {
		this(requestParams, callback);
		this.needEncrypt = needEncrypt;
	}

	public static synchronized void setPublicParams(Map<String, String> pParams) {
		publicParams = pParams;
	}

	private boolean hasFile() {
		if (params != null) {
			return params.getFileParams().size() > 0 ? true : false;
		}
		return false;
	}

	private int calSize() {
		int length = 0;
		Set<Map.Entry<String, FileWrapper>> set = params.fileParams.entrySet();
		Iterator<Map.Entry<String, FileWrapper>> iterator = set.iterator();
		StringBuilder builder = new StringBuilder();
		while (iterator.hasNext()) {
			Map.Entry<String, FileWrapper> entry = iterator.next();
			FileWrapper file = entry.getValue();
			length += file.getLength();
			builder.append(BOUNDARY);
			builder.append(ENTER);
			builder.append(DISPOSITION);
			builder.append("\"" + "file." + file.getFileType() + "\"");
			builder.append(FILENAME);
			builder.append("\"" + "file." + file.getFileType() + "\"");
			builder.append(ENTER);
			builder.append(CONTENTTYPE);
			builder.append("content/unknown");
			builder.append(ENTER);
			builder.append(TRANSFER);
			builder.append(ENTER);
			builder.append(ENTER);
			builder.append(ENTER.getBytes());
		}
		String end = BOUNDARY + "--" + ENTER;
		builder.append(end);
		length += builder.toString().getBytes().length;
		return length;
	}

	public void run() {
		URL myUrl = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream in = null;
		String content = getContent(params);
		String tempUrl = url;
		if (hasFile()) {
			tempUrl = url + "?" + content;
		}
		String errorStr = REQUEST_ERROR;
		if (AppApplication.getInstance().getNetworkState() == NetworkState.NET_NULL) {
			errorStr = "没有可用的网络";
		}
		boolean isProgress = ((callback instanceof HttpPostProgressCallback) && hasFile());
		int totalContentLength = 0;
		if (isProgress) {
			totalContentLength = calSize();
		}
		boolean isSuccess = false;
		boolean isConnectTimeout = false;
		for (int kk = 0; kk < CONNECT_TRY_TIME; kk++) {
			Log.d("kop", "CONNECT_TRY_TIME");
			try {
				myUrl = new URL(tempUrl);

				conn = (HttpURLConnection) myUrl.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setAllowUserInteraction(false);

				if (isProgress) {
					conn.setChunkedStreamingMode(128 * 1024);
				} else {
//					conn.setChunkedStreamingMode(0);
				}

				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				if (Build.VERSION.SDK_INT > 13) {
					// conn.setRequestProperty("Connection", "Keep-Alive");
					// conn.setRequestProperty("Connection", "close");
				} else {
					// conn.setRequestProperty("Connection",
					// "Keep-Alive:timeout=90,max=100");
				}
				conn.setRequestProperty("Accept-Encoding", "gzip");
				conn.setRequestProperty("Accept", "*/*");
				conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
				if (hasFile()) {
					conn.setRequestProperty("Content-Type",
							"multipart/form-data; boundary=293iosfksdfkiowjksdf31jsiuwq003s02dsaffafass3qw");
				} else {
					conn.setRequestProperty("Content-type",
							"application/x-www-form-urlencoded;charset=utf-8");
					if (needEncrypt) {
						conn.setRequestProperty("Encrypt", "1");
					}
				}
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);

				conn.connect();
				// body
				os = conn.getOutputStream();

				if (!isProgress && !isEmpty(content) && !hasFile()) {
					if (needEncrypt) {
						byte[] compressData = compress(content);
						os.write(RC4Util.decry_RC4(compressData, RC4_KEY));
					} else {
						os.write(content.getBytes());
					}

					os.flush();
				}
				if (hasFile()) {
					int uploadedLen = 0;
					byte[] buffer = new byte[1024];
					Set<Map.Entry<String, FileWrapper>> set = params.fileParams
							.entrySet();
					Iterator<Map.Entry<String, FileWrapper>> iterator = set
							.iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, FileWrapper> entry = iterator.next();
						StringBuilder builder = new StringBuilder();
						FileWrapper file = entry.getValue();
						builder.append(BOUNDARY);
						builder.append(ENTER);
						builder.append(DISPOSITION);
						builder.append("\"" + "file." + file.getFileType()
								+ "\"");
						builder.append(FILENAME);
						builder.append("\"" + "file." + file.getFileType()
								+ "\"");
						builder.append(ENTER);
						builder.append(CONTENTTYPE);
						builder.append("content/unknown");
						builder.append(ENTER);
						builder.append(TRANSFER);
						builder.append(ENTER);
						builder.append(ENTER);
						os.write(builder.toString().getBytes());
						os.flush();
						InputStream is = new FileInputStream(file.getFilePath());
						int len = 0;
						while ((len = is.read(buffer)) != -1) {
							os.write(buffer, 0, len);
							if (isProgress) {
								uploadedLen += len;
								int progress = uploadedLen * 100
										/ totalContentLength;
								((HttpPostProgressCallback) callback)
										.progressPublish(params, progress);
							}
							os.flush();
						}
						is.close();
						os.write(ENTER.getBytes());
						os.flush();
					}
				}
				if (hasFile()) {
					String end = BOUNDARY + "--" + ENTER;
					byte[] endData = end.getBytes();
					os.write(endData);
					os.flush();
				}

				int responseCode = conn.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					in = new GZIPInputStream(conn.getInputStream());
					ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
					int len;
					byte[] buffer = new byte[1024];
					while ((len = in.read(buffer)) != -1) {
						arrayOutputStream.write(buffer, 0, len);
					}
					in.close();
					arrayOutputStream.close();
					final String str = new String(
							arrayOutputStream.toByteArray(), "utf-8");
					if (!isEmpty(str)) {
						if (callback != null) {
							if (!TextUtils.isEmpty(str)) {
								final JSONObject jsonObject = new JSONObject(
										str);
								if (jsonObject != null) {
									String isChangeCookie = JSONUtils
											.getString(jsonObject,
													"writeCookie");
									if (!TextUtils.isEmpty(isChangeCookie)
											&& isChangeCookie
													.equalsIgnoreCase("yes")) {
//										String cookie = JSONUtils.getString(
//												jsonObject, "cookie");
//										AppApplication.getInstance().setCookie(
//												cookie);
									}
									String status = JSONUtils.getString(
											jsonObject, "resultStatus");
									params.put("resultStatus", status);
									if (!TextUtils.isEmpty(status)) {
										int code = Integer.parseInt(status);
										if (code == 100 || code == 202) {
											isSuccess = true;
											if (AppApplication.getInstance().updateModel == null
													&& code == 202) {
												AppApplication.getInstance().updateModel = new UpdateModel(
														jsonObject);
											}
											handler.post(new Runnable() {
												@Override
												public void run() {
													// TODO Auto-generated
													// method stub
													callback.requestFinished(
															params, jsonObject);
												}
											});
										} else if (code >= 300 || code < 100) {// 出错
											errorStr = JSONUtils.getString(
													jsonObject, "memo");
										} else if (code >= 200 && code < 300) {// 软件升级
																				// 202普通升级
																				// 203强制升级
											if (AppApplication.getInstance().updateModel == null) {
												AppApplication.getInstance().updateModel = new UpdateModel(
														jsonObject);
											}
										}
									}
								}
							}
						}
						Log.d("kop", "str = " + str);
					}
					close(in, os, conn);
					break;
				} else {
					close(in, os, conn);
				}
			} catch (Exception e0) {
				if (e0 instanceof SocketTimeoutException) {
					isConnectTimeout = true;
				}
				e0.printStackTrace();
				close(in, os, conn);
				continue;
			} finally {
				close(in, os, conn);
			}
		}
		if (!isSuccess && callback != null) {
			if (TextUtils.isEmpty(errorStr)) {
				errorStr = REQUEST_ERROR;
			}
			final String error = errorStr;
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						callback.requestFailed(params, error);
					} catch (Exception e) {

					}
				}
			});

		}
		if (isConnectTimeout) {
//			MobclickAgent.onEvent(AppApplication.getInstance(),
//					"ns_e_httppost_timeout", "IP:" + SystemUtil.getNetIp());
		}
	}

	private void close(InputStream in, OutputStream out, HttpURLConnection conn) {
		try {
			if (in != null) {
				in.close();
				in = null;
			}

		} catch (Exception e) {

		}
		try {
			if (out != null) {
				out.close();
				out = null;
			}
		} catch (Exception e) {

		}
		try {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		} catch (Exception e) {

		}
	}

	public static byte[] compress(String string) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes());
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}

	// public static String RC4(String aInput, String aKey) {
	// int[] iS = new int[256];
	// byte[] iK = new byte[256];
	//
	// for (int i = 0; i < 256; i++)
	// iS[i] = i;
	//
	// for (short i = 0; i < 256; i++) {
	// iK[i] = (byte) aKey.charAt((i % aKey.length()));
	// }
	//
	// int j = 0;
	//
	// for (int i = 0; i < 255; i++) {
	// j = (j + iS[i] + iK[i]) % 256;
	// int temp = iS[i];
	// iS[i] = iS[j];
	// iS[j] = temp;
	// }
	//
	// int i = 0;
	// j = 0;
	// char[] iInputChar = aInput.toCharArray();
	// char[] iOutputChar = new char[iInputChar.length];
	// for (short x = 0; x < iInputChar.length; x++) {
	// i = (i + 1) % 256;
	// j = (j + iS[i]) % 256;
	// int temp = iS[i];
	// iS[i] = iS[j];
	// iS[j] = temp;
	// int t = (iS[i] + (iS[j] % 256)) % 256;
	// int iY = iS[t];
	// char iCY = (char) iY;
	// iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
	// }
	//
	// return new String(iOutputChar);
	// }

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}

	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	
	private static void addElapsedTime(){
		if(publicParams != null){
			long time = SystemClock.elapsedRealtime();
			long minute = time/(1000*60);
			int hour = (int)(minute/60);
			int remain = (int)(minute%60);
			String str = hour+"小时"+remain+"分钟";
			publicParams.put("elapsedTime", str);
		}
	}

	private static String getContent(RequestParams params) {
		addElapsedTime();
		String time = String.valueOf(System.currentTimeMillis() / 1000);
		String operation = new Gson().toJson(params.getUrlParams());
		String sign = MD5Util.md5(operation + time + kTFCommandKey1);
		Map<String, Object> tempPublicParams = new HashMap<String, Object>();
		tempPublicParams.putAll(publicParams);
		tempPublicParams.put("time", time);
		tempPublicParams.put("sign", sign);

		Map<String, Object> mutable = new HashMap<String, Object>();
		mutable.put("operation", operation);// operation params.getUrlParams()
		mutable.put("public", tempPublicParams);// tempPublicParams

		String requestData = new Gson().toJson(mutable);
		String session = MD5Util.md5(requestData + kTFCommandKey2);
		try {
			requestData = URLEncoder.encode(requestData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String realUrl = HttpRequestPost.url + "?requestData=" + requestData
				+ "&session=" + session;
		Log.d("kop", "realUrl = " + realUrl);
		String content = "requestData=" + requestData + "&session=" + session;
		return content;
	}

	static class myX509TrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	static class myHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
}
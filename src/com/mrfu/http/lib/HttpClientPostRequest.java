package com.mrfu.http.lib;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.mrfu.http.AppApplication;
import com.mrfu.http.lib.NetworkStateReceiver.NetworkState;
import com.mrfu.http.util.JSONUtils;
import com.mrfu.http.util.MD5Util;
import com.mrfu.http.util.UpdateModel;

public class HttpClientPostRequest implements Runnable {

	public static String url = "http://www.baidu.com";
//	public static String url = "http://122.225.114.20:28080/service";
	private static final String kTFCommandKey1 = "aaa";
	private static final String kTFCommandKey2 = "bbb";
	private static final String RC4_KEY = "ccc";

	protected static final String BOUNDARY = "--293iosfksdfkiowjksdf31jsiuwq003s02dsaffafass3qw";// ??��???????�线

	private static HttpClient mClient = null;
	public static final String REQUEST_ERROR = "网络不给力";
	private static Map<String, String> publicParams;
	private RequestParams params;
	private HttpPostCallback callback;
	private boolean needEncrypt;// 是否需要加密
	private static Handler handler;

	public HttpClientPostRequest(RequestParams requestParams,
			HttpPostCallback callback) {
		this.params = requestParams;
		this.callback = callback;
		if (handler == null) {
			handler = new Handler(Looper.getMainLooper());
		}
		if (mClient == null) {
			initHttpClient();
		}
	}

	private static void initHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		// if (!TextUtils.isEmpty(MyApp.getProxy())) {
		// HttpHost proxy = new HttpHost(MyApp.getProxy(), MyApp.getPort());
		// httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		// }
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		mClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
				httpParams, schReg), httpParams);
		return;
	}

	public static synchronized void setPublicParams(Map<String, String> pParams) {
		publicParams = pParams;
	}

	private static String getContent(RequestParams params) {
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		HttpPost post = new HttpPost(url);
		StringEntity reqEntity = null;
		OutputStream os = null;
		InputStream in = null;
		String errorStr = REQUEST_ERROR;
		if (AppApplication.getInstance().getNetworkState() == NetworkState.NET_NULL) {
			errorStr = "没有可用的网络";
		}
		boolean isSuccess = false;
		try {
			reqEntity = new StringEntity(getContent(params));
			reqEntity
					.setContentType("application/x-www-form-urlencoded;charset=utf-8");
			post.setEntity(reqEntity);
			post.addHeader("Accept-Encoding", "gzip");
			post.addHeader("Accept", "*/*");
//			post.addHeader("Encrypt", "0");
			HttpResponse response = mClient.execute(post);
			if (response.getStatusLine() != null
					&& response.getStatusLine().getStatusCode() == 200) {
				in = new GZIPInputStream(response.getEntity().getContent());
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				int len;
				byte[] buffer = new byte[1024];
				while ((len = in.read(buffer)) != -1) {
					arrayOutputStream.write(buffer, 0, len);
				}
				in.close();
				arrayOutputStream.close();
				final String str = new String(arrayOutputStream.toByteArray(),
						"utf-8");
				if (!TextUtils.isEmpty(str)) {
					if (callback != null) {
						if (!TextUtils.isEmpty(str)) {
							final JSONObject jsonObject = new JSONObject(str);
							if (jsonObject != null) {
								String isChangeCookie = JSONUtils.getString(
										jsonObject, "writeCookie");
								if (!TextUtils.isEmpty(isChangeCookie)
										&& isChangeCookie
												.equalsIgnoreCase("yes")) {
//									String cookie = JSONUtils.getString(
//											jsonObject, "cookie");
//									AppApplication.getInstance().setCookie(
//											cookie);
								}
								String status = JSONUtils.getString(jsonObject,
										"resultStatus");
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

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(in, os);
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
	}

	private void close(InputStream in, OutputStream out) {
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
	}

}

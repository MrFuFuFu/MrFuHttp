package com.mrfu.http;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.mrfu.http.lib.HttpManager;
import com.mrfu.http.lib.NetworkStateReceiver;
import com.mrfu.http.lib.NetworkStateReceiver.NetworkState;
import com.mrfu.http.util.MD5Util;
import com.mrfu.http.util.SystemUtil;
import com.mrfu.http.util.UpdateModel;

/**
 * @author Mr.傅
 * 2014-11-16 下午2:37:20
 */
public class AppApplication extends Application{

  private static final String CLIENTID = "clientId";
  private static final String COOKIE = "cookie";
  private static final String DEVICE_TOKEN = "device_token";
  private static String DISPLAY = "middle";
	
	private static AppApplication sInstance;
	private NetworkStateReceiver network;
	public UpdateModel updateModel;

	public static AppApplication getInstance() {
		return sInstance;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
	    SystemUtil.initHttpHost(this);
		registerNetworkStateReceiver();
		HttpManager.getInstance().setPublicParams(getPublicParams());
	}
	
	public NetworkState getNetworkState() {
		return network.getNetworkState();
	}
	
	/**
	 * 注册网络状态监听
	 */
	private void registerNetworkStateReceiver() {
		network = new NetworkStateReceiver();
		IntentFilter filter = new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(network, filter);
	}
	
	public Map<String, String> getPublicParams() {
		Map<String, String> params = new HashMap<String, String>();
//		CurrentUser user = getUser();
//		if (user != null) {
//			params.put("userId", user.userId);
//		} else {
			params.put("userId", "");
//		}
		params.put("cookie", getCookie());
		params.put("productId", "android");
		params.put("channelId", "anzhuoapk");
		params.put("clientId", generatelientId());
		params.put("productVersion", getVersionName());
		params.put("deviceToken", getDeviceToken());
		params.put("display", DISPLAY);
//		params.put("root", BizUtil.isRooted() ? "yes":"no");
//		params.put("vendor", StringUtil.toLowerCase(android.os.Build.BRAND));
//		if (user != null) {
//			params.put("gender", user.gender);
//		} else {
			params.put("gender", "f");
//		}
		params.put("seq", "1");
//		String loc = getLocation();
//		if (!TextUtils.isEmpty(loc)) {
//			String[] ss = loc.split(",");
//			if (ss != null && ss.length == 2) {
//				if (!TextUtils.isEmpty(ss[0])) {
//					params.put("lat", ss[0]);
//				}
//				if (!TextUtils.isEmpty(ss[1])) {
//					params.put("lng", ss[1]);
//				}
//			}
//		}
		return params;
	}
	
	private String getClientId() {
		SharedPreferences sp = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		return sp.getString(CLIENTID, "");
	}
	
	@SuppressWarnings("deprecation")
	private String createClientId() {
		String se = android.os.Build.SERIAL;
		String seid = Settings.System.getString(getContentResolver(),
				Settings.System.ANDROID_ID);
		String macAddress = getMacAddress();
		return MD5Util.md5(se + seid + macAddress);
		// return MD5Util.md5(UUID.randomUUID().toString());
	}
	public String getMacAddress() {
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
			return info.getMacAddress();
		}
		return "";
	}
	
	public String generatelientId() {
		String clientId = getClientId();
		if (TextUtils.isEmpty(clientId)) {
			clientId = createClientId();
			SharedPreferences sp = getSharedPreferences(getPackageName(),
					Context.MODE_PRIVATE);
			sp.edit().putString(CLIENTID, clientId).commit();
		}

		return clientId;
	}
	
	/**
	 * get version
	 * 
	 * @return
	 */
	private String getVersionName() {
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			String name = pi.versionName;

			if (!TextUtils.isEmpty(name)) {
				int lastIndex = name.lastIndexOf(".");
				if (lastIndex != name.indexOf(".")) {
					if (lastIndex != -1) {
						String temp = name.substring(0, lastIndex)
								+ name.substring(lastIndex + 1);
						name = temp;
					}
				}
			}
			return name;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "unknow";
		}
	}
	
	public void setCookie(String cookie) {
		SharedPreferences sp = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		sp.edit().putString(COOKIE, cookie).commit();
		HttpManager.getInstance().setPublicParams(getPublicParams());
	}
	
	public String getCookie() {
		SharedPreferences sp = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		return sp.getString(COOKIE, "");
	}
	
	public String getDeviceToken() {
		SharedPreferences sp = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		return sp.getString(DEVICE_TOKEN, "");
	}

	public void setDeviceToken(String deviceToken) {
		SharedPreferences sp = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		sp.edit().putString(DEVICE_TOKEN, deviceToken).commit();
		HttpManager.getInstance().setPublicParams(getPublicParams());
	}
}

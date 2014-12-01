package com.mrfu.http.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mrfu.http.AppApplication;
import com.mrfu.http.MainActivity;
import com.mrfu.http.lib.HttpRequestPost;


public class SystemUtil {
	
	public static Map<String,Activity> stack = new HashMap<String,Activity>();
	public static Class<?> registerGotoActivity = MainActivity.class;
	public static volatile boolean isAlarmRun = false;
	public static volatile boolean isPushRun = false;
	
//	public static volatile OnShareStatusListener shareStatusListener;
	public static volatile String channel;
	public static volatile String ringId;
	public static volatile boolean contactHasChange = true;
	
	/**
	 * 程序是否在前台
	 * @param activity
	 * @return
	 */
	public static boolean isAppForeground() {
		String packageName = AppApplication.getInstance().getPackageName();
		ActivityManager activityManager = (ActivityManager) AppApplication
				.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo != null && tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isMyAppForeground() {
        // Returns a list of application processes that are running on the
        // device
         
        ActivityManager activityManager = (ActivityManager) AppApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = AppApplication.getInstance().getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager
                        .getRunningAppProcesses();
        if (appProcesses == null)
                return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
                // The name of the process that this object is associated with.
                if (appProcess.processName.equals(packageName)
                                && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                }
        }

        return false;
}
	
	public static Activity getStackTopActivity(){
		ActivityManager activityManager = (ActivityManager) AppApplication
				.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo != null && tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			String key = getTopActivityName();
			Log.d("kop", "key = "+key);
			return stack.get(key); 
		}
		return null;
	}
	
	/**
	 * 获取topActivityName
	 * @return
	 */
	public static String getTopActivityName() {
    	ActivityManager am = (ActivityManager) AppApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
    	List<RunningTaskInfo> tasks = am.getRunningTasks(1);
    	if (!tasks.isEmpty()) {
    		ComponentName topActivity = tasks.get(0).topActivity;
    		return topActivity.getClassName();
    	}
    	return null;
    }
	
	public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
        	return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
            	apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }
	
	public static final String PREFERENCE_GENERALINFO = "SearchInfo";
	public static final String RECENT_SEARCH = "recent_search";
	public static final String HTTP_HOST = "http_host";
	public static final String DEVICE_IS_FIRSTLAUNCH = "deviceisfirstlaunch";
	public static final String DEVICE_IS_FIRSTLOGIN = "deviceisfirstlogin";
	
	public static String getPreferenceString(final Context context, String key) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_GENERALINFO, Context.MODE_PRIVATE);
	    final String result = preferences.getString(key, "");
		return result;
	}
	
	public static boolean setPreferenceString(final Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_GENERALINFO, Context.MODE_PRIVATE);
		return preferences.edit().putString(key, value).commit();
	}
	
	public static boolean getIsFirstLaunch(final Context context) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_GENERALINFO, Context.MODE_PRIVATE);
	    final boolean isFirst = preferences.getBoolean(DEVICE_IS_FIRSTLAUNCH, true);
	    if (isFirst) {
	    	if (context instanceof Activity) {
		       	preferences.edit().putBoolean(DEVICE_IS_FIRSTLAUNCH, false).commit();
	    	}
	    }
		return isFirst;
	}
	
	public static boolean getIsFirstLogin(final Context context) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_GENERALINFO, Context.MODE_PRIVATE);
	    final boolean isFirst = preferences.getBoolean(DEVICE_IS_FIRSTLOGIN, true);
	    if (isFirst) {
	    	if (context instanceof Activity) {
		       	preferences.edit().putBoolean(DEVICE_IS_FIRSTLOGIN, false).commit();
	    	}
	    }
		return isFirst;
	}
	
	public static void initHttpHost(final Context context){
		String host = SystemUtil.getPreferenceString(context, SystemUtil.HTTP_HOST);
		if(TextUtils.isEmpty(host)){
//			HttpRequestPost.url = "http://api.nsjnqc.com/service";
		}else{
			HttpRequestPost.url = host;
		}
	}
	
	// delete files
	public static void deleteFiles(File file) {
		try {
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFiles(files[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void hideSoftKeyboard(final Context context, final View view) {
		try {
			InputMethodManager imm =(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getLocalIpAddress() {
    	String ipAddr = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                    	String ip = inetAddress.getHostAddress().toString();
                    	
                    	Pattern p = Pattern.compile("[0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}");
                		Matcher m = p.matcher(ip);
                	    if(!m.matches())
                	    	continue;
                	    
                    	final String[] ipSplit = ip.split("\\.");
                    	boolean isIP = false;
                    	if (ipSplit.length == 4) {
                    		for (int i = 0; i < 4; i++) {
                    			if (Integer.parseInt(ipSplit[i]) < 0 || Integer.parseInt(ipSplit[i]) >= 256) {
                    				break;
                    			}
                    			if (i == 3)
                    				isIP = true;
                    		}
                    	}
                    	
                    	if (isIP) {
                    		ipAddr = ip;
                    		break;
                    	}
                    }
                }
                if (ipAddr != null)
                	break;
            }
        } catch (Exception ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
            return "127.0.0.1";
        }
    	if(TextUtils.isEmpty(ipAddr))
    		return "127.0.0.1";
    	return ipAddr;
    }
	/**
	 * 获取外网ip
	 * 
	 * @return
	 */
	public static String getNetIp() {
		String IP = "";
		try {
			String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setUseCaches(false);

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();

				// 将流转化为字符串
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));

				String tmpString = "";
				StringBuilder retJSON = new StringBuilder();
				while ((tmpString = reader.readLine()) != null) {
					retJSON.append(tmpString + "\n");
				}

				JSONObject jsonObject = new JSONObject(retJSON.toString());
				String code = jsonObject.getString("code");
				if (code.equals("0")) {
					JSONObject data = jsonObject.getJSONObject("data");
					IP = data.getString("ip") + "(" + data.getString("country")
							+ data.getString("area") + "区"
							+ data.getString("region") + data.getString("city")
							+ data.getString("isp") + ")";

					Log.e("提示", "您的IP地址是：" + IP);
				} else {
					IP = "unknow";
					Log.e("提示", "IP接口异常，无法获取IP地址！");
				}
			} else {
				IP = "unknow";
				Log.e("提示", "网络连接异常，无法获取IP地址！");
			}
		} catch (Exception e) {
			IP = "unknow";
			Log.e("提示", "获取IP地址时出现异常，异常信息是：" + e.toString());
		}
		return IP;

	}
	
	/**
	 * whether exist package in system
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean existPackageInSystem(Context context, String packageName) { 
        if (packageName == null || "".equals(packageName)) {
            return false; 
        }

        try { 
        	context.getPackageManager().getApplicationInfo
            	(packageName, PackageManager.GET_UNINSTALLED_PACKAGES); 
            return true; 
        } catch (NameNotFoundException e) { 
            return false; 
        } 
    }
	
	public static String searchMainActivity(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
	
			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
			ResolveInfo ri = apps.iterator().next();
			String result = ri == null ? null : ri.activityInfo.name;
			return result;
		} catch(Exception ex) {
			return null;
		}
	}
	
	public static boolean isExistActivityAndEnable(Context context, String packageName, String className) {
		 if(context == null) {
			 return false;
		 }
		 Intent intent = new Intent();
		 intent.setClassName(packageName, className);
		 List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
		 if(list.size() == 0) {
			 return false;
		 }
		 
		 return list.get(0).activityInfo.exported;
	}
	
	public static boolean isWhitelistDevices() {
		return isMIUI() || isCoolpad() || isHuawei() || isVIVO() || isNubia();
	}
	
	public static boolean isMIUI() {
		return isSpecDevice("xiaomi", "xiaomi", "miui");
	}
	
	public static boolean isCoolpad() {
		return isSpecDevice("coolpad", "coolpad", "");
	}
	
	public static boolean isHuawei() {
		return isSpecDevice("huawei", "huawei", "emui");
	}
	
	public static boolean isVIVO() {
		return isSpecDevice("vivo", "vivo", "vivo");
	}
	
	public static boolean isNubia() {
		return isSpecDevice("nubia", "nubia", "nubia");
	}
	
	public static boolean isSpecDevice(String model, String brand, String display) {
		if(model.startsWith(android.os.Build.MODEL.toLowerCase())
				|| brand.startsWith(android.os.Build.BRAND.toLowerCase())
				|| display.startsWith(android.os.Build.DISPLAY.toLowerCase())) {
			return true;
		}
		return false;
	}
	
}

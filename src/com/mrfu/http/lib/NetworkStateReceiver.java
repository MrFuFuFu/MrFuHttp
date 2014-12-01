package com.mrfu.http.lib;

import com.mrfu.http.AppApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class NetworkStateReceiver extends BroadcastReceiver {
	
	private NetworkState networkState;
	/**确保通知只通知一次**/
	public static boolean hasTell = false;
	
	public NetworkStateReceiver(){
		networkState = searchCommuType(AppApplication.getInstance().getApplicationContext());
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		networkState = searchCommuType(context);
		if (networkState != NetworkState.NET_NULL) {
//			String cookie = AppApplication.getInstance().getCookie();
//			if(TextUtils.isEmpty(cookie)){
////				AppApplication.getInstance().requestAlarmList();//cookie 现在要在注册或者登录的时候传上来
//				
//			}
//			AppApplication.getInstance().updateAlarmRecord();
			
		}
	}

	public NetworkState getNetworkState() {
		return networkState;
	}

	public void setNetworkState(NetworkState networkState) {
		this.networkState = networkState;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	private static NetworkState searchCommuType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active = manager.getActiveNetworkInfo();
		NetworkState commType = NetworkState.NET_NULL;
		if (active != null) {
			int type = active.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				commType = NetworkState.NET_WIFI;
			} else{
				commType = NetworkState.NET_MOBILE;
			}
		}
		return commType;
	}
	
	/**
	 * 分别为无网络,移动网络,wifi网络
	 * @author huxuan.lb
	 *
	 */
	public static enum NetworkState{
		NET_NULL,NET_MOBILE,NET_WIFI
	}
}

package com.mrfu.http.lib;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.text.TextUtils;

public class HttpManager {

	private static HttpManager instance = null;
	private ThreadPoolExecutor executor;

	private HttpManager() {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
	}

	/**
	 * 设置公共参数
	 * 
	 * @param publicParams
	 */
	public void setPublicParams(Map<String, String> publicParams) {
		HttpRequestPost.setPublicParams(publicParams);
		HttpClientPostRequest.setPublicParams(publicParams);
	}

	public static HttpManager getInstance() {
		if (instance == null) {
			instance = new HttpManager();
		}
		return instance;
	}

	/**
	 * 清理线程池
	 */
	public void clearExecutorQueue() {
		if (executor != null) {
			BlockingQueue<Runnable> queue = executor.getQueue();
			if (queue != null) {
				queue.clear();
			}
		}
	}

	/**
	 * 普通请求
	 * @param params
	 * @param callback
	 * @return
	 */
	public synchronized void requestPost(RequestParams params, HttpPostCallback callback) {
		if (params != null) {
			HttpRequestPost post = new HttpRequestPost(params, callback,true);
//			HttpClientPostRequest post = new HttpClientPostRequest(params, callback);
//			HttpRequestPost0 post = new HttpRequestPost0(params, callback,true);
//			HttpRequestPost0.url = "http://api.nsjnqc.com/service?requestData=%7B%22public%22%3A%7B%22sign%22%3A%2205091affc798e296e72d1d96de13dc3f%22%2C%22cookie%22%3A%22%22%2C%22time%22%3A%221403081183%22%2C%22channelId%22%3A%22anzhi%22%2C%22deviceToken%22%3A%220e1285e13ac927853d020a3fb74e5ed1%22%2C%22userId%22%3A%22%22%2C%22seq%22%3A%221%22%2C%22gender%22%3A%22f%22%2C%22display%22%3A%22middle%22%2C%22lng%22%3A%22120.125035%22%2C%22productVersion%22%3A%221.07%22%2C%22lat%22%3A%2230.278601%22%2C%22productId%22%3A%22android%22%7D%2C%22operation%22%3A%22%7B%5C%22operationType%5C%22%3A%5C%22queryAlarmList%5C%22%2C%5C%22imei%5C%22%3A%5C%22355921040628495%5C%22%2C%5C%22os%5C%22%3A%5C%224.0.4%5C%22%2C%5C%22alarmListDigest%5C%22%3A%5C%22%5C%22%2C%5C%22clientId%5C%22%3A%5C%22ea8ded89-113b-4896-b805-7f1c78e3a647%5C%22%2C%5C%22model%5C%22%3A%5C%22Nexus+S%5C%22%2C%5C%22mac%5C%22%3A%5C%22%5C%22%2C%5C%22imsi%5C%22%3A%5C%22460008143634714%5C%22%2C%5C%22ifa%5C%22%3A%5C%22%5C%22%7D%22%7D&session=6a7de319a86d06485b7bd5cfa9aac5dc";
			executor.execute(post);
		}
	}
	
//	public synchronized void requestPost0(RequestParams params, HttpPostCallback callback) {
////		if (params != null) {
////			HttpRequestPost post = new HttpRequestPost(params, callback,true);
////			HttpClientPostRequest post = new HttpClientPostRequest(params, callback);
//			HttpRequestPost0 post = new HttpRequestPost0(params, callback,true);
//			
////			post.url = "http://122.225.114.20:28080/service?requestData=%7B%22public%22%3A%7B%22sign%22%3A%22d353c17f3fb3bd7668cc96f385144111%22%2C%22cookie%22%3A%22ea8ded89-113b-4896-b805-7f1c78e3a647%22%2C%22time%22%3A%221405257430%22%2C%22channelId%22%3A%22dgyb%22%2C%22deviceToken%22%3A%22a9307c050920e506c634d733abf7d92c%22%2C%22userId%22%3A%222878050%22%2C%22seq%22%3A%221%22%2C%22gender%22%3A%22m%22%2C%22display%22%3A%22low%22%2C%22lng%22%3A%22120.156063%22%2C%22productVersion%22%3A%221.20%22%2C%22lat%22%3A%2230.289208%22%2C%22productId%22%3A%22android%22%7D%2C%22operation%22%3A%22%7B%5C%22pageNo%5C%22%3A%5C%221%5C%22%2C%5C%22operationType%5C%22%3A%5C%22queryGoddessHall%5C%22%2C%5C%22pageSize%5C%22%3A%5C%2212%5C%22%2C%5C%22type%5C%22%3A%5C%221%5C%22%7D%22%7D&session=7b9da2e9e64b03668ae7aba84245b5bc";
//			post.url = "http://api.nsjnqc.com/service?requestData=%7B%22public%22%3A%7B%22sign%22%3A%2205091affc798e296e72d1d96de13dc3f%22%2C%22cookie%22%3A%22%22%2C%22time%22%3A%221403081183%22%2C%22channelId%22%3A%22anzhi%22%2C%22deviceToken%22%3A%220e1285e13ac927853d020a3fb74e5ed1%22%2C%22userId%22%3A%22%22%2C%22seq%22%3A%221%22%2C%22gender%22%3A%22f%22%2C%22display%22%3A%22middle%22%2C%22lng%22%3A%22120.125035%22%2C%22productVersion%22%3A%221.07%22%2C%22lat%22%3A%2230.278601%22%2C%22productId%22%3A%22android%22%7D%2C%22operation%22%3A%22%7B%5C%22operationType%5C%22%3A%5C%22queryAlarmList%5C%22%2C%5C%22imei%5C%22%3A%5C%22355921040628495%5C%22%2C%5C%22os%5C%22%3A%5C%224.0.4%5C%22%2C%5C%22alarmListDigest%5C%22%3A%5C%22%5C%22%2C%5C%22clientId%5C%22%3A%5C%22ea8ded89-113b-4896-b805-7f1c78e3a647%5C%22%2C%5C%22model%5C%22%3A%5C%22Nexus+S%5C%22%2C%5C%22mac%5C%22%3A%5C%22%5C%22%2C%5C%22imsi%5C%22%3A%5C%22460008143634714%5C%22%2C%5C%22ifa%5C%22%3A%5C%22%5C%22%7D%22%7D&session=6a7de319a86d06485b7bd5cfa9aac5dc";
//			executor.execute(post);
////		}
//	}
	
//	public synchronized void requestPost0(RequestParams params, HttpPostCallback callback,long seq) {
//		if (params != null) {
//			HttpRequestPost1 post = new HttpRequestPost1(params, callback,seq,true);
//			executor.execute(post);
//		}
//	}
	
//	public synchronized void requestPost1(RequestParams params, HttpPostCallback callback) {
//		if (params != null) {
//			HttpRequestPost0 post = new HttpRequestPost0(params, callback,true);
//			post.url = "http://s.taofen8.com/service/mobile.htm?requestData=%7B%22os%22%3A%224.0.4%22%2C%22sid%22%3A%22%22%2C%22model%22%3A%22HUAWEI+U8825D%22%2C%22channelID%22%3A%22test%22%2C%22clientID%22%3A%22867247016362450460029881307650%22%2C%22display%22%3A%22middle%22%2C%22productVersion%22%3A%22Android_V5.30%22%2C%22productID%22%3A%22taofen8_Android%22%2C%22network%22%3A%22wifi%22%2C%22retina%22%3A%22yes%22%2C%22sign%22%3A%221f2c7a28a60c29f7e5817a4b914c1d8c%22%2C%22cookie%22%3A%222152971bfe2c6621554157ca99e90350%22%2C%22time%22%3A%221402929968425%22%2C%22mobileId%22%3A%22f4007aae90ad07c006df95736f5e909dcb66271cb1f0378f86a017f02b18b1ba%22%2C%22nick%22%3A%22%22%2C%22operationType%22%3A%22queryNewHome%22%2C%22deviceToken%22%3A%22e87e11b2df7686e05e6b7e6cb77df0ab%22%2C%22userId%22%3A%22%22%2C%22deviceId%22%3A%22867247016362450%7C460029881307650%7C0c%3A37%3Adc%3A2f%3Ad1%3A49%22%2C%22outerCode%22%3A%22%22%7D&session=c02c6837d452feef098d9939de9f8f0c&api=api2";
//			executor.execute(post);
//		}
//	}
//	
//	public synchronized void requestPost2(RequestParams params, HttpPostCallback callback) {
//		if (params != null) {
//			HttpRequestPost0 post = new HttpRequestPost0(params, callback,true);
//			post.url = "http://api3.nsjnqc.com/service?requestData=%7B%22public%22%3A%7B%22sign%22%3A%2205091affc798e296e72d1d96de13dc3f%22%2C%22cookie%22%3A%22%22%2C%22time%22%3A%221403081183%22%2C%22channelId%22%3A%22anzhi%22%2C%22deviceToken%22%3A%220e1285e13ac927853d020a3fb74e5ed1%22%2C%22userId%22%3A%22%22%2C%22seq%22%3A%221%22%2C%22gender%22%3A%22f%22%2C%22display%22%3A%22middle%22%2C%22lng%22%3A%22120.125035%22%2C%22productVersion%22%3A%221.07%22%2C%22lat%22%3A%2230.278601%22%2C%22productId%22%3A%22android%22%7D%2C%22operation%22%3A%22%7B%5C%22operationType%5C%22%3A%5C%22queryAlarmList%5C%22%2C%5C%22imei%5C%22%3A%5C%22355921040628495%5C%22%2C%5C%22os%5C%22%3A%5C%224.0.4%5C%22%2C%5C%22alarmListDigest%5C%22%3A%5C%22%5C%22%2C%5C%22clientId%5C%22%3A%5C%22ea8ded89-113b-4896-b805-7f1c78e3a647%5C%22%2C%5C%22model%5C%22%3A%5C%22Nexus+S%5C%22%2C%5C%22mac%5C%22%3A%5C%22%5C%22%2C%5C%22imsi%5C%22%3A%5C%22460008143634714%5C%22%2C%5C%22ifa%5C%22%3A%5C%22%5C%22%7D%22%7D&session=6a7de319a86d06485b7bd5cfa9aac5dc";
//			executor.execute(post);
//		}
//	}
//	
//	public synchronized void requestPost3(RequestParams params, HttpPostCallback callback) {
//		if (params != null) {
//			HttpRequestPost0 post = new HttpRequestPost0(params, callback,true);
//			post.url = "http://122.225.114.19:28080/service?requestData=%7B%22public%22%3A%7B%22sign%22%3A%2205091affc798e296e72d1d96de13dc3f%22%2C%22cookie%22%3A%22%22%2C%22time%22%3A%221403081183%22%2C%22channelId%22%3A%22anzhi%22%2C%22deviceToken%22%3A%220e1285e13ac927853d020a3fb74e5ed1%22%2C%22userId%22%3A%22%22%2C%22seq%22%3A%221%22%2C%22gender%22%3A%22f%22%2C%22display%22%3A%22middle%22%2C%22lng%22%3A%22120.125035%22%2C%22productVersion%22%3A%221.07%22%2C%22lat%22%3A%2230.278601%22%2C%22productId%22%3A%22android%22%7D%2C%22operation%22%3A%22%7B%5C%22operationType%5C%22%3A%5C%22queryAlarmList%5C%22%2C%5C%22imei%5C%22%3A%5C%22355921040628495%5C%22%2C%5C%22os%5C%22%3A%5C%224.0.4%5C%22%2C%5C%22alarmListDigest%5C%22%3A%5C%22%5C%22%2C%5C%22clientId%5C%22%3A%5C%22ea8ded89-113b-4896-b805-7f1c78e3a647%5C%22%2C%5C%22model%5C%22%3A%5C%22Nexus+S%5C%22%2C%5C%22mac%5C%22%3A%5C%22%5C%22%2C%5C%22imsi%5C%22%3A%5C%22460008143634714%5C%22%2C%5C%22ifa%5C%22%3A%5C%22%5C%22%7D%22%7D&session=6a7de319a86d06485b7bd5cfa9aac5dc";
//			executor.execute(post);
//		}
//	}

	/**
	 * post 带进度上传数据
	 * 
	 * @param params
	 * @param callback
	 */
	public void requestPostWithProgress(RequestParams params,
			HttpPostProgressCallback callback) {
		if (params != null) {
			HttpRequestPost post = new HttpRequestPost(params, callback);
			executor.execute(post);
		}
	}
	/**
	 * 普通资源下载
	 * @param url
	 * @param callback
	 */
	public void requestGet(String url,HttpGetCallback callback){
		if(!TextUtils.isEmpty(url)){
			HttpRequestGet get = new HttpRequestGet(url, callback);
			executor.execute(get);
		}
	}
	/**
	 * @param url
	 * @param callback
	 */
	public void requestGet_Path(String targetId, String msgId, String url,HttpGetCallback callback){
		if(!TextUtils.isEmpty(url)){
			HttpRequestGet_Path get = new HttpRequestGet_Path(targetId, msgId,url, callback);
			executor.execute(get);
		}
	}
	/**
	 * 带进度下载
	 * @param url
	 * @param callback
	 */
	public void requestGetWithProgress(String url,HttpGetProgressCallback callback){
		if(!TextUtils.isEmpty(url)){
			HttpRequestGet get = new HttpRequestGet(url, callback);
			executor.execute(get);
		}
	}

}

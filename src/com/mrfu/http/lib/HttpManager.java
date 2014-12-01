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
			executor.execute(post);
		}
	}

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

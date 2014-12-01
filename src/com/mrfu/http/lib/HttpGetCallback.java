package com.mrfu.http.lib;

public interface HttpGetCallback {
	public void requestFinished(String url,String pathName);
	public void requestFailed(String url,String errorStr);
}

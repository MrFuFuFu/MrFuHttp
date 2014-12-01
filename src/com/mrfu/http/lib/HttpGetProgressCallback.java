package com.mrfu.http.lib;

public interface HttpGetProgressCallback extends HttpGetCallback{
	public void progressPublish(String url,int progress);
}

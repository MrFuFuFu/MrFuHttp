package com.mrfu.http.lib;

public interface HttpPostProgressCallback extends HttpPostCallback {

	public void progressPublish(RequestParams requestParams,int progress);
}

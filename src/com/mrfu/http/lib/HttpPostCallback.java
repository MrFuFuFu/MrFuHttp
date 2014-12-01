package com.mrfu.http.lib;

import org.json.JSONObject;

public interface HttpPostCallback 
{	
	public void requestFinished(RequestParams requestParams,JSONObject jsonObject);
	public void requestFailed(RequestParams requestParams,String errorStr);
}

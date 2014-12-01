package com.mrfu.http.util;

import org.json.JSONObject;

public class UpdateModel {
	
	public String updateTitle;
	public String updateUrl;
	public String updateVersion;
	public int code;
	
	public UpdateModel(JSONObject jsonObject){
		updateTitle = JSONUtils.getString(jsonObject, "updateTitle");
		updateUrl = JSONUtils.getString(jsonObject, "updateUrl");
	    updateVersion = JSONUtils.getString(jsonObject, "updateVersion");
	    String status = JSONUtils.getString(
				jsonObject, "resultStatus");
		code = Integer.parseInt(status);
	}
	
}

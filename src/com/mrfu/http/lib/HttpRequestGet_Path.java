package com.mrfu.http.lib;


public class HttpRequestGet_Path extends HttpRequestGet {
	String msgId;
	String targetId;
	public HttpRequestGet_Path(String targetId, String msgId, String url, HttpGetCallback callback) {
		super(url, callback);
		this.msgId = msgId;
		this.targetId = targetId;
	}
//	@Override
//	protected String getFilePath() {
//		return FileStore.createNewVoiceFile(targetId,msgId);
//	}
}

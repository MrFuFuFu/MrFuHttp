package com.mrfu.http;


import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrfu.http.lib.HttpGetCallback;
import com.mrfu.http.lib.HttpManager;
import com.mrfu.http.lib.HttpPostCallback;
import com.mrfu.http.lib.HttpRequestGet;
import com.mrfu.http.lib.RequestParams;
import com.mrfu.http.util.FileStore;
import com.mrfu.http.view.ImageViewEx;

/***
 * @author MrFu
 */
public class MainActivity extends Activity implements HttpPostCallback{
	private Context mContext = MainActivity.this;
	  private static ThreadPoolExecutor executor;
	  private static Handler handler;
	  private ImageView normal_iamgeview;
	  private TextView post_textview;
	  private TextView return_textview;
	  private ImageViewEx square_imageview;
	  private TextView textview;
	  private String urlString = "http://a.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=c5e983ee4b540923b5696e7ea263b27b/fcfaaf51f3deb48fc40cdc13f21f3a292cf57891.jpg";
	  private String urlString2 = "http://d.hiphotos.baidu.com/image/w%3D2048/sign=27a205261ad8bc3ec60801cab6b3a61e/8694a4c27d1ed21b45d79ef9ae6eddc451da3f8b.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.textview = ((TextView)findViewById(R.id.textview));
	    this.post_textview = ((TextView)findViewById(R.id.post_textview));
	    this.return_textview = ((TextView)findViewById(R.id.return_textview));
	    this.normal_iamgeview = ((ImageView)findViewById(R.id.normal_iamgeview));
	    this.square_imageview = ((ImageViewEx)findViewById(R.id.square_imageview));
		executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
		handler = new Handler(Looper.getMainLooper());
		executor.execute(new LoadImageTask(this.normal_iamgeview, this.urlString));
	    this.square_imageview.loadImage(this.urlString2);
	    requestData();
	}
	private void requestData() {
		RequestParams params = new RequestParams();
		params.put("operationType", "recommend");
		HttpManager.getInstance().requestPost(params, this);
	}
	@Override
	public void requestFinished(RequestParams requestParams, JSONObject jsonObject) {
	    Log.i("MrFu", "jsonObject = " + jsonObject.toString());
	    this.return_textview.setText("jsonObject = " + jsonObject.toString());
	}
	@Override
	public void requestFailed(RequestParams requestParams, String errorStr) {
	    Log.i("MrFu", "errorStr = " + errorStr);
	    this.return_textview.setText("errorStr = " + errorStr);
	}
	
	private static class LoadImageTask implements Runnable,HttpGetCallback{
	    private ImageView imageView;
	    private String url;
	    private LoadImageTask(ImageView paramImageView, String paramString)
	    {
	      this.imageView = paramImageView;
	      this.url = paramString;
	    }
	    private void postToMainThread(final Bitmap bitmap)
	    {
	      MainActivity.handler.post(new Runnable()
	      {
	        public void run()
	        {
	          MainActivity.LoadImageTask.this.imageView.setImageBitmap(bitmap);
	        }
	      });
	    }
	    private Bitmap scaleBitmap(String pathName)
	    {
	      BitmapFactory.Options opts = new BitmapFactory.Options();
	      opts.inSampleSize = 4;
	      return BitmapFactory.decodeFile(pathName, opts);
	    }
	    
	    private boolean setImage(String pathName)
	    {
	      if (!TextUtils.isEmpty(pathName)){
	    	  Bitmap bitmap = null;
	    	  try {
	    		  bitmap = BitmapFactory.decodeFile(pathName);
				
			} catch (OutOfMemoryError e) {
				System.gc();
				bitmap = scaleBitmap(pathName);
			}
	    	  if (bitmap != null) {
				postToMainThread(bitmap);
				return true;
			}
	      }
	      return false;
	    }
	    
		@Override
		public void requestFinished(String url, String pathName) {
			setImage(pathName);
		}

		@Override
		public void requestFailed(String url, String errorStr) {
		}

		@Override
		public void run() {
			String pathName = FileStore.cachePathForKey(this.url);
			boolean isMatch = false;
			if (!TextUtils.isEmpty(pathName)) {
				isMatch = setImage(pathName);
			}
			if (!isMatch) {
				if (!TextUtils.isEmpty(pathName)) {
					new File(pathName).delete();
				}
				HttpRequestGet get = new HttpRequestGet(url, this);
				get.run();
			}
		}
		
	}
	
}

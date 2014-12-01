package com.mrfu.http.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.mrfu.http.R;
import com.mrfu.http.lib.HttpGetCallback;
import com.mrfu.http.lib.HttpRequestGet;
import com.mrfu.http.util.FileStore;
import com.mrfu.http.util.LruMemoryCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewEx extends ImageView {
	private static final int MAX_BLOCK_QUEUE_SIZE = 30;
	private static LruMemoryCache<String, Bitmap> bitmapCache;
	private static ThreadPoolExecutor executor;
	private OnLoadImageListener listener;
	private Bitmap defaultImg;
	private Drawable defaultDrawable;
	private static Drawable defaultImage;
	private static Handler handler;
	private static Hashtable<String, Set<ImageViewEx>> processTable;

	public static interface OnLoadImageListener {
		public void onLoadImage(boolean isSuccess);
	}

	public ImageViewEx(Context context) {
		super(context);
		init();
	}

	public ImageViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ImageViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		if (bitmapCache == null) {
			handler = new Handler(Looper.getMainLooper());
			processTable = new Hashtable<String, Set<ImageViewEx>>();
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
			long maxMemory = Runtime.getRuntime().maxMemory();
			// 使用最大可用内存值的1/8作为缓存的大小。
			int cacheSize = (int) (maxMemory / 16);
			bitmapCache = new LruMemoryCache<String, Bitmap>(cacheSize) {
				/**
				 * Measure item size in bytes rather than units which is more
				 * practical for a bitmap cache
				 */
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return getBitmapSize(bitmap);
				}
			};
		}
		if (defaultImage == null) {
			defaultImage = getResources().getDrawable(R.drawable.imageview_bg);
		}
	}

	public void loadImage(String url, OnLoadImageListener listener) {
		this.listener = listener;
		loadImage(url);
	}
	
//	private static Bitmap getWeakBitmap(String url){
//		WeakReference<Bitmap> weak = bitmapCache.get(url);
//		if(weak != null){
//			return weak.get();
//		}
//		return null;
//	}

	public void loadImage(String url) {
		if (!TextUtils.isEmpty(url)) {
			setTag(url);
			boolean isMatch = false;
			if (url.startsWith("http")) {
				Bitmap bitmap = bitmapCache.get(url);
//				Bitmap bitmap = getWeakBitmap(url);
				if (bitmap != null) {
					isMatch = true;
					this.setImageBitmap(bitmap);
				}
			}
			if (!isMatch) {
				if (defaultDrawable != null) {
					setImageDrawable(defaultDrawable);
				} else if (defaultImg != null) {
					setImageBitmap(defaultImg);
				} else {
					setImageDrawable(defaultImage);
				}
				if(processTable.containsKey(url)){
					Set<ImageViewEx> set = processTable.get(url);
					set.add(this);
				}else{
					Set<ImageViewEx> set = new HashSet<ImageViewEx>();
					set.add(this);
					processTable.put(url, set);
					executor.execute(new LoadImageTask(this, url));
				}
				
				BlockingQueue<Runnable> queue = executor.getQueue();
				if (queue != null) {
					int poolSize = queue.size();
					if (poolSize > MAX_BLOCK_QUEUE_SIZE) {// 保留队尾MAX_BLOCK_QUEUE_SIZE个队列其他清理掉
						int keep = poolSize - MAX_BLOCK_QUEUE_SIZE;
						List<Runnable> list = new ArrayList<Runnable>();
						int i = 0;
						while (queue.size() > 0) {
							i++;
							Runnable r = queue.remove();
							if (i >= keep) {
								list.add(r);
							}
						}
						queue.clear();
						queue.addAll(list);
					}
				}
			}
		}
	}

	public void loadImage(String url, Bitmap defaultImage) {
		this.defaultImg = defaultImage;
		loadImage(url);
	}

	public void loadImage(String url, Drawable defaultImage) {
		this.defaultDrawable = defaultImage;
		loadImage(url);
	}

	public static int getBitmapSize(Bitmap bitmap) {
		if(bitmap != null){
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
		return 0;
	}

	public static Bitmap getBitmap(String url) {
		if (!TextUtils.isEmpty(url) && bitmapCache != null) {
			return bitmapCache.get(url);
//			return getWeakBitmap(url);
		}

		return null;
	}
	
	private void setImageWithAnimation(Bitmap bitmap) {
		Drawable[] layers = new Drawable[2];
		Drawable drawable0 = getDrawable();
		if (drawable0 == null) {
			drawable0 = getContext().getResources()
					.getDrawable(R.drawable.imageview_bg);
		}
		Drawable drawable1 = new BitmapDrawable(bitmap);
		layers[0] = drawable0;
		layers[1] = drawable1;
		TransitionDrawable transitionDrawable = new TransitionDrawable(
				layers);
		transitionDrawable.setCrossFadeEnabled(true);
		setImageDrawable(transitionDrawable);
		transitionDrawable.startTransition(300);
	}

	private static class LoadImageTask implements Runnable, HttpGetCallback {
		private String url;
		private ImageView imageView;

		private LoadImageTask(ImageView imageView, String url) {
			this.imageView = imageView;
			this.url = url;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String pathName = FileStore.cachePathForKey(url);
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

		private void postToMainThread(final Bitmap bitmap) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					bitmapCache.put(url, bitmap);
//					bitmapCache.put(url, new WeakReference<Bitmap>(bitmap));
					Set<ImageViewEx> set = processTable.get(url);
					if(set != null){
						for(ImageViewEx view : set){
							String temp = (String)view.getTag();
							if (temp.equals(url)) {
								view.setImageWithAnimation(bitmap);
							}
						}
						set.clear();
						processTable.remove(url);
					}
				}
			});
		}

		private void setImageWithAnimation(Bitmap bitmap) {
			Drawable[] layers = new Drawable[2];
			Drawable drawable0 = imageView.getDrawable();
			if (drawable0 == null) {
				drawable0 = imageView.getContext().getResources()
						.getDrawable(R.drawable.imageview_bg);
			}
			Drawable drawable1 = new BitmapDrawable(bitmap);
			layers[0] = drawable0;
			layers[1] = drawable1;
			TransitionDrawable transitionDrawable = new TransitionDrawable(
					layers);
			transitionDrawable.setCrossFadeEnabled(true);
			imageView.setImageDrawable(transitionDrawable); 
			transitionDrawable.startTransition(300);
		}

		private boolean setImage(String pathName) {
			if (!TextUtils.isEmpty(pathName)) {
				Bitmap bitmap = null;
				try{
					bitmap = BitmapFactory.decodeFile(pathName);
				}catch(OutOfMemoryError oe){
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
		
		private Bitmap scaleBitmap(String pathName) {
			BitmapFactory.Options opts = new BitmapFactory.Options();  
			opts.inSampleSize = 4;  
			return BitmapFactory.decodeFile(pathName, opts);  
		}

		@Override
		public void requestFinished(String url, String pathName) {
			// TODO Auto-generated method stub
			setImage(pathName);
		}

		@Override
		public void requestFailed(final String url, String errorStr) {
			// TODO Auto-generated method stub
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(processTable.containsKey(url)){
						Set<ImageViewEx> set = processTable.get(url);
						processTable.remove(url);
						set.clear();
					}
				}
			});
		}

	}
}

package com.mrfu.http.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.mrfu.http.AppApplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

public class FileStore {

	public static Context context = AppApplication.getInstance();

	/**
	 * ???�?cache??��??�?
	 * 
	 * @return
	 */
	public static String createNewCacheFile() {
		return createNewCacheFile(UUID.randomUUID().toString());
	}

	public static String createNewVideoCacheFile() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		String path = "";
		if (sdCardExist) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/mrfu/cache/";
		} else {
			path = context.getCacheDir().getAbsolutePath() + "/mrfu/cache/";
		}
		File fileDir = new File(path);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = UUID.randomUUID().toString();
		String name = MD5Util.md5(fileName) + ".mp4";
		String pathName = path + name;
		File file = new File(pathName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathName;
	}

	public static String createNewTxtLocalFile() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		String path = "";
		if (sdCardExist) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/mrfu/cache/";
		} else {
			path = context.getCacheDir().getAbsolutePath() + "/mrfu/cache/";
		}
		File fileDir = new File(path);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = UUID.randomUUID().toString();
		String name = MD5Util.md5(fileName) + ".txt";
		String pathName = path + name;
		File file = new File(pathName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathName;
	}

	public static String createNewVideoLocalFile() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		String path = "";
		if (sdCardExist) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/mrfu/女神/";
		} else {
			path = context.getCacheDir().getAbsolutePath() + "/mrfu/女神/";
		}
		File fileDir = new File(path);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = UUID.randomUUID().toString();
		String name = MD5Util.md5(fileName) + ".mp4";
		String pathName = path + name;
		File file = new File(pathName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathName;
	}

	public static String createNewAudioCacheFile() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		String path = "";
		if (sdCardExist) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/mrfu/cache/";
		} else {
			path = context.getCacheDir().getAbsolutePath() + "/mrfu/cache/";
		}
		File fileDir = new File(path);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = UUID.randomUUID().toString();
		String name = MD5Util.md5(fileName) + ".amr";
		String pathName = path + name;
		File file = new File(pathName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathName;
	}

	public static String createNewPicCacheFile() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		String path = "";
		if (sdCardExist) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/mrfu/cache/";
		} else {
			path = context.getCacheDir().getAbsolutePath() + "/mrfu/cache/";
		}
		File fileDir = new File(path);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = UUID.randomUUID().toString();
		String name = MD5Util.md5(fileName) + ".jpg";
		String pathName = path + name;
		File file = new File(pathName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathName;
	}

	private static void clearFile() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File cacheDir = AppApplication.getInstance().getCacheDir();
				SystemUtil.deleteFiles(cacheDir);
				boolean sdCardExist = Environment.getExternalStorageState()
						.equals(android.os.Environment.MEDIA_MOUNTED);
				if (sdCardExist) {
					try {
						SystemUtil.deleteFiles(new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/mrfu/cache/"));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}).start();
	}

	public static String createNewCacheFile(String fileName) {
		synchronized (context) {
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
			String path = "";
			if (sdCardExist) {
				path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/mrfu/cache/";
			} else {
				path = context.getCacheDir().getAbsolutePath()
						+ "/mrfu/cache/";
			}
			File fileDir = new File(path);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
				File noScanFile = new File(path + ".nomedia");
				try {
					noScanFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					clearFile();
				}
			}
			String name = MD5Util.md5(fileName);
			String pathName = path + name;
			File file = new File(pathName);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return pathName;
		}
	}

	/**
	 * 
	 * @param httpUrl
	 * @return
	 */
	public synchronized static String cachePathForKey(String httpUrl) {
		if (!TextUtils.isEmpty(httpUrl)) {
			if (httpUrl.startsWith("http")) {
				String md5 = MD5Util.md5(httpUrl);
				boolean sdCardExist = Environment.getExternalStorageState()
						.equals(android.os.Environment.MEDIA_MOUNTED);
				String pathName = "";
				if (sdCardExist) {
					String path = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/mrfu/cache/";
					pathName = path + md5;
					File file = new File(pathName);
					if (file.exists()) {
						return pathName;
					}
				}

				String path = context.getCacheDir().getAbsolutePath()
						+ "/mrfu/cache/";
				pathName = path + md5;
				File file = new File(pathName);
				if (file.exists()) {
					return pathName;
				}
			}
		}
		return "";
	}
	
	public static boolean createNewFolder(String mPath){
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		String path = "";
		if (sdCardExist) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath() + mPath;
		} else {
			path = context.getCacheDir().getAbsolutePath() + mPath;
		}
		File fileDir = new File(path);
		if (!fileDir.exists()) {
			return fileDir.mkdirs();
		}
		return false;
	}
	
	/**
	 * 创建语音文件的文件路径
	 * @param targetId 文件夹名
	 * @param msgId  文件名
	 * @return
	 */
	public static String createNewVoiceFile(String targetId, String msgId) {
		synchronized (context) {
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			String path = "";
			if (sdCardExist) {
				path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mrfu/voice/"+targetId+"/";
			} else {
				path = context.getCacheDir().getAbsolutePath() + "/mrfu/voice/"+targetId+"/";
			}
			String fileName = path + msgId + ".amr";
			File fileDir = new File(path);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
				File file = new File(fileName);
				try {
					if (!file.exists()) {
						file.createNewFile();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					clearFile();
				}
			}
//			String name = MD5Util.md5(fileName);
//			String pathName = path + name;
//			File file = new File(pathName);
//			if (!file.exists()) {
//				try {
//					file.createNewFile();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			Debug.i_MrFu("fileName="+fileName);
			return fileName;
		}
	}
	public static String getVoiceFile(String targetId, String msgId){
		synchronized (context) {
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			String path = "";
			if (sdCardExist) {
				path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mrfu/voice/"+targetId+"/";
			} else {
				path = context.getCacheDir().getAbsolutePath() + "/mrfu/voice/"+targetId+"/";
			}
			String fileName = path + msgId + ".amr";
			File fileDir = new File(fileName);
			if (fileDir.exists()) {
				return fileName;
			}else {
				return "";
			}
		}
	}
	
	public static String saveImageFile(Bitmap bitmap) {
		synchronized (context) {
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			String path = "";
			String showPath = "";
			if (sdCardExist) {
				path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mrfu/mrfu_image/";
				showPath = "图片已保存至/sdcard/mrfu/mrfu_image/ 文件夹";
			} else {
				path = context.getCacheDir().getAbsolutePath() + "/mrfu/mrfu_image/";
				showPath = "图片已保存至" + path + " 文件夹";
			}
//			Debug.i_MrFu("path="+path);
			long name = System.currentTimeMillis();
			File fileDir = new File(path);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File file = new File(path, name+".jpg");
			try {
				FileOutputStream fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				fos.flush();
				fos.close();
				refreshMedia(path+name+".jpg");
				return showPath;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return "";
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}
	}
	
	/**通知图库刷新指定文件**/
	public static void refreshMedia(String pathString){
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);     
		 Uri uri = Uri.fromFile(new File(pathString));     
		 intent.setData(uri);     
		 context.sendBroadcast(intent);
	}

}

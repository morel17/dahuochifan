package com.dahuochifan.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

public class GetFileSizeUtil {

	private static GetFileSizeUtil instance;

	private GetFileSizeUtil() {

	}

	public static GetFileSizeUtil getInstance() {
		if (instance == null) {
			instance = new GetFileSizeUtil();
		}
		return instance;
	}

	public long getFileSizes(File f) throws Exception {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}
	public String getFilesSize(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				try {
					size = size + getFileSize(flist[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				size = size + flist[i].length();
			}
		}

		return FormetFileSize(size);
	}
	public long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	public String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		if (fileSizeString.equals(".00B")) {
			fileSizeString = "0.00B";
		}
		return fileSizeString;
	}
	public long getlist(File f) {// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {

				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;
	}
//	public static String getPrimaryStoragePath(Context context) {
//		try {
//			StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//			Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", null);
//			String[] paths = (String[]) getVolumePathsMethod.invoke(sm, null);
//			// first element in paths[] is primary storage path
//			return paths[0];
//		} catch (Exception e) {
//			Log.e("TAG", "getPrimaryStoragePath() failed", e);
//		}
//		return null;
//	}

//	public static String getSecondaryStoragePath(Context context) {
//		try {
//			StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//			Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", null);
//			String[] paths = (String[]) getVolumePathsMethod.invoke(sm, null);
//			// second element in paths[] is secondary storage path
//			return paths[1];
//		} catch (Exception e) {
//			Log.e("TAG", "getSecondaryStoragePath() failed", e);
//		}
//
//		return null;
//	}

//	public static String getStorageState(String path, Context context) {
//		try {
//			StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//			Method getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", new Class[]{String.class});
//			String state = (String) getVolumeStateMethod.invoke(sm, path);
//			return state;
//		} catch (Exception e) {
//			Log.e("TAG", "getStorageState() failed", e);
//		}
//		return null;
//	}
	
	public static String getMainSD(Context context){
//		if(!TextUtils.isEmpty(getSecondaryStoragePath(context))&&getStorageState(getSecondaryStoragePath(context), context).equals(Environment.MEDIA_MOUNTED)){
//			return getSecondaryStoragePath(context);
//		}else{
			return Environment.getExternalStorageDirectory().getAbsolutePath();
//		}
	}

}
package com.androidlib.cache;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.androidlib.utils.BaseUtils;
import com.androidlib.utils.EncryptUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * 缓存管理
 * Created by zhangliang on 16/9/14.
 */

public class CacheManager {

	/**
	 * 缓存路径 TODO 变换存储路径,看看存在哪里
	 */
	private static final String APP_CACHE_PATH =
			Environment.getExternalStorageDirectory().getPath() + "/ZhangWeather/appdata/";

	/**
	 * 内部存储最小限制空间, 小于此空间不再接收缓存
	 */
	private static final long STORAGE_MIN_SPACE = 1024 * 1024 * 10;

	private static CacheManager instance;

	private CacheManager() {}

	public static CacheManager getInstance() {
		if(instance == null) {
			instance = new CacheManager();
		}
		return instance;
	}

	// 初始化缓存文件目录
	public void initCacheDir() {
		if(!BaseUtils.isSdcardMounted()) {
			return;
		}
		if(BaseUtils.getAvaiSDStorageSize() < STORAGE_MIN_SPACE) {
			clearAllData();
		}
		File file = new File(APP_CACHE_PATH);
		if(!file.exists()) {
			file.mkdirs();
		}
		Log.d("initCacheDir", file.exists() + "");
	}

	// 存储缓存数据到文件
	public boolean putFileCache(String key, String data, long expiredTime) {
		String md5Key;
		try {
			md5Key = EncryptUtils.encryptMD5U32(key);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		return putIntoCache(new CacheItem(md5Key, data, expiredTime));

	}

	// 获取缓存
	public String getFileCache(String key) {
		String md5Key;
		try {
			md5Key = EncryptUtils.encryptMD5U32(key);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		CacheItem cacheItem = getFromCache(md5Key);
		return cacheItem == null ? null : cacheItem.getData();
	}

	// 是否存在缓存数据文件
	public boolean contains(String key) {
		File file = new File(APP_CACHE_PATH + key);
		return file.exists();
	}

	// 清除缓存文件
	private void clearAllData() {
		File rootFile = new File(APP_CACHE_PATH);
		File[] files = rootFile.listFiles();
		if(files == null || files.length == 0) {
			return;
		}

		for(File file:files) {
			if(file != null) {
				file.delete();
			}
		}
	}

	// 直接存储实体
	private synchronized boolean putIntoCache(@NonNull CacheItem item) {
		Log.d("put path", APP_CACHE_PATH + item.getKey());
		return BaseUtils.getAvaiDataStorageSize() > STORAGE_MIN_SPACE
				&& BaseUtils.saveObject(APP_CACHE_PATH + item.getKey(), item);
	}

	// 直接获取实体
	private synchronized CacheItem getFromCache(String key) {
		Log.d("get path", APP_CACHE_PATH + key);
		Object object = BaseUtils.restoreObject(APP_CACHE_PATH + key);
		if(object == null || !(object instanceof CacheItem)) {
			return null;
		}

		CacheItem cacheItem = (CacheItem) object;
		if(System.currentTimeMillis() > cacheItem.getTimeStamp()) {
			return null;
		}

		return cacheItem;
	}

}

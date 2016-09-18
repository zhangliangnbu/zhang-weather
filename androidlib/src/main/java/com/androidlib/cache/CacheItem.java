package com.androidlib.cache;

import java.io.Serializable;

/**
 * 缓存的数据单元
 * Created by zhangliang on 16/9/14.
 */

public class CacheItem implements Serializable {
	/** 存储的key */
	private String key;

	/** JSON字符串 */
	private String data;

	/** 过期时间的时间戳 =存入时间+url过期时间长度; 当前时间与此时间戳比较以判断是否获取缓存 */
	private long timeStamp = 0L;

	public CacheItem(String key, String data, long expiredTime) {
		this.key = key;
		this.data = data;
		this.timeStamp = System.currentTimeMillis() + expiredTime * 1000;
	}

	public String getKey() {
		return key;
	}

	public String getData() {
		return data;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
}

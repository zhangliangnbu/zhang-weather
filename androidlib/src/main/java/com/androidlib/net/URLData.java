package com.androidlib.net;

/** 每一个MobileAPI接口都对应一个URLData实体
 * Created by zhangliang on 16/9/8.
 */
public class URLData {
	private String key;
	private long expires;
	private String netType;
	private String url;
	private String mockclass;

	public URLData() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public String getNetType() {
		return netType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMockclass() {
		return mockclass;
	}

	public void setMockclass(String mockclass) {
		this.mockclass = mockclass;
	}
}

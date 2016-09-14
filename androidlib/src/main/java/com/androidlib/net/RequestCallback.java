package com.androidlib.net;

/**
 * Created by zhangliang on 16/9/9.
 */
public interface RequestCallback {
	void onSuccess(String content);
	void onFail(String errorMessage);
}

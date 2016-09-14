package com.zhangweather.engine;

import com.androidlib.activity.BaseActivity;
import com.androidlib.net.DefaultThreadPool;
import com.androidlib.net.HttpRequest;
import com.androidlib.net.RequestCallback;
import com.androidlib.net.RequestParameter;
import com.androidlib.net.URLData;

import java.util.List;

/**
 * 网络请求 单例
 * Created by zhangliang on 16/9/12.
 */
public class RemoteService {
	private static RemoteService instance = null;

	private RemoteService() {}
	public static synchronized RemoteService getInstance() {
		if(instance == null) {
			instance = new RemoteService();
		}
		return instance;
	}

	public void invoke(BaseActivity activity, String apiKey, List<RequestParameter> parameters,
	                   RequestCallback requestCallback) {
		// 构造request
		URLData urlData = UrlConfigManager.findURL(activity, apiKey);
		HttpRequest httpRequest = new HttpRequest(urlData, parameters, requestCallback);
		// 添加和执行
		DefaultThreadPool.getInstance().execute(httpRequest);
	}
}

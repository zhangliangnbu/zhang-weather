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

	/**
	 * 遵循缓存策略的网络请求
	 * @param activity 当前应用背景
	 * @param apiKey   请求名
	 * @param parameters 请求参数
	 * @param requestCallback 回调
	 */
	public void invoke(BaseActivity activity, String apiKey, List<RequestParameter> parameters,
	                   RequestCallback requestCallback) {
		invoke(activity, apiKey, parameters, requestCallback, true);
	}

	/**
	 * 网络请求
	 * @param isCache 是否遵循缓存策略 false则立刻更新
	 */
	public void invoke(BaseActivity activity, String apiKey, List<RequestParameter> parameters,
	                   RequestCallback requestCallback, boolean isCache) {
		// 构造request
		URLData urlData = UrlConfigManager.findURL(activity, apiKey);
		if(urlData != null && !isCache) {
			urlData.setExpires(0);
		}
		HttpRequest httpRequest = new HttpRequest(urlData, parameters, requestCallback);
		// 添加和执行
		DefaultThreadPool.getInstance().execute(httpRequest);
	}


}

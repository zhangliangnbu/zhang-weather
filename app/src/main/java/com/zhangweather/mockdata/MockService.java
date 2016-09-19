package com.zhangweather.mockdata;

import com.androidlib.net.Response;

/**
 * 用于mock class 数据类的实现
 * Created by zhangliang on 16/9/19.
 */

public abstract class MockService {
	public abstract String getJsonData();

	public Response getSuccessResponse() {
		Response response = new Response();
		response.setError(false);
		response.setErrorType(0);
		response.setErrorMessage("");

		return response;
	}

	public Response getFailResponse(int errorType, String errorMessage) {
		Response response = new Response();
		response.setError(true);
		response.setErrorType(errorType);
		response.setErrorMessage(errorMessage);

		return response;
	}
}

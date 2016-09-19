package com.zhangweather.mockdata;

import com.alibaba.fastjson.JSON;
import com.androidlib.net.Response;
import com.zhangweather.entity.UserInfo;

/**
 *
 * Created by zhangliang on 16/9/19.
 */

public class MockUserInfo extends MockService {
	@Override
	public String getJsonData() {
		// user info
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName("zhangliang");
		userInfo.setLoginStatus(true);
		userInfo.setUserName("zl");
		userInfo.setScore(5);

		// response
		Response response = new Response();
		response.setResult(JSON.toJSONString(userInfo));

		return JSON.toJSONString(response);
	}
}

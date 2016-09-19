package com.zhangweather.mockdata;

import com.alibaba.fastjson.JSON;
import com.androidlib.net.Response;
import com.zhangweather.entity.WeatherInfo;

/**
 * com.zhangweather.mockdata.MockWeatherInfo
 * WeatherInfo mock data
 * Created by zhangliang on 16/9/19.
 */

public class MockWeatherInfo extends MockService {
	/**
	 * @return mock response string data
	 */
	@Override
	public String getJsonData() {
		// weather info
		WeatherInfo weatherInfo = new WeatherInfo();
		weatherInfo.setCity("莫克");
		weatherInfo.setCityid("10911199107");
		weatherInfo.setTemp("100");
		weatherInfo.setWD("西北风");
		weatherInfo.setWS("10级");
//		weatherInfo.city = "莫克";
//		weatherInfo.cityid ="10911199107";
//		weatherInfo.temp = "100";
//		weatherInfo.WD = "西北风";
//		weatherInfo.WS = "10级";

		// response
		Response response = getSuccessResponse();
		response.setResult(JSON.toJSONString(weatherInfo));

		return JSON.toJSONString(response);
	}
}

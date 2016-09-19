package com.zhangweather.activity.weather;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.androidlib.net.RequestCallback;
import com.androidlib.net.RequestParameter;
import com.zhangweather.R;
import com.zhangweather.base.AppBaseActivity;
import com.zhangweather.engine.RemoteService;
import com.zhangweather.entity.WeatherInfo;
import com.zhangweather.utils.Utils;

import java.util.ArrayList;

public class WeatherByFastJsonActivity extends AppBaseActivity {
	TextView tvCity;
	TextView tvCityId;

	@Override
	protected void initVariables() {
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_weather);
		tvCity = (TextView) findViewById(R.id.tvCity);
		tvCityId = (TextView) findViewById(R.id.tvCityId);
	}

	@Override
	protected void loadData() {
		// 回调
		RequestCallback requestCallback = new AbstractRequestCallback() {
			@Override
			public void onSuccess(String content) {
				if(progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Log.d("weather info", content + "--");
				WeatherInfo weatherInfo = JSON.parseObject(content, WeatherInfo.class);
				if(weatherInfo != null) {
					tvCity.setText(weatherInfo.getCity().concat(weatherInfo.getCityid()));
					tvCityId.setText(weatherInfo.getTemp().concat("摄氏度").concat(weatherInfo.getWD()).concat(weatherInfo.getWS()));
//					tvCity.setText(weatherInfo.city.concat(weatherInfo.cityid));
//					tvCityId.setText(weatherInfo.temp.concat("摄氏度").concat(weatherInfo.WD).concat(weatherInfo.WS));
//					Log.d("temp", weatherInfo.getTemp() + "");
//					Log.d("WD", weatherInfo.getWD() + "");
//					Log.d("WS", weatherInfo.getWS() + "");
				}
			}
		};

		// 请求
		ArrayList<RequestParameter> parameters = new ArrayList<>();
		// 浦东 101021300; 上海 101020100; 杭州 101210101
		RequestParameter parameter1 = new RequestParameter("cityId", "101020100");
//		RequestParameter parameter2 = new RequestParameter("cityName", "Beijing");
		parameters.add(parameter1);
//		parameters.add(parameter2);

		// 执行
		progressDialog = Utils.createProgressDialog(this, getString(R.string.str_loading));
		progressDialog.show();
		RemoteService.getInstance().invoke(this, "getWeatherInfo", parameters, requestCallback, false);
	}
}

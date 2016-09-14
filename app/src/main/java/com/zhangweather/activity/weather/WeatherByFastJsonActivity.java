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
					tvCity.setText(weatherInfo.getCity());
					tvCityId.setText(weatherInfo.getCityid());
				}
			}
		};

		// 请求
		ArrayList<RequestParameter> parameters = new ArrayList<>();
		RequestParameter parameter1 = new RequestParameter("cityId", "111");
		RequestParameter parameter2 = new RequestParameter("cityName", "Beijing");
		parameters.add(parameter1);
		parameters.add(parameter2);

		// 执行
		progressDialog = Utils.createProgressDialog(this, getString(R.string.str_loading));
		progressDialog.show();
		RemoteService.getInstance().invoke(this, "getWeatherInfo", parameters, requestCallback);
	}
}

package com.zhangweather.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zhangweather.R;
import com.zhangweather.activity.weather.WeatherByFastJsonActivity;
import com.zhangweather.base.AppBaseActivity;

public class MainActivity extends AppBaseActivity {
	private Button btnByFastJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initVariables() {

	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		btnByFastJson  = (Button) findViewById(R.id.btnByFastJson);
		btnByFastJson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, WeatherByFastJsonActivity.class));
			}
		});
	}

	@Override
	protected void loadData() {

	}
}

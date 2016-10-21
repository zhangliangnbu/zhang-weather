package com.zhangweather.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhangweather.BuildConfig;
import com.zhangweather.R;
import com.zhangweather.activity.login.LoginMainActivity;
import com.zhangweather.activity.weather.WeatherByFastJsonActivity;
import com.zhangweather.base.AppBaseActivity;

public class MainActivity extends AppBaseActivity {
	private Button btnByFastJson;
	private Button btnByLoginMain;

	@Override
	protected void initVariables() {

	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);

		TextView textView = (TextView) findViewById(R.id.tvText);
		textView.setText(BuildConfig.APPLICATION_ID.concat(":" + BuildConfig.isMonkey));

		btnByFastJson  = (Button) findViewById(R.id.btnByFastJson);
		btnByFastJson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, WeatherByFastJsonActivity.class));
			}
		});

		btnByLoginMain = (Button) findViewById(R.id.btnByLoginMain);
		btnByLoginMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, LoginMainActivity.class));
			}
		});

	}

	@Override
	protected void loadData() {

	}
}

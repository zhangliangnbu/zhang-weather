package com.zhangweather.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.androidlib.net.RequestCallback;
import com.androidlib.net.RequestParameter;
import com.zhangweather.R;
import com.zhangweather.activity.personcenter.PersonCenterActivity;
import com.zhangweather.base.AppBaseActivity;
import com.zhangweather.engine.AppConstants;
import com.zhangweather.engine.RemoteService;
import com.zhangweather.engine.User;
import com.zhangweather.entity.UserInfo;

import java.util.ArrayList;

/**
 * 登录
 * Created by zhangliang on 16/9/19.
 */

public class LoginActivity extends AppBaseActivity {
	private boolean needCallback = false;
	private Button btnLogin;

	@Override
	protected void initVariables() {
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			needCallback = bundle.getBoolean(AppConstants.NeedCallback, false);
		}
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);

		//登录事件
		btnLogin = (Button)findViewById(R.id.sign_in_button);
		btnLogin.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						login();
					}
				});
	}

	@Override
	protected void loadData() {

	}

	private void login() {
		RequestCallback requestCallback = new AbstractRequestCallback() {
			@Override
			public void onSuccess(String content) {
				UserInfo userInfo = JSON.parseObject(content, UserInfo.class);
				User user = User.getInstance();
				user.reset();
				user.setLoginName(userInfo.getLoginName());
				user.setLoginStatus(userInfo.isLoginStatus());
				user.setScore(userInfo.getScore());
				user.setUserName(userInfo.getUserName());
				user.save();

				// 成功后的跳转逻辑
				if(needCallback) {
					setResult(RESULT_OK);
					finish();
				} else {
					Intent intent = new Intent(LoginActivity.this, PersonCenterActivity.class);
					startActivity(intent);
				}

			}
		};

		ArrayList<RequestParameter> parameters = new ArrayList<>();
		parameters.add(new RequestParameter("loginName", "zhangliang"));
		parameters.add(new RequestParameter("password", "123456"));
		RemoteService.getInstance().invoke(this, AppConstants.LOGIN, parameters, requestCallback);
	}




}

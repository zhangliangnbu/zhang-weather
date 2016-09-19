package com.zhangweather.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhangweather.R;
import com.zhangweather.activity.news.NewsActivity;
import com.zhangweather.base.AppBaseActivity;
import com.zhangweather.engine.AppConstants;
import com.zhangweather.engine.User;

// 进入的主页 模拟登录状态的三种操作
public class LoginMainActivity extends AppBaseActivity {
	private static final int LOGIN_REDIRECT_OUTSIDE = 3000;	//登录后跳转到其它页面
	private static final int LOGIN_REDIRECT_INSIDE = 3001;	//登录后仍然在本页面

	@Override
	protected void initVariables() {

	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login_main);

		// 直接跳转登录页
		Button btnLogin1 = (Button)findViewById(R.id.btnLogin1);
		btnLogin1.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginMainActivity.this,
						LoginActivity.class);
				startActivity(intent);
			}
		});

		// 跳转其他页前判断登录状态
		Button btnLogin2 = (Button)findViewById(R.id.btnLogin2);
		btnLogin2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(User.getInstance().isLoginStatus()) {
					// xxx
					gotoNewsActivity();
				} else {
					// to login and back
					startActivityForResult(
							new Intent(LoginMainActivity.this, LoginActivity.class).putExtra(AppConstants.NeedCallback, true),
							LOGIN_REDIRECT_OUTSIDE);
				}
			}
		});

		// 当前页内操作判断登录状态
		Button btnLogin3 = (Button)findViewById(R.id.btnLogin3);
		btnLogin3.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(User.getInstance().isLoginStatus()) {
					changeText();
				} else {
					Intent intent = new Intent(LoginMainActivity.this,
							LoginActivity.class);
					intent.putExtra(AppConstants.NeedCallback, true);
					startActivityForResult(intent, LOGIN_REDIRECT_INSIDE);
				}
			}
		});

		// 注销
		Button btnLogout = (Button)findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User.getInstance().reset();
			}
		});

	}

	@Override
	protected void loadData() {

	}

	private void gotoNewsActivity() {
		Intent intent = new Intent(LoginMainActivity.this,
				NewsActivity.class);
		startActivity(intent);
	}

	private void changeText() {
		TextView textView1 = (TextView)findViewById(R.id.textView1);
		textView1.setText("1");
	}
}

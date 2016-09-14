package com.androidlib.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initVariables();
		initViews(savedInstanceState);
		loadData();
	}

	protected abstract void initVariables();// intent数据/成员变量等
	protected abstract void initViews(Bundle savedInstanceState);// view
	protected abstract void loadData();// MobileApi获取的数据
}

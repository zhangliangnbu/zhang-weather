package com.zhangweather.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import com.androidlib.activity.BaseActivity;
import com.androidlib.net.RequestCallback;

/**
 * Created by zhangliang on 16/9/13.
 */
public abstract class AppBaseActivity extends BaseActivity {

	protected ProgressDialog progressDialog;

	/**
	 * 回调的统一处理
	 * onFail 默认统一处理. 如果需要特殊处理,复写即可.
	 */
	public abstract class AbstractRequestCallback implements RequestCallback {
		@Override
		public void onFail(String errorMessage) {
			if(progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			new AlertDialog.Builder(AppBaseActivity.this)
					.setTitle("出错啦")
					.setMessage(errorMessage)
					.setPositiveButton("确定", null)
					.show();
		}
	}
}

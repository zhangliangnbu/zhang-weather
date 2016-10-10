package com.zhangliang.nativewithh5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// 不用字典的直接对应
	public void gotoAnyWhere(String url) {
		if (url != null) {
			if (url.startsWith("gotoMovieDetail:")) {
				String strMovieId = url.substring(24);
				int movieId = Integer.valueOf(strMovieId);

				Intent intent = new Intent(this, MovieDetailActivity.class);
				intent.putExtra("movieId", movieId);
				startActivity(intent);
			} else if (url.startsWith("gotoNewsList:")) {
				// as above
			} else if (url.startsWith("gotoPersonCenter")) {
				Intent intent = new Intent(this, PersonCenterActivity.class);
				startActivity(intent);
			}
		}
	}

	private String getAndroidPageName(String key) {
		String pageName = null;

		int pos = key.indexOf(",");
		if (pos == -1) {
			pageName = key;
		} else {
			pageName = key.substring(0, pos);
		}

		return pageName;
	}

	// 使用反射
	public void gotoAnyWhere2(String url) {
		if (url == null)
			return;

		String pageName = getAndroidPageName(url);
		if (pageName == null || pageName.trim() == "")
			return;

		Intent intent = new Intent();

		int pos = url.indexOf(":");
		if (pos > 0) {
			String strParams = url.substring(pos);
			String[] pairs = strParams.split("&");
			for (String strKeyAndValue : pairs) {
				String[] arr = strKeyAndValue.split("=");
				String key = arr[0];
				String value = arr[1];
				if (value.startsWith("(int)")) {
					intent.putExtra(key, Integer.valueOf(value.substring(5)));
				} else if (value.startsWith("(double)")) {
					intent.putExtra(key, Double.valueOf(value.substring(8)));
				} else {
					intent.putExtra(key, value);
				}
			}
		}

		try {
			intent.setClass(this, Class.forName(pageName));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		startActivity(intent);
	}

	// 使用字典一一对应(跨平台), 反射
	public void gotoAnyWhere3(String url) {
		Dispatcher.gotoAnyWhere(this, url);
	}

}

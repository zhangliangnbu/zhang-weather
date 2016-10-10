package com.zhangliang.nativewithh5;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

/**
 * 导航器
 * Created by zhangliang on 2016/10/10.
 */

public class Dispatcher {
	/**
	 * * 根据H5传递过来的url， 查找字典获取page的索引并反射为具体的类， 跳转
	* @param currentPage 当前页面背景
	* @param nextPageUrl 要跳转的页面的url eg: gotoMovieDetail:movieId=(int)123
	*/
	public static void gotoAnyWhere(Activity currentPage, String nextPageUrl) {
		String findKey  = null;// next page 类名索引的key
		Intent intent = new Intent();

		// next page 索引key 和 parameters
		int pos = nextPageUrl.indexOf(":");
		if(pos == -1) {
			findKey = nextPageUrl;
		} else {
			findKey = nextPageUrl.substring(0, pos);

			// set parameters
			String parameters = nextPageUrl.substring(pos + 1);
			String[] pairs = parameters.split("&");
			for(String keyValue : pairs) {
				String[] arr = keyValue.split("=");
				String key = arr[0];
				String value = arr[1];
				Log.d("key-value", key + "," + value);

				if(value.startsWith("(int)")) {
					int valueInt = Integer.parseInt(value.substring(5));
					intent.putExtra(key, valueInt);
				} else if(value.startsWith("(double)")) {
					intent.putExtra(key, Double.parseDouble(value.substring(8)));
				} else {
					intent.putExtra(key, value);
				}
			}
		}

		// 索引类名并反射
		ProtocolData protocol = ProtocolManager.findProtocol(findKey, currentPage);
		try {
			intent.setClass(currentPage, Class.forName(protocol.getTarget()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// 跳转
		currentPage.startActivity(intent);
	}
}

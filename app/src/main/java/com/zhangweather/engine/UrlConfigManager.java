package com.zhangweather.engine;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;

import com.androidlib.net.URLData;
import com.androidlib.utils.CollectionUtils;
import com.zhangweather.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 获取 xml config
 * Created by zhangliang on 16/9/12.
 */
public class UrlConfigManager {

	// 缓存url配置, 避免反复解析文件
	private static ArrayList<URLData> urlList = null;

	/**
	 * xml url config --> UrlData Entity
	 * @param activity context
	 * @param findKey  urlddd key
	 * @return
	 */
	public static URLData findURL(final Activity activity, @NonNull final String findKey) {
		if(CollectionUtils.isEmpty(urlList)) {
			fetchUrlDataFromXml(activity);
		}

		for(URLData urlData : urlList) {
			if(findKey.equals(urlData.getKey())) {
				return urlData;
			}
		}

		return null;

	}

	private static void fetchUrlDataFromXml(final Activity activity) {
		final XmlResourceParser xmlParser = activity.getApplication().getResources().getXml(R.xml.url);
		urlList = new ArrayList<>();

		int eventCode;
		try {
			eventCode = xmlParser.getEventType();
			while (eventCode != XmlPullParser.END_DOCUMENT) {
				switch (eventCode) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("Node".equals(xmlParser.getName())) {
							final URLData urlData = new URLData();
							urlData.setKey(xmlParser.getAttributeValue(null, "Key"));
							urlData.setExpires(Long.parseLong(xmlParser.getAttributeValue(null, "Expires")));
							urlData.setNetType(xmlParser.getAttributeValue(null, "NetType"));
							urlData.setUrl(xmlParser.getAttributeValue(null, "Url"));
							urlData.setMockclass(xmlParser.getAttributeValue(null, "MockClass"));
							urlList.add(urlData);
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					default:
						break;
				}
				eventCode = xmlParser.next();
			}
		} catch (final XmlPullParserException | IOException e) {
			e.printStackTrace();
		} finally {
			xmlParser.close();
		}
	}


}

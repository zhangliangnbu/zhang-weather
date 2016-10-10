package com.zhangliang.nativewithh5;

import android.app.Activity;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by zhangliang on 2016/10/10.
 */

public class ProtocolManager {

	public static ProtocolData findProtocol(String findKey, final Activity activity) {
		ProtocolData findProtocol = null;

		final XmlResourceParser xmlParser = activity.getApplication()
				.getResources().getXml(R.xml.protocol);

		int eventCode;
		try {
			eventCode = xmlParser.getEventType();
			while (eventCode != XmlPullParser.END_DOCUMENT) {
				switch (eventCode) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("Node".equals(xmlParser.getName())) {
							final String key = xmlParser.getAttributeValue(null,
									"Key");
							if (key.equals(findKey)) {
								ProtocolData protocol = new ProtocolData();
								protocol.setKey(key);
								protocol.setTarget(xmlParser.getAttributeValue(
										null, "Target"));

								findProtocol = protocol;
								break;
							}
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					default:
						break;
				}
				eventCode = xmlParser.next();
			}
		} catch (final XmlPullParserException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			xmlParser.close();
		}

		return findProtocol;
	}
}

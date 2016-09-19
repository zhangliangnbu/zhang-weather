package com.zhangweather;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}

//	@Test
//	public void testFormatJsonResponse() {
//		String result = "{\"weatherinfo\":{\"city\":\"北京\",\"cityid\":\"101010100\",\"temp\":\"18\",\"WD\":\"东南风\",\"WS\":\"1级\",\"SD\":\"17%\",\"WSE\":\"1\",\"time\":\"17:05\",\"isRadar\":\"1\",\"Radar\":\"JC_RADAR_AZ9010_JB\",\"njd\":\"暂无实况\",\"qy\":\"1011\",\"rain\":\"0\"}}";
//		String ss = HttpRequest.formatJsonResponse(result);
//		System.out.print("ss:" + ss);
//	}

	@Test
	public void testString() {
		String url="http://www.weather.com.cn/data/sk/101010100.html";
		int index = url.indexOf("sk/");
		String sub = url.substring(0, index + 3);
		System.out.print("sub:" + sub);
	}

	@Test
	public void testAscii() {
		String mock = "mock";// 10911199107
		for(char c : mock.toCharArray()) {
			System.out.print("char:" + (int) c);
		}

	}
}
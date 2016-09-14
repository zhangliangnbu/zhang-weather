package com.weather.zhangliang.androidlib;

import com.androidlib.utils.BaseUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}

	@Test
	public void testBaseUtils() {
		String str = BaseUtils.UrlEncodeUnicode("zhang123章");
		System.out.println("str:" + str);
	}
}
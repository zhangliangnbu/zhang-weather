package com.androidlib.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by zhangliang on 16/9/13.
 */
public class CollectionUtils {

	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isEmpty(Map map) {
		return map == null || map.isEmpty();
	}



}

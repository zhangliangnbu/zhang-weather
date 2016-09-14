package com.androidlib.net;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by zhangliang on 16/9/9.
 */
public class RequestParameter implements Serializable, Comparable<Object>{
	private static final long serialVersionUID = -5443497379860307067L;

	private String name;

	private String value;

	public RequestParameter(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public int compareTo(@NonNull Object another) {
		int compared;
		/**
		 * 值比较
		 */
		final RequestParameter parameter = (RequestParameter) another;
		compared = name.compareTo(parameter.name);
		if (compared == 0) {
			compared = value.compareTo(parameter.value);
		}
		return compared;
	}

	public boolean equals(final Object o) {
		if (null == o) {
			return false;
		}

		if (this == o) {
			return true;
		}

		if (o instanceof RequestParameter) {
			final RequestParameter parameter = (RequestParameter) o;
			return name.equals(parameter.name) && value.equals(parameter.value);
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}

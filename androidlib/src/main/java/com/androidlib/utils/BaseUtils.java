package com.androidlib.utils;

/**
 * Created by zhangliang on 16/9/9.
 */
public class BaseUtils {

	// string to unicode
	public static String UrlEncodeUnicode(final String s)
	{
		if (s == null)
		{
			return null;
		}
		final int length = s.length();
		final StringBuilder builder = new StringBuilder(length); // buffer
		for (int i = 0; i < length; i++)
		{
			final char ch = s.charAt(i);
			if ((ch & 0xff80) == 0)// ch < 128 ascii 单字节字符
			{
				if (BaseUtils.IsSafe(ch))
				{
					builder.append(ch);
				}
				else if (ch == ' ')
				{
					builder.append('+');
				}
				else
				{// ch to hex
					builder.append('%');
					builder.append(BaseUtils.IntToHex((ch >> 4) & 15));
					builder.append(BaseUtils.IntToHex(ch & 15));
				}
			}
			else
			{// 2字节
				builder.append("%u");
				builder.append(BaseUtils.IntToHex((ch >> 12) & 15));
				builder.append(BaseUtils.IntToHex((ch >> 8) & 15));
				builder.append(BaseUtils.IntToHex((ch >> 4) & 15));
				builder.append(BaseUtils.IntToHex(ch & 15));
			}
		}
		return builder.toString();
	}

	static char IntToHex(final int n)// 0 - 15
	{
		if (n <= 9)
		{
			return (char) (n + 0x30);// 0 - 9
		}
		return (char) ((n - 10) + 0x61);// a - f
	}

	static boolean IsSafe(final char ch)
	{
		if ((((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))) || ((ch >= '0') && (ch <= '9')))
		{
			return true;
		}
		switch (ch)
		{
			case '\'':
			case '(':
			case ')':
			case '*':
			case '-':
			case '.':
			case '_':
			case '!':
				return true;
		}
		return false;
	}

}

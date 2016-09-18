package com.androidlib.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

	private static char IntToHex(final int n)// 0 - 15
	{
		if (n <= 9)
		{
			return (char) (n + 0x30);// 0 - 9
		}
		return (char) ((n - 10) + 0x61);// a - f
	}

	private static boolean IsSafe(final char ch)
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

	/**
	 * /data 文件夹容量
	 * @return 字节数
	 */
	public static long getAvaiDataStorageSize() {
		String str = Environment.getDataDirectory().getPath();
		StatFs statFs = new StatFs(str);

		long blockSize = 0L;
		long avaiBlockCount = 0L;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = statFs.getBlockSizeLong();
			avaiBlockCount = statFs.getAvailableBlocksLong();
		} else {
			blockSize = statFs.getBlockSize();
			avaiBlockCount = statFs.getAvailableBlocks();
		}

		return avaiBlockCount * blockSize;
	}

	/**
	 * /system 文件夹可用容量
	 * @return 可用字节数
	 */
	public static long getAvaiRootStorageSize() {
		String str = Environment.getRootDirectory().getPath();
		StatFs statFs = new StatFs(str);

		long blockSize = 0L;
		long avaiBlockCount = 0L;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = statFs.getBlockSizeLong();
			avaiBlockCount = statFs.getAvailableBlocksLong();
		} else {
			blockSize = statFs.getBlockSize();
			avaiBlockCount = statFs.getAvailableBlocks();
		}
		return avaiBlockCount * blockSize;
	}

	/**
	 * 获取SD卡剩余空间的大小
	 *
	 * @return long SD卡剩余空间的大小（单位：byte）
	 */
	public static long getAvaiSDStorageSize()
	{
		String str = Environment.getExternalStorageDirectory().getPath();
		StatFs localStatFs = new StatFs(str);
		long blockSize = 0, avaiBlockCount = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = localStatFs.getBlockSizeLong();
			avaiBlockCount = localStatFs.getAvailableBlocksLong();
		} else {
			blockSize = localStatFs.getBlockSize();
			avaiBlockCount = localStatFs.getAvailableBlocks();
		}
		return avaiBlockCount * blockSize;
	}

	public static boolean isSdcardMounted() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED) && !state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}

	/**
	 * 将对象序列化存入文件
	 * @param path   文件路径
	 * @param object 实体
	 */
	public static boolean saveObject(String path, Object object) {
		File file = new File(path);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			// return 时,先保存return的数据,然后执行finally,之后执行return
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeIO(fos, oos);
		}
		return false;
	}

	/**
	 * 从文件中读入实体
	 * @param path object file path
	 * @return 实体
	 */
	public static Object restoreObject(String path) {
		File file = new File(path);
		if(!file.exists()) {
			return null;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			closeIO(fis, ois);
		}

		return null;
	}

	/**
	 * 关闭
	 * @param res 资源
	 */
	public static void closeIO(@Nullable Object... res){
		if(res == null) {
			return;
		}

		for (Object r : res) {
			if(r==null){
				continue;
			}
			if(r instanceof SQLiteDatabase){
				closeSqliteDB((SQLiteDatabase) r);
			}else if (r instanceof Cursor){
				closeCursor((Cursor) r);
			}else if(r instanceof Socket){
				closeSocket((Socket) r);
			} else if (r instanceof Closeable){
				closeIt((Closeable) r);
			}else{
				Log.w("IO", "close unknown:" + r.getClass().getCanonicalName());
			}
		}
	}

	private static void closeSocket(Socket r) {
		if(!r.isClosed()){
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeIt(@NonNull Closeable r) {
		try {
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void closeCursor(@NonNull Cursor cursor) {
		if(!cursor.isClosed()){
			cursor.close();
		}
	}

	private static void closeSqliteDB(@NonNull SQLiteDatabase db) {db.close();}



}

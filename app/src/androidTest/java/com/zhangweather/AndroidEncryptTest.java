package com.zhangweather;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.androidlib.utils.BaseUtils;
import com.androidlib.utils.EncryptUtils;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

/**
 * 加密和解密
 * Created by zhangliang on 16/9/5.
 */
public class AndroidEncryptTest extends InstrumentationTestCase{

	public void testBASE64(){
		String text = "zhang";
		System.out.println("text:" + text);
		String csn = Charset.defaultCharset().name();
		System.out.println("csn:" + csn);

		char[] chars = text.toCharArray();
		EncryptUtils.printChars("chars", chars);

		byte[] bytes = text.getBytes();
		EncryptUtils.printBytes("bytes", bytes);

		String encryptBASE64 = null;
		try {
			encryptBASE64 = EncryptUtils.encryptBASE64(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("encryptBASE64:" + encryptBASE64);

		String decodeText = null;
		try {
			decodeText = EncryptUtils.decryptBASE64(encryptBASE64);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("encryptBASE64:" + decodeText);
	}

	public void testSHA() {
		String encryptText = null;
		try {
			encryptText = EncryptUtils.encryptHash("zhang", EncryptUtils.KEY_SHA1);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		System.out.println("sha:" + encryptText);
	}

	public void testHMAC() {
		String encryptText = null;
		try {
			encryptText = EncryptUtils.parseBytesToHexString(EncryptUtils.encryptHMACSHA256("zhang".getBytes(), EncryptUtils.initHMACSHA256Key()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("mac:" + encryptText);
	}

	public void testAES() throws Exception {
		String originalText = "zhang";

		byte[] keyBytes = EncryptUtils.initAES256Key(null);
		System.out.println("keyBytes: " + EncryptUtils.parseBytesToHexString(keyBytes));
		System.out.println("keyBytes size: " + keyBytes.length);

		byte[] encryptBytes = EncryptUtils.encryptAES(originalText.getBytes(),keyBytes);
		String encryptText = EncryptUtils.parseBytesToHexString(encryptBytes);
		System.out.println("encryptText: " + encryptText);

		byte[] decryptBytes = EncryptUtils.decryptAES(encryptBytes,keyBytes);
		String decryptText = new String(decryptBytes);
		System.out.println("decryptText: " + decryptText);
	}

	public void testStorage() {
		long data = BaseUtils.getAvaiDataStorageSize();
		long root = BaseUtils.getAvaiRootStorageSize();
		Log.d("data-root:", data + "-" + root);
	}

}

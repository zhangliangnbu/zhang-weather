package com.zhangweather;

import android.test.InstrumentationTestCase;

import com.androidlib.utils.Encrypt;

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
		Encrypt.printChars("chars", chars);

		byte[] bytes = text.getBytes();
		Encrypt.printBytes("bytes", bytes);

		String encryptBASE64 = null;
		try {
			encryptBASE64 = Encrypt.encryptBASE64(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("encryptBASE64:" + encryptBASE64);

		String decodeText = null;
		try {
			decodeText = Encrypt.decryptBASE64(encryptBASE64);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("encryptBASE64:" + decodeText);
	}

	public void testSHA() {
		String encryptText = null;
		try {
			encryptText = Encrypt.encryptHash("zhang", Encrypt.KEY_SHA1);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		System.out.println("sha:" + encryptText);
	}

	public void testHMAC() {
		String encryptText = null;
		try {
			encryptText = Encrypt.parseBytesToHexString(Encrypt.encryptHMACSHA256("zhang".getBytes(), Encrypt.initHMACSHA256Key()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("mac:" + encryptText);
	}

	public void testAES() throws Exception {
		String originalText = "zhang";

		byte[] keyBytes = Encrypt.initAES256Key(null);
		System.out.println("keyBytes: " + Encrypt.parseBytesToHexString(keyBytes));
		System.out.println("keyBytes size: " + keyBytes.length);

		byte[] encryptBytes = Encrypt.encryptAES(originalText.getBytes(),keyBytes);
		String encryptText = Encrypt.parseBytesToHexString(encryptBytes);
		System.out.println("encryptText: " + encryptText);

		byte[] decryptBytes = Encrypt.decryptAES(encryptBytes,keyBytes);
		String decryptText = new String(decryptBytes);
		System.out.println("decryptText: " + decryptText);

	}

}

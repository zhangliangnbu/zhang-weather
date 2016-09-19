package com.zhangweather.engine;

import com.zhangweather.utils.Utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * user 管理类 全局唯一
 * Created by zhangliang on 16/9/19.
 */

public class User implements Serializable, Cloneable {
	private static final long serialVersionUID = -2754155970400767973L;
	public final static String TAG = "User";
	private static User instance;

	// 单例
	private User(){}
	public static User getInstance() {
		if(instance == null) {
			Object object = Utils.restoreObject(AppConstants.CACHEDIR + TAG);
			if(object == null) {
				instance = new User();
				Utils.saveObject(AppConstants.CACHEDIR + TAG, instance);
			} else {
				instance = (User) object;
			}
		}

		return instance;
	}

	private String loginName;
	private String userName;
	private int score;
	private boolean loginStatus;

	public void reset() {
		loginName = null;
		userName = null;
		score = 0;
		loginStatus = false;
	}

	public void save() {
		Utils.saveObject(AppConstants.CACHEDIR + TAG, instance);
	}

	public boolean isLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	// -----------以下3个方法用于序列化???-----------------
	public User readResolve() throws ObjectStreamException, CloneNotSupportedException {
		instance = (User) this.clone();
		return instance;
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
	}

	public Object Clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

package com.zhangweather.entity;

/**
 * user information
 * Created by zhangliang on 16/9/19.
 */

public class UserInfo {
	private String loginName;
	private String userName;
	private int score;
	private boolean loginStatus;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
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

	public boolean isLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
	}
}

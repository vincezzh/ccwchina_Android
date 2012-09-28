package com.ccwchina;

import com.ccwchina.bean.User;

import android.app.Application;

public class CCWApplication extends Application {
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}

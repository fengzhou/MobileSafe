package com.example.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/1/4.
 */
public class TaskInfo {
	private String name;
	private String packagename;
	private Drawable icon;
	//true 表示用户进程，false 表示系统进程
	private boolean userTask;
	private long memsize;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public boolean isUserTask() {
		return userTask;
	}

	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
	}

	public long getMemsize() {
		return memsize;
	}

	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
}

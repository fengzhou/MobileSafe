package com.example.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/12/24.
 */
public class AppInfo {

	private Drawable icon;
	private String name;
	private String packagename;
	private boolean isRom;
	private boolean isUserApp;

	public boolean isUserApp() {
		return isUserApp;
	}

	public void setIsUserApp(boolean isUserApp) {
		this.isUserApp = isUserApp;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

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

	public boolean isRom() {
		return isRom;
	}

	public void setIsRom(boolean isRom) {
		this.isRom = isRom;
	}
}

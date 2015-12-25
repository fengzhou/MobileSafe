package com.example.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于提供手机中所有应用信息的提供类
 */
public class AppInfoProvider {


	public static List<AppInfo> getAllAppInfos(Context context){
		List<AppInfo> lists = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> infos = pm.getInstalledApplications(0);
		for (ApplicationInfo info : infos){
			AppInfo appInfo = new AppInfo();
			String packageName = info.packageName;
			Drawable drawable = info.loadIcon(pm);
			String name = info.loadLabel(pm).toString();
			int flags = info.flags;
			if((flags & info.FLAG_SYSTEM)==0){
				//用户程序
				appInfo.setIsUserApp(true);
			}else{
				//系统程序
				appInfo.setIsUserApp(false);
			}
			if((flags & info.FLAG_EXTERNAL_STORAGE) == 0){
				//手机内存
				appInfo.setIsRom(true);
			}else{
				//外部存储
				appInfo.setIsRom(false);
			}
			appInfo.setName(name);
			appInfo.setIcon(drawable);
			appInfo.setPackagename(packageName);
			lists.add(appInfo);
		}
		return lists;
	}

}

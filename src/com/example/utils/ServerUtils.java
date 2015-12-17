package com.example.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2015/12/14.
 */
public class ServerUtils {

	/**
	 * 校验某个服务是否还在运行中
	 * @param context
	 * @param ServerName
	 * @return
	 */
	public static boolean isServerRunning(Context context,String ServerName){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(1000);
		for (ActivityManager.RunningServiceInfo info : infos){
			//得到正在运行的服务的名字
			String name = info.service.getClassName();
			if(ServerName.equals(name)){
				return true;
			}
		}
		return false;
	}
}

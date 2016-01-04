package com.example.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
public class TaskInfoProvider {


	/**
	 * 获取正在运行的进程的信息
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getRunningTaskMem(Context context){
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcessInfo :processInfos){
			TaskInfo taskInfo = new TaskInfo();
			//应用程序的pid
			int pid = appProcessInfo.pid;
			//下面几行代码 根据进程的pid获取占用的内存信息
			Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{pid});
			long processmem = memoryInfos[0].getTotalPrivateDirty()*1024;
			taskInfo.setMemsize(processmem);
			//应用程序的包名
			String packagename = appProcessInfo.processName;
			taskInfo.setPackagename(packagename);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename,0);
				//应用程序的图标
				Drawable icon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				//应用程序的名称
				String name = (String) applicationInfo.loadLabel(pm);
				taskInfo.setName(name);
				if((applicationInfo.flags& applicationInfo.FLAG_SYSTEM) == 0){
					//用户进程
					taskInfo.setUserTask(true);
				}else{
					//系统进程
					taskInfo.setUserTask(false);
				}
				taskInfos.add(taskInfo);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return taskInfos;
	}

}

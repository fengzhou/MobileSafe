package com.example.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.*;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
public class AppMemrotUtils {


	/**
	 * 获取手机正在运行的app进程
	 * @param context
	 * @return
	 */
	public static int getRunningProcess(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
		return lists.size();
	}

	/**
	 * 获取手机可以内存
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	/**
	 * 获取手机总内存(android 4.0 以上的代码是可以直接使用的，但是为了兼容老版本所以使用了下面的方法)
	 * @param context
	 * @return
	 */
	public static long getTotalMem(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		return memoryInfo.totalMem;
	}

	public static long getTotalMem2() {

		try {
			File file = new File("/proc/meminfo");
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader bf = new BufferedReader(new InputStreamReader(fileInputStream));
			String line = bf.readLine();
			StringBuffer sb = new StringBuffer();
			for (char c : line.toCharArray()){
				if(c>='0' && c<='9'){
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
package com.example.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by Administrator on 2016/1/6.
 */
public class KillAllReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo info :infoList){
			am.killBackgroundProcesses(info.processName);
		}
	}
}

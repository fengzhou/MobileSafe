package com.example.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

/**
 * Created by Administrator on 2016/1/5.
 */
public class LockTaskClearServer extends Service{

	private LockReceiver receiver;
	private ActivityManager am;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		receiver = new LockReceiver();
		registerReceiver(receiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}

	/**
	 * 广播接收者
	 */
	private class LockReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			List<ActivityManager.RunningAppProcessInfo> infoLists = am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo info : infoLists){
				am.killBackgroundProcesses(info.processName);
			}
		}
	}

}

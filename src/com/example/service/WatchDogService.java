package com.example.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import com.example.MobileSafe.WatchDogMimaActivity;
import com.example.db.ApplockDao;

import java.util.List;

/**
 * 用于获取当前正在打开的app的报名,如果被设置了程序锁，那么在打开的时候弹出密码输入框
 */
public class WatchDogService extends Service{
	private boolean flag;
	private ApplockDao dao;
	private String stoppackagename;
	private StopWatchReceiver receiver;
	private ScreenLockReceiver lockReceiver;
	private ScreenUnlockReceiver unlockReceiver;
	private List<String> protectPackagenames;
	private DBChangedReceiver dbChangedReceiver;
	private Intent intent;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		flag = true;
		dao = new ApplockDao(this);
		protectPackagenames = dao.protectPackagename();
		//如果设置了程序锁,启动看门狗的密码输入界面
		intent = new Intent(getApplicationContext(), WatchDogMimaActivity.class);
		receiver = new StopWatchReceiver();
		registerReceiver(receiver,new IntentFilter("com.example.watchdog.stop"));
		lockReceiver = new ScreenLockReceiver();
		registerReceiver(lockReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
		unlockReceiver = new ScreenUnlockReceiver();
		registerReceiver(unlockReceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
		dbChangedReceiver = new DBChangedReceiver();
		registerReceiver(dbChangedReceiver,new IntentFilter("com.example.db.changed"));
		super.onCreate();
		WatchDog();
	}

	private void WatchDog() {
		new Thread(){
			@Override
			public void run() {
				super.run();
				while (flag){
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					List<ActivityManager.RunningAppProcessInfo> taskInfoLists = am.getRunningAppProcesses();
					String packagename = taskInfoLists.get(0).processName;
					if(protectPackagenames.contains(packagename)){
						if(packagename.equals(stoppackagename)){
						}else {
							intent.putExtra("packagename", packagename);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}
					try {
						Thread.sleep(90);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		unregisterReceiver(lockReceiver);
		unregisterReceiver(unlockReceiver);
		unregisterReceiver(dbChangedReceiver);
		receiver = null;
		unlockReceiver = null;
		lockReceiver = null;
		dbChangedReceiver = null;
		flag = false;
	}

	/**
	 * 监听停止监听的广播
	 * com.example.watchdog.stop
	 */
	private class StopWatchReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			stoppackagename = intent.getStringExtra("packagename");
		}
	}

	/**
	 * 锁屏监听
	 */
	private class ScreenLockReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			stoppackagename = "";
			flag = false;
		}
	}

	/**
	 * 屏幕解锁时监听
	 */
	private class ScreenUnlockReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			flag = true;
			WatchDog();
		}
	}

	/**
	 * 数据库改变监听
	 */
	private class DBChangedReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			protectPackagenames = dao.protectPackagename();
		}
	}

}

package com.example.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.example.MobileSafe.MyWidget;
import com.example.MobileSafe.R;
import com.example.utils.AppMemrotUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/6.
 */
public class UpdagewidgetServer extends Service{

	private Timer timer;
	private TimerTask task;
	private AppWidgetManager am;

	private ScreenOffReceiver screenOffReceiver;
	private ScreenOnReceiver screenOnReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		am = AppWidgetManager.getInstance(this);
		screenOffReceiver = new ScreenOffReceiver();
		screenOnReceiver = new ScreenOnReceiver();
		registerReceiver(screenOffReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(screenOnReceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
		startTimeTask();
		super.onCreate();
	}


	/**
	 * 屏幕锁屏广播接收者
	 */
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("lock screen off");
			stopTimeTask();
		}
	}

	/**
	 * 屏幕解锁广播接收者
	 */
	private class ScreenOnReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			startTimeTask();
			System.out.println("lock screen on,start task");
		}
	}

	private void startTimeTask() {
		if(timer==null && task == null) {
			System.out.println("start ----> task");
			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {
					//设置更新组件
					ComponentName componentName = new ComponentName(UpdagewidgetServer.this, MyWidget.class);
					RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
					remoteViews.setTextViewText(R.id.process_count, "正在运行的进程数: " +
							AppMemrotUtils.getRunningProcess(getApplicationContext()) + "个");
					remoteViews.setTextViewText(R.id.process_memory, "剩余可用内存: " + android.text.format.Formatter.formatFileSize(getApplicationContext(), AppMemrotUtils.getAvailMem(getApplicationContext())));

					// 描述一个动作,这个动作是由另外的一个应用程序执行的.
					// 自定义一个广播事件,杀死后台进度的事
					Intent intent = new Intent();
					intent.setAction("com.example.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

					am.updateAppWidget(componentName, remoteViews);

				}
			};
			timer.schedule(task, 0, 5000);
		}
	}

	/**
	 * 停止定时任务
	 */
	private void stopTimeTask(){
		System.out.println("stop the task");
		if(timer!=null && task!=null){
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(screenOnReceiver);
		unregisterReceiver(screenOffReceiver);
		screenOffReceiver = null;
		screenOnReceiver = null;
		stopTimeTask();
		super.onDestroy();
	}
}

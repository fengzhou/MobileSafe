package com.example.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import com.example.db.BlackNumberDao;

/**
 * Created by Administrator on 2015/12/21.
 */
public class CallSmsService extends Service{

	private CallSmsReceiver receiver;
	private BlackNumberDao dao;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class CallSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//通过代码注册的方法获取注册短信接受广播
			//下面获取短信的发送者号码 objects数组中每一个元素都是一个短信对象
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects){
				//创建短信管理器
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				//获取发送者
				String sender = smsMessage.getDisplayOriginatingAddress();
				String mode = dao.findMode(sender);
				if("2".equals(mode) || "3".equals(mode)){
					//短信需要被拦截,终止广播,其他的接收器就接收不到了
					abortBroadcast();
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(this);
		receiver = new CallSmsReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, intentFilter);
	}
}

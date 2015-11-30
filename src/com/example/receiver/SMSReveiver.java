package com.example.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import com.example.MobileSafe.R;
import com.example.service.GPSservice;

/**
 * Created by Administrator on 2015/11/27.
 */
public class SMSReveiver extends BroadcastReceiver{

	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		String phone = sp.getString("phone","");
		//接收短信
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs){
			//得到具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			String sender = sms.getOriginatingAddress();
			if(sender.contains(phone)) {
				String body = sms.getMessageBody();
				if (("#*location*3").equals(body)) {
					//得到手机的gps
					Intent i = new Intent(context, GPSservice.class);
					context.startService(i);
					SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
					String last_location = sp.getString("lastlocation",null);
					if(TextUtils.isEmpty(last_location)){
						//位置没有得到
						SmsManager.getDefault().sendTextMessage(sender,null,"正在获取位置.............",null,null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender,null,last_location,null,null);
					}
					//终止这个广播
					abortBroadcast();
				} else if (("#*alarm*#").equals(body)) {
					//播放报警音乐
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.setLooping(true); //设置为true，表示循环播放
					player.setVolume(1.0f,1.0f); //左右声道
					player.start();
					//终止这个广播
					abortBroadcast();
				} else if (("#*wipedata*#").equals(body)) {
					//远程清除数据

					//终止这个广播
					abortBroadcast();
				} else if (("#*lockscreen*#").equals(body)) {
					//远程锁屏

					//终止这个广播
					abortBroadcast();
				}
			}
		}
	}
}

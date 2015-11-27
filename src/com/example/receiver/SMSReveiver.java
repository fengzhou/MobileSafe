package com.example.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/**
 * Created by Administrator on 2015/11/27.
 */
public class SMSReveiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		//接收短信
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs){
			//得到具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			String sender = sms.getOriginatingAddress();
			String body = sms.getMessageBody();
		}
	}
}

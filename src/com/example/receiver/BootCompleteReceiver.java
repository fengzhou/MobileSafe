package com.example.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/11/26.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
	private SharedPreferences sp;
	private TelephonyManager telephonyManager;
	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//读取之前保存的sim卡信息
		String save_sim = sp.getString("sim",null);
		//读取当前的sim卡信息
		String sim = telephonyManager.getSimSerialNumber();
		//比较是否相等
		if(save_sim.equals(sim)){
			//如果相等
		}else{
			//如果不相等,发短信给安全号码
			Toast.makeText(context,"sim card 已变更",Toast.LENGTH_LONG).show();
		}
	}
}

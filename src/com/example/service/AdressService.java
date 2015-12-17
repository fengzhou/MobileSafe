package com.example.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.example.MobileSafe.R;
import com.example.utils.SQLDBTools;

/**
 * 来电显示窗体
 */
public class AdressService extends Service {

	//窗体管理者
	private WindowManager wm;
	private View view;

	//电话服务
	private TelephonyManager tm;
	private myPhoneListener listener;

	private OutCallReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class OutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			String address = SQLDBTools.queryPhoneNumber(phone);
			myToast(address);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener,PhoneStateListener.LISTEN_NONE);
		listener = null;
		unregisterReceiver(receiver);
		receiver = null;
	}

	class myPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state){
				case TelephonyManager.CALL_STATE_RINGING: //电话铃声响起的时候
					String address = SQLDBTools.queryPhoneNumber(incomingNumber);
					myToast(address);
					break;
				case TelephonyManager.CALL_STATE_IDLE: //电话的空闲状态：挂断或则来电拒绝等
					if(view!=null){
						wm.removeView(view);
					}
					break;
				default:
					break;
			}
		}
	}

	public void myToast(String address){
		view = View.inflate(this, R.layout.activity_address,null);
		TextView textView = (TextView) view.findViewById(R.id.tv_address);

		//"半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
				,R.drawable.call_locate_gray,R.drawable.call_locate_green};
		SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
		view.setBackgroundResource(ids[sp.getInt("which",0)]);
		textView.setText(address);

		WindowManager.LayoutParams params =
				new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		wm.addView(view,params);
	}

}

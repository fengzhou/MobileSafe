package com.example.service;

import android.app.Service;
import android.content.*;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.example.MobileSafe.R;
import com.example.utils.SQLDBTools;

/**
 * 来电显示窗体
 */
public class AdressService extends Service {

	private static final String TAG = "location";
	//窗体管理者
	private WindowManager wm;
	private View view;
	private int startX;
	private int startY;

	//电话服务
	private TelephonyManager tm;
	private myPhoneListener listener;

	private OutCallReceiver receiver;

	private WindowManager.LayoutParams params;

	private SharedPreferences sp;

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
		sp = getSharedPreferences("config",MODE_PRIVATE);
		params = new WindowManager.LayoutParams();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new myPhoneListener();
		tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);

		//用代码去注册广播接收者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		//实例化 窗体
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);

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
		view.setBackgroundResource(ids[sp.getInt("which",0)]);
		textView.setText(address);
		final long[] mites = new long[2];
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(mites,0,mites,1,mites.length-1);
				mites[mites.length-1] = SystemClock.uptimeMillis();
				if(mites[0] >=(SystemClock.uptimeMillis()-500)){
					params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
					wm.updateViewLayout(view,params);
					SharedPreferences.Editor editor = sp.edit();
					editor.putInt("startx",params.x);
					editor.commit();
				}
			}
		});


		//设置触摸监听事件
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN:
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:
						int newX = (int) event.getRawX();
						int newY = (int) event.getRawY();
						int dx = newX - startX;
						int dy = newY - startY;
						params.x += dx;
						params.y += dy;
						if(params.x<0){
							params.x = 0;
						}
						if(params.y<0){
							params.y = 0;
						}
						if(params.x>(wm.getDefaultDisplay().getWidth()-view.getWidth())){
							params.x = wm.getDefaultDisplay().getWidth()-view.getWidth();
						}
						if(params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())){
							params.y = wm.getDefaultDisplay().getHeight()- view.getHeight();
						}
						//重新初始化起始位置的坐标
						wm.updateViewLayout(view,params);
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_UP:
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt("startx",params.x);
						editor.putInt("starty",params.y);
						editor.commit();
						break;
				}
				return false;//返回true 表示该事件处理完成了，不需要父控件或者其他控件来处理了
			}
		});
		int xx = sp.getInt("startx",0);
		int yy = sp.getInt("starty",0);
		params.x = xx;
		params.y = yy;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
	}

}

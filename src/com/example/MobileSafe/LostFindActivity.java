package com.example.MobileSafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/11/26.
 */
public class LostFindActivity extends Activity {

	private SharedPreferences sp;
	private TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		boolean isconfiged = sp.getBoolean("configed",false);
		if(isconfiged){
			//已经设置了向导,直接进入主界面
			setContentView(R.layout.activity_lostfind);
			textView = (TextView) findViewById(R.id.safenumber);
			String phone = sp.getString("phone",null);
			textView.setText(phone);
		}else{
			//还没有设置向导,弹出设置向导界面
			Intent intent = new Intent(LostFindActivity.this,Step1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 重新进入手机防盗设置向导页面
	 * @param view
	 */
	public void reEnterSetup(View view){
		Intent intent = new Intent(this,Step1Activity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

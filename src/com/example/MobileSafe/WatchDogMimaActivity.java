package com.example.MobileSafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/1/7.
 */
public class WatchDogMimaActivity extends Activity{

	private ImageView iv_dog_src;
	private TextView tv_dog_packagename;
	private EditText et_dog_pwd;
	private String packagename;
	private PackageManager pm;
	private SharedPreferences sp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchdogmima);
		iv_dog_src = (ImageView) findViewById(R.id.iv_doc_prc);
		tv_dog_packagename = (TextView) findViewById(R.id.tv_dog_packagename);
		et_dog_pwd = (EditText) findViewById(R.id.et_dog_pwd);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		pm = getPackageManager();
		Intent intent = getIntent();
		packagename = intent.getStringExtra("packagename");
		tv_dog_packagename.setText(packagename);
		try {
			ApplicationInfo info = pm.getApplicationInfo(packagename,0);
			Drawable drawable = info.loadIcon(pm);
			iv_dog_src.setImageDrawable(drawable);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

	}
	public void quitWatchDog(View view){
		String ppwd = sp.getString("applock","");
		String pwd = et_dog_pwd.getText().toString().trim();
		if(TextUtils.isEmpty(pwd)){
			Toast.makeText(this,"密码不能为空!",Toast.LENGTH_SHORT).show();
			return;
		}else if (ppwd.equals(pwd)){
			//自定义一个广播，停止监听
			Intent intent = new Intent();
			intent.putExtra("packagename",packagename);
			intent.setAction("com.example.watchdog.stop");
			sendBroadcast(intent);
			finish();
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	/**
	 * 按返回键直接退回到桌面
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}
}

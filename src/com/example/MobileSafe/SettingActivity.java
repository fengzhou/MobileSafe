package com.example.MobileSafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.service.AdressService;
import com.example.service.CallSmsService;
import com.example.service.WatchDogService;
import com.example.ui.SettingClickView;
import com.example.ui.SettingItemView;
import com.example.utils.ServerUtils;


/**
 * 设置activity
 */
public class SettingActivity extends Activity {


	//设置显示是否开启自动更新
	private SettingItemView siv_update;

	private SharedPreferences sp;

	//设置开启来电归属地
	private SettingItemView siv_phoneaddress;

	private SettingClickView set_changebk;
	private Intent intent;
	private boolean isServerRunning = false;

	//黑名单拦截设置
	private SettingItemView siv_callsms_safe;

	private SettingItemView siv_watchdog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		siv_update = (SettingItemView) findViewById(R.id.srv_update);
		set_changebk = (SettingClickView) findViewById(R.id.set_changebg);
		siv_callsms_safe = (SettingItemView) findViewById(R.id.srv_callsms_safe);
		siv_watchdog = (SettingItemView) findViewById(R.id.srv_watchdog);
		boolean update = sp.getBoolean("update",false);
		if(update){
			//自动升级已经打开
			siv_update.setBoxChecked(true);
		}else{
			//自动升级已经关闭
			siv_update.setBoxChecked(false);
		}
		siv_update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SharedPreferences.Editor editor = sp.edit();
				//判断是否选中
				//已经打开自动更新
				if(siv_update.isChecked()){
					siv_update.setBoxChecked(false);
					editor.putBoolean("update",false);
				}else{
					siv_update.setBoxChecked(true);
					editor.putBoolean("update",true);
				}
				editor.commit();
			}
		});
		siv_phoneaddress = (SettingItemView) findViewById(R.id.srv_addphoneress);
		intent = new Intent(this, AdressService.class);
		isServerRunning = ServerUtils.isServerRunning(getApplicationContext(),"com.example.service.AdressService");
		if(isServerRunning){
			//如果服务开启
			siv_phoneaddress.setBoxChecked(true);
		}else {
			siv_phoneaddress.setBoxChecked(false);
		}
		siv_phoneaddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(siv_phoneaddress.isChecked()){
					//变为非选中状态
					stopService(intent);
					siv_phoneaddress.setBoxChecked(false);
				}else{
					//选择启动
					startService(intent);
					siv_phoneaddress.setBoxChecked(true);
				}
			}
		});
		final Intent intent = new Intent(this, CallSmsService.class);
		siv_callsms_safe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(siv_callsms_safe.isChecked()){
					stopService(intent);
					siv_callsms_safe.setBoxChecked(false);
				}else{
					startService(intent);
					siv_callsms_safe.setBoxChecked(true);
				}
			}
		});
		isServerRunning = ServerUtils.isServerRunning(this,"com.example.service.WatchDogService");
		if(isServerRunning){
			//表示服务开启，且正在运行
			siv_watchdog.setBoxChecked(true);
		}else{
			//表示服务未开启
			siv_watchdog.setBoxChecked(false);
		}


		//开启开门狗服务
		final Intent wathdogserver = new Intent(this, WatchDogService.class);
		siv_watchdog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(!siv_watchdog.isChecked()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
					View view = View.inflate(SettingActivity.this, R.layout.dialog_watchdog_pwd, null);
					builder.setView(view);
					et_watchdog_pwd1 = (EditText) view.findViewById(R.id.et_watchdog_pwd1);
					et_watchdog_pwd2 = (EditText) view.findViewById(R.id.et_watchdog_pwd2);
					bt_watchdog_ok = (Button) view.findViewById(R.id.bt_watchdog_ok);
					bt_watchdog_cancel = (Button) view.findViewById(R.id.bt_watchdog_cancel);

					bt_watchdog_ok.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String pwd1 = et_watchdog_pwd1.getText().toString().trim();
							String pwd2 = et_watchdog_pwd2.getText().toString().trim();
							if (TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2)) {
								Toast.makeText(SettingActivity.this, "对不起，密码不能为空.", Toast.LENGTH_SHORT).show();
								return;
							} else {
								if (pwd1.equals(pwd2)) {
									SharedPreferences.Editor editor = sp.edit();
									editor.putString("applock", pwd1);
									editor.commit();
									Toast.makeText(SettingActivity.this, "设置成功!", Toast.LENGTH_SHORT).show();
									siv_watchdog.setBoxChecked(true);
									startService(wathdogserver);
									dialog.dismiss();
								} else {
									Toast.makeText(SettingActivity.this, "对不起，两次密码输入不正确.", Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
					bt_watchdog_cancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog = builder.show();
				}else{
					siv_watchdog.setBoxChecked(false);
					stopService(wathdogserver);
				}
			}
		});
		set_changebk.setTitle("归属地提示风格");
		int default_which = sp.getInt("which",0);
		final String[] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		set_changebk.setDesc(items[default_which]);
		set_changebk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int dd = sp.getInt("which",0);
				AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
				builder.setTitle("归属地图片显示");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//保存用户的选择，然后在退出后显示用户的选择
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt("which",which);
						editor.commit();
						set_changebk.setDesc(items[which]);

						//关闭对话框
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消",null);
				builder.show();
			}
		});
	}

	private EditText et_watchdog_pwd1;
	private EditText et_watchdog_pwd2;
	private Button bt_watchdog_ok;
	private Button bt_watchdog_cancel;
	private AlertDialog dialog;

	@Override
	protected void onResume() {
		super.onResume();
		boolean isServerRunning = ServerUtils.isServerRunning(SettingActivity.this,"com.example.service.AdressService");
		if(isServerRunning){
			//监听服务是开启的
			siv_phoneaddress.setBoxChecked(true);
		}else{
			siv_phoneaddress.setBoxChecked(false);
		}

		boolean isCallSmsServerRunning = ServerUtils.isServerRunning(SettingActivity.this,"com.example.service.CallSmsService");
		siv_callsms_safe.setBoxChecked(isCallSmsServerRunning);
	}
}

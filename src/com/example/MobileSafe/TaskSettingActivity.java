package com.example.MobileSafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.example.service.LockTaskClearServer;
import com.example.utils.ServerUtils;

/**
 * Created by Administrator on 2016/1/5.
 */
public class TaskSettingActivity extends Activity{

	private CheckBox cb_task_showsys;
	private CheckBox cb_task_lockclear;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("create tasksetting activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasksetting);
		sp = getSharedPreferences("config",MODE_PRIVATE);

		cb_task_showsys = (CheckBox) findViewById(R.id.cb_task_showsys);
		cb_task_lockclear = (CheckBox) findViewById(R.id.cb_task_lockclear);

		cb_task_showsys.setChecked(sp.getBoolean("showsys",false));
		cb_task_showsys.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("showsys",isChecked);
				editor.commit();
			}
		});

		cb_task_lockclear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(getApplicationContext(), LockTaskClearServer.class);
				if(isChecked){
					//勾选了锁屏清理
					startService(intent);
				}else{
					stopService(intent);
				}
			}
		});

	}
	@Override
	protected void onStart() {
		boolean flag = ServerUtils.isServerRunning(this,"com.example.service.LockTaskClearServer");
		cb_task_lockclear.setChecked(flag);
		super.onStart();
	}
}

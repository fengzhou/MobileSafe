package com.example.MobileSafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.example.ui.SettingItemView;

/**
 * 设置activity
 */
public class SettingActivity extends Activity {
	private SettingItemView siv_update;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		siv_update = (SettingItemView) findViewById(R.id.srv_update);
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
	}
}

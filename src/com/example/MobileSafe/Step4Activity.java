package com.example.MobileSafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by Administrator on 2015/11/26.
 */
public class Step4Activity extends BaseSetupActivity {
	private CheckBox checkBox;
	private Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step4);
		checkBox = (CheckBox) findViewById(R.id.cb_proteting);
		button = (Button) findViewById(R.id.pre4_3);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pre4_3();
			}
		});
		boolean proteting = sp.getBoolean("proteting",false);
		if(proteting){
			checkBox.setText("手机防盗已经开启");
			checkBox.setChecked(true);
		}else{
			checkBox.setText("手机防盗未经开启");
			checkBox.setChecked(false);
		}
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if(b){
					checkBox.setText("手机防盗已开启");
				}else{
					checkBox.setText("手机防盗未开启");
				}
				editor.putBoolean("proteting",b);
				editor.commit();
			}
		});
	}
	@Override
	public void next(View view) {
		super.next(view);
		showNext();
	}
	@Override
	protected void showNext() {
		editor.putBoolean("configed",true);
		editor.commit();
		Intent intent = new Intent(this,LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
	}
	@Override
	protected void showPre() {
		pre4_3();
	}
	public void pre4_3(){
		Intent intent = new Intent(this,Step3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out);
	}
}

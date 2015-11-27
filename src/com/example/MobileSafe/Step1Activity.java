package com.example.MobileSafe;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

/**
 * Created by Administrator on 2015/11/26.
 */
public class Step1Activity extends BaseSetupActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step1);
	}

	/**
	 * 下一步的点击事件
	 */
	public void next(View view){
		showNext();
	}
	protected void showNext(){
		Intent intent = new Intent(Step1Activity.this,Step2Activity.class);
		startActivity(intent);
		finish();
		//该方法只能在finsh() 或者startActivity() 方法后面调用
		overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
	}
	@Override
	protected void showPre() {

	}

}

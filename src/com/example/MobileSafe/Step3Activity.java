package com.example.MobileSafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/11/26.
 */
public class Step3Activity extends BaseSetupActivity {
	private EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step3);
		editText = (EditText) findViewById(R.id.inputphonenumber);
		String phone = sp.getString("phone",null);
		if(!TextUtils.isEmpty(phone)){
			editText.setText(phone);
		}
	}

	@Override
	protected void showNext() {
		if(TextUtils.isEmpty(editText.getText())){
			Toast.makeText(getApplicationContext(),"请先绑定安全手机号码",Toast.LENGTH_LONG).show();
			return;
		}
		editor.putString("phone", String.valueOf(editText.getText()));
		editor.commit();
		Intent intent = new Intent(Step3Activity.this, Step4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	protected void showPre() {
		Intent intent = new Intent(this, Step2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	public void selectContant(View view){
		Intent intent = new Intent(this,SelectContantActivity.class);
		startActivityForResult(intent,0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data==null){
			return;
		}
		if(resultCode==0){
			String phone = data.getStringExtra("phone");
			editText.setText(phone);
		}
	}
}


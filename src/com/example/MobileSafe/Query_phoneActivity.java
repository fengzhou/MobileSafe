package com.example.MobileSafe;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.utils.SQLDBTools;

/**
 * Created by Administrator on 2015/12/2.
 */
public class Query_phoneActivity extends Activity{
	private Button queryphone;
	private EditText phoneNumber;
	private TextView tv_show_phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_queryphone);
		queryphone = (Button) findViewById(R.id.queryphone);
		tv_show_phone = (TextView) findViewById(R.id.showphone);
		phoneNumber = (EditText) findViewById(R.id.input_query_phone);
		phoneNumber.addTextChangedListener(new TextWatcher() {
			/*
			在文本变化前回调
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			/*
			文本变化时回调
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s!=null && s.length()>=3){
					//查询数据库，并且显示结果
					String address = SQLDBTools.queryPhoneNumber(s.toString());
					tv_show_phone.setText(address);
				}
			}
			/*
			文本变化后回调
			 */
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		queryphone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = phoneNumber.getText().toString().trim();
				if (TextUtils.isEmpty(phone)) {
					Toast.makeText(getApplicationContext(), "查询号码为空", Toast.LENGTH_SHORT).show();
					return;
				} else {
					//连接数据库查询
					String address = SQLDBTools.queryPhoneNumber(phone);
					tv_show_phone.setText(address);
				}
			}
		});
	}
}

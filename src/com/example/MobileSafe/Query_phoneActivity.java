package com.example.MobileSafe;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
		queryphone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = phoneNumber.getText().toString().trim();
				if(TextUtils.isEmpty(phone)){
					Toast.makeText(getApplicationContext(),"查询号码为空",Toast.LENGTH_SHORT).show();
					return;
				}else {
					//连接数据库查询
					String address = SQLDBTools.queryPhoneNumber(phone);
					tv_show_phone.setText(address);
				}
			}
		});
	}
}

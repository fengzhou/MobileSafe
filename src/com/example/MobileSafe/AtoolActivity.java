package com.example.MobileSafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.utils.SMSUtils;

/**
 * Created by Administrator on 2015/12/2.
 */
public class AtoolActivity extends Activity{
	private TextView click_button;
	private TextView smsbackup;
	private TextView smsrestore;
	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		click_button = (TextView) findViewById(R.id.checkphone);
		smsbackup = (TextView) findViewById(R.id.smsbackup);
		smsrestore = (TextView) findViewById(R.id.smsrestore);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("正在备份短信");
		/**
		 * 号码归属地查询
		 */
		click_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplication(),Query_phoneActivity.class);
				startActivity(intent);
			}
		});
		/**
		 * 短信备份
		 */
		smsbackup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pd.show();
				new Thread(){
					@Override
					public void run() {
						super.run();
						try {
							SMSUtils.smsbackup(getApplicationContext(),pd);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(),"短信备份成功",Toast.LENGTH_SHORT).show();
								}
							});
						} catch (Exception e) {
							android.util.Log.i("iii::",e.getMessage());
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(),"短信备份失败",Toast.LENGTH_SHORT).show();
								}
							});
						}finally {
							pd.dismiss();
						}
					}
				}.start();
			}
		});
		/**
		 * 短信恢复
		 */
		smsrestore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}
}

package com.example.MobileSafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
		pd.setMessage("正在备份...");
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
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
							SMSUtils.smsbackup(getApplicationContext(), new SMSUtils.SmsCallBack() {
								@Override
								public void beforebackup(int max) {
									pd.setMax(max);
								}

								@Override
								public void progessbackup(int progess) {
									pd.setProgress(progess);
								}
							});
							runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(),"短信备份成功",Toast.LENGTH_SHORT).show();
							}
						}); } catch (Exception e) {
							android.util.Log.i("iii::",e.getMessage());
							e.printStackTrace();
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
				AlertDialog.Builder builder = new AlertDialog.Builder(AtoolActivity.this);
				builder.setTitle("警告！").setMessage("是否清空所有短信?");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							SMSUtils.smsrestore(AtoolActivity.this, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							SMSUtils.smsrestore(AtoolActivity.this,false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				builder.create().show();

			}
		});
	}
}

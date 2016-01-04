package com.example.MobileSafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.utils.MD5Utils;

/**
 * Created by Administrator on 2015/11/24.
 */
public class HomeActivity extends Activity {

	private static final String TAG = "HomeActivity" ;
	private GridView list_home;
	private MyAdapter myAdapter;
	private SharedPreferences sp;

	private static String[] itemname = {"手机防盗","通讯卫士","软件管理",
			"进程管理","流量统计","手机杀毒","缓存工具","高级工具","设置中心",};
	private static int[] images = {
			R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
			R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
			R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		list_home = (GridView) findViewById(R.id.list_home);
		myAdapter = new MyAdapter();
		list_home.setAdapter(myAdapter);
		sp = getSharedPreferences("config",MODE_PRIVATE);

		list_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			Intent intent = null;
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				switch (i) {
					case 0://进入手机防盗
						showLostDialog();
						break;
					case 1:
						intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
						startActivity(intent);
						break;
					case 2:
						intent = new Intent(HomeActivity.this,AppManagerActivity.class);
						startActivity(intent);
						break;
					case 3:
						intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
						startActivity(intent);
						break;
					case 7: //进入高级应用
						intent = new Intent(HomeActivity.this, AtoolActivity.class);
						startActivity(intent);
						break;
					case 8://进入设置
						intent = new Intent(HomeActivity.this, SettingActivity.class);
						startActivity(intent);
						break;
					default:
						break;
				}
			}
		});
	}



	private void showLostDialog() {
		if (isSetPwd()){
			//设置了密码，弹出输入对话框
			showEnterDialog();
		}else{
			//没有设置密码,弹出设置密码输入框
			showSetupPwdDialog();
		}
	}


	private EditText enterPwd;

	/**
	 * 输入密码对话框
	 */
	private void showEnterDialog() {
		View view = View.inflate(HomeActivity.this,R.layout.dialog_enter_pwd,null);
		enterPwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
			}
		});
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//取出输入的密码
				String password = enterPwd.getText().toString().trim();
				//从sp文件中获取保存的密码
				String confimpwd = sp.getString("password","");
				if(TextUtils.isEmpty(password)){
					Toast.makeText(HomeActivity.this,"密码为空",Toast.LENGTH_SHORT).show();
					return;
				}
				if(MD5Utils.getPwdMd5(password).equals(confimpwd)){
					alertDialog.dismiss();
					Intent it = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(it);
					finish();
				}else{
					Toast.makeText(HomeActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
					enterPwd.setText("");
					return;
				}

			}
		});
		AlertDialog.Builder buider = new AlertDialog.Builder(HomeActivity.this);
		buider.setView(view);
		alertDialog = buider.show();
	}

	private EditText et_setup_pwd;
	private EditText et_setup_confim;
	private Button ok;
	private Button cancel;
	private AlertDialog alertDialog;

	/**
	 * 设置密码对话框
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this,R.layout.dialog_setup_pwd,null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confim = (EditText) view.findViewById(R.id.et_setup_confim);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//把对话框取消掉
				alertDialog.dismiss();
			}
		});
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String confimpwd = et_setup_confim.getText().toString().trim();
				if(TextUtils.isEmpty(password) || TextUtils.isEmpty(confimpwd)){
					Toast.makeText(HomeActivity.this,"密码或确认密码不能为空",Toast.LENGTH_SHORT).show();
					return;
				}
				//判断是否一致才去保存
				if(password.equals(confimpwd)){
					//一直的话，保存密码。取消对话框。进入防盗页面
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("password", MD5Utils.getPwdMd5(password));
					editor.commit();
					alertDialog.dismiss();
					Log.i(TAG,"//一直的话，保存密码。取消对话框。进入防盗页面");
				}else {
					Toast.makeText(HomeActivity.this,"密码不一致",Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		builder.setView(view);
		alertDialog = builder.show();
	}
	/**
	 * 判断是否设置密码
	 */
	private boolean isSetPwd(){
		String password = sp.getString("password",null);
		return !TextUtils.isEmpty(password);
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return itemname.length;
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			View list_view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) list_view.findViewById(R.id.iv_itme);
			TextView tv_item = (TextView) list_view.findViewById(R.id.tv_item);
			tv_item.setText(itemname[i]);
			iv_item.setImageResource(images[i]);
			return list_view;
		}
	}
}

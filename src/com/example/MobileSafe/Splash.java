package com.example.MobileSafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.example.utils.StreamTools;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 欢迎界面
 */
public class Splash extends Activity {

	protected static final String TAG = "Splash";
	private static final int ENTER_HOME = 0;
	private static final int SHOW_UPDATE_DIALOG = 1;
	private static final int URL_ERROR = 2;
	private static final int NETWORK_ERROR = 3;
	private static final int JSON_ERROR = 4;
	private final String  apkurl  = "http://192.168.40.99:8080/zgzycs_debug.apk";


	private TextView tv_splash_version;
	private String msg;
	private TextView tv_update_info;
	private SharedPreferences sp;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本号:"+getVersionName());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		boolean update = sp.getBoolean("update",false);
		//拷贝数据库
		copyDB();
		if(update){
			//检查升级
			CheckUpdate();
		}else{
			//关闭自动升级,延时2s进入主界面
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		AlphaAnimation aa = new AlphaAnimation(0.2f,1.0f);
		aa.setDuration(500);
		findViewById(R.id.rl_root_splash).startAnimation(aa);
	}

	private void copyDB() {
		File file = new File(getFilesDir(), "address.db");
		Log.i("filedir is : ",getFilesDir().getPath());
		try {
			if (file.exists() && file.length() > 0) {
				Log.i("db exists?","true");
			} else {
				InputStream is = getAssets().open("address.db");
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case SHOW_UPDATE_DIALOG:
					Log.i(TAG,"显示升级的对话框");
					showUpdateDialog();
					break;
				case ENTER_HOME:
					Log.i(TAG,"无需升级,直接进入主界面");
					enterHome();
					break;
				case URL_ERROR:
					enterHome();
					Toast.makeText(Splash.this,"URL 错误",Toast.LENGTH_LONG).show();
					break;
				case NETWORK_ERROR:
					enterHome();
					Toast.makeText(Splash.this,"网络异常",Toast.LENGTH_LONG).show();
					break;
				case JSON_ERROR:
					enterHome();
					Toast.makeText(Splash.this,"JSON 解析错误",Toast.LENGTH_LONG).show();
					break;
				default:
					break;

			}
		}
	};

	/**
	 * 弹出升级对话框
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
		builder.setTitle("提示升级");
//		builder.setCancelable(false); //一般用于强制升级
		builder.setMessage(msg);
		//监听返回按键
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				enterHome();
				dialogInterface.dismiss();
			}
		});
		builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, final int i) {
				//下载apk，并替换安装
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					//sdcard 存在
					FinalHttp finalHttp = new FinalHttp();
					Log.i(TAG,"url:"+apkurl);
					finalHttp.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe2.0.apk", new AjaxCallBack<File>() {
						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							t.printStackTrace();
							super.onFailure(t, errorNo, strMsg);
							Toast.makeText(getApplicationContext(),"下载失败",Toast.LENGTH_LONG).show();
							enterHome();
						}
						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							//显示下载进度的文本,该文本默认是不显示
							tv_update_info.setVisibility(View.VISIBLE);
							//当前下载的百分比 count 总大小，current 当前现在的大小
							int progress = (int) (current*100 / count);
							tv_update_info.setText("下载进度: "+progress+"%");
						}
						@Override
						public void onSuccess(File file) {
							super.onSuccess(file);
							installApk(file);
						}
						/**
						 * 安装apk(使用系统提供的，这里我们只需要发送意图即可)
						 * @param file
						 */
						private void installApk(File file) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
							startActivity(intent);
						}
					});
				}else{
					Toast.makeText(getApplicationContext(),"没有sdcard，请安装上在试",Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				enterHome();
			}
		});
		builder.show();
	}

	private void enterHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);

		//关闭启动界面
		this.finish();
	}


	/**
	 * 检查是否有新版本，如果有就升级
	 */
	private void CheckUpdate() {
		new Thread(){
			Message message = Message.obtain();
			long startTime = System.currentTimeMillis();
			@Override
			public void run() {
				super.run();
				//URL
				try {
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(4000);
					int code = connection.getResponseCode();
					if(code==200){
						//联网成功
						InputStream inputStream = connection.getInputStream();
						String result = StreamTools.readFromStream(inputStream);
						//json 解析
						JSONObject object = new JSONObject(result);
						String version = (String) object.get("version");
						 msg = (String) object.get("msg");
//						 apkurl = (String) object.get("url");
						//校验版本信息
						if(getVersionName().equals(version)){
							//没有新版本
							Log.i(TAG,"version:"+getVersionName()+" srv version : "+version);
							message.what=ENTER_HOME;
						}else{
							//有新版本,弹出一个升级对话框
							message.what=SHOW_UPDATE_DIALOG;
						}
					}else{
						message.what=NETWORK_ERROR;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					message.what=URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					message.what=NETWORK_ERROR;
				} catch (JSONException e) {
					message.what=JSON_ERROR;
					e.printStackTrace();
				}finally {
					long endTime = System.currentTimeMillis();
					//用了多长时间
					long dtime = endTime-startTime;
					//停留2s
					if(dtime<2000){
						try {
							Thread.sleep(2000-1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(message);
				}
			}
		}.start();
	}

	/**
	 * 得到应用程序的版本名称
	 */
	private String getVersionName(){
		//PackageManager 用来管理手机的APK
		PackageManager packageManager = getPackageManager();
		//得到指定apk的功能清单文件
		try {
			PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

}

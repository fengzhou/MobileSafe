package com.example.MobileSafe;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.utils.AppInfo;
import com.example.utils.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24.
 */
public class AppManagerActivity extends Activity{

	private TextView tv_sd_size;
	private TextView tv_rom_size;

	private ListView lv_app_list;
	private LinearLayout lv_app_loading;
	private List<AppInfo> lists;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> sysAppInfos;

	private TextView tv_status;

	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanager);
		tv_rom_size = (TextView) findViewById(R.id.tx_rom_size);
		tv_sd_size = (TextView) findViewById(R.id.tx_sd_size);
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		lv_app_loading = (LinearLayout) findViewById(R.id.lv_app_loading);
		tv_status = (TextView) findViewById(R.id.tv_status);
		long rom_path = getSize(Environment.getDataDirectory().getAbsolutePath());
		long sd_path = getSize(Environment.getExternalStorageDirectory().getAbsolutePath());
		tv_rom_size.setText("内存可用大小:"+ Formatter.formatFileSize(this,rom_path));
		tv_sd_size.setText("SD可用大小:"+ Formatter.formatFileSize(this,sd_path));

		userAppInfos = new ArrayList<AppInfo>();
		sysAppInfos = new ArrayList<AppInfo>();

		lv_app_loading.setVisibility(View.VISIBLE);
		new Thread(){
			@Override
			public void run() {
				lists = AppInfoProvider.getAllAppInfos(getApplicationContext());
				for (AppInfo appInfo : lists){
					if(appInfo.isUserApp()){
						userAppInfos.add(appInfo);
					}else{
						sysAppInfos.add(appInfo);
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						lv_app_list.setAdapter(new MyAdapter());
						lv_app_loading.setVisibility(View.INVISIBLE);
					}
				});
			}
		}.start();
		//给listview添加滚动事件
		lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			/**
			 *
			 * @param view
			 * @param firstVisibleItem 第一个可见条目在listview集合中的位置
			 * @param visibleItemCount
			 * @param totalItemCount
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				dismissPopUpWindow();
				if(userAppInfos != null && sysAppInfos != null){
					if(firstVisibleItem<=userAppInfos.size()){
						tv_status.setText("用户程序: "+userAppInfos.size()+" 个");
					}else{
						tv_status.setText("系统程序: "+sysAppInfos.size()+" 个");
					}
				}
			}
		});
		lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AppInfo appInfo;
				if(position == 0){
					return;
				}else if(position == userAppInfos.size()+1){
					return;
				}else if(position<userAppInfos.size()){
					//用户程序
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				}else{
					//系统程序
					int newposition = position-1-1-userAppInfos.size();
					appInfo = userAppInfos.get(newposition);
				}
				dismissPopUpWindow();
				TextView tv = new TextView(getApplicationContext());
				tv.setText(appInfo.getPackagename());
				tv.setTextColor(Color.WHITE);
				popupWindow = new PopupWindow(tv,-2,-2);
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.RED));
				int[] locations = new int[2];
				view.getLocationInWindow(locations);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,locations[0],locations[1]);

			}
		});

	}

	private void dismissPopUpWindow(){
		if(popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private static class ViewHolder{
		private TextView tv_app_name;
		private TextView tv_app_location;
		private ImageView tv_app_icon;
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			AppInfo appInfo;
			if(position == 0){
				//显示用户程序的文本
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户程序: "+userAppInfos.size()+" 个");
				return tv;
			}else if(position == (userAppInfos.size()+1)){
				//显示系统程序的文本
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统程序: "+sysAppInfos.size()+" 个");
				return tv;
			}else if(position<= userAppInfos.size()){
				//显示用户程序列表
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			}else{
				//显示系统程序列表
				int newposition = position-userAppInfos.size() - 1-1;
				appInfo = sysAppInfos.get(newposition);
			}

			View view;
			ViewHolder holder;
			if(convertView!=null && convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(),R.layout.list_item_appinfo,null);
				holder = new ViewHolder();
				holder.tv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_app_location = (TextView) view.findViewById(R.id.tv_app_location);
				view.setTag(holder);
			}
			holder.tv_app_name.setText(appInfo.getName());
			holder.tv_app_icon.setImageDrawable(appInfo.getIcon());
			if(appInfo.isRom()){
				holder.tv_app_location.setText("手机内存");
			}else{
				holder.tv_app_location.setText("外部存储器");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}


	}

	@Override
	protected void onDestroy() {
		dismissPopUpWindow();
		super.onDestroy();
	}

	/**
	 * 获取存储空间
	 * @param path
	 * @return
	 */
	private long getSize(String path){
		StatFs statFs = new StatFs(path);
//		statFs.getBlockCountLong() //获取分区的个数
		long size = statFs.getBlockSizeLong();//获取分区的大小
		long count = statFs.getAvailableBlocksLong();//获取可用的区块的个数
		return size*count;
	}

}

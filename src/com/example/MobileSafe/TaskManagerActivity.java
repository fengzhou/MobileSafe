package com.example.MobileSafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.utils.AppMemrotUtils;
import com.example.utils.TaskInfo;
import com.example.utils.TaskInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
public class TaskManagerActivity extends Activity{
	private TextView tv_task_memory;
	private TextView tv_task_process;
	private LinearLayout ll_loading;
	private LinearLayout ll_menu;

	private List<TaskInfo> allTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> sysTaskInfos;

	private TaskInfo taskInfo;
	private ListView listView;
	private SharedPreferences sp;
	private TextView tv_task_staus;

	private TaskInfoAdapter adapter;
	private int process;
	private long avalmem;
	private long totlmem;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager);
		tv_task_memory = (TextView) findViewById(R.id.tv_task_memory);
		tv_task_process = (TextView) findViewById(R.id.tv_task_process);
		ll_loading = (LinearLayout) findViewById(R.id.ll_task_loading);
		ll_menu = (LinearLayout) findViewById(R.id.ll_task_memu);
		listView = (ListView) findViewById(R.id.lv_task_info);
		tv_task_staus = (TextView) findViewById(R.id.tv_task_status);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		ll_loading.setVisibility(View.VISIBLE);
		fillData();
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(userTaskInfos!=null && sysTaskInfos!=null){
					if(firstVisibleItem<=userTaskInfos.size()){
						tv_task_staus.setText("用户进程数:"+userTaskInfos.size()+"个");
					}else{
						tv_task_staus.setText("系统进程数:"+sysTaskInfos.size()+"个");
					}
				}
			}
		});

		/**
		 * listview 条目点击事件
		 */
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position!=0 && position!=(userTaskInfos.size()+1)){
					ViewHolder holder = (ViewHolder) view.getTag();
					//用户进程
					if(position<=userTaskInfos.size()){
						int newposition = position-1;
						taskInfo = userTaskInfos.get(newposition);
					}else{
						//系统进程
						int newposition = position - userTaskInfos.size()-1-1;
						taskInfo = sysTaskInfos.get(newposition);
					}
					//防止自己本选中
					if(getPackageName().equals(taskInfo.getPackagename())){
						return;
					}
					if(taskInfo.ischecked()){
						//设置checkbox未被选中
						holder.cb_task_staus.setChecked(false);
						taskInfo.setIschecked(false);
					}else{
						//选中checkbox
						holder.cb_task_staus.setChecked(true);
						taskInfo.setIschecked(true);
					}
				}
			}
		});
	}

	private void setTitle() {
		totlmem = AppMemrotUtils.getTotalMem2();
		avalmem = AppMemrotUtils.getAvailMem(this);
		process = AppMemrotUtils.getRunningProcess(this);
		process = allTaskInfos.size();
		tv_task_process.setText("正在运行的进程数: "+process);
		tv_task_memory.setText("剩余/总内存: "+ Formatter.formatFileSize(this, avalmem)+" / "+
				Formatter.formatFileSize(this,totlmem));
	}

	/**
	 * 填充数据
	 */
	private void fillData() {
		new Thread(){
			@Override
			public void run() {
				super.run();
				allTaskInfos = TaskInfoProvider.getRunningTaskMem(getApplicationContext());
				userTaskInfos = new ArrayList<TaskInfo>();
				sysTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : allTaskInfos){
					if(taskInfo.isUserTask()){
						userTaskInfos.add(taskInfo);
					}else{
						sysTaskInfos.add(taskInfo);
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(adapter == null) {
							adapter = new TaskInfoAdapter();
							listView.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						ll_menu.setVisibility(View.VISIBLE);
						setTitle();
					}
				});
			}
		}.start();
	}

	private class TaskInfoAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			boolean flag = sp.getBoolean("showsys",true);
			if(flag){
				return allTaskInfos.size()+2;
			}else{
				return userTaskInfos.size()+1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if(position==0){
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setText("用户进程数: "+userTaskInfos.size());
				return textView;
			}else if(position == (userTaskInfos.size()+1)){
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setText("系统进程数: "+sysTaskInfos.size());
				return textView;
			}else if(position<=userTaskInfos.size()){
				//显示用户进程
				int newposition = position-1;
				taskInfo = userTaskInfos.get(newposition);
			}else {
				//显示系统进程
				int newposition = position-userTaskInfos.size()-2;
				taskInfo = sysTaskInfos.get(newposition);
			}
			View view;
			ViewHolder holder;
			if(convertView!=null && convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(),R.layout.list_item_taskinfo,null);
				holder.iv_task_icon = (ImageView) view.findViewById(R.id.iv_taskinfo_icon);
				holder.tv_task_name = (TextView) view.findViewById(R.id.tv_taskinfo_name);
				holder.tv_task_memsize = (TextView) view.findViewById(R.id.tv_taskinfo_memsize);
				holder.cb_task_staus = (CheckBox) view.findViewById(R.id.cb_status);
				view.setTag(holder);
			}
			holder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
			holder.cb_task_staus.setChecked(taskInfo.ischecked());
			holder.tv_task_name.setText(taskInfo.getName());
			holder.tv_task_memsize.setText(Formatter.formatFileSize(getApplicationContext(),taskInfo.getMemsize()));
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

	static class ViewHolder{
		ImageView iv_task_icon;
		TextView tv_task_name;
		TextView tv_task_memsize;
		CheckBox cb_task_staus;
	}

	/**
	 * 勾选所有进程
	 * @param view
	 */
	public void selectAll(View view){
		for (TaskInfo taskInfo : allTaskInfos){
			if(!getPackageName().equals(taskInfo.getPackagename())){
				taskInfo.setIschecked(true);
			}
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 取消全选
	 * @param view
	 */
	public void disselectAll(View view){
		for (TaskInfo info : allTaskInfos) {
			if (getPackageName().equals(info.getPackagename())) {
				continue;
			}
			info.setIschecked(!info.ischecked());
		}
		adapter.notifyDataSetChanged();
	}

		/**
		 * 清理选中的进程
		 * @param view
		 */
		public void clearSelectTask(View view){
			ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
			int count = 0;
			long saveMem = 0;
			List<TaskInfo> removeList = new ArrayList<TaskInfo>();
			for (TaskInfo taskInfo : allTaskInfos){
				if(taskInfo.ischecked()){
					am.killBackgroundProcesses(taskInfo.getPackagename());
					if(taskInfo.isUserTask()){
						//如果是用户进程
						userTaskInfos.remove(taskInfo);
					}else{
						//系统进程
						sysTaskInfos.remove(taskInfo);
					}
					count++;
					saveMem += taskInfo.getMemsize();
					removeList.add(taskInfo);
			}
		}
		allTaskInfos.removeAll(removeList);
		adapter.notifyDataSetChanged();
		Toast.makeText(this,"本次共清理了: "+count+"个进程,释放了: "+Formatter.formatFileSize(this,saveMem),Toast.LENGTH_SHORT).show();
		process -= count;
		avalmem +=saveMem;
		tv_task_process.setText("正在运行的进程数: "+process);
		tv_task_memory.setText("剩余/总内存: "+ Formatter.formatFileSize(this, avalmem)+" / "+
				Formatter.formatFileSize(this,totlmem));
	}

	/**
	 * 进入设置界面
	 * @param view
	 */
	public void startSetting(View view){
		Intent intent = new Intent(this,TaskSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("request","10086-99");
		intent.putExtras(bundle);
		startActivityForResult(intent, 99);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 99){
			adapter.notifyDataSetChanged();
		}
	}
}

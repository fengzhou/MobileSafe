package com.example.MobileSafe;

import android.app.Activity;
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

	private List<TaskInfo> allTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> sysTaskInfos;

	private TaskInfo taskInfo;
	private ListView listView;

	private TaskInfoAdapter adapter;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager);
		tv_task_memory = (TextView) findViewById(R.id.tv_task_memory);
		tv_task_process = (TextView) findViewById(R.id.tv_task_process);
		ll_loading = (LinearLayout) findViewById(R.id.ll_task_loading);
		listView = (ListView) findViewById(R.id.lv_task_info);

		userTaskInfos = new ArrayList<TaskInfo>();
		sysTaskInfos = new ArrayList<TaskInfo>();

		long totlmem = AppMemrotUtils.getTotalMem2();
		long avalmem = AppMemrotUtils.getAvailMem(this);
		int process = AppMemrotUtils.getRunningProcess(this);

		ll_loading.setVisibility(View.VISIBLE);

		fillData();

		tv_task_process.setText("正在运行的进程数: "+process);
		tv_task_memory.setText("剩余/总内存: "+ Formatter.formatFileSize(this,avalmem)+" / "+
				Formatter.formatFileSize(this,totlmem));

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});
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
						adapter = new TaskInfoAdapter();
						listView.setAdapter(adapter);
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			}
		}.start();
	}

	private class TaskInfoAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return allTaskInfos.size()+2;
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
				view.setTag(holder);
			}
			holder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
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
	}

}

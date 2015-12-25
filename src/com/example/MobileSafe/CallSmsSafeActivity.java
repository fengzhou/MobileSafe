package com.example.MobileSafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.db.BlackNumberDao;
import com.example.db.BlackNumberInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/12/18.
 */
public class CallSmsSafeActivity extends Activity{

	private static final String TAG = "info size";
	private ListView listView;
	private List<BlackNumberInfo> infoList;
	private BlackNumberDao dao;

	private Button addcallsms;
	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;
	private LinearLayout ll_loading;
	private callsmsAdapter adapter;
	private int offset = 0;
	private int pagemax = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsmssafe);

		listView = (ListView) findViewById(R.id.lv_call_sms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		dao = new BlackNumberDao(this);
		addcallsms = (Button) findViewById(R.id.lv_addcallsms);
		filldata();
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState){
					case SCROLL_STATE_FLING:
						System.out.println("惯性滑动..........");
						break;
					case SCROLL_STATE_IDLE:
						System.out.println("空闲状态..........");
						//获取最后一个可见条目在集合中的位置
						int lastposition = listView.getLastVisiblePosition();
						if(lastposition == infoList.size()-1){
							offset+=pagemax;
							filldata();
						}
						break;
					case SCROLL_STATE_TOUCH_SCROLL:
						System.out.println("触摸滑动..........");
						break;
					default:
						break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		addcallsms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder buider = new AlertDialog.Builder(CallSmsSafeActivity.this);
				final AlertDialog dialog = buider.create();
				View contentView = View.inflate(getApplicationContext(), R.layout.dialog_blacknumber, null);
				et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
				cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
				cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
				bt_cancel = (Button) contentView.findViewById(R.id.cancel);
				bt_ok = (Button) contentView.findViewById(R.id.ok);
				dialog.setView(contentView,0,0,0,0);
				dialog.show();

				bt_cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				bt_ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String blacknumber = et_blacknumber.getText().toString();
						if(TextUtils.isEmpty(blacknumber)){
							Toast.makeText(getApplicationContext(),"黑名单号码不能为空！！",Toast.LENGTH_SHORT).show();
							return;
						}
						String mode;
						if(cb_phone.isChecked() && cb_sms.isChecked()){
							mode = "3";
						}else if(cb_phone.isChecked()){
							mode = "1";
						}else if (cb_sms.isChecked()){
							mode = "2";
						}else{
							Toast.makeText(getApplicationContext(),"请选择拦截模式",Toast.LENGTH_SHORT).show();
							return;
						}
						//保存黑名单
						dao.add(blacknumber,mode);
						//更新listview集合
						BlackNumberInfo info = new BlackNumberInfo();
						info.setMode(mode);
						info.setNumber(blacknumber);
						infoList.add(0,info);
						//通知适配器数据更新了
						adapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});
			}
		});
	}

	private void filldata(){
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			@Override
			public void run() {
				super.run();
				if(infoList == null){
					infoList = dao.findall(offset,pagemax);
				}else{
					infoList.addAll(dao.findall(offset,pagemax));
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(adapter==null) {
							adapter = new callsmsAdapter();
							listView.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			}
		}.start();

	}

	private class callsmsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return infoList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			VoiltHoder hoder;
			//做内存优化
			if(convertView == null){
				view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
				hoder = new VoiltHoder();
				hoder.tv_number = (TextView) view.findViewById(R.id.lv_callnumber);
				hoder.tv_mode = (TextView) view.findViewById(R.id.lv_callmode);
				hoder.tv_image = (ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(hoder);
			}else{
				view = convertView;
				hoder = (VoiltHoder) view.getTag();
			}
			hoder.tv_image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder buider = new AlertDialog.Builder(CallSmsSafeActivity.this);
					buider.setTitle("警告");
					buider.setMessage("确定删除这条信息吗？");
					buider.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.del(infoList.get(position).getNumber());
							infoList.remove(position);
							adapter.notifyDataSetChanged();
						}
					});
					buider.setNegativeButton("取消",null);
					buider.create().show();
				}
			});
			hoder.tv_number.setText(infoList.get(position).getNumber());
			String mode = infoList.get(position).getMode();
			if("1".equals(mode)){
				hoder.tv_mode.setText("电话拦截");
			}else if("2".equals(mode)){
				hoder.tv_mode.setText("短信拦截");
			}else{
				hoder.tv_mode.setText("全部拦截");
			}
			return view;
		}
	}

	static class VoiltHoder{
		private TextView tv_number;
		private TextView tv_mode;
		private ImageView tv_image;
	}

}

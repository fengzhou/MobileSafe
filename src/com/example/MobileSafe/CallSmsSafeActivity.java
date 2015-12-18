package com.example.MobileSafe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsmssafe);
		listView = (ListView) findViewById(R.id.lv_call_sms_safe);
		dao = new BlackNumberDao(this);
		infoList = dao.findall();
		Log.i(TAG,"info size : "+infoList.size());
		listView.setAdapter(new callsmsAdapter());
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
			TextView tv_number = (TextView) view.findViewById(R.id.lv_callnumber);
			TextView tv_mode = (TextView) view.findViewById(R.id.lv_callmode);
			tv_number.setText(infoList.get(position).getNumber());
			String mode = infoList.get(position).getMode();
			if("1".equals(mode)){
				tv_mode.setText("电话拦截");
			}else if("2".equals(mode)){
				tv_mode.setText("短信拦截");
			}else{
				tv_mode.setText("全部拦截");
			}
			return view;
		}
	}

}

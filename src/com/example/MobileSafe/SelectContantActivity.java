package com.example.MobileSafe;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加联系人activity
 */
public class SelectContantActivity extends Activity {

	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectcontant);
		listView = (ListView) findViewById(R.id.list_select_contact);
		final List<Map<String,String>> data = getData();
		listView.setAdapter(new SimpleAdapter(getApplicationContext(),data,R.layout.contact_view,
				new String[]{"name","phone"},new int[]{R.id.tv_name,R.id.tv_phone}));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String phone = data.get(i).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone",phone);
				setResult(0,intent);
				finish();
			}
		});
	}
	private List<Map<String,String>> getData(){
		List<Map<String,String>> list = new ArrayList<Map<String, String>>();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri,new String[]{"contact_id"},null,null,null);
		while (cursor.moveToNext()){
			String id = cursor.getString(0);
			Cursor dataCursor = resolver.query(datauri,new String[]{"data1","mimetype"},"contact_id=?",new String[]{id},null);
			while (dataCursor.moveToNext()){
				Map<String,String> map = new HashMap<String, String>();
				String data1 = dataCursor.getString(0);
				String mimetype = dataCursor.getString(1);
				if(mimetype.equals("vnd.android.cursor.item/name")){
					//data1 为联系人姓名
					map.put("name",data1);
				}
				if(mimetype.equals("vnd.android.cursor.item/phone_v2")){
					//data1 为联系人电话号码
					map.put("phone",data1);
				}
				list.add(map);
			}
		}
		return list;
	}

}

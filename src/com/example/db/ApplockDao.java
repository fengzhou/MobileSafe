package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/6.
 */
public class ApplockDao {
	private ApplockDBOpenHelper helper;
	private Context context;

	public ApplockDao(Context context){
		helper = new ApplockDBOpenHelper(context);
		this.context = context;
	}

	/**
	 * 添加一个被锁的程序
	 * @param packagename
	 */
	public void add(String packagename){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename",packagename);
		db.insert("applocktb",null,values);
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.example.db.changed");
		this.context.sendBroadcast(intent);
	}

	/**
	 * 移除一个被锁的程序
	 * @param packagename
	 */
	public void delete(String packagename){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applocktb","packagename=?",new String[]{packagename});
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.example.db.changed");
		this.context.sendBroadcast(intent);
	}

	/**
	 * 查询程序是否被锁
	 * @param packagename
	 * @return
	 */
	public boolean findpg(String packagename){
		boolean result = false;
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query("applocktb", null, "packagename=?", new String[]{packagename}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 获取全部的包名
	 * @return
	 */
	public List<String> protectPackagename(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applocktb", new String[]{"packagename"}, null, null, null, null, null);
		List<String> lists = new ArrayList<String>();
		while (cursor.moveToNext()){
			String packagename = cursor.getString(0);
			lists.add(packagename);
		}
		cursor.close();
		db.close();
		return lists;
	}

}

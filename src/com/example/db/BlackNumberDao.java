package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/12/18.
 */
public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context){
		helper = new BlackNumberDBOpenHelper(context);
	}


	/**
	 * 查询黑名单是否已经存在了
	 * @param number
	 * @return
	 */
	public boolean find(String number){
		boolean result = false;
		SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select * from blacknumber where number = ?", new String[]{number});
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		sqLiteDatabase.close();
		return result;
	}

	/**
	 * 查询某个号码的黑名单拦截模式
	 * @param number
	 * @return
	 */
	public String findMode(String number){
		String result = null;
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select mode from blacknumber where number = ?", new String[]{number});
		while (cursor.moveToNext()){
			result = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return result;
	}

	/**
	 * 查询所有的黑名单
	 * @param offset : 从哪个位置开始;
	 * @param max: 一次最多获取多少个
	 * @return
	 */
	public List<BlackNumberInfo> findall(int offset,int max){
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<BlackNumberInfo> list = new LinkedList<BlackNumberInfo>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?",
				new String[]{String.valueOf(max),String.valueOf(offset)});
		while (cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			blackNumberInfo.setMode(mode);
			blackNumberInfo.setNumber(number);
			list.add(blackNumberInfo);
		}
		cursor.close();
		database.close();
		return list;
	}

	/**
	 * 添加黑名单
	 * @param number : 黑名单号码
	 * @param mode : 黑名单拦截模式 1.电话拦截 2.短信拦截 3 全部拦截
	 * @return
	 */
	public void add(String number , String mode){
		SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		sqLiteDatabase.insert("blacknumber", null, values);
		sqLiteDatabase.close();
}

	/**
	 * 更改黑名单拦截模式
	 * @param number : 黑名单号码
	 * @param mode : 拦截模式
	 */
	public void update(String number,String mode){
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode",mode);
		database.update("blacknumber",values,"number = ?",new String[]{number});
		database.close();
	}

	/**
	 * 移除黑名单
	 * @param number : 黑名单号码
	 * @return
	 */
	public void del(String number){
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete("blacknumber","number = ?",new String[]{number});
		database.close();
	}
}

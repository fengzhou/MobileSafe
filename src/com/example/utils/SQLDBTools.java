package com.example.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/12/2.
 */
public class SQLDBTools {

	private static String path = "/data/data/com.example.MobileSafe/files/address.db";

	/**
	 * 查询号码归属地
	 * @param phone 需要查询的电话号码
	 * @return 返回归属地
	 */
	public static String queryPhoneNumber(String phone){
		String address  = phone;
		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		if(phone.matches("^1[23458]\\d{9}")) {
			Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{phone.substring(0, 7)});
			while (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		}else{
			switch (phone.length()){
				case 3:
					// 110
					address = "匪警号码";
					break;
				case 4:
					// 5554
					address = "模拟器";
					break;
				case 5:
					// 10086
					address = "客服电话";
					break;
				case 7:
					//
					address = "本地号码";
					break;

				case 8:
					address = "本地号码";
					break;
				default:
					// /处理长途电话 10
					if (phone.length() > 10 && phone.startsWith("0")) {
						// 010-59790386
						Cursor cursor = sqLiteDatabase.rawQuery(
								"select location from data2 where area = ?",
								new String[] { phone.substring(1, 3) });
						while (cursor.moveToNext()) {
							String location = cursor.getString(0);
							address = location.substring(0, location.length() - 2);
						}
						cursor.close();
						// 0855-59790386
						cursor = sqLiteDatabase.rawQuery(
								"select location from data2 where area = ?",
								new String[] { phone.substring(1, 4) });
						while (cursor.moveToNext()) {
							String location = cursor.getString(0);
							address = location.substring(0, location.length() - 2);
						}
					}
					break;
			}
		}
		return address;
	}

}

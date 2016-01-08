package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/1/6.
 */
public class ApplockDBOpenHelper extends SQLiteOpenHelper{

	/**
	 * applock.db 数据库名称
	 * @param context
	 */
	public ApplockDBOpenHelper(Context context) {
		super(context,"applock.db",null,1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applocktb (_id integer primary key autoincrement,packagename varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}

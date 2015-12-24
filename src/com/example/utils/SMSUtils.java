package com.example.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class SMSUtils {

	/**
	 * 短信备份状态显示的回调接口
	 */
	public interface SmsCallBack{
		/**
		 * 短信备份
		 * @param max : 最大值
		 */
		 void beforebackup(int max);

		/**
		 *短信备份进度
		 * @param progess : 备份的进度
		 */
		 void progessbackup(int progess);
	}


	/**
	 * 短信备份
	 * @param context
	 */
	public static void smsbackup(Context context,SmsCallBack callBack) throws Exception {

		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		XmlSerializer xml = Xml.newSerializer();
		xml.setOutput(fileOutputStream,"utf-8");
		xml.startDocument("utf-8",true);
		xml.startTag(null,"smss");
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
		int count = cursor.getCount();
		//给根节点添加一个属性
		xml.attribute(null,"max",count+"");
		callBack.beforebackup(count);
		int temp = 0;
		while (cursor.moveToNext()){
			Thread.sleep(50);
			String body = cursor.getString(0);
			if(body==null || body.length()<=0){
				body = "";
			}
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);

			xml.startTag(null,"sms");

			xml.startTag(null,"body");
			xml.text(body);
			xml.endTag(null,"body");

			xml.startTag(null,"address");
			xml.text(address);
			xml.endTag(null,"address");

			xml.startTag(null,"type");
			xml.text(type);
			xml.endTag(null,"type");

			xml.startTag(null,"date");
			xml.text(date);
			xml.endTag(null,"date");

			xml.endTag(null,"sms");

			temp+=1;
			callBack.progessbackup(temp);
		}
		cursor.close();
		xml.endTag(null,"smss");
		xml.endDocument();
	}

	/**
	 * 短信恢复
	 * @param context
	 */
	public static void smsrestore(Context context,boolean flag) throws Exception {
		Uri uri = Uri.parse("content://sms/");
		//1.根据flag判断是否清空原有短信
		if(flag){
			//需要清空
			context.getContentResolver().delete(uri,null,null);
		}
		ReadXml(context);
		//2获取backup.xml
		//3.读取 backup.xml 中每条短信的 body，address，date，type
		//4.还原
	}

	private static void ReadXml(Context context) throws Exception {
		Uri uri = Uri.parse("content://sms/");
		XmlPullParser pullParser = Xml.newPullParser();
		Log.i("read_path:",Environment.getExternalStorageDirectory()+"");
		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		if(file.exists()){
			Log.i("file : ",file.getAbsolutePath());
		}else{
			Log.i("file :","not exists");
		}
		ContentValues values;
		FileInputStream inputStream = new FileInputStream(file);
		pullParser.setInput(inputStream,"utf-8");
		int event = pullParser.getEventType();
		while (event!=XmlPullParser.END_DOCUMENT){
			values = new ContentValues();
			switch (event){
				case XmlPullParser.START_DOCUMENT:
					event = pullParser.next();
					break;
				case XmlPullParser.START_TAG:
					if("body".equals(pullParser.getName())){
						values.put("body",pullParser.getName());
					}else if ("address".equals(pullParser.getName())){
						values.put("address",pullParser.getName());
					}else if ("date".equals(pullParser.getName())){
						values.put("date",pullParser.getName());
					}else if("type".equals(pullParser.getName())){
						values.put("type",pullParser.getName());
					}
					context.getContentResolver().insert(uri,values);
					event = pullParser.next();
					break;
				default:
					event = pullParser.next();
					break;
			}
		}
		inputStream.close();
	}

}

package com.example.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Time;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;


public class SMSUtils {

	/**
	 * 短信备份
	 * @param context
	 */
	public static void smsbackup(Context context,ProgressDialog pd) throws Exception {

		File file = new File(Environment.getExternalStorageDirectory(),"sms.xml");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		XmlSerializer xml = Xml.newSerializer();
		xml.setOutput(fileOutputStream,"utf-8");
		xml.startDocument("utf-8",true);
		xml.startTag(null,"smss");

		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
		int count = cursor.getCount();
		pd.setMax(count);
		int temp = 0;
		while (cursor.moveToNext()){
			Thread.sleep(50);
			String body = cursor.getString(0);
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
			pd.setProgress(temp);
		}
		cursor.close();
		xml.endTag(null,"smss");
		xml.endDocument();
	}

	/**
	 * 短信恢复
	 * @param context
	 */
	public static void smsrestore(Context context){

	}
}

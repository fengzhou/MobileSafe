package com.example.MobileSafe;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import com.example.service.UpdagewidgetServer;

/**
 * Created by Administrator on 2016/1/6.
 */
public class MyWidget extends AppWidgetProvider{

	/**
	 * 第一次显示在桌面上的时候会调用
	 * @param context
	 */
	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(context, UpdagewidgetServer.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	/**
	 * 最后一次从桌面移除的时候会调用(因为可能桌面有多个相同的widget)
	 * @param context
	 */
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context, UpdagewidgetServer.class);
		context.stopService(intent);
		super.onDisabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Intent intent = new Intent(context,UpdagewidgetServer.class);
		context.startActivity(intent);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
}

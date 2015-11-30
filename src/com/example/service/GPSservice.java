package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by Administrator on 2015/11/30.
 */
public class GPSservice extends Service {
	private LocationManager lm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new MyListener();
		//注册监听位置服务
		//给位置提供者设置条
		Criteria criteria = new Criteria();
		//设置参数细化：
		//criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
		//criteria.setAltitudeRequired(false);//不要求海拔信息
		//criteria.setBearingRequired(false);//不要求方位信息
		//criteria.setCostAllowed(true);//是否允许付费
		//criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria,true);
		lm.requestLocationUpdates(provider,0,0,listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//当服务销毁的时候取消位置服务
		lm.removeUpdates(listener);
		listener = null;
	}

	class MyListener implements LocationListener{

		/**
		 * 当位置发生改变时
		 * @param location
		 */
		@Override
		public void onLocationChanged(Location location) {
			//精度
			String j = "j:"+location.getLongitude()+"\n";
			//纬度
			String w = "w:"+location.getLatitude()+"\n";
			//精确度
			String a = "a:"+location.getAccuracy()+"\n";
			SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("lastlocation",j+w+a);
			editor.commit();
		}

		/**
		 * 当状态发生改变的时候回调 开启--关闭  关闭--开启
		 * @param provider
		 * @param status
		 * @param extras
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		/**
		 * 某一个位置提供者可以使用了
		 * @param provider
		 */
		@Override
		public void onProviderEnabled(String provider) {

		}

		/**
		 * 某一个位置提供者不能使用了
		 * @param provider
		 */
		@Override
		public void onProviderDisabled(String provider) {

		}
	}
}

package com.example.MobileSafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/11/26.
 */
abstract class BaseSetupActivity extends Activity {

	protected SharedPreferences sp;
	protected SharedPreferences.Editor editor;
	//1.声明一个手势识别器
	private GestureDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		editor = sp.edit();
		//2.实例化手势识别器
		detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				//屏蔽斜着滑动
				if(Math.abs(e1.getRawY() - e2.getRawY())>100){
					Toast.makeText(getApplicationContext(),"不能斜着滑动",Toast.LENGTH_SHORT).show();
					return true;
				}
				//屏蔽在X轴滑动很慢的情形
				if(Math.abs(velocityX)<200){
					Toast.makeText(getApplicationContext(),"滑动太慢了",Toast.LENGTH_SHORT).show();
					return true;
				}
				//正常滑动
				if((e1.getRawX()-e2.getRawX())>200){
					//显示下一个界面
					showNext();
					return true;
				}
				if((e2.getRawX()-e1.getRawX())>200){
					//显示上一个界面
					showPre();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	protected abstract void showNext();
	protected abstract void showPre();
	//3.给手势识别器绑定事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	/**
	 * 下一步的点击事件
	 */
	public void next(View view){
		showNext();
	}

}

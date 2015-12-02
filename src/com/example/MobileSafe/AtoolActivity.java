package com.example.MobileSafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/12/2.
 */
public class AtoolActivity extends Activity{
	private TextView click_button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		click_button = (TextView) findViewById(R.id.checkphone);
		click_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplication(),Query_phoneActivity.class);
				startActivity(intent);
			}
		});
	}
}

package com.example.MobileSafe;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.example.ui.SettingItemView;

/**
 * Created by Administrator on 2015/11/26.
 */
public class Step2Activity extends BaseSetupActivity {

	private SettingItemView svi_step_sim;
	private TelephonyManager telephonyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step2);
		svi_step_sim = (SettingItemView) findViewById(R.id.bingsim);
		String sim = sp.getString("sim",null);
		if(TextUtils.isEmpty(sim)){
			//如果为空,没有绑定
			svi_step_sim.setBoxChecked(false);
		}else{
			svi_step_sim.setBoxChecked(true);
		}
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		svi_step_sim.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(svi_step_sim.isChecked()){
					svi_step_sim.setBoxChecked(false);
					editor.putString("sim",null);
				}else{
					svi_step_sim.setBoxChecked(true);
					String sim = telephonyManager.getSimSerialNumber();
					sim = "11223344556677889900";
					editor.putString("sim",sim);
				}
				editor.commit();
			}

		});
	}

	@Override
	protected void showNext() {
		String sim = sp.getString("sim",null);
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(this,"请先绑定sim卡",Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(this,Step3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
	}

	@Override
	protected void showPre() {
		Intent intent = new Intent(this,Step1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out);
	}

}

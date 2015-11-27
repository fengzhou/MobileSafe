package com.example.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.MobileSafe.R;

/**
 * 自定义组合控件
 * 2个 textview 1个 checkbox 1个view
 */
public class SettingItemView extends RelativeLayout {

	private CheckBox cb_stats;
	private TextView tv_desc;
	private TextView tv_title;
	private String title;
	private String desc_on;
	private String desc_off;



	/**
	 * 初始化布局文件
	 * @param context 上下文
	 */
	public void initView(Context context){
		View.inflate(context, R.layout.setting_item_view,this);
		cb_stats = (CheckBox) this.findViewById(R.id.cb_stats);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}


	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		//根据命名空间和参数名称获取对应的值
		title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.MobileSafe","title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.MobileSafe","desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.MobileSafe","desc_off");
		tv_title.setText(title);
		setDesc(desc_off);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 校验组合空间是否有焦点
	 * @return
	 */
	public boolean isChecked(){
		return cb_stats.isChecked();
	}

	/**
	 * 设置组合控件状态
	 * @param checked
	 */
	public void setBoxChecked(boolean checked){
		if(checked){
			tv_desc.setText(desc_on);
		}else{
			tv_desc.setText(desc_off);
		}
		cb_stats.setChecked(checked);
	}

	/**
	 * 设置 组合控件的信息描述
	 * @param text
	 */
	public void setDesc(String text){
		tv_desc.setText(text);
	}
}

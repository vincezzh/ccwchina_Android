/**
 * 
 */
package com.ccwchina.information;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 我的资料Activity
 * @author 飞雪无情
 * @since 2011-3-8
 */
public class CCWInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView=new TextView(this);
		textView.setText("这是我的资料！");
		setContentView(textView);
	}
	
}

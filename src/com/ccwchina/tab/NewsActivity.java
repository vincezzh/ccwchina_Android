/**
 * 
 */
package com.ccwchina.tab;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 信息Activity
 * @author 飞雪无情
 * @since 2011-3-8
 */
public class NewsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView=new TextView(this);
		textView.setText("这是信息！");
		setContentView(textView);
	}
	
}

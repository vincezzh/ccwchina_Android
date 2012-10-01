package com.ccwchina.tab;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.ccwchina.R;
import com.ccwchina.calendar.CCWCalendarActivity;
import com.ccwchina.information.CCWInfoActivity;
import com.ccwchina.map.CCWMapActivity;

public class CCWTabActivity extends TabActivity implements OnCheckedChangeListener{
	public static CCWTabActivity ccwTabActivityActivityInstance;
	
	private RadioGroup mainTab;
	private TabHost mTabHost;
	
	//内容Intent
	private Intent mHomeIntent;
	private Intent mNewsIntent;
	private Intent mInfoIntent;
	
	private final static String TAB_TAG_HOME="tab_tag_home";
	private final static String TAB_TAG_NEWS="tab_tag_news";
	private final static String TAB_TAG_INFO="tab_tag_info";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ccwTabActivityActivityInstance = this;
        setContentView(R.layout.tab);
        mainTab = (RadioGroup)findViewById(R.id.main_tab);
        mainTab.setOnCheckedChangeListener(this);
        prepareIntent();
        setupIntent();
    }
    /**
     * 准备tab的内容Intent
     */
	private void prepareIntent() {
		mHomeIntent=new Intent(this, CCWCalendarActivity.class);
		mInfoIntent=new Intent(this, CCWInfoActivity.class);
		mNewsIntent=new Intent(this, CCWMapActivity.class);
	}
	/**
	 * 
	 */
	private void setupIntent() {
		this.mTabHost=getTabHost();
		TabHost localTabHost=this.mTabHost;
		localTabHost.addTab(buildTabSpec(TAB_TAG_HOME, R.string.main_home, R.drawable.icon_1_n, mHomeIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_INFO, R.string.main_my_info, R.drawable.icon_2_n, mInfoIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_NEWS, R.string.main_news, R.drawable.icon_3_n, mNewsIntent));
	}
	
	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	} 
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
			case R.id.radio_button0:
				this.mTabHost.setCurrentTabByTag(TAB_TAG_HOME);
				break;
			case R.id.radio_button1:
				this.mTabHost.setCurrentTabByTag(TAB_TAG_INFO);
				break;
			case R.id.radio_button2:
				this.mTabHost.setCurrentTabByTag(TAB_TAG_NEWS);
				break;
		}
	}
    
    
}
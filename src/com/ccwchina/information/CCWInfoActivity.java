package com.ccwchina.information;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ccwchina.CCWApplication;
import com.ccwchina.LoginActivity;
import com.ccwchina.R;
import com.ccwchina.bean.User;
import com.ccwchina.common.CCWChinaConst;

public class CCWInfoActivity extends Activity {
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.profile);
		
		refreshActivity();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshActivity();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.reportErr:
	        	sendEmail(CCWChinaConst.REPORT_ERROR_EMAIL, CCWChinaConst.REPORT_ERROR_EMAIL_TITLE, CCWChinaConst.REPORT_ERROR_EMAIL_CONTENT);
	            return true;
	        case R.id.sendEmail:
	        	sendEmail(CCWChinaConst.SEND_EMAIL, CCWChinaConst.SEND_EMAIL_TITLE, CCWChinaConst.SEND_EMAIL_CONTENT);
	        	return true;
	        case R.id.callCCW:
	        	Intent callIntent = new Intent(Intent.ACTION_CALL);
	            callIntent.setData(Uri.parse("tel:" + CCWChinaConst.PHONE_CALL_NUMBER));
	            startActivity(callIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void sendEmail(String to, String subject, String content) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = getPackageManager();     
        @SuppressWarnings("static-access")
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, pm.MATCH_DEFAULT_ONLY);     
        ResolveInfo best = null;     
        for (final ResolveInfo info : matches) {
        	if (info.activityInfo.name.toLowerCase().contains("mail"))
            	best = info;
        }
        if (best != null) {
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name); 
            startActivity(emailIntent);
        }else {
        	Toast.makeText(CCWInfoActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
	}
	
	public void refreshActivity() {
		user = ((CCWApplication)this.getApplication()).getUser();
		
		TextView username = (TextView)findViewById(R.id.username);
		username.setText(user.getUsername());
		TextView titleName = (TextView)findViewById(R.id.titleName);
		titleName.setText(user.getTitleName());
		TextView firstname = (TextView)findViewById(R.id.firstname);
		firstname.setText(user.getFirstname());
		TextView lastname = (TextView)findViewById(R.id.lastname);
		lastname.setText(user.getLastname());
		TextView email = (TextView)findViewById(R.id.email);
		email.setText(user.getEmail());
		TextView cellphone = (TextView)findViewById(R.id.cellphone);
		cellphone.setText(user.getCellphone());
		
		Button myOrders = (Button)findViewById(R.id.myOrders);
		myOrders.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showMyOrders();
			}
		});
		Button updateInformation = (Button)findViewById(R.id.updateInformation);
		updateInformation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateInformation();
			}
		});
		Button updatePassword = (Button)findViewById(R.id.updatePassword);
		updatePassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updatePassword();
			}
		});
	}
	
	private void showMyOrders() {
		Intent intent = new Intent(CCWInfoActivity.this, MyOrderActivity.class);
		startActivity(intent);
	}
	
	private void updateInformation() {
		Intent intent = new Intent(CCWInfoActivity.this, UpdateInformationActivity.class);
		startActivity(intent);
	}
	
	private void updatePassword() {
		Intent intent = new Intent(CCWInfoActivity.this, UpdatePasswordActivity.class);
		startActivity(intent);
	}
}

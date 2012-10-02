package com.ccwchina.information;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ccwchina.CCWApplication;
import com.ccwchina.R;
import com.ccwchina.bean.User;

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
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
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

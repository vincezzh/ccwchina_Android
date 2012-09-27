package com.ccwchina;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ccwchina.tab.CCWTabActivity;

public class LoginActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						CCWTabActivity.class);
				startActivity(intent);
			}
		});
		
		User user = new User();
		user.setUsername("vincezzh");
		user.setEmail("vincezzh@gmail.com");
		user.setFirstname("Vince");
		user.setLastname("Zhang");
		user.setTitleId(4);
		user.setTitleName("Miss");
		user.setCellphone("+14168397036");
		((CCWApplication) this.getApplication()).setUser(user);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}

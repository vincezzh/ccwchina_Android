package com.ccwchina.calendar;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ccwchina.CCWApplication;
import com.ccwchina.R;
import com.ccwchina.User;

public class OrderActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);

		CourseCalendar cc = (CourseCalendar)getIntent().getSerializableExtra("_CourseCalendar");
		User user = ((CCWApplication)this.getApplication()).getUser();
		
		TextView courseLocationName = (TextView)findViewById(R.id.courseLocationName);
		courseLocationName.setText(cc.getCourseLocationName());
		TextView classTimeName = (TextView)findViewById(R.id.classTimeName);
		classTimeName.setText(cc.getClassTimeName());
		TextView classDate = (TextView)findViewById(R.id.classDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		classDate.setText(sdf.format(cc.getClassDate()));
		TextView course = null;
		for(int i=1; i<=cc.getCourseList().size(); i++) {
			switch(i) {
				case 1:
					course = (TextView)findViewById(R.id.course1);
					break;
				case 2:
					course = (TextView)findViewById(R.id.course2);
					break;
				case 3:
					course = (TextView)findViewById(R.id.course3);
					break;
			}
			course.setText(cc.getCourseList().get(i-1).getCourseNameEn());
		}
		RadioButton title = null;
		switch(user.getTitleId()) {
			case 1:
				title = (RadioButton)findViewById(R.id.title1);
				break;
			case 2:
				title = (RadioButton)findViewById(R.id.title2);
				break;
			case 3:
				title = (RadioButton)findViewById(R.id.title3);
				break;
			case 4:
				title = (RadioButton)findViewById(R.id.title4);
				break;
		}
		title.setChecked(true);
		
		EditText contactPerson = (EditText)findViewById(R.id.contactPerson);
		contactPerson.setText(user.getFirstname() + " " + user.getLastname());
		EditText email = (EditText)findViewById(R.id.email);
		email.setText(user.getEmail());
		EditText cellNumber = (EditText)findViewById(R.id.cellNumber);
		cellNumber.setText(user.getCellphone());
		TextView pricePerPerson = (TextView)findViewById(R.id.pricePerPerson);
		pricePerPerson.setText(cc.getPricePerPerson().toString());
		pricePerPerson.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}

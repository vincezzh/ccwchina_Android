package com.ccwchina.calendar;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ccwchina.CCWApplication;
import com.ccwchina.R;
import com.ccwchina.bean.CourseCalendar;
import com.ccwchina.bean.User;
import com.ccwchina.common.CCWChinaConst;

public class OrderActivity extends Activity {
	private String message;
	private CourseCalendar cc;
	private User user;
	private Handler handler;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private ProgressDialog waitingDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.order);

		cc = (CourseCalendar)getIntent().getSerializableExtra("_CourseCalendar");
		user = ((CCWApplication)this.getApplication()).getUser();
		
		TextView orderTitle = (TextView)findViewById(R.id.orderTitle);
		orderTitle.setText(cc.getCourseBranchTypeName());
		orderTitle.setTextColor(Color.parseColor(cc.getFontColor()));
		orderTitle.setBackgroundColor(Color.parseColor(cc.getBackgroundColor()));
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		Button order = (Button)findViewById(R.id.order);
		order.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				placeOrder();
			}
		});
		
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
			final String courseName = cc.getCourseList().get(i-1).getCourseNameEn();
			final String coursePicturePath = cc.getCourseList().get(i-1).getPictureOne();
			course.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(OrderActivity.this, CourseDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("_courseName", courseName);
					bundle.putString("_coursePicturePath", coursePicturePath);
			        intent.putExtras(bundle);
					startActivity(intent);
				}
			});
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void showAlertDialog(String title, String message, int icon, final boolean closeActivity) {
		new AlertDialog.Builder(OrderActivity.this)
		.setTitle(title)
		.setIcon(icon)
		.setMessage(message)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				if(closeActivity)
					finish();
			}
		}).show();
	}
	
	private void placeOrder() {
		waitingDialog = ProgressDialog.show(this, null, "Placing Order...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				waitingDialog.dismiss();
	            switch(msg.what){
	            case 1:
	            	showAlertDialog("Place Order", message, android.R.drawable.ic_dialog_info, true);
	                break;
	            case 2:
	            	showAlertDialog("Place Order Error", message, android.R.drawable.ic_dialog_alert, false);
	            	break;
	            }
			}
		};
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessful = sendOrderRequest();
	                Message msg = new Message();
	                if(isSuccessful)
	                	msg.what = 1;
	                else
	                	msg.what = 2;
	                handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private boolean sendOrderRequest() {
		message = null;
		boolean isSuccessful = false;
		try {
			String urlParams = setupAndCheckParams();
			if(message == null) {
				URL url = new URL(CCWChinaConst.WEBSITE_CONTEXT + "/mobile/book-public-order.htm?" + urlParams);
				InputStream inputStream = url.openStream();
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = inputStream.read()) != -1) {
					baf.append((byte) current);
				}
				String xml = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
				isSuccessful = parsePlaceOrderXMl(xml);
			}
		}catch(Exception e) {
			e.printStackTrace();
			message = CCWChinaConst.APP_ERROR_MSG;
		}
		return isSuccessful;
	}
	
	private String setupAndCheckParams() throws Exception {
		String usernameParam = user.getUsername();
		Integer peopleNumParam = 1;
		try {
			String peopleNumString = ((EditText)findViewById(R.id.peopleNumber)).getText().toString();
			peopleNumParam = Integer.valueOf(peopleNumString);
		}catch(NumberFormatException e) {
			message = "People Number must be integer.";
		}
		String courseCalendarIdParam = cc.getCourseCalendarId();
		Integer pricePerPersonParam = cc.getPricePerPerson();
		int peopleTitleIdParam = 1;
		switch(((RadioGroup)findViewById(R.id.titleGroup)).getCheckedRadioButtonId()) {
			case R.id.title1:
				peopleTitleIdParam = 1;
				break;
			case R.id.title2:
				peopleTitleIdParam = 2;
				break;
			case R.id.title3:
				peopleTitleIdParam = 3;
				break;
			case R.id.title4:
				peopleTitleIdParam = 4;
				break;
		}
		String contactPersonParam = ((EditText)findViewById(R.id.contactPerson)).getText().toString();
		String cellphoneParam = ((EditText)findViewById(R.id.cellNumber)).getText().toString();
		String emailParam = ((EditText)findViewById(R.id.email)).getText().toString();
		String flagParam = "byAndroid";
		
		if(contactPersonParam.length() == 0 || cellphoneParam.length() == 0 || emailParam.length() == 0) {
			message = "Please fill all fields.";
	    }else if(peopleNumParam.intValue() > cc.getSeatLeft().intValue()) {
	        message = "People number is bigger than the acceptable number.";
	    }
		
		StringBuffer urlParams = new StringBuffer();
		urlParams.append("username=" + URLEncoder.encode(usernameParam, "UTF-8"));
		urlParams.append("&order.totalPeopleNumber=" + peopleNumParam);
		urlParams.append("&courseCalendarId=" + URLEncoder.encode(courseCalendarIdParam, "UTF-8"));
		urlParams.append("&pricePerPerson=" + pricePerPersonParam);
		urlParams.append("&order.orderbasic.peopletitle.peopleTitleId=" + peopleTitleIdParam);
		urlParams.append("&order.orderbasic.contactPerson=" + URLEncoder.encode(contactPersonParam, "UTF-8"));
		urlParams.append("&order.orderbasic.cellphone=" + URLEncoder.encode(cellphoneParam, "UTF-8"));
		urlParams.append("&order.orderbasic.email=" + URLEncoder.encode(emailParam, "UTF-8"));
		urlParams.append("&order.flag=" + URLEncoder.encode(flagParam, "UTF-8"));
		return urlParams.toString();
	}
	
	private boolean parsePlaceOrderXMl(String xml) throws Exception {
		boolean isSuccessful = false;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = dbBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        NodeList messageNodeList = doc.getElementsByTagName("message");
        if(messageNodeList.getLength() > 0) {
        	message = messageNodeList.item(0).getTextContent();
        	isSuccessful = true;
        }else {
        	message = doc.getElementsByTagName("errorMsg").item(0).getTextContent();
        }
        return isSuccessful;
	}
}

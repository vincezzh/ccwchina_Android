package com.ccwchina.information;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
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
import com.ccwchina.bean.User;
import com.ccwchina.common.CCWChinaConst;

public class UpdateInformationActivity extends Activity {
	private String message;
	private User user;
	private Handler handler;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private ProgressDialog waitingDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.information);
		
		user = ((CCWApplication)this.getApplication()).getUser();
		
		TextView username = (TextView)findViewById(R.id.username);
		username.setText(user.getUsername());
		EditText firstname = (EditText)findViewById(R.id.firstname);
		firstname.setText(user.getFirstname());
		EditText lastname = (EditText)findViewById(R.id.lastname);
		lastname.setText(user.getLastname());
		EditText email = (EditText)findViewById(R.id.email);
		email.setText(user.getEmail());
		EditText cellphone = (EditText)findViewById(R.id.cellphone);
		cellphone.setText(user.getCellphone());
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
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveInformation();
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void saveInformation() {
		waitingDialog = ProgressDialog.show(this, null, "Updating Information...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				waitingDialog.dismiss();
	            switch(msg.what){
	            case 1:
	            	((CCWApplication) UpdateInformationActivity.this.getApplication()).setUser(user);
	            	showAlertDialog("Edit Information", message, android.R.drawable.ic_dialog_info, true);
	                break;
	            case 2:
	            	showAlertDialog("Edit Information Error", message, android.R.drawable.ic_dialog_alert, false);
	            	break;
	            }
			}
		};
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessful = sendSaveInformationRequest();
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
	
	private boolean sendSaveInformationRequest() {
		message = null;
		boolean isSuccessful = false;
		try {
			String urlParams = setupAndCheckParams();
			if(message == null) {
				URL url = new URL(CCWChinaConst.WEBSITE_CONTEXT + "/mobile/edit-information.htm?" + urlParams);
				InputStream inputStream = url.openStream();
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = inputStream.read()) != -1) {
					baf.append((byte) current);
				}
				String xml = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
				isSuccessful = parseSaveInformationXMl(xml);
			}
		}catch(Exception e) {
			e.printStackTrace();
			message = CCWChinaConst.APP_ERROR_MSG;
		}
		return isSuccessful;
	}
	
	private String setupAndCheckParams() throws Exception {
		String usernameParam = user.getUsername();
		int peopleTitleIdParam = 1;
		String peopleTitleName = "";
		switch(((RadioGroup)findViewById(R.id.titleGroup)).getCheckedRadioButtonId()) {
			case R.id.title1:
				peopleTitleIdParam = 1;
				peopleTitleName = ((RadioButton)findViewById(R.id.title1)).getText().toString();
				break;
			case R.id.title2:
				peopleTitleIdParam = 2;
				peopleTitleName = ((RadioButton)findViewById(R.id.title2)).getText().toString();
				break;
			case R.id.title3:
				peopleTitleIdParam = 3;
				peopleTitleName = ((RadioButton)findViewById(R.id.title3)).getText().toString();
				break;
			case R.id.title4:
				peopleTitleIdParam = 4;
				peopleTitleName = ((RadioButton)findViewById(R.id.title4)).getText().toString();
				break;
		}
		user.setTitleId(peopleTitleIdParam);
		user.setTitleName(peopleTitleName);
		String firstnameParam = ((EditText)findViewById(R.id.firstname)).getText().toString();
		user.setFirstname(firstnameParam);
		String lastnameParam = ((EditText)findViewById(R.id.lastname)).getText().toString();
		user.setLastname(lastnameParam);
		String emailParam = ((EditText)findViewById(R.id.email)).getText().toString();
		user.setEmail(emailParam);
		String cellphoneParam = ((EditText)findViewById(R.id.cellphone)).getText().toString();
		user.setCellphone(cellphoneParam);
		
		StringBuffer urlParams = new StringBuffer();
		urlParams.append("user.userId=" + URLEncoder.encode(usernameParam, "UTF-8"));
		urlParams.append("&user.peopletitle.peopleTitleId=" + peopleTitleIdParam);
		urlParams.append("&user.firstName==" + URLEncoder.encode(firstnameParam, "UTF-8"));
		urlParams.append("&user.lastName=" + URLEncoder.encode(lastnameParam, "UTF-8"));
		urlParams.append("&user.email=" + URLEncoder.encode(emailParam, "UTF-8"));
		urlParams.append("&user.cellphone=" + URLEncoder.encode(cellphoneParam, "UTF-8"));
		return urlParams.toString();
	}
	
	private boolean parseSaveInformationXMl(String xml) throws Exception {
		boolean isSuccessful = false;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = dbBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        NodeList errorMsgNodeList = doc.getElementsByTagName("errorMsg");
        if(errorMsgNodeList.getLength() > 0) {
        	message = errorMsgNodeList.item(0).getTextContent();
        }else {
        	message = doc.getElementsByTagName("message").item(0).getTextContent();
        	isSuccessful = true;
        }
        return isSuccessful;
	}
	
	private void showAlertDialog(String title, String message, int icon, final boolean closeActivity) {
		new AlertDialog.Builder(UpdateInformationActivity.this)
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
}

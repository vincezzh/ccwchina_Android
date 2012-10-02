package com.ccwchina.information;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.ccwchina.CCWApplication;
import com.ccwchina.R;
import com.ccwchina.bean.User;
import com.ccwchina.common.CCWChinaConst;

public class UpdatePasswordActivity extends Activity {
	private String message;
	private User user;
	private Handler handler;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private ProgressDialog waitingDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_password);
		
		user = ((CCWApplication)this.getApplication()).getUser();
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				savePassword();
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void savePassword() {
		waitingDialog = ProgressDialog.show(this, null, "Upading Password...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				waitingDialog.dismiss();
	            switch(msg.what){
	            case 1:
	            	showAlertDialog("Update Password", message, android.R.drawable.ic_dialog_info, true);
	                break;
	            case 2:
	            	showAlertDialog("Update Password Error", message, android.R.drawable.ic_dialog_alert, false);
	            	break;
	            }
			}
		};
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessful = sendSavePasswordRequest();
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
	
	private boolean sendSavePasswordRequest() {
		message = null;
		boolean isSuccessful = false;
		try {
			String urlParams = setupAndCheckParams();
			if(message == null) {
				URL url = new URL(CCWChinaConst.WEBSITE_CONTEXT + "/mobile/edit-password.htm?" + urlParams);
				InputStream inputStream = url.openStream();
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = inputStream.read()) != -1) {
					baf.append((byte) current);
				}
				String xml = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
				isSuccessful = parseSavePasswordXMl(xml);
			}
		}catch(Exception e) {
			e.printStackTrace();
			message = CCWChinaConst.APP_ERROR_MSG;
		}
		return isSuccessful;
	}
	
	private String setupAndCheckParams() throws Exception {
		String usernameParam = user.getUsername();
		String passwordParam = ((EditText)findViewById(R.id.password)).getText().toString();
		String confirmPasswordParam = ((EditText)findViewById(R.id.confirmPassword)).getText().toString();
		
		if(passwordParam.length() < 6 || passwordParam.length() > 20) {
			message = "The length of password should be from 6 to 20";
		}else if(!isValidInput(passwordParam)) {
			message = "Please input letters, numbers, -, _, . or @ in Password";
		}else if(!passwordParam.equals(confirmPasswordParam)) {
			message = "Password and Confirm password are not same.";
		}
		
		StringBuffer urlParams = new StringBuffer();
		urlParams.append("user.userId=" + URLEncoder.encode(usernameParam, "UTF-8"));
		urlParams.append("&user.password=" + URLEncoder.encode(passwordParam, "UTF-8"));
		return urlParams.toString();
	}
	
	private boolean parseSavePasswordXMl(String xml) throws Exception {
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
		new AlertDialog.Builder(UpdatePasswordActivity.this)
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
	
	private boolean isValidInput(String input) {
		boolean isValid = false;
		String regEx = "^[a-zA-Z0-9_@\\.\\-]*$";
		Pattern pattern = Pattern.compile(regEx); 
        Matcher matcher = pattern.matcher(input);
        isValid = matcher.matches();
        return isValid;
	}
}

package com.ccwchina;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ccwchina.bean.User;
import com.ccwchina.common.CCWChinaConst;
import com.ccwchina.tab.CCWTabActivity;

public class SignUpActivity extends Activity {
	private String message;
	private User user;
	private Handler handler;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		Button signUp = (Button)findViewById(R.id.signUp);
		signUp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				signUp();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void signUp() {
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
	            switch(msg.what){
	            case 1:
	            	((CCWApplication) SignUpActivity.this.getApplication()).setUser(user);
	            	Intent intent = new Intent(SignUpActivity.this, CCWTabActivity.class);
					startActivity(intent);
	                break;
	            case 2:
	            	showAlertDialog("Sign Up Error", message, android.R.drawable.ic_dialog_alert, false);
	            	break;
	            }
			}
		};
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessful = sendSignUpRequest();
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
	
	private boolean sendSignUpRequest() {
		boolean isSuccessful = false;
		try {
			String urlParams = setupAndCheckParams();
			if(message == null) {
				URL url = new URL(CCWChinaConst.WEBSITE_CONTEXT + "/mobile/register.htm?" + urlParams);
				InputStream inputStream = url.openStream();
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = inputStream.read()) != -1) {
					baf.append((byte) current);
				}
				String xml = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
				isSuccessful = parseSignUpXMl(xml);
			}
		}catch(Exception e) {
			e.printStackTrace();
			message = CCWChinaConst.APP_ERROR_MSG;
		}
		return isSuccessful;
	}
	
	private String setupAndCheckParams() {
		String usernameParam = ((EditText)findViewById(R.id.username)).getText().toString();
		String passwordParam = ((EditText)findViewById(R.id.password)).getText().toString();
		String confirmPasswordParam = ((EditText)findViewById(R.id.confirmPassword)).getText().toString();
		String emailParam = ((EditText)findViewById(R.id.email)).getText().toString();
		
		//Todo Check input logic
		
		StringBuffer urlParams = new StringBuffer();
		urlParams.append("user.userId=" + usernameParam);
		urlParams.append("&user.password=" + passwordParam);
		urlParams.append("&user.email=" + emailParam);
		return urlParams.toString();
	}
	
	private boolean parseSignUpXMl(String xml) throws Exception {
		boolean isSuccessful = false;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = dbBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        NodeList errorMsgNodeList = doc.getElementsByTagName("errorMsg");
        if(errorMsgNodeList.getLength() > 0) {
        	message = errorMsgNodeList.item(0).getTextContent();
        }else {
        	user = new User();
        	user.setUsername(doc.getElementsByTagName("username").item(0).getTextContent());
        	user.setTitleId(Integer.valueOf(doc.getElementsByTagName("id").item(0).getTextContent()));
        	user.setTitleName(doc.getElementsByTagName("value").item(0).getTextContent());
        	user.setFirstname(doc.getElementsByTagName("firstname").item(0).getTextContent());
        	user.setLastname(doc.getElementsByTagName("lastname").item(0).getTextContent());
        	user.setEmail(doc.getElementsByTagName("email").item(0).getTextContent());
        	user.setCellphone(doc.getElementsByTagName("cellphone").item(0).getTextContent());
        	isSuccessful = true;
        }
        return isSuccessful;
	}
	
	private void showAlertDialog(String title, String message, int icon, final boolean closeActivity) {
		new AlertDialog.Builder(SignUpActivity.this)
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

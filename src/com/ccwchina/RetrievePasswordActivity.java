package com.ccwchina;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ccwchina.common.CCWChinaConst;

public class RetrievePasswordActivity extends Activity {
	private String message;
	private Handler handler;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private ProgressDialog waitingDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.retrieve_password);
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		Button retrievePassword = (Button)findViewById(R.id.retrievePassword);
		retrievePassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				retrievePassword();
			}
		});
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
        	Toast.makeText(RetrievePasswordActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
	}
	
	private void retrievePassword() {
		waitingDialog = ProgressDialog.show(this, null, "Retrieving Password...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				waitingDialog.dismiss();
	            switch(msg.what){
	            case 1:
	            	showAlertDialog("Retrieve Password", message, android.R.drawable.ic_dialog_info, true);
	                break;
	            case 2:
	            	showAlertDialog("Retrieve Password Error", message, android.R.drawable.ic_dialog_alert, false);
	            	break;
	            }
			}
		};
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessful = sendRetrievePasswordRequest();
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
	
	private boolean sendRetrievePasswordRequest() {
		message = null;
		boolean isSuccessful = false;
		try {
			String urlParams = setupAndCheckParams();
			if(message == null) {
				URL url = new URL(CCWChinaConst.WEBSITE_CONTEXT + "/mobile/retrieve-password.htm?" + urlParams);
				InputStream inputStream = url.openStream();
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = inputStream.read()) != -1) {
					baf.append((byte) current);
				}
				String xml = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
				isSuccessful = parseRetrievePasswordXMl(xml);
			}
		}catch(Exception e) {
			e.printStackTrace();
			message = CCWChinaConst.APP_ERROR_MSG;
		}
		return isSuccessful;
	}
	
	private String setupAndCheckParams() throws Exception {
		String usernameParam = ((EditText)findViewById(R.id.username)).getText().toString();
		if(usernameParam.length() == 0) {
			message = "Please enter username.";
	    }
		StringBuffer urlParams = new StringBuffer();
		urlParams.append("username=" + URLEncoder.encode(usernameParam, "UTF-8"));
		return urlParams.toString();
	}
	
	private boolean parseRetrievePasswordXMl(String xml) throws Exception {
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
		new AlertDialog.Builder(RetrievePasswordActivity.this)
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

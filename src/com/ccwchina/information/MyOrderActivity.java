package com.ccwchina.information;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ccwchina.CCWApplication;
import com.ccwchina.LoginActivity;
import com.ccwchina.R;
import com.ccwchina.bean.CourseCalendar;
import com.ccwchina.bean.PublicOrder;
import com.ccwchina.bean.User;
import com.ccwchina.common.CCWChinaConst;

public class MyOrderActivity extends Activity {
	private String message;
	private User user;
	private List<PublicOrder> orderList;
	private Handler handler;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private ProgressDialog waitingDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_orders);
		
		user = ((CCWApplication)this.getApplication()).getUser();
		loadMyOrders();
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
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
        	Toast.makeText(MyOrderActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
	}
	
	private void loadMyOrders() {
		waitingDialog = ProgressDialog.show(this, null, "Loading My Orders...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				waitingDialog.dismiss();
	            switch(msg.what){
	            case 1:
	            	refreshMyOrders();
	                break;
	            case 2:
	            	showAlertDialog("My Orders Error", message, android.R.drawable.ic_dialog_alert, false);
	            	break;
	            }
			}
		};
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessful = sendLoadMyOrdersRequest();
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
	
	private boolean sendLoadMyOrdersRequest() {
		message = null;
		boolean isSuccessful = false;
		try {
			String urlParams = setupAndCheckParams();
			if(message == null) {
				URL url = new URL(CCWChinaConst.WEBSITE_CONTEXT + "/mobile/list-public-order.htm?" + urlParams);
				InputStream inputStream = url.openStream();
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = inputStream.read()) != -1) {
					baf.append((byte) current);
				}
				String xml = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
				isSuccessful = parseLoadMyOrdersXMl(xml);
			}
		}catch(Exception e) {
			e.printStackTrace();
			message = CCWChinaConst.APP_ERROR_MSG;
		}
		return isSuccessful;
	}
	
	private String setupAndCheckParams() throws Exception {
		String usernameParam = user.getUsername();
		
		StringBuffer urlParams = new StringBuffer();
		urlParams.append("username=" + URLEncoder.encode(usernameParam, "UTF-8"));
		return urlParams.toString();
	}
	
	private boolean parseLoadMyOrdersXMl(String xml) throws Exception {
		boolean isSuccessful = false;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = dbBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        NodeList errorMsgNodeList = doc.getElementsByTagName("errorMsg");
        if(errorMsgNodeList.getLength() > 0) {
        	message = errorMsgNodeList.item(0).getTextContent();
        }else {
        	orderList = new ArrayList<PublicOrder>();
            NodeList orderNodeList = doc.getElementsByTagName("order");
            PublicOrder order = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for(int i = 0; i < orderNodeList.getLength(); i++) {
            	order = new PublicOrder();
            	Element orderElement = (Element)orderNodeList.item(i);
            	
            	order.setOrderPublicId(orderElement.getElementsByTagName("orderPublicId").item(0).getTextContent());
            	order.setClassDate(sdf.parse(orderElement.getElementsByTagName("classDate").item(0).getTextContent()));
            	
            	Element classtime = (Element)orderElement.getElementsByTagName("classTime").item(0);
            	order.setClassTimeId(Integer.valueOf(classtime.getElementsByTagName("id").item(0).getTextContent()));
            	order.setClassTimeName(classtime.getElementsByTagName("name").item(0).getTextContent());
            	
            	order.setTotalPeopleNumber(Integer.valueOf(orderElement.getElementsByTagName("totalPeopleNumber").item(0).getTextContent()));
            	
            	Element courseLocation = (Element)orderElement.getElementsByTagName("courseLocation").item(0);
            	order.setCourseLocationId(Integer.valueOf(courseLocation.getElementsByTagName("id").item(0).getTextContent()));
            	order.setCourseLocationName(courseLocation.getElementsByTagName("name").item(0).getTextContent());
            	
            	Element courseTrunkType = (Element)orderElement.getElementsByTagName("courseTrunkType").item(0);
            	order.setCourseTrunkTypeId(Integer.valueOf(courseTrunkType.getElementsByTagName("id").item(0).getTextContent()));
            	order.setCourseTrunkTypeName(courseTrunkType.getElementsByTagName("name").item(0).getTextContent());
            	order.setFontColor(courseTrunkType.getElementsByTagName("fontColor").item(0).getTextContent());
            	order.setBackgroundColor(courseTrunkType.getElementsByTagName("backgroundColor").item(0).getTextContent());
            	Element courseBranchType = (Element)courseTrunkType.getElementsByTagName("courseBranchType").item(0);
            	order.setCourseBranchTypeId(Integer.valueOf(courseBranchType.getElementsByTagName("id").item(0).getTextContent()));
            	order.setCourseBranchTypeName(courseBranchType.getElementsByTagName("name").item(0).getTextContent());
            	
            	orderList.add(order);
            }
        	isSuccessful = true;
        }
        return isSuccessful;
	}
	
	private void showAlertDialog(String title, String message, int icon, final boolean closeActivity) {
		new AlertDialog.Builder(MyOrderActivity.this)
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
	
	private void refreshMyOrders() {
		LinearLayout orderListFrameLayout = (LinearLayout)findViewById(R.id.orderListLayout);
		ScrollView orderListView = new ScrollView(this);
		
		LinearLayout.LayoutParams orderListLayoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		LinearLayout orderListLayout = createLayout(LinearLayout.VERTICAL);
		
		orderListView.addView(orderListLayout, orderListLayoutParams);
		orderListFrameLayout.addView(orderListView);
		
		LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);  
		titleLayoutParams.setMargins(20, 0, 20, 0);
		LinearLayout.LayoutParams subTitleLayoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);  
		subTitleLayoutParams.setMargins(20, 0, 20, 10);
		TextView title = null;
		TextView subTitle = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(PublicOrder order : orderList) {
			title = new TextView(this);
			title.setText("[" +  sdf.format(order.getClassDate()) + " " + order.getClassTimeName() + "]");
			title.getPaint().setFakeBoldText(true);
			orderListLayout.addView(title, titleLayoutParams);
			subTitle = new TextView(this);
			subTitle.setText(order.getCourseTrunkTypeName() + " at " + order.getCourseLocationName() + " kitchen");
			orderListLayout.addView(subTitle, subTitleLayoutParams);
		}
	}
	
	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this);
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);
		
		return lay;
	}
}

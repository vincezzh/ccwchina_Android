package com.ccwchina.calendar;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccwchina.R;
import com.ccwchina.common.CCWChinaConst;

public class CourseDetailActivity extends Activity {
	private ImageView coursePicture;
	private TextView courseName;
	private String coursePicturePathString;
	private Bitmap courseImage;
	private Handler handler;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private ProgressDialog waitingDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.course_detail);
		
		String courseNameString = getIntent().getStringExtra("_courseName");
		coursePicturePathString = getIntent().getStringExtra("_coursePicturePath");
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		coursePicture = (ImageView)findViewById(R.id.coursePicture);
		courseName = (TextView)findViewById(R.id.courseName);
		courseName.setText(courseNameString);
		downloadPicture();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void downloadPicture() {
		waitingDialog = ProgressDialog.show(this, null, "Loading...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				waitingDialog.dismiss();
	            switch(msg.what){
	            case 1:
	            	coursePicture.setImageBitmap(courseImage);
	                break;
	            }
			}
		};
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessful = sendDownloadPictureRequest();
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
	
	private boolean sendDownloadPictureRequest() {
		try {
			URL url = new URL(CCWChinaConst.WEBSITE_CONTEXT + coursePicturePathString);
			InputStream inputStream = url.openStream();
			courseImage = BitmapFactory.decodeStream(inputStream);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
}

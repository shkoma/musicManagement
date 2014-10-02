package com.shkoma.musicarrangemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity{

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.loading);
		
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable(){
			public void run()
			{
				startApp();
			}
		}, 2000);
	}
	
	private void startApp()
	{
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}

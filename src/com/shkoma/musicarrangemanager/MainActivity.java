package com.shkoma.musicarrangemanager;

import com.shkoma.musicarrangemanager.fragment.LocalFragment;
import com.shkoma.musicarrangemanager.fragment.SettingFragment;
import com.shkoma.musicarrangemanager.fragment.WebFragment;
import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements View.OnClickListener{

	private final String TAG = MainActivity.class.getSimpleName();
	
	public final static int LOCALFRAGMENT = 0;
	public final static int WEBFRAGMENT = 1;
	public final static int SETTINGFRAGMENT = 2;
	private int currentFragment;
	
	private Button tabLocal;
	private Button tabWeb;
	private Button tabSetting;
	
	private RelativeLayout main;
	
	private Fragment newFragment;
	private LocalFragment localFragment;
	private WebFragment webFragment;
	private SettingFragment settingFragment;
	
	//private static int dpi = -1;
	//private static float density = -1;
	
	public static Point mainSize;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		main = (RelativeLayout)View.inflate(this, R.layout.main, null);
		
		tabLocal = (Button)main.findViewById(R.id.btn_tab1);
		tabWeb = (Button)main.findViewById(R.id.btn_tab2);
		tabSetting = (Button)main.findViewById(R.id.btn_tab3);
		
		tabLocal.setOnClickListener(this);
		tabWeb.setOnClickListener(this);
		tabSetting.setOnClickListener(this);
		
		currentFragment = LOCALFRAGMENT;
		// dp 와 density를 알아낸다.
		DisplayMetrics outMetrics = new DisplayMetrics();
		mainSize = new Point();
		this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		this.getWindowManager().getDefaultDisplay().getSize(mainSize);
		
		Log.i(TAG, "mainSize W: " + mainSize.x);
		Log.i(TAG, "mainSize W: " + mainSize.y);
	//	dpi = outMetrics.densityDpi;
	//	density = outMetrics.density;
		
		setContentView(main);
		fragmentReplace(currentFragment);
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event){
//		super.onTouchEvent(event);
//		
//		int x = (int)event.getX();
//		int y = (int)event.getY();
//		
//		Log.i(TAG, "x: " + x + ", y: " + y);
//		return true;
//	}
////	// 540, 1034
	
//	static public int getDpi()
//	{
//		return dpi;
//	}
//	static public float getDensity()
//	{
//		return density;
//	}
	
	public void onResume()
	{
		super.onResume();
		
	}
	
	@Override
	public void onBackPressed()
	{
		if( localFragment.isSlide() ){
			localFragment.onBackPressedWithSlide();
			return;
		}
		
		if( currentFragment == LOCALFRAGMENT && !localFragment.isRoot() ){
			localFragment.onBackPressed();
			return;
		}
		super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		boolean isSwap = false;
		
		switch(v.getId())
		{
		case R.id.btn_tab1:
			if( currentFragment != LOCALFRAGMENT ){
				currentFragment = LOCALFRAGMENT;
				isSwap = true;
			}
			break;
			
		case R.id.btn_tab2:
			if( currentFragment != WEBFRAGMENT ){
				currentFragment = WEBFRAGMENT;
				isSwap = true;
			}
			break;
			
		case R.id.btn_tab3:
			if( currentFragment != SETTINGFRAGMENT ){
				currentFragment = SETTINGFRAGMENT;
				isSwap = true;
			}
			break;
		}
		if( isSwap )
			fragmentReplace(currentFragment);
	}
	
	private void fragmentReplace(int newFragmentIndex)
	{
		Fragment newFragment = null;
		
		newFragment = getFragment(newFragmentIndex);
		
		// replace Fragment
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		
		transaction.replace(R.id.linearlayout_fragment_view, newFragment);
		
		transaction.commit();
	}
	
	private Fragment getFragment(int index)
	{
		newFragment = null;
		
		switch(index)
		{
		case LOCALFRAGMENT:
			localFragment = new LocalFragment();
			newFragment = localFragment;
			break;
		case WEBFRAGMENT:
			webFragment = new WebFragment();
			newFragment = webFragment;
			break;
		case SETTINGFRAGMENT:
			settingFragment = new SettingFragment();
			newFragment = settingFragment;
			break;
		}
		
		return newFragment;
	}
}

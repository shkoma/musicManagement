package com.shkoma.musicarrangemanager.fragment;

import java.io.File;
import java.util.ArrayList;

import com.shkoma.musicarrangemanager.*;
import com.shkoma.musicarrangemanager.dialog.LocalMenuDialog;
import com.shkoma.musicarrangemanager.dialog.LocalPriorityDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocalFragment extends Fragment{

	private final String TAG = LocalFragment.class.getSimpleName();
	
	public static final int SONGNAME = 0;
	public static final int ARTIST = 1;
	public static final int ALBUM = 2;
	public static final int SONGWRITHER = 3;
	public static final int LYLICSWRITER = 4;
	public static final int SONGYEAR = 5;
	public static final int SONGMONTH = 6;
	public static final int TRACK = 7;
	
	private final String priOneString = "1���� ����";
	private final String priTwoString = "2���� ����";
	private final String priThreeString = "3���� ����";
	
	private String extRoot ="/sdcard";
	private String mPath = "/sdcard";
	
	private ArrayList<String> fileList;
	private ArrayList<String> folderList;
	
	private LinearLayout main;
	private LinearLayout slide;
	private FrameLayout frameMain;
	private RelativeLayout btnRelative;
	
	private ListView fileView;
	private ListView folderView;
	private TextView textViewLocal;
	private TextView textViewFile;
	private TextView textViewFolder;
	private ImageView imageViewBtn;
	
	//button
	private Button btnPriorityOne;
	private Button btnPriorityTwo;
	private Button btnPriorityThree;
	
	private Button btnOk;
	private Button btnCancel;
	
	public static final int PRIORITYONE = 100;
	public static final int PRIORITYTWO = 200;
	public static final int PRIORITYTHREE = 300;
	
	//EditText
	private EditText editTextOne;
	private EditText editTextTwo;
	private EditText editTextThree;
	
	//private LocalMenuDialog menuDialog;
	
	private TranslateAnimation mShowAni;
	private TranslateAnimation mHideAni;
	private LinearLayout.LayoutParams slideParam;
	private Point mainSize;
	
	private LocalPriorityDialog localPriDialog;
	
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		mainSize = new Point();
		getActivity().getWindow().getWindowManager().getDefaultDisplay().getSize(mainSize);
		
		main = (LinearLayout)View.inflate(getActivity(), R.layout.local , null);
		
		slide = (LinearLayout)View.inflate(getActivity(), R.layout.local_slide_layout, null);
		slideParam = new LinearLayout.LayoutParams((int)(mainSize.x*0.8), LayoutParams.MATCH_PARENT);
		slide.setLayoutParams(slideParam);
		
		// slide button and EditText
		btnPriorityOne = (Button)slide.findViewById(R.id.btn_priority_one);
		btnPriorityTwo = (Button)slide.findViewById(R.id.btn_priority_two);
		btnPriorityThree = (Button)slide.findViewById(R.id.btn_priority_three);
		
		btnPriorityOne.setOnClickListener(btnPriListener);
		btnPriorityTwo.setOnClickListener(btnPriListener);
		btnPriorityThree.setOnClickListener(btnPriListener);
		
		editTextOne = (EditText)slide.findViewById(R.id.editText_fileNameSelect_one);
		editTextTwo = (EditText)slide.findViewById(R.id.editText_fileNameSelect_two);
		editTextThree = (EditText)slide.findViewById(R.id.editText_fileNameSelect_three);
		
		btnOk = (Button)slide.findViewById(R.id.btn_slide_ok);
		btnCancel = (Button)slide.findViewById(R.id.btn_slide_cancel);
		
		btnOk.setOnClickListener(btnOkAndCancelListener);
		btnCancel.setOnClickListener(btnOkAndCancelListener);
		
		
		frameMain = (FrameLayout)View.inflate(getActivity(), R.layout.local_frame_layout, null);
		btnRelative = (RelativeLayout)View.inflate(getActivity(), R.layout.local_relative_btn, null);
		
		imageViewBtn = (ImageView)btnRelative.findViewById(R.id.imageView_local_btn);
		
		textViewLocal = (TextView)main.findViewById(R.id.textview_local_location);
		textViewFile = (TextView)main.findViewById(R.id.textview_local_file);
		textViewFolder = (TextView)main.findViewById(R.id.textview_local_folder);
		
		imageViewBtn.setOnClickListener(menuListener);
		
		//extRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		//sdcard�� �ִ��� Ȯ��
		if(!isSdCard()){
			Toast.makeText(getActivity(), "sdcard�� �����Ǿ����� �ʰų� ������ ����", 0).show();
			return null;
		}
		
		// ListView�� �ʱ�ȭ �Ѵ�. (file, folder)
		initListView();
		
		// ù sdcard�� ���� ���ϱ����� �ľ��Ѵ�.
		checkFileAndFolder(extRoot);
		setListView();
		
		frameMain.addView(main);
		frameMain.addView(btnRelative);
		frameMain.addView(slide);
		slide.setVisibility(View.INVISIBLE);
		return frameMain;
	}
	
	private Handler mHandler = new Handler(){
		
		public void handleMessage(Message msg)
		{
			String setPriority;
			switch(msg.what)
			{
			case PRIORITYONE:
				// handler message�� ���������� �ü� �ֱ⿡ �Լ��� ������ �ҷ�����.
				setPriority = checkPriority(msg.arg1);
				if( !setPriority.equals(null) )
					btnPriorityOne.setText(setPriority);
				break;
				
			case PRIORITYTWO:
				setPriority = checkPriority(msg.arg1);
				if( !setPriority.equals(null) )
					btnPriorityTwo.setText(setPriority);
				break;
				
			case PRIORITYTHREE:
				setPriority = checkPriority(msg.arg1);
				if( !setPriority.equals(null) )
					btnPriorityThree.setText(setPriority);
				break;
			}
		}
	};
	
	private String checkPriority(int priority)
	{
		String stringPri = null;
		switch(priority)
		{
		case SONGNAME:
			stringPri = "���";
			break;
		case ARTIST:
			stringPri = "��Ƽ��Ʈ";
			break;
		case ALBUM:
			stringPri = "�ٹ�";
			break;
		case SONGWRITHER:
			stringPri = "�۰";
			break;
		case LYLICSWRITER:
			stringPri = "�ۻ簡";
			break;
		case SONGYEAR:
			stringPri = "�߸ų⵵";
			break;
		case SONGMONTH:
			stringPri = "�߸ſ�";
			break;
		case TRACK:
			stringPri = "Ʈ��";
			break;
		}
		return stringPri;
	}
	
	public void onStart()
	{
		super.onStart();
		doAnimation();
	}
	
	// slide animation
	private void openSlide(){
		
		slide.setVisibility(View.VISIBLE);
		slide.startAnimation(mShowAni);
	}
	private void closeSlide(){
		
		slide.setVisibility(View.INVISIBLE);
		slide.startAnimation(mHideAni);
	}
	private void doAnimation(){
		
		mShowAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, 
										  Animation.RELATIVE_TO_SELF, 0.0f, 
										  Animation.RELATIVE_TO_SELF, 0.0f, 
										  Animation.RELATIVE_TO_SELF, 0.0f);
		
		mHideAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
										  Animation.RELATIVE_TO_SELF, -1.0f,
										  Animation.RELATIVE_TO_SELF, 0.0f,
										  Animation.RELATIVE_TO_SELF, 0.0f);
		
		mShowAni.setDuration(1000);
		mHideAni.setDuration(1000);
	}
	
	View.OnClickListener menuListener = new View.OnClickListener() {
		
		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.imageView_local_btn:
				
				Log.i(TAG, "is if");
				if( slide.getVisibility() == View.INVISIBLE ){
					openSlide();
					Log.i(TAG, "open Slide");
				}
				
				break;
			}
		}
	};
	
	View.OnClickListener btnPriListener = new View.OnClickListener() {
		
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			int priority = -1;
			switch(v.getId()){
			case R.id.btn_priority_one:
				priority = PRIORITYONE;
				break;

			case R.id.btn_priority_two:
				priority = PRIORITYTWO;
				break;
				
			case R.id.btn_priority_three:
				priority = PRIORITYTHREE;
				break;
			}
			if( priority != -1 ){
				localPriDialog = new LocalPriorityDialog(getActivity(), mHandler, priority);
				localPriDialog.show();
			}
		}
	};
	
	View.OnClickListener btnOkAndCancelListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch( v.getId() )
			{
			case R.id.btn_slide_ok:
			
			case R.id.btn_slide_cancel:
				closeSlide();

				resetPriority();
				resetFileName();
				break;
			}
		}
	};
	
	
	public boolean isRoot()
	{
		return mPath.equals(extRoot);
	}
	public boolean isSlide(){
		
		return slide.getVisibility() == View.VISIBLE ? true : false;
	}
	
	private void resetPriority(){
		
		btnPriorityOne.setText(priOneString);
		btnPriorityTwo.setText(priTwoString);
		btnPriorityThree.setText(priThreeString);
	}
	private void resetFileName(){
		editTextOne.setHint("%a");
		editTextTwo.setHint("%a");
		editTextThree.setHint("%a");
	}
	
	public void onBackPressedWithSlide(){
		closeSlide();
		
		resetPriority();
		resetFileName();
		
	}
	
	public void onBackPressed()
	{
		// ���� ��η� ������
		int pos = mPath.lastIndexOf("/");
		String strPath = mPath.substring(0, pos);
		checkFileAndFolder(strPath);
		setListView();
	}
	
	private void initListView()
	{
		fileView = (ListView)main.findViewById(R.id.listview_local_file);
		folderView = (ListView)main.findViewById(R.id.listview_local_folder);
		
		fileList = new ArrayList<String>();
		folderList = new ArrayList<String>();
		
		ArrayAdapter<String> adapterFile = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, fileList);
		ArrayAdapter<String> adapterFolder = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, folderList);
		
		fileView.setAdapter(adapterFile);
		folderView.setAdapter(adapterFolder);
		
		fileView.setOnItemClickListener(fileListener);
		folderView.setOnItemClickListener(folderListener);
		
	}
	
	private AdapterView.OnItemClickListener fileListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private AdapterView.OnItemClickListener folderListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String strItem = folderList.get(position);
			String strPath = mPath + "/" + strItem;
			checkFileAndFolder(strPath);
			setListView();
		}
	};
	
	private boolean isSdCard()
	{
		String extState = Environment.getExternalStorageState();
		if( extState.equals(Environment.MEDIA_MOUNTED) == false )
		{
			return false;
		}
		return true;
	}
	
	private void checkFileAndFolder(String strPath)
	{
		File fileRoot = new File(strPath);
		
		// list clear
		folderList.clear();
		fileList.clear();
		
		// ������ �ƴ϶�� �����ε�, �׷��� �� �Լ��� �θ��� �ȵȴ�.
		if( !fileRoot.isDirectory() )
		{
			Log.i(TAG, "fileRoot is not a directory");
			return;
		}
		mPath = strPath;
		textViewLocal.setText(mPath);
		
		String[] list = fileRoot.list();
		if( list == null )
		{
			Log.i(TAG, "fileRoot has no list");
			return;
		}
		
		// ������ list�� �ľ��� �������� �������� ����
		File tempFile;
		
		// �ϳ��� Ȯ��
		for( int i = 0; i < list.length; i++ )
		{
			tempFile = new File(strPath + "/" + list[i]);
			if( tempFile.isDirectory() )
			{
				folderList.add(list[i]);
			}
			else
				fileList.add(list[i]);
		}
		Log.i(TAG, "folder: " + folderList.size() + ", file: " + fileList.size());
	}
	
	private void setListView()
	{
		if( fileList.size() == 0 && folderList.size() == 0){
			Log.i(TAG, "file and folderList are null");
			folderView.setVisibility(View.GONE);
			fileView.setVisibility(View.GONE);
			
			textViewFolder.setVisibility(View.GONE);
			textViewFile.setVisibility(View.GONE);
			return;
		}
		
		ArrayAdapter adapter;
		if( folderList.size() != 0 ){
			if( folderView.getVisibility() == View.GONE ){
				folderView.setVisibility(View.VISIBLE);
				textViewFolder.setVisibility(View.VISIBLE);
			}
			adapter = (ArrayAdapter)folderView.getAdapter();
			adapter.notifyDataSetChanged();
		}
		else
		{
			folderView.setVisibility(View.GONE);
			textViewFolder.setVisibility(View.GONE);
		}
		if( fileList.size() != 0 ){
			
			if( fileView.getVisibility() == View.GONE ){
				fileView.setVisibility(View.VISIBLE);
				textViewFile.setVisibility(View.VISIBLE);
			}
			adapter = (ArrayAdapter)fileView.getAdapter();
			adapter.notifyDataSetChanged();
		}
		else
		{
			fileView.setVisibility(View.GONE);
			textViewFile.setVisibility(View.GONE);
		}
	}
}

package com.shkoma.musicarrangemanager.fragment;

import java.io.File;
import java.util.ArrayList;

import com.shkoma.musicarrangemanager.*;
import com.shkoma.musicarrangemanager.dialog.LocalMenuDialog;
import com.shkoma.musicarrangemanager.dialog.LocalPriorityDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LocalFragment extends Fragment {

	private final String TAG = LocalFragment.class.getSimpleName();

	public static final int NOTHING = 0;
	public static final int SONGNAME = 1;
	public static final int ARTIST = 2;
	public static final int ALBUM = 3;
	public static final int SONGWRITHER = 4;
	public static final int LYLICSWRITER = 5;
	public static final int SONGYEAR = 6;
	public static final int SONGMONTH = 7;
	public static final int TRACK = 8;

	private final String priOneString = "1순위 선택";
	private final String priTwoString = "2순위 선택";
	private final String priThreeString = "3순위 선택";

	private String extRoot = "/sdcard";
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
	private TextView textPreviewFileName;
	private ImageView imageViewBtn;

	// button
	private Button btnPriorityOne;
	private Button btnPriorityTwo;
	private Button btnPriorityThree;

	private Button btnOk;
	private Button btnCancel;

	public static final int PRIORITYONE = 100;
	public static final int PRIORITYTWO = 200;
	public static final int PRIORITYTHREE = 300;

	// EditText
	private EditText editTextOne;
	private EditText editTextTwo;
	private EditText editTextThree;

	// private LocalMenuDialog menuDialog;

	private TranslateAnimation mShowAni;
	private TranslateAnimation mHideAni;
	private LinearLayout.LayoutParams slideParam;
	private Point mainSize;

	private LocalPriorityDialog localPriDialog;
	private InputMethodManager inputManager;
	
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mainSize = new Point();
		getActivity().getWindow().getWindowManager().getDefaultDisplay()
				.getSize(mainSize);
		inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		main = (LinearLayout) View.inflate(getActivity(), R.layout.local, null);

		slide = (LinearLayout) View.inflate(getActivity(),
				R.layout.local_slide_layout, null);
		slideParam = new LinearLayout.LayoutParams((int) (mainSize.x * 0.8),
				LayoutParams.MATCH_PARENT);
		slide.setLayoutParams(slideParam);

		// slide button and EditText
		btnPriorityOne = (Button) slide.findViewById(R.id.btn_priority_one);
		btnPriorityTwo = (Button) slide.findViewById(R.id.btn_priority_two);
		btnPriorityThree = (Button) slide.findViewById(R.id.btn_priority_three);

		btnPriorityOne.setOnClickListener(btnPriListener);
		btnPriorityTwo.setOnClickListener(btnPriListener);
		btnPriorityThree.setOnClickListener(btnPriListener);

		editTextOne = (EditText) slide
				.findViewById(R.id.editText_fileNameSelect_one);
		editTextTwo = (EditText) slide
				.findViewById(R.id.editText_fileNameSelect_two);
		editTextThree = (EditText) slide
				.findViewById(R.id.editText_fileNameSelect_three);

		// set editorListener
		editTextOne.setOnEditorActionListener(textListener);
		editTextTwo.setOnEditorActionListener(textListener);
		editTextThree.setOnEditorActionListener(textListener);
		
		// set textWatcher
		editTextOne.addTextChangedListener(textWatcher);
		editTextTwo.addTextChangedListener(textWatcher);
		editTextThree.addTextChangedListener(textWatcher);
		
		// editText 숨기기
		editTextOne.setVisibility(View.INVISIBLE);
		editTextTwo.setVisibility(View.INVISIBLE);
		editTextThree.setVisibility(View.INVISIBLE);

		btnOk = (Button) slide.findViewById(R.id.btn_slide_ok);
		btnCancel = (Button) slide.findViewById(R.id.btn_slide_cancel);

		btnOk.setOnClickListener(btnOkAndCancelListener);
		btnCancel.setOnClickListener(btnOkAndCancelListener);

		frameMain = (FrameLayout) View.inflate(getActivity(),
				R.layout.local_frame_layout, null);
		btnRelative = (RelativeLayout) View.inflate(getActivity(),
				R.layout.local_relative_btn, null);

		imageViewBtn = (ImageView) btnRelative
				.findViewById(R.id.imageView_local_btn);

		textViewLocal = (TextView) main
				.findViewById(R.id.textview_local_location);
		textViewFile = (TextView) main.findViewById(R.id.textview_local_file);
		textViewFolder = (TextView) main
				.findViewById(R.id.textview_local_folder);
		
		textPreviewFileName = (TextView)slide.findViewById(R.id.textView_slide_preview_file_name);

		imageViewBtn.setOnClickListener(menuListener);

		// extRoot =
		// Environment.getExternalStorageDirectory().getAbsolutePath();

		// sdcard가 있는지 확인
		if (!isSdCard()) {
			Toast.makeText(getActivity(), "sdcard가 장착되어있지 않거나 에러가 존재", 0)
					.show();
			return null;
		}

		// ListView를 초기화 한다. (file, folder)
		initListView();

		// 첫 sdcard의 폴더 파일구조를 파악한다.
		checkFileAndFolder(extRoot);
		setListView();

		frameMain.addView(main);
		frameMain.addView(btnRelative);
		frameMain.addView(slide);
		slide.setVisibility(View.INVISIBLE);
		return frameMain;
	}

	public void onStart() {
		super.onStart();
		doAnimation();
	}

	public boolean isRoot() {
		return mPath.equals(extRoot);
	}

	public boolean isSlide() {

		return slide.getVisibility() == View.VISIBLE ? true : false;
	}

	private void initListView() {
		fileView = (ListView) main.findViewById(R.id.listview_local_file);
		folderView = (ListView) main.findViewById(R.id.listview_local_folder);

		fileList = new ArrayList<String>();
		folderList = new ArrayList<String>();

		ArrayAdapter<String> adapterFile = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1, fileList);
		ArrayAdapter<String> adapterFolder = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1, folderList);

		fileView.setAdapter(adapterFile);
		folderView.setAdapter(adapterFolder);

		fileView.setOnItemClickListener(fileListener);
		folderView.setOnItemClickListener(folderListener);

	}

	// slide animation
	private void openSlide() {

		slide.setVisibility(View.VISIBLE);
		slide.startAnimation(mShowAni);
	}

	private void closeSlide() {

		slide.setVisibility(View.INVISIBLE);
		slide.startAnimation(mHideAni);
	}

	private void doAnimation() {

		mShowAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

		mHideAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

		mShowAni.setDuration(1000);
		mHideAni.setDuration(1000);
	}
	
	View.OnClickListener menuListener = new View.OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.imageView_local_btn:

				Log.i(TAG, "is if");
				if (slide.getVisibility() == View.INVISIBLE) {
					openSlide();
					
//					fileView.postDelayed(new Runnable(){
//						public void run()
//						{
							if (fileView.getVisibility() == View.VISIBLE
									&& folderView.getVisibility() == View.VISIBLE) {
								fileView.setVisibility(View.INVISIBLE);
								folderView.setVisibility(View.INVISIBLE);
							}
//						}
//					}, 500);
					
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
			switch (v.getId()) {
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
			if (priority != -1) {
				localPriDialog = new LocalPriorityDialog(getActivity(),
						mHandler, priority);
				localPriDialog.show();
			}
		}
	};

	View.OnClickListener btnOkAndCancelListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.btn_slide_ok:

				// 미리보기 완성 후 주석 없애기
//				fileView.setVisibility(View.VISIBLE);
//				folderView.setVisibility(View.VISIBLE);
				break;
			case R.id.btn_slide_cancel:

				Builder dlg = new AlertDialog.Builder(getActivity());
				dlg.setTitle("취소하기")
						.setMessage("정말 취소하시겠습니까?")
						.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										closeSlide();
										resetPriority();
										resetFileName();
								
										if( fileView.getVisibility() != View.VISIBLE ){
											fileView.setVisibility(View.VISIBLE);
											folderView.setVisibility(View.VISIBLE);
										}
									}
								})
						.setNegativeButton("NO",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								}).show();
				break;
			}
		}
	};

	// 초기화 하는 부분
	private void resetPriority() {

		btnPriorityOne.setText(priOneString);
		btnPriorityTwo.setText(priTwoString);
		btnPriorityThree.setText(priThreeString);
		if (btnPriorityThree.getVisibility() != View.VISIBLE) {
			btnPriorityThree.setVisibility(View.VISIBLE);
		}
	}

	private void resetFileName() {
		editTextOne.setHint("%a");
		editTextTwo.setHint("%a");
		editTextThree.setHint("%a");
		textPreviewFileName.setText("");

		if (editTextOne.getVisibility() != View.INVISIBLE)
			editTextOne.setVisibility(View.INVISIBLE);
		if (editTextTwo.getVisibility() != View.INVISIBLE)
			editTextTwo.setVisibility(View.INVISIBLE);
		if (editTextThree.getVisibility() != View.INVISIBLE)
			editTextThree.setVisibility(View.INVISIBLE);
	}

	public void onBackPressedWithSlide() {
		
		if (fileView.getVisibility() != View.VISIBLE) {
			fileView.setVisibility(View.VISIBLE);
			folderView.setVisibility(View.VISIBLE);
		}
		closeSlide();
		resetPriority();
		resetFileName();

	}

	public void onBackPressed() {
		// 이전 경로로 ㄱㄱㄱ
		int pos = mPath.lastIndexOf("/");
		String strPath = mPath.substring(0, pos);
		checkFileAndFolder(strPath);
		setListView();
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			String setPriority;
			switch (msg.what) {
			case PRIORITYONE:
				// handler message가 여러곳에서 올수 있기에 함수를 세번다 불러야함.
				setPriority = checkPriority(msg.arg1);
				if (!setPriority.equals(null)) {
					btnPriorityOne.setText(setPriority);
					setEditText(msg.arg1, setPriority, editTextOne);
				}
				break;

			case PRIORITYTWO:
				setPriority = checkPriority(msg.arg1);
				
				if (!setPriority.equals(null)) {
					btnPriorityTwo.setText(setPriority);
					setEditText(msg.arg1, setPriority, editTextTwo);

					if (msg.arg1 == NOTHING) // 3순위 선택 불가 하기
					{
						btnPriorityThree.setText(priThreeString);
						btnPriorityThree.setVisibility(View.INVISIBLE);

						if (editTextThree.getVisibility() == View.VISIBLE) {
							editTextThree.setHint("%a");
							editTextThree.setVisibility(View.INVISIBLE);
						}
					} else {
						if (btnPriorityThree.getVisibility() != View.VISIBLE)
							btnPriorityThree.setVisibility(View.VISIBLE);
					}
				}
				break;

			case PRIORITYTHREE:
				setPriority = checkPriority(msg.arg1);
				if (!setPriority.equals(null)) {
					btnPriorityThree.setText(setPriority);
					setEditText(msg.arg1, setPriority, editTextThree);

				}
				break;
			}
			setPreviewFileName();
		}
	};

	private void setEditText(int pri, String setPriority, EditText something) {
		if (pri != NOTHING) {
			something.setText(setPriority);
			if (something.getVisibility() != View.VISIBLE)
				something.setVisibility(View.VISIBLE);
		} else {
			something.setHint("%a");
			if (something.getVisibility() != View.INVISIBLE)
				something.setVisibility(View.INVISIBLE);
		}
	}

	private String checkPriority(int priority) {
		String stringPri = null;
		switch (priority) {
		case NOTHING:
			stringPri = "없음";
			break;
		case SONGNAME:
			stringPri = "곡명";
			break;
		case ARTIST:
			stringPri = "아티스트";
			break;
		case ALBUM:
			stringPri = "앨범";
			break;
		case SONGWRITHER:
			stringPri = "작곡가";
			break;
		case LYLICSWRITER:
			stringPri = "작사가";
			break;
		case SONGYEAR:
			stringPri = "발매년도";
			break;
		case SONGMONTH:
			stringPri = "발매월";
			break;
		case TRACK:
			stringPri = "트랙";
			break;
		}
		return stringPri;
	}
	
	private void setPreviewFileName(){
		
		String name="";
		Editable preName;
		//preName = (String) editTextOne.getText();
		
		preName = editTextOne.getText();
		name = (String) preName.toString();
		
		if( editTextTwo.getVisibility() == View.VISIBLE )
		{
			name += "_" + (String)editTextTwo.getText().toString();
		}
		
		if( editTextThree.getVisibility() == View.VISIBLE )
		{
			name += "_" + (String)editTextThree.getText().toString();
		}

		Log.i(TAG, "name: " + name);
		textPreviewFileName.setText(name);
	}

	
	TextView.OnEditorActionListener textListener = new TextView.OnEditorActionListener(){

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.editText_fileNameSelect_one:
			case R.id.editText_fileNameSelect_two:
			case R.id.editText_fileNameSelect_three:
				
				// IME_NULL is the generic key for ENTER, IME= inputMethodEditor
				if( actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP )
				{
					setPreviewFileName();
					inputManager.hideSoftInputFromWindow(((EditText)v).getWindowToken(), 0);
				}
				return true;
			}
			return false;
		}
		
	};
	
	TextWatcher textWatcher = new TextWatcher(){
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count){
			
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			setPreviewFileName();
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
	};
	

	private AdapterView.OnItemClickListener fileListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub

			String strItem = fileList.get(position);

			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				File file = new File(mPath + "/" + strItem);
				String extension = android.webkit.MimeTypeMap
						.getFileExtensionFromUrl(Uri.fromFile(file).toString());
				String mimetype = android.webkit.MimeTypeMap.getSingleton()
						.getMimeTypeFromExtension(extension);
				intent.setDataAndType(Uri.fromFile(file), mimetype);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

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

	private boolean isSdCard() {
		String extState = Environment.getExternalStorageState();
		if (extState.equals(Environment.MEDIA_MOUNTED) == false) {
			return false;
		}
		return true;
	}

	private void checkFileAndFolder(String strPath) {
		File fileRoot = new File(strPath);

		// list clear
		folderList.clear();
		fileList.clear();

		// 폴더가 아니라면 파일인데, 그러면 위 함수를 부르면 안된다.
		if (!fileRoot.isDirectory()) {
			Log.i(TAG, "fileRoot is not a directory");
			return;
		}
		mPath = strPath;
		textViewLocal.setText(mPath);

		String[] list = fileRoot.list();
		if (list == null) {
			Log.i(TAG, "fileRoot has no list");
			return;
		}

		// 각각의 list를 파악해 파일인지 폴더인지 구별
		File tempFile;

		// 하나씩 확인
		for (int i = 0; i < list.length; i++) {
			tempFile = new File(strPath + "/" + list[i]);
			if (tempFile.isDirectory()) {
				folderList.add(list[i]);
			} else
				fileList.add(list[i]);
		}
		Log.i(TAG,
				"folder: " + folderList.size() + ", file: " + fileList.size());
	}

	private void setListView() {
		if (fileList.size() == 0 && folderList.size() == 0) {
			Log.i(TAG, "file and folderList are null");
			folderView.setVisibility(View.GONE);
			fileView.setVisibility(View.GONE);

			textViewFolder.setVisibility(View.GONE);
			textViewFile.setVisibility(View.GONE);
			return;
		}

		ArrayAdapter adapter;
		if (folderList.size() != 0) {
			if (folderView.getVisibility() == View.GONE) {
				folderView.setVisibility(View.VISIBLE);
				textViewFolder.setVisibility(View.VISIBLE);
			}
			adapter = (ArrayAdapter) folderView.getAdapter();
			adapter.notifyDataSetChanged();
		} else {
			folderView.setVisibility(View.GONE);
			textViewFolder.setVisibility(View.GONE);
		}
		if (fileList.size() != 0) {

			if (fileView.getVisibility() == View.GONE) {
				fileView.setVisibility(View.VISIBLE);
				textViewFile.setVisibility(View.VISIBLE);
			}
			adapter = (ArrayAdapter) fileView.getAdapter();
			adapter.notifyDataSetChanged();
		} else {
			fileView.setVisibility(View.GONE);
			textViewFile.setVisibility(View.GONE);
		}
	}
}

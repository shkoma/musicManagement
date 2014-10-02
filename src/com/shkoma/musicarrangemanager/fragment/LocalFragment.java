package com.shkoma.musicarrangemanager.fragment;

import java.io.File;
import java.util.ArrayList;

import com.shkoma.musicarrangemanager.*;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocalFragment extends Fragment{

	private final String TAG = LocalFragment.class.getSimpleName();
	
	private String extRoot ="/sdcard";
	private String mPath = "/sdcard";
	
	private ArrayList<String> fileList;
	private ArrayList<String> folderList;
	
	private LinearLayout main;
	private ListView fileView;
	private ListView folderView;
	private TextView textViewLocal;
	private TextView textViewFile;
	private TextView textViewFolder;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		main = (LinearLayout)View.inflate(getActivity(), R.layout.local , null);
		
		textViewLocal = (TextView)main.findViewById(R.id.textview_local_location);
		textViewFile = (TextView)main.findViewById(R.id.textview_local_file);
		textViewFolder = (TextView)main.findViewById(R.id.textview_local_folder);
		
		//extRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		//sdcard가 있는지 확인
		if(!isSdCard()){
			Toast.makeText(getActivity(), "sdcard가 장착되어있지 않거나 에러가 존재", 0).show();
			return null;
		}
		
		// ListView를 초기화 한다. (file, folder)
		initListView();
		
		// 첫 sdcard의 폴더 파일구조를 파악한다.
		checkFileAndFolder(extRoot);
		setListView();
		
		return main;
	}
	
	public boolean isRoot()
	{
		return mPath.equals(extRoot);
	}
	
	public void onBackPressed()
	{
		// 이전 경로로 ㄱㄱㄱ
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
		
		// 폴더가 아니라면 파일인데, 그러면 위 함수를 부르면 안된다.
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
		
		// 각각의 list를 파악해 파일인지 폴더인지 구별
		File tempFile;
		
		// 하나씩 확인
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

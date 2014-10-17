package com.shkoma.musicarrangemanager.dialog;

import com.shkoma.musicarrangemanager.R;
import com.shkoma.musicarrangemanager.fragment.LocalFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class LocalPriorityDialog extends Dialog{

	public final String TAG = LocalPriorityDialog.class.getSimpleName();
	
	private LinearLayout main;
	private ListView list;
	private ArrayAdapter<CharSequence> adapter;
	private Handler mHandler;
	private Context mContext;
	private int mPriority;
	private String[] priorityArray;
	
	public LocalPriorityDialog(Context context, Handler handler, int priority) {
		super(context);
		// TODO Auto-generated constructor stub
		mPriority = priority;
		mHandler = handler;
		mContext = context;
		
		main = (LinearLayout)View.inflate(context, R.layout.local_priority_list, null);
		list = (ListView)main.findViewById(R.id.listView_priority);
		
		adapter = ArrayAdapter.createFromResource(context, R.array.priority, android.R.layout.simple_list_item_1);
		
		
//		String [] priorityArray = { "곡명", "아티스트", "앨범", "작곡가", "작사가",
//				"발매년도", "발매월", "트랙번호"};
//		
//		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>( context,
//				android.R.layout.simple_list_item_1, priorityArray );
//		
		priorityArray = context.getResources().getStringArray(R.array.priority);
		
		list.setAdapter(adapter);
		list.setOnItemClickListener(mItemClickListener);
		//list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//list.setDivider(new ColorDrawable(0x000000));
		list.setDividerHeight(5);
		
		setContentView(main);
	}
	
	AdapterView.OnItemClickListener mItemClickListener = new
			AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					
					final Message msg = Message.obtain();
					switch(mPriority)
					{
					case LocalFragment.PRIORITYONE:
						msg.what = LocalFragment.PRIORITYONE;
						break;
					case LocalFragment.PRIORITYTWO:
						msg.what = LocalFragment.PRIORITYTWO;
						break;
					case LocalFragment.PRIORITYTHREE:
						msg.what = LocalFragment.PRIORITYTHREE;
						break;
					}
					msg.arg1 = position;
					
					// 최종 확인
					Builder dlg = new AlertDialog.Builder(mContext);
					dlg.setTitle( mPriority/100 + "번째 우선 순위 선택")
					.setMessage ("[" + priorityArray[position] + "]" + " 를(을) 선택하십니까?")
					.setPositiveButton("YES", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							mHandler.sendMessage(msg);
							dialog.dismiss();
							dismiss();
						}
					})
					.setNegativeButton("NO", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							dialog.cancel();
						}
					})
					.show();
					
				}
			};
}

package com.shkoma.musicarrangemanager.dialog;

import com.shkoma.musicarrangemanager.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class LocalMenuDialog extends Dialog implements View.OnClickListener{

	private Handler mHandler;
	private Context mContext;
	
	private LinearLayout main;
	private Button btnMenuSetting;
	private Button btnTransaction;
	
	public LocalMenuDialog(Context context, Handler handler) {
		super(context);
		// TODO Auto-generated constructor stub

		mContext = context;
		mHandler = handler;
		
		main = (LinearLayout)View.inflate(context, R.layout.dialog_local_menu, null);
		btnMenuSetting = (Button)main.findViewById(R.id.btn_setting_menu);
		btnTransaction = (Button)main.findViewById(R.id.btn_transaction);
		
		btnMenuSetting.setOnClickListener(this);
		btnTransaction.setOnClickListener(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		setContentView(main);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_setting_menu:
			break;
			
		case R.id.btn_transaction:
			break;
		}
	}
}

package com.tang.calendar;
import com.example.android.BluetoothChat.R;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;

public class CalGridView extends GridView{
	
	private Context mContext;

	public CalGridView(Context context) {
		super(context);
		mContext = context;
		
		setGirdView();
	}

	private void setGirdView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		setLayoutParams(params);
		setNumColumns(7);
		setGravity(Gravity.CENTER_VERTICAL);
		setVerticalSpacing(1);
		setHorizontalSpacing(1);
		setBackgroundColor(getResources().getColor(R.color.calendar_background));
	
		WindowManager windowManager = ((Activity)mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int i = display.getWidth() / 7;
		int j = display.getWidth() - (i * 7);
		int x = j / 2;
		setPadding(x, 0, 0, 0);
	}
	
}

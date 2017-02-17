package com.tang.calendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.android.BluetoothChat.BluetoothChat;
import com.example.android.BluetoothChat.R;

import database.Operation;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class CalGridViewAdapter extends BaseAdapter {

	private Calendar calStartDate = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	static String SelectedDate = null;
	static String selString;
	private Operation database;
	public static int x = 0 ,y = 0;
	
	public void setSelectedDate(Calendar cal)
	{
		calSelected=cal;
	}
	
	private Calendar calToday = Calendar.getInstance();
	private int iMonthViewCurrentMonth = 0;

	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1);
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		
		int iDay = 0;
		int iFirstDayOfWeek = Calendar.MONDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

		calStartDate.add(Calendar.DAY_OF_MONTH, -1);

	}
	ArrayList<java.util.Date> titles;
	private ArrayList<java.util.Date> getDates() {

		UpdateStartDateForMonth();

		ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>();

		for (int i = 1; i <= 42; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}

		return alArrayList;
	}

	private Activity activity;
	Resources resources;
	private BluetoothChat bluetoothChat;
	
	public CalGridViewAdapter(Activity a,Calendar cal) {
		calStartDate=cal;
		activity = a;
		resources=activity.getResources();
		titles = getDates();
		database = new Operation(activity);
		bluetoothChat = new BluetoothChat();
	}
	
	public CalGridViewAdapter(Activity a) {
		activity = a;
		resources=activity.getResources();
		database = new Operation(activity);
		bluetoothChat = new BluetoothChat();
	}


	
	public int getCount() {
		return titles.size();
	}

	
	public Object getItem(int position) {
		return titles.get(position);
	}


	public long getItemId(int position) {
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout iv = new LinearLayout(activity);
		iv.setId(position + 5000);
		LinearLayout imageLayout = new LinearLayout(activity);
		imageLayout.setOrientation(0);
		iv.setGravity(Gravity.CENTER);
		iv.setOrientation(1);
		iv.setBackgroundColor(resources.getColor(R.color.white));

		Date myDate = (Date) getItem(position);
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		final int iMonth = calCalendar.get(Calendar.MONTH);
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);
		selString = String.valueOf(iMonth+1)+"."+String.valueOf(calCalendar.get(Calendar.DAY_OF_MONTH));
		
		iv.setBackgroundColor(resources.getColor(R.color.white));
		if (iDay == 7 || iDay == 1) {
			iv.setBackgroundColor(resources.getColor(R.color.text_7));
		}

		TextView txtToDay = new TextView(activity);
		txtToDay.setGravity(Gravity.CENTER_HORIZONTAL);
		txtToDay.setTextSize(9);
		
		int flag = 0;
		final DecimalFormat df = new DecimalFormat("00");
		String eString = (myDate.getYear()+1900) + "-" + df.format(myDate.getMonth() + 1) +"-"+ df.format(myDate.getDate());
		
		if (equalsDate(calToday.getTime(), myDate)) {
			iv.setBackgroundColor(resources.getColor(R.color.event_center));
			txtToDay.setText("TODAY");
		}
		
		if (database.query(eString) != 0) {
			txtToDay.setText("data");
			flag = 1;
		}
		
		if (equalsDate(calSelected.getTime(), myDate)) {
			SelectedDate = selString;
			iv.setBackgroundColor(resources.getColor(R.color.selection));
			if (!equalsDate(calSelected.getTime(), calToday.getTime())) {
				if (flag == 0) {
					Toast.makeText(activity, "没有该天的数据", Toast.LENGTH_SHORT).show();
				}else {
					bluetoothChat.estring = eString;
					bluetoothChat.page = database.lis;
					bluetoothChat.rep(eString, 1);
//					Log.e("page-estring", bluetoothChat.page+"-"+eString);
					activity.finish();
				}
			}
			if (equalsDate(calSelected.getTime(), calToday.getTime())) {	
				bluetoothChat.estring = eString;
				bluetoothChat.page = database.lis;
				bluetoothChat.rep(eString, 1);
//				Log.e("page-estring2", bluetoothChat.page+"-"+eString);
			}
		} else {
			if (equalsDate(calToday.getTime(), myDate)) {
				iv.setBackgroundColor(resources.getColor(R.color.calendar_zhe_day));
			}
		}
		
		TextView txtDay = new TextView(activity);
		txtDay.setGravity(Gravity.CENTER_HORIZONTAL);

		
		if (iMonth == iMonthViewCurrentMonth) {
			txtToDay.setTextColor(resources.getColor(R.color.ToDayText));
			txtDay.setTextColor(resources.getColor(R.color.Text));
		} else {
			txtDay.setTextColor(resources.getColor(R.color.noMonth));
			txtToDay.setTextColor(resources.getColor(R.color.noMonth));
		}

		int day = myDate.getDate(); 
		txtDay.setText(String.valueOf(day));
		txtDay.setId(position + 500);
		iv.setTag(myDate);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		iv.addView(txtDay, lp);

		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		iv.addView(txtToDay, lp1);

		return iv;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private Boolean equalsDate(Date date1, Date date2) {

		if (date1.getYear() == date2.getYear()
				&& date1.getMonth() == date2.getMonth()
				&& date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}

	}

}

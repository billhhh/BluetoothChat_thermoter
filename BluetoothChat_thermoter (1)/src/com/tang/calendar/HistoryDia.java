package com.tang.calendar;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.android.BluetoothChat.BluetoothChat;
import com.example.android.BluetoothChat.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

public class HistoryDia extends Activity implements OnTouchListener,android.view.GestureDetector.OnGestureListener{

	
	public boolean onTouch(View v, MotionEvent event) {
		
		return mGesture.onTouchEvent(event);
	}
	AnimationListener animationListener=new AnimationListener() {
		
		public void onAnimationStart(Animation animation) {
		}
		
		
		public void onAnimationRepeat(Animation animation) {
		}
		
		
		public void onAnimationEnd(Animation animation) {
			CreateGirdView();
		}
	};
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;
	GestureDetector mGesture = null;
	
	private static final int calLayoutID = 11;
	private static final int mainLayoutID = 22;
	private static final int titleLayoutID = 33;
	private static final int caltitleLayoutID = 44;
	private static final int brID = 55;
	
	private RelativeLayout mainLayout;

	private CalGridViewAdapter gAdapter;
	private CalGridViewAdapter gAdapter1;
	private CalGridViewAdapter gAdapter3;

	private int iFirstDayOfWeek = Calendar.MONDAY;
	
	private Calendar calStartDate = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private GridView title_gView;
	private GridView gView1;
	private GridView gView2;
	private GridView gView3;
	private Context mContext = HistoryDia.this;
	
	private Button btnToday;
	private ImageButton btnPre;
	private ImageButton btnNext;
	
	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(generateContentView());

		UpdateStartDateForMonth();
		
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this,R.anim.slide_right_out);
		
		slideLeftIn.setAnimationListener(animationListener);
		slideLeftOut.setAnimationListener(animationListener);
		slideRightIn.setAnimationListener(animationListener);
		slideRightOut.setAnimationListener(animationListener);

		mGesture = new GestureDetector(this);
	}
	private View generateContentView(){
		viewFlipper = new ViewFlipper(this);
		viewFlipper.setId(calLayoutID);
		
		mainLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params_main = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainLayout.setLayoutParams(params_main);
		mainLayout.setId(mainLayoutID);
		mainLayout.setGravity(Gravity.CENTER);
		
		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);

		generateTopButtons(layTopControls); 
		RelativeLayout.LayoutParams params_title = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params_title.topMargin = 5;
		layTopControls.setId(titleLayoutID);
		
		mainLayout.addView(layTopControls, params_title);
		
		calStartDate = getCalendarStartDate();

		setTitleGirdView();
		RelativeLayout.LayoutParams params_cal_title = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params_cal_title.addRule(RelativeLayout.BELOW, titleLayoutID);
		mainLayout.addView(title_gView, params_cal_title);

		CreateGirdView();
		
		RelativeLayout.LayoutParams params_cal = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params_cal.addRule(RelativeLayout.BELOW, caltitleLayoutID);
		mainLayout.addView(viewFlipper, params_cal);
		
		LinearLayout br = new LinearLayout(this);
		RelativeLayout.LayoutParams params_br = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 1);
		params_br.addRule(RelativeLayout.BELOW, calLayoutID);
		br.setId(brID);
		br.setBackgroundColor(getResources().getColor(R.color.calendar_background));
		mainLayout.addView(br, params_br);
		return mainLayout;
	}
	
	private LinearLayout createLayout(int orientation){
		LinearLayout lay = new LinearLayout(this);
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.topMargin = 10;
		
		lay.setLayoutParams(params);
		lay.setOrientation(orientation);
		return lay;
	}
	private void generateTopButtons(LinearLayout layTopControls){
		btnToday = new Button(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		btnToday.setLayoutParams(lp);
		btnToday.setTextColor(Color.WHITE);
		btnToday.setBackgroundColor(Color.TRANSPARENT);
		btnToday.setTextSize(25);
		
		btnPre = new ImageButton(this);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(40, 40);
		lp2.rightMargin = 30;
		lp2.gravity = Gravity.CENTER_VERTICAL;
		btnPre.setLayoutParams(lp2);
		btnPre.setBackgroundDrawable(getResources().getDrawable(R.drawable.left));
		
		btnNext = new ImageButton(this);
		LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(40, 40);
		lp3.leftMargin = 30;
		lp3.gravity = Gravity.CENTER_VERTICAL;
		btnNext.setLayoutParams(lp3);
		btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.right));

       
		btnPre.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				viewFlipper.setInAnimation(slideRightIn);
				viewFlipper.setOutAnimation(slideRightOut);
				viewFlipper.showPrevious();
				setPrevViewItem();
			}
		});
		
	
		btnNext.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				viewFlipper.setInAnimation(slideLeftIn);
				viewFlipper.setOutAnimation(slideLeftOut);
				viewFlipper.showNext();
				setNextViewItem();
			}
		});

        layTopControls.setGravity(Gravity.CENTER_HORIZONTAL);
        layTopControls.addView(btnPre);
        layTopControls.addView(btnToday);
        layTopControls.addView(btnNext);
	}
	private void setTitleGirdView() {
		title_gView = setGirdView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		title_gView.setLayoutParams(params);
		title_gView.setVerticalSpacing(0);
		title_gView.setHorizontalSpacing(0);
		TitleGridAdapter titleAdapter = new TitleGridAdapter(this);
		title_gView.setAdapter(titleAdapter);
		title_gView.setId(caltitleLayoutID);
	}
	private void CreateGirdView() {

		Calendar tempSelected1 = Calendar.getInstance(); 
		Calendar tempSelected2 = Calendar.getInstance(); 
		Calendar tempSelected3 = Calendar.getInstance();
		tempSelected1.setTime(calStartDate.getTime());
		tempSelected2.setTime(calStartDate.getTime());
		tempSelected3.setTime(calStartDate.getTime());

		gView1 = new CalGridView(mContext);
		tempSelected1.add(Calendar.MONTH, -1);
		gAdapter1 = new CalGridViewAdapter(this, tempSelected1);
		gView1.setAdapter(gAdapter1);

		gView2 = new CalGridView(mContext);
		gAdapter = new CalGridViewAdapter(this, tempSelected2);
		gView2.setAdapter(gAdapter);

		gView3 = new CalGridView(mContext);
		tempSelected3.add(Calendar.MONTH, 1);
		gAdapter3 = new CalGridViewAdapter(this, tempSelected3);
		gView3.setAdapter(gAdapter3);

		gView2.setOnTouchListener(this);
		gView1.setOnTouchListener(this);
		gView3.setOnTouchListener(this);

		if (viewFlipper.getChildCount() != 0) {
			viewFlipper.removeAllViews();
		}

		viewFlipper.addView(gView2);
		viewFlipper.addView(gView3);
		viewFlipper.addView(gView1);

		DecimalFormat fm = new DecimalFormat("00");
		String s = calStartDate.get(Calendar.YEAR)
				+ "-" + fm.format(calStartDate.get(Calendar.MONTH) + 1);

		btnToday.setText(s);
	}
	private GridView setGirdView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		GridView gridView = new GridView(this);
		gridView.setLayoutParams(params);
		gridView.setNumColumns(7);
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setBackgroundColor(getResources().getColor(R.color.calendar_background));

		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int i = display.getWidth() / 7;
		int j = display.getWidth() - (i * 7);
		int x = j / 2;
		gridView.setPadding(x, 0, 0, 0);

		return gridView;
	}
	private void setPrevViewItem() {
		iMonthViewCurrentMonth--;
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);

	}
	private void setNextViewItem() {
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);

	}
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1);
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);

		DecimalFormat fm = new DecimalFormat("00");
		String s = calStartDate.get(Calendar.YEAR)
				+ "-" + fm.format(calStartDate.get(Calendar.MONTH) + 1);
		btnToday.setText(s);

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

	}
	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		return calStartDate;
	}

	public class TitleGridAdapter extends BaseAdapter {

		int[] titles = new int[] { R.string.Sun, R.string.Mon, R.string.Tue,
				R.string.Wed, R.string.Thu, R.string.Fri, R.string.Sat };

		private Activity activity;

		public TitleGridAdapter(Activity a) {
			activity = a;
		}

		public int getCount() {
			return titles.length;
		}

		
		public Object getItem(int position) {
			return titles[position];
		}

		
		public long getItemId(int position) {
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout iv = new LinearLayout(activity);
			TextView txtDay = new TextView(activity);
			txtDay.setFocusable(false);
			txtDay.setBackgroundColor(Color.TRANSPARENT);
			iv.setOrientation(1);

			txtDay.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

			int i = (Integer) getItem(position);

			txtDay.setTextColor(Color.WHITE);
			Resources res = getResources();

			if (i == R.string.Sat || i == R.string.Sun) {
				txtDay.setBackgroundColor(res.getColor(R.color.title_text_7));
			}

			txtDay.setText((Integer) getItem(position));

			iv.addView(txtDay, lp);

			return iv;
		}
	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (Math.abs(e1.getY() - e2.getY()) > 250)
				return false;
			if (e1.getX() - e2.getX() > 120	&& Math.abs(velocityX) > 200) {
				viewFlipper.setInAnimation(slideLeftIn);
				viewFlipper.setOutAnimation(slideLeftOut);
				viewFlipper.showNext();
				setNextViewItem();
				return true;

			} else if (e2.getX() - e1.getX() > 120 && Math.abs(velocityX) > 200) {
				viewFlipper.setInAnimation(slideRightIn);
				viewFlipper.setOutAnimation(slideRightOut);
				viewFlipper.showPrevious();
				setPrevViewItem();
				return true;

			}
		} catch (Exception e) {
		}
		return false;
	}
	public void onLongPress(MotionEvent e) {
		
	}
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}
	public void onShowPress(MotionEvent e) {
		
	}
	public boolean onSingleTapUp(MotionEvent e) {
		int pos = gView2.pointToPosition((int) e.getX(), (int) e.getY());
		LinearLayout txtDay = (LinearLayout) gView2.findViewById(pos + 5000);
		if (txtDay != null) {
			if (txtDay.getTag() != null) {
				Date date = (Date) txtDay.getTag();
				calSelected.setTime(date);
				Log.e("calselect", date.toString());

				gAdapter.setSelectedDate(calSelected);
				gAdapter.notifyDataSetChanged();

				gAdapter1.setSelectedDate(calSelected);
				gAdapter1.notifyDataSetChanged();

				gAdapter3.setSelectedDate(calSelected);
				gAdapter3.notifyDataSetChanged();
			}
		}
		return false;
	}
}

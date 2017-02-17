package com.example.android.BluetoothChat;

import java.text.DecimalFormat;
import java.util.Calendar;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import database.Operation;
 
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class BluetoothChat extends Activity {
	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	 
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	private TextView mTitle;
	
	public static Operation database;
	public static Cursor cursor;

	private String mConnectedDeviceName = null;
 
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothChatService mChatService = null;
	
    private int flag=0;
    public static GraphicalView myview;
    private LinearLayout layout;
    private Vibrator vibrator;
    public static XYMultipleSeriesRenderer renderer;
    private XYMultipleSeriesDataset dataset;
    public TextView current_time;
    private static String current;
    private DecimalFormat df;
    private Button butleft;
	private Button butright;
    public static int page;
    public static Context context;
    public static String estring = null;
    
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		butleft = (Button)this.findViewById(R.id.mainbutleft);
		butright = (Button)this.findViewById(R.id.mainbutright);
		layout = (LinearLayout) findViewById(R.id.graph);
		current_time = (TextView) findViewById(R.id.current_time);
		context = this;
		
		database = new Operation(this);
    	cursor = database.todo();
    	
    	renderer = new XYMultipleSeriesRenderer();
    	dataset = new XYMultipleSeriesDataset();
    	series = new XYSeries("高烧温度38℃");
    	series1 = new XYSeries("正常体温上限37.5℃");
    	series2 = new XYSeries("正常体温下限36.5℃");
    	series3 = new XYSeries("测量值");
    	df = new DecimalFormat("00");
    	
		chart = new LineChart(dataset, renderer);
		myview = new GraphicalView(this, chart);
		
		Calendar calendar = Calendar.getInstance();
		current = calendar.get(Calendar.YEAR)+"-"+df.format(calendar.get(Calendar.MONTH)+1)+"-"+df.format(calendar.get(Calendar.DAY_OF_MONTH));
		database.query(current);
		page = database.lis;
		rep(current, 0);
    	
		drawChat();
		
		
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "没有蓝牙设备",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

 
	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
		if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");
	 
		mChatService = new BluetoothChatService(this, mHandler);
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		try {
			vibrator.cancel();
		} catch (Exception e) {
		}
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}
	private final Handler mHandler = new Handler() {
		byte indexwr = 0; // 帧写入指针
		byte[] framedat = new byte[1024]; // 帧数据
		boolean syn = false;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					Time t = new Time();
			        t.setToNow(); //取得系统时间
			        int year = t.year;
			        int month = t.month+1;
			        int date = t.monthDay;
			        int hour = t.hour; // 注意 是0-23
			        int minute = t.minute;
			        int second = t.second;
			        String time;
					flag=1;
					time = year+"-"+df.format(month)+"-"+df.format(date)+"-"+df.format(hour)+":"+df.format(minute)+":"+df.format(second);
					cursor = database.todo();
					database.insert_data(cursor.getCount() + 1,"tang", time, 1);
				
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					break;
				case BluetoothChatService.STATE_CONNECTING:
					flag=0;
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					flag=0;
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				//byte[] writeBuf = (byte[]) msg.obj;
				//byte[] writeBuf={};
				// construct a string from the buffer
				//String writeMessage = new String(writeBuf);
				//mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] revdat = (byte[]) msg.obj; 
				byte bytes = (byte) msg.arg1;
				byte indexrd = 0;  
					if (!syn) { 
						for (byte i = indexrd; i < bytes; i++) {
							if ((revdat[i] == (byte) 0x80)) {
								syn = true;
								indexwr = 0; //
								indexrd = i;
								break;
							}
						}
					}
					if (syn){
						Time t = new Time();
				        t.setToNow(); //取得系统时间
				        int year = t.year;
				        int month = t.month+1;
				        int date = t.monthDay;
				        int hour = t.hour; // 注意 是0-23
				        int minute = t.minute;
				        int second = t.second;
				        String time, temperature;

						time = year+"-"+df.format(month)+"-"+df.format(date)+"-"+df.format(hour)+":"+df.format(minute)+":"+df.format(second);
						
						for (byte i = indexrd; i < bytes; i++) { //
							framedat[indexwr] = revdat[i];
							
//								System.out.print(framedat[i]+"-");
//							Log.e("tang-2", framedat[1]+"-"+framedat[0]);
							if ((indexwr > 1) && (indexwr == (framedat[1] - 1))) {//
								int length = framedat[1];
								byte[] data = new byte[length];
								for (int j = 0; j < length; j++) {
									data[j] = framedat[j];
								}
								
								cursor = database.todo();
								temperature = data[2]+"."+data[3];
									database.insert_data(cursor.getCount() + 1,temperature, time, 0);
								rep(time.substring(0, 10), 0);
								current_time.setText("当前温度："+temperature+"℃");
								
								
								if (Double.parseDouble(temperature) >= 38) {
									vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
		    				        long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启   
		    				        vibrator.vibrate(pattern,2);           //重复两次上面的pattern 如果只想震动一次，index设为-1
		    				        current_time.append("出于高烧状态");
		    				        current_time.setTextColor(Color.RED);
								}
								if (Double.parseDouble(temperature) < 36.5) {
//									vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
//		    				        long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启   
//		    				        vibrator.vibrate(pattern,2);           //重复两次上面的pattern 如果只想震动一次，index设为-1
									try {
										vibrator.cancel();
									} catch (Exception e) {
									}
		    				        current_time.append("您的温度计没放好！");
		    				        current_time.setTextColor(Color.RED);
								}
								if (Double.parseDouble(temperature) >= 36.5 && Double.parseDouble(temperature) < 38) {
									try {
										vibrator.cancel();
									} catch (Exception e) {
									}
		    				        current_time.setTextColor(Color.BLACK);
								}
								
								String[] temp = new String[length];
								for (int k = 0; k < length; k++) {
									temp[k] = Integer
											.toHexString(data[k] & 0xFF);
								}
								String total = "";
								for (int j = 0; j < length; j++) {
									total = total + " " + temp[j];
								}
								syn = false;
								break;
							}
							indexwr++;
						}
						System.out.println();
				}
				break;
			case MESSAGE_DEVICE_NAME:
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};
	private LineChart chart;
	public static XYSeries series;
	public static XYSeries series1;
	public static XYSeries series2;
	public static XYSeries series3;
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				setupChat();
			} else {
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(0, 0, 1, "蓝牙设置" ).setIcon( android.R.drawable.ic_menu_manage);
		menu.add(0, 2, 1, "取消报警" ).setIcon( android.R.drawable.ic_menu_send);
		menu.add(0, 1, 1, "连接上次" ).setIcon( android.R.drawable.ic_menu_revert);
		menu.add(0, 3, 1, "帮助" ).setIcon( android.R.drawable.ic_menu_help);
		return super.onCreateOptionsMenu(menu);
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
    	if( item.getItemId() == 0 )
    	{
    		Intent serverIntent = new Intent();
			serverIntent.setClass(getApplicationContext(), DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    	}
    	else if( item.getItemId() == 1 ){
    		try {
    			if (flag==1) {
    				Toast.makeText(getApplicationContext(), "蓝牙已经连接上", Toast.LENGTH_SHORT).show();
    			}
    			else {
    				
    				SharedPreferences SP = getSharedPreferences("SP",0);		
    				String address=SP.getString("device_address", null);
    				System.out.println(address);
    				BluetoothDevice device = mBluetoothAdapter
    						.getRemoteDevice(address);
    				mChatService.connect(device);
    			}
			} catch (Exception e) {
			}
    		
		}
    	else if( item.getItemId() == 2 ){
    		try {
        		vibrator.cancel();
			} catch (Exception e) {
			}
		}
    	else if( item.getItemId() == 3 ){
    		 
		}
    	return super.onOptionsItemSelected(item);
	}
    
    public void drawChat() {
    	
		dataset.addSeries(series);
        XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
		xyRenderer.setColor(Color.RED);
		xyRenderer.setPointStyle(PointStyle.CIRCLE);
		xyRenderer.setLineWidth(2);
		renderer.addSeriesRenderer(xyRenderer);
		renderer.setPanEnabled(true, false);
		renderer.setMarginsColor(Color.argb(0, 0xFc, 0xFc, 0xFc));
		renderer.setMargins(new int[] { 10, 10, 10, 5 });
		renderer.setBackgroundColor(Color.parseColor("#f3f3f3"));
		renderer.setApplyBackgroundColor(true);

		dataset.addSeries(series1);
        XYSeriesRenderer xyRenderer2 = new XYSeriesRenderer();
		xyRenderer2.setColor(Color.GREEN);
		xyRenderer2.setPointStyle(PointStyle.TRIANGLE);
		xyRenderer2.setLineWidth(2);
		renderer.addSeriesRenderer(xyRenderer2);
    	
		dataset.addSeries(series2);
        XYSeriesRenderer xyRenderer3 = new XYSeriesRenderer();
		xyRenderer3.setColor(Color.BLUE);
		xyRenderer3.setPointStyle(PointStyle.SQUARE);
		xyRenderer3.setLineWidth(2);
		renderer.addSeriesRenderer(xyRenderer3);

		dataset.addSeries(series3);
        XYSeriesRenderer xyRenderer4 = new XYSeriesRenderer();
		xyRenderer4.setColor(Color.BLACK);
		xyRenderer4.setPointStyle(PointStyle.DIAMOND);
		xyRenderer4.setLineWidth(3);
		renderer.addSeriesRenderer(xyRenderer4);
    	
		renderer.setYTitle("℃");
		renderer.setYAxisMax(39.50);
		renderer.setYAxisMin(36);
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setXLabels(0);
		renderer.setYLabels(10);
		renderer.setChartTitleTextSize(25);
		renderer.setLabelsTextSize(10);
		
		myview.setPadding(20, 20, 2, 3);
		
		myview.addZoomListener(new ZoomListener() {
			
			public void zoomReset() {
				
			}
			
			public void zoomApplied(ZoomEvent arg0) {

			}
		}, true, true);
		layout.addView(myview, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));
	}
    int position;
    public void drawxy(String timeString, int index) {
    	cursor = database.todo();
    	
    	series.clear();
		series1.clear();
		series2.clear();
		series3.clear();
		renderer.clearTextLabels();
		
		int end = 0;
		position = database.query(timeString) - database.count + 1;
		if (index == 0) {
	    	cursor.moveToPosition(position);
	    	end = database.count - 1;
	    	if (database.lis != 0) {
	    		int pos = position;
	    		for (int i = 0; i < database.lis; i++) {
					pos += database.c[i];
				}
//	    		Log.e("tang", "lis"+database.lis+"-"+"pos"+pos);
	    		cursor.moveToPosition(pos + database.lis);
				end = database.c[database.lis];
			}
		}else {
			int pos = position;
			for (int i = 0; i < page; i++) {
				pos += database.c[i];
				Log.e("tang-pos", timeString+"-"+pos+"-"+database.c[i]);
			}
			cursor.moveToPosition(pos + page);
			end = database.c[page];
			try {
				current_time.setText("");
				current_time.setText(cursor.getString(2).substring(0, 16) + "历史记录");
			} catch (Exception e) {
			}
		}
    	Log.e("tang","position"+ position+"-end"+end+"-lis"+database.lis+"-page"+page);
    	for (int i = 0 ; i < end; i++) {
    		series.add(i, 38);
    		series1.add(i, 37.5);
    		series2.add(i, 36.5);
    		series3.add(i, Double.parseDouble(cursor.getString(1)));
    		renderer.addTextLabel(i, cursor.getString(2).substring(8, 16));

    		cursor.moveToNext();
		}
    	
	}
    public void rep(String timeString, int index) {
    	drawxy(timeString, index);
    	if (index == 1) {
    		renderer.setXAxisMin(0);
    		int showcount = 8;
    		if (database.c[page] > 8) {
				showcount = database.c[page];
			}
            renderer.setXAxisMax(showcount);
		}else {
			renderer.setXAxisMin(0);
			int showcount = 8;
			if (database.c[database.lis] > 8) {
				showcount = database.c[database.lis];
			}
	    	renderer.setXAxisMax(showcount);
		}
		myview.repaint();

	}
    public void MainLeft(View view) {
    	Log.e("tang-page", page+"-"+ database.lis);
    	if (page <= 0) {
			butleft.setEnabled(false);

			if (page < database.lis) {
				butright.setEnabled(true);
			}
			else {
				butright.setEnabled(false);
			}
			
//			butleft.setBackgroundColor(Color.TRANSPARENT);
		}else {

			butleft.setEnabled(true);
			Log.e("==", "right");
			if (estring == null) {
				estring = current;
			}
			page--;
			rep(estring, 1);
			if (page < database.lis) {
				butright.setEnabled(true);
			}
			else {
				butright.setEnabled(false);
			}
			
//			butright.setBackgroundDrawable(getResources().getDrawable(R.drawable.right));
		}
	}
    public void MainRight(View view) {
    	Log.e("tang-page2", page+"-"+database.lis);
    	if (page >= database.lis) {
			butright.setEnabled(false);
			if (page > 0) {
				butleft.setEnabled(true);
			}else {
				butleft.setEnabled(false);
			}
//			butright.setBackgroundColor(Color.TRANSPARENT);
		}else{
			butright.setEnabled(true);
			Log.e("==", "right");
			if (estring == null) {
				estring = current;
			}
			page++;
			rep(estring, 1);
			if (page > 0) {
				butleft.setEnabled(true);
			}else {
				butleft.setEnabled(false);
			}
//			butleft.setBackgroundDrawable(getResources().getDrawable(R.drawable.left));
		}
	}
//    public static void drawButton(){
////		butleft = (Button)findViewById(R.id.mainbutleft);
////		butright = (Button)findViewById(R.id.mainbutright);
//    	Log.e("page", page+"-"+ database.lis);
//    	if (page <= 0) {
//    		butleft.setEnabled(false);
////    		butleft.setBackgroundColor(Color.TRANSPARENT);
//		}
//    	if (page >= database.lis) {
//    		butright.setEnabled(false);
////    		butright.setBackgroundColor(Color.TRANSPARENT);
//		}
//    	if (page < database.lis && page > 0) {
//    		butright.setEnabled(true);
////    		butright.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.right));
//    		butleft.setEnabled(true);
////    		butleft.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.left));
//		}
//    }
    public void check(View view) {
    	current_time.setText("");
        MyDialog dialog = new MyDialog(this);
        dialog.setTitle("选择");
        dialog.show();
        
	}
    
}
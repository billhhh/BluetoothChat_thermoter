package com.example.android.BluetoothChat;

import com.tang.calendar.HistoryDia;

import database.Operation;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MyDialog extends Dialog {

	private Context context;
	private BluetoothChat bluetoothChat;
	public MyDialog(Context context) {
		super(context);
		this.context = context;
	}
	private Operation database;
	private Button ok,no;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydialog);
		
		database = new Operation(context);
		bluetoothChat = new BluetoothChat();
		ok = (Button) findViewById(R.id.dialog_ok);
		no = (Button) findViewById(R.id.dialog_no);
		ok.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(context, HistoryDia.class);
				context.startActivity(intent);
				dismiss();
			}
		});
		no.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					database.del();
				bluetoothChat.cursor = bluetoothChat.database.todo();
				bluetoothChat.series.clear();
				bluetoothChat.series1.clear();
				bluetoothChat.series2.clear();
				bluetoothChat.series3.clear();
				bluetoothChat.renderer.clearTextLabels();
				bluetoothChat.cursor.close();
				try {
					bluetoothChat.current_time.setText("");
				} catch (Exception e) {
					Log.e("tang", "nerror");
				}
				bluetoothChat.myview.repaint();
				dismiss();
			}
		});
		
        
	}

}

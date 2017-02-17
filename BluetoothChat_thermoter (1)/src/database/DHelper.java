package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DHelper extends SQLiteOpenHelper{

	public DHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		 
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("cao", "here table---");
		String sql="CREATE TABLE Storage_tempture(id int,tempture varchar,time varchar,link int)";
		db.execSQL(sql);
		Log.e("create database", "success-------");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 
	}

}

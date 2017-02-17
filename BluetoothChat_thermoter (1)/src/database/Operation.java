package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class Operation extends DHelper{
	public SQLiteDatabase db;
	private DHelper dHelper;
	private static  String db_name = "Tempture";
	public String tab_name = "Storage_tempture";
	public int count;
	public int lis;
	public int[] c;
	
	public Operation(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		 dHelper = new DHelper(context, name, factory, version);
	}
	public Operation(Context context) {
		this(context, db_name, null, 1);
	}
	
	public void insert_data(int id, String tempture, String time, int link) {
		db = dHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		try {
			cv.put("id", id);
			cv.put("tempture", tempture);
			cv.put("time", time);
			cv.put("link", link);
			db.insert(tab_name, null, cv);
		} catch (Exception e) {
			Log.e("cao", "save error ---------");
		} finally {
			Log.e("cao", "save end  ---------");
		}
	}
	public Cursor todo() {
		
		db = dHelper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from Storage_tempture", null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public int query(String t) {
		Cursor cursor = todo();
		int flag = 0;
		int i,j,count1=0;
		count = 0;
		lis = 0;
		c = new int[1024];
		for (i = 0,j = 0; i < cursor.getCount(); i++) {
			if (cursor.getString(2).toString().substring(0, 10).equals(t)) {
				count++;
				flag = 1;
				if (cursor.getInt(3) == 1 && count != 1) {
					c[j++] = count1-1;
					count1 = 0;
					lis++;
//					Log.e("operation-lis", t+" "+lis+" here "+j);
				}
				count1++;
			}
			else {
				if (flag == 1) {
					break;
				}
			}
			cursor.moveToNext();
		}
		if (count != 0) {
				c[j] = count1 -1;
			cursor.moveToPrevious();
			return cursor.getInt(0);
		}
		return 0;
	}
	public void del() {
		db = dHelper.getWritableDatabase();
		db.delete(tab_name, null, null);
		db.close();
	}
	
}

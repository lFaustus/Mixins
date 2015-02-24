package com.faustus.mixins.database;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBAdapter
{

	DBHelper dbhelper;
	Context context;
	public DBAdapter(Context context)
	{
		this.context = context;
		dbhelper = new DBHelper(context);
	}
	
	public void insertData(Object... params)
	{
		SQLiteDatabase db = dbhelper.openDB();
		ContentValues contentValues = new ContentValues();
		
		Log.e("params length",params.length+"");
		for(int i = 0; i<params.length ; i++)
		{
			contentValues.put(DBHelper.COLUMN_NAMES[1], ((LiquorList)params[i]).getName());
			contentValues.put(DBHelper.COLUMN_NAMES[3], ((LiquorList)params[i]).getUrl());
			db.insert(DBHelper.TABLE_NAME, null, contentValues);
		}
		db.close();
		dbhelper.closeDB();
	}
	
	public void insertData(JSONObject JSON)
	{
		SQLiteDatabase db = dbhelper.openDB();
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_NAMES[1], JSON.toString());
		db.insert(DBHelper.TABLE_NAME,null, contentValues);
		db.close();
		dbhelper.closeDB();
		Toast.makeText(context, "Drink Added!", Toast.LENGTH_SHORT).show();
		
	}

	public ArrayList<String> getAllLiquors()
	{
		SQLiteDatabase db = dbhelper.openDB();
		Cursor cursor = null;
		ArrayList<String> temp = new ArrayList<String>();
		try
		{
			cursor = db.query(DBHelper.TABLE_NAME,DBHelper.COLUMN_NAMES,null,null,null,null,null,null);
			while(cursor.moveToNext())
			{
				temp.add(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAMES[1])));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.e("SQLEXCEPTIO","UGH");
		}
		catch(Exception e)
		{
			Log.e("EXCEPTIO","UGH");
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			db.close();
			dbhelper.closeDB();
		}
		
		return temp;
	}
	
	public ArrayList<String> getSingleLiquor(String selectionArgs)
	{
		SQLiteDatabase db = dbhelper.openDB();
		Cursor cursor = null;
		ArrayList<String> temp = new ArrayList<String>();
		try
		{
			cursor = db.query(DBHelper.TABLE_NAME, DBHelper.COLUMN_NAMES, DBHelper.COLUMN_NAMES[1]+"=?", new String[]{selectionArgs}, null, null,null);
			while(cursor.moveToNext())
			{
				temp.add(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAMES[1])));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			db.close();
			dbhelper.close();
		}
		return temp;
	}

	
	static class DBHelper extends SQLiteOpenHelper
	{
		private static final String DATABASE_NAME = "Mixins";
		private static final String TABLE_NAME = "WineList";
		private static final int DATABASE_VERSION = 1;
		//private static final String[] COLUMN_NAMES = { "_id", "Name", "Picture" ,"URL"};
		private static final String[] COLUMN_NAMES = { "_id", "JSONLiquour"};
		private static final String column = "JSONLiquor";

		public DBHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		public SQLiteDatabase openDB()
		{
			return super.getWritableDatabase();
		}
		
		public void closeDB()
		{
			super.close();
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			/*String query = "create table "
					+ TABLE_NAME
					+ " (" +COLUMN_NAMES[0] +" integer primary key autoincrement,"+COLUMN_NAMES[1]+" VARCHAR(255), "
					+ COLUMN_NAMES[2] +" BLOB, "+COLUMN_NAMES[3]+" VARCHAR(255));";*/
			String query = "create table if not exists "
					+ TABLE_NAME
					+ " (" +COLUMN_NAMES[0] +" integer primary key autoincrement,"+COLUMN_NAMES[1]+" TEXT);";
			db.execSQL(query);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXIST "+ TABLE_NAME);
			onCreate(db);

		}
	}
}

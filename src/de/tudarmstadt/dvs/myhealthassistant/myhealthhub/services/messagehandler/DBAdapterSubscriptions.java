/* 
 * Copyright (C) 2014 TU Darmstadt, Hessen, Germany.
 * Department of Computer Science Databases and Distributed Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.messagehandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapterSubscriptions {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_EVENT_TYPE = "eventtype";
	public static final String KEY_EVENT_ID = "eventid";
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_PRODUCER_ID = "producerid";
	public static final String KEY_PACKAGE_NAME = "packagename";
	public static final String KEY_READING_EVENT_TYPE = "readingeventtype";
	
	private static final String TAG = "database.test";
	
	private static final String DATABASE_NAME = "SubscriptionDB";
	private static final String DATABASE_TABLE = "subscriptiondb";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE =
			"create table if not exists "+DATABASE_TABLE+"("
			+KEY_ROWID+" integer, " 
			+KEY_EVENT_TYPE+" VARCHAR, "
			+KEY_EVENT_ID+" VARCHAR, "
			+KEY_TIMESTAMP+" VARCHAR, "
			+KEY_PRODUCER_ID+" VARCHAR, "
			+KEY_PACKAGE_NAME+" VARCHAR not null, "
			+KEY_READING_EVENT_TYPE+" VARCHAR not null, "
			+"PRIMARY KEY("+KEY_PACKAGE_NAME+","+KEY_READING_EVENT_TYPE+") ON CONFLICT REPLACE);";
	
	private final Context context;
	
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DBAdapterSubscriptions(Context ctx){
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL(DATABASE_CREATE);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					 + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
			onCreate(db);
		}
	}
	
	public DBAdapterSubscriptions openReadableDB() throws SQLException{
		db = DBHelper.getReadableDatabase();
		return this;
	}
	
	public DBAdapterSubscriptions openWritabelDB() throws SQLException{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		DBHelper.close();
	}
	
	public void createDatabase(){
		db.execSQL(DATABASE_CREATE);
	}
	
	public long insertOrReplaceRecord(String eventType, String eventID, String timestamp, String producerID, String packageName, String readingEventType){
		long i=1;
		//autoincrement _id, if database is empty it starts with 1
		Cursor c = db.rawQuery("SELECT max("+KEY_ROWID+") FROM "+DATABASE_TABLE, null);
		c.moveToFirst();
		if(c.getString(0)!=null){
			Log.d(TAG,"c.getString(0): "+c.getString(0));
			i=Long.parseLong(c.getString(0));
			i++;
			if(i==Long.MAX_VALUE-1){
				//TODO: Reset DB _id field to 1
			}
		}		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, ""+i);
		initialValues.put(KEY_EVENT_TYPE, eventType);
		initialValues.put(KEY_EVENT_ID, eventID);
		initialValues.put(KEY_TIMESTAMP, timestamp);
		initialValues.put(KEY_PRODUCER_ID, producerID);
		initialValues.put(KEY_PACKAGE_NAME, packageName);
		initialValues.put(KEY_READING_EVENT_TYPE, readingEventType);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public boolean deleteSubsriFption(long rowId){
		return db.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId,null) > 0;
	}
	
	public Cursor getAllRecords(){
		return db.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_EVENT_TYPE, KEY_EVENT_ID, KEY_TIMESTAMP, KEY_PRODUCER_ID, KEY_PACKAGE_NAME, KEY_READING_EVENT_TYPE}, null, null, null, null, null);
	}
	
	public Cursor getRecord(long rowId)throws SQLException{
		Cursor mCursor = 
				db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_EVENT_TYPE, KEY_EVENT_ID, KEY_TIMESTAMP, KEY_PRODUCER_ID, KEY_PACKAGE_NAME, KEY_READING_EVENT_TYPE}, KEY_ROWID+"="+rowId, null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public boolean deleteAll(){
		return db.delete(DATABASE_TABLE, null, null)>0;
	}
		
}
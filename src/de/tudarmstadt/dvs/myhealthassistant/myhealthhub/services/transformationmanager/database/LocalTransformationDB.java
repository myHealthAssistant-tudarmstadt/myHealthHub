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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalTransformationDB extends SQLiteOpenHelper {
	
	private static final String TAG = LocalTransformationDB.class.getName();
	
	public static final String TABLE_LOCAL_TRANSFORMATIONS = "local_transformations";
	public static final String COLUMN_ID ="id";
	public static final String COLUMN_BUNDLE_ID = "bundleId";
	public static final String COLUMN_TRANSFORMATION_NAME = "tranformationName";
	public static final String COLUMN_PRODUCED_EVENT_TYPE = "producedEventType";	
	public static final String COLUMN_REQUIRED_EVENT_TYPES = "requiredEventTypes";
	public static final String COLUMN_TRANSFORMATION_COSTS = "transformationCosts";
	
	// TRAFFIC MONITORING
	public static final String COLUMN_DATE_ID = "dateId";
	public static final String TABLE_TRAFFIC_MON = "local_traffic_monitoring";
	public static final String TABLE_DATE_TO_TRAFFIC = "local_date_to_traffic";
	public static final String COLUMN_X_AXIS = "xValue";
	public static final String COLUMN_Y_AXIS = "yValue";
	public static final String COLUMN_DATE_TEXT = "dateValue";
	public static final String COLUMN_TRAFFIC_ID = "trafficId";
	public static final String COLUMN_TYPE = "trafficType";
	
	public static final String TABLE_JSON_DATA_EXCHANGE = "local_data_exchange";
	public static final String COLUMN_JSON_ID = "json_id";
	public static final String COUMN_JSON_DATE = "json_date";
	public static final String COUMN_JSON_CONTENT = "json_content";
	public static final String COUMN_JSON_EXTRA = "json_extra";
	public static final String COUMN_JSON_IMAGE = "json_image";

	private static final String JSON_DATA_EXCHANGE = "CREATE TABLE "
			+ TABLE_JSON_DATA_EXCHANGE + " (" + 
			COLUMN_JSON_ID     +  " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			COUMN_JSON_DATE    +  " TEXT, " +
			COUMN_JSON_CONTENT +  " TEXT, " +
			COUMN_JSON_EXTRA   +  " TEXT, " +
			COUMN_JSON_IMAGE   +  " BLOB);";
	
	public static final String DATABASE_NAME = "transformations.db";
	public static final int DATABASE_VERSION = 1;
	
	public static final String[] columns = {COLUMN_ID,COLUMN_BUNDLE_ID,COLUMN_TRANSFORMATION_NAME,
		COLUMN_PRODUCED_EVENT_TYPE};
	
	public static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_LOCAL_TRANSFORMATIONS + " (" + 
			COLUMN_ID +	" INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			COLUMN_BUNDLE_ID+ " INTEGER, " +
			COLUMN_TRANSFORMATION_NAME+	" TEXT, " + 
			COLUMN_PRODUCED_EVENT_TYPE+	" TEXT, " +
			COLUMN_REQUIRED_EVENT_TYPES+	" TEXT, " +
			COLUMN_TRANSFORMATION_COSTS+ " INTEGER);";

	public static final String TRAFFIC_MON_CREATE = "CREATE TABLE "
			+ TABLE_TRAFFIC_MON + " (" + 
			COLUMN_ID +	" INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			COLUMN_DATE_TEXT+ " TEXT, " +
			COLUMN_TYPE+ " INTEGER, " +
			COLUMN_X_AXIS+	" REAL, " + 
			COLUMN_Y_AXIS+	" REAL);";
	
	public static final String DATE_TO_TRAFFIC = "CREATE TABLE "
			+ TABLE_DATE_TO_TRAFFIC + " (" + 
			COLUMN_DATE_ID +	" INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			COLUMN_TRAFFIC_ID+ " INTEGER, " +
			COLUMN_DATE_TEXT+ " TEXT);";
	
	
	public LocalTransformationDB(Context context) {
		super(context, DATABASE_NAME,null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i(TAG, "creating database tabelle");
		database.execSQL(DATABASE_CREATE);
		database.execSQL(TRAFFIC_MON_CREATE);
		database.execSQL(DATE_TO_TRAFFIC);
		database.execSQL(JSON_DATA_EXCHANGE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version "+ oldVersion+ " to "+ newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL_TRANSFORMATIONS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAFFIC_MON);
		database.execSQL("DROP TABLE IF EXISTS " + DATE_TO_TRAFFIC);
		database.execSQL("DROP TABLE IF EXISTS " + JSON_DATA_EXCHANGE);
		onCreate(database);
	}

}
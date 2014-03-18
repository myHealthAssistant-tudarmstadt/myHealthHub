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

	public LocalTransformationDB(Context context) {
		super(context, DATABASE_NAME,null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i(TAG, "creating database tabelle");
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version "+ oldVersion+ " to "+ newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL_TRANSFORMATIONS);
		onCreate(database);
	}

}
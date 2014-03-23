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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalTransformationDBMS {

	private static String TAG = "LocalTransformationDBMS";
	private static boolean D = false;

	private SQLiteDatabase database;
	private LocalTransformationDB dbHelper;

	public LocalTransformationDBMS(Context context) {
		dbHelper = new LocalTransformationDB(context);
	}

	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean addTransformation(long bundleId, String transformationName, String producedEventType,
			List<String> requiredEventTypes, int costs) {
		// generate semicolon separated list of required event types
		String requiredEvents = "";
		for(String type : requiredEventTypes) {
			requiredEvents += type+";";
		}
		
		ContentValues values = new ContentValues();
		values.put(LocalTransformationDB.COLUMN_BUNDLE_ID, bundleId);
		values.put(LocalTransformationDB.COLUMN_TRANSFORMATION_NAME, transformationName);
		values.put(LocalTransformationDB.COLUMN_PRODUCED_EVENT_TYPE, producedEventType);
		values.put(LocalTransformationDB.COLUMN_REQUIRED_EVENT_TYPES, requiredEvents);
		values.put(LocalTransformationDB.COLUMN_TRANSFORMATION_COSTS, costs);
		long insertId = database.insert(LocalTransformationDB.TABLE_LOCAL_TRANSFORMATIONS,
				null, values);
		return insertId != -1;
	}

	private Transformation cursorToTransformation(Cursor cursor, int position) {

		if (cursor.moveToPosition(position)) {

			Transformation transformation = new Transformation(
					cursor.getLong(cursor.getColumnIndex(LocalTransformationDB.COLUMN_BUNDLE_ID)),
					cursor.getString(cursor.getColumnIndex(LocalTransformationDB.COLUMN_TRANSFORMATION_NAME)),
					cursor.getString(cursor.getColumnIndex(LocalTransformationDB.COLUMN_PRODUCED_EVENT_TYPE)),
					cursor.getInt(cursor.getColumnIndex(LocalTransformationDB.COLUMN_TRANSFORMATION_COSTS)));
			
			String requiredEventTypes = cursor.getString(cursor.getColumnIndex(LocalTransformationDB.COLUMN_REQUIRED_EVENT_TYPES));
			String[] types = requiredEventTypes.split(";");			
			for(String type : types) {
				if(D)Log.d(TAG, "required event type: "+type);
				transformation.addRequiredEvent(type);
			}
			
			return transformation;

		} else
			return null;
	}
	
	public ArrayList<Transformation> getAvailableTransformations() {
		ArrayList<Transformation> transformations = new ArrayList<Transformation>();
		
		if(D)Log.i(TAG, " quering for all transformations");
		
		Cursor cursor = database.query(
				LocalTransformationDB.TABLE_LOCAL_TRANSFORMATIONS, 
				null, null, null, null, null, null);
		
		Transformation tempTrans;
		for(int i = 0; i < cursor.getCount(); i++) {
			tempTrans = cursorToTransformation(cursor, i);
			if(tempTrans!=null) transformations.add(tempTrans);
		}		
		
		return transformations;
	}

	/**
	 * @param name
	 */
	public int deleteTransformation(long name) {
		return database.delete(LocalTransformationDB.TABLE_LOCAL_TRANSFORMATIONS, 
				LocalTransformationDB.COLUMN_BUNDLE_ID+" = ?", new String[] {name+""});	
	}
}
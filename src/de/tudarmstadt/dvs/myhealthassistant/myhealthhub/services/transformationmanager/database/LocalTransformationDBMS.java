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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.JSONDataExchange;
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

	public boolean addTransformation(long bundleId, String transformationName,
			String producedEventType, List<String> requiredEventTypes, int costs) {
		// generate semicolon separated list of required event types
		String requiredEvents = "";
		for (String type : requiredEventTypes) {
			requiredEvents += type + ";";
		}

		ContentValues values = new ContentValues();
		values.put(LocalTransformationDB.COLUMN_BUNDLE_ID, bundleId);
		values.put(LocalTransformationDB.COLUMN_TRANSFORMATION_NAME,
				transformationName);
		values.put(LocalTransformationDB.COLUMN_PRODUCED_EVENT_TYPE,
				producedEventType);
		values.put(LocalTransformationDB.COLUMN_REQUIRED_EVENT_TYPES,
				requiredEvents);
		values.put(LocalTransformationDB.COLUMN_TRANSFORMATION_COSTS, costs);
		long insertId = database
				.insert(LocalTransformationDB.TABLE_LOCAL_TRANSFORMATIONS,
						null, values);
		return insertId != -1;
	}

	private Transformation cursorToTransformation(Cursor cursor, int position) {

		if (cursor.moveToPosition(position)) {

			Transformation transformation = new Transformation(
					cursor.getLong(cursor
							.getColumnIndex(LocalTransformationDB.COLUMN_BUNDLE_ID)),
					cursor.getString(cursor
							.getColumnIndex(LocalTransformationDB.COLUMN_TRANSFORMATION_NAME)),
					cursor.getString(cursor
							.getColumnIndex(LocalTransformationDB.COLUMN_PRODUCED_EVENT_TYPE)),
					cursor.getInt(cursor
							.getColumnIndex(LocalTransformationDB.COLUMN_TRANSFORMATION_COSTS)));

			String requiredEventTypes = cursor
					.getString(cursor
							.getColumnIndex(LocalTransformationDB.COLUMN_REQUIRED_EVENT_TYPES));
			String[] types = requiredEventTypes.split(";");
			for (String type : types) {
				if (D)
					Log.d(TAG, "required event type: " + type);
				transformation.addRequiredEvent(type);
			}

			return transformation;

		} else
			return null;
	}

	public ArrayList<Transformation> getAvailableTransformations() {
		ArrayList<Transformation> transformations = new ArrayList<Transformation>();

		if (D)
			Log.i(TAG, " quering for all transformations");

		Cursor cursor = database.query(
				LocalTransformationDB.TABLE_LOCAL_TRANSFORMATIONS, null, null,
				null, null, null, null);

		Transformation tempTrans;
		for (int i = 0; i < cursor.getCount(); i++) {
			tempTrans = cursorToTransformation(cursor, i);
			if (tempTrans != null)
				transformations.add(tempTrans);
		}

		cursor.close();
		return transformations;
	}

	/**
	 * @param name
	 */
	public int deleteTransformation(long name) {
		return database.delete(
				LocalTransformationDB.TABLE_LOCAL_TRANSFORMATIONS,
				LocalTransformationDB.COLUMN_BUNDLE_ID + " = ?",
				new String[] { name + "" });
	}

	public boolean addTraffic(String date, int trafficType, double xValue,
			double yValue) {

		ContentValues values = new ContentValues();
		values.put(LocalTransformationDB.COLUMN_DATE_TEXT, date);
		values.put(LocalTransformationDB.COLUMN_TYPE, trafficType);
		values.put(LocalTransformationDB.COLUMN_X_AXIS, xValue);
		values.put(LocalTransformationDB.COLUMN_Y_AXIS, yValue);
		long insertId = database.insert(
				LocalTransformationDB.TABLE_TRAFFIC_MON, null, values);
		if (insertId == -1) {
			Log.e(TAG, "addTraffic failed at:[" + date + "; type:"
					+ trafficType + "; " + xValue + "; " + yValue + "]");
		}
		return insertId != -1;
	}

	public boolean addDateOfTraffic(String date, int trafficId) {

		ContentValues values = new ContentValues();
		values.put(LocalTransformationDB.COLUMN_TRAFFIC_ID, trafficId);
		values.put(LocalTransformationDB.COLUMN_DATE_TEXT, date);
		long insertId = database.insert(
				LocalTransformationDB.TABLE_DATE_TO_TRAFFIC, null, values);
		if (insertId == -1) {
			Log.e(TAG, "addTraffic failed at:[" + date + "]");
		}
		return insertId != -1;
	}

	public ArrayList<TrafficData> getAllTrafficFromDate(String date, int typeID) {
		ArrayList<TrafficData> list = new ArrayList<TrafficData>();
		String q = "SELECT * FROM "
				+ LocalTransformationDB.TABLE_TRAFFIC_MON
				// + ";";
				+ " where( " + LocalTransformationDB.COLUMN_DATE_TEXT
				+ " like '" + date + "%' AND "
				+ LocalTransformationDB.COLUMN_TYPE + " = " + typeID + ")"
				+ " ORDER BY " + LocalTransformationDB.COLUMN_ID + ";";
		Cursor cursor = database.rawQuery(q, null);
		if (cursor.moveToFirst()) {
			do {
				TrafficData trafficData = new TrafficData(
						cursor.getString(cursor
								.getColumnIndex(LocalTransformationDB.COLUMN_DATE_TEXT)),
						cursor.getInt(cursor
								.getColumnIndex(LocalTransformationDB.COLUMN_TYPE)),
						cursor.getDouble(cursor
								.getColumnIndex(LocalTransformationDB.COLUMN_X_AXIS)),
						cursor.getDouble(cursor
								.getColumnIndex(LocalTransformationDB.COLUMN_Y_AXIS)));
				list.add(trafficData);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public ArrayList<String> getAllAvalDate() {
		ArrayList<String> list = new ArrayList<String>();
		String q = "SELECT * FROM "
				+ LocalTransformationDB.TABLE_DATE_TO_TRAFFIC + " ORDER BY "
				+ LocalTransformationDB.COLUMN_DATE_ID + ";";
		Cursor cursor = database.rawQuery(q, null);
		if (cursor.moveToFirst()) {
			do {
				String date = cursor
						.getString(cursor
								.getColumnIndex(LocalTransformationDB.COLUMN_DATE_TEXT));
				if (!list.contains(date))
					list.add(date);
			} while (cursor.moveToNext());
		}
		cursor.close();

		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				int compareResult = 0;
				try {
					Date arg0Date = format.parse(arg0);
					Date arg1Date = format.parse(arg1);
					compareResult = arg0Date.compareTo(arg1Date);
				} catch (ParseException e) {
					e.printStackTrace();
					compareResult = arg0.compareTo(arg1);
				}
				return compareResult;
			}
		});

		for (String s : list) {
			Log.e(TAG, "AvalDate:" + s);
		}
		return list;
	}

	public void storeJsonData(ArrayList<ContentValues> vArray) {
		for (ContentValues values : vArray) {
			long insertId = database.insert(
					LocalTransformationDB.TABLE_JSON_DATA_EXCHANGE, null,
					values);
			if (insertId == -1) {
				Log.e(TAG, "addJsonData failed at:[" + values.toString() + "]");
			}
		}
	}

	public void editJsonData(int id, ContentValues content) {
		int nrRows = database.update(LocalTransformationDB.TABLE_JSON_DATA_EXCHANGE,
				content, LocalTransformationDB.COLUMN_JSON_ID + "=?",
				new String[] { String.valueOf(id) });
		
		Log.e(TAG, "editJsonData, nrOfRowsEffect=" + nrRows);
	}
	
	public void deleteJsonData(int id) {
		int nrRows = database.delete(LocalTransformationDB.TABLE_JSON_DATA_EXCHANGE,
				LocalTransformationDB.COLUMN_JSON_ID + "=?",
				new String[] { String.valueOf(id) });
		
		Log.e(TAG, "deleteJsonData, nrOfRowsEffect=" + nrRows);
	}

	public JSONArray getAlljsonData() {
		JSONArray jArray = new JSONArray();
		String q = "SELECT * FROM "
				+ LocalTransformationDB.TABLE_JSON_DATA_EXCHANGE + " ORDER BY "
				+ LocalTransformationDB.COLUMN_JSON_ID + ";";
		Cursor cursor = database.rawQuery(q, null);
		if (cursor.moveToFirst()) {
			do {
				String contents = cursor
						.getString(cursor
								.getColumnIndex(LocalTransformationDB.COUMN_JSON_CONTENT));
				int id = cursor.getInt(cursor
						.getColumnIndex(LocalTransformationDB.COLUMN_JSON_ID));
				try {
					JSONObject jObj = new JSONObject(contents);
					jObj.put(JSONDataExchange.JSON_CONTENT_ID, id);
					jArray.put(jObj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		return jArray;
	}

	public void deleteAllTrafficRecords() {
		// drop and recreate table
		database.execSQL("DROP TABLE IF EXISTS "
				+ LocalTransformationDB.TABLE_TRAFFIC_MON);
		database.execSQL(LocalTransformationDB.TRAFFIC_MON_CREATE);
		database.execSQL("DROP TABLE IF EXISTS "
				+ LocalTransformationDB.TABLE_DATE_TO_TRAFFIC);
		database.execSQL(LocalTransformationDB.DATE_TO_TRAFFIC);
	}

	public int deleteAllTrafficFromDate(String date) {
		database.delete(LocalTransformationDB.TABLE_DATE_TO_TRAFFIC,
				LocalTransformationDB.COLUMN_DATE_TEXT + " like '" + date
						+ "%'", null);
		return database.delete(LocalTransformationDB.TABLE_TRAFFIC_MON,
				LocalTransformationDB.COLUMN_DATE_TEXT + " like '" + date
						+ "%'", null);
	}
}
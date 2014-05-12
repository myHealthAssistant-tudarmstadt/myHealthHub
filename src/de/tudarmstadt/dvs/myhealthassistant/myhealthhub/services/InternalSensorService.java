/* Copyright (C) 2014 TU Darmstadt, Hessen, Germany.
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

package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw.AmbientLightEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw.AmbientPressureEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw.ProximityEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw.TemperatureEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventKnee;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.GyroscopeSensorEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.MagneticSensorEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.graphActivities.Coordinate;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDBMS;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * 
 * @author HieuHa
 * 
 *         A service that listens to SensorEvent, and sends the received Events
 *         out (advertises those for any subscription)
 */
public class InternalSensorService extends Service implements
		SensorEventListener {

	private static final String TAG = InternalSensorService.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private SharedPreferences pref;
	private static final long timespan = DateUtils.MINUTE_IN_MILLIS; // FIXME if
																		// needed

	@Override
	public void onCreate() {
		// The service is being created
		Log.e(TAG, "onCreate");
		lastAddedLightData = "";
		readingLightValue = 0.0d;
		lightDataCounter = 1;
		listLightData = new ArrayList<Coordinate>();

		lastAddedAccData = "";
		listAccData = new ArrayList<Coordinate>();
		accValues = new ArrayList<Double>();

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (pref.getBoolean(getResources().getString(R.string.in_acc), false)) {
			Sensor mSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (pref.getBoolean(getResources().getString(R.string.in_lig), false)) {
			Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (pref.getBoolean(getResources().getString(R.string.in_pres), false)) {
			Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (pref.getBoolean(getResources().getString(R.string.in_prox), false)) {
			Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (pref.getBoolean(getResources().getString(R.string.in_mag), false)) {
			Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (pref.getBoolean(getResources().getString(R.string.in_gyrs), false)) {
			Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind Service");
		return iservicestub;
	}

	private IMyHealthHubRemoteService.Stub iservicestub = new IMyHealthHubRemoteService.Stub() {

		@Override
		public int getStatus() throws RemoteException {
			// Write here, code to be executed in background
			Log.d(TAG, "Hello World From Remote Service!!");
			return 0;
		}
	};

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnBind Service");
		// All clients unbound
		// super.onUnbind(intent);

		return true;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		if (mSensorManager != null) {
			// unregister all sensors
			mSensorManager.unregisterListener(this);
		} else
			Log.e(TAG, "mSensorManager is null!");

		// The service is no longer used and is being destroyed
		// stopForeground(true);

		super.onDestroy();
	}

	// ###############################################################################
	// SensorListerner Side
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// not needed
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (pref == null)
			pref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

		int mSensorType = event.sensor.getType();
		if (pref.getBoolean(getResources().getString(R.string.in_acc), false)
				|| pref.getBoolean(getResources().getString(R.string.in_lig),
						false)) {
			if (mSensorType == Sensor.TYPE_ACCELEROMETER) {

				int x = Math.round(event.values[0]);
				int y = Math.round(event.values[1]);
				int z = Math.round(event.values[2]);

				// using ACCELEROMETER_KNEE only as a demonstration,
				// in practice new event can be created
				AccSensorEventKnee accEvt = new AccSensorEventKnee("eventID",
						getTimestamp(), "producerID",
						SensorReadingEvent.ACCELEROMETER_KNEE, getTimestamp(),
						x, y, z, x, y, z);

				sendToChannel(accEvt, AbstractChannel.RECEIVER); // for external
																	// apps
																	// subscription
				gotAccEvent(getTimestamp(), x, y, z); // for internal graphViews
														// using
			}

			else if (mSensorType == Sensor.TYPE_LIGHT) {

				float x = event.values[0];

				// using ambientLightEvent only as a demonstration,
				// in practice new event can be created
				AmbientLightEvent lightEvnt = new AmbientLightEvent("eventID",
						getTimestamp(), "producerID",
						SensorReadingEvent.AMBIENT_LIGHT, getTimestamp(),
						"location", "object", x, AmbientLightEvent.UNIT_LUX);
				sendToChannel(lightEvnt, AbstractChannel.RECEIVER);
				gotLightEvent(getTimestamp(), x);

			}

			else if (mSensorType == Sensor.TYPE_PRESSURE) {
				float x = event.values[0];
				AmbientPressureEvent temEvnt = new AmbientPressureEvent(
						"eventID", getTimestamp(), "producerID",
						SensorReadingEvent.AMBIENT_PRESSURE, getTimestamp(),
						"location", "object", x, TemperatureEvent.UNIT_CELSIUS);
				sendToChannel(temEvnt, AbstractChannel.RECEIVER);
			}
			else if (mSensorType == Sensor.TYPE_PROXIMITY) {
				float x = event.values[0];
				ProximityEvent evnt = new ProximityEvent(
						"eventID", getTimestamp(), "producerID",
						SensorReadingEvent.PROXIMITY, getTimestamp(),
						"location", "object", x, TemperatureEvent.UNIT_CELSIUS);
				sendToChannel(evnt, AbstractChannel.RECEIVER);
			}

			else if (mSensorType == Sensor.TYPE_GYROSCOPE) {
				int x = Math.round(event.values[0]);
				int y = Math.round(event.values[1]);
				int z = Math.round(event.values[2]);

				GyroscopeSensorEvent gSEvnt = new GyroscopeSensorEvent("eventID",
						getTimestamp(), "producerID",
						SensorReadingEvent.GYROSCOPE, getTimestamp(),
						x, y, z, x, y, z);
				sendToChannel(gSEvnt, AbstractChannel.RECEIVER);

			}

			else if (mSensorType == Sensor.TYPE_MAGNETIC_FIELD) {
				int x = Math.round(event.values[0]);
				int y = Math.round(event.values[1]);
				int z = Math.round(event.values[2]);

				MagneticSensorEvent gSEvnt = new MagneticSensorEvent("eventID",
						getTimestamp(), "producerID",
						SensorReadingEvent.MAGNETIC_FIELD, getTimestamp(),
						x, y, z, x, y, z);
				sendToChannel(gSEvnt, AbstractChannel.RECEIVER);

			}
		}
	}

	private String getTimestamp() {
		return (String) android.text.format.DateFormat.format(dateFormat,
				new java.util.Date());
	}

	private void sendToChannel(Event evt, String channel) {
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
		i.setAction(channel);
		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(i);
	}

	// ###############################################################################
	// working with SensorEvent
	// normally a new app can subscript to myHealthHub to receive these events

	private static final String dateFormat = "yyyy-MM-dd_kk:mm:ss";
	private static String lastAddedLightData = "";
	private double readingLightValue;
	private int lightDataCounter;
	private ArrayList<Coordinate> listLightData;

	private static String lastAddedAccData = "";
	private ArrayList<Coordinate> listAccData = new ArrayList<Coordinate>();

	private void gotLightEvent(String dateOfMeasurement, float value) {
		if (lastAddedLightData == null || lastAddedLightData.isEmpty())
			lastAddedLightData = dateOfMeasurement;
		else {
			if (timeDiff(lastAddedLightData, dateOfMeasurement, timespan)) {
				// after each min the added up data being divided by
				// average and stored
				double yValue = readingLightValue / lightDataCounter;
				String xValue = dateOfMeasurement;

				listLightData.add(new Coordinate(xValue, yValue));

				// store to dbs each 10 min (when size of list reach xxx0)
				if (listLightData.size() % 10 == 0) {
					addTrafficToDB(dateOfMeasurement, Sensor.TYPE_LIGHT,
							listLightData); // add only 10 data to dbs
					listLightData = new ArrayList<Coordinate>();
				}
				// the data being reset after that
				readingLightValue = Double.parseDouble(Float.toString(value));
				lightDataCounter = 1;

				lastAddedLightData = dateOfMeasurement;
			} else {
				// within a minute value being added up only
				readingLightValue += Double.parseDouble(Float.toString(value));
				lightDataCounter++;

			}
		}
	}

	private ArrayList<Double> accValues = new ArrayList<Double>();

	private void gotAccEvent(String dateOfMeasurement, int x, int y, int z) {
		if (lastAddedAccData == null || lastAddedAccData.isEmpty()) {
			lastAddedAccData = dateOfMeasurement;
		} else {
			if (timeDiff(lastAddedAccData, dateOfMeasurement, timespan)) {

				String xValue = dateOfMeasurement;
				double mean = 0.0d;
				double variance = 0.0d;
				int n = accValues.size();
				for (double b : accValues) {
					mean += b;
				}
				mean = mean / n;
				for (double b : accValues) {
					variance += (mean - b) * (mean - b);
				}
				variance = variance / n;

				double yValue = variance;
				// double yValue = Math.sqrt(variance); // standard Deviation
				listAccData.add(new Coordinate(xValue, yValue));

				// store to dbs each 10 min (when size of list reach 10)
				if (listAccData.size() % 10 == 0) { //
					addTrafficToDB(dateOfMeasurement,
							Sensor.TYPE_ACCELEROMETER, listAccData);
					listAccData = new ArrayList<Coordinate>();
				}

				// the data being reset after each min
				accValues = new ArrayList<Double>();

				lastAddedAccData = dateOfMeasurement;
			} else {
				// within a minute value being added up only
				// readingAccValue += Math.sqrt(x * x + y * y + z * z);
				accValues.add(Math.sqrt(x * x + y * y + z * z));
			}
		}
	}

	/**
	 * calculate the difference in minutes between two dates in millisecond
	 * 
	 * @param first
	 * @param second
	 * @param timespan
	 *            : the different of (e.g, ONE minute, ONE day...)
	 * @return true if two dates are difference by time span
	 */
	public boolean timeDiff(String firstDate, String secondDate, long timespan) {
		SimpleDateFormat dfDate = new SimpleDateFormat(dateFormat);

		try {
			Date first = dfDate.parse(firstDate);
			Date second = dfDate.parse(secondDate);

			long diff = ((second.getTime() / timespan) - (first.getTime() / timespan));

			return (diff < 1) ? false : true;

		} catch (ParseException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		return false;
	}

	private LocalTransformationDBMS transformationDB;

	// private String currentDate = "";
	// public void addTrafficToDB(String timeOfMeasurement, String type,
	// ArrayList<Coordinate> listData) {
	// String date = "";
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_kk:mm:ss");
	//
	// try {
	// Date today = sdf.parse(timeOfMeasurement);
	// sdf.applyPattern("dd-MM-yyyy");
	//
	// date = sdf.format(today);
	//
	// } catch (ParseException e) {
	// e.printStackTrace();
	// Log.e(TAG, e.toString());
	// }
	//
	// Log.e(TAG, timeOfMeasurement + "-date=" + date + " type:" + type);
	// transformationDB = new
	// LocalTransformationDBMS(this.getApplicationContext());
	// transformationDB.open();
	// // add to traffic table
	// for (int i = 0; i < listData.size(); i++) {
	// Coordinate d = listData.get(i);
	// transformationDB.addTraffic(date, type, d.getX(), d.getY());
	// }
	// // add to date table
	// if (currentDate.isEmpty()){
	// currentDate = timeOfMeasurement;
	// transformationDB.addDateOfTraffic(date, 0);
	// } else {
	// if (timeDiff(currentDate, timeOfMeasurement, DateUtils.DAY_IN_MILLIS)){
	// // if currentDate is differ by one day, format and add it to date table
	// try {
	// Date today = sdf.parse(currentDate);
	// sdf.applyPattern("dd-MM-yyyy");
	// String newDate = sdf.format(today);
	// transformationDB.addDateOfTraffic(newDate, 0);
	// } catch (ParseException e) {
	// e.printStackTrace();
	// Log.e(TAG, e.toString());
	// }
	// currentDate = timeOfMeasurement;
	// }
	// }
	// transformationDB.close();
	// }

	private void addTrafficToDB(String timeOfMeasurement, int typeID,
			ArrayList<Coordinate> listData) {

		ArrayList<String> dateToAdd = new ArrayList<String>();

		transformationDB = new LocalTransformationDBMS(
				this.getApplicationContext());
		transformationDB.open();
		ArrayList<String> databaseListData = transformationDB.getAllAvalDate();
		// add to traffic table
		for (Coordinate d : listData) {
			String date = getDayFromDate(d.getX(), dateFormat, "dd-MM-yyyy");
			double xDouble = convertTimeToDouble(d.getX(), dateFormat, "kk.mm");
			transformationDB.addTraffic(date, typeID, xDouble, d.getY());

			Log.e(TAG,
					"addTraffic: " + date + " -- " + xDouble + " - " + d.getY());
			if (!databaseListData.contains(date) && !dateToAdd.contains(date)) {
				dateToAdd.add(date);
			}
		}

		for (String s : dateToAdd) {
			transformationDB.addDateOfTraffic(s, -1);
		}
		transformationDB.close();
	}

	private String getDayFromDate(String timeOfMeasurement, String dateFormat,
			String applyPattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		try {
			Date today = sdf.parse(timeOfMeasurement);
			sdf.applyPattern(applyPattern);

			return sdf.format(today);

		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		return "";
	}

	private static double convertTimeToDouble(String fullTime,
			String dateFormat, String applyPattern) {
		SimpleDateFormat fullDate = new SimpleDateFormat(dateFormat);
		SimpleDateFormat timeDate = new SimpleDateFormat(applyPattern);

		try {
			Date now = fullDate.parse(fullTime);
			String strDate = timeDate.format(now);
			double parseDate = Double.parseDouble(strDate);
			if (parseDate >= 24.00d)
				return parseDate - 24;
			return parseDate;

		} catch (ParseException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		return Double.parseDouble("00.00");
	}
}

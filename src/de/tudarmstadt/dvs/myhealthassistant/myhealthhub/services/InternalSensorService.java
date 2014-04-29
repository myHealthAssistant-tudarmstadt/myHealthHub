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

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.MyHealthHubWithFragments;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter.InternalSensorListAdapter;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw.AmbientLightEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventKnee;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
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

	// @Override
	// public void onCreate() {
	// // The service is being created
	//
	// }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// The service is starting, due to a call to startService()

		// A client wants to listen to a Sensor Type
		if (intent == null)
			return 0;
		Bundle extras = intent.getExtras();
		if (extras != null)
			if (extras.containsKey(InternalSensorListAdapter.PREF_SENSOR_TYPE)) {
				mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

				int mSensorType = extras
						.getInt(InternalSensorListAdapter.PREF_SENSOR_TYPE);

				Sensor mSensor = mSensorManager.getDefaultSensor(mSensorType);
				mSensorManager.registerListener(this, mSensor,
						SensorManager.SENSOR_DELAY_NORMAL);

				// Notice User about this
				return systemNotice();
			} else
				Log.e(TAG, "contains no extra SensorType");

		return 0;
	}

	private int systemNotice() {
		int t = START_STICKY;
		Log.e(TAG, "call me redundant BABY!  onStartCommand service");

		int myID = android.os.Process.myPid();
		// The intent to launch when the user clicks the expanded notification
		Intent intent = new Intent(this, MyHealthHubWithFragments.class);
		// Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent
				.getActivity(this, 0, intent, 0);

		Notification notice = new Notification.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(getPackageName())
				.setContentText("Start advertising events")
				.setContentIntent(pendIntent).getNotification();

		notice.flags |= Notification.FLAG_AUTO_CANCEL;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(myID, notice);

		return t;
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
		if (mSensorManager != null) {
			// unregister all sensors
			mSensorManager.unregisterListener(this);
		} else
			Log.e(TAG, "mSensorManager is null!");

		// The service is no longer used and is being destroyed
		stopForeground(true);

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
		int mSensorType = event.sensor.getType();
		if (mSensorType == Sensor.TYPE_ACCELEROMETER
				|| mSensorType == Sensor.TYPE_MAGNETIC_FIELD
				|| mSensorType == Sensor.TYPE_GRAVITY
				|| mSensorType == Sensor.TYPE_GYROSCOPE
				|| mSensorType == Sensor.TYPE_LINEAR_ACCELERATION) {

			int x = Math.round(event.values[0]);
			int y = Math.round(event.values[1]);
			int z = Math.round(event.values[2]);

			// using ACCELEROMETER_KNEE only as a demonstration,
			// in practice new event can be created
			AccSensorEventKnee accEvt = new AccSensorEventKnee("eventID",
					getTimestamp(), "producerID",
					SensorReadingEvent.ACCELEROMETER_KNEE, getTimestamp(), x,
					y, z, x, y, z);

			sendToChannel(accEvt, AbstractChannel.RECEIVER);
		}

		else if (mSensorType == Sensor.TYPE_LIGHT
				|| mSensorType == Sensor.TYPE_PRESSURE
				|| mSensorType == Sensor.TYPE_PROXIMITY
				|| mSensorType == Sensor.TYPE_RELATIVE_HUMIDITY) {

			float x = event.values[0];

			// using ambientLightEvent only as a demonstration,
			// in practice new event can be created
			AmbientLightEvent lightEvnt = new AmbientLightEvent("eventID",
					getTimestamp(), "producerID",
					SensorReadingEvent.AMBIENT_LIGHT, getTimestamp(),
					"location", "object", x, AmbientLightEvent.UNIT_LUX);
			sendToChannel(lightEvnt, AbstractChannel.RECEIVER);

		}

	}

	public String getTimestamp() {
		return (String) android.text.format.DateFormat.format(
				"yyyy-MM-dd_hh:mm:ss", new java.util.Date());
	}

	private void sendToChannel(Event evt, String channel) {
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
		i.setAction(channel);
		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(i);
	}
}

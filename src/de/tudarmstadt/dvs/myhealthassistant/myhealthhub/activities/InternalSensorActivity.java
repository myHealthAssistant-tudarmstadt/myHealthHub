package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw.AmbientLightEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventKnee;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventWrist;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class InternalSensorActivity implements SensorEventListener {

	private static final String TAG = InternalSensorActivity.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mSensorType;
	private Activity activity;
	private boolean isStarted;

	public InternalSensorActivity(){
		
	}
	
	public InternalSensorActivity(int sensorType, Activity activity) {
		this.activity = activity;
		mSensorType = sensorType;
		mSensorManager = (SensorManager) activity
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(sensorType);
		isStarted = false;
	}

	public void onStart() {
		isStarted = true;
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
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
		LocalBroadcastManager.getInstance(activity.getApplicationContext())
				.sendBroadcast(i);
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// mSensorManager.registerListener(this, mSensor,
	// SensorManager.SENSOR_DELAY_NORMAL);
	// }

	public void onStop() {
		isStarted = false;
		mSensorManager.unregisterListener(this);
	}

	public boolean isStated() {
		return isStarted;
	}

}

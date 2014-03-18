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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.examples;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.notifications.NotificationEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.WeightEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.WeightEventInKg;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.activity.ActivityEventSSWRC;

public class ActivityHRtoAlarm {

	// Debug
	private static String TAG = "ActivityHRtoAlarm";
	private boolean D = false;

	private ReadingEventReceiver mReadingEventReceiver;

	private Context ctx;

	private String lastActivity;

	private int eventIndex;

	private int[] thesholdIdle = { 40, 90 };
	private int[] thesholdModerade = { 50, 130 };
	private int[] thesholdSports = { 100, 170 };

	// Roger: onStart
	public ActivityHRtoAlarm(Context ctx) {
		if (D)
			Log.d(TAG, TAG + " is started.");

		this.ctx = ctx;

		// Register event receiver
		mReadingEventReceiver = new ReadingEventReceiver();
		ctx.getApplicationContext().registerReceiver(mReadingEventReceiver,
				new IntentFilter(SensorReadingEvent.HEART_RATE));
		ctx.getApplicationContext().registerReceiver(mReadingEventReceiver,
				new IntentFilter(SensorReadingEvent.ACTIVITY));

		eventIndex = 0;
		lastActivity = "";
	}

	// Roger: onStop
	public void onStop() {
		ctx.getApplicationContext().unregisterReceiver(mReadingEventReceiver);
	}

	/** Event receiver implemented as a Android BroadcastReceiver */
	private class ReadingEventReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			/* Get event type and the event itself */
			String eventType = intent
					.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE);

			if (D)
				Log.d(TAG, "Incoming event of type " + eventType);

			if (eventType.equals(HeartRateEvent.EVENT_TYPE)) {
				HeartRateEvent evt = (HeartRateEvent) intent
						.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);

				// Trigger check
				isHeartRateOK(evt);
			} else if (eventType.equals(ActivityEventSSWRC.EVENT_TYPE)) {
				lastActivity = ((ActivityEventSSWRC) intent
						.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT)).getActivityName();
			}
		};
	}

	public void isHeartRateOK(HeartRateEvent evt) {
		// TODO Check time difference...

		// w/o time difference
		int hr = evt.getValue();
		if (lastActivity.equals(ActivityEventSSWRC.SITTING_NAME)
				|| lastActivity.equals(ActivityEventSSWRC.STANDING_NAME)) {
			if (hr < thesholdIdle[0] || hr > thesholdIdle[1]) {
				//sendAlarm(hr, lastActivity);
			}
		} else if (lastActivity.equals(ActivityEventSSWRC.WALKING_NAME)
				|| lastActivity.equals(ActivityEventSSWRC.CYCLING_NAME)) {
			if (hr < thesholdModerade[0] || hr > thesholdModerade[1]) {
				//sendAlarm(hr, lastActivity);
			}
		} else if (lastActivity.equals(ActivityEventSSWRC.CYCLING_NAME)) {
			if (hr < thesholdSports[0] || hr > thesholdSports[1]) {
				//sendAlarm(hr, lastActivity);
			}
		}
		
		/* generate event */
		WeightEvent evt2 = new WeightEventInKg(TAG+"_Weight", "abcdef",
				"EventGenerator", "EventGenerator", "b",10);
		
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt2.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt2);
		i.setAction(AbstractChannel.RECEIVER);
		ctx.sendBroadcast(i);
	}

	private void sendAlarm(int hr, String activity) {
		// Create Alarm Event
		NotificationEvent alarmEvent = new NotificationEvent(
				"ActivtyHRtoAlarm_" + getIndex(),
				(String) android.text.format.DateFormat.format(
						"yyyy-MM-dd_hh:mm:ss", new java.util.Date()),
				"ActivtyHRtoAlarm_Transformation",
				NotificationEvent.SEVERITY_CRITICAL, "Severe heart rate!",
				"Heart rate of " + hr + " while " + activity);

		// Send event
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, alarmEvent.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, alarmEvent);
		i.setAction(AbstractChannel.RECEIVER);
		ctx.sendBroadcast(i);
	}

	private int getIndex() {
		if (eventIndex != Integer.MAX_VALUE) {
			return eventIndex++;
		} else {
			return eventIndex = 0;
		}
	}
}
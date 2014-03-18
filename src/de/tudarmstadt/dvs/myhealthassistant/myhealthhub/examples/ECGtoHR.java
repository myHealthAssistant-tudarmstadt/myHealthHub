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
 
 /**
 * 
 */
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.examples;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.ECGEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;

/**
 * @author chris
 * 
 */
public class ECGtoHR {

	/* Define event channel and event receiver */
	private final IntentFilter readingChannel = new IntentFilter(SensorReadingEvent.ECG_STREAM);
	private ReadingEventReceiver mReadingEventReceiver;
	private Context ctx;

	private static int PEAK_HIGHT_THRES = 30;
	private static int NUMBER_OF_VALUES = 104;

	private String TAG = "ECGtoHR";
	private boolean D = false;

	private int number;
	private String oldID;

	private List<Integer> stream;

	int[] pulseValues;
	int pulseValuesIdx;

	// calc RR-Interval factor
	float factor;

	private int eventIndex;

	private String timeOfMeasurment;

	// Roger: on start
	public ECGtoHR(Context ctx) {
		if (D)
			Log.d(TAG, TAG + " was started and listening on "
					+ SensorReadingEvent.ECG_STREAM);
		this.ctx = ctx;

		mReadingEventReceiver = new ReadingEventReceiver();
		ctx.getApplicationContext().registerReceiver(mReadingEventReceiver,
				readingChannel);
		oldID = "";
		number = 0;

		stream = new LinkedList<Integer>();
		factor = 1f / (2f * NUMBER_OF_VALUES);
		pulseValuesIdx = 0;
		pulseValues = new int[4];

		eventIndex = 0;
	}

	// Roger: on stop
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

			if (eventType.equals(ECGEvent.EVENT_TYPE)) {
				// Handle incoming acceleration event:
				ECGEvent evt = (ECGEvent) intent
						.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
				if (evt.getID() == oldID) {
					if (D)
						Log.d(TAG, "old event");
				} else {
					incomingECGEvent(evt);
					oldID = evt.getID();
					findRRInterval();
					timeOfMeasurment = evt.getTimeOfMeasurement();
				}
			}
		};

		private void incomingECGEvent(ECGEvent evt) {
			int[] values = evt.getEcgValues();
			if (D)
				Log.d(TAG, "#readings: " + values.length);
			number++;
			values = filterValues(values);

			// printValues(values);

			for (int i = 1; i < values.length - 1; i++) {
				if (values[i - 1] < values[i] && values[i] > values[i + 1]) {
					// check difference of peak
					int diff = (values[i] - values[i - 1])
							+ (values[i] - values[i + 1]);
					if (diff >= PEAK_HIGHT_THRES) {
						if (D)
							Log.d(TAG, "Peak found at " + i
									+ " in packet number " + number);
						addPeak(i);
						break;
					}
				}
			}
			addPeak(-1);
		}

		public void findRRInterval() {
			int rrInterval;
			int peak;

			// Search for first peak
			Iterator<Integer> streamIterator = stream.iterator();
			while (streamIterator.hasNext()) {
				peak = (Integer) streamIterator.next();
				if (peak != -1) {
					// first Peak found
					rrInterval = NUMBER_OF_VALUES - peak;

					// Search for second peak
					while (streamIterator.hasNext()) {
						peak = (Integer) streamIterator.next();

						if (peak != -1) {
							// Peak found: calc pulse
							rrInterval += peak;
							calculatePulseFromRR(rrInterval);
							return;
						} else {
							// no peak: add miliseconds
							rrInterval += NUMBER_OF_VALUES;
						}
					}
				}
			}
		}

		private void calculatePulseFromRR(int rrInterval) {
			// calc pulse
			float pulse = 60.0f / ((float) rrInterval * factor);

			// Average pulse
			pulseValues[pulseValuesIdx] = (int) pulse;
			int outputPulse = 0;
			for (int val : pulseValues) {
				outputPulse += val;
			}
			outputPulse = outputPulse / pulseValues.length;

			// increase index
			if (pulseValuesIdx < pulseValues.length - 1) {
				pulseValuesIdx++;
			} else {
				pulseValuesIdx = 0;
			}

			// Create HR Event
			HeartRateEvent heartRateEvent = new HeartRateEvent(
					"ECGtoHR_transformation_" + getIndex(),
					(String) android.text.format.DateFormat.format(
							"yyyy-MM-dd_hh:mm:ss", new java.util.Date()),
					"ECGtoHR_transformation", "ECG sensor", timeOfMeasurment,
					outputPulse);

			// Send event
			Intent i = new Intent();
			i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE,
					heartRateEvent.getEventType());
			i.putExtra(Event.PARCELABLE_EXTRA_EVENT, heartRateEvent);
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

		private int[] filterValues(int[] values) {
			for (int i = 0; i < values.length - 5; i++) {
				values[i] = (values[i] + values[i + 1] + values[i + 2]
						+ values[i + 3] + values[i + 4]) / 5;
			}
			if(D)printValues(values);
			return values;
		}
		
		private void printValues(int[] values) {
			String output = "";
			for(int i : values) {
				output += i+" ";
			}
			Log.d(TAG, "xyz "+output);
		}

		private void addPeak(int pos) {
			// Add to top
			stream.add(0, pos);

			// Remove last element while more than 4
			while (stream.size() >= 5) {
				stream.remove(stream.size() - 1);
			}
		}
	}

}
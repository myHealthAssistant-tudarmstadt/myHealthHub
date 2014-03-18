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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services;

import java.util.LinkedList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;

/**
 * @author Chris
 *
 */
public class EventComposer extends Service {

	private final static int minPulseValue = 55;
	private final static int maxPulseValue = 180;
	
	private final int[] pulseRangesIdle = {55, 160};
	private final int[] pulseRangesRunning = {130, 175};
	private final int[] pulseRangesCycling = {80, 160};
	private final int[] pulseRangesWalking = {55, 160};
	
	/** For LogCat. */
	private static final String TAG = "EventComposer";
	private static boolean D = false;
	
	private final IBinder mEventComposerBinder = new EventComposerBinder();
	private Handler mCallbackHandler;
	private Handler mDBCallbackHandler;
	
	private int heartbeatEventCounter;
	private HeartbeatMonitor heartbeatMonitor;
	private static int BUFFERSIZE_HEARTBEAT_MONITOR = 30;
	
	// workout monitoring
	private boolean isMonitoringGymWorkout;
	
		
	public EventComposer() {
		heartbeatMonitor = new HeartbeatMonitor(BUFFERSIZE_HEARTBEAT_MONITOR);
	}
	
	public class EventComposerBinder extends Binder {
		private static final String TAG = "MessageHandler.LocalSensorHandlerBinder";
		
		public void setActivityCallbackHandler(final Handler callback) {
			mCallbackHandler = callback;
		}
		
		public void setDBCallbackHandler(final Handler callback) {
			mDBCallbackHandler = callback;
		}
		
		public void incomingEvent(SensorReadingEvent event) {
			processIncomingEvent(event);
		}
	}
	

	
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onCreate(): entered...");
		heartbeatEventCounter = 0;
		isMonitoringGymWorkout = false;
		//TODO
		return mEventComposerBinder;
	}
	
	private class HeartbeatMonitor {
		private LinkedList<Integer> heartbeats;
		private int bufferSize;
		
		public HeartbeatMonitor(int bufferSize) {
			heartbeats = new LinkedList<Integer>();
			this.bufferSize = bufferSize;
		}
		
		public void add(HeartRateEvent evt) {
			heartbeats.add(evt.value);
			
			if(heartbeats.size()>bufferSize) {
				heartbeats.removeFirst();
			}	
		}
		
		public float getAvgValue(int numberOfRegardedReadings) {
			if(numberOfRegardedReadings>heartbeats.size())
				numberOfRegardedReadings = heartbeats.size();
			
			float sum = 0;
			for(int i = heartbeats.size()-numberOfRegardedReadings; 
							i<heartbeats.size(); i++) {
				sum += heartbeats.get(i);
			}
			
			if(heartbeats.size()!=0) {
				return (sum/numberOfRegardedReadings);
			} else {
				return 0;
			}
		}
		
		public boolean hasJumpsOf(int numberOfBeats, int numberOfRegardedReadings) {
			if(numberOfRegardedReadings>heartbeats.size())
				numberOfRegardedReadings = heartbeats.size();
			
			int offset = heartbeats.size()-numberOfRegardedReadings;
			
			int prevValue = heartbeats.get(offset);
			for(int i = offset+1; i<heartbeats.size(); i++) {
				if(Math.abs(prevValue-heartbeats.get(i)
						)>=numberOfBeats) return true;
				prevValue = heartbeats.get(i);
			}
			return false;
		}
		
		public boolean hasMinDifferenceOf(int heartbeatDifference, int numberOfRegardedReadings) {
			if(numberOfRegardedReadings>heartbeats.size())
				numberOfRegardedReadings = heartbeats.size();
			
			int offset = heartbeats.size()-numberOfRegardedReadings;
			
			int prevValue = heartbeats.get(offset);
			int diff = 0;
			for(int i = offset+1; i<heartbeats.size(); i++) {
				diff += Math.abs(prevValue-heartbeats.get(i));
				prevValue = heartbeats.get(i);
			}
			
			if(diff >= heartbeatDifference) {
				return true;
			} else {
				return false;	
			}			
		}
	}
	
	private int severity = 0;
	
	private void processIncomingEvent(SensorReadingEvent event){
		if(D)Log.d(TAG, "Incoming Event of type: "+event.getEventType());
		
		if(event instanceof HeartRateEvent) {
			
			HeartRateEvent myEvent = (HeartRateEvent)event;
			
			heartbeatMonitor.add(myEvent);
			
			/* Heart rate check every fifth reading (second) */
			heartbeatEventCounter++;
			if(heartbeatEventCounter != 5) return;
			heartbeatEventCounter = 0;
			
			/* Check whether heart rate is in healthy range */
			if(heartbeatMonitor.getAvgValue(5)<minPulseValue) {
				sendSensorAdjustMessage("Pulse too low");
				severity++;
			} else if(heartbeatMonitor.getAvgValue(5)>maxPulseValue) {
				sendSensorAdjustMessage("Pulse too high");
				severity++;
			/* Check for jumps, peaks */
			} else if(heartbeatMonitor.hasJumpsOf(40, 5)) {
				sendSensorAdjustMessage("High jumps");
			} else if(!heartbeatMonitor.hasMinDifferenceOf(1,30)) {
				sendSensorAdjustMessage("Difference too low");
			} else {
				severity = 0;
			}
			
			/* Send alarm */
			//TODO include severity
			if(severity == 2) sendPulseAlarm(myEvent);
			
			
			
			
			// Check whether pulsevalue is within the general range
			/*if (myEvent.value < minPulseValue ||
					myEvent.value > maxPulseValue) { 
				sendPulseAlarm(myEvent);
				return;
			}*/
			
			
			
			/* sensys
			 *if(mDBMSBinder.isBinderAlive()) {
			 
				Cursor mCursor = mDBMSBinder.showRecentActivies(30);
				
				mCursor.moveToFirst();
				int count = mCursor.getInt(mCursor.getColumnIndex("count"));
				if(count >= 20) {
					String ACTIVITY = mCursor.getString(mCursor.getColumnIndex("ACTIVITY"));
					if(ACTIVITY.equals("standing") || ACTIVITY.equals("sitting")) {
						if(!isPulseInRange(myEvent.value, pulseRangesIdle))
							sendPulseAlarm(myEvent);
					} else if (ACTIVITY.equals("running")) {
						if(!isPulseInRange(myEvent.value, pulseRangesRunning))
							sendPulseAlarm(myEvent);
					} else if (ACTIVITY.equals("walking")) {
						if(!isPulseInRange(myEvent.value, pulseRangesWalking))
							sendPulseAlarm(myEvent);
					} else if (ACTIVITY.equals("cycling")) {
						if(!isPulseInRange(myEvent.value, pulseRangesCycling))
							sendPulseAlarm(myEvent);
					} else {
						sendPulseAlarm(myEvent);
					}
				} else {
					Log.i(TAG, "Too different homecareActivities.");
					int i = 1;
					while (mCursor.isAfterLast() == false) {
						Log.i(TAG, i+". Activity: "+mCursor.getString(mCursor.getColumnIndex("ACTIVITY"))
								+"with count of "+mCursor.getInt(mCursor.getColumnIndex("count")));
						i++;
						mCursor.moveToNext();
					}
				}
				mCursor.close();
				
        	} else {
        		if(D)Log.d(TAG, "Incoming heartbeat: DBMS was started again");
        		start_DB();
        	}*/
	
			
				
		} 
	}
	
		
	private void sendSensorAdjustMessage(String text) {
		/*String alarmText =  "Please check sensor: "+text;
		HeartbeatAlarmEvent testAlarm = 
			new HeartbeatAlarmEvent(alarmText, Calendar.getInstance().getTime());
		
		mCallbackHandler.obtainMessage(MessageHandler.ALARM_EVENT, 
				testAlarm).sendToTarget();
		if(D)Log.d(TAG, testAlarm.getText());	
		ringAlarm();*/
	}


	private boolean isPulseInRange(int pulsevalue, int[] ranges) {
		return (pulsevalue >= ranges[0] && pulsevalue <= ranges[1]);
	}

	private void sendPulseAlarm(HeartRateEvent event) {
		/*String alarmText = "Dangerous heartrate of "+event.value+
			" beats per minute!";
		HeartbeatAlarmEvent testAlarm = 
			new HeartbeatAlarmEvent(alarmText, Calendar.getInstance().getTime());

		mCallbackHandler.obtainMessage(MessageHandler.ALARM_EVENT, 
				testAlarm).sendToTarget();
		if(D)Log.d(TAG, alarmText);	
		ringAlarm();*/
	}
	
	private void ringAlarm() {
		//Context context = getApplicationContext();
		NotificationManager notificationManager = 
			(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.vibrate = new long[] {100, 250}; // (6)
		notification.sound =  Uri.parse("file:///system/media/audio/alarms/Alarm_Buzzer.ogg");
		//notification.sound = Notification.DEFAULT_SOUND;
		notificationManager.notify(123456789, notification);
	}
	

}
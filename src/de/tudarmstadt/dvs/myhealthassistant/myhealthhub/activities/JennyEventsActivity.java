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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Advertisement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.ManagementEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Subscription;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Unadvertisement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Unsubscription;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventAnkle;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventWrist;


public class JennyEventsActivity extends Activity {
    
	public static String TAG = "JennyEventsActivity";
	private boolean D = true;
	
	private static int SECOND = 1000;

	private static Handler eventGeneratorHandler = new Handler();
	
	private int[] accValuesA= {120, 120, 120, 0, 50, 0};
	private int[] accValuesB= {10, 10, 10, 30, 30, 30};
	
	private boolean randomHR;
	private boolean randomAccAnkle;
	private boolean randomAccWrist;
	
	private boolean sendHR;
	private boolean sendAccAnkle;
	private boolean sendAccWrist;
	
	private int duration;
	private int warmUpTime;	
	
	private int round;
	
	private ReadingEventReceiver mReadingEventReceiver;

	protected EventUtils myManagementUtils;
	
	private LocalBroadcastManager localBroadcastManager;
	
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat df = new java.text.SimpleDateFormat("dd MMM yyyy hh:mm:ss");

	public void onClickStart(View v) {
		loadConfiguration();
		advertiseSensors();
      	warmUp();
	}

	private void loadConfiguration() {
		/* Configure rounds */
		duration = Integer.valueOf(((EditText)findViewById(R.id.etSimulationTime)).getText().toString());
		warmUpTime = Integer.valueOf(((EditText)findViewById(R.id.etWarmUpTime)).getText().toString());
		
		/* config */
		sendHR = ((CheckBox)findViewById(R.id.cbHR)).isChecked();
		sendAccAnkle = ((CheckBox)findViewById(R.id.cbAccLeg)).isChecked();
		sendAccWrist = ((CheckBox)findViewById(R.id.cbAccWrist)).isChecked();
		
		
		/* random? */
		randomHR = ((CheckBox)findViewById(R.id.cbHRrandom)).isChecked();
		randomAccAnkle = ((CheckBox)findViewById(R.id.cbAccLegrandom)).isChecked();
		randomAccWrist = ((CheckBox)findViewById(R.id.cbAccWristrandom)).isChecked();
	
		round = 1;
	}
	


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jenny_events_activity);
        
		/* set time zone */
		df.setTimeZone(TimeZone.getTimeZone("gmt"));
		
		myManagementUtils = new EventUtils(ManagementEvent.getManagement(), "simpleEventGenerator");
		
		mReadingEventReceiver = new ReadingEventReceiver();
			
		localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
		
		// Register receivers
		getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.HEART_RATE));
		getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.ACCELEROMETER));
		getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.ACCELEROMETER_ANKLE));
		getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.ACCELEROMETER_WRIST));
		
		// generate subscription
		Subscription sub = new Subscription(
				TAG, getGMTTime(), TAG, getPackageName(), 
				SensorReadingEvent.HEART_RATE);
		publishManagemntEvent(sub);
		
		sub = new Subscription(
				TAG, getGMTTime(), TAG, getPackageName(), 
				SensorReadingEvent.ACCELEROMETER_ANKLE);
		publishManagemntEvent(sub);
		
		sub = new Subscription(
				TAG, getGMTTime(), TAG, getPackageName(), 
				SensorReadingEvent.ACCELEROMETER_WRIST);
		publishManagemntEvent(sub);	

    }
    
	
	/**
	 * Warm-up phase
	 */
	private void warmUp() {
		((CheckBox)findViewById(R.id.cbHR)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbHRrandom)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbAccLeg)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbAccLegA)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbAccLegB)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbAccLegrandom)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbAccWrist)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbAccWristA)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbAccWristB)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbAccWristrandom)).setEnabled(false);
		((Button)findViewById(R.id.buttonStart)).setEnabled(false);
		((EditText)findViewById(R.id.etHR)).setEnabled(false);
		((Button)findViewById(R.id.buttonStart)).setText("warm-up...");
		eventGeneratorHandler.postDelayed(startEventGenerationThread, warmUpTime*SECOND);
	}

	
	/**
	 * Triggers the event generation
	 */
	private Runnable startEventGenerationThread = new Runnable() {
		public void run() {
			((Button)findViewById(R.id.buttonStart)).setText("running...");
			eventGeneratorHandler.post(sendEvents);
		}
	};
    
	 private Runnable sendEvents = new Runnable() {
			public void run() {
				Event evt = null;
				
				// HR events
				if(sendHR) {
					if(randomHR) {
						evt = new HeartRateEvent(TAG+"_HR_"+round, getGMTTime(),
								"EventGenerator", "EventGenerator", round+"",
								getRandomNumber(55, 180));
					} else {
						evt = new HeartRateEvent(TAG+"_HR_"+round, getGMTTime(),
								"EventGenerator", "EventGenerator", round+"",
								Integer.valueOf(((EditText)findViewById(R.id.etHR)).getText().toString()));
					}
					injectEvent(evt);
					if(D)Log.d(TAG, "sent HR event");
				}
				
				// Acc ankle events
				if(sendAccAnkle) {
					if(randomAccAnkle) {
						/*if(getRandomNumber(0, 10)<=5) {
							evt = new AccSensorEventAnkle(TAG+"_AccAnkle_"+round, 
									getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
									//accValuesA[0], accValuesA[1], accValuesA[2],
									//accValuesA[3], accValuesA[4], accValuesA[5],
									getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
									getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
									false, false, false);							
						} else {
							evt = new AccSensorEventAnkle(TAG+"_AccAnkle_"+round, 
									getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
									//accValuesB[0], accValuesB[1], accValuesB[2],
									//accValuesB[3], accValuesB[4], accValuesB[5],
									getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
									getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
									false, false, false);
						}*/
						evt = new AccSensorEventAnkle(TAG+"_AccAnkle_"+round, 
								getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
								//accValuesB[0], accValuesB[1], accValuesB[2],
								//accValuesB[3], accValuesB[4], accValuesB[5],
								getRandomNumber(120, 140),getRandomNumber(0, 10),getRandomNumber(60, 80),
								getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
								false, false, false);
					} else {
						if(((CheckBox)findViewById(R.id.cbAccLegA)).isChecked()) {
							evt = new AccSensorEventAnkle(TAG+"_AccAnkle_"+round, 
									getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
									getRandomNumber(0, 30),getRandomNumber(20, 60),getRandomNumber(10, 20),
									getRandomNumber(128, 254),getRandomNumber(128, 254),getRandomNumber(128, 254),
									false, false, false);							
						} else {
							evt = new AccSensorEventAnkle(TAG+"_AccAnkle_"+round, 
									getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
									getRandomNumber(120, 150),getRandomNumber(200, 240),getRandomNumber(150, 170),
									getRandomNumber(0, 127),getRandomNumber(0, 127),getRandomNumber(0, 127),
									false, false, false);
						}
					}
					injectEvent(evt);
					if(D)Log.d(TAG, "send acc ankle event");
				}
				
				// Acc wrist event
				if(sendAccWrist) {
					if(randomAccWrist) {
						/*if(getRandomNumber(0, 10)<=5) {
							evt = new AccSensorEventWrist(TAG+"_AccWrist_"+round, 
									getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
									//accValuesA[0], accValuesA[1], accValuesA[2],
									//accValuesA[3], accValuesA[4], accValuesA[5], 
									getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
									getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
									false, false, false);							
						} else {
							evt = new AccSensorEventWrist(TAG+"_AccWrist_"+round, 
									getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
									//accValuesB[0], accValuesB[1], accValuesB[2],
									//accValuesB[3], accValuesB[4], accValuesB[5], 
									getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
									getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
									false, false, false);
							
						}*/
						evt = new AccSensorEventWrist(TAG+"_AccWrist_"+round, 
								getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
								getRandomNumber(100, 120),getRandomNumber(40, 60),getRandomNumber(30, 35),
								getRandomNumber(0, 254),getRandomNumber(0, 254),getRandomNumber(0, 254),
								false, false, false);
					} else {
						if(((CheckBox)findViewById(R.id.cbAccWristA)).isChecked()) {
							evt = new AccSensorEventWrist(TAG+"_AccWrist_"+round, 
									getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
									getRandomNumber(0, 30),getRandomNumber(20, 60),getRandomNumber(10, 20),
									getRandomNumber(128, 254),getRandomNumber(128, 254),getRandomNumber(128, 254),
									false, false, false);							
						} else {
							evt = new AccSensorEventWrist(TAG+"_AccWrist_"+round, 
									getGMTTime(), "EventGenerator", "EventGenerator", round+"", 
									getRandomNumber(120, 150),getRandomNumber(200, 240),getRandomNumber(150, 170),
									getRandomNumber(0, 127),getRandomNumber(0, 127),getRandomNumber(0, 127),
									false, false, false);
						}
					}
					injectEvent(evt);
					if(D)Log.d(TAG, "send acc wrist event");
				}
							
				// increase round counter
				round++;
				
				// post delayed if secondly triggered round is smaller than duration in seconds.
				if(round <= duration) {
					eventGeneratorHandler.postDelayed(sendEvents, SECOND);
				} else {
					unadvertiseSensors();
					((Button)findViewById(R.id.buttonStart)).setText("StartProducer");
					((Button)findViewById(R.id.buttonStart)).setEnabled(true);	
					((CheckBox)findViewById(R.id.cbHR)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbHRrandom)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbAccLeg)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbAccLegA)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbAccLegB)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbAccLegrandom)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbAccWrist)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbAccWristA)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbAccWristB)).setEnabled(true);
					((CheckBox)findViewById(R.id.cbAccWristrandom)).setEnabled(true);
					((Button)findViewById(R.id.buttonStart)).setEnabled(true);
					((EditText)findViewById(R.id.etHR)).setEnabled(true);
				}
			}
	    };
       
    private int getRandomNumber(int min, int max) {
    	Random randomGenerator = new Random();
    	return randomGenerator.nextInt(max-min)+min;
    }
    
    public void onClickHRRandom(View v) {
    	if(((CheckBox)findViewById(R.id.cbHRrandom)).isChecked()) {
    		((EditText)findViewById(R.id.etHR)).setEnabled(false);
    	} else {
    		((EditText)findViewById(R.id.etHR)).setEnabled(true);
    	}
    }
  
    private void injectEvent(Event evt) {
    	Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
		i.setAction(AbstractChannel.RECEIVER);
		sendBroadcast(i);
    }
    
    
    private String getGMTTime() {
		return df.format(new Date());
	}
    
	private void advertiseSensors() {
        // Generate advertisement
		Advertisement adverisement;
		
		if(sendHR) {
			adverisement = new Advertisement(
	        		TAG, getGMTTime(), 
	        		"JennyEventsHR",
	        		getApplicationContext().getPackageName(),
	        		SensorReadingEvent.HEART_RATE, 
	        		"-");
			publishManagemntEvent(adverisement);
		}

		if(sendAccAnkle) {
			adverisement = new Advertisement(
	        		TAG, getGMTTime(), 
	        		"JennyEventsHR",
	        		getApplicationContext().getPackageName(),
	        		SensorReadingEvent.ACCELEROMETER_ANKLE, 
	        		"-");
			publishManagemntEvent(adverisement);
		}

		if(sendAccWrist) {
			adverisement = new Advertisement(
	        		TAG, getGMTTime(), 
	        		"JennyEventsHR",
	        		getApplicationContext().getPackageName(),
	        		SensorReadingEvent.ACCELEROMETER_WRIST, 
	        		"-");
			publishManagemntEvent(adverisement);
		}		
	}
	
	private void unadvertiseSensors() {
        // Generate advertisement
		Unadvertisement adverisement;
		
		if(sendHR) {
			adverisement = new Unadvertisement(
	        		TAG, getGMTTime(), 
	        		"JennyEventsHR",
	        		getApplicationContext().getPackageName(),
	        		SensorReadingEvent.HEART_RATE);
			publishManagemntEvent(adverisement);
		}

		if(sendAccAnkle) {
			adverisement = new Unadvertisement(
	        		TAG, getGMTTime(), 
	        		"JennyEventsHR",
	        		getApplicationContext().getPackageName(),
	        		SensorReadingEvent.ACCELEROMETER_ANKLE);
			publishManagemntEvent(adverisement);
		}

		if(sendAccWrist) {
			adverisement = new Unadvertisement(
	        		TAG, getGMTTime(), 
	        		"JennyEventsHR",
	        		getApplicationContext().getPackageName(),
	        		SensorReadingEvent.ACCELEROMETER_WRIST);
			publishManagemntEvent(adverisement);
		}		
	}
	
	public void onDestroy() {
		// unsubscribe from readings
		Log.d(TAG, "Unsubscribe from readings");
		
		// generate subscription
		Unsubscription sub = new Unsubscription(
				TAG, getGMTTime(), TAG, getPackageName(), 
				SensorReadingEvent.HEART_RATE);
		publishManagemntEvent(sub);
		
		sub = new Unsubscription(
				TAG, getGMTTime(), TAG, getPackageName(), 
				SensorReadingEvent.ACCELEROMETER_ANKLE);
		publishManagemntEvent(sub);
		
		sub = new Unsubscription(
				TAG, getGMTTime(), TAG, getPackageName(), 
				SensorReadingEvent.ACCELEROMETER_WRIST);
		publishManagemntEvent(sub);	
	}
    
	/**
	 * Publishes a management event.
	 * @param managementEvent 
	 */
    private void publishManagemntEvent(Event managementEvent) {
    	publishEvent(managementEvent, AbstractChannel.MANAGEMENT);
    }
    
	/**
	 * Publishes an event on a specific myHealthHub channel. 
	 * @param event that shall be published.
	 * @param channel on which the event shall be published.
	 */
	private void publishEvent(Event event, String channel) {
    	Intent i = new Intent();
    	// add event
    	i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, event.getEventType());
    	i.putExtra(Event.PARCELABLE_EXTRA_EVENT, event);
    	
    	// set channel
    	i.setAction(channel);
    	
    	// sent intent
    	localBroadcastManager.sendBroadcast(i);    	
    }
	

	/** Event receiver implemented as a Android BroadcastReceiver */
	private class ReadingEventReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			/* Get event type and the event itself */
			String eventType = intent
					.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE);
		
			if(D){
				if(eventType.equals(SensorReadingEvent.ACCELEROMETER_WRIST)) {
					AccSensorEventWrist evt = (AccSensorEventWrist)intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
					Log.d(TAG, "Wrist :"+evt.x_mean+", "+evt.y_mean+", "+evt.z_mean);
				} else if(eventType.equals(SensorReadingEvent.ACCELEROMETER_ANKLE)) {
					AccSensorEventAnkle evt = (AccSensorEventAnkle)intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
					Log.d(TAG, "Ankle :"+evt.x_mean+", "+evt.y_mean+", "+evt.z_mean);
				}				
			}
		};
	}
}
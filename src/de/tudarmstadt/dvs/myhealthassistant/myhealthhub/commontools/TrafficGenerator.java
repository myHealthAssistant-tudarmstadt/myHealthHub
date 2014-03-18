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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools;

import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.ECGEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.WeightEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.WeightEventInKg;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.activity.ActivityEventSSWRC;

/**
 * @author Chris
 *
 */
public class TrafficGenerator {
	public String TAG = "TrafficGenerator";
	private boolean D = true;
	
	private static int HR = 0;
	private static int ACTIVITY = 1;
	private static int WEIGHT = 2;
	private static int ECG = 3;
	
	private static int SECOND = 1000;
	private static Handler eventGeneratorHandler = new Handler();
			
	private boolean isRunning;
	private int round;
	private int totalRound;
	private int[][] messagesPerSecond;
	
	private Context ctx;
	
	private int eventNumber;
	
	protected String myHealthHubReceiver = AbstractChannel.RECEIVER;
	
	int[] values;

	public TrafficGenerator(int instance, int[][] msgsPerSecond, Context ctx) {
    	TAG += "_"+instance;

    	this.isRunning = false;
        this.messagesPerSecond = msgsPerSecond;
        this.ctx = ctx;
        
		// prepare ECG reading
		int size = 208;
		values = new int[size];		
		for(int i=0; i<size; i++) {
			values[i] = 2000;			
		}
		// create a peak
		values[48] = 2200;
		values[49] = 2800;
		values[50] = 3200;
		values[51] = 2800;
		values[52] = 2200;
    }
    
    public void startRound(int i, int j) {
    	if(D)Log.d(TAG, "Start round "+i+" of "+j+" rounds.");
    	eventNumber = 0;
    	round = i;
    	totalRound = j;
		isRunning = true;
		if(messagesPerSecond[round][HR]!=0) generateEventsHR.run();
		if(messagesPerSecond[round][ACTIVITY]!=0) generateEventsActivity.run();
		if(messagesPerSecond[round][WEIGHT]!=0) generateEventsWeight.run();
		if(messagesPerSecond[round][ECG]!=0) generateEventsECG.run();
    }
    
    public void stop() {
    	isRunning = false;
    }
    
    private Runnable generateEventsHR = new Runnable() {
		public void run() {
			if (isRunning) {
				eventGeneratorHandler.postDelayed(generateEventsHR, SECOND
						/ messagesPerSecond[round][HR]);
				
				/* generate event */
				HeartRateEvent evt = new HeartRateEvent(TAG+"_HR_"+round+"_"+eventNumber, getGMTTime(),
						"EventGenerator", "EventGenerator", totalRound+"",
						65);
						//getRandomNumber(55, 180));				
				
				injectEventToLocalBroadcastReceiver(evt);
			}
		}
    };
    
    private Runnable generateEventsActivity = new Runnable() {
		public void run() {
			if (isRunning) {
				eventGeneratorHandler.postDelayed(generateEventsActivity, SECOND
						/ messagesPerSecond[round][ACTIVITY]);

			/* generate event */
//				BloodPressureEvent evt = new BloodPressureEvent(TAG+"_BP_"+round+"_"+eventNumber,
//						getGMTTime(), "EventGenerator", "EventGenerator",
//						totalRound+"", getRandomNumber(120, 140), getRandomNumber(60,
//								80), getRandomNumber(55, 120), "HGmm");
				String activity;
				switch (getRandomNumber(0, 4)) {
				case 0: activity = ActivityEventSSWRC.SITTING_NAME;break;
				case 1: activity = ActivityEventSSWRC.STANDING_NAME;break;
				case 2: activity = ActivityEventSSWRC.WALKING_NAME;break;
				case 3: activity = ActivityEventSSWRC.CYCLING_NAME;break;
				case 4: activity = ActivityEventSSWRC.RUNNING_NAME;break;
				default:activity = ActivityEventSSWRC.SITTING_NAME;break;
				}
				
				ActivityEventSSWRC evt = new ActivityEventSSWRC(TAG+"_HR_"+round+"_"+eventNumber, getGMTTime(),
						"EventGenerator", "EventGenerator", totalRound+"");
				evt.setActivity(1, activity, 0, 0);
				
				injectEventToLocalBroadcastReceiver(evt);
			}
		}
    };
    
    private Runnable generateEventsWeight = new Runnable() {
		public void run() {
			if (isRunning) {
				eventGeneratorHandler.postDelayed(generateEventsWeight, SECOND
						/ messagesPerSecond[round][WEIGHT]);

				/* generate event */
				WeightEvent evt = new WeightEventInKg(TAG+"_Weight_"+round+"_"+eventNumber, getGMTTime(),
						"EventGenerator", "EventGenerator", totalRound+"",
						getRandomNumber(60, 120)*10);
				
				injectEventToLocalBroadcastReceiver(evt);
			}
		}
    };

    private Runnable generateEventsECG = new Runnable() {
		public void run() {
			if (isRunning) {
				eventGeneratorHandler.postDelayed(generateEventsECG, SECOND
						/ messagesPerSecond[round][ECG]);
				
				ECGEvent evt = new ECGEvent(TAG+"_HR_"+round+"_"+eventNumber, getGMTTime(),
						"EventGenerator", "EventGenerator", totalRound+"", values.clone(), 200);
				injectEventToLocalBroadcastReceiver(evt);
			}
		}
    };
    
    
    public int getRandomNumber(int min, int max) {
    	Random randomGenerator = new Random();
    	return randomGenerator.nextInt(max-min)+min;
    }
    
    private void injectEventToLocalBroadcastReceiver(Event evt) {
    	Intent i = new Intent();
    	i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
    	i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
    	i.setAction(myHealthHubReceiver);
    	LocalBroadcastManager.getInstance(ctx).sendBroadcast(i);
    }
    
    /*private void injectEvent(Event evt) {
		if(D)Log.d(TAG, "injectEvent of type: "+evt.getEventType());
    	Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
		i.setAction(myHealthHubReceiver);
		ctx.sendBroadcast(i);
    }*/
    
    private String getGMTTime() {
		return (String) android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new java.util.Date());
	}
}
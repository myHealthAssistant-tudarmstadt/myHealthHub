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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.ManagementEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.BloodPressureEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventKnee;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.WeightEventInKg;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SimpleEventsFragment extends Fragment implements OnClickListener{
	public static String TAG = "EventGeneratorActivity";
	private boolean D = true;
	
	private static int SECOND = 1000;

	private static Handler eventGeneratorHandler = new Handler();
	
	private boolean randomHR;
	private boolean randomBP;
	private boolean randomWeight;
	
	private boolean sendHR;
	private boolean sendBP;
	private boolean sendWeight;
	private boolean sendAcc;
	
	private int duration;
	private int warmUpTime;	
	private int round;
	
	private ReadingEventReceiver mReadingEventReceiver;

	protected EventUtils myManagementUtils;	
	
	private SimpleDateFormat df = new java.text.SimpleDateFormat("dd MMM yyyy hh:mm:ss");

	private View rootView;
	
	public void onClickStart(View v) {
		loadConfiguration();
      	warmUp();
	}

	private void loadConfiguration() {
		/* Configure rounds */
		duration = Integer.valueOf(((EditText) rootView.findViewById(R.id.etSimulationTime)).getText().toString());
		warmUpTime = Integer.valueOf(((EditText) rootView.findViewById(R.id.etWarmUpTime)).getText().toString());
		
		/* config */
		sendHR = ((CheckBox) rootView.findViewById(R.id.cbHR)).isChecked();
		sendBP = ((CheckBox) rootView.findViewById(R.id.cbBP)).isChecked();
		sendWeight = ((CheckBox) rootView.findViewById(R.id.cbWeight)).isChecked();
		sendAcc = ((CheckBox) rootView.findViewById(R.id.cbAcc)).isChecked();
		
		/* random? */
		randomHR = ((CheckBox) rootView.findViewById(R.id.cbHRrandom)).isChecked();
		randomBP = ((CheckBox) rootView.findViewById(R.id.cbBPrandom)).isChecked();
		randomWeight = ((CheckBox) rootView.findViewById(R.id.cbWeightrandom)).isChecked();
		
		round = 1;
	}

	/** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(
				R.layout.simple_events_fragment, container, false);
		
		/* set time zone */
		df.setTimeZone(TimeZone.getTimeZone("gmt"));
		
		myManagementUtils = new EventUtils(ManagementEvent.getManagement(), "simpleEventGenerator");
		
		mReadingEventReceiver = new ReadingEventReceiver();
			
		// Register receivers
		getActivity().getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.HEART_RATE));
		getActivity().getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.ACCELEROMETER));
		getActivity().getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.WEIGHT));
		getActivity().getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.BLOOD_PRESSURE));
		
		// Register onClickListener
		((Button) rootView.findViewById(R.id.cbHRrandom)).setOnClickListener(this);
		((Button) rootView.findViewById(R.id.cbBPrandom)).setOnClickListener(this);
		((Button) rootView.findViewById(R.id.cbWeightrandom)).setOnClickListener(this);
		((Button) rootView.findViewById(R.id.buttonStart)).setOnClickListener(this);
		
		return rootView;
    }
    
	
	/**
	 * Warm-up phase
	 */
	private void warmUp() {
		((Button) rootView.findViewById(R.id.buttonStart)).setEnabled(false);
		((Button) rootView.findViewById(R.id.buttonStart)).setText("warm-up...");
		eventGeneratorHandler.postDelayed(startEventGenerationThread, warmUpTime*SECOND);
	}

	
	/**
	 * Triggers the event generation
	 */
	private Runnable startEventGenerationThread = new Runnable() {
		public void run() {
			((Button) rootView.findViewById(R.id.buttonStart)).setText("running...");
			eventGeneratorHandler.post(sendEvents);
		}
	};
    
	 private Runnable sendEvents = new Runnable() {
			public void run() {
				Event evt = null;
				
				// HR event
				if(sendHR) {
					if(randomHR) {
						evt = new HeartRateEvent(TAG+"_HR_"+round, getGMTTime(),
								"EventGenerator", "EventGenerator", round+"",
								getRandomNumber(55, 180));
					} else {
						evt = new HeartRateEvent(TAG+"_HR_"+round, getGMTTime(),
								"EventGenerator", "EventGenerator", round+"",
								Integer.valueOf(((EditText) rootView.findViewById(R.id.etHR)).getText().toString()));
					}
					injectEvent(evt);
					if(D)Log.d(TAG, "sent HR event");
				}
				
				// BP event
				if(sendBP) {
					if(randomBP) {
						evt = new BloodPressureEvent(TAG+"_BP_"+round, getGMTTime(),
								"EventGenerator", "EventGenerator", round+"",
								getRandomNumber(70, 150), getRandomNumber(50, 110),
								getRandomNumber(55, 180), "mmHG");
					} else {
						evt = new BloodPressureEvent(TAG+"_BP_"+round, getGMTTime(),
								"EventGenerator", "EventGenerator", round+"",
								Integer.valueOf(((EditText) rootView.findViewById(R.id.etBPsys)).getText().toString()),
								Integer.valueOf(((EditText) rootView.findViewById(R.id.etBPdia)).getText().toString()),								
								getRandomNumber(55, 180), "mmHG");
					}
					injectEvent(evt);
					if(D)Log.d(TAG, "sent BP event");
				}
				
				// Weight event
				if(sendWeight) {
					if(randomWeight) {
						evt = new WeightEventInKg(TAG+"_Weight_"+round, getGMTTime(),
								"EventGenerator", "EventGenerator", round+"",
								getRandomNumber(70, 90)*10);
					} else {
						evt = new WeightEventInKg(TAG+"_Weight_"+round, getGMTTime(),
								"EventGenerator", "EventGenerator", round+"",
								Integer.valueOf(((EditText) rootView.findViewById(R.id.etWeight)).getText().toString())*10);
					}
					injectEvent(evt);
					if(D)Log.d(TAG, "sent Weight event");
				}
				
				// Acceleration event
				if(sendAcc) {
					evt = new AccSensorEventKnee(TAG+"_Acc_"+round, getGMTTime(),
							"EventGenerator", "EventGenerator", round+"", 
							getRandomNumber(0, 120),
							getRandomNumber(0, 120), getRandomNumber(0, 120),
							getRandomNumber(0, 120), getRandomNumber(0, 120),
							getRandomNumber(0, 120));
					
					injectEvent(evt);
					if(D)Log.d(TAG, "sent ACC event");
				}
								
				// increase round counter
				round++;
				
				// post delayed if secondly triggered round is smaller than duration in seconds.
				if(round <= duration) {
					eventGeneratorHandler.postDelayed(sendEvents, SECOND);
				} else {
					((Button) rootView.findViewById(R.id.buttonStart)).setText("StartProducer");
					((Button) rootView.findViewById(R.id.buttonStart)).setEnabled(true);
				}
			}
	    };
       
    private int getRandomNumber(int min, int max) {
    	Random randomGenerator = new Random();
    	return randomGenerator.nextInt(max-min)+min;
    }
    
    public void onClickWeightRandom(View v) {
    	if(((CheckBox) rootView.findViewById(R.id.cbWeightrandom)).isChecked()) {
    		((EditText) rootView.findViewById(R.id.etWeight)).setEnabled(false);
    	} else {
    		((EditText) rootView.findViewById(R.id.etWeight)).setEnabled(true);
    	}
    }
    
    public void onClickBPRandom(View v) {
    	if(((CheckBox) rootView.findViewById(R.id.cbBPrandom)).isChecked()) {
    		((EditText) rootView.findViewById(R.id.etBPsys)).setEnabled(false);
    		((EditText) rootView.findViewById(R.id.etBPdia)).setEnabled(false);
    	} else {
    		((EditText) rootView.findViewById(R.id.etBPsys)).setEnabled(true);
    		((EditText) rootView.findViewById(R.id.etBPdia)).setEnabled(true);
    	}
    }
    
    public void onClickHRRandom(View v) {
    	if(((CheckBox) rootView.findViewById(R.id.cbHRrandom)).isChecked()) {
    		((EditText) rootView.findViewById(R.id.etHR)).setEnabled(false);
    	} else {
    		((EditText) rootView.findViewById(R.id.etHR)).setEnabled(true);
    	}
    }
  
    private void injectEvent(Event evt) {
    	Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
		i.setAction(AbstractChannel.RECEIVER);
		getActivity().sendBroadcast(i);
    }
    
    
    private String getGMTTime() {
		return df.format(new Date());
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
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.cbHRrandom:
			onClickHRRandom(v);
			return;
		case R.id.cbBPrandom:
			onClickBPRandom(v);
			return;
		case R.id.cbWeightrandom:
			onClickWeightRandom(v);
			return;
		case R.id.buttonStart:
			onClickStart(v);
			return;
		}
	}
}
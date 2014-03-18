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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationRequest;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationResponse;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.ECGEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.WeightEvent;

public class BrokerActivity extends Activity {

	private static String TAG = "BrokerActivity";
	
	private IntentFilter myResponseReceiverChannel;

	private EventUtils evtUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.examples_broker_activity);

		// Register local management receiver with LocalBroadcastManager
		myResponseReceiverChannel = new IntentFilter(AbstractChannel.LOCAL_MANAGEMENT);
		LocalBroadcastManager.getInstance(this).registerReceiver(myResponseReceiver, myResponseReceiverChannel);  // IMPORTANT: use LocalBroadcastManager
		
		// StartProducer TransformationManager which should be started as an Android service (not done here)
		new TransformationManager(this, AbstractChannel.LOCAL_MANAGEMENT);
		
		// Event helper
		evtUtils = new EventUtils(EventTransformationRequest.EVENT_TYPE, TAG);
	}

	public void onClickSub1(View v) {
		Log.d(TAG, "Button 1 is pressed.");
		
		// Create request
		EventTransformationRequest request = 
				createRequestEvent(EventTransformationRequest.TYPE_REQUEST_TRANSFORMATION, 
						new String[] {AccSensorEvent.EVENT_TYPE, ECGEvent.EVENT_TYPE}, 
						HeartRateEvent.EVENT_TYPE);
		
		// Broadcast event locally
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, request.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, request);
		i.setAction(AbstractChannel.LOCAL_MANAGEMENT);
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);	// IMPORTANT: use LocalBroadcastManager
	}
	
	public void onClickSub2(View v) {
		Log.d(TAG, "Button 2 is pressed.");
		
		// Create request
		EventTransformationRequest request = 
				createRequestEvent(EventTransformationRequest.TYPE_REQUEST_TRANSFORMATION, 
						new String[] {AccSensorEvent.EVENT_TYPE, ECGEvent.EVENT_TYPE}, 
						WeightEvent.EVENT_TYPE);
		
		// Broadcast event locally
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, request.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, request);
		i.setAction(AbstractChannel.LOCAL_MANAGEMENT);
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);	// IMPORTANT: use LocalBroadcastManager
	}

		
	// Implements a usual broadcast receiver 
	private BroadcastReceiver myResponseReceiver = new BroadcastReceiver() {
		@Override
	    public void onReceive(Context context, Intent intent) {
			// Skip if not a event transformation response
			if(!intent.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE).equals(EventTransformationResponse.EVENT_TYPE)) return;
			
			// Get and print event
			EventTransformationResponse response = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
			Log.d(TAG, "Got result "+response.isTransformationFound()+" for request ID: "+response.getID()+".");    		
	    }
	};

	private EventTransformationRequest createRequestEvent(int requestType, String[] advertisedEvents, String eventSubscription) {
		return new EventTransformationRequest(
				evtUtils.getEventID(), 
				evtUtils.getTimestamp(), 
				TAG, 
				requestType, 
				advertisedEvents, 
				eventSubscription);
	}
}
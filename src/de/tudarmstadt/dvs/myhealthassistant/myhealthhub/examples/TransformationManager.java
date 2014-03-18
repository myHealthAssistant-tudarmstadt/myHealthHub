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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationRequest;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationResponse;

public class TransformationManager {

	private Context applicationContext;
	private IntentFilter myRequestReceiverChannel;
	
	private String TAG = "TransformationManager";
	
	private EventUtils evtUtils;
	
	public TransformationManager(Context cxt, final String requestChannel) {
		// Store application context
		applicationContext = cxt;
		
		// Register Receiver
		myRequestReceiverChannel = new IntentFilter(requestChannel);
		LocalBroadcastManager.getInstance(applicationContext).registerReceiver(myRequestReceiver, myRequestReceiverChannel); // IMPORTANT: use LocalBroadcastManager
		
		// Event helper
		evtUtils = new EventUtils(EventTransformationResponse.EVENT_TYPE, TAG);
	}
	
	// Implements a usual broadcast receiver 
    private BroadcastReceiver myRequestReceiver = new BroadcastReceiver() {
    	@Override
	    public void onReceive(Context context, Intent intent) {
			// Skip if not a event transformation request   		
    		if(!intent.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE).equals(EventTransformationRequest.EVENT_TYPE)) return;
    		
    		EventTransformationRequest request = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
    		
    		if(request.getRequestType()==EventTransformationRequest.TYPE_REQUEST_TRANSFORMATION) {
    			Log.d(TAG, "Received transformation request");
    			findTransformation(
    					request.getID(),
    					request.getAdvertisedEvents(),
    					request.getEventSubscription());
    		} else if (request.getRequestType()==EventTransformationRequest.TYPE_STOP_TRANSFORMATION) {
    			Log.d(TAG, "Received a stop transformation request");
    			stopTransformation(
    					request.getID(),
    					request.getAdvertisedEvents(),
    					request.getEventSubscription());
    		} 
	    }
    };

	protected void findTransformation(String eventID, String[] advertisedEvents,
			String subscription) {
		
		// Print request
		Log.d(TAG, "("+eventID+") Find transformation for getting event type: "+subscription+".");
		String events = "";
		for(String evt : advertisedEvents) events += evt +"\n"; 
		Log.d(TAG, "Advertised events: \n" + events);
		
		// Create response event 		
		EventTransformationResponse response = new EventTransformationResponse(
				evtUtils.getEventID(), 
				evtUtils.getTimestamp(), 
				TAG,
				eventID,
				true);
		
		// Send response over local broadcast
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, response.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, response);
		i.setAction(AbstractChannel.LOCAL_MANAGEMENT);
		LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i);	 // IMPORTANT: use LocalBroadcastManager
	}

	protected void stopTransformation(String eventID, String[] stringArrayExtra,
			String stringExtra) {
		// TODO Auto-generated method stub		
	}
}
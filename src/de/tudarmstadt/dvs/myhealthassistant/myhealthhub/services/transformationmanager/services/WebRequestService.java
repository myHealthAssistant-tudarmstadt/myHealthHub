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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.Preferences;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationRequest;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationResponse;

public class WebRequestService extends IntentService {

	// for debugging 
	private final static String TAG = "WebRequestService";
	private final static boolean D = false;
	
	// for JSON communication
	public final static String JSON_TRANSFORMATION_URI = "uri";
	public final static String JSON_TRANSFORMATION_BYTE_CODE = "transformationByteCode";
	public final static String JSON_REQUESTED_EVENT_TYPE = "requestedEventType";
	public final static String JSON_AVAILABLE_EVENT_TYPES = "availableEventTypes";
	public final static String JSON_REQUIRED_EVENT_TYPES = "requiredEventTypes";
	public final static String JSON_TRANSFORMATION_COSTS = "transformationCosts";
	
	/**
	 * Constructor.
	 */
	public WebRequestService() {
		super("WebRequestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		EventTransformationRequest request = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
		if(D)Log.d(TAG, "Receiving web request for transformation to type: "+request.getEventSubscription());
		
		// create JSON object for querying the server
		JSONArray advertisedEventTypes = new JSONArray(convertArrayToList(request.getAdvertisedEvents()));
		JSONObject jsonRequestString = new JSONObject();
		
		try{			
			jsonRequestString.put(JSON_AVAILABLE_EVENT_TYPES, advertisedEventTypes);
			jsonRequestString.put(JSON_REQUESTED_EVENT_TYPE, request.getEventSubscription());
		}catch(JSONException ex){
			ex.printStackTrace();
		}
		
		// Build server address
		SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
		String host = spref.getString(Preferences.TM_REMOTE_REPOSITORY_HOST, "192.168.1.7");
		int port = Integer.valueOf(spref.getString(Preferences.TM_REMOTE_REPOSITORY_PORT, "8080"));
		String baseUrl = "http://"+host+":"+port;		
		
		// Build URI
		Uri.Builder b = Uri.parse(baseUrl).buildUpon();
		b.path("/transformators/query/");
		b.appendQueryParameter("jsonString", jsonRequestString.toString());
		String urlRequest = b.build().toString(); 
		if(D)Log.d(TAG, "Send request to: "+urlRequest);
		
		// Request server
		String serverResponse = null;
		HttpClient httpClient = new DefaultHttpClient();
		boolean connectionFailure = false;
		boolean transformationFound = false;
		
		try{
			// generate httpGet
			HttpGet httpGet = new HttpGet(urlRequest);
			
			// headers
		    httpGet.setHeader("Accept", "application/json");
		    httpGet.setHeader("Content-Type", "application/json");
		    
		    // execute request
			HttpResponse response = httpClient.execute(httpGet);
			
			// process response
			StatusLine statusLine = response.getStatusLine();
			if(D) Log.d(TAG, "Server response status "+ statusLine);
			
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				// read response
				transformationFound = true;
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				serverResponse = out.toString();
				
				
				
			} else if (statusLine.getStatusCode() == HttpStatus.SC_NO_CONTENT){
				if(D)Log.i(TAG, "no transformation found");
				transformationFound = false;
			} 
		// catch exceptions
		}catch(ClientProtocolException ex){
			ex.printStackTrace();
			connectionFailure = true;			
		}catch(IOException ex){
			ex.printStackTrace();
			connectionFailure = true;
		}		
		
		// Generate response to TransformationManager and FelixService
		// Connection failed
		if(connectionFailure){
			if(D)Log.d(TAG, "Notifying a failed request");	
			// TODO do we want to transmit a connection error?
			EventTransformationResponse reponse = new EventTransformationResponse(
					"todo", "todo", TAG, request.getID(), false);
			sendToChannel(reponse, AbstractChannel.LOCAL_MANAGEMENT);
		// Connection successful
		} else {
			
			// no transformation found
			if(!transformationFound) {
				if(D)Log.d(TAG, "No transformation available");
				EventTransformationResponse reponse = new EventTransformationResponse(
						"todo", "todo", TAG, request.getID(), false);
				sendToChannel(reponse, AbstractChannel.LOCAL_MANAGEMENT);
				
			// transformation found
			} else {
				if(D)Log.d(TAG, "Transformation available");
				
				// generate Intent for felix service
				Intent i = new Intent();
				
				// add EventTRansformationResponse
				EventTransformationResponse reponse = new EventTransformationResponse(
						"todo", "todo", TAG, request.getID(), true);				
				i.putExtra(Event.PARCELABLE_EXTRA_EVENT, reponse);
				i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, reponse.getEventType());
				
				// add server response
				i.putExtra(FelixService.INTENT_EXTRA_TRANSFORMATION_BYTE_CODE, serverResponse);
						
				// successful requests are first forwarded to the felix service 
				i.setAction(FelixService.FELIX_SUCCESSFUL_WEB_REQUEST);
				LocalBroadcastManager.getInstance(this).sendBroadcast(i);
			}			
		}		
	}
	
	private List<String> convertArrayToList(String[] advertisedEvents) {
		List<String> names = new ArrayList<String>();
		for(String eventName : advertisedEvents){
			names.add(eventName);
		}
		return names;
	}
	
	/**
	 * Returns only the last part after the "." of an event type. 
	 * @return Short event type
	 */
    public String getShortEventType(String eventType) {
    	if(eventType.contains("myhealthassistant.event.")) {
    		return eventType.substring(eventType.lastIndexOf("myhealthassistant.event.")+24, eventType.length());	
    	} else {
    		return eventType;
    	}
    	
    }
    
	/**
	 * Sends an event to a specific channel using the LocalBroadcastManager
	 * @param Event to send
	 * @param Channel on which the event is sent
	 */
	protected void sendToChannel(Event evt, String channel) {
    	Intent i = new Intent();
    	i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
    	i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
    	i.setAction(channel);
    	LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

}
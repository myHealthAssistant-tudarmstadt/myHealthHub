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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.messagehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationRequest;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationResponse;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Advertisement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Announcement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.JSONDataExchange;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.ManagementEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.StartProducer;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.StopProducer;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Subscription;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Unadvertisement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Unsubscription;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.notifications.NotificationEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDB;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDBMS;

public class MessageHandler extends Service {

	/** For Debugging */
	private static final String TAG = "MessageHandler";
	private static boolean D = false;
	
	private static final String TAG_ANNOUNCEMENT = "Announcement MessageHandler";
	private static boolean D_ANNOUNCEMENT = false;
	
	private static final String TAG_ROUTING = "MessageHandler Routing";
	private static boolean D_ROUTING = true;


    DBAdapterSubscriptions db; // = new DBAdapterSubscriptions(this);
    private HashMap<String, List<String>> subscriberChannelHashMap; 
    private HashMap<String, List<ProducerDetails>> advertisementHashMap;
    private HashMap<String, Integer> sensorConnectivityAnnouncement;

    private EventUtils evtUtils;
  		
	private final IntentFilter sensorReadingsChannel = 
			new IntentFilter(AbstractChannel.RECEIVER);
	private EventReceiver mEventReceiver;
	
	private ManagementReceiver mManagementReceiver;
	private final IntentFilter managementReceiverChannel = new IntentFilter(AbstractChannel.MANAGEMENT);
	
	private ResponseReceiver mResponseReceiver;
	private IntentFilter mResponseReceiverChannel = new IntentFilter(AbstractChannel.LOCAL_MANAGEMENT);

	
	private final IntentFilter notificationChannel = new IntentFilter(AbstractChannel.NOTIFICATION);
	private NotificationReceiver mNotificationReceiver;
	
	private class NotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final NotificationEvent evt = (NotificationEvent) intent
					.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);

			// Create subject
			String tempSubject = "myHealthAssistant ";
			switch (evt.severity) {
			case NotificationEvent.SEVERITY_INFORMATION:
				tempSubject += "INFORMATION";
				break;
			case NotificationEvent.SEVERITY_DEBUG:
				tempSubject += "DEBUG";
				break;
			case NotificationEvent.SEVERITY_CRITICAL:
				tempSubject += "CRITICAL";
				break;
			case NotificationEvent.SEVERITY_WARNING:
				tempSubject += "WARNING";
				break;
			}
			tempSubject += ": " + evt.subject;
			final String subject = tempSubject;

			// Create body
			final String body = "(This is an auto-generated message from myHealthAssistant.)"
					+ "\n\nMessage: " + evt.text+ "\n\nProcuder: "+evt.getProducerID();

			// Send e-mail
			Thread thread = new Thread() {
				public void run() {
					try {
//						GMailSender sender = new GMailSender(
//								null,
//								null);
//						sender.sendMail(subject, body,
//								null,
//								null);
					} catch (Exception e) {
						Log.e("SendMail", e.getMessage(), e);
					}
				}
			};
			thread.start();
		}
	}
	
	/** Event Handler */
    private class EventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Event evt = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
        	if(D)Log.d(TAG, "Incoming event of type "+evt.getEventType());

        	String eventType = intent.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE);        	
        	       	
        	// Discard null values for a String
        	if(eventType==null){
        		Log.e(TAG, "eventType is null");
        		return;
        	}
        	
        	// Parse event type into an event tree
        	String[] eventTree = parseEventType(eventType);
        	
        	// Discard invalid events
        	if(eventTree == null) {
        		Log.e(TAG, "Invalid incoming event type: "+eventType);
        		return;
        	}
        	
        	// Skip if tree height is smaller than 2
        	if(eventTree.length<1) {
        		Log.e(TAG, "Event tree too small. Event is not forwarded. "+eventType);
        		return;
        	}
        	
        	List<String> tempSubscriberForEventTypeList;
        	String eventTypeKey = Event.EVENT_ROOT;
        	
        	//Check for each step in the eventTree if anyone is subscribed
        	for(int i=0;i<eventTree.length;i++){
        		eventTypeKey=(eventTypeKey+"."+eventTree[i]);
        		tempSubscriberForEventTypeList = subscriberChannelHashMap.get(eventTypeKey);
        		
        		if(tempSubscriberForEventTypeList!=null){       			
        			//set implicit
        			intent.setAction(eventTypeKey);
        			//Send to all subscribers
        			for(int j=0;j<tempSubscriberForEventTypeList.size();j++){
        				String receiverPackage = tempSubscriberForEventTypeList.get(j);
        				       				
        				/*if(receiverPackage.equals(getPackageName())) {
        					// send locally
        					if(D)Log.i(TAG, "Send locally to package: "+receiverPackage);
        					
        					LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        					//TODO
        					intent.setPackage(tempSubscriberForEventTypeList.get(j));
            				getApplicationContext().sendBroadcast(intent);
        				} else {*/
        					// globally
        					if(D)Log.i(TAG, "Send globally to package: "+receiverPackage);
        					
            				//set explicit
            				intent.setPackage(tempSubscriberForEventTypeList.get(j));
            				getApplicationContext().sendBroadcast(intent);
        				//}
                	}
        		}
        	}
        };        
    }
    
    /** Management Receiver */
    private class ManagementReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	if(intent == null)return;
        	
        	//Log.d(TAG, "Received EventTypeShort: "+getLastStringAfterDot(intent.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE)));
        	Event evt = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
        	
        	//Checks the ManamgentEvent and notifies the sender about the invalid argument
        	if(!isManagementEventValid(evt))return;
        	
        	// JSONDataExchange
        	if (evt.getEventType().equals(ManagementEvent.JSON_DATA_EXCHANGE)){
        		Log.d(TAG, "JSON Data Exchange event from: "+ evt.getProducerID());

        		String eventProducerID = evt.getProducerID();
        		String eventProducersPackageName = ((JSONDataExchange)evt).getPackageName();
        		String dataExchangeEventType = ((JSONDataExchange)evt).getDataExchangeEventType();
//        		ProducerDetails producerDetails = new ProducerDetails(eventProducerID, eventProducersPackageName);
        		String jsonDataString = ((JSONDataExchange)evt).getJSONEncodedData();
        		
        		try {
					JSONObject jsonData = new JSONObject(jsonDataString);
					String json_request = jsonData.optString(JSONDataExchange.JSON_REQUEST, "null");
					JSONArray jObjArray = jsonData.optJSONArray(JSONDataExchange.JSON_CONTENT_ARRAY);
					
					if (json_request.equalsIgnoreCase(JSONDataExchange.JSON_STORE) && jObjArray != null){
						// save contents to db
						ArrayList<ContentValues> vArray = new ArrayList<ContentValues>();
						for (int i = 0; i < jObjArray.length(); i++){
							JSONObject jObj = jObjArray.optJSONObject(i);
							if (jObj != null){
								
								String contents = jObj.toString();
								String contentDate = jObj.optString(JSONDataExchange.JSON_DATE, "null");
								String contentExtra = jObj.optString(JSONDataExchange.JSON_EXTRA, "null");
								
								ContentValues value = new ContentValues();
								value.put(LocalTransformationDB.COUMN_JSON_DATE, contentDate);
								value.put(LocalTransformationDB.COUMN_JSON_CONTENT, contents);
								value.put(LocalTransformationDB.COUMN_JSON_EXTRA, contentExtra);
								
								vArray.add(value);
							}
						}
						LocalTransformationDBMS transformationDB = new LocalTransformationDBMS(context);
						transformationDB.open();
						transformationDB.storeJsonData(vArray);
						transformationDB.close();
						
					} else if (json_request.equalsIgnoreCase(JSONDataExchange.JSON_GET)){
						// send to eventProducer all data from db
						LocalTransformationDBMS transformationDB = new LocalTransformationDBMS(context);
						transformationDB.open();
						JSONArray getJArray = transformationDB.getAlljsonData();
						transformationDB.close();
						JSONObject jEncodedData = new JSONObject();
						jEncodedData.putOpt(JSONDataExchange.JSON_REQUEST, JSONDataExchange.JSON_GET);
						jEncodedData.putOpt(JSONDataExchange.JSON_CONTENT_ARRAY, getJArray);
						
						if (evtUtils != null){
							JSONDataExchange eData = new JSONDataExchange(
									evtUtils.getEventID(), evtUtils.getTimestamp(), TAG, eventProducersPackageName, dataExchangeEventType, jEncodedData.toString());
							sendToManagementChannel(eData, eventProducersPackageName);
							Log.e(TAG, "send EncodedData to " + eventProducersPackageName + "; ID:" + eventProducerID );
							
						} else {
							Log.e(TAG, "cant send back jsonEncodedData to " + eventProducersPackageName + "; ID:" + eventProducerID );
						}
					} else if (json_request.equalsIgnoreCase(JSONDataExchange.JSON_EDIT) && jObjArray != null){
						// edit contents in db
						ArrayList<ContentValues> vArray = new ArrayList<ContentValues>();
						int id = -1;
						for (int i = 0; i < jObjArray.length(); i++){
							JSONObject jObj = jObjArray.optJSONObject(i);
							if (jObj != null){
								
								id = jObj.optInt(JSONDataExchange.JSON_CONTENT_ID, -1);
								String contents = jObj.toString();
								String contentDate = jObj.optString(JSONDataExchange.JSON_DATE, "null");
								String contentExtra = jObj.optString(JSONDataExchange.JSON_EXTRA, "null");
								
								ContentValues value = new ContentValues();
								value.put(LocalTransformationDB.COUMN_JSON_DATE, contentDate);
								value.put(LocalTransformationDB.COUMN_JSON_CONTENT, contents);
								value.put(LocalTransformationDB.COUMN_JSON_EXTRA, contentExtra);
								
								vArray.add(value);
							}
						}
						LocalTransformationDBMS transformationDB = new LocalTransformationDBMS(context);
						transformationDB.open();
						transformationDB.editJsonData(id, vArray.get(0)); // assume that only one jObj passed in
						transformationDB.close();
						
					}  else if (json_request.equalsIgnoreCase(JSONDataExchange.JSON_DEL) && jObjArray != null){
						// delete contents in db
						int id = -1;
						for (int i = 0; i < jObjArray.length(); i++){
							JSONObject jObj = jObjArray.optJSONObject(i);
							if (jObj != null){
								id = jObj.optInt(JSONDataExchange.JSON_CONTENT_ID, -1);
							}
						}
						Log.e(TAG, "delet json:" + id);
						LocalTransformationDBMS transformationDB = new LocalTransformationDBMS(context);
						transformationDB.open();
						transformationDB.deleteJsonData(id);
						transformationDB.close();
												
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
        	}
        	
        	//Advertisement
        	if(evt.getEventType().equals(ManagementEvent.ADVERTISEMENT)) {
        		if(D)Log.d(TAG, "Advertisement event from "+evt.getProducerID());
        		      		
        		String eventProducerID = evt.getProducerID();
        		String eventProducersPackageName = ((Advertisement)evt).getPackageName();
        		String advertisedEventType = ((Advertisement)evt).getAdvertisedEventType();
        		ProducerDetails producerDetails = new ProducerDetails(eventProducerID, eventProducersPackageName);
        		
        		//Get producers for the EventType
        		List<ProducerDetails> tempProducerDetailsList = advertisementHashMap.get(advertisedEventType);
        		        		
				// Check if anyone is already providing the event
				if (tempProducerDetailsList!=null) {
					//Check if producer is already in list
					for(int i=0;i<tempProducerDetailsList.size();i++){
						if(tempProducerDetailsList.get(i).getProducerID().equals(eventProducerID)){
							if(D)Log.d(TAG, "Producer already in List: "+eventProducerID);
							sendAnnouncement(Announcement.ADVERTSIMENT_UNSUCESSFULL_PRODUCER_ALREADY_IN_LIST, advertisedEventType, eventProducersPackageName);
							return;
						}
					}
					tempProducerDetailsList.add(producerDetails);
					if(D)Log.d(TAG, "Producer added to advertisementList: "+ eventProducerID);
				} else {
					// If no producer exists for EventType create a new entry
					List<ProducerDetails> producerList = new ArrayList<ProducerDetails>();
					producerList.add(producerDetails);
					advertisementHashMap.put(advertisedEventType,producerList);
					if(D)Log.d(TAG, "New Entry in Producer HashMap: "+eventProducerID);
				}
				
				// Send a startEvent/StopEvent to the producer and a ManagementEvent to all subscribers
				List<String> subscribersListForEventType = getSubscribersListForEventType(advertisedEventType);
				if (subscribersListForEventType != null) {
					sendStartEvent(eventProducerID, advertisedEventType, eventProducersPackageName);
					sendAnnouncementToRecieverList(Announcement.ADVERTISMENT_NEW_EVENT_TYPE_AVAILABLE,advertisedEventType,subscribersListForEventType);
				} else {
					sendStopEvent(eventProducerID, advertisedEventType,eventProducersPackageName);					
				}
				sendAnnouncement(Announcement.ADVERTISMENT_SUCCESSFULL, advertisedEventType, eventProducersPackageName);
				
	        	if(D_ROUTING)printAdvertisementsAndSubscriptions();
				
			//Unadvertisement
        	} else if(evt.getEventType().equals(ManagementEvent.UNADVERTISEMENT)) {
        		String eventProducer = evt.getProducerID();
        		String packageName = ((Unadvertisement)evt).getPackageName();
        		String unadvertisedEventType = ((Unadvertisement)evt).getUnadvertisedEventType();
        		ProducerDetails producerDetails = new ProducerDetails(eventProducer, packageName);
        		
        		//Check if there are producers for EventType
        		if(advertisementHashMap.containsKey(unadvertisedEventType)){
        			List<ProducerDetails> producerDetailsList = advertisementHashMap.get(unadvertisedEventType);
        			
        			for(int i=0;i<producerDetailsList.size();i++){
        				if(producerDetailsList.get(i).getProducerID().equals(producerDetails.getProducerID())){
        					producerDetailsList.remove(i);
        					i--;
        					if(D)Log.d(TAG,"Removed ProducerDetails: ProducerID: "+producerDetails.getProducerID() + " PackageNameShort "+getLastStringAfterDot(producerDetails.getProducerPackageName()));
        				}
        			}
        			
        			//If there are no more producer for the EventType remove from EventType advertismentHashMap and notify all subscribers
        			if(producerDetailsList.isEmpty()){        				
        				advertisementHashMap.remove(unadvertisedEventType);
        				//Notify all EventType subscribers
        				List<String> receiverPackageNameList = getSubscribersListForEventType(unadvertisedEventType);
        				if(receiverPackageNameList!=null){
        					sendAnnouncementToRecieverList(Announcement.UNADVERTISMENT_EVENT_TYPE_NOT_LONGER_AVAILABLE, unadvertisedEventType, receiverPackageNameList);
        				}
        			}        			
        			sendAnnouncement(Announcement.UNADVERTISMENT_SUCCESSFULL, unadvertisedEventType, packageName);
        		}
            	
        		if(D_ROUTING)printAdvertisementsAndSubscriptions();
        	
        	//Subscription
        	} else if(evt.getEventType().equals(ManagementEvent.SUBSCRIPITON)) {
        		if(D)Log.d(TAG, "Subscription event from "+evt.getProducerID());
        		
        		String subscribersPackageName = ((Subscription)evt).getPackageName();
        		String subscriptionSensorReadings = ((Subscription)evt).getSubscriptionSensorReadings();
        		        		
				// Check if an entry for the eventType already exists
				if (subscriberChannelHashMap.containsKey(subscriptionSensorReadings)) {
					List<String> tempSubscribersListForEventType = subscriberChannelHashMap.get(subscriptionSensorReadings);

					if (!tempSubscribersListForEventType.contains(subscribersPackageName)) {
						//Check all effected producers and send a StartEvent if no one is already subscribed to it
						subscriptionCheckWithAllAvailableEventsThatMatch(subscriptionSensorReadings);		//Important: check before adding!
						tempSubscribersListForEventType.add(subscribersPackageName);
						if(D)Log.d(TAG,"Subscriber added to Entry in subscribersHashMap: EventTypeShort: "+getLastStringAfterDot(subscriptionSensorReadings)+", PackageNameShort: "+subscribersPackageName);
					} else {						
						sendAnnouncement(Announcement.SUBSCRIPTOPN_SUBSCRIBER_ALREADY_IN_LIST, subscriptionSensorReadings, subscribersPackageName);
						return;
					}
				} else {
					// If no entry exists create a new one and send a StartEvent to the EventType producer
					List<String> receiverList = new ArrayList<String>();
					receiverList.add(subscribersPackageName);
					//Check all effected producers and send a StartEvent if no one is already subscribed to it
					subscriptionCheckWithAllAvailableEventsThatMatch(subscriptionSensorReadings);		//Important: check before adding!
					subscriberChannelHashMap.put(subscriptionSensorReadings, receiverList);
					if(D)Log.d(TAG,"Subscriber Package new Entry in subscribersHashMap: EventTypeShort: "+getLastStringAfterDot(subscriptionSensorReadings)+", PackageNameShort: "+subscribersPackageName);
				}
				sendAnnouncement(Announcement.SUBSCRIPTION_SUCCESSFULL,subscriptionSensorReadings, subscribersPackageName);
				// send sensor connectivity
				if(sensorConnectivityAnnouncement.containsKey(subscriptionSensorReadings))
					sendAnnouncement(sensorConnectivityAnnouncement.get(subscriptionSensorReadings), subscriptionSensorReadings, subscribersPackageName);
				
				//Write Subscription into databases
				db.openWritabelDB();
				db.insertOrReplaceRecord(evt.getEventType(), evt.getID(), evt.getTimestamp() /*evtUtils.getTimestamp()*/, evt.getProducerID(), subscribersPackageName, subscriptionSensorReadings);				
				if(D)Log.d(TAG, "insertOrReplaceRecord("+evt.getEventType()+", "+evt.getID()+", "+evt.getTimestamp()+", "+evt.getProducerID()+", "+subscribersPackageName+", "+subscriptionSensorReadings+")");
				db.close();
				
				//If there are no EventProducers send a TransformationManagerRequest
				//TODO: Due to lack in restore DB - erwaehnen in der Bachelorthesis
				if(advertisementHashMap.get(subscriptionSensorReadings)==null){
					sendEventTransformationRequest(subscriptionSensorReadings);
					if(D)Log.d(TAG,"sendTransfromtionRequest("+subscriptionSensorReadings+");");
				}
				
				if(D_ROUTING)printAdvertisementsAndSubscriptions();
				
			//Unsubscription
        	} else if(evt.getEventType().equals(ManagementEvent.UNSUBSCRIPTION)) {        		
        		
        		if(D)Log.d(TAG, "Unsubscription event from "+evt.getProducerID());
        		        		
        		String unsubscribersPackageName = ((Unsubscription)evt).getPackageName();
        		String unsubscriptionSensorReadings = ((Unsubscription)evt).getUnsubscriptionSensorReadings();
        		
        		//Check if Event is already subscribed
        		if(subscriberChannelHashMap.containsKey(unsubscriptionSensorReadings)){
        			List<String> tempList = subscriberChannelHashMap.get(unsubscriptionSensorReadings);

        			if(tempList.contains(unsubscribersPackageName)){
        				//Check all effected producers and send a StartEvent if no one is already subscribed to it
						unsubscriptionCheckWithAllAvailableEventsThatMatch(unsubscriptionSensorReadings);		//Important: Check before removing!
        				tempList.remove(unsubscribersPackageName);
        				if(D)Log.d(TAG,"Unsubscription successful: EventTypeShort: "+getLastStringAfterDot(unsubscriptionSensorReadings)+", PackageNameShort: "+getLastStringAfterDot(unsubscribersPackageName));
        				//Unsubscription successful write to database
        				db.openWritabelDB();
        				db.insertOrReplaceRecord(evt.getEventType(), evt.getID(), evt.getTimestamp() /*evtUtils.getTimestamp() */, evt.getProducerID(), unsubscribersPackageName, unsubscriptionSensorReadings);
        				if(D)Log.d(TAG, "insertRecord("+evt.getEventType()+", "+evt.getID()+", "+evt.getTimestamp()+", "+evt.getProducerID()+", "+unsubscribersPackageName+", "+unsubscriptionSensorReadings+")");
        				db.close();
        			}
        			if(tempList.isEmpty()){
        				// delete key from HashMap if there are no more subscriptions for this EventType
        				subscriberChannelHashMap.remove(unsubscriptionSensorReadings);
        				if(D)Log.d(TAG,"No more Subscribers delte Entry in subscribersHashMap for EventTypeShort: "+getLastStringAfterDot(unsubscriptionSensorReadings));
        			}
        		}
        		
        		if(D_ROUTING)printAdvertisementsAndSubscriptions();
        		
        	//Announcement	
        	} else if(evt.getEventType().equals(ManagementEvent.ANNOUNCEMENT)){
    			int announcement = ((Announcement)evt).getAnnouncement();
    			String packageName = ((Announcement)evt).getPackageName();
    			String eventType = ((Announcement)evt).getTransmittedEventType();
    			String sender = ((Announcement)evt).getProducerID();
        		
    			printAnnouncement(evt);
    			
        		switch (announcement) {
				case Announcement.SENSOR_CONNECTED:
				case Announcement.SENSOR_DISCONNECTED:
					// Distribute announcement via LocalBroadcastManager for SensorConfigurationActivity
					sendToManagementChannel(evt, getPackageName());					
					
    				// Distribute announcement to subscribed consumers
					List<String> receiverPackageNameList = getSubscribersListForEventType(eventType);
    				if(receiverPackageNameList!=null){
    					sendAnnouncementToRecieverList(announcement, eventType, receiverPackageNameList);
    				}
    				
    				// store state for future subscriptions
    				sensorConnectivityAnnouncement.put(eventType, announcement);
    				if(D_ANNOUNCEMENT)Log.d(TAG,"Announcement Received: "+announcement+" from: "+sender);
				break;
				
				case Announcement.GET_ALL_AVAILABLE_EVENT_TYPES:						
						sendAllAvailableEventTypes(packageName);
						if(D_ANNOUNCEMENT)Log.d(TAG,"Announcement Received: sendAllAvailableEventTypes from: "+sender);
					break;
				
				case Announcement.DATABASE_CLEAR_ALL:
						if(D_ANNOUNCEMENT)Log.d(TAG_ANNOUNCEMENT, "Announcement Received: DATABASE_CLEAR_ALL from: "+sender);
						db.openWritabelDB();
						db.deleteAll();
						db.close();
					break;
					
				case Announcement.DATABASE_SHOW_WITH_ALL_SUBSCRIPTIONS_AND_UNSUBSCRIPTIONS:
					if(D_ANNOUNCEMENT)Log.d(TAG_ANNOUNCEMENT, "Announcement Received: DATABASE_SHOW_WITH_ALL_SUBSCRIPTIONS_AND_UNSUBSCRIPTIONS from: "+sender);
						db.openReadableDB();
				    	Cursor c = db.getAllRecords();
				    	if(c.moveToFirst()){
				    		if(D_ANNOUNCEMENT)Log.d(TAG_ANNOUNCEMENT,"c.moveToFirst() == "+c.moveToFirst());
				    		while(!c.isAfterLast()){
				    			displayRecord(c);
				    			c.moveToNext();
				    		}
				    	}
				    	db.close();
				    							
					break;
										
				default:
					break;
				}
        	}
    	};
    }
    
    
    /**
     * Sends an EventTransformationsRequest for the given event type and all available events.
     * @param requested event type
     */
    public void sendEventTransformationRequest(String subscriptionSensorReadings) {
    	String[] allAvailableEvents = getAllAvailableEvents();

    	// Create request
		EventTransformationRequest request = 
				createRequestEvent(EventTransformationRequest.TYPE_REQUEST_TRANSFORMATION, 
						allAvailableEvents, 
						subscriptionSensorReadings);
		
		// Broadcast event locally
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, request.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, request);
		i.setAction(AbstractChannel.LOCAL_MANAGEMENT);
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);	
	}

	/**
	 * Sends all available event types from the advertisementHashMap as an announcement using the package names for explicit addressing.
	 * @param packageName used to setPackageName(packageName)
	 */
    private void sendAllAvailableEventTypes(String packageName) {
		String[] availableEventTypes = getAllAvailableEvents();
		Announcement announcement = new Announcement(
				evtUtils.getEventID(), 
				evtUtils.getTimestamp(), 
				TAG, 
				"", 
				getPackageName(), 
				Announcement.ALL_AVAILABLE_EVENT_TYPES);

		Intent intent = new Intent();
		intent.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, announcement.getEventType());
		intent.putExtra(Event.PARCELABLE_EXTRA_EVENT, announcement);
		intent.putExtra(Event.PARCELABLE_EXTRA_AVAILABLE_EVENTS, availableEventTypes);
		if(D)Log.d(TAG,"availableEventTypes String[]"+availableEventTypes);
		//Implicit addressing
		intent.setAction(AbstractChannel.MANAGEMENT);
		//Explicit addressing
    	intent.setPackage(packageName);
    	getApplicationContext().sendBroadcast(intent);
	}

    /**
     * Sends a StopEvent to all producers who have exactly one subscriber now and will have zero after removing.
     * @param unsubscriptionSensorReadings event type that gets unsubscribed
     */
    private void unsubscriptionCheckWithAllAvailableEventsThatMatch(String unsubscriptionSensorReadings) {
    	String[] allAvailableEventTypes = getAllAvailableEvents();
		List<String> matchUnsubscriptionAvailableEventTypes = new ArrayList<String>();
		
		if(allAvailableEventTypes == null)return;
		
		//Get all availableEvents that start with channel subscription
		for(int i=0;i<allAvailableEventTypes.length;i++){
			if(allAvailableEventTypes[i].startsWith(unsubscriptionSensorReadings)){
				matchUnsubscriptionAvailableEventTypes.add(allAvailableEventTypes[i]);
			}
		}
		//Needs to check with each possible matching event, due to the tree structure and possibility to unsubscribe on a node or leaf
		//Send a StopEvent to all producers, who have exactly one subscribers yet and will have zero after removing
		for(int j=0;j<matchUnsubscriptionAvailableEventTypes.size();j++){
			//StopEvent to all producers only if no other channel is subscribed on this EventType already
			if(getSubscribersListForEventType(matchUnsubscriptionAvailableEventTypes.get(j)).size() == 1){
				sendStopEventToAllProducersForEventType(matchUnsubscriptionAvailableEventTypes.get(j));
			}
		}	
	}

    /**
     * Sends a StartEvent to all producers, who have zero subscriber now and will have one after removing.
     * @param subscriptionSensorReadings EventType that gets subscribed for
     */
	private void subscriptionCheckWithAllAvailableEventsThatMatch(String subscriptionSensorReadings) {
		String[] allAvailableEventTypes = getAllAvailableEvents();
		List<String> matchSubscriptionAvailableEventTypes = new ArrayList<String>();
		
		if(allAvailableEventTypes==null)return;
		
		//Get all availableEvents that start with channel subscription
		for(int i=0;i<allAvailableEventTypes.length;i++){
			if(allAvailableEventTypes[i].startsWith(subscriptionSensorReadings)){
				matchSubscriptionAvailableEventTypes.add(allAvailableEventTypes[i]);
			}
		}
		//Needs to check with each possible event, due to the tree structure and possibility to subscribe on a node or leaf
		//Send a StartEvent to all producers, who have no subscribers yet and will have exactly 1 after subscription
		for(int j=0;j<matchSubscriptionAvailableEventTypes.size();j++){
			//StartEvent to all producers only if no other channel is subscribed on this EventType already
			if(getSubscribersListForEventType(matchSubscriptionAvailableEventTypes.get(j)) == null){
				sendStartEventToAllProducersForEventType(matchSubscriptionAvailableEventTypes.get(j));
			}
		}	
	}

	/**
	 * Sends a StopEvent to all producers in the advertismentHashMap for the given event type.
	 * @param eventType
	 */
	private void sendStopEventToAllProducersForEventType(String eventType) {
		List<ProducerDetails> producersDetailsList = advertisementHashMap.get(eventType);
		if(producersDetailsList==null)return;
		for(int i=0;i<producersDetailsList.size();i++){
			sendStopEvent(producersDetailsList.get(i).getProducerID(), eventType, producersDetailsList.get(i).getProducerPackageName());
		}
	}

	/**
	 * Sends a StartEvent to all producers in the advertismentHashMap for the given event type
	 * @param eventType
	 */
	private void sendStartEventToAllProducersForEventType(String eventType) {
		List<ProducerDetails> producersDetailsList = advertisementHashMap.get(eventType);
		if(producersDetailsList==null)return;
		for(int i=0;i<producersDetailsList.size();i++){
			sendStartEvent(producersDetailsList.get(i).getProducerID(), eventType, producersDetailsList.get(i).getProducerPackageName());
		}
	}

	/**
	 * Different checks if the given Event is valid. Announcements are sent accordingly to the violation.
	 * @param evt The Event that has to be checked
	 * @return returns true if all checks are passed successful
	 */
    public boolean isManagementEventValid(Event evt) {
    	String packageName = null;
    	String transmittedEventType =null;
    	
    	//discard invalid EventTypes
    	if(evt.getEventType()== null)return false;
    	

    	//Advertisement
    	if(evt.getEventType().equals(ManagementEvent.JSON_DATA_EXCHANGE)){
    		packageName = ((JSONDataExchange)evt).getPackageName();
    		transmittedEventType = ((JSONDataExchange)evt).getDataExchangeEventType();
    		
    		String json = ((JSONDataExchange)evt).getJSONEncodedData();
    		if(json==null){
    			sendAnnouncement(Announcement.INVALID_EVENT_TYPE_ARGUMENTS, transmittedEventType, packageName);
    			return false;
    		}
    	}
    	//Advertisement
    	if(evt.getEventType().equals(ManagementEvent.ADVERTISEMENT)){
    		packageName = ((Advertisement)evt).getPackageName();
    		transmittedEventType = ((Advertisement)evt).getAdvertisedEventType();
    		
    		String json = ((Advertisement)evt).getJSONEncodedProperties();
    		if(json==null){
    			sendAnnouncement(Announcement.INVALID_EVENT_TYPE_ARGUMENTS, transmittedEventType, packageName);
    			return false;
    		}
    	
    	//Unadvertisement
    	}else if(evt.getEventType().equals(ManagementEvent.UNADVERTISEMENT)){
    		packageName = ((Unadvertisement)evt).getPackageName();
    		transmittedEventType = ((Unadvertisement)evt).getUnadvertisedEventType();
    	
    	//Subscription
    	}else if(evt.getEventType().equals(ManagementEvent.SUBSCRIPITON)){
    		packageName = ((Subscription)evt).getPackageName();
    		transmittedEventType = ((Subscription)evt).getSubscriptionSensorReadings();
    		
    		/*
    		//Check if application(packageName) has permission to subscribe to this EventType
    		if(!securityManager.hasPermission(packageName, transmittedEventType)){
    			sendAnnouncement(ManagementEvent.SUBSCRIPTION_UNSUCCESSFULL_NO_PERMISSION, transmittedEventType, packageName);
    			return false;
    		} 
    		*/
    	
    	//Unsubscription
    	}else if(evt.getEventType().equals(ManagementEvent.UNSUBSCRIPTION)){
    		packageName = ((Unsubscription)evt).getPackageName();
    		transmittedEventType = ((Unsubscription)evt).getUnsubscriptionSensorReadings();
    	
    	//Announcement
    	}else if(evt.getEventType().equals(ManagementEvent.ANNOUNCEMENT)){
    		packageName = ((Announcement)evt).getPackageName();
    		//none transmitted, set to valid value to support the testing scheme
    		transmittedEventType = Event.EVENT_ROOT;
	    }
		   
    	// If any Event variables are null
    	if(evt.getID() == null || evt.getProducerID() == null || evt.getTimestamp()==null){
    		Log.w(TAG,"Error at least one Event argument is null");
    		sendAnnouncement(Announcement.INVALID_EVENT_TYPE_ARGUMENTS, transmittedEventType, packageName);
    		return false;
    	}
    	
    	
    	//If EventType does not follow conventions
		if(!doesEventTypeMatchConventions(transmittedEventType)){
			Log.w(TAG,"Error EVENT_TYPE_DOES_NOT_MATCH_NAME_CONVENTION");
			sendAnnouncement(Announcement.EVENT_TYPE_DOES_NOT_MATCH_NAME_CONVENTION, transmittedEventType, packageName);
			return false;
		}
		
		//Check if application(packageName) is installed on device
		if(!isPackageInstalled(packageName)){
			//Probably not useful to send this announcement
			Log.w(TAG, "PackageName invalid:"+packageName);
			sendAnnouncement(Announcement.MANAGEMENTEVENT_UNSUCCESSFULL_APPLICATION_NOT_INSTALLED, transmittedEventType, packageName);
			return false;
		} 		
		return true;
	}

	/**
     * Check if the event type matches event type conventions
     * @param eventType
     * @return returns true if check is passed
     */
    public boolean doesEventTypeMatchConventions(String eventType){
    	if(eventType == null)return false;
    	if(eventType.startsWith(Event.EVENT_ROOT))return true;
    	return false;
    }
    
    /**
     * Sends an announcement event to the given announcement receiver package name ManagementReceiver
     * @param typeOfAnnouncement Types defined in ManagmentEvent.STRING
     * @param transmittedEventType
     * @param announcementReceiverPackageName Used for explicit addressing - setPackageName(packageName)
     */
    private void sendAnnouncement(int typeOfAnnouncement, String transmittedEventType, String announcementReceiverPackageName){
    	
    	//if no packageName is set do not send an announcement
    	if(announcementReceiverPackageName==null) return;
    	    	
    	Announcement announcement = new Announcement(
    			evtUtils.getEventID(), 
    			evtUtils.getTimestamp(), 
    			TAG, 
    			transmittedEventType, 
    			announcementReceiverPackageName, 
    			typeOfAnnouncement);
    	
    	sendToManagementChannel(announcement, announcementReceiverPackageName);
    	/*Intent intent = new Intent();
		intent.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, announcement.getEventType());
		intent.putExtra(Event.PARCELABLE_EXTRA_EVENT, announcement);
    	//Implicit addressing
		intent.setAction(ReadingChannels.MANAGEMENT);
		//Explicit addressing
		printAnnouncement(announcement);
		
		intent.setPackage(announcementReceiverPackageName);
    	
    	getApplicationContext().sendBroadcast(intent);*/	
    }
    
    /**
     * Sends announcements events to the given receiver package names list
     * @param typeOfAnnouncement Types defined in ManagmentEvent.STRING
     * @param eventType
     * @param listOfRecievers List of receiver package names used to explicit address ManagementReceivers
     */
    private void sendAnnouncementToRecieverList(int typeOfAnnouncement, String eventType, List<String> listOfRecievers){
    	for(int i=0;i<listOfRecievers.size();i++){
    		sendAnnouncement(typeOfAnnouncement, eventType, listOfRecievers.get(i));
    	}
    }
    
    /**
     * Sends a start event to the given package names ManagementReceiver
     * @param eventProducerID
     * @param startEventType
     * @param packageName Receivers package name used to explicit address ManagementReceivers
     */
    private void sendStartEvent(String eventProducerID, String startEventType, String packageName){
    	StartProducer startEvent = new StartProducer(
    			evtUtils.getEventID(), 
    			evtUtils.getTimestamp(), 
    			TAG, 
    			eventProducerID, 
    			startEventType);
    	sendToManagementChannel(startEvent, packageName);
    	Log.i(TAG, "StartProducer Sensor: "+eventProducerID+", EventTypeShort: "+getLastStringAfterDot(startEventType)+", PackageNameShort: "+getLastStringAfterDot(packageName));
    }
    
    /**
     * Sends a StopEvent to the given package names ManagementReceiver
     * @param eventProducerID
     * @param stopEventType
     * @param packageName Receivers package name used to explicit address ManagementReceiver
     */
    private void sendStopEvent(String eventProducerID, String stopEventType, String packageName){
		StopProducer stopEvent = new StopProducer(
				evtUtils.getEventID(),
				evtUtils.getTimestamp(), 
				TAG, 
				eventProducerID, 
				stopEventType);
    	sendToManagementChannel(stopEvent, packageName);		
    	Log.i(TAG, "StopProducer Sensor: "+eventProducerID+", EventTypeShort: "+getLastStringAfterDot(stopEventType)+", PackageNameShort: "+getLastStringAfterDot(packageName));
    }
    
    
    /**
     * Sends an event to the local or global management channel depending on
     * the packageName. If the packageName equals the myHealthHub package name, 
     * the event is send locally.
     * @param event that needs to be send
     * @param packageName of the receiver
     */
    private void sendToManagementChannel(Event evt, String packageName) {
    	Intent intent = new Intent();
		intent.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
		intent.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
    	
		// use local management channel for internal sensor modules
    	if(packageName.equals(getPackageName())) {
    		if(D)Log.d(TAG, "Sending on local management channel:"+evt.getEventType());
    		intent.setAction(AbstractChannel.LOCAL_MANAGEMENT);
    		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    	
    	// use global management channel for external sensor modules (i.e. other applications)
    	} else {
    		if(D)Log.d(TAG, "Sending on global management channel:"+evt.getEventType());
    		//Implicit addressing
    		intent.setAction(AbstractChannel.MANAGEMENT);
    		//Explicit addressing    		
    		intent.setPackage(packageName);
    		getApplicationContext().sendBroadcast(intent);
    	}
    }
    
    /**
     * Creates a EventTransformationRequest
     * @param requestType
     * @param advertisedEvents
     * @param eventSubscription
     * @return EventTransformationRequest
     */
	private EventTransformationRequest createRequestEvent(int requestType, String[] advertisedEvents, String eventSubscription) {
		return new EventTransformationRequest(
				evtUtils.getEventID(), 
				evtUtils.getTimestamp(), 
				TAG, 
				requestType, 
				advertisedEvents, 
				eventSubscription);
	}
    
	/**
	 * Get all available events from the advertismentHashMap
	 * @return String[] containing all available event types
	 */
    public String[] getAllAvailableEvents(){
    	//Android documentation: The set does not support adding
    	Set<String> availableEvents = advertisementHashMap.keySet();
    	if(availableEvents==null)return null;
    	String[] events = availableEvents.toArray(new String[availableEvents.size()]);
    	for(int i=0;i<events.length;i++){
    		if(D||D_ROUTING)Log.d(TAG,"String["+i+"] = Value: "+events[i]);
    	}
    	return events;
    }
        
    /**
     * Checks the subscribers permission for the channel/event type with a list of allowed subscribers for the channel/event type
     * @param eventType
     * @param allowedSubscribersPackageNamesList
     */
    public void checkPermissionsForEventType(String eventType, List<String> allowedSubscribersPackageNamesList){
    	
    	List<String> subscribersPackageNames = subscriberChannelHashMap.get(eventType);
    	List<String> toBeUnsubscribedPackageNamesList = new ArrayList<String>();
    	//If there are subscribers verify them with allowedSubscribersPackageNamesList
    	if(subscribersPackageNames!=null){
    		for(int i=0;i<subscribersPackageNames.size();i++){
    			//Check if subscriber still has the permission
    			if(allowedSubscribersPackageNamesList.contains(subscribersPackageNames.get(i))){
    				if(D)Log.d(TAG,"Permission verified successfull" + subscribersPackageNames.get(i));
    			}else{
    				if(D)Log.d(TAG,"Permission not verified - unsubscribe" + subscribersPackageNames.get(i));
    				sendAnnouncement(Announcement.SECURITY_PERMISSION_REMOVED, eventType, subscribersPackageNames.get(i));
    				toBeUnsubscribedPackageNamesList.add(subscribersPackageNames.get(i));
    			}
    		}
    	}
    	sendUnsubscriptionsForAllInvalidSubscribers(eventType, toBeUnsubscribedPackageNamesList);
    }
    
    /**
     * Sends an Unsubscription event for all subscribers package names in the list
     * @param eventType
     * @param toBeUnsubscribedPackageNamesList Package names to be removed from the subscribersHashMap
     */
    private void sendUnsubscriptionsForAllInvalidSubscribers(String eventType, List<String> toBeUnsubscribedPackageNamesList) {
    	for(int i=0;i<toBeUnsubscribedPackageNamesList.size();i++){
    		sendUnsubscription(eventType, toBeUnsubscribedPackageNamesList.get(i));
		}
	}
    
    /**
     * Sends an Unsubscription event to the subscribers package name for the given event type
     * @param eventType
     * @param toBeUnsubscribedPackageName
     */
    private void sendUnsubscription(String eventType, String toBeUnsubscribedPackageName){
    	Unsubscription unsubcribe = new Unsubscription(evtUtils.getEventID(), evtUtils.getTimestamp(), TAG, toBeUnsubscribedPackageName, eventType);
    	Intent intent = new Intent();
		intent.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, unsubcribe.getEventType());
		intent.putExtra(Event.PARCELABLE_EXTRA_EVENT, unsubcribe);
		intent.setAction(AbstractChannel.MANAGEMENT);
		sendBroadcast(intent);
    }

	/**
     * Parses event type filed and returns String array representing
     * the tree structure of the event type. myHealhAssistant prefix
     * is removed.
     * @param EVENT_TYPE Event type.
     * @return String[] including event tree. 
     */
	private String[] parseEventType(String eventType) {
				
		// Return null if not myHealthAssistant prefix
		if(!eventType.startsWith(Event.EVENT_ROOT)) return null;
		
		// Remove prefix
		eventType = eventType.substring(Event.EVENT_ROOT.length()+1, eventType.length());
		
		// Store fields into String array
		String[] eventTree = eventType.split("\\.");
		
		return eventTree;
	}
	
    /**
     * Get all subscribers (for each step in the event tree) for the event type, according to the tree structure and the possibility to subscribe to a node or leaf
     * 
     * @return: a list of package names or null if there are no receivers
     */
    private List<String> getSubscribersListForEventType(String eventType){
    	
    	// Parse event type into an event tree
    	String[] eventTree = parseEventType(eventType);
    	
    	// Discard invalid events
    	if(eventTree == null) {
    		Log.e(TAG, "Invalid incoming event type: "+eventType);
    		return null;
    	}
    	
    	List<String> listOfReceivers = new ArrayList<String>();
    	List<String> tempSubscriberList;
    	String channelName = Event.EVENT_ROOT;
    	
    	//Build a list of receivers referring to the event tree subscriptions
    	for(int i=0;i<eventTree.length;i++){
    		channelName=(channelName+"."+eventTree[i]);
    		//Log.d(TAG,"Search key for HashMap: "+channelName);
    		tempSubscriberList = subscriberChannelHashMap.get(channelName);
    		if(tempSubscriberList!=null){       			
    			//Log.d(TAG,"Length HashMap result: "+tempSubscriberList.size());
    			listOfReceivers.addAll(tempSubscriberList);
    		}
    	}
    	if(listOfReceivers.size()!=0){
    		return listOfReceivers;
    	}
    	return null;
    }
    
    /**
     * Verifies if the package is installed using the PackageManager and the given package name
     * @param packageName
     * @return Returns true if package is installed
     */
	public boolean isPackageInstalled(String packageName) {
		if(packageName==null)return false;		
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return false;
		}
		return true;
	}
    
    /**
     * SecurityManager: quick and dirty implementation for testing only
     */
    private class SecurityManager{
    	private HashMap<String, List<String>> permissionHashMap;    	
    	private List<String> listOfPermissionApplication01 = new ArrayList<String>();
    	private List<String> listOfPermissionApplication02 = new ArrayList<String>();
    	
    	public SecurityManager(){
    		permissionHashMap = new HashMap<String, List<String>>();
    		listOfPermissionApplication01.add(SensorReadingEvent.READING_EVENT);
    		listOfPermissionApplication01.add(SensorReadingEvent.ACCELEROMETER);
    		listOfPermissionApplication02.add(SensorReadingEvent.HEART_RATE);
    		permissionHashMap.put("com.example.myhealthhubsetpackage.application01", listOfPermissionApplication01);
    		permissionHashMap.put("com.example.myhealthhubsetpackage.application02", listOfPermissionApplication02);   		
    	}

        /**
         * Checks if a application has permission to subscribe on channel
         */
		private boolean hasPermission(String packageName, String channel){
    		List<String> tempList = new ArrayList<String>();
    		tempList = permissionHashMap.get(packageName);
    		if(tempList!= null){
    			if(tempList.contains(channel)){
    				if(D)Log.d(TAG,"True: Permission is avaiable: "+channel);
    				return true;
    			}
    		}
    		if(D)Log.d(TAG,"False: Permission is avaiable: "+channel);
			return false;
		}
    }
    
    /** EventTransformation event receiver */
    public class ResponseReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// Skip if not a event transformation response
			if(!intent.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE).equals(EventTransformationResponse.EVENT_TYPE)) return;
						
			// Get and print event
			EventTransformationResponse response = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
			if(D)Log.d(TAG, "Got result "+response.isTransformationFound()+" for request ID: "+response.getID()+".");
		}
    }
    
    /**
     * Returns the last String separated by "."
     * @param name
     * @return Returns last String separated by "."
     */
    private String getLastStringAfterDot(String name){
    	if(name.contains(".") && name.length()>=3){
    		String[] nameSplit = name.split("\\.");
        	return nameSplit[nameSplit.length-1];
    	}
    	return "Error String does not contain '.' or is to short)";
    }
    
    /**
     * Used for connecting a producerID with a package name to explicit address the producer
     * @author Jens
     *
     */
    private class ProducerDetails{
    	private String ProducerID;
    	private String ProducerPackageName;
    	
    	public ProducerDetails(String producerID, String producerPackageName){
    		ProducerID = producerID;
    		ProducerPackageName = producerPackageName;
    	}

		public String getProducerID() {
			return ProducerID;
		}

		public void setProducerID(String producerID) {
			ProducerID = producerID;
		}

		public String getProducerPackageName() {
			return ProducerPackageName;
		}

		public void setProducerPackageName(String producerPackageName) {
			ProducerPackageName = producerPackageName;
		}
    }
    
	
	@Override
	public void onCreate() {		
	    Log.i(TAG, getPackageName());
		
		// Initialize event RECEIVER
	    mEventReceiver = new EventReceiver();
	    getApplication().registerReceiver(mEventReceiver, sensorReadingsChannel);
	    LocalBroadcastManager.getInstance(this).registerReceiver(mEventReceiver, sensorReadingsChannel);
	    
	    // Notification Receiver
	    mNotificationReceiver = new NotificationReceiver();
	    getApplication().registerReceiver(mNotificationReceiver, notificationChannel);
	    
	    // Management Receiver
	    mManagementReceiver = new ManagementReceiver();
	    getApplication().registerReceiver(mManagementReceiver, managementReceiverChannel);
	    LocalBroadcastManager.getInstance(this).registerReceiver(mManagementReceiver, managementReceiverChannel);
	    
	    mResponseReceiver = new ResponseReceiver();
	    LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver, mResponseReceiverChannel);


	    subscriberChannelHashMap = new HashMap<String, List<String>>();
	    advertisementHashMap = new HashMap<String, List<ProducerDetails>>();
	    sensorConnectivityAnnouncement = new HashMap<String, Integer>();
	    //securityManager = new SecurityManager();
	    evtUtils = new EventUtils(NotificationEvent.EVENT_TYPE, TAG);

	    //mTransformationManager = new TransformationManager(this, AbstractChannel.LOCAL_MANAGEMENT);
	    
	    db = new DBAdapterSubscriptions(this);
	    restoreSubscriptionsFromDB();
	}
	
	/**
	 * Starts to restore the subscriberHashMap from a database
	 */
	private void restoreSubscriptionsFromDB() {
		Log.i(TAG,"Restore Subscriptions From DB");
		
		db.openReadableDB();
    	Cursor c = db.getAllRecords();
    	if(c.moveToFirst()){    		
    		while(!c.isAfterLast()){
    			sendSubscription(c);
    			c.moveToNext();
    		}
    	}	
    	db.close();
    	printAdvertisementsAndSubscriptions();
	}

	/**
	 * Creates Subscriptions 
	 * @param c Cursor fetched from database with the necessary information to create a Subscription
	 */
	private void sendSubscription(Cursor c) {
		String eventType = c.getString(1);
		String eventID = c.getString(2);
		String timeStamp = c.getString(3);
		String producerID = c.getString(4);
		String packageName = c.getString(5);
		String subscribedEventType = c.getString(6);
		
		Intent intent = new Intent();
		
		if(eventType.equals(ManagementEvent.SUBSCRIPITON)){
			Subscription subscribe = new Subscription(eventID, timeStamp, producerID, packageName, subscribedEventType);
			intent.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, subscribe.getEventType());
			intent.putExtra(Event.PARCELABLE_EXTRA_EVENT, subscribe);
			if(D_ANNOUNCEMENT)Log.d(TAG_ANNOUNCEMENT, "Subscription sent: "+producerID+", packageNameShort: "+getLastStringAfterDot(packageName)+", subscribedEventTypeShort: "+getLastStringAfterDot(subscribedEventType));
			intent.setAction(AbstractChannel.MANAGEMENT);
			intent.setPackage(getPackageName());
			sendBroadcast(intent);
		}
		//No need to send Unsubscription
		 	else if(eventType.equals(ManagementEvent.UNSUBSCRIPTION)){
			Unsubscription unsubscribe = new Unsubscription(eventID, timeStamp, producerID, packageName, subscribedEventType);
			//intent.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, unsubscribe.getEventType());
			//intent.putExtra(Event.PARCELABLE_EXTRA_EVENT, unsubscribe);
			if(D_ANNOUNCEMENT)Log.d(TAG_ANNOUNCEMENT, "Unsubscription restored but not sent: "+producerID+", packageNameShort: "+getLastStringAfterDot(packageName)+", subscribedEventTypeShort: "+getLastStringAfterDot(subscribedEventType));
		}	
	}

		
	@Override
	public IBinder onBind(Intent intent) {
	    Log.d(TAG, "onBind(): entered...");
	    return null;
	}
	
	public void onUnbind() {
		if(D)Log.d(TAG, "onUnbind(): entered...");
	}
	
	public void onDestroy() {
		if(D)Log.d(TAG, "onDestroy() entered...");		
		getApplication().unregisterReceiver(mEventReceiver);		
		getApplication().unregisterReceiver(mNotificationReceiver);
	}
	
	//TODO: Can be removed after testing
	/**
	 * Prints an announcement to LogCat using TAG_ANNOUNCEMENT
	 * @param evt
	 */
	public void printAnnouncement(Event evt) {
		int announcement = ((Announcement)evt).getAnnouncement();
		String packageName = ((Announcement)evt).getPackageName();
		String eventType = ((Announcement)evt).getTransmittedEventType();
		String sender = ((Announcement)evt).getProducerID();
		
		if(D_ANNOUNCEMENT)Log.d(TAG_ANNOUNCEMENT, "Announcement Details: "+sender+", AnnouncementType: "+announcement+", PackageNameShort: "+getLastStringAfterDot(packageName)+", EventTypeShort: "+getLastStringAfterDot(eventType));	
	}
	
	/**
	 * Prints an database entry to LogCat using TAG_ANNOUNCEMENT
	 * @param c
	 */
    private void displayRecord(Cursor c) {
		Log.d(TAG,"DisplayRecord: ");
    	Log.d(TAG,
				""+DBAdapterSubscriptions.KEY_ROWID+" : "+c.getString(0)+"\n"
				+DBAdapterSubscriptions.KEY_EVENT_TYPE+" : "+c.getString(1)+"\n"
				+DBAdapterSubscriptions.KEY_EVENT_ID+" : "+c.getString(2)+"\n"
				+DBAdapterSubscriptions.KEY_TIMESTAMP+" : "+c.getString(3)+"\n"
				+DBAdapterSubscriptions.KEY_PRODUCER_ID+" : "+c.getString(4)+"\n"
				+DBAdapterSubscriptions.KEY_PACKAGE_NAME+" : "+c.getString(5)+"\n"
				+DBAdapterSubscriptions.KEY_READING_EVENT_TYPE+" : "+c.getString(6)+"\n"
		);
	}
    
    /**
     * Prints lists of advertised and subscribed events.
     */
    private void printAdvertisementsAndSubscriptions() {
    	Log.d(TAG_ROUTING, "--\nList of Advertisements:");
    	Log.d(TAG_ROUTING, "=======================");
    	getAllAvailableEvents();
    	
    	Log.d(TAG_ROUTING, "\nList of Subscriptions:");
    	Log.d(TAG_ROUTING, "======================");
    	String output = "";
    	Iterator<Entry<String, List<String>>> it = subscriberChannelHashMap.entrySet().iterator();
    	while(it.hasNext()) {
    		Entry<String, List<String>> e = it.next();
    		output += e.getKey() +"\t | ";
    		for(String channel : e.getValue()) {
    			output += channel + ", ";
    		}
    		output += "\n";
    	}
    	Log.d(TAG_ROUTING, output+"\n--");
    }
	
}
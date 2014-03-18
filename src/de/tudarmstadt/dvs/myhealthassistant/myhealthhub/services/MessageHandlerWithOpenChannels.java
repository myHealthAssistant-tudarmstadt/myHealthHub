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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.mail.GMailSender;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.notifications.NotificationEvent;

/**
 * @author Christian Seeger
 *
 */
public class MessageHandlerWithOpenChannels extends Service {
	
	/* for debugging */
	private static String TAG = "MessageHandlerWithOpenChannels";
	private static boolean D = true;
	
	private final IntentFilter notificationChannel = new IntentFilter(AbstractChannel.NOTIFICATION);
	private NotificationReceiver mNotificationReceiver;
	
	private final IntentFilter sensorReadingChannel = new IntentFilter(AbstractChannel.RECEIVER);
	//private final IntentFilter sensorReadingChannel = new IntentFilter(AbstractChannel.RECEIVER);
	private SensorReadingReceiver mSensorReadingReceiver;
	
	@Override
	public void onCreate() {
		if(D)Log.d(TAG, TAG+": onCreate");
		
		// Register sensor reading receiver
	    mSensorReadingReceiver = new SensorReadingReceiver();
	    getApplication().registerReceiver(mSensorReadingReceiver, sensorReadingChannel);	 
	    
	    // Register notification receiver
	    mNotificationReceiver = new NotificationReceiver();
	    getApplication().registerReceiver(mNotificationReceiver, notificationChannel);
	    
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		if(D)Log.d(TAG, TAG+": onDestroy");
				
		// unregister broadcast receivers
		getApplication().unregisterReceiver(mSensorReadingReceiver);
		getApplication().unregisterReceiver(mNotificationReceiver);
		
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		if(D)Log.d(TAG, TAG+": onBind");
		return null;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		if(D)Log.d(TAG, TAG+": onUnbind");
		return super.onUnbind(intent);
	}
	
	
	/** Event Handler */
    private class SensorReadingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if(intent == null) {
        		Log.e(TAG, "Intent is null");
        		return;
        	}
        	
        	String eventType = intent.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE);
        	if(D)Log.d(TAG, "Received event of type: "+eventType);
        	
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

        	// distribute along the tree
        	String channel = "de.tudarmstadt.dvs.myhealthassistant";
        	channel = Event.EVENT_ROOT;
        	for(int i = 0; i<eventTree.length; i++) {
        		channel+="."+eventTree[i];
        		publishInChannel(channel, intent);
        	}
        }
        
    	private void publishInChannel(String channel, Intent i) {
        	i.setAction(channel);
        	getApplicationContext().sendBroadcast(i);
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
			eventType = eventType.substring(
					Event.EVENT_ROOT.length()+1, eventType.length());
			
			// Store fields into String array
			String[] eventTree = eventType.split("\\.");
			
			return eventTree;
		}
    };
    
    /**
     * 
     * @author chris
     *
     */
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
						GMailSender sender = new GMailSender(
								null,
								null);
						sender.sendMail(subject, body,
								null,
								null);
					} catch (Exception e) {
						Log.e("SendMail", e.getMessage(), e);
					}
				}
			};
			thread.start();
		}
	}    
}
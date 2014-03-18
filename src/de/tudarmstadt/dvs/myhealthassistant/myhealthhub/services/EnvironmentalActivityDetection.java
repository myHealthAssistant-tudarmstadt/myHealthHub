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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Vector;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.Preferences;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.AbstractEnvironmentalEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.EnvironmentActivityEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.xml.ECARule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.xml.ExtendedECARule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.xml.XMLtoECARule;

/**
 * @author Christian Seeger
 *
 */
public class EnvironmentalActivityDetection extends Service {
	/* for debugging */
	private boolean D = false;
	private String TAG = "EnvironmentalActivityDetection";

	/* event bus */
	private final static String PRODUCER_ID = "de.tudarmstadt.dvs.myhealthhub.EnvironmentalActivityDetection";
	protected String myHealthHubReceiver = AbstractChannel.RECEIVER;
	
	/* Define event channel and event RECEIVER */
	// TODO: does not work anymore
	private final IntentFilter readingChannel = new IntentFilter(AbstractEnvironmentalEvent.EVENT_TYPE);
	private readingEventReceiver mReadingEventReceiver;
	
	private SensorUtilityList sensorUtilityList;
	
	private HashMap<Integer, Runnable> runningTemporaryRules;
	Handler temporaryThreadsHandler;
	
	private HashMap<String, Vector<ECARule>> listOfRules;
	private HashMap<String, Vector<ECARule>> listOfTemporaryRules;
	
	@Override
	public void onCreate() {
		if (D) Log.d(TAG, "Initilize event RECEIVER.");
		
		mReadingEventReceiver = new readingEventReceiver();
		getApplication().registerReceiver(mReadingEventReceiver, readingChannel);

		sensorUtilityList = new SensorUtilityList();
		
		temporaryThreadsHandler = new Handler();
		runningTemporaryRules = new HashMap<Integer, Runnable>();
		
		listOfRules = new  HashMap<String, Vector<ECARule>>();
		listOfTemporaryRules = new  HashMap<String, Vector<ECARule>>();

		parseAndLoadXMLRules();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {				
		return mBinder;
	}
	
	private final IBinder mBinder = new ServiceBinder();
	public class ServiceBinder extends Binder {
		public void reloadXMLRules() {
			parseAndLoadXMLRules();
		}
	}
	
	private void parseAndLoadXMLRules() {
		String xmlFile = PreferenceManager.getDefaultSharedPreferences(this).getString(Preferences.XML_FILE_ENV_RULES, null);
		
		if(D)Log.d(TAG, "Parsing file: "+xmlFile);
		
		XMLtoECARule parser = new XMLtoECARule();
		//ECARule[] rules = parser.parseFile("/sdcard/myHealthAssistant/rules/rules.xml");
		ECARule[] rules = parser.parseFile(xmlFile);

		
		if(rules == null) {
			if(D) Log.i(TAG, "No rules added.");
			return;
		}
		
		if(D)Log.d(TAG, "Number of rules parsed: "+rules.length);
		
		// Create threads for rules. An alternative: HashMap<Eventtype, List<ECARule>>
		for (final ECARule rule : rules) {
			if(D)Log.i(TAG, "Adding rule #"+rule.id+" for action: "+rule.action);
			addToListOfRules(rule);
		}	
	}
	
	private void addToListOfRules(ECARule rule) {
		// check whether there's already an entry for event type
		if(listOfRules.containsKey(rule.eventType)) {
			Vector<ECARule> rules = listOfRules.get(rule.eventType);
			rules.add(rule);
		} else {
			Vector<ECARule> rules = new Vector<ECARule>();
			rules.add(rule);
			listOfRules.put(rule.eventType, rules);	
		}	
	}
	
	private void addToListOfTemporaryRules(ECARule rule) {
		// check whether there's already an entry for event type
		if(listOfTemporaryRules.containsKey(rule.eventType)) {
			Vector<ECARule> rules = listOfTemporaryRules.get(rule.eventType);
			rules.add(rule);
		} else {
			Vector<ECARule> rules = new Vector<ECARule>();
			
			for(ECARule existingRule : rules) {
				if(existingRule.id == rule.id) return;
			}
			rules.add(rule);
			listOfTemporaryRules.put(rule.eventType, rules);					
		}	
	}
	
	private void removeFromListOfTemporaryRules(ECARule rule) {
		if(D)Log.d(TAG, "Remove rule #"+rule.id+" from temporary rules list.");
		
		// check whether event type is in list
		if(listOfTemporaryRules.containsKey(rule.eventType)) {
			Vector<ECARule> rules = listOfTemporaryRules.get(rule.eventType);
			// is specific rule in list
			if(rules.remove(rule)) {
				if(D)Log.d(TAG, "Rule #"+rule.id+" was removed from listOfRules.");
			}
		}
	}
	
	private boolean triggerECA(AbstractEnvironmentalEvent evt, ECARule rule) {
		// Check event type, needs to be specified in rule
		if (!evt.getEventType().equals(rule.eventType)) {
			return false;
		}
		
		// Check location
		if (rule.location != null) {
			if(!evt.getLocation().equals(rule.location)) return false;
		}

		// Check object
		if (rule.object != null) {
			if(!rule.object.equals(evt.getObject())) return false;
		}
		
		return checkCondition(evt, rule);
	} 
	
	private boolean checkCondition(AbstractEnvironmentalEvent evt, ECARule rule) {
		if(D)Log.d(TAG, "checkCondition");
		
		// Operator
		switch (rule.operator) {
		case ECARule.OP_EQUALS:
			if(D)Log.d(TAG, "Equals operator");
			// Left term
			switch (rule.leftTerm) {
			case ECARule.LEFTTERM_VALUE:
				if (evt.getValue().equals(rule.rightTerm)) {
					if(D)Log.d(TAG, evt.getValue()+ " Equals "+rule.rightTerm);
					triggerAction(evt, rule);
					return true;
				}
				
			}
			break;
		}
		return false;
	}
	
	SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
	
	private void triggerAction(AbstractEnvironmentalEvent evt, ECARule rule) {
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));        
        String date = (dateFormatGmt.format(new Date())+"");
        
        
		if(D)Log.d(TAG, "triggerAction");
		switch(rule.action) {
	   case ECARule.ACTION_EVENT_SHOWERING:
		   injectEvent(new EnvironmentActivityEvent(
				   sensorUtilityList.getEventID(SensorReadingEvent.ACTIVITY, evt.getObject()), 
				   date, 
				   PRODUCER_ID,
				   evt, 
				   EnvironmentActivityEvent.SHOWERING,
				   -1));
		   break;
	   case ECARule.ACTION_EVENT_ENTERING:
		   injectEvent( new EnvironmentActivityEvent(
				   sensorUtilityList.getEventID(SensorReadingEvent.ACTIVITY, evt.getObject()), 
				   date, 
				   PRODUCER_ID,
				   evt, 
				   EnvironmentActivityEvent.ENTERING,
				   -1));
		   break;
	   case ECARule.ACTION_EVENT_LEAVING:
		   injectEvent(new EnvironmentActivityEvent(
				   sensorUtilityList.getEventID(SensorReadingEvent.ACTIVITY, evt.getObject()), 
				   date, 
				   PRODUCER_ID,
				   evt, 
				   EnvironmentActivityEvent.LEAVING,
				   -1));
		   break;
	   case ECARule.ACTION_EVENT_TOOTH_BRUSHING:
		   injectEvent(new EnvironmentActivityEvent(
				   sensorUtilityList.getEventID(SensorReadingEvent.ACTIVITY, evt.getObject()), 
				   date, 
				   PRODUCER_ID,
				   evt, 
				   EnvironmentActivityEvent.TEETH_BRUSHING,
				   -1));
		   break;
	   case ECARule.ACTION_EVENT_EATING:
		   injectEvent(new EnvironmentActivityEvent(
				   sensorUtilityList.getEventID(SensorReadingEvent.ACTIVITY, evt.getObject()), 
				   date, 
				   PRODUCER_ID,
				   evt, 
				   EnvironmentActivityEvent.EATING,
				   -1));
		   break;
	   case ECARule.ACTION_EVENT_AIRING:
		   injectEvent(new EnvironmentActivityEvent(
				   sensorUtilityList.getEventID(SensorReadingEvent.ACTIVITY, evt.getObject()), 
				   date, 
				   PRODUCER_ID,
				   evt, 
				   EnvironmentActivityEvent.AIRING,
				   -1));
		   break;
	   case ECARule.ACTION_EXTENDED_RULE:
		   //createTemporaryThread((ExtendedECARule) rule);
		   addTemporaryRule((ExtendedECARule) rule);
		   break;
	   }
	}

	private void addTemporaryRule(final ExtendedECARule extendedECARule) {
		
		final ECARule rule = extendedECARule.secondRule;
		
		// Check whether rule is already active
		if(runningTemporaryRules.containsKey(rule.id)) {
			// reset timer
			if(D)Log.d(TAG, "Temporary thread is already running. Resetting timer.");
			Runnable r = runningTemporaryRules.get(rule.id);
			temporaryThreadsHandler.removeCallbacks(r);
			temporaryThreadsHandler.postDelayed(r, extendedECARule.maxTimeDifference);
			return;
		}	
		
		// Add temporary rule to listOfRules
		addToListOfTemporaryRules(rule);
		
		// Initialize runnable for removing temporary rule
		Runnable r=new Runnable() {
		    public void run() {
		    	// StopProducer thread, remove it from list
		    	removeFromListOfTemporaryRules(rule);
		    	runningTemporaryRules.remove(rule.id);
		    }
		};
		
		// Add runnable to runningTemporaryRules
		runningTemporaryRules.put(rule.id, r);
		temporaryThreadsHandler.postDelayed(r, extendedECARule.maxTimeDifference);		
	}
	
	
	/** Event RECEIVER implemented as a Android BroadcastReceiver */
	private class readingEventReceiver extends BroadcastReceiver {
	   @Override
	   public void onReceive(Context context, Intent intent) {
		  // Get event type and the event itself
		  String eventType = intent.getStringExtra("event_type");
		  if(D)Log.d(TAG, "Incoming event of type "+eventType);
		  
		  if(!(intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT) instanceof AbstractEnvironmentalEvent)) {
			  Log.d(TAG, "Not an instance of AbstractEnvironmentalEvent: "+eventType);
			  return;
		  }		  	  
		  AbstractEnvironmentalEvent evt = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
		  
		  // Check for matching rules
		  if(listOfRules.containsKey(eventType)) {
			  if(D)Log.d(TAG, "List of rules contains event type "+eventType);
			  Vector<ECARule> rules = listOfRules.get(eventType);
			  for(ECARule rule : rules) {
				  triggerECA(evt, rule);
			  }
		  } else {
			  if(D)Log.d(TAG, "List of rules does not contain event type "+eventType);
		  }

		  // Check for matching temporary rules
		  if(listOfTemporaryRules.containsKey(eventType)) {
			  Vector<ECARule> rules = listOfTemporaryRules.get(eventType);
			  if(D)Log.d(TAG, "### "+rules);
			  for(ECARule rule : rules) {
				  // remove from lists if action was triggered
				  if(triggerECA(evt, rule)) {
					  rules.remove(rule);
					  runningTemporaryRules.remove(rule.id);
				  }
			  }
		  }
		  
		  // Generate OCCUPANCY event based on sensor type and location
		  //generateOccupancyEvent(evt);
	   }
	}
	
	private void generateOccupancyEvent(AbstractEnvironmentalEvent evt) {
//		OccupancyEvent objEvt = new OccupancyEvent(
//				sensorUtilityList.getEventID(OccupancyEvent.PARCELABLE_EXTRA_EVENT_TYPE, evt.getObject()), 
//				Calendar.getInstance().getTime().toGMTString(), 
//				PRODUCER_ID, 
//				evt.getSensorType(), 
//				evt.getTimeOfMeasurement(), 
//				evt.getLocation(), 
//				evt.getObject());
//		injectEvent(objEvt);
	}
	
	private void injectEvent(SensorReadingEvent evt) {
		if(D)Log.d(TAG, "Inject event of type: "+evt.getEventType());		
		
		// Send event
		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
		i.setAction(AbstractChannel.RECEIVER);
		getApplicationContext().sendBroadcast(i);
	}
	
	/**
	 * Maintains instances of EventUtils for the individual
	 * @author Chris
	 *
	 */
	private class SensorUtilityList {
		private EventUtils eventUtil;
		private HashMap<String, EventUtils> eventUtilsMap = new HashMap<String, EventUtils>();
		
		public String getEventID(String eventType, String sensorID) {
			eventUtil = eventUtilsMap.get(sensorID);
			if(eventUtil != null) {
				return eventUtil.getEventID();
			} else {
				eventUtilsMap.put(sensorID, new EventUtils(eventType, sensorID));
				return eventUtilsMap.get(sensorID).getEventID();
			}
		}
	};
}
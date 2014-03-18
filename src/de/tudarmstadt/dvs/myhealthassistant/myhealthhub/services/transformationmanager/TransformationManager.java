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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.Bundle;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationRequest;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationResponse;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Announcement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.StopProducer;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDBMS;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.Transformation;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.services.FelixService;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.services.FelixService.FelixServiceBinder;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.services.IFelixServiceBinder;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.services.WebRequestService;

/**
 * @author Christian Seeger
 *
 */
public class TransformationManager extends Service {

	/** For Debugging */
	private static final String TAG = "TransformationManager";
	private static boolean D = false;
	
	// receives management events such as transformation requests
	private ManagementReceiver mManagementReceiver;
	private TransformationDownloadReceiver mDownloadReceiver;
	
	// database including already downloaded availableTransformations
	private LocalTransformationDBMS transformationDB;
	
	// web request channel
	private static String WEB_REQUEST_CHANNEL = "webRequestChannel";
	public static String TM_SUCCUESSFUL_WEB_REQUEST = "tmSuccessfulWebRequest"; 
	public static final String INTENT_EXTRA_TRANSFORMATION = "transformation";
	
	// service binder to Felix OSGi service
	private IFelixServiceBinder felixServiceBinder;
	
	// list of available availableTransformations
	private ArrayList<Transformation> availableTransformations;
		
	// for transformation management (stopping transformations)
	private HashMap<String, List<Transformation>> requiredEventTypes;
	private HashMap<String, List<Transformation>> providedEventTypes;
	private LinkedList<Transformation> runningTransformations;

	private final IBinder mTransformationManagerBinder = new TransformationManagerBinder();
	public class TransformationManagerBinder extends Binder {
		public Bundle[] getTransformations() {
			if(felixServiceBinder!=null) {
				return felixServiceBinder.getTransformations();
			} else return null;		
		}
		
		public void deleteTransformation(long name) {
			if(felixServiceBinder!=null) felixServiceBinder.removeTransformation(name);
			if(transformationDB!=null) transformationDB.deleteTransformation(name);
			availableTransformations = transformationDB.getAvailableTransformations();
		}
		
		public void stopTransformation(long name) {
			if(felixServiceBinder!=null) felixServiceBinder.stopTransformation(name);
		}
		
		public void startTransformation(long name) {
			if(felixServiceBinder!=null) felixServiceBinder.startTransformation(name);
		}
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mTransformationManagerBinder;
	}
	

	public void onUnbind() {
	}

	@Override
	public void onCreate() {
		if(D)Log.i(TAG, "TransformationManager created.");
		
		// start Felix OSGi service 
		Intent intent = new Intent(this, FelixService.class);
		bindService(intent, felixServiceConnection, Context.BIND_AUTO_CREATE);
			
		// register local management receiver
	    mManagementReceiver = new ManagementReceiver();
	    LocalBroadcastManager.getInstance(this).registerReceiver(mManagementReceiver, new IntentFilter(AbstractChannel.LOCAL_MANAGEMENT));
	    LocalBroadcastManager.getInstance(this).registerReceiver(mManagementReceiver, new IntentFilter(WEB_REQUEST_CHANNEL));
			
	    // register transformation download receiver
	    mDownloadReceiver = new TransformationDownloadReceiver();
	    LocalBroadcastManager.getInstance(this).registerReceiver(mDownloadReceiver, new IntentFilter(TM_SUCCUESSFUL_WEB_REQUEST));
	    
	    // initialize database
	    this.transformationDB = new LocalTransformationDBMS(getApplicationContext());
	        
	    //TODO better solution
	    transformationDB.open();
	    availableTransformations = transformationDB.getAvailableTransformations();
	    if(D)printLocalTransformations();
	    
	    // initialize management lists
	    providedEventTypes = new HashMap<String, List<Transformation>>();
	    requiredEventTypes = new HashMap<String, List<Transformation>>();
	    runningTransformations = new LinkedList<Transformation>();
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "TransformationManager destroyed.");
		
		// stop all transformations
		for(Transformation transformation : runningTransformations) {
			if(D)Log.d(TAG, "Stop transformation: "+transformation.getTransformationName());
			if(felixServiceBinder!=null)
				felixServiceBinder.stopTransformation(transformation.getBundleId());
		}		
		
		// stop felix service
		unbindService(felixServiceConnection);
		
		// unregister local management receiver
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mManagementReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadReceiver);
		
		// close database
		transformationDB.close();
	}
	
	/** Management Receiver */
    private class ManagementReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	// extract event
        	Event evt = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
        	
        	// handle event transformation requests
        	if(evt instanceof EventTransformationRequest) {
        		incomingTransformationRequest((EventTransformationRequest)evt);
        	} else if (evt instanceof Announcement) {
        		if(((Announcement)evt).getAnnouncement()==
        				Announcement.UNADVERTISMENT_EVENT_TYPE_NOT_LONGER_AVAILABLE)        		
        			incomingUnadvertisement(((Announcement)evt).getTransmittedEventType());
        	} else if (evt instanceof StopProducer) {
        		incomingStopProducer((StopProducer)evt);
        	}
        }
    };
    
    private void incomingUnadvertisement(String eventType) {
    	if(D)Log.d(TAG, "Handle unadvertisement for "+eventType);
    	
    	// Check whether transformations require this event type
    	List<Transformation> transformations = requiredEventTypes.get(eventType);
    	if(transformations!=null) deleteTransformationsFromManagementLists(transformations);
    }
    
	public void incomingStopProducer(StopProducer stop) {
		if(D)Log.d(TAG, "Handle stop producer for "+stop.getShortEventType());
		
		// Check whether a transformation produces this event type
		List<Transformation> transformations = providedEventTypes.get(stop.getStopEventType());
		if(transformations!=null) deleteTransformationsFromManagementLists(transformations);		
	}
	
	private void deleteTransformationsFromManagementLists(List<Transformation> transformations) {
		// stop corresponding transformations
		for(Transformation transformation : transformations) {
			// delete from running transformations
			runningTransformations.remove(transformation);
			
			// stop transformation
			if(felixServiceBinder!=null) 
				felixServiceBinder.stopTransformation(transformation.getBundleId());
			
			// delete required event types
			List<String> reqTypes = transformation.getRequiredEventTypes();
			for(String type : reqTypes) {
				requiredEventTypes.get(type).remove(transformation);
			}
			
			// delete produced event type
			providedEventTypes.get(transformation.getProducedEventType()).remove(transformation);
		}
	}

	private void addToRunningTransformations(Transformation transformation) {
		// Add to running transformations
		runningTransformations.add(transformation);
		
		// Add to list of provided event types
		List<Transformation> producers = providedEventTypes.get(transformation.getProducedEventType());
		if(producers!=null) {
			producers.add(transformation);
		} else {
			producers = new ArrayList<Transformation>();
			producers.add(transformation);
		}
		providedEventTypes.put(transformation.getProducedEventType(), producers);
		
		// Add to list of required event types
		List<String> requiredTypes = transformation.getRequiredEventTypes();
		for(String eventType : requiredTypes) {
			List<Transformation> transformations = requiredEventTypes.get(eventType);
			if(transformations!=null) {
				transformations.add(transformation);
			} else {
				transformations = new ArrayList<Transformation>();
				transformations.add(transformation);
			}
			requiredEventTypes.put(eventType, transformations);
		}
	}

	/** Transformation Download Receiver */
    private class TransformationDownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	EventTransformationResponse response = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
        	
        	if(response.isTransformationFound()) {
        		Transformation transformation = intent.getParcelableExtra(INTENT_EXTRA_TRANSFORMATION);
        		if(D)Log.d(TAG, "Transformation was found: "+transformation.getTransformationName());
        		
        		// Add new and already started transformation to DB 
        		if(transformationDB!=null) transformationDB.addTransformation(
        				transformation.getBundleId(), 
        				transformation.getTransformationName(), 
        				transformation.getProducedEventType(), 
        				transformation.getRequiredEventTypes(), 
        				transformation.getTransformationCost());
        		
        		// Add transformation to list of availableTransformations
        		availableTransformations.add(transformation);
        		
        		// Add to running transformations
        		addToRunningTransformations(transformation);
        		
        	} else {
        		if(D)Log.d(TAG, "No transformation found.");
        		//TODO
        	}
        }
    };
    
	/**
	 * @param transformationRequest
	 */
	private void incomingTransformationRequest(EventTransformationRequest transformationRequest) {
		if(D)printTransformationRequest(transformationRequest);
		
		// skip request if no event types are advertised
		if(transformationRequest.getAdvertisedEvents().length==0) return;
		
		// is transformation stored locally?
		Transformation trans = getLocalTransformation(transformationRequest);
				
		if(trans != null) {
			if(felixServiceBinder!=null) {
				felixServiceBinder.startTransformation(trans.getBundleId());
				addToRunningTransformations(trans);
			} else {
				if(D)Log.e(TAG, "not connected to felix service.");
			}
			
		// if not, query remote repository
		} else {
			Log.i(TAG, "Query remote repository for transformation to: "+
						transformationRequest.getEventSubscription());
			Intent intentRequest = new Intent(getApplicationContext(), WebRequestService.class);
			intentRequest.putExtra(Event.PARCELABLE_EXTRA_EVENT, transformationRequest);
			getApplicationContext().startService(intentRequest);
		}
	}



	/**
     * Queries for local transformation that is applicable and has the lowest costs.
	 * @param transformationRequest containing the available event types and the requested event types.
	 * @return transformation. <code>null</code> if no transformation was found.
	 */
	private Transformation getLocalTransformation(EventTransformationRequest transformationRequest) {
		//TODO make it efficient
		ArrayList<String> list = new ArrayList<String>();
		String[] advertisedEvents = transformationRequest.getAdvertisedEvents();
		for(int i = 0; i < advertisedEvents.length; i++) {
			list.add(advertisedEvents[i]);
		}
		
		// Find a transformation
		Transformation bestTransformation = null;
		int costs = Integer.MAX_VALUE;
		int tempCosts;
		for(Transformation trans : availableTransformations) {
			tempCosts = trans.isTransformationApplicable(list, transformationRequest.getEventSubscription());
			// if the transformation is applicable and the costs are lower than before, store
			if(tempCosts!=-1 && tempCosts < costs) {
				costs = tempCosts;
				bestTransformation = trans;
			}
		}
		
		return bestTransformation;
	}
	
	
	private ServiceConnection felixServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			felixServiceBinder = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			FelixServiceBinder binder = ((FelixServiceBinder) service);
			felixServiceBinder = (IFelixServiceBinder) binder.getService();
		}
	};

	
	/* =============================================================================
       helper methods 
       ============================================================================= */
	/**
     * Prints a transformation request.
     * @param req event transformation request
     */
    private void printTransformationRequest(EventTransformationRequest req) {
    	String out = "Incoming event transformation request for event type\n";
    	out += req.getEventSubscription();
    	out += "\nhaving the following event types available:";
    	for(String type : req.getAdvertisedEvents()) {
    		out += "\n"+type;
    	}
    	Log.d(TAG, out);
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
    
    private void printLocalTransformations() {
    	String out = "List of available availableTransformations\n";
    	out += "ID | Name\n";
    	for(Transformation trans : availableTransformations) {
    		out += trans.getBundleId()+" | "+trans.getTransformationName()+"\n";
    	}
    	Log.d(TAG, out);
    }
}
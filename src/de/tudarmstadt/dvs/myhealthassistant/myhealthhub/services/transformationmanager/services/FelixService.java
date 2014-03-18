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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.apache.felix.main.AutoProcessor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.Unzip;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationResponse;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.TransformationManager;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.Transformation;

public class FelixService extends Service implements IFelixServiceBinder {

	// for debugging
	private static final String TAG = "FelixService";
	private static final boolean D = false;

	// intent extra for transmitting server response
	public static final String INTENT_EXTRA_TRANSFORMATION_BYTE_CODE = "transformationByteCode";
	public static final String FELIX_SUCCESSFUL_WEB_REQUEST = "felixSuccessfullWebRequest";

	// felix framework
	private Framework felixFramework;
	
	private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
		
			EventTransformationResponse response = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);

			// if no transformation has been found, we cannot load it.
			if(!response.isTransformationFound()) return;
			
			// get server response
			String server_response = intent.getStringExtra(INTENT_EXTRA_TRANSFORMATION_BYTE_CODE);
			
			try {
				JSONObject jsObject = new JSONObject(server_response);

				// get transmitted bundle as String and decode it to Bytes
				String bundleURI = jsObject.getString(WebRequestService.JSON_TRANSFORMATION_URI);
				String bundle = jsObject.getString(WebRequestService.JSON_TRANSFORMATION_BYTE_CODE);
				byte[] bytes = Base64.decode(bundle, Base64.DEFAULT);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

				// Load new bundle in Felix framework 
				Bundle transformatorBundle = felixFramework.getBundleContext()
						.installBundle(bundleURI, inputStream);
						
				// Start transformation
				startTransformation(transformatorBundle.getBundleId());
				
				if(D)Log.i(TAG, "New transformation deployed: " + 
						transformatorBundle.getSymbolicName() + " (with ID: "+transformatorBundle.getBundleId()+")");

				// Get array of required event types
				ArrayList<String> requiredEventTypes = new ArrayList<String>();
				JSONArray temp = jsObject.getJSONArray(WebRequestService.JSON_REQUIRED_EVENT_TYPES);
				for(int i = 0; i < temp.length(); i++) {
					requiredEventTypes.add(temp.getString(i));
				}
				
				// Create transformation object
				Transformation transformation = new Transformation(
						transformatorBundle.getBundleId(), 
						bundleURI, 
						requiredEventTypes,
						jsObject.getString(WebRequestService.JSON_REQUESTED_EVENT_TYPE),
						jsObject.getInt(WebRequestService.JSON_TRANSFORMATION_COSTS));
				
				// Send response to TransformationManager
				Intent i = new Intent();
				i.putExtra(Event.PARCELABLE_EXTRA_EVENT, response);
				i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, response.getEventType());
				i.putExtra(TransformationManager.INTENT_EXTRA_TRANSFORMATION, transformation);
				i.setAction(TransformationManager.TM_SUCCUESSFUL_WEB_REQUEST);
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
	};
	

	@Override
	public void onCreate() {
		super.onCreate();

		if(D) Log.d(TAG, "Setting up a thread for felix.");
		
		Thread felixThread = new Thread() {

			@Override
			public void run() {
								
				File dexOutputDir = getApplicationContext().getDir("transformationmanager", 0);
				
				// if default bundles were not installed already, install them
				File f = new File(dexOutputDir.getAbsolutePath()+"/bundle");
				if(!f.isDirectory()) {
					if(D) Log.i(TAG, "Installing default bundles...");
					unzipBundles(FelixService.this.getResources().openRawResource(R.raw.bundles),
							dexOutputDir.getAbsolutePath()+"/");					
				} 		
				
				FelixConfig felixConfig = new FelixConfig(dexOutputDir.getAbsolutePath());
				Map<String, String> configProperties = felixConfig.getProperties2();
			
				try {
					FrameworkFactory frameworkFactory = new org.apache.felix.framework.FrameworkFactory();
					
					felixFramework = frameworkFactory.newFramework(configProperties);
					felixFramework.init();
					AutoProcessor.process(configProperties,felixFramework.getBundleContext());
					felixFramework.start();

					// Registering the android context as an osgi service
					Hashtable<String, String> properties = new Hashtable<String, String>();
					properties.put("platform", "android");
					felixFramework.getBundleContext().registerService(
							Context.class.getName(), getApplicationContext(),
							properties);

				} catch (Exception ex) {					
					Log.e(TAG, "Felix could not be started", ex);
					ex.printStackTrace();
				}
			}
		};
		
		felixThread.setDaemon(true);
		felixThread.start();

		LocalBroadcastManager.getInstance(this).registerReceiver(downloadReceiver, 
				new IntentFilter(FELIX_SUCCESSFUL_WEB_REQUEST));
	}

	private void unzipBundles(InputStream rawResource, String internalDataDir) {
		Unzip decompress = new Unzip(rawResource, internalDataDir);
		decompress.unzip();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(D)Log.i(TAG, "stopping the felix service");
		try {
			felixFramework.stop();
			felixFramework = null;
		} catch (BundleException ex) {
			Log.w(TAG, "problem occuring when stopping felix", ex);
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		return new FelixServiceBinder();
	}

	public class FelixServiceBinder extends Binder {
		public FelixService getService() {
			return FelixService.this;
		}
	}

	@Override
	public void startTransformation(long bundleId) {
		Bundle transformatorBundle = felixFramework.getBundleContext()
				.getBundle(bundleId);
		if(D)Log.i(TAG, "Starting bundle "+bundleId+": "+transformatorBundle.getSymbolicName());
		
		try {
			transformatorBundle.start();
		} catch (BundleException e) {
			Log.w("Bundle cannot be started", e);
		}
	}

	@Override
	public void stopTransformation(long bundleId) {
		Bundle transformatorBundle = felixFramework.getBundleContext()
				.getBundle(bundleId);
		if(D)Log.i(TAG, "Stopping bundle "+bundleId+": "+transformatorBundle.getSymbolicName());
		
		try {
			transformatorBundle.stop();
		} catch (BundleException e) {
			Log.w("Bundle cannot be stopped", e);
		}
	}

	@Override
	public void removeTransformation(long bundleId) {
		Bundle transformatorBundle = felixFramework.getBundleContext()
				.getBundle(bundleId);
		if(D)Log.i(TAG, "Uninstalling bundle "+bundleId+": "+transformatorBundle.getSymbolicName());
		
		try {
			transformatorBundle.uninstall();
		} catch (BundleException e) {
			Log.w("bundle cannot be removed", e);
		}
	}
	
	@Override
	public Bundle[] getTransformations() {
		return felixFramework.getBundleContext().getBundles();
	}
}
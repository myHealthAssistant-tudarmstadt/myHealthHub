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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.Preferences;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Announcement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.StartProducer;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.StopProducer;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventAnkle;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventWrist;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.SensorConfigFragment;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.AbstractSensorModule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.cardiovascular.PolarHRModule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.physical.AccSensorModule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.physical.CountAccHedgeHogSensorModule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.physical.DebugSensorModule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.BodySensors;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.cardiovascular.HeartRatePolarBluetoothSensor;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.physical.HedgeHogAccelerometer;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.physical.PorcupineAccelerometer;

/**
 * @author Christian Seeger
 * 
 */
public class SensorModuleManager extends Service {

	/* for debugging */
	private static String TAG = SensorModuleManager.class.getSimpleName();
	private static boolean D = true;
	private DebugSensorModule mDebugAccSensor;

	// ECG Modules
	private PolarHRModule mPolarHRModule;

	// Acc Modules
	private AccSensorModule mHedgeHogAccAnkleLegSensor;
	private AccSensorModule mPorcupineChestAccSensor;
	private AccSensorModule mHedgeHogWristSensor;
	private DebugSensorModule mHedgehogDebugSensor;

	// Blood Pressure

	// Scale

	// Ambient Sensing

	// running sensor modules: eventType -> list of modules providing this event
	// type
	private HashMap<String, List<AbstractSensorModule>> availableSensorModules;

	// For auto-connect
	private SharedPreferences preferences;
	private static int AUTO_CONNECT_WARM_UP = 2000; // was: 3000
	private static int AUTO_CONNECT_TIME_TO_NEXT_CONNECT = 3000; // was: 3000
	private static int AUTO_CONNECT_TIME_UNTIL_NEXT_CONNECTION_CHECK = 3000; // was:
																				// 60000
	private static Handler autoConnectionHandler = new Handler();
	private LinkedList<AbstractSensorModule> listOfActiveModules; // list of all
																	// active
																	// modules
	private int activeModulesPointer; // pointer in order to allow re-trying a
										// connection one after the other

	// Local management receiver
	private LocalManagementReceiver mLocalManagementReceiver;

	@Override
	public void onCreate() {
		if (D)
			Log.d(TAG, TAG + ": onCreate");

		// Initialize map of running modules
		availableSensorModules = new HashMap<String, List<AbstractSensorModule>>();

		// Initialize list of active modules that try to connect to their
		// sensors
		listOfActiveModules = new LinkedList<AbstractSensorModule>();
		activeModulesPointer = 0;

		// Load preferences preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		// TODO add further preferences such as timeouts

		// autoConnectionHandler.postDelayed(autoConnection,
		// AUTO_CONNECT_WARM_UP);

		// Subscribe to local management channel for receiving
		// StartProducer and StopProducer events
		mLocalManagementReceiver = new LocalManagementReceiver();
		LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(mLocalManagementReceiver,
						new IntentFilter(AbstractChannel.LOCAL_MANAGEMENT));

		super.onCreate();
	}
	

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		Log.e(TAG,
				"call me redundant BABY!  onStartCommand service");
		
		
		Intent intent = new Intent(this, SensorConfigFragment.class);
		PendingIntent pendIntent = PendingIntent
				.getActivity(this, 0, intent, 0);
		int myID = android.os.Process.myPid();
		Notification notice = new Notification.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(getPackageName())
				.setContentText(TAG + " running").setContentIntent(pendIntent)
				.getNotification();

		notice.flags |= Notification.FLAG_AUTO_CANCEL;
		startForeground(myID, notice);
		
		//Start sensor connections
		startAutoConnectSensorModules();
		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (D)
			Log.d(TAG, "I was destroyed");

		// Unregister local management receiver
		LocalBroadcastManager.getInstance(getApplicationContext())
				.unregisterReceiver(mLocalManagementReceiver);

		// stop auto-connection of active modules
		autoConnectionHandler.removeCallbacks(autoConnection);

		//stop all sensor modules
		stopSensorModules();
		
		stopForeground(true);
		super.onDestroy();
	}

	/** Management Receiver */
	private class LocalManagementReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String eventType = ((Event) intent
					.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT))
					.getEventType();
			if (D)
				Log.d(TAG, "Received local management event of type: "
						+ eventType);
			if (D) {
				if (eventType.equals(Announcement.EVENT_TYPE)) {
					Announcement ann = (Announcement) intent
							.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
					Log.d(TAG, "Announcement: " + ann.getAnncouncementText());
				}
			}

			// StartProducer event was received
			if (eventType.equals(StartProducer.EVENT_TYPE)) {
				StartProducer start = (StartProducer) intent
						.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
				if (D)
					Log.d(TAG,
							"searching for fitting event producer to start for event type: "
									+ start.getStartEventType());

				List<AbstractSensorModule> producers = availableSensorModules
						.get(start.getStartEventType());
				if (producers != null) {
					// start each sensor modules that provides the required
					// event type
					for (AbstractSensorModule sensorModule : producers) {
						if (!sensorModule.isActiveModule()) {
							if (D)
								Log.i(TAG,
										"+ Incoming request for events of type '"
												+ start.getShortStartEventType()
												+ "' starting PASSIVE module: "
												+ sensorModule.getModuleID()
												+ ".");
							sensorModule.start();
						} else {
							if (D)
								Log.i(TAG,
										"+ Incoming request for events of type '"
												+ start.getShortStartEventType()
												+ "' starting ACTIVE module: "
												+ sensorModule.getModuleID()
												+ ".");
							if (!sensorModule.isConnected())
								sensorModule.start();
							addToActiveModules(sensorModule);
						}
					}
				} else {
					if (D)
						Log.d(TAG, "List of producers is null");
				}

				// StopProducer event was received
			} else if (eventType.equals(StopProducer.EVENT_TYPE)) {
				StopProducer stop = (StopProducer) intent
						.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
				if (D)
					Log.d(TAG,
							"searching for fitting event producer to stop for event type: "
									+ stop.getStopEventType());

				List<AbstractSensorModule> producers = availableSensorModules
						.get(stop.getStopEventType());
				if (producers != null) {
					// stop each sensor modules that provides the required event
					// type
					for (AbstractSensorModule sensorModule : producers) {
						if (!sensorModule.isActiveModule()) {
							if (D)
								Log.i(TAG,
										"- Incoming stop request for events of type '"
												+ stop.getShortStopEventType()
												+ "' stopping PASSIVE module: "
												+ sensorModule.getModuleID()
												+ ".");
							sensorModule.stop();
						} else {
							if (D)
								Log.i(TAG,
										"- Incoming stop request for events of type '"
												+ stop.getShortStopEventType()
												+ "' stopping ACTIVE module: "
												+ sensorModule.getModuleID()
												+ ".");
							sensorModule.stop();
							removeFromActiveModules(sensorModule);
						}
					}
				}
			}
		}
	};

	/**
	 * Checks preferences for auto connection
	 */
	private void startAutoConnectSensorModules() {
		Log.e(TAG, "...");
		if (preferences.getBoolean(Preferences.AUTO_CONNECT_ENABLED, false)) {
			if (D)
				Log.e(TAG,
						"Auto-Connect is selected. StartProducer enabling modules...");
			String[] sensorTypesArray = getResources().getStringArray(
					R.array.sensor_type_config);
			for (String st : sensorTypesArray) {
				if (preferences.getBoolean(st, false)) {
					String type = preferences.getString(st
							+ SensorConfigFragment.deviceType, "");
					String deviceMac = preferences.getString(st
							+ SensorConfigFragment.deviceMac, "");

					if (!type.isEmpty() && !deviceMac.isEmpty()) {
						Log.i(TAG, st + "\n" + type + " --- " + deviceMac);
						enableSensorModule(type, true, type, deviceMac);
					} else
						Log.e(TAG, "sth might wrong here: " + st);
				}
			}
		}
	}
	
	private void stopSensorModules(){
		if (D)
			Log.e(TAG,
					"disabling modules...");
		String[] sensorTypesArray = getResources().getStringArray(
				R.array.sensor_type_config);
		for (String st : sensorTypesArray) {
			AbstractSensorModule module = getModule(st);
			if (module != null)
				disable(module);
		}
	}

	public void enableSensorModule(String moduleKey, boolean enable, String id,
			String mac) {
		if (moduleKey.equals(getResources().getString(R.string.key_polar))) {
			if (enable)
				mPolarHRModule = (PolarHRModule) initializeSensorModule(new PolarHRModule(
						getApplicationContext(),
						new HeartRatePolarBluetoothSensor(id, mac)));
			else
				disable(mPolarHRModule);
		}
		if (moduleKey.equals(getResources().getString(R.string.key_debug))) {
			if (enable)
				mHedgehogDebugSensor = (DebugSensorModule) initializeSensorModule(new DebugSensorModule(
						getApplicationContext(), new PorcupineAccelerometer(
								BodySensors.ACC_HEDGEHOG_DEBUG_ID,
								BodySensors.ACC_HEDGEHOG_DEBUG_MAC)));
			else
				disable(mHedgehogDebugSensor);
		}
	}

	private Runnable autoConnection = new Runnable() {
		public void run() {

			boolean loop = true;
			AbstractSensorModule module;

			while (loop) {
				if (listOfActiveModules.size() == 0) {
					loop = false;
				}

				if (activeModulesPointer < listOfActiveModules.size()) {
					// TODO Check null Pointer Exception
					module = listOfActiveModules.get(activeModulesPointer);
					if (module != null && module.isConnected()) {
						activeModulesPointer++;
					} else {
						module.start();
						activeModulesPointer++;
						checkNextDev();
						loop = false;
					}
				} else {
					activeModulesPointer = 0;
					loop = false;
					waitBeforeCheckNextDev();
				}
			}
		}

		private void checkNextDev() {
			if (D)
				Log.d(TAG, "autoConnection: checkNextDev()");
			autoConnectionHandler.postDelayed(autoConnection,
					AUTO_CONNECT_TIME_TO_NEXT_CONNECT);
		}

		private void waitBeforeCheckNextDev() {
			autoConnectionHandler.postDelayed(autoConnection,
					AUTO_CONNECT_TIME_UNTIL_NEXT_CONNECTION_CHECK);
		}
	};

	private AbstractSensorModule initializeSensorModule(
			AbstractSensorModule module) {
		// Initialize sensor module
		module.initializeSensorModule();

		// Add module to list of available sensor modules
		addModuleToAvailableSensorModules(module);
		if (D)
			Log.i(TAG, "Module " + module.getModuleID() + " is initialized.");

		return module;
	}

	private void destroySensorModule(AbstractSensorModule module) {
		if (module != null) {
			module.destroySensorModule();
			listOfActiveModules.remove(module);
			availableSensorModules.remove(module);
			module = null;

		}
	}
	
	private AccSensorModule initializeModulePorcupineChest() {
		return (AccSensorModule) initializeSensorModule(new AccSensorModule(
				this, new PorcupineAccelerometer(
						BodySensors.ACC_HEDGEHOG_CHEST_ID,
						BodySensors.ACC_HEDGEHOG_CHEST_MAC)));
	}

	/*
	 * private AccSensorModule initializeModulePorcupineLeg() { return
	 * (AccSensorModule) initializeSensorModule(new AccSensorModule( this, new
	 * PorcupineAccelerometer( BodySensors.ACC_HEDGEHOG_LEG_ID,
	 * BodySensors.ACC_HEDGEHOG_LEG_MAC))); }
	 */

	private AccSensorModule initializeModuleHedgeHogAnkle() {
		return (AccSensorModule) initializeSensorModule(new CountAccHedgeHogSensorModule(
				this, new HedgeHogAccelerometer(
						BodySensors.ACC_HEDGEHOG_LEG_ID,
						BodySensors.ACC_HEDGEHOG_LEG_MAC,
						AccSensorEventAnkle.EVENT_TYPE)));
	}

	/*
	 * private CountAccSensorModule initializeModulePorcupineWrist() { return
	 * (CountAccSensorModule) initializeSensorModule(new CountAccSensorModule(
	 * this, new PorcupineAccelerometer( BodySensors.ACC_HEDGEHOG_WRIST_ID,
	 * BodySensors.ACC_HEDGEHOG_WRIST_MAC))); }
	 */

	private AccSensorModule initializeModuleHedgeHogWrist() {
		return (AccSensorModule) initializeSensorModule(new CountAccHedgeHogSensorModule(
				this, new HedgeHogAccelerometer(
						BodySensors.ACC_HEDGEHOG_WRIST_ID,
						BodySensors.ACC_HEDGEHOG_WRIST_MAC,
						AccSensorEventWrist.EVENT_TYPE)));
	}

	private PolarHRModule initializeModuleHartRatePolarBluetooth() {
		return (PolarHRModule) initializeSensorModule(new PolarHRModule(this,
				new HeartRatePolarBluetoothSensor(
						BodySensors.HEART_RATE_SENSOR_POLAR_BLUETOOTH_ID,
						BodySensors.HEART_RATE_SENSOR_POLAR_BLUETOOTH_MAC)));
	}

	private DebugSensorModule initializeModuleHedgehogDebug() {
		return (DebugSensorModule) initializeSensorModule(new DebugSensorModule(
				this, new PorcupineAccelerometer(
						BodySensors.ACC_HEDGEHOG_DEBUG_ID,
						BodySensors.ACC_HEDGEHOG_DEBUG_MAC)));
	}

	private DebugSensorModule initializeModuleHedgehogDebug(String MAC) {
		return (DebugSensorModule) initializeSensorModule(new DebugSensorModule(
				this, new PorcupineAccelerometer(
						BodySensors.ACC_HEDGEHOG_DEBUG_ID, MAC)));
	}

	/*
	 * private void startInfraWoTModule() { if(mInfraWOTModule == null)
	 * mInfraWOTModule = new InfraWotModule(this); }
	 */

	private void addModuleToAvailableSensorModules(AbstractSensorModule module) {
		List<AbstractSensorModule> modules = availableSensorModules.get(module
				.getProducingEventType());

		if (D)
			Log.d(TAG,
					"Adding module for event type "
							+ module.getProducingEventType()
							+ " to availableSensorModules");

		// Create new list if list does not exist already
		if (modules == null) {
			modules = new LinkedList<AbstractSensorModule>();
		}

		// add module to list
		modules.add(module);

		availableSensorModules.put(module.getProducingEventType(), modules);
	}

	private void removeModuleFromAvailableSensorModules(
			AbstractSensorModule module) {
		// if module was not instantiated it can't be removed
		if (module == null)
			return;

		List<AbstractSensorModule> modules = availableSensorModules.get(module
				.getProducingEventType());

		// Create new list if list does not exist already
		if (modules != null) {
			modules.remove(module);
		}
	}

	private void disable(AbstractSensorModule module) {
		removeModuleFromAvailableSensorModules(module);
		destroySensorModule(module);
		module = null;
	}

	private void addToActiveModules(AbstractSensorModule module) {
		listOfActiveModules.add(module);
	}

	private void removeFromActiveModules(AbstractSensorModule module) {
		listOfActiveModules.remove(module);
	}
	
	private AbstractSensorModule getModule(String moduleKey) {
		// switch with mapping sensor ID to its Module;
		if (moduleKey.equals(getResources().getString(R.string.key_polar))) {
			return mPolarHRModule;
		}
		if (moduleKey.equals(getResources().getString(R.string.key_debug))) {
			return mHedgehogDebugSensor;
		}
		return null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mSensorModuleManagerBinder;
	}
	

	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);
		
		return true;
	}

	private final IBinder mSensorModuleManagerBinder = new SensorModuleManagerBinder();

	public class SensorModuleManagerBinder extends Binder {

		public boolean isActiveModule(String id) {
			AbstractSensorModule module = getModule(id);
			if (module != null)
				return module.isActiveModule();
			return false;
		}

		public String getMacAddress(String sensorType) {
			String result = null;
			if (preferences != null) {
				result = preferences.getString(sensorType
						+ SensorConfigFragment.deviceMac, "");
			}
			return result;

		}

		// *********************

		public void connectModule(String moduleKey) {
			AbstractSensorModule module = getModule(moduleKey);
			if (module != null)
				module.start();
		}

		/**
		 * Ha enableModule acording to moduleKey
		 * 
		 * @param moduleKey
		 * @param enable
		 * @param id
		 * @param mac
		 */
		public void enableModule(String moduleKey, boolean enable, String id,
				String mac) {
			enableSensorModule(moduleKey, enable, id, mac);
		}

		// **********************************

		public void enableHeartRatePolarBT(boolean enable) {
			if (enable) {
				mPolarHRModule = initializeModuleHartRatePolarBluetooth();
			} else {
				removeModuleFromAvailableSensorModules(mPolarHRModule);
				destroySensorModule(mPolarHRModule);
				mPolarHRModule = null;
			}
		}

		public void connectToHeartRatePolarBT() {
			if (mPolarHRModule != null)
				mPolarHRModule.start();
		}

		public void enableAccPorcupineLeg(boolean enable) {
			if (enable) {
				mHedgeHogAccAnkleLegSensor = initializeModuleHedgeHogAnkle();
			} else {
				removeModuleFromAvailableSensorModules(mHedgeHogAccAnkleLegSensor);
				destroySensorModule(mHedgeHogAccAnkleLegSensor);
			}
		}

		public void connectToLegAccSensor() {
			if (mHedgeHogAccAnkleLegSensor != null)
				mHedgeHogAccAnkleLegSensor.start();
		}

		public void enableAccPorcupineChest(boolean enable) {
			if (enable) {
				mPorcupineChestAccSensor = initializeModulePorcupineChest();
			} else {
				removeModuleFromAvailableSensorModules(mPorcupineChestAccSensor);
				destroySensorModule(mPorcupineChestAccSensor);
			}
		}

		public void connectToChestAccSensor() {
			if (mPorcupineChestAccSensor != null)
				mPorcupineChestAccSensor.start();
		}

		public void enableAccPorcupineWrist(boolean enable) {
			if (enable) {
				mHedgeHogWristSensor = initializeModuleHedgeHogWrist();
			} else {
				removeModuleFromAvailableSensorModules(mHedgeHogWristSensor);
				destroySensorModule(mHedgeHogWristSensor);
			}
		}

		public void connectToWristAccSensor() {
			if (mHedgeHogWristSensor != null)
				mHedgeHogWristSensor.start();
		}

		public void enableAccHedgehogDebug(boolean enable) {
			if (enable) {
				mHedgehogDebugSensor = initializeModuleHedgehogDebug();
			} else {
				removeModuleFromAvailableSensorModules(mHedgehogDebugSensor);
				destroySensorModule(mHedgehogDebugSensor);
			}
		}

		public void enableAccHedgehogDebug(boolean enable, String MAC) {
			if (enable) {
				mHedgehogDebugSensor = initializeModuleHedgehogDebug(MAC);
			} else {
				removeModuleFromAvailableSensorModules(mHedgehogDebugSensor);
				destroySensorModule(mHedgehogDebugSensor);
			}
		}

		public void connectToAccDebugSensor() {
			if (mHedgehogDebugSensor != null)
				mHedgehogDebugSensor.start();
		}
		
		public void autoEnableConfigChanged() {
			startAutoConnectSensorModules();
		}

		public List<AbstractSensorModule> getListActiveModule() {
			return listOfActiveModules;
		}
	}
}
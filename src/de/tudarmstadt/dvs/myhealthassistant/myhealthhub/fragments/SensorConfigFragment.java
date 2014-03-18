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

import java.util.ArrayList;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.Preferences;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities.SensorSettingsActivity;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter.SensorListAdapter;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.SensorModuleManager;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.messagehandler.MessageHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 
 * @author HieuHa
 * 
 */
public class SensorConfigFragment extends ListFragment {

	public interface OnTitleSelectedListener {

	}

	// for debugging
	private static final String TAG = SensorConfigFragment.class.getSimpleName();
	private static boolean D = true;

	// shared preferences including sensor auto-connect information
	private SharedPreferences preferences;

	private SensorListAdapter mAdapter;
	// private MAbstractSensorListAdapter adapter;

	// for enabling Bluetooth
	private BluetoothAdapter mBluetoothAdapter;

	/** Flag indicating whether we have called bind on the service. */
	private boolean mBound;
	
	// for communication with SensorModuleManager
	private Intent mSensorModuleManagerIntent;
	private SensorModuleManager.SensorModuleManagerBinder mSensorModuleManager;
	
	private Intent mMessageHandlerIntent;
	
	private View rootView;

	private ArrayList<SensorType> sensorTypes;
	
	// Extendtion for saving device name and macAdresse in preferenc
	public static final String deviceType = "_sensorType";
	public static final String deviceMac = "_deviceMac";
	
	private ProgressDialog progressDialog;

	private static final int REQUEST_ENABLE_BT = 1 + 5 << 12;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(
				R.layout.fragment_list_with_empty_container, container, false);

		if (D)
			Log.d(TAG, TAG + ": onCreateView");

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// no bluetooth support on this device, app should be stop
			Toast.makeText(getActivity().getApplicationContext(),
					"No Bluetooth Support!", Toast.LENGTH_LONG).show();
			getActivity().finish();
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return;
		}

		// start create Sensors when bluetooth is enabled;
		startSensorModule();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT)
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "Enabled Bluetooth, Wee!");
				startSensorModule();
			} else {
				Log.e(TAG, "Bluetooth should be enable, dude!");
			}
	}

	private void startSensorModule() {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle("");
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();

		/* Preferences */
		preferences = PreferenceManager.getDefaultSharedPreferences(this
				.getActivity());

		// StartProducer message handler
		mMessageHandlerIntent = new Intent(getActivity().getApplicationContext(), MessageHandler.class);
		getActivity().startService(mMessageHandlerIntent);

		registerForContextMenu(getListView());

		mSensorModuleManagerIntent = new Intent(getActivity(),
				SensorModuleManager.class);
		getActivity().getApplicationContext().bindService(mSensorModuleManagerIntent,
				mSensorModuleManagerConnection, Context.BIND_AUTO_CREATE);
		getActivity().startService(mSensorModuleManagerIntent);
	}

	/**
	 * Sets up a connection to the SensorModuleManager.
	 */
	private ServiceConnection mSensorModuleManagerConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			if (D)
				Log.d(TAG, "I am connected.");
			mSensorModuleManager = (SensorModuleManager.SensorModuleManagerBinder) binder;
			mBound = true;
			
			if (progressDialog != null)
				progressDialog.dismiss();
			createActivity();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBound = false;
			Toast.makeText(getActivity(),
					"mSensorModuleManager: disconnected from service.",
					Toast.LENGTH_SHORT).show();
		}
	};

	private void createActivity() {
		// adapter = new MAbstractSensorListAdapter(getActivity(), getActivity()
		// .getApplicationContext());
		// setListAdapter(adapter);
		sensorTypes = new ArrayList<SensorType>();
		// Create a bunch of sensor types
		SensorType s_hr = new SensorType(getResources().getString(
				R.string.pulse), getResources().getStringArray(
				R.array.type_pulse));
		SensorType s_ecg = new SensorType(getResources()
				.getString(R.string.ecg), getResources().getStringArray(
				R.array.type_ecg));
		SensorType s_iem = new SensorType(getResources().getString(
				R.string.scale), getResources().getStringArray(
				R.array.type_cale));
		SensorType s_boso = new SensorType(getResources().getString(
				R.string.blood_pressure), getResources().getStringArray(
				R.array.type_bp));
		SensorType s_acc_leg = new SensorType(getResources().getString(
				R.string.acc_leg), getResources().getStringArray(
				R.array.type_acc_leg));
		SensorType s_acc_chest = new SensorType(getResources().getString(
				R.string.acc_chest), getResources().getStringArray(
				R.array.type_acc_chest));
		SensorType s_acc_wrist = new SensorType(getResources().getString(
				R.string.acc_wrist), getResources().getStringArray(
				R.array.type_acc_wrist));
		SensorType s_ambient_roving = new SensorType(getResources().getString(
				R.string.ambient_roving), getResources().getStringArray(
				R.array.type_ambient_roving));
		SensorType s_ambient_infraWot = new SensorType(getResources()
				.getString(R.string.ambient_infra), getResources()
				.getStringArray(R.array.type_ambient_infra));
		SensorType s_debug = new SensorType(getResources().getString(
				R.string.acc_debug), getResources().getStringArray(
				R.array.type_acc_debug));

		mAdapter = new SensorListAdapter(getActivity(), getActivity()
				.getApplicationContext());
		setListAdapter(mAdapter);

		sensorTypes.add(s_hr);
		sensorTypes.add(s_ecg);
		sensorTypes.add(s_iem);
		sensorTypes.add(s_boso);
		sensorTypes.add(s_acc_leg);
		sensorTypes.add(s_acc_chest);
		sensorTypes.add(s_acc_wrist);
		sensorTypes.add(s_ambient_roving);
		sensorTypes.add(s_ambient_infraWot);
		sensorTypes.add(s_debug);

		checkAutoConnectPreferences();
		mAdapter.setData(sensorTypes);
	}

	/** Checks preferences for auto connection */
	private void checkAutoConnectPreferences() {
		Log.e(TAG, "check auto connect pref!");
		if (sensorTypes != null && preferences != null) {
			for (SensorType st : sensorTypes) { 
				String sensorType = st.getType();
				String deviceAdd = preferences.getString(sensorType
						+ deviceMac, "");
				String sensorFamily = preferences.getString(sensorType
						+ deviceType, "");
				st.setDeviceFamily(sensorFamily);
				st.setDeviceMac(deviceAdd);

				if (preferences.getBoolean(Preferences.AUTO_CONNECT_ENABLED,
						false))
					st.onEnableEvent(preferences.getBoolean(st.getType(), false));

				if (mSensorModuleManager != null)
					st.setActiveModule(mSensorModuleManager
							.isActiveModule(sensorFamily));
				else
					Log.e(TAG, "mSensorModuleManager'S still null!");

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onStop() {
		if (D)
			Log.d(TAG, "onStop");
		
        // Unbind from the service
		if (mBound) {
			getActivity().getApplicationContext().unbindService(mSensorModuleManagerConnection);
			mBound = false;
		}
		else 
			Log.i(TAG, "mSensorModuleManagerConnection is already unbounded");
		
		if (mSensorModuleManagerIntent != null)
			getActivity().stopService(mSensorModuleManagerIntent);
		if (mMessageHandlerIntent != null)
			getActivity().stopService(mMessageHandlerIntent);
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		if (D)
			Log.d(TAG, "onPauseView");
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		if (sensorTypes != null) {
			for (SensorType st : sensorTypes) {
				String sensorType = st.getType();
				String deviceAdd = pref.getString(sensorType + deviceMac, "");
				String sensorFamily = pref.getString(sensorType + deviceType, "");
				st.setDeviceFamily(sensorFamily);
				st.setDeviceMac(deviceAdd);

				// in case of changing device, sensor must be enable again
//				st.onEnableEvent(st.isEnabled());

				st.setActiveModule(mSensorModuleManager
						.isActiveModule(sensorFamily));

			}

			mAdapter.notifyDataSetChanged();
		}
	}

//	private BluetoothDevice getDevice(String deviceName) {
//		BluetoothDevice[] bondDevice = (BluetoothDevice[]) mBluetoothAdapter
//				.getBondedDevices().toArray(new BluetoothDevice[0]);
//		for (BluetoothDevice bd : bondDevice) {
//			if (bd.getName().equals(deviceName))
//				return bd;
//		}
//		return null;
//	}

	public class SensorType {
		private String sensorType;
		private String mDevice;
		private String[] sensorNames;
		private String deviceFamily;
		private boolean activeModule;

		private boolean enable;

		public SensorType(String type, String[] names) {
			this.sensorType = type;
			this.sensorNames = names;
		}

		public void setActiveModule(boolean activeModule) {
			this.activeModule = activeModule;
		}

		public boolean isActiveModule() {
			return this.activeModule;
		}

		public boolean isCheckable() {
			return (deviceFamily != null && mDevice != null);
		}

		public String getType() {
			return this.sensorType;
		}

		public void onConnectEvent() {
			if (mSensorModuleManager != null) {
				mSensorModuleManager.connectModule(deviceFamily);
			}
		}

		public void onEnableEvent(boolean en) {
			if (mSensorModuleManager != null && isCheckable()) {
				mSensorModuleManager.enableModule(deviceFamily, en,
						deviceFamily, mDevice);

				// auto-save state of sensor's Enabling on phone
				// so that each time this app open, sensor can be start
				// immediately
				// preferences.edit().putBoolean(sensorType, en).commit();
			}
			this.enable = en;
		}

		public boolean isEnabled() {
			return this.enable;
		}

		public boolean isOn() {
			return (deviceFamily != null && !deviceFamily.equals(getResources()
					.getString(R.string.dummy_empty)));
		}

		public void onSetupSensor() {
			openSetupSensorActivity(sensorType, sensorNames);
		}

		public void setDeviceMac(String device) {
			this.mDevice = device;
		}

		public String hasDevice() {
			return mDevice;
		}

		public String getDeviceFamily() {
			return deviceFamily;
		}

		public void setDeviceFamily(String deviceFamily) {
			this.deviceFamily = deviceFamily;
		}
	}

	/**
	 * Open up a new tab for setting up sensors
	 * 
	 * @param sensorType
	 * @param sensorNames
	 */
	private void openSetupSensorActivity(String sensorType, String[] sensorNames) {
		Intent intent = new Intent(getActivity().getApplicationContext(),
				SensorSettingsActivity.class);
		Bundle arg = new Bundle();
		arg.putString("type", sensorType);
		arg.putStringArray("names", sensorNames);
		intent.putExtras(arg);
		startActivity(intent);
	}
}
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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.SensorConfigFragment;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.SensorModuleManager;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.messagehandler.MessageHandler;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Design to fit on Google Glass GUI
 * @author HieuHa
 *
 */
public class MyHealthHubGlassMainActivity extends FragmentActivity {
	// for debugging
	private static final String TAG = MyHealthHubGlassMainActivity.class
			.getSimpleName();
	private static boolean D = true;

	/** Flag indicating whether we have called bind on the service. */
	boolean mBound;

	private ViewHolder viewHolder;
	private SharedPreferences pref;

	// for communication with SensorModuleManager
	private Intent mSensorModuleManagerIntent;
	private Intent mMessageHandlerIntent;
	private SensorModuleManager.SensorModuleManagerBinder mSensorModuleManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (D)
			Log.d(TAG, "onCreate");
		setContentView(R.layout.glass_sensor_type_config);

		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		// StartProducer message handler
		mMessageHandlerIntent = new Intent(this, MessageHandler.class);
		startService(mMessageHandlerIntent);

		// StartProducer sensor modules
		mSensorModuleManagerIntent = new Intent(getApplicationContext(),
				SensorModuleManager.class);
		getApplicationContext().bindService(mSensorModuleManagerIntent,
				mSensorModuleManagerConnection, Context.BIND_AUTO_CREATE);
		startService(mSensorModuleManagerIntent);

		viewHolder = new ViewHolder();
		viewHolder.tv_typePulse = (TextView) findViewById(R.id.sensor_name);
		viewHolder.tv_typePulse.setText(getResources()
				.getString(R.string.pulse));

		viewHolder.tv_PulseSensorAdd = (TextView) findViewById(R.id.device_add);

		viewHolder.pulseCheckBox = (CheckBox) findViewById(R.id.enablePulse);
		viewHolder.autoConnectCheckBox = (CheckBox) findViewById(R.id.autoEnable);

		viewHolder.tv_typeAcc = (TextView) findViewById(R.id.acc_sensor_name);
		viewHolder.tv_AccSensorAdd = (TextView) findViewById(R.id.acc_device_add);
		viewHolder.accCheckBox = (CheckBox) findViewById(R.id.enableAcc);

	}

	private static class ViewHolder {
		CheckBox autoConnectCheckBox;
		TextView tv_typePulse;
		TextView tv_PulseSensorAdd;
		CheckBox pulseCheckBox;

		TextView tv_typeAcc;
		TextView tv_AccSensorAdd;
		CheckBox accCheckBox;

	}

	/**
	 * Sets up a connection to the SensorModuleManager.
	 */
	private ServiceConnection mSensorModuleManagerConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			if (D)
				Log.d(TAG, "I am connected to SensorModuleManagerBinder: "
						+ className.toString());
			mSensorModuleManager = (SensorModuleManager.SensorModuleManagerBinder) binder;
			mBound = true;
			checkAutoConnectEnable();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBound = false;
			Toast.makeText(getApplicationContext(),
					"mSensorModuleManager: disconnected from service.",
					Toast.LENGTH_SHORT).show();
		}
	};

	private void checkAutoConnectEnable() {
		viewHolder.autoConnectCheckBox.setChecked(pref.getBoolean(
				Preferences.AUTO_CONNECT_ENABLED, false));

		String pulseType = getResources().getString(R.string.pulse);
		boolean enPulse = pref.getBoolean(pulseType, false);
		if (enPulse) {
			viewHolder.pulseCheckBox.setChecked(enPulse);
			String deviceAdd = pref.getString(pulseType
					+ SensorConfigFragment.deviceMac, "-");
			String sensorFamily = pref.getString(pulseType
					+ SensorConfigFragment.deviceType, "-");
			viewHolder.tv_typePulse.setText(sensorFamily);
			viewHolder.tv_PulseSensorAdd.setText(deviceAdd);
			// if (mSensorModuleManager != null) {
			// mSensorModuleManager.enableModule(sensorFamily, true,
			// sensorFamily, deviceAdd);
			// }
		}

		String accType = getResources().getString(R.string.acc_leg);
		if (pref.getBoolean(accType, false)) {
			String deviceAdd = pref.getString(accType
					+ SensorConfigFragment.deviceMac, "-");
			String sensorFamily = pref.getString(accType
					+ SensorConfigFragment.deviceType, "-");
			viewHolder.tv_typeAcc.setText(sensorFamily);
			viewHolder.tv_AccSensorAdd.setText(deviceAdd);
			viewHolder.accCheckBox.setChecked(true);
			// if (mSensorModuleManager != null) {
			// mSensorModuleManager.enableModule(sensorFamily, true,
			// sensorFamily, deviceAdd);
			// }
		}
	}

	public void onClickAutoEnable(View v) {
		boolean en = viewHolder.autoConnectCheckBox.isChecked();
		Log.d(TAG, "autoEnable:" + en);
		pref.edit().putBoolean(Preferences.AUTO_CONNECT_ENABLED, en).commit();
	}

	private void onClickEnableDevice(String sensorType) {
		boolean en = pref.getBoolean(sensorType, false);
		String deviceAdd = pref.getString(sensorType
				+ SensorConfigFragment.deviceMac, "");
		String sensorFamily = pref.getString(sensorType
				+ SensorConfigFragment.deviceType, "");
		if (mSensorModuleManager != null) {
			mSensorModuleManager.enableModule(sensorFamily, !en, sensorFamily,
					deviceAdd);

			// auto-save state of sensor's Enabling on phone
			// so that each time this app open, sensor can be start
			// immediately
			pref.edit().putBoolean(sensorType, !en).commit();
		}
	}

	public void onClickSelectPulseType(View v) {
		Bundle extras = new Bundle();
		openSelectSensorType(extras, getResources().getString(R.string.pulse),
				getResources().getStringArray(R.array.type_pulse));
	}

	public void onClickEnablePulse(View v) {
		onClickEnableDevice(getResources().getString(R.string.pulse));
	}

	public void onClickSelectAccSensor(View v) {
		Bundle extras = new Bundle();
		openSelectSensorType(extras,
				getResources().getString(R.string.acc_leg), getResources()
						.getStringArray(R.array.type_acc_leg));
	}

	public void onClickEnableAccSensor(View v) {
		onClickEnableDevice(getResources().getString(R.string.acc_leg));
	}

	public void onClickExit(View v) {
		this.finish();
	}

	private void openSelectSensorType(Bundle extras, String sensorType,
			String[] names) {
		DialogFragment deviceDialog = new SelectDeviceDialogFragment();
		extras.putInt("position", 0);
		extras.putBoolean("device", false);
		extras.putString("sensorType", sensorType);
		extras.putStringArray("names", names);
		deviceDialog.setArguments(extras);
		getFragmentManager().beginTransaction();
		deviceDialog.show(getSupportFragmentManager().beginTransaction(),
				"deviceDialog");
	}

	private void openSelectDeviceDialog(String sensorType) {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			BluetoothDevice[] mAllBondedDevices = (BluetoothDevice[]) mBluetoothAdapter
					.getBondedDevices().toArray(new BluetoothDevice[0]);

			int mDeviceIndex = 0;
			if (mAllBondedDevices.length > 0) {
				int deviceCount = mAllBondedDevices.length;
				String[] deviceNames = new String[deviceCount];
				int i = 0;
				for (BluetoothDevice device : mAllBondedDevices) {
					deviceNames[i++] = device.getName() + "|"
							+ device.getAddress();
				}
				DialogFragment deviceDialog = new SelectDeviceDialogFragment();
				Bundle args = new Bundle();
				args.putString("sensorType", sensorType);
				args.putStringArray("names", deviceNames);
				args.putInt("position", mDeviceIndex);
				args.putBoolean("device", true);
				deviceDialog.setArguments(args);
				getFragmentManager().beginTransaction();
				deviceDialog.show(getSupportFragmentManager()
						.beginTransaction(), "deviceDialog");
			}
		}
	}

	/**
	 * Dialog to display a list of bonded Bluetooth devices for user to select
	 * from. This is needed only for connection initiated from the application.
	 */
	public class SelectDeviceDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle arg = getArguments();
			final boolean isSelectingDevice = arg.getBoolean("device");
			final String[] names = arg.getStringArray("names");
			final String sensorType = arg.getString("sensorType");
			int position = arg.getInt("position", -1);
			if (position == -1)
				position = 0;
			return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.select_device)
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dismiss();
								}
							})
					.setSingleChoiceItems(names, position,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									int i = (which == -1) ? 0 : which;
									String value = names[i];
									if (isSelectingDevice) {
										value = names[i].split("\\|", 2)[1]; // get
																				// mac
																				// Address
																				// of
																				// selected
																				// device
										updateBTDeviceSummary(sensorType, value);
									} else {
										updateBTTypeSummary(sensorType, value);
									}
									dismiss();
								}
							}).create();
		}
	}

	private void updateBTTypeSummary(String sensorType, String value) {
		if (value.equals(getResources().getString(R.string.dummy_empty))) {
			updateBTDeviceSummary(sensorType, "-");
		}
		if (sensorType.equals(getResources().getString(R.string.pulse))) {
			viewHolder.tv_typePulse.setText(value);
			pref.edit()
					.putString(sensorType + SensorConfigFragment.deviceType,
							value).commit();

			// auto start select deviceMac after select pulse type
			openSelectDeviceDialog(getResources().getString(R.string.pulse));
		} else if (sensorType
				.equals(getResources().getString(R.string.acc_leg))) {
			viewHolder.tv_typeAcc.setText(value);
			pref.edit()
					.putString(sensorType + SensorConfigFragment.deviceType,
							value).commit();

			// auto start select deviceMac after select pulse type

			openSelectDeviceDialog(getResources().getString(R.string.acc_leg));
		}
	}

	private void updateBTDeviceSummary(String sensorType, String value) {
		if (sensorType.equals(getResources().getString(R.string.pulse))) {
			viewHolder.tv_PulseSensorAdd.setText(value);
			pref.edit()
					.putString(sensorType + SensorConfigFragment.deviceMac,
							value).commit();
		}

		else if (sensorType.equals(getResources().getString(R.string.acc_leg))) {
			viewHolder.tv_AccSensorAdd.setText(value);
			pref.edit()
					.putString(sensorType + SensorConfigFragment.deviceMac,
							value).commit();
		}
	}

	@Override
	protected void onDestroy() {
		if (D)
			Log.d(TAG, "onDestroy " + TAG);
		
        // Unbind from the service
		if (mBound) {
			getApplicationContext().unbindService(mSensorModuleManagerConnection);
			mBound = false;
		}
		else 
			Log.i(TAG, "mSensorModuleManagerConnection is already unbounded");
		
		stopService(mSensorModuleManagerIntent);
		stopService(mMessageHandlerIntent);
		super.onDestroy();
	}
}
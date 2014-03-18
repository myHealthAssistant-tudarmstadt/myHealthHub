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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.SensorConfigFragment;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SensorSettingsActivity extends FragmentActivity {
	private static String TAG = SensorSettingsActivity.class.getName();
	private ViewHolder viewHolder;
	private String sensorType;
	private String deviceType;
	private String deviceMacAdd;
	private SharedPreferences pref;

	// private CheckBoxPreference discoverableBox;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.e(TAG, "onCreate");

		setContentView(R.layout.sensor_connection_settings_fragment);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		sensorType = extras.getString("type");

		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		// Keeps reference to avoid future findViewById()
		viewHolder = new ViewHolder();
		viewHolder.tv_sensor_type = (TextView) findViewById(R.id.tv_sensor_type);
		viewHolder.tv_sensor_type.setText(sensorType);
		// -----
		viewHolder.ll_bt_type = (LinearLayout) findViewById(R.id.ll_bluetooth_type);
		viewHolder.ll_bt_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openSelectSensorType();
			}
		});

		deviceType = pref.getString(sensorType
				+ SensorConfigFragment.deviceType, "");
		viewHolder.tv_bt_type_sum = (TextView) findViewById(R.id.tv_bluetooth_type_summary);
		viewHolder.tv_bt_type_sum.setText(deviceType);
		// -----
		viewHolder.ll_bt_sel = (LinearLayout) findViewById(R.id.ll_bluetooth_sel);
		viewHolder.ll_bt_sel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openSelectDeviceDialog();
			}
		});

		viewHolder.tv_bt_sel_sum = (TextView) findViewById(R.id.tv_bluetooth_sel_summary);
		deviceMacAdd = pref.getString(sensorType
				+ SensorConfigFragment.deviceMac, "");
		if (!deviceMacAdd.isEmpty()){
			String name = getDeviceName(deviceMacAdd);
			viewHolder.tv_bt_sel_sum.setText(name + " (" + deviceMacAdd + ")");
		}
	}

	private String getDeviceName(String deviceAddress) {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		BluetoothDevice[] bondDevice = (BluetoothDevice[]) mBluetoothAdapter
				.getBondedDevices().toArray(new BluetoothDevice[0]);
		for (BluetoothDevice bd : bondDevice) {
			if (bd.getAddress().equals(deviceAddress))
				return bd.getName();
		}
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private static class ViewHolder {
		TextView tv_sensor_type;
		// -------
		LinearLayout ll_bt_type;
		TextView tv_bt_type_sum;
		// -------
		LinearLayout ll_bt_sel;
		TextView tv_bt_sel_sum;

	}

	private void openSelectSensorType() {
		Bundle extras = getIntent().getExtras();
		DialogFragment deviceDialog = new SelectDeviceDialogFragment();
		extras.putInt("position", -1);
		extras.putBoolean("device", false);
		deviceDialog.setArguments(extras);
		getFragmentManager().beginTransaction();
		deviceDialog.show(getSupportFragmentManager().beginTransaction(),
				"deviceDialog");
	}

	private void openSelectDeviceDialog() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			BluetoothDevice[] mAllBondedDevices = (BluetoothDevice[]) mBluetoothAdapter
					.getBondedDevices().toArray(new BluetoothDevice[0]);

			int mDeviceIndex = 0;
			if (mAllBondedDevices.length > 0) {
				int deviceCount = mAllBondedDevices.length;
				String[] deviceNames = new String[deviceCount];
				for (int i = 0; i < deviceCount; i++) {
					BluetoothDevice device = mAllBondedDevices[i];
					deviceNames[i] = device.getName() + "\n|"
							+ device.getAddress();
					if (deviceMacAdd.equals(device.getAddress())) {
						mDeviceIndex = i;
					}
				}
				DialogFragment deviceDialog = new SelectDeviceDialogFragment();
				Bundle args = new Bundle();
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
			int position = arg.getInt("position", 0);
			if (position == -1){
				for (int i = 0; i< names.length; i++){
					if (deviceType.equals(names[i]))
						position = i;
				}
			}
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
										String[] sp = value.split("\\|", 2);
										String id = sp[0];
										String mac = sp[1];// get mac address of
															// selected device
										updateBTDeviceSummary(id, mac);
									} else {
										updateBTTypeSummary(value);
									}
									dismiss();
								}
							}).create();
		}
	}

	private void updateBTDeviceSummary(String id, String mac) {
		if (!id.isEmpty())
			viewHolder.tv_bt_sel_sum.setText(id + " (" + mac + ")");
		else
			viewHolder.tv_bt_sel_sum.setText("");
		deviceMacAdd = mac;
		pref.edit()
				.putString(sensorType + SensorConfigFragment.deviceMac, mac)
				.commit();

	}

	private void updateBTTypeSummary(String value) {
		viewHolder.tv_bt_type_sum.setText(value);
		deviceType = value;
		pref.edit()
				.putString(sensorType + SensorConfigFragment.deviceType, value)
				.commit();
		if (value.equals(getResources().getString(R.string.dummy_empty))) {
			viewHolder.ll_bt_sel.setClickable(false);
			updateBTDeviceSummary("", "");
		} else {
			viewHolder.ll_bt_sel.setClickable(true);
		}
	}
}
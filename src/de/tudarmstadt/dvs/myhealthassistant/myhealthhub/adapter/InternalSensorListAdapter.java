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

package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter;

import java.util.List;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.InternalSensorService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.opengl.Visibility;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author HieuHa
 * 
 *         Represent each Sensor with on/off button
 */
public class InternalSensorListAdapter extends ArrayAdapter<Integer> {
	private static final String TAG = InternalSensorListAdapter.class
			.getSimpleName();
	private LayoutInflater mInflater;
	private static final int POS_TAG = 1 + 2 << 24;
	public static final String PREF_SENSOR_TYPE = "SensorType=";
	private SharedPreferences preferences;

	public InternalSensorListAdapter(Activity activity, Context ctx) {
		super(ctx, 0);
		mInflater = (LayoutInflater) LayoutInflater.from(activity);
		preferences = PreferenceManager.getDefaultSharedPreferences(activity);
	}

	static class ViewHolder {
		private TextView mTitle;
		private TextView mStatus;
		private CompoundButton mSwitch;
		private TextView mDevice;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.list_sensor_config_row, parent,
					false);
		} else {
			view = convertView;
		}

		final int item = getItem(position);

		// Create the view holder
		final ViewHolder viewHolder = new ViewHolder();

		viewHolder.mTitle = ((TextView) view.findViewById(R.id.sensor_name));
		viewHolder.mStatus = ((TextView) view.findViewById(R.id.connect_type));
		viewHolder.mSwitch = (CompoundButton) view.findViewById(R.id.switch1);
		viewHolder.mDevice = ((TextView) view.findViewById(R.id.device_add));

		view.setTag(POS_TAG, position);

		String sensorName = "Unknown";
		
		switch (item) {
		case Sensor.TYPE_ACCELEROMETER:
			sensorName = "Accelerometer";
			viewHolder.mSwitch.setChecked(isOn(item));
			viewHolder.mSwitch.setVisibility(View.VISIBLE);
			break;
		case Sensor.TYPE_LIGHT:
			sensorName = "Light";
			viewHolder.mSwitch.setChecked(isOn(item));
			break;
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			sensorName = "Ambient Temperature";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;
		case Sensor.TYPE_GRAVITY:
			sensorName = "Gravity";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;
		case Sensor.TYPE_GYROSCOPE:
			sensorName = "Gyroscope";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			sensorName = "Linear Acceleration";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			sensorName = "Magnetic Field";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;
		case Sensor.TYPE_PRESSURE:
			sensorName = "Pressure";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;
		case Sensor.TYPE_PROXIMITY:
			sensorName = "Proximity";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			sensorName = "Relative Humidity";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			sensorName = "Rotation Vector";
			viewHolder.mSwitch.setVisibility(View.GONE);
			break;

		}
		viewHolder.mTitle.setText(sensorName);
		viewHolder.mDevice.setText("");
		viewHolder.mStatus.setText("");

		viewHolder.mSwitch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						setSensor(item, isChecked);
					}
				});
		return view;
	}

	public void setData(List<Integer> data) {
		clear();
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				add(data.get(i));
			}
		}
	}

	private boolean isOn(int sensorType) {
		boolean b = preferences.getBoolean(PREF_SENSOR_TYPE + sensorType, false);
		if (b){
			setSensor(sensorType, true);
		}
		return b;
	}

	private boolean setSensor(int type, boolean on) {
		boolean b = preferences.edit().putBoolean(PREF_SENSOR_TYPE + type, on).commit();
		if (on){
			Intent intent = new Intent("de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ISS");
			intent.putExtra(PREF_SENSOR_TYPE, type);
			getContext().startService(intent);
		} else {
			Intent intent = new Intent("de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ISS");
			getContext().stopService(intent);
		}
		return b;
	}
}
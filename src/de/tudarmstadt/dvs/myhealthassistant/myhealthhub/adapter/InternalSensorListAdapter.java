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
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities.InternalSensorActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 
 * @author HieuHa
 * 
 *         Represent each Sensor with on/off button
 */
public class InternalSensorListAdapter extends ArrayAdapter<Sensor> {
	private LayoutInflater mInflater;
	private static final int POS_TAG = 1 + 2 << 24;
	private Context context;
	private Activity activity;
	private SharedPreferences preferences;

	public InternalSensorListAdapter(Activity activity, Context ctx) {
		super(ctx, 0);
		mInflater = (LayoutInflater) LayoutInflater.from(activity);
		this.context = ctx;
		this.activity = activity;
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

		final Sensor item = getItem(position);

		// Create the view holder
		final ViewHolder viewHolder = new ViewHolder();

		viewHolder.mTitle = ((TextView) view.findViewById(R.id.sensor_name));
		viewHolder.mStatus = ((TextView) view.findViewById(R.id.connect_type));
		viewHolder.mSwitch = (CompoundButton) view.findViewById(R.id.switch1);
		viewHolder.mDevice = ((TextView) view.findViewById(R.id.device_add));

		view.setTag(POS_TAG, position);

		viewHolder.mTitle.setText(item.getName());
		viewHolder.mDevice.setText(item.getVendor());
		viewHolder.mStatus.setText("Version:" + item.getVersion());
		
//		viewHolder.mSwitch.setChecked(isOn(item.getName()));
		viewHolder.mSwitch.setChecked(false);
		setSensor(item.getName(), item.getType(), false);
//		view.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				boolean oldValue = viewHolder.mSwitch.isChecked();
//				viewHolder.mSwitch.setChecked(!oldValue);
//				setSensor(item.getName(), item.getType(), !oldValue);
//			}
//		});

		// viewHolder.mStatus.setTextColor(getContext().getResources().getColor(
		// (android.R.color.holo_red_light)));
		//
		// viewHolder.mStatus.setText(item.toString());
		viewHolder.mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setSensor(item.getName(), item.getType(), isChecked);
			}
		});
		return view;
	}
	
	

	public void setData(List<Sensor> data) {
		clear();
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				add(data.get(i));
			}
		}
	}

	private boolean isOn(String sensorName) {
		return preferences.getBoolean(sensorName, false);
	}

	private InternalSensorActivity isa;

	private boolean setSensor(String sensorName, int type, boolean on) {
		if (on) {
			isa = new InternalSensorActivity(type, activity);
			isa.onStart();

		} else {
			if (isa != null) {
				if (isa.isStated())
					isa.onStop();
			}
		}
		return preferences.edit().putBoolean(sensorName, on).commit();
	}
}
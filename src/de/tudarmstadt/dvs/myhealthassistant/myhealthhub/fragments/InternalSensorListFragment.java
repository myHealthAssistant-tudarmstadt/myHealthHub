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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter.InternalSensorListAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author HieuHa
 * 
 *         This class shows all sensors that are available on android device,
 *         allows user to quickly enable/disable them.
 */
public class InternalSensorListFragment extends ListFragment {
	
	// for debugging
	private static final String TAG = InternalSensorListFragment.class
			.getSimpleName();
	private static boolean D = true;

	private View rootView;
	private List<Integer> SensorTypes;
//	private Map<Integer, String> SensorTypes;
//	private SparseArray<String> SensorTypes;
	private InternalSensorListAdapter mAdapter;

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

		SensorTypes = new ArrayList<Integer>();
		SensorTypes.add(Sensor.TYPE_ACCELEROMETER);
		SensorTypes.add(Sensor.TYPE_LIGHT);
		SensorTypes.add(Sensor.TYPE_AMBIENT_TEMPERATURE);
		SensorTypes.add(Sensor.TYPE_GRAVITY);
		SensorTypes.add(Sensor.TYPE_GYROSCOPE);
		SensorTypes.add(Sensor.TYPE_LINEAR_ACCELERATION);
		SensorTypes.add(Sensor.TYPE_MAGNETIC_FIELD);
		SensorTypes.add(Sensor.TYPE_PRESSURE);
		SensorTypes.add(Sensor.TYPE_PROXIMITY);
		SensorTypes.add(Sensor.TYPE_RELATIVE_HUMIDITY);
		SensorTypes.add(Sensor.TYPE_ROTATION_VECTOR);
		
//		SensorTypes = new SparseArray<String>();		
//		SensorTypes = new HashMap<Integer, String>();
//		SensorTypes.put(Sensor.TYPE_ACCELEROMETER, "Accelerometer");
//		SensorTypes.put(Sensor.TYPE_LIGHT, "Light");
//		SensorTypes.put(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature");
//		SensorTypes.put(Sensor.TYPE_GRAVITY, "Gravity");
//		SensorTypes.put(Sensor.TYPE_GYROSCOPE, "Gyroscope");
//		SensorTypes.put(Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration");
//		SensorTypes.put(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Field");
//		SensorTypes.put(Sensor.TYPE_PRESSURE, "Pressure");
//		SensorTypes.put(Sensor.TYPE_PROXIMITY, "Proximity");
//		SensorTypes.put(Sensor.TYPE_RELATIVE_HUMIDITY, "Relative Humidity");
//		SensorTypes.put(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector");
		
		mAdapter = new InternalSensorListAdapter(getActivity(), getActivity().getApplicationContext());
		setListAdapter(mAdapter);
		
//		SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		
		mAdapter.setData(SensorTypes);
		
		
	}

}

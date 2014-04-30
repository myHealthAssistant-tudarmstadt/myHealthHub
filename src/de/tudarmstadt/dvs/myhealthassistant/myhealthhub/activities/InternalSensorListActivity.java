package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities;

import java.util.ArrayList;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter.InternalSensorListAdapter;
import android.app.ListActivity;
import android.hardware.Sensor;
import android.os.Bundle;

public class InternalSensorListActivity extends ListActivity {
	private ArrayList<Integer> SensorTypes;
	
	@Override
	public void onCreate(Bundle saveInstances) {
		super.onCreate(saveInstances);
		setContentView(R.layout.fragment_list_with_empty_container);

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
		
		InternalSensorListAdapter mAdapter = new InternalSensorListAdapter(this, this.getApplicationContext());
		setListAdapter(mAdapter);
		
//		SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		
		mAdapter.setData(SensorTypes);
		
	}
}

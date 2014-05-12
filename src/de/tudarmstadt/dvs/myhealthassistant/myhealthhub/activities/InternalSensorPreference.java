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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class InternalSensorPreference extends PreferenceActivity {

	private static final String TAG = InternalSensorPreference.class.getSimpleName();
	private Context context;
	private CheckBoxPreference checkboxPref;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getApplicationContext();
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new MyPreferenceFragment())
				.commit();
	}

	public class MyPreferenceFragment extends PreferenceFragment {
		public MyPreferenceFragment() {
			// Just an empty constructor!
		}

		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.internal_sensor__confix);

		}
	}
	
	@Override
	public void onStop(){
		Log.e(TAG, "onStop");
		Intent i = new Intent("de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ISS");
		context.getApplicationContext().stopService(i);
		
		if (isAnyServiceOn()){
			// restart service
			context.getApplicationContext().startService(i);
		}
		else {
			// cancel the repeating alarm
			Intent intent = new Intent(
					"de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ALARM");
			PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(sender);
		}
		
		super.onStop();
	}
	
	private boolean isAnyServiceOn(){
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String[] sensorTypesArray = getResources().getStringArray(
				R.array.internal_sensor_config);
		
		for (String s : sensorTypesArray){
			if (pref.getBoolean(s, false))
				return true;
		}
//		if (pref.getBoolean(getResources().getString(R.string.in_acc), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_grav), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_gyrs), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_hum), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_lig), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_lin_acc), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_mag), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_pres), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_prox), false)
//				|| pref.getBoolean(getResources().getString(R.string.in_tem), false)){
//			return true;
//		}
		
		return false;
	}
}

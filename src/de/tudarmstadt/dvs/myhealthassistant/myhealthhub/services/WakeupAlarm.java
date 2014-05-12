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

package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.Preferences;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class WakeupAlarm extends BroadcastReceiver {
	private static final String TAG = WakeupAlarm.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();

		// to check in case of system reboot if the SYS_MONITORING service was
		// enabled
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (preferences.getBoolean(Preferences.SYS_MONITORING, false)) {
			startSMService(context);
		}
		
		// check for internal Sensor service
		String[] sensorTypesArray = context.getResources().getStringArray(
				R.array.internal_sensor_config);

		for (String s : sensorTypesArray) {
			if (preferences.getBoolean(s, false)) {
				Intent i = new Intent(
						"de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ISS");
				context.getApplicationContext().startService(i);
				
				wl.release();
				return;
			}
		}

		Intent i = new Intent(
				"de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ISS");
		context.getApplicationContext().stopService(i);
		wl.release();

	}

	private void startSMService(Context context) {
		// start System monitor service
		Intent i = new Intent(context.getApplicationContext(),
				SystemMonitor.class);
		context.getApplicationContext().startService(i);
	}

	public void setAlarm(Context context) {
		Log.e(TAG, "alarm being set repeatig");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// Intent i = new Intent(context, WakeupAlarm.class);
		Intent i = new Intent(
				"de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ALARM");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		// repeat after 5min
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 60 * 5, pi); // Millisec * Second * Minute FIXME
	}

	public void setOnceTimeAlarm(Context context) {
		Log.e(TAG, "alarm being set for once time");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// Intent i = new Intent(context, WakeupAlarm.class);
		Intent i = new Intent(
				"de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ALARM");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		// set for 5min
		long triggerAtMillis = System.currentTimeMillis() + (1000 * 60 * 5);
		am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
	}

	public void cancelAlarm(Context context) {
		// Intent i = new Intent(context, WakeupAlarm.class);
		Log.e(TAG, "alarm being canceled");
		Intent i = new Intent(
				"de.tudarmstadt.dvs.myhealthassistant.myhealthhub.START_ALARM");
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

}
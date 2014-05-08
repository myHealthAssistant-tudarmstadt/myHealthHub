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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.Preferences;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.SystemMonitor;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.WakeupAlarm;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDB;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDBMS;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.widget.TextView;

/**
 * @author Chris
 * 
 */
public class EditPreferences extends PreferenceActivity {
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
			addPreferencesFromResource(R.xml.preferences);

			checkboxPref = (CheckBoxPreference) getPreferenceManager()
					.findPreference(Preferences.SYS_MONITORING);
			checkboxPref
					.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							openPopupDialog("System Monitoring", context.getString(R.string.popup_sys_monitor), sysMonDialogClickListener);
							// checkbox appear to be checked before the dialog
							// appear, so we force it to not change the display
							// yet
							checkboxPref.setChecked(!checkboxPref.isChecked());
							return false;
						}
					});

			Preference preference = findPreference(getResources().getString(R.string.settings_sensor_bluetooth_pairing_key));
			preference
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference pref) {
							Intent settingsIntent = new Intent(
									Settings.ACTION_BLUETOOTH_SETTINGS);
							startActivity(settingsIntent);
							return true;
						}
					});

			Preference trafficClearing = findPreference(getResources().getString(R.string.settings_traffic_clearing));
			trafficClearing
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference pref) {
							openPopupDialog("Clear Records", context.getString(R.string.popup_sys_traffic_clearing), trafficClearingDialogClickListener);
							return true;
						}
					});
		}
	}

	private void openPopupDialog(String title, String mess, DialogInterface.OnClickListener dialogClickListener) {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView bodyView = (TextView) layoutInflater.inflate(
				R.layout.popup_dialog, null);
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setView(bodyView).setTitle(title)
				.setMessage(mess)
				.setPositiveButton(android.R.string.yes, dialogClickListener)
				.setNegativeButton(android.R.string.no, dialogClickListener)
				.create().show();

	}

	WakeupAlarm wAlarm = new WakeupAlarm();
	private DialogInterface.OnClickListener sysMonDialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				checkboxPref.setChecked(true);
				// schedule alarm for next time system monitor service running
				wAlarm.setAlarm(context);
				dialog.dismiss();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				checkboxPref.setChecked(false);
				dialog.dismiss();
				break;
			}
		}
	};
	
	private DialogInterface.OnClickListener trafficClearingDialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				LocalTransformationDBMS db = new LocalTransformationDBMS(getApplicationContext());
				db.open();
				db.deleteAllTrafficRecords();
				db.close();
				dialog.dismiss();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				// do nothing
				dialog.dismiss();
				break;
			}
		}
	};
}
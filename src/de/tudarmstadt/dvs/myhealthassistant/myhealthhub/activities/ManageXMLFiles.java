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
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.AndroidExplorer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


/**
 * @author Christian Seeger
 * 
 */
public class ManageXMLFiles extends Activity {

	private static String TAG = "ManageXMLFiles";
	private static boolean D = false;

	public static String MAPPING_CHANGED = "mappingChanged";
	public static String SENSING_RULES_CHANGED = "sensingRulesChanged";
	private Intent changesMade;
	
	private static int GET_FILE_REQUEST_ENV = 1234;
	private static int GET_FILE_REQUEST_MAC = 1235;
	private String filename;
	
	private SharedPreferences preferences;
	private Editor preferencesEditor;

	/** Called when the ACTIVITY is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_xml_files_activity);		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferencesEditor = preferences.edit();
		TextView tv;
		
		tv = (TextView)findViewById(R.id.tvMACtoSensor);
		tv.setText(preferences.getString(Preferences.XML_FILE_MAC_TO_SENSOR, "unknown"));
		
		tv = (TextView)findViewById(R.id.tvEnvRules);
		tv.setText(preferences.getString(Preferences.XML_FILE_ENV_RULES, "unknown"));
		
		changesMade = new Intent();
		changesMade.putExtra(MAPPING_CHANGED, false);
		changesMade.putExtra(SENSING_RULES_CHANGED, false);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GET_FILE_REQUEST_ENV) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				if (data != null) {
					Bundle bundle = data.getExtras();
					filename = bundle.getString(AndroidExplorer.FILE_NAME);

					preferencesEditor.putString(Preferences.XML_FILE_ENV_RULES, filename);
					((TextView)findViewById(R.id.tvEnvRules)).setText(filename);
					
					changesMade.putExtra(SENSING_RULES_CHANGED, true);
				}
				break;
			case Activity.RESULT_CANCELED:
				break;
			}
		} else if (requestCode == GET_FILE_REQUEST_MAC) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				if (data != null) {
					Bundle bundle = data.getExtras();
					filename = bundle.getString(AndroidExplorer.FILE_NAME);

					preferencesEditor.putString(Preferences.XML_FILE_MAC_TO_SENSOR, filename);
					((TextView)findViewById(R.id.tvMACtoSensor)).setText(filename);
					
					changesMade.putExtra(MAPPING_CHANGED, true);
					
					if(D) Log.d(TAG, "New file for MAC to sensor: "+filename);
				}
				break;
			case Activity.RESULT_CANCELED:
				break;
			}
		}

	}

	public void onClickSelectMappingFile(View v) {
		Intent intent = new Intent(this, AndroidExplorer.class);
		startActivityForResult(intent, GET_FILE_REQUEST_MAC);
	}
	
	public void onClickDefaultMappingFile(View v) {

	}
	
	public void onClickSelectEnvRulesFile(View v) {
		Intent intent = new Intent(this, AndroidExplorer.class);
		startActivityForResult(intent, GET_FILE_REQUEST_ENV);
	}
	
	public void onClickDefaultEnvRulesFile(View v) {
		
	}
	
	public void onClickAbort(View v) {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}
	
	public void onClickSaveChanges(View v) {
		preferencesEditor.commit();
		setResult(Activity.RESULT_OK, changesMade);
		finish();
	}
}
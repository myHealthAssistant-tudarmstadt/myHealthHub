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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Chris
 * 
 */
public class ExportDatabaseTask extends AsyncTask<Void, Void, Boolean> {
	// Debugging
	private static final boolean D = true;
	private static final String TAG = "ExportDatabaseTask";

	private ProgressDialog dialog;
	private Context context;

	public ExportDatabaseTask(Context c) {
		super();
		context = c;
							
		if (D)
			Log.d(TAG, "ExportDatabaseTask started..."+dialog);
	}

	// can use UI thread here
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
/*		dialog = new ProgressDialog(this.context);
		dialog.setMessage("Exporting database...");
		dialog.setIndeterminate(true);
		dialog.show();*/
	}

	// automatically done on worker thread (separate from UI thread)
	@Override
	protected Boolean doInBackground(final Void... args) {

		File dbFile = new File(
				Environment.getDataDirectory()
						+ "/data/de.tudarmstadt.dvs.healthassistant/databases/sensordata.db");
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			//File file = new File(root, dbFile.getName());
			String date =  (String) android.text.format.DateFormat.format(
					"yyyy-MM-dd_hh-mm", new java.util.Date());
			String filename = date+"_"+dbFile.getName();
			//File file = new File(root, dbFile.getName());
			File file = new File(root, filename);
			try {
				file.createNewFile();
				FileUtil.copyFile(dbFile, file);
				return true;
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
				return false;
			}
		} else {
			return false;
		}

	}

	// can use UI thread here
	@Override
	protected void onPostExecute(final Boolean success) {
/*		if (dialog.isShowing()) {
			dialog.dismiss();
		}*/
		if (success) {
			Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT)
					.show();
		}
		
	}
}
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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class ExternalStorageManager {

	private File externalStorage;

	private boolean isExternalStorageAvailable = false;
	private boolean isExternalStorageWriteable = false;

	public ExternalStorageManager() {
		externalStorage = Environment.getExternalStorageDirectory();
		checkExternalStorageAvailability();
	}

	public boolean canRead() {
		return isExternalStorageAvailable;
	}

	public boolean canWrite() {
		return isExternalStorageWriteable;
	}

	public boolean exist(String path) {
		return (canRead() && (new File(externalStorage, path).exists()));
	}

	public void makeDir(String path) {
		if (canWrite()) {
			File make = new File(externalStorage, path);
			make.mkdirs();
		}
	}

	public void makeDirIfNotExist(String path) {
		if (!exist(path)) {
			makeDir(path);
		}
	}

	public void write(String path, String file, String text) {
		try {
			if (externalStorage.canWrite()) {
				File tmpfile = new File(externalStorage, path + file);
				FileWriter writer = new FileWriter(tmpfile);
				BufferedWriter out = new BufferedWriter(writer);
				out.write(text);
				out.close();
			}
		} catch (IOException e) {
			Log.e("ExternalStorageManager", "Could not write file "
					+ e.getMessage());
		}
	}

	public String[] read(String path, String filename) {
		try {
			ArrayList<String> tmpList = new ArrayList<String>();
			File f = new File(externalStorage, path + filename);
			FileInputStream fileIS = new FileInputStream(f);
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					fileIS));

			String l = "";
			while ((l = buf.readLine()) != null) {
				tmpList.add(l);
			}
			String[] r = new String[tmpList.size()];
			for (int i = 0; i < tmpList.size(); i++) {
				r[i] = tmpList.get(i);
			}
			return r;

		} catch (FileNotFoundException e) {
			Log.e("ExternalStorageManager", "Cant find file " + externalStorage
					+ path + filename);
		} catch (IOException e) {
			Log.e("ExternalStorageManager", "IOException while reading "
					+ externalStorage + path + filename);
		}
		return null;

	}

	public String[] readDirectory(String path) {
		File dir = new File(externalStorage, path);
		return dir.list();
	}

	public File[] getFileList(String path) {
		String[] names = readDirectory(path);
		File[] files = new File[names.length];
		for (int i = 0; i < names.length; i++) {
			files[i] = new File(externalStorage, path + names[i]);
		}
		return files;
	}

	public void deleteFile(String path) {
		new File(externalStorage, path).delete();
	}

	private void checkExternalStorageAvailability() {

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			isExternalStorageAvailable = isExternalStorageWriteable = true;
			Log.i("ExternalStorageManager", "We can read and write the media");
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			Log.i("ExternalStorageManager", "We can only read the media");
			isExternalStorageAvailable = true;
			isExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			isExternalStorageAvailable = isExternalStorageWriteable = false;
		}
	}

}
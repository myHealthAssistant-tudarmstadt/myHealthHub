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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class Unzip {
	
	private static final String TAG = Unzip.class.getName();
	private InputStream zipFile;
	private String location;
	private byte[] buffer = new byte[2048];

	public Unzip(InputStream zipFile, String location) {
		this.zipFile = zipFile;
		this.location = location;
	}

	public void unzip() {
		try {
			BufferedInputStream fin = new BufferedInputStream(zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			
			while ((ze = zin.getNextEntry()) != null) {
				
				Log.v(TAG, "Unzipping " + ze.getName());

				if (ze.isDirectory()) {
					makedir(ze.getName());
				} else {
					BufferedOutputStream fout = new BufferedOutputStream( new FileOutputStream(location
							+ ze.getName()));

					int size;
					while ((size = zin.read(buffer)) != -1) {
						fout.write(buffer, 0, size);
					}
					zin.closeEntry();
					fout.flush();
					fout.close();
				}

			}
			zin.close();
		} catch (Exception e) {
			Log.e(TAG, "error by unzipping", e);
		}

	}

	private void makedir(String dir) {
		File f = new File(location + dir);
		boolean created = false;
		created = f.mkdirs();
		if(created){
			Log.i(TAG, "dir created " + dir);
		}
		else{
			Log.i(TAG, "dir creation failled " + dir);
		}
	}
}
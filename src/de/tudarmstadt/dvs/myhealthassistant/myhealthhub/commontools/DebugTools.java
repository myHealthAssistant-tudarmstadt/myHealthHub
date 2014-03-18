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

import android.util.Log;

public class DebugTools {

	public void printByteArray(byte[] array, int length, String info, String TAG) {
		String text = "";
		for(int i = 0; i < length; i++){
			text += "["+i+"] "+array[i]+", ";
		}
		Log.d(TAG, info+" "+text);
	}
	
	public void printByteArrayInHex(byte[] array, int length, String info, String TAG) {
		String text = "";
		for(int i = 0; i < length; i++){
			
			text += "["+i+"] "+java.lang.Integer.toHexString(array[i]&0xff)+", ";
		}
		Log.d(TAG, info+" "+text);
	}
	
	/**
	 * @param b
	 *            is the byte to convert
	 * @return a integer from the given byte
	 */
	public int readUnsignedByte(byte b) {
		return (b & 0xff);
	}

	/** Normalizes the ACCELEROMETER data to a Java format */
	public int normalizeByte(byte data) {
		int value = 0;

		if (getBit(data, 0) == 1) {
			value = data & 0x7F;
		} else {
			value = -(~data + 128);
		}

		return value;
	}
	
	/** Returns the bit at position pos from a given byte */
	public int getBit(byte data, int pos) {
		return data >> (8 - (pos + 1)) & 0x0001;
	}
	
	public void printByteArrayInChar(byte[] array, int length, String info, String TAG) {
		String text = "";
		String element;
		for(int i = 0; i < length; i++){
			if(array[i]==(byte)0x0d) {
				element = "<CR>";
			}  else if (array[i]==(byte)0xa) {
				element = "<LF>";
			} else {
				element = ""+(char)array[i];	
			}
			
			text += "["+i+"] "+element+", ";
		}
		Log.d(TAG, info+" "+text);
	}

	public void printByteArrayInCharAndInt(byte[] array, int length, String info, String TAG) {
		String text = "";
		String element;
		for(int i = 0; i < length; i++){
			if(array[i]==(byte)0x0d) {
				element = "<CR>";
			}  else if (array[i]==(byte)0xa) {
				element = "<LF>";
			} else {
				element = ""+(char)array[i];	
			}
			
			text += "["+i+"] "+element+", "+array[i]+", "+
				java.lang.Integer.toHexString(array[i]&0xff)+" ";
		}
		Log.d(TAG, info+" "+text);
		
	}

	public String getString(byte[] array, int length) {
		String text = "";
		String element;
		for(int i = 0; i < length; i++){
			if(array[i]==(byte)0x0d) {
				element = "<CR>";
			}  else if (array[i]==(byte)0xa) {
				element = "<LF>";
			} else {
				element = ""+(char)array[i];	
			}
			
			text += element+" ";
		}
		return text;
	}
	
	
}
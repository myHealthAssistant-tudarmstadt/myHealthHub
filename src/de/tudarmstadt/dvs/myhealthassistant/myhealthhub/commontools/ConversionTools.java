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

import android.util.Log;

/**
 * @author Chris
 *
 */
public class ConversionTools {
	private String TAG = "ConversionTools";
	
	public byte[] hexChars = {(byte)0x0a, (byte)0x0b, (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f};
	
	/**
	 * Converts a given ASCII number or character (A-F) to the corresponding
	 * byte representation for hex.
	 * @param ascii
	 * @return
	 */
	public byte convertAsciiCharToHex(byte ascii) {
    	byte hexValue = (byte)0x00;
    	if(ascii < 65) {
    		hexValue = (byte)((ascii-48)&0xff);
    	} else {
    		int idx = ascii-65;
    		if(idx<6) {
    			hexValue = hexChars[idx];
    		} else {
    			Log.e(TAG, "No match found in hexChars for: "+ascii);
    		}
    	}
    	return hexValue;
    }
}
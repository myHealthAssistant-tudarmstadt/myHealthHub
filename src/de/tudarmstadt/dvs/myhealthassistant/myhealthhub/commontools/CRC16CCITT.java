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

public class CRC16CCITT { 
	
	private static String TAG = "CRC16CCITT";
	private static boolean D = false;
   
    int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 
	
    public static void main() { 
      // byte[] testBytes = "123456789".getBytes("ASCII");
        //byte[] bytes = args[0].getBytes();
    }
    
    public int calcCRC (byte[] bytes) {
    	 int crc = 0xFFFF;          // initial value
    	
    	 if(D)Log.d(TAG, "Packet length: "+bytes.length);
    	 if(D)printPacket(bytes.length, bytes, "CRC16");
    	 
    	 for (byte b : bytes) {
             for (int i = 0; i < 8; i++) {
                 boolean bit = ((b   >> (7-i) & 1) == 1);
                 boolean c15 = ((crc >> 15    & 1) == 1);
                 crc <<= 1;
                 if (c15 ^ bit) crc ^= polynomial;
              }
         }

         crc &= 0xffff;
         return crc;
    }
    
	// For debugging
	public void printPacket(int bytes, byte[] buffer, String info) {
		String text = "";
		for(int i = 0; i < bytes; i++){
			text += i+": "+buffer[i]+" |";
		}
		
		if(D)Log.d(TAG, info+" "+text);
	}

}
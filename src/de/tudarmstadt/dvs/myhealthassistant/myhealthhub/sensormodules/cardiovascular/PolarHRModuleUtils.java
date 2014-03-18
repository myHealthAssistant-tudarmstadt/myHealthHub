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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.cardiovascular;

import android.util.Log;

/**
 * /**
 * An implementation of a Sensor MessageParser for Polar Wearlink Bluetooth HRM.
 *
 *  Polar Bluetooth Wearlink packet example;
 *   Hdr Len Chk Seq Status HeartRate RRInterval_16-bits
 *    FE  08  F7  06   F1      48          03 64
 *   where; 
 *      Hdr always = 254 (0xFE), 
 *      Chk = 255 - Len
 *      Seq range 0 to 15
 *      Status = Upper nibble may be battery voltage
 *               bit 0 is Beat Detection flag.
 *               
 *  Additional packet examples;
 *    FE 08 F7 06 F1 48 03 64           
 *    FE 0A F5 06 F1 48 03 64 03 70
 *    
 * 
 * @author Christian Seeger
 * 
 */
public class PolarHRModuleUtils {

	// for debugging
	private static boolean D = false;
	private static String TAG = "PolarHRModuleUtils";

	public final int INVALID_HEART_RATE = -1;
	
	private byte[] packetBuffer;
	private int packetBufferPointer; 
	
	private final int MAX_PACKET_SIZE = 16; // for Polar
	
	/**
	 * 
	 */
	public PolarHRModuleUtils() {
		
		packetBuffer = new byte[MAX_PACKET_SIZE];
	}

	public int getHeartRate(byte[] packet, int length) {
		//if(D)printPacketInHex(length, packet, "Incoming Polar HR packet: ");
		
		int heartRate = INVALID_HEART_RATE;

		if(packetValid(packet, 0)) {
			heartRate = packet[5] & 0xFF;
			if (D)
				Log.d(TAG, "Heart rate found: " + heartRate);
		}

		return heartRate;
	}
	
	public int incomingPacket(byte[] packet, int length) {
		if(D) printPacketInHex(length, packet, "Incoming packet: ");
				
		int heartRate = INVALID_HEART_RATE;
		
		// if buffer is empty
		if(packetBufferPointer==0) {
			// check for valid packet
			if(packetValid(packet, 0)) {
				//if(D) printPacketInHex(length, packet, "Process single packet: ");
				heartRate = packet[5] & 0xFF;
			// add to packet buffer
			} else {
				System.arraycopy(packet, 0, packetBuffer, packetBufferPointer, length);
				packetBufferPointer = length;
			}
		} else {

			//TODO make it better
			// avoid buffer indexOutOfBound Exception
			if(packetBufferPointer+length>MAX_PACKET_SIZE) {
				if(D) Log.d(TAG, "avoid indexOutOfBoundException");
				// empty buffer
				packetBufferPointer = 0;
				packetBuffer = new byte[MAX_PACKET_SIZE];
				return INVALID_HEART_RATE;
			}
			
			// Concatenate packet to buffer
			System.arraycopy(packet, 0, packetBuffer, packetBufferPointer, length);
			packetBufferPointer += length;
			
			// check for valid packet
			if(packetValid(packetBuffer, 0)) {
				//if(D) printPacketInHex(packetBuffer.length, packetBuffer, "Process concat packet: ");
				heartRate = packetBuffer[5] & 0xFF;
				
				// empty buffer
				packetBufferPointer = 0;
				packetBuffer = new byte[MAX_PACKET_SIZE];
			}
		}		
		
		return heartRate;
	}

	/**
	 * Applies Polar packet validation rules to buffer. Polar packets are
	 * checked for following; offset 0 = header byte, 254 (0xFE). offset 1 =
	 * packet length byte, 8, 10, 12, 14. offset 2 = check byte, 255 - packet
	 * length. offset 3 = sequence byte, range from 0 to 15.
	 * 
	 * @param an
	 *            array of bytes to parse
	 * @param buffer
	 *            offset to beginning of packet.
	 * @return whether buffer has a valid packet at offset i
	 */
	private boolean packetValid(byte[] buffer, int i) {
		if(buffer.length-i >= 6 ) {
			boolean headerValid = (buffer[i] & 0xFF) == 0xFE;
			boolean checkbyteValid = (buffer[i + 2] & 0xFF) == (0xFF - (buffer[i + 1] & 0xFF));
			boolean sequenceValid = (buffer[i + 3] & 0xFF) < 16;
			return headerValid && checkbyteValid && sequenceValid;		
		} else {
			return false;
		}

		
	}
	
	  /**
	   * Searches buffer for the beginning of a valid packet.
	   *     
	   * @param an array of bytes to parse
	   * @return index to beginning of good packet, or -1 if none found.
	   */
	   public int findNextAlignment(byte[] buffer) {
	    // Minimum length Polar packets is 8, so stop search 8 bytes before buffer ends.
	    for (int i = 0; i < buffer.length - 8; i++) {
	      if (packetValid(buffer,i)) {
		    return i;
	      }
	    }
	    return -1;
	  }
	
	
	public void printPacketInHex(int bytes, byte[] buffer, String info) {
		String text = "";
		for(int i = 0; i < bytes; i++){
			text += i+": "+java.lang.Integer.toHexString(buffer[i]&0xff)+" |";
		}
		if(D)Log.d(TAG, info+" "+text);
	}
	
	public void printPacket(int bytes, byte[] buffer, String info) {
		String text = "";
		for(int i = 0; i < bytes; i++){
			text += i+": "+buffer[i]+" |";
		}
		Log.d(TAG, info+" "+text);
	}
}
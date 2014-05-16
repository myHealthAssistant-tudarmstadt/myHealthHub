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

import java.util.UUID;

import android.content.Context;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.AbstractBluetoothSensorModule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

/**
 * @author Christian Seeger
 *
 */
public class PolarHRModule extends AbstractBluetoothSensorModule {

    private static String MY_TAG = "PolarHRModule";
    
    private static boolean D = true;
	
    // Name for the SDP record when creating server socket
    private static String SENSOR_BLUETOOTH_NAME = "PolarHRModule";
    
    // Unique UUID for this application. 
    private static UUID SENSOR_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Polar HR utils
	private PolarHRModuleUtils polarUtils;
	
	/**
	 * @param context
	 * @param sensor
	 * @param isActiveModule
	 */
	public PolarHRModule(Context context, AbstractSensorType sensor) {
		super(context, sensor, true, SENSOR_UUID, SENSOR_BLUETOOTH_NAME, MY_TAG);
		
		myEventUtils = new EventUtils(
				mySensor.getSensorReadingType(0), mySensor.getSensorID());
		
		polarUtils = new PolarHRModuleUtils();
		
		if(D)Log.d(TAG,"New sensor created with ID: "+mySensor.getSensorID()+
				" and sensor reading type: "+mySensor.getSensorReadingType(0));
	}

	@Override
	protected void deliverPacket(byte[] packet, int length) {
		// Since we use RFCOMM we need to re-build the packet if segmented.
		
		int heartRate = polarUtils.incomingPacket(packet, length);
		
		// upon valid heart rate send corresponding event 
		if(heartRate!=polarUtils.INVALID_HEART_RATE) {
			// Create HR Event
			HeartRateEvent heartRateEvent = new HeartRateEvent(
					myEventUtils.getEventID(), 
					myEventUtils.getTimestamp(),
					mySensor.getSensorID(),
					mySensor.getSensorType(),
					myEventUtils.getTimestamp(), heartRate);
			
			// Send heart rate
			sendSensorReading(heartRateEvent);
		}
	}
	
	// Debugging
	public void printPacketInHex(int bytes, byte[] buffer, String info) {
		String text = "";
		for(int i = 0; i < bytes; i++){
			text += i+": "+java.lang.Integer.toHexString(buffer[i]&0xff)+" |";
		}
		if(D)Log.d(TAG, info+" "+text);
	}

}
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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.physical;

import java.util.UUID;

import android.content.Context;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventKnee;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.AbstractBluetoothSensorModule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

public class DebugSensorModule extends AbstractBluetoothSensorModule {
    private static String MY_TAG = "DebugSensorModule";
    
    private static boolean D = true;
	
	public static final UUID MY_UUID_ACC = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	public DebugSensorModule(Context context, AbstractSensorType sensor) {
		super(context, sensor, true, MY_UUID_ACC, "AccSensor", MY_TAG);
		myEventUtils = new EventUtils(
				mySensor.getSensorReadingType(0), mySensor.getSensorID());
		
		if(D)Log.d(TAG,"New sensor created with ID: "+mySensor.getSensorID()+
				" and sensor reading type: "+mySensor.getSensorReadingType(0));
	}
	
	@Override
	protected void deliverPacket(byte[] packet, int bytes) {
	
		// print packet
		int[] values = readUnsignedByte(packet, bytes);
		String output = "";
		for(int i : values) {
			output += i+"; ";
		}
		Log.d(TAG, output);
		
		// for creating a sensor event:
		/*AccSensorEvent myAccReading = transferToAccEvent(bytes, packet);

		if(myAccReading!=null) {
			Intent i = new Intent();
			i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, myAccReading.getEventType());
			i.putExtra(Event.PARCELABLE_EXTRA_EVENT, myAccReading);
			i.setAction(AbstractChannel.RECEIVER);
			context.sendBroadcast(i);
		}*/
	}

	private AccSensorEvent transferToAccEvent(int bytes, byte[] b) {
		if (bytes == 6) {
			AccSensorEvent accEvent = new AccSensorEventKnee(
					myEventUtils.getEventID(),
					myEventUtils.getTimestamp(), 
					mySensor.getSensorID(), 
					mySensor.getSensorType(), 
					myEventUtils.getTimestamp());
			int[] myData = readUnsignedByte(b, bytes); 
			accEvent.x_mean = myData[0];
			accEvent.y_mean = myData[1];
			accEvent.z_mean = myData[2];
			accEvent.x_var = myData[3];
			accEvent.y_var = myData[4];
			accEvent.z_var = myData[5];
			return accEvent;
		} else {
			Log.e(TAG, "transferToAccEvent(): Wrong packet size");
			return null;
		}
	}

	/**
	 * @param b
	 *            is the byte to convert
	 * @return a integer from the given byte
	 */
	protected int readUnsignedByte(byte b) {
		return (b & 0xff);
	}

	/**
	 * @param b
	 *            is the byte array to convert
	 * @return a integer array from the given byte
	 */
	protected int[] readUnsignedByte(byte[] b, int numOfBytes) {
		int[] result = new int[numOfBytes];
		for (int i = 0; i < numOfBytes; i++) {
			result[i] = readUnsignedByte(b[i]);
		}
		return result;
	}


}
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

import android.content.Context;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccPeakSensorEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventAnkle;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventKnee;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventWrist;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

public class CountAccHedgeHogSensorModule extends AccSensorModule {

	private final static String TAG = "CountAccHedgeHogSensorModule";
	private final static boolean D = true;
	private String eventType;

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public CountAccHedgeHogSensorModule(Context context, 
			AbstractSensorType sensor) {
		super(context, sensor);

		
		myEventUtils = new EventUtils(mySensor.getSensorReadingType(0),
				mySensor.getSensorID());
		this.eventType = mySensor.getSensorReadingType(0);
		
		if (D) Log.d(TAG,"New sensor created with ID: " + mySensor.getSensorID()
				+" and sensor reading type: "+  mySensor.getSensorReadingType(0));
	}

	@Override
	protected void deliverPacket(byte[] packet, int bytes) {
		AccPeakSensorEvent myAccReading = transferToAccCountEvent(bytes, packet);
		if(myAccReading!=null) sendSensorReading(myAccReading);
	}

	private AccPeakSensorEvent transferToAccCountEvent(int bytes, byte[] b) {
		// Jenny: hier 6
		if (bytes == 9) {
		
			AccPeakSensorEvent countEvent;
			
			if(eventType.equals(AccSensorEventAnkle.EVENT_TYPE)) {
				countEvent = new AccSensorEventAnkle(
						myEventUtils.getEventID(),
						myEventUtils.getTimestamp(), 
						mySensor.getSensorID(), 
						mySensor.getSensorType(), 
						myEventUtils.getTimestamp());
			} else if(eventType.equals(AccSensorEventWrist.EVENT_TYPE)) {
				countEvent = new AccSensorEventWrist(
						myEventUtils.getEventID(),
						myEventUtils.getTimestamp(), 
						mySensor.getSensorID(), 
						mySensor.getSensorType(), 
						myEventUtils.getTimestamp());
			}  else {
				return null;
			}
	
			int[] myData = convertToInt(b, bytes);
			countEvent.x_mean = myData[0];
			countEvent.y_mean = myData[1];
			countEvent.z_mean = myData[2];
			countEvent.x_var = myData[3];
			countEvent.y_var = myData[4];
			countEvent.z_var = myData[5];

			// Jenny: das hier ausklammmern
			countEvent.x_peak = (myData[6]==1) ? true : false;
			countEvent.y_peak = (myData[7]==1) ? true : false;
			countEvent.z_peak = (myData[8]==1) ? true : false;
			// ---
			
			return countEvent;
		} else {
			Log.e(TAG, "transferToAccCountEvent(): Wrong packet size of "+bytes);
			return null;
		}
	}

	/**
	 * Reads byte array delivered from a HedgeHog acceleration sensor. 
	 * 
	 * @param b
	 * @return Integer array
	 */
	private int[] convertToInt(byte[] b, int numOfBytes)  {
		int[] result = new int[numOfBytes];

		for (int i = 0; i < numOfBytes; i++) {
					result[i] = readUnsignedByte(b[i]);
		}
		
		return result;
	}


	/**
	 * @param b
	 *            is the byte array to convert
	 * @return a integer array from the given byte
	 */
	/*private int[] readUnsignedByte(byte[] b) {
		int[] result = new int[b.length];
		for (int i = 0; i < b.length; i++) {
			result[i] = readUnsignedByte(b[i]);
		}
		return result;
	}*/

	/*private int readNegativeByte(byte b) {
	int result = 0;

	return result = -(b & 127);

	}*/

}
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
import android.content.Intent;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.CountAccSensorEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

public class CountAccSensorModule extends AccSensorModule {

	private final static String TAG = "CountAccSensorModul";
	private final static boolean D = true;

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public CountAccSensorModule(Context context, 
			AbstractSensorType sensor) {
		super(context, sensor);

		myEventUtils = new EventUtils(mySensor.getSensorReadingType(0),
				mySensor.getSensorID());

		if (D)
			Log.d(TAG,
					"New sensor created with ID: " + mySensor.getSensorID()
							+ " and sensor reading type: "
							+ mySensor.getSensorReadingType(0));
	}

	@Override
	protected void deliverPacket(byte[] packet, int bytes) {
		CountAccSensorEvent myAccReading = transferToAccCountEvent(bytes, packet);

		Intent i = new Intent();
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, myAccReading.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, myAccReading);
		i.setAction(AbstractChannel.RECEIVER);
		context.sendBroadcast(i);
	}

	private CountAccSensorEvent transferToAccCountEvent(int bytes, byte[] b) {
		if (bytes == 12) {
/*
			CountAccSensorEvent countEvent = new CountAccSensorEvent(
					new EventUtils("acc", "porc").getEventID("acc", "porc 3d",
							Calendar.getInstance().getTime().toGMTString()),
					Calendar.getInstance().getTime().toGMTString(), "porc 3D",
					"Kristofs Stachelschwein", Calendar.getInstance().getTime()
							.toGMTString());
*/
			
			CountAccSensorEvent countEvent = new CountAccSensorEvent(
					myEventUtils.getEventID(),
					myEventUtils.getTimestamp(), 
					mySensor.getSensorID(), 
					mySensor.getSensorType(), 
					myEventUtils.getTimestamp());
			
			int[] myData = readWithPeaks(b, bytes);
			countEvent.x_mean = myData[0];
			countEvent.y_mean = myData[1];
			countEvent.z_mean = myData[2];
			countEvent.x_var = myData[3];
			countEvent.y_var = myData[4];
			countEvent.z_var = myData[5];

			countEvent.x_peak_neg = myData[6];
			countEvent.x_peak_pos = myData[7];
			countEvent.y_peak_neg = myData[8];
			countEvent.y_peak_pos = myData[9];
			countEvent.z_peak_neg = myData[10];
			countEvent.z_peak_pos = myData[11];

			return countEvent;
		} else {
			Log.e(TAG, "transferToAccCountEvent(): Wrong packet size");
			return null;
		}
	}

	/**
	 * Reads byte array delivered from a porc acceleration sensor. Bytes at
	 * position 6,8,10 are signed bytes, all others are unsigned.
	 * 
	 * @param b
	 * @return Integer array
	 */
	private int[] readWithPeaks(byte[] b, int numOfBytes)  {				//changed by S.Niederhöfer (numOfBytes) to use it as real length
		int[] result = new int[12];

		for (int i = 0; i < numOfBytes/**b.length**/; i++) {
			/* values at position 6,8,10 are negative */
			if (i == 6 || i == 8 || i == 10) {
				result[i] = b[i];
			} else {
				/* reads byte as an unsigned values */
				result[i] = readUnsignedByte(b[i]);
			}
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
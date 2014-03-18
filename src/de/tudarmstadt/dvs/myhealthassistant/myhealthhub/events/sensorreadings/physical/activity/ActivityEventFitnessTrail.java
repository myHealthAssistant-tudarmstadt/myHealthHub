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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.activity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;

public class ActivityEventFitnessTrail extends AbstractActivityEvent{

	public static String EVENT_TYPE = SensorReadingEvent.ACTIVITY_FITNESSTRAIL;
	
	private static String TAG = "ActivityEventReha";
	private static boolean D = true;
	private byte toCount; 
	private byte isCountable;
	
	/**
	 * 
	 * @param eventType
	 * @param eventID
	 * @param timestamp
	 * @param producerID
	 * @param sensorType
	 * @param timeOfMeasurement
	 */
	public ActivityEventFitnessTrail(String eventID,
			String timestamp, String producerID, String sensorType,
			String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
		if (D)Log.d(TAG, "Konstruktor 1");
		// TODO Auto-generated constructor stub
	}

	public ActivityEventFitnessTrail(Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString());
		if (D)Log.d(TAG, "Konstruktor 2");
		readfromParcel(source);
	}

	private void readfromParcel(Parcel source) {
		if (D)Log.d(TAG, "Read from Parcel Begin ");
		setActivityNumber(source.readInt());
		setActivityName(source.readString());
		setDistance(source.readFloat());
		setSumDistances(source.readFloat());
		if (D)Log.d(TAG, "Read from Parcel before To Count ");
		setToCount(source.readByte());//setToCount((Boolean) source.readValue(boolean.class.getClassLoader()));
		setIsCountable(source.readByte());
		if (D)Log.d(TAG, "Read from Parcel End ");
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public static final Parcelable.Creator<ActivityEventFitnessTrail> CREATOR = new Parcelable.Creator<ActivityEventFitnessTrail>() {

		@Override
		public ActivityEventFitnessTrail createFromParcel(Parcel source) {
			if (D)Log.d(TAG, "create from Parcal  ");
			return new ActivityEventFitnessTrail(source);
		}

		@Override
		public ActivityEventFitnessTrail[] newArray(int size) {
			return new ActivityEventFitnessTrail[size];
		}
	};
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.d(TAG, "Write To Parcel Begin ");
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(sensorType);
		dest.writeString(timeOfMeasurement);
		dest.writeInt(getActivityNumber());
		dest.writeString(getActivityName());
		dest.writeFloat(getDistance());
		dest.writeFloat(getSumDistances());
		dest.writeByte(getToCount());//dest.writeValue(getToCount());
		dest.writeByte(getIsCountable());
		Log.d(TAG, "Write To Parcel End ");
		
	}
	
	
	/* 
	 * SET AND GET METHODS 
	 */
	
	/**
	 * 
	 * @param toCount
	 */
	public void  setToCount (byte toCount){
		this.toCount = toCount;
	}
	
	
	/**
	 * 
	 * @return 	true, if there are countable peaks 
	 * 			else false
	 */
	public byte getToCount(){
		return this.toCount;
	}
	
	/**
	 * 
	 * @param toCount
	 */
	public void  setIsCountable (byte isCountable){
		this.isCountable = isCountable;
	}
	
	
	/**
	 * 
	 * @return 	true, if there are countable peaks 
	 * 			else false
	 */
	public byte getIsCountable(){
		return this.isCountable;
	}
}
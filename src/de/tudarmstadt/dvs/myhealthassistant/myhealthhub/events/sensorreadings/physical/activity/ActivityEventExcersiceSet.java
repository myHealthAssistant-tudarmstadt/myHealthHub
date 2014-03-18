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


public class ActivityEventExcersiceSet extends SensorReadingEvent{




	private static String EVENT_TYPE = SensorReadingEvent.EXERCISE_SET;
	
	private static String TAG = "ActivityEventExcersiceSet";
	private static boolean D = true;
	
	
	protected String 	exerciseName; 
	protected String 	exerciseUnit;
	protected int		exerciseCount;

	/**
	 * 
	 * @param eventID
	 * @param timestamp
	 * @param producerID
	 * @param sensorType
	 * @param timeOfMeasurement
	 */
	public ActivityEventExcersiceSet(String eventID,
			String timestamp, String producerID, String sensorType,
			String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
		if (D)Log.d(TAG, "Konstruktor 1");
	}

	public ActivityEventExcersiceSet(Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString());
		if (D)Log.d(TAG, "Konstruktor 2");
		readfromParcel(source);
	}

	private void readfromParcel(Parcel source) {
		if (D)Log.d(TAG, "Read from Parcel Begin ");
		setExerciseName(source.readString());
		setExerciseUnit(source.readString());
		setExerciseCount(source.readInt());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public static final Parcelable.Creator<ActivityEventExcersiceSet> CREATOR = new Parcelable.Creator<ActivityEventExcersiceSet>() {

		@Override
		public ActivityEventExcersiceSet createFromParcel(Parcel source) {
			if (D)Log.d(TAG, "create from Parcal  ");
			return new ActivityEventExcersiceSet(source);
		}

		@Override
		public ActivityEventExcersiceSet[] newArray(int size) {
			return new ActivityEventExcersiceSet[size];
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
		dest.writeString(getExerciseName());
		dest.writeString(getExerciseUnit());
		dest.writeInt(getExerciseCount());
		Log.d(TAG, "Write To Parcel End ");
		
	}

	/**
	 * @return the exerciseName
	 */
	public String getExerciseName() {
		return exerciseName;
	}

	/**
	 * @param exerciseName the exerciseName to set
	 */
	public void setExerciseName(String exerciseName) {
		this.exerciseName = exerciseName;
	}

	/**
	 * @return the exerciseUnit
	 */
	public String getExerciseUnit() {
		return exerciseUnit;
	}

	/**
	 * @param exerciseUnit the exerciseUnit to set
	 */
	public void setExerciseUnit(String exerciseUnit) {
		this.exerciseUnit = exerciseUnit;
	}

	/**
	 * @return the exerciseCount
	 */
	public int getExerciseCount() {
		return exerciseCount;
	}

	/**
	 * @param exerciseCount the exerciseCount to set
	 */
	public void setExerciseCount(int exerciseCount) {
		this.exerciseCount = exerciseCount;
	}
	
	
	
}
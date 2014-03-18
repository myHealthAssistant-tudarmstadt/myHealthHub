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
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;

public class ActivityEventReha extends AbstractActivityEvent {

	public static String EVENT_TYPE = SensorReadingEvent.ACTIVITY_REHA;
	
	public static String ACTIVITY_WALKING_SLOW = "slow walking";
	public static String ACTIVITY_WALKING = "walking";
	public static String ACTIVITY_RUNNING = "running";
	public static String ACTIVITY_SITTING = "sitting";
	public static String ACTIVITY_STANDING = "standing";
	public static String ACTIVITY_CYCLING = "cycling";
	
	public static String ACTIVITY_UNKNOWN = "unknown";
	

	/**
	 * Event for reha activities.
	 * @param eventType
	 * @param eventID
	 * @param timestamp
	 * @param producerID
	 * @param sensorType
	 * @param timeOfMeasurement
	 */
	public ActivityEventReha(String eventID,
			String timestamp, String producerID, String sensorType,
			String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
	}

	public ActivityEventReha(Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString());
		readfromParcel(source);
	}

	private void readfromParcel(Parcel source) {
		setActivityNumber(source.readInt());
		setActivityName(source.readString());
		setDistance(source.readFloat());
		setSumDistances(source.readFloat());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public static final Parcelable.Creator<ActivityEventReha> CREATOR = new Parcelable.Creator<ActivityEventReha>() {

		@Override
		public ActivityEventReha createFromParcel(Parcel source) {
			return new ActivityEventReha(source);
		}

		@Override
		public ActivityEventReha[] newArray(int size) {
			return new ActivityEventReha[size];
		}
	};
	@Override
	public void writeToParcel(Parcel dest, int flags) {
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
	}
}
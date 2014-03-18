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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.activity;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Christian Seeger
 * 
 */
public class ActivityEventSSWRC extends SensorReadingEvent {
	public static String EVENT_TYPE = SensorReadingEvent.ACTIVITY;

	public static String STANDING_NAME = "standing";
	public static String WALKING_NAME = "walking";
	public static String RUNNING_NAME = "running";
	public static String SITTING_NAME = "sitting";
	public static String CYCLING_NAME = "cycling";
	public static int STANDING = 1;
	public static int WALKING = 2;
	public static int RUNNING = 3;
	public static int SITTING = 4;
	public static int CYCLING = 5;
	
	public int activityNumber;
	public String activityName;
	public float distance;
	public float sum_distances;

	/**
	 * Creates an ACTIVITY event.
	 * 
	 * @param eventID
	 *            Event ID.
	 * @param timestamp
	 *            Timestamp of event.
	 * @param producerID
	 *            ID of event producer.
	 * @param sensorType
	 *            Sensor type of event producer.
	 * @param timeOfMeasurement
	 *            Time of measurement.
	 */
	public ActivityEventSSWRC(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {

		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);

	}

	/**
	 * Sets the detected user ACTIVITY.
	 * 
	 * @param activityNumber
	 *            Number of user ACTIVITY.
	 * @param distance
	 *            Distance to Gaussian distribution.
	 * @param sum_distances
	 *            Sum of all distances.
	 */
	public void setActivity(int activityNumber, String activityName,
			float distance, float sum_distances) {
		this.activityNumber = activityNumber;
		this.activityName = activityName;
		this.distance = distance;
		this.sum_distances = sum_distances;
	}

	/**
	 * Return the detected user ACTIVITY number
	 * 
	 * @return User ACTIVITY
	 */
	public int getActivityNumber() {
		return activityNumber;
	}

	/**
	 * Returns the detected user ACTIVITY
	 * 
	 * @return user ACTIVITY
	 */
	public String getActivityName() {
		return activityName;
	}

	public float getRecognitionDistance() {
		return distance;
	}

	public float getSumOfRecognitionDistances() {
		return sum_distances;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ActivityEventSSWRC> CREATOR = new Parcelable.Creator<ActivityEventSSWRC>() {

		@Override
		public ActivityEventSSWRC createFromParcel(Parcel source) {
			return new ActivityEventSSWRC(source);
		}

		@Override
		public ActivityEventSSWRC[] newArray(int size) {
			return new ActivityEventSSWRC[size];
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
		dest.writeInt(activityNumber);
		dest.writeString(activityName);
		dest.writeFloat(distance);
		dest.writeFloat(sum_distances);
	}

	private ActivityEventSSWRC(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		activityNumber = source.readInt();
		activityName = source.readString();
		distance = source.readFloat();
		sum_distances = source.readFloat();
	}
}
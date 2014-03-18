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

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import android.os.Parcel;
import android.os.Parcelable;

public class ActivityEventGymWorkout extends ActivityEventSSWRC {

	public static String EVENT_TYPE = SensorReadingEvent.ACTIVITY_GYM_WORKOUT;

	public int repetitionCount;
	public int debugRepetitionCountNormal;
	public int debugRepetitionCountFast;

	/**
	 * Creates a gym ACTIVITY event.
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
	public ActivityEventGymWorkout(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(eventID, timestamp, producerID, sensorType, timeOfMeasurement);

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
	 * @param repetitionCount
	 *            Number of repetitions.
	 */
	public void setActivity(int activityNumber, float distance,
			float sum_distances, int repetitionCount) {
		this.activityNumber = activityNumber;
		this.distance = distance;
		this.sum_distances = sum_distances;
		this.repetitionCount = repetitionCount;
	}

	/**
	 * Return the detected user ACTIVITY number
	 * 
	 * @return User ACTIVITY
	 */
	public int getActivity() {
		return activityNumber;
	}

	/**
	 * Returns number of counted repetition
	 * 
	 * @return Number of repetitions
	 */
	public int getRepetitionCount() {
		return repetitionCount;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ActivityEventGymWorkout> CREATOR = new Parcelable.Creator<ActivityEventGymWorkout>() {

		@Override
		public ActivityEventGymWorkout createFromParcel(Parcel source) {
			return new ActivityEventGymWorkout(source);
		}

		@Override
		public ActivityEventGymWorkout[] newArray(int size) {
			return new ActivityEventGymWorkout[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(sensorType);
		dest.writeString(timeOfMeasurement);
		dest.writeInt(activityNumber);
		dest.writeString(activityName);
		dest.writeFloat(distance);
		dest.writeFloat(sum_distances);
		dest.writeInt(repetitionCount);
		dest.writeInt(debugRepetitionCountFast);
		dest.writeInt(debugRepetitionCountNormal);
	}

	private ActivityEventGymWorkout(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		activityNumber = source.readInt();
		activityName = source.readString();
		distance = source.readFloat();
		sum_distances = source.readFloat();
		repetitionCount = source.readInt();
		debugRepetitionCountFast = source.readInt();
		debugRepetitionCountNormal = source.readInt();
	}
}
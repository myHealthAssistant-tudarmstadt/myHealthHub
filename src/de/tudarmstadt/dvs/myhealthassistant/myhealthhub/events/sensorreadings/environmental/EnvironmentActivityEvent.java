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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Christian Seeger
 * 
 */
public class EnvironmentActivityEvent extends AbstractEnvironmentalEvent {

	public static String EVENT_TYPE = AbstractEnvironmentalEvent.ENVIRONMENT_ACTIVITY;

	public final static String SHOWERING = "showering";
	public final static String ENTERING = "entering";
	public final static String LEAVING = "leaving";
	public final static String EATING = "eating";
	public final static String TEETH_BRUSHING = "tooth_brushing";
	public final static String AIRING = "airing";

	private String activity;
	private int duration;
	private String relatedEventIDs;

	/**
	 * Constructor of a environmental sensor reading event.
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
	 * @param location
	 *            Location of sensor.
	 * @param object
	 *            Specific opject to which sensor is attached/integrated.
	 * @param ACTIVITY
	 *            Recognized ACTIVITY.
	 * @param duration
	 *            Duration of ACTIVITY.
	 */
	public EnvironmentActivityEvent(String eventID, String timestamp,
			String producerID, String sensorType, String timeOfMeasurement,
			String location, String object, String activity, int duration) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement,
				location, object);

		this.activity = activity;
		this.duration = duration;
	}

	/**
	 * Constructor of a environmental sensor reading event.
	 * 
	 * @param eventID
	 *            Event ID.
	 * @param timestamp
	 *            Timestamp of event.
	 * @param producerID
	 *            Producer.
	 * @param event
	 *            AbstractEnvironmentalEvent
	 * @param ACTIVITY
	 *            Recognized ACTIVITY.
	 * @param duration
	 *            Duration of ACTIVITY.
	 */
	public EnvironmentActivityEvent(String eventID, String timestamp,
			String producerID, AbstractEnvironmentalEvent event,
			String activity, int duration) {
		super(EVENT_TYPE, eventID, timestamp, producerID, event.getEventType(), event
				.getTimeOfMeasurement(), event.getLocation(), event.getObject());

		this.activity = activity;
		this.duration = duration;
	}

	/**
	 * Constructor of a environmental sensor reading event.
	 * 
	 * @param eventID
	 *            Event ID.
	 * @param timestamp
	 *            Timestamp of event.
	 * @param producerID
	 *            Producer.
	 * @param event
	 *            AbstractEnvironmentalEvent
	 * @param ACTIVITY
	 *            Recognized ACTIVITY.
	 * @param duration
	 *            Duration of ACTIVITY.
	 * @param relatedEventIDs
	 *            List of events used for deriving ACTIVITY
	 */
	public EnvironmentActivityEvent(String eventID, String timestamp,
			String producerID, AbstractEnvironmentalEvent event,
			String activity, int duration, String relatedEventIDs) {
		super(EVENT_TYPE, eventID, timestamp, producerID, event.getEventType(), event
				.getTimeOfMeasurement(), event.getLocation(), event.getObject());

		this.activity = activity;
		this.duration = duration;
		this.relatedEventIDs = relatedEventIDs;
	}

	@Override
	public String getValue() {
		return activity;
	}

	public String getActivity() {
		return activity;
	}

	public int getDuration() {
		return duration;
	}

	public String getRelatedEventIDs() {
		return relatedEventIDs;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<EnvironmentActivityEvent> CREATOR = new Parcelable.Creator<EnvironmentActivityEvent>() {

		@Override
		public EnvironmentActivityEvent createFromParcel(Parcel source) {
			return new EnvironmentActivityEvent(source);
		}

		@Override
		public EnvironmentActivityEvent[] newArray(int size) {
			return new EnvironmentActivityEvent[size];
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
		dest.writeString(location);
		dest.writeString(object);
		dest.writeString(activity);
		dest.writeInt(duration);
	}

	private EnvironmentActivityEvent(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString(), source.readString(),
				source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		activity = source.readString();
		duration = source.readInt();
	}

}
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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Event transformation response consisting of the request's eventID and <code>true</code> if
 * a transformation was found, otherwise <code>false</code>. Since requesting a transformation
 * can take a while, request and response are asynchronous. 
 * @author Christian Seeger
 *
 */
public class EventTransformationResponse extends Event {

	public static String EVENT_TYPE = LocalManagementEvent.EVENT_TRANSFORMATION_RESPONSE;
	
	private boolean transformationFound;
	private String requestEventID;
		
	/**
	 * Event transformation response consisting of the request's eventID and <code>true</code> if
	 * a transformation was found, otherwise <code>false</code>. Since requesting a transformation
	 * can take a while, request and response are asynchronous. 
	 * @param eventID
	 * @param timestamp
	 * @param producerID
	 * @param requestEventID
	 * @param success <code>true</code> if a transformation was found, otherwise: <code>false</code>.
	 */
	public EventTransformationResponse(String eventID,
			String timestamp, String producerID, String requestEventID, boolean success) {
		super(EVENT_TYPE, eventID, timestamp, producerID);
		this.requestEventID = requestEventID;
		this.transformationFound = success;
	}
	
		
	/**
	 * @return the transformationFound
	 */
	public boolean isTransformationFound() {
		return transformationFound;
	}


	/**
	 * @return the requestEventID
	 */
	public String getRequestEventID() {
		return requestEventID;
	}


	
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<EventTransformationResponse> CREATOR = new Parcelable.Creator<EventTransformationResponse>() {

		
		public EventTransformationResponse createFromParcel(Parcel source) {
			return new EventTransformationResponse(source);
		}

		
		public EventTransformationResponse[] newArray(int size) {
			return new EventTransformationResponse[size];
		}
	};

	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(requestEventID);
		dest.writeInt(transformationFound ? 1 : 0); 
	}

	private EventTransformationResponse(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		requestEventID = source.readString();
		transformationFound = source.readInt()==1 ? true : false;
	}
}
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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.notifications;

import android.os.Parcel;
import android.os.Parcelable;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;

/**
 * @author Christian Seeger
 * 
 */
public class NotificationEvent extends Event {

	public static String NOTIFICATION_EVENT = EVENT_ROOT + ".Notification";

	public static String EVENT_TYPE = NOTIFICATION_EVENT;

	public static final int SEVERITY_INFORMATION = 0;
	public static final int SEVERITY_WARNING = 1;
	public static final int SEVERITY_CRITICAL = 2;
	public static final int SEVERITY_DEBUG = 3;

	public String subject;
	public String text;
	public int severity;

	public NotificationEvent(String eventID,
			String timestamp, String producerID, int severity, String subject,
			String text) {
		super(EVENT_TYPE, eventID, timestamp, producerID);

		this.severity = severity;
		this.subject = subject;
		this.text = text;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<NotificationEvent> CREATOR = new Parcelable.Creator<NotificationEvent>() {

		@Override
		public NotificationEvent createFromParcel(Parcel source) {
			return new NotificationEvent(source);
		}

		@Override
		public NotificationEvent[] newArray(int size) {
			return new NotificationEvent[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(text);
		dest.writeString(subject);
		dest.writeInt(severity);
	}

	private NotificationEvent(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(),
				source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		text = source.readString();
		subject = source.readString();
		severity = source.readInt();
	}
}
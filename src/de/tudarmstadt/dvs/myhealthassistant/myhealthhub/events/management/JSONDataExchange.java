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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author HIEU HA
 *
 */
public class JSONDataExchange extends ManagementEvent {

	public static String EVENT_TYPE = JSON_DATA_EXCHANGE;
	public static final String JSON_REQUEST = "request";
	public static final String JSON_GET = "get";
	public static final String JSON_STORE = "store";
	public static final String JSON_EDIT = "_edit";
	public static final String JSON_DEL = "_dele";
	public static final String JSON_CONTENT_ID = "_ID";
	public static final String JSON_CONTENT_ARRAY = "jArray";
	public static final String JSON_DATE = "date";
	public static final String JSON_CONTENTS = "contents";
	public static final String JSON_EXTRA = "extra";
	
	private String packageName;
	private String dataExchangeEventType;
	private String JSONEncodedData;
	
	public JSONDataExchange(String eventID, String timestamp,
			String producerID, String packageName, String dataExchangeEventType, String JSONEncodedData) {
		super(JSON_DATA_EXCHANGE, eventID, timestamp, producerID);
		this.packageName = packageName;
		this.dataExchangeEventType = dataExchangeEventType;
		this.JSONEncodedData = JSONEncodedData;
	}



	public String getPackageName() {
		return packageName;
	}



	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}



	public String getDataExchangeEventType() {
		return dataExchangeEventType;
	}



	public void setDataExchangeEventType(String eventType) {
		this.dataExchangeEventType = eventType;
	}



	public String getJSONEncodedData() {
		return JSONEncodedData;
	}



	public void setJSONEncodedData(String JSONEncodedData) {
		this.JSONEncodedData = JSONEncodedData;
	}



	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<JSONDataExchange> CREATOR = new Parcelable.Creator<JSONDataExchange>() {

		public JSONDataExchange createFromParcel(Parcel source) {
			return new JSONDataExchange(source);
		}

		public JSONDataExchange[] newArray(int size) {
			return new JSONDataExchange[size];
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(packageName);
		dest.writeString(dataExchangeEventType);
		dest.writeString(JSONEncodedData);
	}

	private JSONDataExchange(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		packageName = source.readString();
		dataExchangeEventType = source.readString();
		JSONEncodedData = source.readString();
	}

}
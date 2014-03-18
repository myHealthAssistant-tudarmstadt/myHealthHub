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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement.EventTransformationRequest;

/**
 * Contains all information needed for managing a specific event transformation.
 * @author Christian Seeger
 *
 */
public class Transformation implements Parcelable {

	private String transformationName;
	private long bundleId;
	private List<String> requiredEventTypes;
	private String producedEventType;
	private int transformationCost;

	/**
	 * Constructor.
	 * @param bundleID Felix bundle ID
	 * @param transformationName transformation's name
	 * @param requiredEventTypes event types required for transformation
	 * @param producedEventType produced event type
	 * @param transformationCosts costs for transformation
	 */
	public Transformation(long bundleID, String transformationName,
			List<String> requiredEventTypes, String producedEventType,
			int transformationCosts) {
		this.bundleId = bundleID;
		this.transformationName = transformationName;
		this.requiredEventTypes = requiredEventTypes;
		this.producedEventType = producedEventType;
		this.transformationCost = transformationCosts;
	}
	
	/**
	 * Constructor.
	 * @param bundleID Felix bundle ID
	 * @param transformationName transformation's name
	 * @param producedEventType produced event type
	 * @param transformationCosts costs for transformation
	 */
	public Transformation(long bundleID, String transformationName,
			String producedEventType,
			int transformationCosts) {
		this.bundleId = bundleID;
		this.transformationName = transformationName;
		this.producedEventType = producedEventType;
		this.transformationCost = transformationCosts;
		requiredEventTypes = new ArrayList<String>();
	}
	
	/**
	 * @param bundleId
	 *            the bundleId to set
	 */
	public void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}

	/**
	 * 
	 * @return bundleID transformation's bundle ID
	 */
	public long getBundleId() {
		return bundleId;
	}

	/**
	 * 
	 * @return transformation's name
	 */
	public String getTransformationName() {
		return transformationName;
	}

	/**
	 * Adds a list of event types required for transformation.
	 * @param events the event types required for transformation.
	 */
	public void setRequiredEvents(List<String> events) {
		this.requiredEventTypes = events;
	}

	/**
	 * Adds an event type required for transformation.
	 * @param event an event type 
	 */
	public void addRequiredEvent(String event) {
		requiredEventTypes.add(event);
	}

	/**
	 * @return the producedEventType
	 */
	public String getProducedEventType() {
		return producedEventType;
	}

	/**
	 * @return the requiredEventTypes
	 */
	public List<String> getRequiredEventTypes() {
		return requiredEventTypes;
	}

	public void setProducedEventType(String event) {
		this.producedEventType = event;
	}

	/**
	 * Sets the costs for the transformation.
	 * @param cost between 1 and Integer.MAX_VALUE
	 */
	public void setTransformationCost(int cost) {
		// we don't want costs smaller than 1
		if (cost > 0)
			this.transformationCost = cost;
	}

	/**
	 * Returns the costs for this event transformation.
	 * @return cost (1..Integer.MAX_VALUE)
	 */
	public int getTransformationCost() {
		return transformationCost;
	}

	/**
	 * Checks whether the transformation is applicable. Return -1 if not,
	 * otherwise the cost value.
	 * 
	 * @param availableEventTypes
	 * @param requiredEventType
	 * @return Cost value
	 */
	public int isTransformationApplicable(List<String> availableEventTypes,
			String requiredEventType) {
		// check required event type
		if (!requiredEventType.equals(producedEventType))
			return -1;

		// check whether all required event types are available
		for (String type : requiredEventTypes) {
			if (!availableEventTypes.contains(type))
				return -1;
		}

		return transformationCost;
	}

	/**
	 * Checks whether the transformation is applicable. Return -1 if not,
	 * otherwise the cost value.
	 * 
	 * @param availableEventTypes
	 * @param requiredEventType
	 * @return Cost value
	 */
	public int isTransformationApplicable(EventTransformationRequest req) {
		// TODO make it efficient
		ArrayList<String> list = new ArrayList<String>();
		String[] advertisedEvents = req.getAdvertisedEvents();
		for (int i = 0; i < advertisedEvents.length; i++) {
			list.add(advertisedEvents[i]);
		}

		return isTransformationApplicable(list, req.getEventSubscription());
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Transformation> CREATOR = new Parcelable.Creator<Transformation>() {
		
		public Transformation createFromParcel(Parcel source) {
			return new Transformation(source);
		}
		
		public Transformation[] newArray(int size) {
			return new Transformation[size];
		}
	};


	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(transformationName);
		dest.writeLong(bundleId);
		dest.writeList(requiredEventTypes);
		dest.writeString(producedEventType);
		dest.writeInt(transformationCost);
	}
	
	private Transformation(final Parcel source) {
		transformationName = source.readString();
		bundleId = source.readLong();
		requiredEventTypes = source.createStringArrayList();
		producedEventType = source.readString();
		transformationCost = source.readInt();		
	}
}
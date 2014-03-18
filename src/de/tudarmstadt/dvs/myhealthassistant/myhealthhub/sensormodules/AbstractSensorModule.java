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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;

/**
 * @author Christian Seeger
 *
 */
public abstract class AbstractSensorModule {

	protected String moduleID;
	protected Context context;
	
	public AbstractSensorModule(final String moduleID, Context context) {
		this.moduleID = moduleID;
		this.context = context;
	}
	
	/**
	 * Starts the sensor module. For passive modules, a corresponding
	 * port is opened. For active modules, the module tries to connect
	 * to the sensors.
	 */
	public abstract void start();
	
	/**
	 * Stops the sensor module. It cancels open connections and 
	 * closes open ports.
	 */
	public abstract void stop();
	
    /**
     * Initializes the sensor module by sending an advertisement message.
     */
	public abstract void initializeSensorModule();
	
	/**
	 * Stops the sensor module and sends an un-advertisement message.
	 */
	public abstract void destroySensorModule();
		
	/**
	 * Returns the event type which is produced by this sensor module. 
	 * @return event type 
	 */
	public abstract String getProducingEventType();
	
	/**
	 * Returns true if the module actively establishes a connection to its sensor.
	 * @return true if active module
	 */
	public abstract boolean isActiveModule();
	
	/**
	 * Returns true if module is currently connected to sensor, otherwise false.
	 * @return true if connected.
	 */
	public abstract boolean isConnected();
	
	/**
	 * Returns the module's ID.
	 * @return ID
	 */
	public String getModuleID() {
		return moduleID;
	}
	
	/**
	 * Sends an event to a specific channel using the LocalBroadcastManager
	 * @param Event to send
	 * @param Channel on which the event is sent
	 */
	protected void sendToChannel(Event evt, String channel) {
    	Intent i = new Intent();
    	i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
    	i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
    	i.setAction(channel);
    	LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }
	
	/**
	 * Sends an event to the managment channel.
	 * @param management event
	 */
	protected void sendManagementEvent(Event evt) {
		sendToChannel(evt, AbstractChannel.MANAGEMENT);
	}
	
	/**
	 * Sends an event to the reading channel.
	 * @param sensor reading
	 */
	protected void sendSensorReading(Event evt) {
		sendToChannel(evt, AbstractChannel.RECEIVER);
	}
}
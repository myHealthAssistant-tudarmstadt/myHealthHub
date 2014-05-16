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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDBMS;

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
		if (evt.getEventType().equals(SensorReadingEvent.HEART_RATE)){
			Log.e("AbstractSensor", "this's heartrate event");
			storeHeartRateEvent((HeartRateEvent) evt);
		}
	}
	
	private void storeHeartRateEvent(HeartRateEvent evt){
		LocalTransformationDBMS db = new LocalTransformationDBMS(this.context);
		db.open();
		String timeStp = evt.getTimeOfMeasurement();
		double yValue = evt.getValue();
		double xValue = convertTimeToDouble(timeStp, "yyyy-MM-dd kk:mm:ss", "kk.mm");
		String time = getDayFromDate(timeStp, "yyyy-MM-dd kk:mm:ss", "dd-MM-yyyy");
		db.addTraffic(time, 999, xValue, yValue);
		db.addDateOfTraffic(time, 0);
		db.close();
	}
	

	private String getDayFromDate(String timeOfMeasurement, String dateFormat,
			String applyPattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		try {
			Date today = sdf.parse(timeOfMeasurement);
			sdf.applyPattern(applyPattern);

			return sdf.format(today);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static double convertTimeToDouble(String fullTime,
			String dateFormat, String applyPattern) {
		SimpleDateFormat fullDate = new SimpleDateFormat(dateFormat);
		SimpleDateFormat timeDate = new SimpleDateFormat(applyPattern);

		try {
			Date now = fullDate.parse(fullTime);
			String strDate = timeDate.format(now);
			double parseDate = Double.parseDouble(strDate);
			if (parseDate >= 24.00d)
				return parseDate - 24;
			return parseDate;

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return Double.parseDouble("00.00");
	}
}
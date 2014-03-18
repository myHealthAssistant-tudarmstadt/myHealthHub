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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.physical;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

/**
 * @author Chris
 *
 */
public final class HedgeHogAccelerometer extends AbstractSensorType {
	
	public HedgeHogAccelerometer(String sensorID, String sensorMAC, String eventType) {
		super(sensorID, sensorMAC);
		sensorType = "ACCELEROMETER";
		description = "Accelerometer designed by ESS Group, TU Darmstadt, Germany";
		properties = "delivers mean and variance values over a 4 seconds window";
		sensorEventTypes = new String[] {eventType};
		sensorStatusTypes = new int[] {STATUS_DISCONNECTED, STATUS_CONNECTED};	
	}
	
}
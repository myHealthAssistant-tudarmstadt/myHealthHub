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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.environmental;

import java.util.HashMap;

/**
 * @author Chris
 *
 */
public class EnvironmentalSensorConfigurationWithNames extends EnvironmentalSensorConfiguration {
	
	public HashMap<String, String> MACtoNameMapping;
	
	public EnvironmentalSensorConfigurationWithNames() {
		super();
		MACtoNameMapping = new HashMap<String, String>();
	}

	public String getName(String MAC_address) {
		return MACtoNameMapping.get(MAC_address.toLowerCase());
	}
	
	public void addSensor(String MACAddress, String sensorType, String location, String object) {
		super.addSensor(MACAddress, sensorType, location, object);
		MACtoNameMapping.put(MACAddress, location);
	}
}
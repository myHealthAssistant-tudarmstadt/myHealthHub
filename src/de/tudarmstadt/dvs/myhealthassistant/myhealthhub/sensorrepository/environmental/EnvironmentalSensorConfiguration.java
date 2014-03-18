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
public class EnvironmentalSensorConfiguration {
	
	public HashMap<String, String> MACtoSensorTypeMapping;
	public HashMap<String, String> MACtoLocationMapping;
	public HashMap<String, String> MACtoObjectTypeMapping;
	
	
	public final static String KITCHEN = "kitchen";
	public final static String LIVING_ROOM = "living_room";
	public final static String FLOOR = "floor";
	public final static String BED_ROOM = "bed_room";
	public final static String BATH_ROOM = "bath_room";
	public final static String ENTRANCE = "entrance";
	public final static String DINING_ROOM = "dining_room";	
	
	public final static String OBJ_TOOTH_BRUSH = "tooth_brush";
	public final static String OBJ_CHAIR = "chair";
	public final static String OBJ_FRIDGE = "fridge";
	public final static String OBJ_WALL = "wall";
	public final static String OBJ_DOOR = "door";
	public final static String OBJ_SHOWER = "shower";
	public final static String OBJ_SHAMPOO = "shampoo";
	public final static String OBJ_DRAWER = "drawer";
	
	public EnvironmentalSensorConfiguration() {
		MACtoLocationMapping = new HashMap<String, String>();
		MACtoSensorTypeMapping = new HashMap<String, String>();
		MACtoObjectTypeMapping = new HashMap<String, String>();
	}
	
	public String getLocation(String MAC_address) {
		return MACtoLocationMapping.get(MAC_address.toLowerCase());
	}

	public String getSensorType(String MAC_address) {
		return MACtoSensorTypeMapping.get(MAC_address.toLowerCase());
	}
	
	public String getObjectType(String MAC_address) {
		return MACtoObjectTypeMapping.get(MAC_address.toLowerCase());
	}
	
	public void addSensor(String MACAddress, String sensorType, String location, String object) {
		MACtoLocationMapping.put(MACAddress, location);
		MACtoObjectTypeMapping.put(MACAddress, object);
		MACtoSensorTypeMapping.put(MACAddress, sensorType);
	}
}
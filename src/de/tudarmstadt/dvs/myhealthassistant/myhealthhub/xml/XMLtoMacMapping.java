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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.environmental.EnvironmentalSensorConfiguration;

/**
 * @author Chris
 *
 */
public class XMLtoMacMapping {

	static final String KEY_ROOT = "mac-sensor-mapping";
	static final String KEY_SENSOR_MAPPING = "sensormapping";
	static final String KEY_MAC = "mac";
	static final String KEY_SENSOR_TYPE = "sensortype";
	static final String KEY_LOCATION = "location";
	static final String KEY_OBJECT = "object";
	
	// for debugging
	private static boolean D = false;
	private static String TAG = "XMLtoMACMapping"; 
	
	private XMLParser parser;
	
	public XMLtoMacMapping() {
		if(D)Log.d(TAG, "XMLtoMapping Parser instantiated.");
		parser = new XMLParser();		
	}
	
	public EnvironmentalSensorConfiguration parseFile(String filename) {
		if(D)Log.d(TAG, "Parsing started...");
		
		// get DOM document
		Document doc = parser.getDomElementFromFile(filename);
		if(doc == null) return null;
		
		
		
		//TODO error handler if wrong file
		
		// retrieve entries and store them in config file
		NodeList nodes = doc.getElementsByTagName(KEY_SENSOR_MAPPING);
		
		EnvironmentalSensorConfiguration conf = new EnvironmentalSensorConfiguration();
		Element e;
		for(int i = 0; i < nodes.getLength(); i++) {
			e = (Element) nodes.item(i);
			if(D)Log.i(TAG, "Adding node with MAC "+parser.getValue(e, KEY_MAC)+"...");
			conf.addSensor(
					parser.getValue(e, KEY_MAC),
					parser.getValue(e, KEY_SENSOR_TYPE),
					parser.getValue(e, KEY_LOCATION), 
					parser.getValue(e, KEY_OBJECT));
		}		
		
		return conf;
	}
	
}
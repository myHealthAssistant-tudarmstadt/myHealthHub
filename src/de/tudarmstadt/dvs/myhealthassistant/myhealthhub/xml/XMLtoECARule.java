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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 * @author Christian Seeger
 *
 */
public class XMLtoECARule {
	
	static final String KEY_SIMPLERULE = "simplerule";
	static final String KEY_EXTENDEDRULE = "extendedrule";
	static final String KEY_CONSECUTIVERULE = "consecutiverule";
	static final String KEY_ID = "id";	
	static final String KEY_EVENT_TYPE = "event-type";
	static final String KEY_SPECIFIC_ROOM = "specific-room";
	static final String KEY_SPECIFIC_LOCATION = "specific-location";
	static final String KEY_LEFT_TERM = "left-term";
	static final String KEY_OPERATOR = "operator";
	static final String KEY_RIGHT_TERM = "right-term";
	static final String KEY_ACTION = "action";
	static final String KEY_RULE_ID = "ruleid";
	static final String KEY_DURATION = "duration";
	
	// for debugging
	private static boolean D = false;
	private static String TAG = "XMLtoECARule"; 
	
	private XMLParser parser;
	private HashMap<String, Integer> leftTermValues;
	private HashMap<String, Integer> operators;
	private HashMap<String, Integer> actions;
	private HashMap<Integer, Element> xmlElements;
	
	public XMLtoECARule() {
		parser = new XMLParser();
		
		leftTermValues = new HashMap<String, Integer>();
		leftTermValues.put("Event.sensor_value", ECARule.LEFTTERM_VALUE);
		
		operators = new HashMap<String, Integer>();
		operators.put("=", ECARule.OP_EQUALS);
		
		actions = new HashMap<String, Integer>();
		actions.put("tooth_brushing", ECARule.ACTION_EVENT_TOOTH_BRUSHING);
		actions.put("eating", ECARule.ACTION_EVENT_EATING);
		actions.put("entering", ECARule.ACTION_EVENT_ENTERING);
		actions.put("leaving", ECARule.ACTION_EVENT_LEAVING);
		actions.put("showering", ECARule.ACTION_EVENT_SHOWERING);
		actions.put("airing", ECARule.ACTION_EVENT_AIRING);
	}
	
	public ECARule[] parseFile(String filename) {
		// load XML file
		String xml = readFile(filename);
		
		if(D)Log.d(TAG, "XML file:"+xml);
		
		// get DOM document
		Document doc = parser.getDomElement(xml);
		if(doc == null) return null;
		
		NodeList nl_simplerules = doc.getElementsByTagName(KEY_SIMPLERULE);
		NodeList nl_extendedrules = doc.getElementsByTagName(KEY_EXTENDEDRULE);
		NodeList nl_consecutiverules = doc.getElementsByTagName(KEY_CONSECUTIVERULE);
					
		// create ECARule array
		ECARule[] rules = new ECARule[nl_simplerules.getLength()+
		                              nl_extendedrules.getLength()];
		if(D)Log.d(TAG, "Prepare to create "+nl_simplerules.getLength()+
				" simple rules and "+nl_extendedrules.getLength()+
				" extended rules");
		
		xmlElements = new HashMap<Integer, Element>();
		
		// looping through all simple rules
		for(int i = 0; i < nl_simplerules.getLength(); i++) {
			Element e = (Element) nl_simplerules.item(i);
		
			// check ID and add to list
			addToElementsList(e);
			
			rules[i] = createRule(e);		
		}
		
		// loop through consecutive rules first in order
		// to provide them for extended rules creation
		for(int i = 0; i < nl_consecutiverules.getLength(); i++) {
			Element e = (Element) nl_consecutiverules.item(i);
			// check ID and add to list
			addToElementsList(e);
		}
		
		int rulesOffset = nl_simplerules.getLength();
		
		// looping through all extended rules
		for(int i = 0; i < nl_extendedrules.getLength(); i++) {
			Element e = (Element) nl_extendedrules.item(i);
			
			// check ID and add to list
			addToElementsList(e);
			
			int consecutiveRuleID = new Integer(parser.getValue(e, KEY_RULE_ID));
			
			if(D)printValues(e, true);
			rules[rulesOffset+i] = new ExtendedECARule(
					new Integer(parser.getValue(e, KEY_ID)), 
					parser.getValue(e, KEY_EVENT_TYPE),
					parser.getValue(e, KEY_SPECIFIC_ROOM),
					parser.getValue(e, KEY_SPECIFIC_LOCATION),
					leftTermValues.get(parser.getValue(e, KEY_LEFT_TERM)),
					operators.get(parser.getValue(e, KEY_OPERATOR)), 
					parser.getValue(e, KEY_RIGHT_TERM), 
					new Integer(parser.getValue(e, KEY_DURATION))*1000,
					createRule(xmlElements.get(consecutiveRuleID)));			
		}
		
		return rules;
	}
	
	private ECARule createRule(Element e) {
		if(D)printValues(e, false);
		ECARule rule = new ECARule(
				new Integer(parser.getValue(e, KEY_ID)), 
				parser.getValue(e, KEY_EVENT_TYPE),
				parser.getValue(e, KEY_SPECIFIC_ROOM),
				parser.getValue(e, KEY_SPECIFIC_LOCATION),
				leftTermValues.get(parser.getValue(e, KEY_LEFT_TERM)),
				operators.get(parser.getValue(e, KEY_OPERATOR)), 
				parser.getValue(e, KEY_RIGHT_TERM), 
				actions.get(parser.getValue(e, KEY_ACTION)));		
		return rule;
	}
	
	private void addToElementsList(Element e) {
		int id = new Integer(parser.getValue(e, KEY_ID));
		if(xmlElements.containsKey(id)) {
			// TODO create error event
			Log.e(TAG, "Duplicated rule ID "+id);
		} else {
			xmlElements.put(id, e);
		}
	}
	
	private void printValues(Element e, boolean extended) {
		Log.d(TAG, KEY_ID+": "+parser.getValue(e, KEY_ID));
		Log.d(TAG, KEY_EVENT_TYPE+": "+parser.getValue(e, KEY_EVENT_TYPE));
		Log.d(TAG, KEY_SPECIFIC_ROOM+": "+parser.getValue(e, KEY_SPECIFIC_ROOM));
		Log.d(TAG, KEY_SPECIFIC_LOCATION+": "+parser.getValue(e, KEY_SPECIFIC_LOCATION));
		Log.d(TAG, KEY_LEFT_TERM+": "+parser.getValue(e, KEY_LEFT_TERM));
		Log.d(TAG, KEY_OPERATOR+": "+parser.getValue(e, KEY_OPERATOR));
		Log.d(TAG, KEY_RIGHT_TERM+": "+parser.getValue(e, KEY_RIGHT_TERM));
		if(!extended) {
			Log.d(TAG, KEY_ACTION+": "+parser.getValue(e, KEY_ACTION));
		} else {
			Log.d(TAG, KEY_DURATION+": "+parser.getValue(e, KEY_DURATION));
			Log.d(TAG, KEY_ACTION+": rule #"+parser.getValue(e, KEY_ACTION));
		}
	}
	

	private String readFile(String filename) {
		StringBuilder filecontent = new StringBuilder();
		//File root = Environment.getExternalStoragePublicDirectory("myHealthAssistant");
		//File file = new File(root, filename);
		
		//File root = Environment.getExternalStorageDirectory();
		//File file = new File(root, "rules.xml");
		// new:
		File file = new File(filename);
		
		try {
    	    BufferedReader br = new BufferedReader(new FileReader(file));
    	    
    	    if(D)Log.d(TAG, "StartProducer reading file: "+file.toString()+"...");

    	    String line;
            String lineFeed = System.getProperty("line.separator");
    	    while ((line = br.readLine()) != null) {
    	    	filecontent.append(line).append(lineFeed);
    	    }
    	    
    	    if(D)Log.d(TAG, "Reading of file "+file.toString()+" is finished");
    	}
    	catch (IOException e) {
    		Log.e(TAG, "Unable to read file: "+file.toString());
    	}   	
		
		return filecontent.toString();
	}
}
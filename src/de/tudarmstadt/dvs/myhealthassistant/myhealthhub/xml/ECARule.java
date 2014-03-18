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

/**
 * @author Chris
 *
 */
public class ECARule {
	public static final int ACTION_EXTENDED_RULE = 0;
	public static final int ACTION_EVENT_ENTERING = 1;
	public static final int ACTION_EVENT_LEAVING = 2;
	public static final int ACTION_EVENT_SHOWERING = 3;
	public static final int ACTION_EVENT_TOOTH_BRUSHING = 4;
	public static final int ACTION_EVENT_EATING = 5;
	public static final int ACTION_EVENT_AIRING = 6;	
	
	public static final int OP_EQUALS = 1;
	
	public static final int LEFTTERM_VALUE = 1;
	public static final int LEFTTERM_TIMESTAMP = 2;
	
	public int id;
	public String eventType;
	public String location;
	public String object;
	public int leftTerm;
	public int operator;
	public String rightTerm;
	public int action;
			
	public ECARule(int id, String eventType, 
			String location, String object,
			int leftTerm, int operator, String rightTerm, 
			int action) {
		this.id = id;
		this.eventType = eventType;
		this.location = location;
		this.object = object;
		this.leftTerm = leftTerm;
		this.operator = operator;
		this.rightTerm = rightTerm;
		this.action = action;
	}
}
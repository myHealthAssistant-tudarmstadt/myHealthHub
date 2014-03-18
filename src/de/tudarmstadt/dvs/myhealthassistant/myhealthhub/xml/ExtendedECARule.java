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
public class ExtendedECARule extends ECARule {
	public int maxTimeDifference;
	public ECARule secondRule;
	
	public ExtendedECARule(int id, 
			String eventType, String location, String object, 
			int leftTerm, int operator, String rightTerm,
			int maxTimeDifference, ECARule secondRule) {
		super(id, eventType, location, object, leftTerm, operator, rightTerm, ACTION_EXTENDED_RULE);

		this.maxTimeDifference = maxTimeDifference;
		this.secondRule = secondRule;
	}
	
}
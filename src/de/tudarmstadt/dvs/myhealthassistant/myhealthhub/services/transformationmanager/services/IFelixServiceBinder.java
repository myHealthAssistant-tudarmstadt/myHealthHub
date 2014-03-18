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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.services;

import org.osgi.framework.Bundle;

public interface IFelixServiceBinder {

	/**
	 * Start the transformation with bundleID
	 * @param bundleId transformation's ID
	 */
	public void startTransformation(long bundleId);
	
	/**
	 * Stops the transformation with bundleID
	 * @param bundleId transformation's ID
	 */
	public void stopTransformation(long bundleId);
	
	/**
	 * Removes the transformation with bundleID
	 * @param bundleId transformation's ID
	 */
	public void removeTransformation(long bundleId);
	
	/**
	 * Returns all available transformations
	 * @return Bundle[] of transformations
	 */
	public Bundle[] getTransformations();

}
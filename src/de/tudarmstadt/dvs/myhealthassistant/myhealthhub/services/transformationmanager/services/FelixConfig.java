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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.main.AutoProcessor;

public class FelixConfig {

	private Map<String, String> config;
	
	private final String ANDROID_PACKAGES_FOR_EXPORT= 
		        "android; " + 
		        "android.app;" + 
		        "android.content;" + 
		        "android.database;" + 
		        "android.database.sqlite;" + 
		        "android.graphics; " + 
		        "android.graphics.drawable; " + 
		        "android.graphics.glutils; " + 
		        "android.hardware; " + 
		        "android.location; " + 
		        "android.media;" + 
		        "android.net;" + 
		        "android.opengl; " + 
		        "android.os; " + 
		        "android.provider; " + 
		        "android.sax; " + 
		        "android.speech.recognition; " + 
		        "android.telephony; " + 
		        "android.telephony.gsm; " + 
		        "android.text; " + 
		        "android.text.method; " + 
		        "android.text.style; " + 
		        "android.text.util; " + 
		        "android.util; " + 
		        "android.view; " + 
		        "android.view.animation; " + 
		        "android.webkit; " + 
		        "android.widget"; 
	
	public FelixConfig(String felixDeploymentDir){		
		config = new HashMap<String, String>();
		config.put(org.osgi.framework.Constants.FRAMEWORK_STORAGE,
				felixDeploymentDir.concat(File.separator).concat("felix-cache"));
		config.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY,
				felixDeploymentDir.concat(File.separator).concat("bundle"));
		config.put(BundleCache.CACHE_ROOTDIR_PROP, felixDeploymentDir);
		config.put("felix.embedded.execution", "true");
		config.put("org.osgi.framework.bootdelegation", "*");
		config.put("felix.bootdelegation.implicit", "false");
		//config.put("felix.log.level", "3");
		config.put("felix.service.urlhandlers", "false");
		config.put("felix.auto.deploy.action", "install,start");
		config.put("org.osgi.framework.system.packages.extra", ANDROID_PACKAGES_FOR_EXPORT);			
	}
	
	public Map<String,String> getProperties2() {
		return config;
	}
}
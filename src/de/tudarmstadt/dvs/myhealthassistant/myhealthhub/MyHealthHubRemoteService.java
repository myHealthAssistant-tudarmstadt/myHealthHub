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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.SensorModuleManager;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.messagehandler.MessageHandler;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.TransformationManager;

/**
 * Implements the myHealthHub Service which needs to be started in order
 * to run myHealthHub
 * 
 * @author Christian Seeger
 */
public class MyHealthHubRemoteService extends Service {

	/* for debugging */
	private static String TAG = "MyHealthHubRemoteService";
	private static boolean D = true;
	
	private Intent mMessageHandlerOpenChannelsIntent;
	private Intent mMessageHandlerIntent;
	private Intent mSensorModuleManagerIntent;
	private Intent mTransformationManager;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		if(D)Log.d(TAG, "onBind");
		
		//TODO StartProducer security manager
		
		
		// StartProducer message handler
		//mMessageHandlerOpenChannelsIntent = new Intent(this, MessageHandlerWithOpenChannels.class);
		//startService(mMessageHandlerOpenChannelsIntent);
			
		// StartProducer message handler
		mMessageHandlerIntent = new Intent(this, MessageHandler.class);
		startService(mMessageHandlerIntent);
		
		
		// StartProducer sensor modules
		mSensorModuleManagerIntent = new Intent(this, SensorModuleManager.class);
		startService(mSensorModuleManagerIntent);
		
		
		//TODO StartProducer system monitor
		
		
		
		//TODO StartProducer event composer
		
		
		
		//Start TransformationManager
		mTransformationManager = new Intent(this, TransformationManager.class);
		startService(mTransformationManager);
		
		
		return iservicestub;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if(D)Log.d(TAG, "onUnbind");
		// Unbind services
		if(mMessageHandlerOpenChannelsIntent!=null) stopService(mMessageHandlerOpenChannelsIntent);
		if(mMessageHandlerIntent!=null) stopService(mMessageHandlerIntent);
		if(mSensorModuleManagerIntent!=null)stopService(mSensorModuleManagerIntent);
		if(mTransformationManager!=null)stopService(mTransformationManager);
		
		return super.onUnbind(intent);
	}
	
	public void onCreate() {
		if(D)Log.d(TAG, "onCreate");		
	}
	
	public void onDestroy() {
		if(D)Log.d(TAG, "onDestroy");
	}
	

	private IMyHealthHubRemoteService.Stub iservicestub = new IMyHealthHubRemoteService.Stub() {
		
		@Override
		public int getStatus() throws RemoteException {
			return 0;
		}		
		
	};
}
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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;

public class TrafficGenerator2 {
	public String TAG = "TrafficGenerator2";
	private boolean D = false;
	
	private static int HR = 0;
	/*private static int BP = 1;
	private static int WEIGHT = 2;
	private static int ACC_LEG = 3;*/

	private static int SECOND = 1000;
	private static Handler eventGeneratorHandler = new Handler();
			
	private boolean isRunning;
	private int round;
	private int totalRound;
	private int[][] messagesPerSecond;
	
	private Context ctx;
	
	private int eventNumber;
	
	protected String myHealthHubReceiver = AbstractChannel.RECEIVER;
	
	private SimpleDateFormat df = new java.text.SimpleDateFormat("dd MMM yyyy hh:mm:ss");
	
	public TrafficGenerator2(int instance, int[][] msgsPerSecond, Context ctx) {
    	TAG += "_"+instance;

    	this.isRunning = false;
        this.messagesPerSecond = msgsPerSecond;
        this.ctx = ctx;
        
		/* set time zone */
		df.setTimeZone(TimeZone.getTimeZone("gmt"));
    }
    
    public void startRound(int i, int j) {
    	eventNumber = 0;
    	round = i;
    	totalRound = j;
    	//hr_counter = 0;
		isRunning = true;
		if(messagesPerSecond[round][HR]!=0) generateEventsHR.run();
		/*if(messagesPerSecond[round][BP]!=0) generateEventsBP.run();
		if(messagesPerSecond[round][WEIGHT]!=0) generateEventsWeight.run();
		if(messagesPerSecond[round][ACC_LEG]!=0) generateEventsAccLeg.run();*/
    }
    
    public void stop() {
    	isRunning = false;
    }
    
    private Runnable generateEventsHR = new Runnable() {
		public void run() {
			if (isRunning) {
				eventGeneratorHandler.postDelayed(generateEventsHR, SECOND
						/ messagesPerSecond[round][HR]);
				
				/* generate event */
				HeartRateEvent evt = new HeartRateEvent(TAG+"_HR_"+round+"_"+eventNumber, getGMTTime(),
						"EventGenerator", "EventGenerator", totalRound+"",
						getRandomNumber(55, 180));
		    	Intent i = new Intent();
				i.putExtra("event_type", evt.getEventType());
				i.putExtra("event", evt);
				i.setAction(myHealthHubReceiver);
				ctx.sendBroadcast(i);
			}
		}
    };
            
    public int getRandomNumber(int min, int max) {
    	//Random randomGenerator = new Random();
    	//return randomGenerator.nextInt(max-min)+min;
    	return eventNumber++;
    }
    
    private void injectEvent(Event evt) {
		if(D)Log.d(TAG, "injectEvent of type: "+evt.getEventType());
    	Intent i = new Intent();
		i.putExtra("event_type", evt.getEventType());
		i.putExtra("event", evt);
		i.setAction(myHealthHubReceiver);
		ctx.sendBroadcast(i);
    }
    
    private String getGMTTime() {
		return df.format(new Date());
	}
}
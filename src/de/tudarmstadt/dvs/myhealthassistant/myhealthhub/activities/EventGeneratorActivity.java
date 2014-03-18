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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.TrafficGenerator;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.ManagementEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.ECGEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.WeightEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.activity.ActivityEventSSWRC;


public class EventGeneratorActivity extends Activity {
    
	public static String TAG = "EventGeneratorActivity";
	private boolean D = true;
	
	public static int START = 1;
	public static int STOP = 0;
	
	private static int HR = 0;
	private static int ACTIVITY = 1;
	private static int WEIGHT = 2;
	private static int ECG = 3;
	
	private int stepsPerRound = 1;
	private static int SECOND = 1000;
	private static int SECONDS_BETWEEN_ROUNDS = 5;
	private static int SECONDS_FOR_WARMUP = 5; //15
	private static int SECONDS_FOR_SETUP = 5; //15
	private static Handler eventGeneratorHandler = new Handler();
				
	private boolean isRunning;
	private int eventGenerators;
	private int maxEventGenerators;
	private int numReceivers;
	private int maxReceivers;
	private int increaseRateReceivers;
	private boolean multipeReceivers;
	
	private int[][] messagesPerSecond;
	private int duration;
	
	/* for performance evaluation */
	private int pid;
	private ActivityManager activityManager;
	private float[] cpuUsage;
	private float[] memUsagePss;
	private float[] memUsageDirty;
	private int usagePointer;
	private MemoryInfo mi;
	
	/* for delivery ratio */
	private ReadingEventReceiver mReadingEventReceiver;
	private int countEventsHR;
	private int countEventsActivity;
	private int countEventsWeight;
	private int countEventsECG;

	protected EventUtils myManagementUtils;	
	
	private TrafficGenerator tg1;private TrafficGenerator tg2;private TrafficGenerator tg3;
	private TrafficGenerator tg4;private TrafficGenerator tg5;private TrafficGenerator tg6;
	private TrafficGenerator tg7;private TrafficGenerator tg8;private TrafficGenerator tg9;
	private TrafficGenerator tg10;private TrafficGenerator tg11;private TrafficGenerator tg12;
	private TrafficGenerator tg13;private TrafficGenerator tg14;private TrafficGenerator tg15;
	private TrafficGenerator tg16;private TrafficGenerator tg17;private TrafficGenerator tg18;
	private TrafficGenerator tg19;private TrafficGenerator tg20;private TrafficGenerator tg21;
	private TrafficGenerator tg22;private TrafficGenerator tg23;private TrafficGenerator tg24;
	private TrafficGenerator tg25;private TrafficGenerator tg26;private TrafficGenerator tg27;
	private TrafficGenerator tg28;private TrafficGenerator tg29;private TrafficGenerator tg30;
	
	private SimpleDateFormat df = new java.text.SimpleDateFormat("dd MMM yyyy hh:mm:ss");
	
	public void onClickStartReceivers(View v) {
		multipeReceivers = true;
		numReceivers = 9;
		maxReceivers = 11;
		increaseRateReceivers = 2;
		setupReceivers();
	}	

	public void onClickStart(View v) {
		multipeReceivers = false;
		
		// Register receivers
		getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.HEART_RATE));
		getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.ACTIVITY));
		getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.WEIGHT));
		getApplicationContext().registerReceiver(mReadingEventReceiver, new IntentFilter(SensorReadingEvent.ECG_STREAM));
		
		startRound();
	}

	private void loadConfiguration() {
		messagesPerSecond = new int[1][4];
		
		/* Configure rounds */
		duration = 120;			// amount of seconds per round
		maxEventGenerators = 26;	// maximum number of event generators (limited to 30)
		int startEventGenerators = 5;	// start number of event generators
		stepsPerRound=5;		//	additional event generators per round
		
		/* which events shall be generated */
		messagesPerSecond[0][HR] = 1;			// heart rate
	    messagesPerSecond[0][ACTIVITY] = 0;		// activities
	    messagesPerSecond[0][WEIGHT] = 1;		// weight
	    messagesPerSecond[0][ECG] = 0;			// ECG
	    
	    eventGenerators=startEventGenerators-1;
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_activity);
        
		/* tiny hack */
		((CheckBox)findViewById(R.id.cbAcc)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbBP)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbHR)).setEnabled(false);
		((CheckBox)findViewById(R.id.cbWeight)).setEnabled(false);
		
		((EditText)findViewById(R.id.etAcc)).setEnabled(false);
		((EditText)findViewById(R.id.etBP)).setEnabled(false);
		((EditText)findViewById(R.id.etHR)).setEnabled(false);
		((EditText)findViewById(R.id.etSimulationTime)).setEnabled(false);
		((EditText)findViewById(R.id.etWeight)).setEnabled(false);   
        
        isRunning = false;
        
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        pid = android.os.Process.myPid();
        Log.i(TAG, "PID: "+pid);
        
		/* set time zone */
		df.setTimeZone(TimeZone.getTimeZone("gmt"));
		
		myManagementUtils = new EventUtils(ManagementEvent.getManagement(), "exampleSensor");
		
		mReadingEventReceiver = new ReadingEventReceiver();
    }
    
	private void startEventGeneration() {
		
		if(eventGenerators < maxEventGenerators) {
			Log.i(TAG, "Perf: Starting eventGenerators "+(eventGenerators+1)+" with "+(numReceivers+1)+" Receivers...");
			((TextView)findViewById(R.id.tvRound)).setText("Event Generators: "+(eventGenerators+1));
			
			isRunning = true;
			((Button)findViewById(R.id.buttonStart)).setEnabled(false);
			((Button)findViewById(R.id.buttonStart)).setText("running...");
			
			/* Control thread */
			eventGeneratorHandler.postDelayed(controlThread, duration*SECOND);

			/* measure each second */
			cpuUsage = new float[duration];
			memUsagePss = new float[duration];
			memUsageDirty = new float[duration];
			usagePointer = 0;
			
			/* reset delivery ratio */
			countEventsActivity = 0;
			countEventsECG = 0;
			countEventsHR = 0;
			countEventsWeight = 0;
			
			storeCPUandMemUsage.run();

			switch(eventGenerators) {
			case 29: tg30.startRound(0, eventGenerators);
			case 28: tg29.startRound(0, eventGenerators);
			case 27: tg28.startRound(0, eventGenerators);
			case 26: tg27.startRound(0, eventGenerators);
			case 25: tg26.startRound(0, eventGenerators);
			case 24: tg25.startRound(0, eventGenerators);
			case 23: tg24.startRound(0, eventGenerators);
			case 22: tg23.startRound(0, eventGenerators);
			case 21: tg22.startRound(0, eventGenerators);
			case 20: tg21.startRound(0, eventGenerators);
			case 19: tg20.startRound(0, eventGenerators);
			case 18: tg19.startRound(0, eventGenerators);
			case 17: tg18.startRound(0, eventGenerators);
			case 16: tg17.startRound(0, eventGenerators);
			case 15: tg16.startRound(0, eventGenerators);
			case 14: tg15.startRound(0, eventGenerators);
			case 13: tg14.startRound(0, eventGenerators);
			case 12: tg13.startRound(0, eventGenerators);
			case 11: tg12.startRound(0, eventGenerators);
			case 10: tg11.startRound(0, eventGenerators);
			case 9: tg10.startRound(0, eventGenerators);
			case 8: tg9.startRound(0, eventGenerators);
			case 7: tg8.startRound(0, eventGenerators);
			case 6: tg7.startRound(0, eventGenerators);
			case 5: tg6.startRound(0, eventGenerators);
			case 4: tg5.startRound(0, eventGenerators);
			case 3: tg4.startRound(0, eventGenerators);
			case 2: tg3.startRound(0, eventGenerators);
			case 1: tg2.startRound(0, eventGenerators);
			case 0: tg1.startRound(0, eventGenerators);
			break;
			default: break;
			
			}
		} else {
			((TextView)findViewById(R.id.tvRound)).setText("done. :-)");
			getApplicationContext().unregisterReceiver(mReadingEventReceiver);
		}
	}
	
	private Runnable controlThread = new Runnable() {
		public void run() {
			// Chi Hieu: Send STOP Announcement
			// ...
			
			// StopProducer generators
			tg1.stop();	tg2.stop();	tg3.stop();	tg4.stop();			
			tg5.stop();	tg6.stop();	tg7.stop();	tg8.stop();
			tg9.stop();	tg10.stop(); tg11.stop(); tg12.stop();
			tg13.stop();tg14.stop();tg15.stop();tg16.stop();
			tg17.stop();tg18.stop();tg19.stop();tg20.stop();
			tg21.stop();tg22.stop();tg23.stop();tg24.stop();
			tg25.stop();tg26.stop();tg27.stop();tg28.stop();
			tg29.stop();tg30.stop();
			isRunning = false;
			
			// Write Log files
			String text = "\n"+(eventGenerators+1)+" ";
			for(float value : cpuUsage) text += value + " ";
			writeStringToLogFileCPU(text);
			
			text = "\n"+(eventGenerators+1)+" ";
			for(float value : memUsagePss) text += value + " ";
			writeStringToLogFileMem(text);
			
			int sumDelivered = countEventsActivity+countEventsECG+countEventsHR+countEventsWeight;
			int sent = (eventGenerators+1)*duration*
							(messagesPerSecond[0][HR]+messagesPerSecond[0][WEIGHT]+
							messagesPerSecond[0][ECG]+messagesPerSecond[0][ACTIVITY]);
			float deliveryRatio = (float)sumDelivered / (float)sent;
			text = "\n"+(eventGenerators+1)+" "+deliveryRatio+" "+sumDelivered+"/"+sent+" HR: "+countEventsHR+ " Weight: "+countEventsWeight+
					" ECG: "+countEventsECG+" Activity: "+countEventsActivity;
			writeStringToLogDelivery(text);
			
			if(D)Log.d(TAG, "sumDelivered: "+sumDelivered+", sent: "+sent+", ratio: "+deliveryRatio);
			
			
			// increase eventGenerators number
			eventGenerators+=stepsPerRound;
			
			if(eventGenerators>=maxEventGenerators) {
				
				eventGenerators = 0;
				
				if(!multipeReceivers) Log.i(TAG, "Perf: done.");
				
				if(multipeReceivers & numReceivers <= maxReceivers) {
					/* increase number of receivers */
					numReceivers += increaseRateReceivers;
					setupReceivers();
				} else {
					((Button)findViewById(R.id.buttonStart)).setEnabled(true);
					((Button)findViewById(R.id.buttonStart)).setText("StartProducer");
					Log.i(TAG, "Perf: done.");
					((TextView)findViewById(R.id.tvRound)).setText("done. :-)");
				}
			} else {
				// wait before the next eventGenerators
				waitForNext();				
			}			
		}
	};
	
	/**
	 * Warum up phase
	 */
	private void warmUp() {
		// Chi Hieu: Send START Announcement
		// ...
		((TextView)findViewById(R.id.tvRound)).setText("waiting...");
		eventGeneratorHandler.postDelayed(startEventGenerationThread, SECONDS_FOR_WARMUP*SECOND);
	}
	
	/**
	 * Wait until next eventGenerators is started.
	 */
	private void waitForNext() {
		((TextView)findViewById(R.id.tvRound)).setText("waiting...");
		eventGeneratorHandler.postDelayed(startEventGenerationThread, SECONDS_BETWEEN_ROUNDS*SECOND);
	}
	
	/**
	 * Triggers the event generation
	 */
	private Runnable startEventGenerationThread = new Runnable() {
		public void run() {
			startEventGeneration();
		}
	};
	

	private Runnable storeCPUandMemUsage = new Runnable() {
		public void run() {
			if(isRunning && usagePointer<cpuUsage.length) {
				eventGeneratorHandler.postDelayed(storeCPUandMemUsage, SECOND);
			
				cpuUsage[usagePointer] = readCPUUsage();
				mi = getMemInfo();
				memUsageDirty[usagePointer] = mi.getTotalPrivateDirty();
				memUsagePss[usagePointer] = mi.getTotalPss();
				usagePointer++;				
			}
			
		}
	};
    
       
    public int getRandomNumber(int min, int max) {
    	Random randomGenerator = new Random();
    	return randomGenerator.nextInt(max-min)+min;
    }
    
    private void setupReceivers() {
    	//TODO changed. find another solution
    	//SensorConnectionStatus evt = new SensorConnectionStatus("egal", "auch egal", "event_generator", "BigMAC", numReceivers+"", 0, "egal");
    	//injectEvent(evt);
    	
    	/* wait for setup */
    	eventGeneratorHandler.postDelayed(waitForSetupThread, SECONDS_FOR_SETUP);
    }
    
	private Runnable waitForSetupThread = new Runnable() {
		public void run() {
			//onClickStart(findViewById(R.id.buttonStart));
			startRound();
		}
	};
   
    private void startRound() {
   		loadConfiguration();
  
   		// StartProducer event generators
   		tg1 = new TrafficGenerator(1, messagesPerSecond, getApplicationContext());
    	tg2 = new TrafficGenerator(2, messagesPerSecond, getApplicationContext());
    	tg3 = new TrafficGenerator(3, messagesPerSecond, getApplicationContext());
    	tg4 = new TrafficGenerator(4, messagesPerSecond, getApplicationContext());
    	tg5 = new TrafficGenerator(5, messagesPerSecond, getApplicationContext());
    	tg6 = new TrafficGenerator(6, messagesPerSecond, getApplicationContext());
    	tg7 = new TrafficGenerator(7, messagesPerSecond, getApplicationContext());
    	tg8 = new TrafficGenerator(8, messagesPerSecond, getApplicationContext());
    	tg9 = new TrafficGenerator(9, messagesPerSecond, getApplicationContext());
    	tg10 = new TrafficGenerator(10, messagesPerSecond, getApplicationContext());
    	tg11 = new TrafficGenerator(11, messagesPerSecond, getApplicationContext());
    	tg12 = new TrafficGenerator(12, messagesPerSecond, getApplicationContext());
    	tg13 = new TrafficGenerator(13, messagesPerSecond, getApplicationContext());
    	tg14 = new TrafficGenerator(14, messagesPerSecond, getApplicationContext());
    	tg15 = new TrafficGenerator(15, messagesPerSecond, getApplicationContext());
    	tg16 = new TrafficGenerator(16, messagesPerSecond, getApplicationContext());
    	tg17 = new TrafficGenerator(17, messagesPerSecond, getApplicationContext());
    	tg18 = new TrafficGenerator(18, messagesPerSecond, getApplicationContext());
    	tg19 = new TrafficGenerator(19, messagesPerSecond, getApplicationContext());
    	tg20 = new TrafficGenerator(20, messagesPerSecond, getApplicationContext());
    	tg21 = new TrafficGenerator(21, messagesPerSecond, getApplicationContext());
    	tg22 = new TrafficGenerator(22, messagesPerSecond, getApplicationContext());
    	tg23 = new TrafficGenerator(23, messagesPerSecond, getApplicationContext());
    	tg24 = new TrafficGenerator(24, messagesPerSecond, getApplicationContext());
    	tg25 = new TrafficGenerator(25, messagesPerSecond, getApplicationContext());
    	tg26 = new TrafficGenerator(26, messagesPerSecond, getApplicationContext());
    	tg27 = new TrafficGenerator(27, messagesPerSecond, getApplicationContext());
    	tg28 = new TrafficGenerator(28, messagesPerSecond, getApplicationContext());
    	tg29 = new TrafficGenerator(29, messagesPerSecond, getApplicationContext());
    	tg30 = new TrafficGenerator(30, messagesPerSecond, getApplicationContext());

    	/* write log file */
		writeLogFile();	
    	
    	warmUp();
    }
    
    public void onClickCBManual(View v) {
    	if(((CheckBox)findViewById(R.id.cbManual)).isChecked()) {
    		/* Enable views */
    		((CheckBox)findViewById(R.id.cbAcc)).setEnabled(true);
    		((CheckBox)findViewById(R.id.cbBP)).setEnabled(true);
    		((CheckBox)findViewById(R.id.cbHR)).setEnabled(true);
    		((CheckBox)findViewById(R.id.cbWeight)).setEnabled(true);
    		
    		((EditText)findViewById(R.id.etAcc)).setEnabled(true);
    		((EditText)findViewById(R.id.etBP)).setEnabled(true);
    		((EditText)findViewById(R.id.etHR)).setEnabled(true);
    		((EditText)findViewById(R.id.etSimulationTime)).setEnabled(true);
    		((EditText)findViewById(R.id.etWeight)).setEnabled(true);    		
    	} else {
    		/* Disable views */
    		((CheckBox)findViewById(R.id.cbAcc)).setEnabled(false);
    		((CheckBox)findViewById(R.id.cbBP)).setEnabled(false);
    		((CheckBox)findViewById(R.id.cbHR)).setEnabled(false);
    		((CheckBox)findViewById(R.id.cbWeight)).setEnabled(false);
    		
    		((EditText)findViewById(R.id.etAcc)).setEnabled(false);
    		((EditText)findViewById(R.id.etBP)).setEnabled(false);
    		((EditText)findViewById(R.id.etHR)).setEnabled(false);
    		((EditText)findViewById(R.id.etSimulationTime)).setEnabled(false);
    		((EditText)findViewById(R.id.etWeight)).setEnabled(false);    		
    	}
    }
    
   
    private void writeLogFile() {
    	String text = "\nPerformance test from "+getGMTTime()+".\n"+
    			"Event generator configuration (events/second) for "+
    			duration+" seconds.\n";
		    	
		text += "Heart rate: "+messagesPerSecond[0][HR]+"\n";
		text += "Activity: "+messagesPerSecond[0][ACTIVITY]+"\n";
		text += "Weight: "+messagesPerSecond[0][WEIGHT]+"\n";
		text += "ECG: "+messagesPerSecond[0][ECG]+"\n";
		
		text += "---";
		writeStringToLogFileCPU(text);		
		writeStringToLogFileMem(text);
		writeStringToLogDelivery(text);
    }
    
    private void writeStringToLogFileCPU(String text) {
    	File root = Environment.getExternalStorageDirectory();
    	File file = new File(root, "EventGeneratorLogFileCPU.txt");
        try {
        	if(!file.exists()) file.createNewFile();
			FileWriter filewriter = new FileWriter(file, true);
			filewriter.append(text);
			filewriter.close();
		} catch (IOException e) {
			Toast.makeText(this, "Unable to write file: "+e.toString(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} 
    }
    
    private void writeStringToLogFileMem(String text) {
    	File root = Environment.getExternalStorageDirectory();
    	File file = new File(root, "EventGeneratorLogFileMem.txt");
        try {
        	if(!file.exists()) file.createNewFile();
			FileWriter filewriter = new FileWriter(file, true);
			filewriter.append(text);
			filewriter.close();
		} catch (IOException e) {
			Toast.makeText(this, "Unable to write file: "+e.toString(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} 
    }
    
    private void writeStringToLogDelivery(String text) {
    	File root = Environment.getExternalStorageDirectory();
    	File file = new File(root, "EventGeneratorLogDelivery.txt");
        try {
        	if(!file.exists()) file.createNewFile();
			FileWriter filewriter = new FileWriter(file, true);
			filewriter.append(text);
			filewriter.close();
		} catch (IOException e) {
			Toast.makeText(this, "Unable to write file: "+e.toString(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} 
    }
    
    private String getGMTTime() {
		return df.format(new Date());
	}
  
	private float readCPUUsage() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" ");

			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" ");

			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return 0;
	} 
	 
	private MemoryInfo getMemInfo() {
		return activityManager.getProcessMemoryInfo(new int[] {pid})[0];
		
	}
	
//		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, evt.getEventType());
//		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, evt);
//		i.setAction(AbstractChannel.RECEIVER);
//		sendBroadcast(i);
//    }
    

	/** Event receiver implemented as a Android BroadcastReceiver */
	private class ReadingEventReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			/* Get event type and the event itself */
			String eventType = intent
					.getStringExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE);

			if (D)
				Log.d(TAG, "Incoming event of type " + eventType);

			if (eventType.equals(HeartRateEvent.EVENT_TYPE)) {

				if(isRunning && messagesPerSecond[0][ECG]==0){
					countEventsHR++;
				} else if (isRunning) {
					HeartRateEvent evt = (HeartRateEvent)intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
					// don't count self generated events
					if(!evt.getSensorType().equals("ECG sensor")) {
						countEventsHR++;
					}
				}
			} else if (eventType.equals(ActivityEventSSWRC.EVENT_TYPE)) {
				if(isRunning) countEventsActivity++;
			} else if (eventType.equals(WeightEvent.EVENT_TYPE)) {
				if(isRunning) countEventsWeight++;
			} else if (eventType.equals(ECGEvent.EVENT_TYPE)) {
				if(isRunning) countEventsECG++;
			}
		};
	}
}
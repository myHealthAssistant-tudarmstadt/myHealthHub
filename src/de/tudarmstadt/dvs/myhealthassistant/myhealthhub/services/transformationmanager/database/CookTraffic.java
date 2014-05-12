package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.graphActivities.Coordinate;
import android.content.Context;
import android.hardware.Sensor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;

public class CookTraffic {

	private static final String TAG = CookTraffic.class.getSimpleName();
	private LocalTransformationDBMS transformationDB;
	private Context context;
	private String currentDate;
	private int mSize = 100;
	
	public CookTraffic(Context ctx){
		context = ctx;
		currentDate = "";
	}
	
	public void cookUp(){
		ArrayList<Coordinate> listAcc = new ArrayList<Coordinate>();
		ArrayList<Coordinate> listLight = new ArrayList<Coordinate>();
		
		String currentTime = "2014-05-10_23:50:00";//getTimestamp();
		// populate lists with random data;
		for (int i = 0; i < mSize; i++){
			String xValue = currentTime; //getCurrentTimeInDouble(currentTime);
			double yValue = getRandomNumber(70, 100);
			currentTime = increaseTime(currentTime, Calendar.MINUTE, false);
			Coordinate coo = new Coordinate(xValue, yValue);
			listAcc.add(coo);
			listLight.add(coo);
			
		}
		addTrafficToDB(currentTime, Sensor.TYPE_LIGHT, listLight);
		addTrafficToDB(currentTime, Sensor.TYPE_ACCELEROMETER, listAcc);
		
		Log.e(TAG, "data being cook up:"+ listAcc.size());
		Log.e(TAG, "data being cook up:"+ listLight.size());
	}
	
	private String increaseTime(String time, int timespan, boolean back) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_kk:mm:ss");

		try {
			Date today = sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);

			if (back) {
//				cal.add(Calendar.MINUTE, -1);
				cal.add(timespan, -1);
			} else {
				cal.add(timespan, 1);
			}

			String newDate = sdf.format(cal.getTime());
			return newDate;

		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		
		return time;
	}
	
	/**
	 * Return a random number with a specific range.
	 * 
	 * @param minimum
	 *            number count.
	 * @param maximum
	 *            number count.
	 * @return random number
	 */
	private double getRandomNumber(int minimum, int maximum) {
		Random randomGenerator = new Random();
		int ran = randomGenerator.nextInt(maximum - minimum) + minimum;
		return Math.sqrt(ran);
	}
	
	private void addTrafficToDB(String timeOfMeasurement, int type,
			ArrayList<Coordinate> listData) {
		
		ArrayList<String> dateToAdd = new ArrayList<String>();

		transformationDB = new LocalTransformationDBMS(context);
		transformationDB.open();
		ArrayList<String> databaseListData = transformationDB.getAllAvalDate();
		// add to traffic table
		for (Coordinate d : listData) {
			String date = getDayFromDate(d.getX(), "yyyy-MM-dd_kk:mm:ss", "dd-MM-yyyy");
			double xDouble = convertTimeToDouble(d.getX(), "yyyy-MM-dd_kk:mm:ss", "kk.mm");
			transformationDB.addTraffic(date, type, xDouble, d.getY());

			Log.e(TAG, "addTraffic: " + date + " -- " + xDouble + " - " + d.getY());
			if (!databaseListData.contains(date) && !dateToAdd.contains(date)){
				dateToAdd.add(date);
			}
		}
		
		for (String s : dateToAdd){
			transformationDB.addDateOfTraffic(s, -1);
		}
		transformationDB.close();
	}
	
	private String getDayFromDate(String timeOfMeasurement, String dateFormat, String applyPattern){
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		try {
			Date today = sdf.parse(timeOfMeasurement);
			sdf.applyPattern(applyPattern);
			
			return sdf.format(today);

		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		return "";
	}

	private static double convertTimeToDouble(String fullTime, String dateFormat, String applyPattern) {
		SimpleDateFormat fullDate = new SimpleDateFormat(dateFormat);
		SimpleDateFormat timeDate = new SimpleDateFormat(applyPattern);

		try {
			Date now = fullDate.parse(fullTime);
			String strDate = timeDate.format(now);
			double parseDate = Double.parseDouble(strDate);
			if (parseDate >= 24.00d)
				return parseDate - 24;
			return parseDate;

		} catch (ParseException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		
		return Double.parseDouble("00.00");
	}
}

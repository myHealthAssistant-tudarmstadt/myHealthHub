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

package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments;

import android.support.v4.app.Fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.graphActivities.GraphPlotBigActivity;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.CookTraffic;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDBMS;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.TrafficData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GraphPlotFragment extends Fragment {

	private static final String TAG = GraphPlotFragment.class.getSimpleName();
	private View rootView;

	private ArrayList<GraphViewData> data_line;
	private ArrayList<GraphViewData> data_bar;

	private ArrayList<String> avalDate;

	public static String firstGrpTitle = "";
	public static String secondGrpTitle = "";
	private int firstGrpType = -1;
	private int secondGrpType = -1;
	private boolean isFirstBar = false;
	private boolean isSecondBar = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_with_graph, container,
				false);

		Log.e(TAG, ": onCreateView");

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		data_line = new ArrayList<GraphViewData>();
		data_bar = new ArrayList<GraphViewData>();

		CheckBox cBox = (CheckBox) rootView.findViewById(R.id.start_record);
		cBox.setVisibility(View.GONE);

		cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// startRecording(isChecked);
				Log.e(TAG, "Recording sensors Evt = " + isChecked);
			}
		});

		Button refrBtn = (Button) rootView.findViewById(R.id.refresh_btn);
		refrBtn.setVisibility(View.GONE);
		refrBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CookTraffic mCook = new CookTraffic(getActivity().getApplicationContext());
				mCook.cookUp();
//				clearAllCharts();
//				clearDb(((TextView) rootView.findViewById(R.id.at_date))
//						.getText().toString());
			}
		});

		TextView atDate = (TextView) rootView.findViewById(R.id.at_date);
		atDate.setText(getCurrentDate());

		Button backBtn = (Button) rootView.findViewById(R.id.date_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dateBackAndForth(true);
			}
		});

		Button forthBtn = (Button) rootView.findViewById(R.id.date_next);
		forthBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dateBackAndForth(false);

			}
		});

		
		updateTitleAndType();
		
		data_line = updateTrafficOnDate(getCurrentDate(), firstGrpType);
		data_bar = updateTrafficOnDate(getCurrentDate(), secondGrpType);
		redrawCharts();

		LinearLayout layout_light = (LinearLayout) rootView
				.findViewById(R.id.light_graph);
		LinearLayout layout_motion = (LinearLayout) rootView
				.findViewById(R.id.motion_graph);

		layout_light.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openBig();
			}
		});
		layout_motion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openBig();
			}
		});
	}

	private void openBig() {
		Intent i = new Intent(this.getActivity().getApplicationContext(),
				GraphPlotBigActivity.class);
		String date = ((TextView) rootView.findViewById(R.id.at_date))
				.getText().toString();
		i.putExtra("Timy", date);
		i.putExtra("lineGraphTitle", firstGrpTitle);
		i.putExtra("lineGraphType", firstGrpType);
		i.putExtra("isFirstBarType", isFirstBar);
		i.putExtra("barGraphTitle", secondGrpTitle);
		i.putExtra("barGraphType", secondGrpType);
		i.putExtra("isSecondBarType", isSecondBar);
		this.getActivity().startActivity(i);

	}
	
	private void updateTitleAndType(){

		ArrayList<String> grpTitle = new ArrayList<String>();
		ArrayList<Integer> grpType = new ArrayList<Integer>();
		ArrayList<Boolean> grpBarType = new ArrayList<Boolean>();
		
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getApplicationContext());

		if (pref.getBoolean(getResources().getString(R.string.in_hum), false)){
			grpTitle.add("Humidity");
			grpType.add(Sensor.TYPE_RELATIVE_HUMIDITY);
			grpBarType.add(false);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.in_lig), false)){
			grpTitle.add("Light in lux/min");
			grpType.add(Sensor.TYPE_LIGHT);
			grpBarType.add(false);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.in_pres), false)){
			grpTitle.add("Pressure in min");
			grpType.add(Sensor.TYPE_PRESSURE);
			grpBarType.add(false);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.in_prox), false)){
			grpTitle.add("Proximity in min");
			grpType.add(Sensor.TYPE_PROXIMITY);
			grpBarType.add(false);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.in_tem), false)){
			grpTitle.add("Ambient Temperature in °C");
			grpType.add(Sensor.TYPE_AMBIENT_TEMPERATURE);
			grpBarType.add(false);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.pulse), false)){
			grpTitle.add("Heart Rate bpm");
			grpType.add(999);
			grpBarType.add(false);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.in_acc), false)){
			grpTitle.add("Motion Strength/min");
			grpType.add(Sensor.TYPE_ACCELEROMETER);
			grpBarType.add(true);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.in_mag), false)){
			grpTitle.add("Magnetic Field Strength/min");
			grpType.add(Sensor.TYPE_MAGNETIC_FIELD);
			grpBarType.add(true);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.in_grav), false)){
			grpTitle.add("Gravity Strength/min");
			grpType.add(Sensor.TYPE_GRAVITY);
			grpBarType.add(true);
			
		}

		if (pref.getBoolean(getResources().getString(R.string.in_gyrs), false)){
			grpTitle.add("Gyroscope Strength/min");
			grpType.add(Sensor.TYPE_GYROSCOPE);
			grpBarType.add(true);
			
		}
		if (pref.getBoolean(getResources().getString(R.string.in_lin_acc), false)){
			grpTitle.add("Linear Accelerometer Strength/min");
			grpType.add(Sensor.TYPE_LINEAR_ACCELERATION);
			grpBarType.add(true);
			
		}
		if (grpTitle.size() == 2 && grpType.size() == 2){
			firstGrpTitle = grpTitle.get(0);
			firstGrpType = grpType.get(0);
			isFirstBar = grpBarType.get(0);
			
			secondGrpTitle = grpTitle.get(1);
			secondGrpType = grpType.get(1);
			isSecondBar = grpBarType.get(1);
		} else if (grpTitle.size() == 1 && grpType.size() == 1){
			firstGrpTitle = grpTitle.get(0);
			firstGrpType = grpType.get(0);
			isFirstBar = grpBarType.get(0);
			
			secondGrpTitle = "";
			secondGrpType = -1;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void dateBackAndForth(boolean back) {
		updateTitleAndType();
		TextView atDate = (TextView) rootView.findViewById(R.id.at_date);
		String newDate = atDate.getText().toString();
		if (back) {
			newDate = getDate(-1, atDate.getText().toString());
		} else {
			newDate = getDate(1, atDate.getText().toString());
		}

		Log.e(TAG, "newDate: " + newDate); // FIXME
		data_line = updateTrafficOnDate(newDate, firstGrpType);
		data_bar = updateTrafficOnDate(newDate, secondGrpType);
		redrawCharts();
		atDate.setText(newDate);
	}

	private LocalTransformationDBMS transformationDB;

	private ArrayList<GraphView.GraphViewData> updateTrafficOnDate(String date,
			int type) {
		// initialize database
		Log.e(TAG, "date=" + date + " type:" + type);
		this.transformationDB = new LocalTransformationDBMS(getActivity()
				.getApplicationContext());
		transformationDB.open();
		ArrayList<TrafficData> list = transformationDB.getAllTrafficFromDate(
				date, type);
		ArrayList<GraphView.GraphViewData> data = new ArrayList<GraphView.GraphViewData>();
		for (TrafficData t : list) {
			GraphViewData x = new GraphViewData(t.getxValue(), t.getyValue());
			data.add(x);
		}
		transformationDB.close();

		return data;
	}

//	private void clearDb(String date) {
//		// clear DBs of a today date
//		// initialize database
//		Log.e(TAG, "clear date: " + date);
//		this.transformationDB = new LocalTransformationDBMS(getActivity()
//				.getApplicationContext());
//		transformationDB.open();
//		int n = transformationDB.deleteAllTrafficFromDate(date);
//		transformationDB.close();
//
//		Log.e(TAG, "number of deleted rows= " + n);
//	}

	private void createGraph(String graphTitle, GraphViewSeries series,
			int Rid, boolean isBarChart) {
		GraphView graphView = null;

		if (isBarChart) {
			graphView = new BarGraphView(getActivity().getApplicationContext(),
					graphTitle);
			// graphView.setVerticalLabels(new String[] { "high", "mid", "low"
			// });
			// graphView.setManualYAxisBounds(11.0d, 9.0d);
		} else
			graphView = new LineGraphView(
					getActivity().getApplicationContext(), graphTitle);

		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isValueX) {
				// make sure not have smth like 4:60 or 11:83 time frame!
				double whole = value;
				double fractionalPart = value % 1;
				double integralPart = value - fractionalPart;
				if (fractionalPart > 0.59) {
					whole = integralPart + 1.0d + (fractionalPart - 0.60);
				}
				
				if (isValueX) {
					// convert (double) hour.mm to hour:mm
					return new DecimalFormat("00.00").format(whole).replaceAll(
							"\\,", ":");
				}
				else {
					if (value > 1000){
						return "high";
					}
					return new DecimalFormat("#0.00").format(value);
				}
			}
		});

		// add data
		graphView.addSeries(series);
		graphView.setScrollable(false);
		// optional - activate scaling / zooming
		// graphView.setScalable(true);
		// optional - legend
		// graphView.setShowLegend(true);
		graphView.getGraphViewStyle().setNumVerticalLabels(3);
		graphView.getGraphViewStyle().setNumHorizontalLabels(3);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(80);

		LinearLayout layout = (LinearLayout) rootView.findViewById(Rid);
		layout.removeAllViews();
		layout.addView(graphView);
		rootView.postInvalidate();
	}

//	private void clearAllCharts() {
//		clearChart(firstGrpTitle);
//		clearChart(secondGrpTitle);
//	}

	private void clearChart(String title) {
		GraphViewData[] dataList = new GraphViewData[1];
		dataList[0] = new GraphViewData(0.0, 0.0);
		GraphViewSeries gvs_series = new GraphViewSeries(dataList);

		if (title.equals(firstGrpTitle)) {
			createGraph(firstGrpTitle, gvs_series, R.id.light_graph, isFirstBar);
			data_line = new ArrayList<GraphView.GraphViewData>();
		}
		if (title.equals(secondGrpTitle)) {
			createGraph(secondGrpTitle, gvs_series, R.id.motion_graph, isSecondBar);
			data_bar = new ArrayList<GraphView.GraphViewData>();
		}
	}

	private void redrawCharts() {
		if (data_line.size() > 0) {

			GraphViewData[] dataList = new GraphViewData[data_line.size()];
			for (int i = 0; i < data_line.size(); i++) {
				dataList[i] = data_line.get(i);
			}
			GraphViewSeries gvs_light = new GraphViewSeries(dataList);
			createGraph(firstGrpTitle, gvs_light, R.id.light_graph, isFirstBar);
		} else {
			clearChart(firstGrpTitle);
		}
		if (data_bar.size() > 0) {
			GraphViewData[] dataAcc = new GraphViewData[data_bar.size()];
			for (int i = 0; i < data_bar.size(); i++) {
				dataAcc[i] = data_bar.get(i);
			}
			GraphViewSeries gvs_acc = new GraphViewSeries(dataAcc);
			createGraph(secondGrpTitle, gvs_acc, R.id.motion_graph, isSecondBar);
		} else {
			clearChart(secondGrpTitle);
		}
	}

	/**
	 * 
	 * @param i
	 *            0, 1 or -1
	 * @return current, next or prev date
	 */
	private String getDate(int i, String currentDate) {
		avalDate = new ArrayList<String>();
		this.transformationDB = new LocalTransformationDBMS(getActivity()
				.getApplicationContext());
		transformationDB.open();
		avalDate = transformationDB.getAllAvalDate();
		transformationDB.close();
		if (avalDate.isEmpty())
			avalDate.add(getCurrentDate());

		int x = avalDate.indexOf(currentDate);
		if (x >= 0) {
			if (i == 1 && x < avalDate.size() - 1)
				return avalDate.get(x + i);

			if (i == -1 && x > 0)
				return avalDate.get(x + i);
		} else {
			if (i == 1) {
				return avalDate.get(avalDate.size() - 1); // last date
			}
			if (i == -1) {
				return avalDate.get(0); // last date
			}
		}
		return currentDate;

	}

	private static String getCurrentDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.graphActivities;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDBMS;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.TrafficData;
import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class GraphPlotBigActivity extends Activity {

	private static final String TAG = GraphPlotBigActivity.class
			.getSimpleName();

	private static boolean barType = true;

	private ArrayList<GraphViewData> data_line;
	private ArrayList<GraphViewData> data_bar;

	public static String lineGrpTitle = "";
	public static String barGrpTitle = "";
	private int lineGrpType = -1;
	private int barGrpType = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_with_graph_big);

		Log.e(TAG, ": onCreateView");

		data_line = new ArrayList<GraphViewData>();
		data_bar = new ArrayList<GraphViewData>();

		CheckBox cBox = (CheckBox) findViewById(R.id.start_record);
		cBox.setVisibility(View.GONE);

		cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// startRecording(isChecked);
				Log.e(TAG, "Recording sensors Evt = " + isChecked);
			}
		});

		Button refrBtn = (Button) findViewById(R.id.refresh_btn);
		refrBtn.setVisibility(View.GONE);
		refrBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearAllCharts();
			}
		});

		TextView atDate = (TextView) findViewById(R.id.at_date);
		Bundle extras = this.getIntent().getExtras();
		String time = getCurrentDate();
		if (extras != null){
			if (extras.containsKey("Timy")) {
				time = extras.getString("Timy");
			}
			
			if (extras.containsKey("lineGraphTitle"))
				lineGrpTitle = extras.getString("lineGraphTitle");
			
			if (extras.containsKey("lineGraphType"))
				lineGrpType = extras.getInt("lineGraphType");
			
			if (extras.containsKey("barGraphTitle"))
				barGrpTitle = extras.getString("barGraphTitle");
			
			if (extras.containsKey("barGraphType"))
				barGrpType = extras.getInt("barGraphType");
		}
		
		atDate.setText(time);

		Button backBtn = (Button) findViewById(R.id.date_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dateBackAndForth(true);
			}
		});

		Button forthBtn = (Button) findViewById(R.id.date_next);
		forthBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dateBackAndForth(false);

			}
		});

		data_line = updateTrafficOnDate(atDate.getText().toString(), lineGrpType);
		data_bar = updateTrafficOnDate(atDate.getText().toString(), barGrpType);
		redrawCharts();

	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore any keyboard or orientation changes.
		super.onConfigurationChanged(newConfig);
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
		TextView atDate = (TextView) findViewById(R.id.at_date);
		String newDate = atDate.getText().toString();
		if (back) {
			newDate = getDate(-1, atDate.getText().toString());
		} else {
			newDate = getDate(1, atDate.getText().toString());
		}

		Log.e(TAG, "newDate: " + newDate); // FIXME
		data_line = updateTrafficOnDate(newDate, lineGrpType);
		data_bar = updateTrafficOnDate(newDate, barGrpType);
		redrawCharts();
		atDate.setText(newDate);

	}

	private LocalTransformationDBMS transformationDB;

	private ArrayList<GraphView.GraphViewData> updateTrafficOnDate(String date,
			int type) {
		// initialize database
		Log.e(TAG, "date=" + date + " type:" + type);
		this.transformationDB = new LocalTransformationDBMS(
				this.getApplicationContext());
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

	private void clearAllCharts() {
		clearChart(lineGrpTitle);
		clearChart(barGrpTitle);
	}

	private void clearChart(String title) {
		GraphViewData[] dataList = new GraphViewData[1];
		dataList[0] = new GraphViewData(0.0, 0.0);
		GraphViewSeries gvs_series = new GraphViewSeries(dataList);

		if (title.equals(lineGrpTitle)) {
			createGraph(lineGrpTitle, gvs_series, R.id.light_graph, !barType, 0);
			
			
			data_line = new ArrayList<GraphView.GraphViewData>();
		}
		if (title.equals(barGrpTitle)) {
			createGraph(barGrpTitle, gvs_series, R.id.motion_graph, barType, 0);
			
			
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
				createGraph(lineGrpTitle, gvs_light, R.id.light_graph, !barType,
					dataList.length);
		} else {
			clearChart(lineGrpTitle);
		}
		if (data_bar.size() > 0) {
			GraphViewData[] dataAcc = new GraphViewData[data_bar.size()];
			for (int i = 0; i < data_bar.size(); i++) {
				dataAcc[i] = data_bar.get(i);
			}
			GraphViewSeries gvs_acc = new GraphViewSeries(dataAcc);
			createGraph(barGrpTitle, gvs_acc, R.id.motion_graph, barType,
					dataAcc.length);
		} else {
			clearChart(barGrpTitle);
		}
	}
	
	private GraphView createGraph(String graphTitle, GraphViewSeries series,
			int Rid, boolean isBarChart, int seriesSize) {
		
		Log.e(TAG, "create graph:" + graphTitle);
		GraphView graphView = null;

		if (isBarChart) {
			graphView = new BarGraphView(this.getApplicationContext(),
					graphTitle);
			((BarGraphView) graphView).setDrawValuesOnTop(false);
//			syncGraphViewList.set(0, graphView);
//			 graphView.setManualYAxisBounds(3.0d, 0.000d);
		} else {
			graphView = new LineGraphView(this.getApplicationContext(),
					graphTitle);
			((LineGraphView) graphView).setDrawDataPoints(false);
//			syncGraphViewList.set(1, graphView);
//			if(syncGraphViewList.size() > 1){
//				((LineGraphView) graphView).setSyncGraph(syncGraphViewList.get(0));
//				
//			}
		}
		
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
					// convert the X-Axis label from (double) hour.mm to
					// (String) "hour:mm"
					return new DecimalFormat("00.00").format(whole).replaceAll(
							"\\,", ":");
				}
				else {
					return new DecimalFormat("#0.00").format(value);
				}
			}
		});
		
		// add data
		graphView.addSeries(series);
		// set view port, start=2, size=10
//		graphView.setViewPort(0, 23);
		graphView.setScrollable(true);
		// optional - activate scaling / zooming
		 graphView.setScalable(true);
//		graphView.setHorizontalScrollBarEnabled(true);
		// optional - legend
		// graphView.setShowLegend(true);
//		graphView.getGraphViewStyle().setNumVerticalLabels(3);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(100);
		graphView.getGraphViewStyle().setNumHorizontalLabels(7);

		// graphView.getGraphViewStyle().setNumVerticalLabels(7);
		// graphView.setManualYAxisBounds(300.0d, 0.0d);
		// graphView.setVerticalLabels(new String[] { "hgh", "mid", "low" });
		// graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
		// @Override
		// public String formatLabel(double value, boolean isValueX) {
		// if (!isValueX) {
		// if (value < 200) {
		// return "low";
		// } else if (value < 400) {
		// return "mid";
		// } else {
		// return "hgh";
		// }
		// }
		// return null; // let graphview generate X-axis label for us
		// }
		// });

		LinearLayout layout = (LinearLayout) findViewById(Rid);
		layout.removeAllViews();
		layout.addView(graphView);
		
		return graphView;
	}

	private ArrayList<String> avalDate;
	/**
	 * 
	 * @param i 0, 1 or -1
	 * @return current, next or prev date
	 */
	private String getDate(int i, String currentDate){
		avalDate = new ArrayList<String>();
		this.transformationDB = new LocalTransformationDBMS(this
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

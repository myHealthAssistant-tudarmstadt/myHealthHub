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

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities.EditPreferences;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities.ManageXMLFiles;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities.PersonalActivity;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities.TransformationManagerActivity;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.EventGeneratorFragment;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.GraphFragment;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.InternalSensorListFragment;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.SensorConfigFragment;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.SensorConfigurationListFragment;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.SimpleEventsFragment;

public class MyHealthHubWithFragments extends FragmentActivity implements
		SensorConfigurationListFragment.OnTitleSelectedListener {

	// for debugging
	private static String TAG = "MyHealthHubWithFragments";
	private static boolean D = true;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	// Activity results
	private static int MANAGE_XML_FILES = 2000;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_health_hub);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			// Fragment fragment = new DummySectionFragment();
			// Bundle args = new Bundle();
			// args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position +
			// 1);
			// fragment.setArguments(args);
			// return fragment;

			Fragment fragment;
			Bundle args;

			switch (position) {
			case 0:
				fragment = (Fragment) new SensorConfigFragment();
				return fragment;

			case 1:
				fragment = new InternalSensorListFragment();
//				fragment = new SimpleEventsFragment();
				return fragment;

			case 2:
				fragment = new GraphFragment();
//				fragment = new EventGeneratorFragment();
				return fragment;

			default:
				fragment = new DummySectionFragment();
				args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
						position + 1);
				fragment.setArguments(args);
				return fragment;

			}
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section_sensor_config)
						.toUpperCase(l);
			case 1:
				return getString(R.string.title_section_internal_sensor)
						.toUpperCase(l);
			case 2:
				return getString(R.string.title_section_traffic_generator)
						.toUpperCase(l);
			case 3:
				return getString(R.string.title_section_status).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_my_health_hub_dummy, container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	/** Menu creation */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my_health_hub_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/** Menu methods */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		// Preferences:
		case R.id.men_preferences:
			Intent intent = new Intent(getApplicationContext(),
					EditPreferences.class);
			startActivity(intent);
			return true;

			// Personalization
		case R.id.men_pers:
			Intent pers_intent = new Intent(getApplicationContext(),
					PersonalActivity.class);
			startActivity(pers_intent);
			return true;

			// Manage XML files:
		case R.id.men_manage_xml:
			Intent xmlIntent = new Intent(getApplicationContext(),
					ManageXMLFiles.class);
			startActivityForResult(xmlIntent, MANAGE_XML_FILES);
			return true;

			// Manage Event Transformation 
		case R.id.men_transformation_manager:
    		Intent tm = new Intent(getApplicationContext(), TransformationManagerActivity.class);
    		startActivity(tm);
    		return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MANAGE_XML_FILES) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				if (data != null) {
					if (data.getBooleanExtra(ManageXMLFiles.MAPPING_CHANGED,
							true)) {
						if (D)
							Log.d(TAG, "Mapping file was changed.");
						// TODO rovingModule.reloadMACMapping();
					}

					if (data.getBooleanExtra(
							ManageXMLFiles.SENSING_RULES_CHANGED, true)) {
						if (D)
							Log.d(TAG, "Sensing rules were changed.");
						// TODO mEnvSensingBinder.reloadXMLRules();
					}
				}
				break;
			case Activity.RESULT_CANCELED:
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.
	 * SensorConfigurationListFragment
	 * .OnTitleSelectedListener#onTitleSelected(int)
	 */
	@Override
	public void onTitleSelected(int index) {
		// TODO Auto-generated method stub
		Log.i("testActivity", "index:_" + index);
	}

}
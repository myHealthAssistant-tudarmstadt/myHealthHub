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

import java.util.ArrayList;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author HieuHa
 * 
 */
public class PersonalActivity extends ListActivity {

	private static final String TAG = PersonalActivity.class.getSimpleName();
	private SharedPreferences preferences;
	private ArrayList<String> profile;
	private ArrayList<String> profile_type;
	private ArrayAdapter<String> mAdapter;
	private String empty;
	private QuickContactBadge qcb;
	private String PERSONAL_IC = "Personal_Image"; 

	@Override
	public void onCreate(Bundle saveInstances) {
		super.onCreate(saveInstances);
		setContentView(R.layout.personal_profile_list);

		preferences = getApplicationContext().getSharedPreferences("personal",
				Context.MODE_PRIVATE);

		profile = new ArrayList<String>();
		profile_type = new ArrayList<String>();
		
		String[] p = getResources().getStringArray(R.array.personal_field);
		for (String s : p){
			profile.add(s);
		}
		
		p = getResources().getStringArray(R.array.personal_field_type);
		for (String s : p){
			profile_type.add(s);
		}

		empty = getResources().getString(R.string.p_empty);

		mAdapter = new mArrayAdapter(this, getApplicationContext());
		mAdapter.addAll(profile);

		qcb = (QuickContactBadge) findViewById(R.id.quickContactBadge1);
		qcb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(
						Intent.ACTION_GET_CONTENT);
//						Intent.ACTION_PICK,
//						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				i.setType("image/*");
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
		
		String picPath = preferences.getString(PERSONAL_IC, null);
		if (picPath != null)
			setBitmap(picPath, false);
		setListAdapter(mAdapter);
	}

	private class mArrayAdapter extends ArrayAdapter<String> {
		private LayoutInflater mInflater;
		private Activity mActivity;

		public mArrayAdapter(Activity activity, Context ctx) {
			super(ctx, 0); // , R.layout.fragment_list_with_empty_container);
			mInflater = (LayoutInflater) LayoutInflater.from(activity);
			this.mActivity = activity;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.personal_profile_row, parent,
						false);
			} else {
				view = convertView;
			}

			final String key = getItem(position);

			TextView tv_title = (TextView) view.findViewById(R.id.txtTitle_3);
			TextView tv_data = (TextView) view.findViewById(R.id.txtData_3);
			TextView tv_type = (TextView) view.findViewById(R.id.tv_typ_3);

			tv_title.setText(profile.get(position));
			tv_data.setText(preferences.getString(profile.get(position), empty));
			tv_type.setText(profile_type.get(position));

			view.setTag(position);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					FragmentManager fm = mActivity.getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					Fragment prev = fm.findFragmentByTag("popup_input");
					if (prev != null) {
						ft.remove(prev);
					}
					int pos = Integer.parseInt(String.valueOf(arg0.getTag()));

					try {
						arg0.setBackgroundDrawable(mActivity.getResources()
								.getDrawable(android.R.color.holo_blue_light));
					} catch (Exception e) {
						Log.e(TAG, "setBackgroundDrawble exception: " + e);
					}
					new mDialog(arg0, key, pos).show(fm, "popup_input");
				}
			});

			return view;
		}
	}

	private class mDialog extends DialogFragment {
		private String title;
		private int pos;
		private View mView;

		public mDialog(View v, String title, int pos) {
			this.title = title;
			this.pos = pos;
			this.mView = v;
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			mView.setBackgroundDrawable(null);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			LayoutInflater layoutInflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View prompt = (View) layoutInflater.inflate(
					R.layout.popup_input, null);

			final Editor mEditor = preferences.edit();
			final EditText edTxt = (EditText) prompt.findViewById(R.id.et_un);
			TextView tvTxt = (TextView) prompt.findViewById(R.id.tv_typ);

			edTxt.setText(preferences.getString(profile.get(pos), empty));
			edTxt.setSelection(0, edTxt.getText().length());

			tvTxt.setText(profile_type.get(pos));

			switch (pos) {
			case 1: {
				edTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			}
			case 2: {
				edTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			}
			case 3: {
				edTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			}
			case 4: {
				((RelativeLayout) prompt.findViewById(R.id.simple_input))
						.setVisibility(View.GONE);
				((RadioGroup) prompt.findViewById(R.id.gender_group))
						.setVisibility(View.VISIBLE);
			}
			case 5: {
				edTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			}
			case 6: {
				edTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			}
			}

			final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			alertDialogBuilder
					.setView(prompt)
					.setTitle(title)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (((RadioGroup) prompt
											.findViewById(R.id.gender_group))
											.isShown()) {
										RadioButton maleButton = (RadioButton) prompt
												.findViewById(R.id.male);
										if (maleButton.isChecked()) {
											mEditor.putString(title, "male")
													.commit();
										} else {
											mEditor.putString(title, "female")
													.commit();
										}
										mAdapter.notifyDataSetChanged();
									} else {
										if (!edTxt.getText().toString()
												.isEmpty()) {
											mEditor.putString(title,
													edTxt.getText().toString())
													.commit();
											mAdapter.notifyDataSetChanged();
										}
									}
									dialog.dismiss();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

			final AlertDialog alert = alertDialogBuilder.create();
			edTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						try {
							alert.getWindow()
									.setSoftInputMode(
											WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						} catch (Exception e) {
							Log.e(TAG, "setSoftInputMode exception: " + e);
						}
					}
				}
			});
			return alert;
		}
	}

	private int RESULT_LOAD_IMAGE = 1 + 1 >> 23;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			super.onActivityResult(requestCode, resultCode, data);
			
//			Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
//			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), contactUri);
			
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			setBitmap(picturePath, true);
			// ImageView imageView = (ImageView) findViewById(R.id.imgView);
			// imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		}
	}
	
	private void setBitmap(String picPath, boolean commit){
		int size = getResources().getInteger(R.integer.personal_icon_size);
		if (qcb != null) {
			Bitmap b = getScaledBitmap(picPath, size, size);
			if (b != null)
			qcb.setImageBitmap(b);
			if (commit){
				Editor mEditor = preferences.edit();
				mEditor.putString(PERSONAL_IC, picPath).commit();
			}
		}
	}
	
	private Bitmap getScaledBitmap(String picturePath, int width, int height) {
	    BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
	    sizeOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(picturePath, sizeOptions);

	    int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

	    sizeOptions.inJustDecodeBounds = false;
	    sizeOptions.inSampleSize = inSampleSize;

	    return BitmapFactory.decodeFile(picturePath, sizeOptions);
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and
	        // width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will
	        // guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    return inSampleSize;
	}
}
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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.helper;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;

public class TransformatorListAdapter extends
		ArrayAdapter<TransformatorListItem> {

	private List<TransformatorListItem> items;

	public TransformatorListAdapter(Context context, int textViewResourceId,
			List<TransformatorListItem> objects) {
		super(context, textViewResourceId, objects);
		this.items = objects;
	}

	public static class ViewHolder {
		public TextView state;
		public TextView type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		ViewHolder viewHolder = null; 

		if (view == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.transformation_list_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.state = (TextView) view
					.findViewById(R.id.transformator_state);
			viewHolder.type = (TextView) view
					.findViewById(R.id.transformator_type);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		final TransformatorListItem currentRowItem = items.get(position);

		if (currentRowItem != null) {
			viewHolder.type.setText(currentRowItem.getTransformatorType());
			viewHolder.state.setText(currentRowItem.getState());
		}
		return view;

	}

}
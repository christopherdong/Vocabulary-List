package com.chris.vocabularylist;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

	private Activity activity;
	private ArrayList<WordObject> data;
	private LayoutInflater inflater;
	//private boolean checkRead = false;

	public CustomExpandableListAdapter(Activity a, ArrayList<WordObject> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (vi == null) {
			vi = inflater.inflate(R.layout.expand_list_child, parent, false);
		}

		TextView txtType = (TextView) vi.findViewById(R.id.txt_type);
		TextView txtDefinition = (TextView) vi
				.findViewById(R.id.txt_definition);
		TextView txtExample = (TextView) vi.findViewById(R.id.txt_example);

		String type = data.get(groupPosition).getWordtype().toString();
		String definition = data.get(groupPosition).getWord().toString()
				+ " means "
				+ data.get(groupPosition).getDefinition().toString();
		String example = data.get(groupPosition).getExample().toString();

		txtType.setText(type);
		txtDefinition.setText(definition);
		txtExample.setText(example);
		
		
		final String readText;
		
		readText = data.get(groupPosition).getWord().toString() + " means "
				+ data.get(groupPosition).getDefinition().toString() + ",  "
				+ data.get(groupPosition).getExample().toString();
		
		ImageButton btnRedeem = (ImageButton) vi.findViewById(R.id.btn_speak2);
		btnRedeem.setFocusable(false);

		btnRedeem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ListVocab.soundQ(readText);
			}
		});
		
		

		return vi;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub

		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (vi == null) {
			vi = inflater.inflate(R.layout.expand_list_parent, parent, false);
		}

		TextView txtVoucherValue = (TextView) vi
				.findViewById(R.id.txt_voucher_value);
		int count = data.get(groupPosition).getID();
		final String word = data.get(groupPosition).getWord().toString();
		txtVoucherValue.setText(count + "  " + word);

		ImageButton btnRedeem = (ImageButton) vi.findViewById(R.id.btn_speak);
		btnRedeem.setFocusable(false);

		btnRedeem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ListVocab.soundQ(word);
			}
		});

		return vi;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

}

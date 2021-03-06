package com.valleskeyp.androidproject1;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SecondFragment extends Fragment {
	
	private SecondListener listener;
	
	public interface SecondListener {
		public void onListClick(int position);
	}
	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.second,	container, false);
		
		final ListView _listView = (ListView) view.findViewById(R.id.listView1);
		
		_listView.setOnItemClickListener(new OnItemClickListener() {
			// when user clicks a row, it saves the title of the position clicked
			// then it executes the finish method
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listener.onListClick(position);
			}

		});
		
		return view;
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			listener = (SecondListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SecondListener");
		}
	}
}

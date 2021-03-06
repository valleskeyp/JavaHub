package com.valleskeyp.androidproject1;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainFragment extends Fragment {
	
	private MainListener listener;
	
	public interface MainListener {
		public void onRecentSelect(String recentTitle);
		public void onSearchGo(String searchedTitle);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.main_view,	container, false);
		
		//Get elements from XML Layouts
        Button fieldButton = (Button) view.findViewById(R.id.edit_button);
        TextView _textField = (TextView) view.findViewById(R.id.text_view);
        Spinner _recentsList = (Spinner) view.findViewById(R.id.recents_list);
        _recentsList.setVisibility(View.INVISIBLE);

        
        //setup scrolling on textview
        _textField.setMovementMethod(new ScrollingMovementMethod());
        
		//setup recents listener
        _recentsList.setOnItemSelectedListener(new OnItemSelectedListener() {
        	
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
        		Log.i("RECENT SELECTED", "POSITION: " + pos + "\r\nID: " + id);
        		String str = parent.getItemAtPosition(pos).toString();
        		
        		//Gets the selected title from memory and displays it
        		if (!(pos == 0)) {
        			listener.onRecentSelect(str);
				}
        	}
        	
        	@Override
        	public void onNothingSelected(AdapterView<?> parent) {
        		Log.i("RECENT SELECTED", "NO SELECTION");
        	}
		});
        
        //Search button listener for search bar
        fieldButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//gets edittext from xml
				EditText field = (EditText) getActivity().findViewById(R.id.edit_field);
				//hide keyboard after button press
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
				String str = field.getText().toString().trim();
				listener.onSearchGo(str);
			}
		});
		
		return view;
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			listener = (MainListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement MainListener");
		}
	}
}

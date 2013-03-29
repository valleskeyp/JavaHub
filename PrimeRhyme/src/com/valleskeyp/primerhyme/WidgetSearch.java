package com.valleskeyp.primerhyme;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WidgetSearch extends Activity {
	private ResponseReceiver receiver;
	
	private ListView _widgetList;
	ArrayList<String> _list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_search);
		Typeface font = Typeface.createFromAsset(getAssets(), "FultonsHand_Regular.ttf");
		TextView txt = (TextView) findViewById(R.id.search_title);
		txt.setTypeface(font);
		
		final EditText textView = (EditText ) findViewById(R.id.searchText);
		textView.requestFocus();
		textView.setTypeface(font);
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		_widgetList = (ListView) findViewById(R.id.widgetList);
		Button searchButton = (Button) findViewById(R.id.searchGo);
		searchButton.setTypeface(font);
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// search for rhyming words
			    if (TextUtils.isEmpty(textView.getText().toString())) {
					Toast.makeText(WidgetSearch.this, "Please enter a word to rhyme", Toast.LENGTH_LONG).show();
			    } else {
			    	getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			    	
			    	InputMethodManager im = (InputMethodManager) WidgetSearch.this.getSystemService(Context.INPUT_METHOD_SERVICE);
			    	im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			    	
			    	Toast.makeText(WidgetSearch.this, "Searching words that rhyme with " + textView.getText().toString() + "...", Toast.LENGTH_LONG).show();
			    	
			    	// clear out listview, then begin search process
			    	_widgetList.setAdapter(null);
			    	
			    	Intent msgIntent = new Intent(WidgetSearch.this, RhymeService.class);
			    	msgIntent.putExtra("word", textView.getText().toString());
			    	startService(msgIntent);
			    }
			}
		});
	}
	
	public class ResponseReceiver extends BroadcastReceiver {
		public static final String RHYME_RESPONSE = "com.valleskeyp.intent.action.RHYME_RESPONSE";
		@Override
		public void onReceive(Context context, Intent intent) {
			_list = new ArrayList<String>();
			int check = 0;
			if(intent.getAction().equals(RHYME_RESPONSE)) {
				String data = intent.getStringExtra("rhymeList");
				try {
					JSONArray json = new JSONArray(data);
					
					for (int i = 0; i < json.length(); i++) {
						JSONObject word = json.getJSONObject(i);
						int myNum = 0;
						try {
						    myNum = Integer.parseInt(word.getString("score"));
						} catch(NumberFormatException nfe) {}
						if (word.getString("flags").contains("a")) {
							// word is considered offensive, skip it
							Log.i("PRIME_RHYME", word.getString("word"));
							Log.i("PRIME_RHYME", word.getString("flags"));
							Log.i("PRIME_RHYME", "----OFFENSIVE_WORD-----");
						} else if (myNum > 299) {
							// skips words that aren't perfect rhymes
							Log.i("PRIME_RHYME", word.getString("word"));
							Log.i("PRIME_RHYME", word.getString("score"));
							Log.i("PRIME_RHYME", "---------------");
							check++;
							_list.add(word.getString("word"));
						}
					}
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				if (check == 0) {
					_list.add("No rhyming matches found");
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(WidgetSearch.this, android.R.layout.simple_list_item_1, android.R.id.text1, _list) {
					public View getView(int position, View v, android.view.ViewGroup parent) {
						TextView textView = (TextView) super.getView(position, v, parent);
						Typeface font = Typeface.createFromAsset(getAssets(), "FultonsHand_Regular.ttf");
			            textView.setTypeface(font);
						
						return textView;
					};
				};
				_widgetList.setAdapter(adapter);
			}
			
		}
		
	}
	
	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(ResponseReceiver.RHYME_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);
        
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		
		super.onPause();
	}
	
}

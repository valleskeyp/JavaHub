package com.valleskeyp.androidproject1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.valleskeyp.lib.FileStuff;
import com.valleskeyp.lib.FormMethods;
import com.valleskeyp.lib.WebStuff;

import android.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	Boolean _connected = false;
	Context _context;
	TextView _textField;
	String _movieTitle;
	
	HashMap<String, String> _recent = new HashMap<String, String>();
	Spinner _recentsList;
	ArrayList<String> _recentTitle = new ArrayList<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black);
        
        TextView tView = (TextView)getLayoutInflater().inflate(R.layout.txtview, null);
        _context = this;
        getAndUpdate();
                
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        
        //Create layouts from FormMethods
        LinearLayout entryBox = FormMethods.textEntryWithSideButton(this, "Movie Name", "Go");
        LinearLayout textView = FormMethods.textView(this, "View movie information");
        LinearLayout recentsList = FormMethods.RecentDisplay(this);
        
        //Get elements from those layouts
        Button fieldButton = (Button) entryBox.findViewById(2);
        _textField = (TextView) textView.findViewById(1);
        _recentsList = (Spinner) recentsList.findViewById(1);
        
        //setup scrolling on textview
        _textField.setMovementMethod(new ScrollingMovementMethod());
        
        //setup recents list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, _recentTitle);
        listAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        _recentsList.setAdapter(listAdapter);
        
        //setup recents listener
        _recentsList.setOnItemSelectedListener(new OnItemSelectedListener() {
        	
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
        		Log.i("RECENT SELECTED", "POSITION: " + pos + "\r\nID: " + id);
        		String str = parent.getItemAtPosition(pos).toString();
        		
        		//Gets the selected title from memory and displays it
        		if (!(pos == 0)) {
        			String movieString = _recent.get(str);
        			try {
        				JSONObject json = new JSONObject(movieString);
        				_textField.setText("\r\nTitle: " + json.getString("title") + "\r\n\r\nRating: " + json.getString("mpaa_rating") + "\r\n\r\nCritics Consensus: " + json.getString("critics_consensus") + "\r\n\r\nSynopsis: " + json.getString("synopsis"));
        				} catch (JSONException e) {
        					Log.e("JSON", "JSON OBJECT EXCEPTION");
        			}
				}
        	}
        	
        	@Override
        	public void onNothingSelected(AdapterView<?> parent) {
        		Log.i("RECENT SELECTED", "NO SELECTION");
        	}
		});
        fieldButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//gets edittext tag from button reference
				EditText field = (EditText) v.getTag();
				//hide keyboard after button press
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
				_movieTitle = field.getText().toString().trim();
				//check network access first, then begin process of getting api data with field string
				_connected = WebStuff.getConnectionStatus(_context);
				if (_connected) {
					//have connection, now send API request
					getMovie(field.getText().toString().trim().replace(" ", "+"));
				} else {
					//no connection.
					_textField.setText("Sorry, unable to search without a working connection.\n\rPlease connect and try again.");
				}
			}
		});
        // add layouts generated from the FormMethods class
        ll.addView(entryBox);
        ll.addView(textView);
        ll.addView(recentsList);
        
        setContentView(ll);
    }
    
    private void updateRecents() {
    	//_recentTitle
    	
    	for(String key : _recent.keySet()) {
    		_recentTitle.add(key);
    	}
    }
    
    @SuppressWarnings("unchecked")
	private HashMap<String, String> getRecents() {
    	Object stored = FileStuff.ReadObjectFile(_context, "recent", false);
    	
    	HashMap<String, String> recents;
    	if (stored == null) {
			Log.i("RECENTS", "NO RECENTS FOUND");
			recents = new HashMap<String, String>();
		} else {
			recents = (HashMap<String, String>) stored;
		}
    	return recents;
    }
    
    //makes sure that whenever the _recent list gets updated, the spinner updates as well
    private void getAndUpdate() {
    	if (!_recentTitle.isEmpty()) {
    		_recentTitle.clear();
		}
    	_recentTitle.add("View recent movies.");
    	_recent = getRecents();
    	updateRecents();
    }
    private void getMovie(String name) {
    	String rtURL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=q9gq656wf8xzsacnfza7ndtm&q="+name;
    	URL finalURL;
    	try {
			finalURL = new URL(rtURL);
			MovieRequest mq = new MovieRequest();
			mq.execute(finalURL);
		} catch (MalformedURLException e) {
			Log.e("BAD URL", "MALFORMED URL");
			finalURL = null;
		}
    }
    
    private class MovieRequest extends AsyncTask<URL, Void, String> {
    	protected String doInBackground(URL... urls) {
    		String response = "";
    		for (URL url: urls) {
				response = WebStuff.APICall(url);
			}
    		return response;
    	};
    	@Override
    	protected void onPostExecute(String result) {
    		try {
    			boolean didFind = false;
    			JSONObject json = new JSONObject(result);
    			if (json.getString("total").compareTo("0")==0) {
					Toast toast = Toast.makeText(_context, "Invalid Movie", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 70);
					toast.show();
				} else {
					JSONArray ary = json.getJSONArray("movies");
					for (int i = 0; i < ary.length(); i++) {
						JSONObject tmp = ary.getJSONObject(i);
						Log.i("MOVIE NAME",tmp.getString("title"));
						if ((tmp.getString("title")).equalsIgnoreCase(_movieTitle)) {
							didFind = true;
							JSONObject movieObject = json.getJSONArray("movies").getJSONObject(i);
			    			_textField.setText("\r\nTitle: " + movieObject.getString("title") + "\r\n\r\nRating: " + movieObject.getString("mpaa_rating") + "\r\n\r\nCritics Consensus: " + movieObject.getString("critics_consensus") + "\r\n\r\nSynopsis: " + movieObject.getString("synopsis"));
			    			
			    			_recent.put(movieObject.getString("title"), movieObject.toString());
			    			FileStuff.storeObjectFile(_context, "recent", _recent, false);
			    			getAndUpdate();
						}
					}
				}
    			if (didFind == false) {
    				JSONObject movieObject = json.getJSONArray("movies").getJSONObject(0);
	    			_textField.setText("\r\nTitle: " + movieObject.getString("title") + "\r\n\r\nRating: " + movieObject.getString("mpaa_rating") + "\r\n\r\nCritics Consensus: " + movieObject.getString("critics_consensus") + "\r\n\r\nSynopsis: " + movieObject.getString("synopsis"));
	    			
	    			_recent.put(movieObject.getString("title"), movieObject.toString());
	    			FileStuff.storeObjectFile(_context, "recent", _recent, false);
	    			getAndUpdate();
    			}
			} catch (JSONException e) {
				Log.e("JSON", "JSON OBJECT EXCEPTION");
			}
    		
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    // q9gq656wf8xzsacnfza7ndtm rotten tomatoes API key
}

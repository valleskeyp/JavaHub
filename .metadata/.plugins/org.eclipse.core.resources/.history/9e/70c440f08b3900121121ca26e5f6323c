package com.valleskeyp.androidproject1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.valleskeyp.lib.FileStuff;
import com.valleskeyp.lib.WebStuff;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	Boolean _connected = false;
	Context _context;
	TextView _textField;
	String _movieTitle;
	ImageButton _imageButton;
	
	HashMap<String, String> _recent = new HashMap<String, String>();
	Spinner _recentsList;
	ArrayList<String> _recentTitle = new ArrayList<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black);
        setContentView(R.layout.main_view);
        
        _movieTitle = "";
        _context = this;
        getAndUpdate();
                
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        
        //Get elements from layouts
        Button fieldButton = (Button) findViewById(R.id.edit_button);
        _textField = (TextView) findViewById(R.id.text_view);
        _recentsList = (Spinner) findViewById(R.id.recents_list);
        _imageButton = (ImageButton) findViewById(R.id.imageButton1);
        _imageButton.setBackgroundColor(Color.BLACK);
        
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
        				_movieTitle = json.getString("title");
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
				EditText field = (EditText) findViewById(R.id.edit_field);
				//hide keyboard after button press
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
				_movieTitle = field.getText().toString().trim();
				//check network access first, then begin process of getting api data with field string
				_connected = WebStuff.getConnectionStatus(_context);
				if (_connected) {
					if (_movieTitle.equals("")) {
						return;
					}
					Toast toast = Toast.makeText(_context, "Attempting to retrieve movie data", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 70);
					toast.show();
					//have connection, now send API request
					getMovie(field.getText().toString().trim().replace(" ", "+"));
				} else {
					//no connection.
					_textField.setText("Sorry, unable to search without a working connection.\n\rPlease connect and try again.");
				}
			}
		});
        _imageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//open movie website with implicit intent
				if (!(_movieTitle.equals(""))) {
					String str = _movieTitle.replace("The", "").trim().replace(" ", "_");
					Uri uri = Uri.parse("http://www.rottentomatoes.com/mobile/m/" + str + "/");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
			}
		});
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
    			JSONObject json = new JSONObject(result);
    			if (json.getString("total").compareTo("0")==0) {
					Toast toast = Toast.makeText(_context, "Invalid Movie", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 70);
					toast.show();
				} else {
					//open SecondView and pass along the JSONdata
					Intent i = new Intent(_context, SecondView.class);
					i.putExtra("JSONdata", result);
					startActivityForResult(i, 1);
				}
			} catch (JSONException e) {
				Log.e("JSON", "JSON OBJECT EXCEPTION");
			}
    	}
    }
    //get the Intent data back from SecondView and use it to display the chosen movie and save it to recents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode == RESULT_OK) {
    		//"title" of chosen movie, "result" of json data to parse below
    		String title = (String) data.getExtras().getString("title");
    		String result = (String) data.getExtras().getString("result");
    		
    		try {
    			JSONObject json = new JSONObject(result);
    			JSONArray ary = json.getJSONArray("movies");
    			for (int i = 0; i < ary.length(); i++) {
					JSONObject tmp = ary.getJSONObject(i);
					if ((tmp.getString("title")).equalsIgnoreCase(title)) {
						JSONObject movieObject = json.getJSONArray("movies").getJSONObject(i);
						_movieTitle = movieObject.getString("title");
		    			_textField.setText("\r\nTitle: " + movieObject.getString("title") + "\r\n\r\nRating: " + movieObject.getString("mpaa_rating") + "\r\n\r\nCritics Consensus: " + movieObject.getString("critics_consensus") + "\r\n\r\nSynopsis: " + movieObject.getString("synopsis"));
		    			
		    			_recent.put(movieObject.getString("title"), movieObject.toString());
		    			FileStuff.storeObjectFile(_context, "recent", _recent, false);
		    			getAndUpdate();
					}
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

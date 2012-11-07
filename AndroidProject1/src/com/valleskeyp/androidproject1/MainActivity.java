package com.valleskeyp.androidproject1;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.valleskeyp.lib.FormMethods;
import com.valleskeyp.lib.WebStuff;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	Boolean _connected = false;
	Context _context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = this;
        
        LinearLayout entryBox = FormMethods.textEntryWithSideButton(this, "Movie Name", "Go");
        LinearLayout textView = FormMethods.textView(this, "View movie information");
       
        LinearLayout ll = new LinearLayout(this);
        Button fieldButton = (Button) entryBox.findViewById(2);
        final TextView textField = (TextView) textView.findViewById(1);
        
        fieldButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//gets edittext tag from button reference
				EditText field = (EditText) v.getTag();
				Log.i("Button clicked: ", field.getText().toString());
				//check network access first, then begin process of getting api data with field string
				_connected = WebStuff.getConnectionStatus(_context);
				if (_connected) {
					//have connection, now send API request
					getMovie(field.getText().toString().trim().replace(" ", "+"));
				} else {
					//no connection.
					textField.setText("Sorry, unable to search without a working connection.\n\rPlease connect and try again.");
				}
			}
		});
        // add layouts generated from the FormMethods class
        ll.addView(entryBox);
        ll.addView(textView);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);
    }

    private void getMovie(String name) {
    	String rtURL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=q9gq656wf8xzsacnfza7ndtm&q="+name+"&page_limit=1";
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
					toast.show();
				} else {
    			JSONObject movieObject = json.getJSONArray("movies").getJSONObject(0);
    			
    			Log.i("INFO","\r\nTitle: " + movieObject.getString("title") + "\r\nRating: " + movieObject.getString("mpaa_rating") + "\r\nCritics Consensus: " + movieObject.getString("critics_consensus") + "\r\nSynopsis: " + movieObject.getString("synopsis"));
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

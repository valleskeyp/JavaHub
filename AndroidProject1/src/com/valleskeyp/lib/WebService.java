package com.valleskeyp.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.valleskeyp.androidproject1.MainActivity.ResponseReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class WebService extends IntentService {
	public static final String MOVIE_REQUEST = "movieRequest";
	public static final String MOVIE_RESULT = "movieResult";
	
	public WebService() {
		super("WebService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String title = intent.getStringExtra(MOVIE_REQUEST);
		StringBuilder builder = new StringBuilder();
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet("http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=q9gq656wf8xzsacnfza7ndtm&q="+title);
	    try {
	      HttpResponse response = client.execute(httpGet);
	      StatusLine statusLine = response.getStatusLine();
	      int statusCode = statusLine.getStatusCode();
	      if (statusCode == 200) {
	        HttpEntity entity = response.getEntity();
	        InputStream content = entity.getContent();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	        String line;
	        while ((line = reader.readLine()) != null) {
	          builder.append(line);
	        }
	      } else {
	        Log.e(WebService.class.toString(), "Failed to download file");
	      }
	    } catch (ClientProtocolException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    String movieResult = builder.toString();

	    //broadcast here
	    Intent broadcastIntent = new Intent();
	    broadcastIntent.setAction(ResponseReceiver.ACTION_RESPONSE);
	    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
	    broadcastIntent.putExtra(MOVIE_RESULT, movieResult);
	    sendBroadcast(broadcastIntent);
	}

}

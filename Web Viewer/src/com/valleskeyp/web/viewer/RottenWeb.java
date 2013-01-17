package com.valleskeyp.web.viewer;

import java.net.URL;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class RottenWeb extends Activity implements SensorEventListener{
	private SensorManager _sensorManager;
	private long _updated;
	
	WebView _webView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        WebViewClient yourWebClient = new WebViewClient()
        {
            // only allow rottentomatoes webpages
            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url)
            {
             if ( url.contains("rottentomatoes") == true )
                // don't override URL Link
                return false;
              
             // Return true to override url loading
             return true;
            }
        };
        
        
        // get and set webview
        _webView = (WebView) findViewById( R.id.webView ); 
        _webView.getSettings().setJavaScriptEnabled(true);   
        _webView.getSettings().setSupportZoom(true);      
        _webView.getSettings().setBuiltInZoomControls(true);
        _webView.setWebViewClient(yourWebClient);
        
        // get intent data
        Intent intent = getIntent();
        
        Uri data = intent.getData();
        URL url = null;
        try {
			url = new URL(data.getScheme(), data.getHost(), data.getPath());
		} catch (Exception e) {
			Log.i("URL ERROR", "ERROR WITH INTENT URL");
		}
        
        // Load URL from intent
        _webView.loadUrl(url.toString());
        
        _sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        _updated = System.currentTimeMillis();
    }
    @Override
    public void onBackPressed() {
        WebView webView1 = (WebView) findViewById(R.id.webView);
        // If there is history in the web view, go back
        if (webView1.canGoBack()) {
            webView1.goBack();
            return;
        }
        //Handle anything else
        super.onBackPressed();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// Allow class to listen to the accelerometer when app starts
    	_sensorManager.registerListener(this, _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	// Stop listening to the accelerometer when app is not in use
    	_sensorManager.unregisterListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

 // Code to act on accelerometer changes
    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		// Do nothing	
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// When sensor adequately changes, run method to switch orientation
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
		{
		      setOrientation(event);
		}
		
	}

	private void setOrientation(SensorEvent event) {
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if (accelationSquareRoot >= 3) //
		{
			if (actualTime - _updated < 200) {
				return;
			}
			_updated = actualTime;
			int orientation = getResources().getConfiguration().orientation;
			
			if (orientation == 1) {
				// currently portrait, change to landscape
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE	);
			} else {
				// currently landscape, change to portrait
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
		
	}
}

package com.valleskeyp.web.viewer;

import java.net.URL;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.VideoView;

@SuppressLint("SetJavaScriptEnabled")
public class RottenWeb extends Activity implements SensorEventListener {
	private SensorManager _sensorManager;
	private long _updated;
	
	MediaPlayer _mediaPlayer;
	
	WebView _webView;
	Context _context;
	String _url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _context = this;
        
        
        WebViewClient yourWebClient = new WebViewClient()
        {
        	// only allow rottentomatoes webpages
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView  view, String  url)
        	{
        		_url = url;
        		if (_mediaPlayer != null) {
        			_mediaPlayer.release();
        			_mediaPlayer = null;
				}
        		if ( url.contains("rottentomatoes") == true )
        		{
        			// don't override URL Link
        			return false;
        		} else if (url.contains("videodetective") == true) {
        			ConnectivityManager connMgr = (ConnectivityManager) 
        			        getSystemService(Context.CONNECTIVITY_SERVICE);
        			NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
        			boolean isWifiConn = networkInfo.isConnected();
        			if (isWifiConn) {
        				loadVideo();
        			} else {
        				AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        				builder.setMessage("Continue loading video?")
        				.setCancelable(false)
        				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int id) {
        						loadVideo();
        					}
        				})
        				.setNegativeButton("No", new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int id) {
        						dialog.cancel();
        					}
        				});
        				AlertDialog alert = builder.create();
        				alert.show();
        			}
        			return true;
				}

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
    
    public void loadVideo() {
    	try
		{
			_mediaPlayer = new MediaPlayer();
			_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			_mediaPlayer.setDataSource(_url);
			_mediaPlayer.prepare(); // might take long! (for buffering, etc)
			_mediaPlayer.start();
		}
		catch(Exception e)
		{
			Log.e("VIDEO EXCEPTION", "VIDEO UNABLE TO LOAD OR PLAY: " + e);
		}
    }
    
    @Override
    public void onBackPressed() {
        WebView webView1 = (WebView) findViewById(R.id.webView);
        // If there is history in the web view, go back
        if (webView1.canGoBack()) {
            webView1.goBack();
            _mediaPlayer.release();
            _mediaPlayer = null;
            return;
        }
        //Handle anything else
        _mediaPlayer.release();
        _mediaPlayer = null;
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

    
 ///////////////////////////////////////////////////////////// 
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

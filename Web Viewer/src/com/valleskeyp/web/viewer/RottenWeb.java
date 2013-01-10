package com.valleskeyp.web.viewer;

import java.net.URL;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class RottenWeb extends Activity {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}

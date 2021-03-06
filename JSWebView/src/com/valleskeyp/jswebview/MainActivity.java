package com.valleskeyp.jswebview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends Activity {
	WebView webview;
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webView);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/index.html");
        webview.addJavascriptInterface(new WebAppInterface(this), "Android");
        
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /* Javascript classes here */
        
        public void getData(String title, String month, String day, String location, String description) {
        	if (title.toString().equals("")) {
        		Toast.makeText(mContext, "Please enter a title", Toast.LENGTH_SHORT).show();
				return;
			} else if (day.toString().equals("") || month.toString().equals("")) {
				Toast.makeText(mContext, "Please enter a date", Toast.LENGTH_SHORT).show();
				return;
			} else {
				int monthInt;
				int dayInt;
				try {
					  	monthInt = Integer.parseInt(month.trim()) - 1;
					  	dayInt = Integer.parseInt(day.trim());
					} catch (NumberFormatException e) {
					  //  invalid entry
						Toast.makeText(mContext, "Please enter a valid date", Toast.LENGTH_SHORT).show();
						return;
				}
				try {
					if (month.length() < 2) {
						month = "0" + month;
					}
					if (day.length() < 2) {
						day = "0" + day;
					}
					String str = "2013" + month.trim() + day.trim();
					SimpleDateFormat df = new SimpleDateFormat("yyyymmdd");
			        df.setLenient(false);
			        df.parse(str);
				} catch (java.text.ParseException e) {
					Toast.makeText(mContext, "Please enter a valid date", Toast.LENGTH_SHORT).show();
					return;
				}
				// Set intent to pass calendar data
				
				Intent intent = new Intent(Intent.ACTION_INSERT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra(Events.TITLE, title);
				intent.putExtra(Events.EVENT_LOCATION, location);
				intent.putExtra(Events.DESCRIPTION, description);

				// Setting dates
				Calendar calDate = new GregorianCalendar(2013, monthInt, dayInt);
				intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				  calDate.getTimeInMillis());
				intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				  calDate.getTimeInMillis());
				startActivity(intent);
				webview.loadUrl("javascript:clear()");
			}
        }
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}

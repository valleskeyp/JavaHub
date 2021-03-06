package com.valleskeyp.androidproject1;

import java.util.HashMap;

import com.valleskeyp.lib.FileStuff;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	public WidgetProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
Log.i("WIDGET TEST", "WIDGET UPDATE OCCURED");
		// Get all ids
	    ComponentName thisWidget = new ComponentName(context,
	        WidgetProvider.class);
	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    for (int widgetId : allWidgetIds) {

	      RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
	          R.layout.widget_layout);
	      
	      // Set the text
	      Object content = FileStuff.ReadObjectFile(context, "recent", false);
	      if (content == null) {
	    	  Log.i("RECENTS", "NO RECENTS FOUND");
	    	  remoteViews.setTextViewText(R.id.recentContent, "");
	      } else {
	    	  HashMap<String, String> recent = (HashMap<String, String>) content;
	    	  for(String key : recent.keySet()) {
	    		  remoteViews.setTextViewText(R.id.recentContent, key);
	    		  break;
	    	  }
	    	  
	      }

	      // Register an onClickListener
	      Intent searchIntent = new Intent(context, WidgetSearch.class);
	      PendingIntent pIntent = PendingIntent.getActivity(context, 0, searchIntent, 0);
	      
	      remoteViews.setOnClickPendingIntent(R.id.searchText, pIntent);
	      appWidgetManager.updateAppWidget(widgetId, remoteViews);
	    }
	}
}

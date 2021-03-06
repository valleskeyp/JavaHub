package com.valleskeyp.primerhyme;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	public WidgetProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		ComponentName thisWidget = new ComponentName(context,
		        WidgetProvider.class);
		
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			
			Intent searchIntent = new Intent(context, WidgetSearch.class);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, searchIntent, 0);
			
			remoteViews.setOnClickPendingIntent(R.id.widget_search, pIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
}

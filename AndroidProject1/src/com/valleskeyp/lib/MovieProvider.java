package com.valleskeyp.lib;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class MovieProvider extends ContentProvider {
	public static final String PROVIDER_NAME = "com.valleskeyp.AndroidProject1.provider";
	public static final Uri CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/title");
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.valleskeyp.AndroidProject1.provider", "", 1); // no title provided
		uriMatcher.addURI("com.valleskeyp.AndroidProject1.provider", "*", 2); // title provided from spinner
	}
	
	HashMap<String, String> _recent = new HashMap<String, String>();
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case 1:
		{
			return "com.valleskeyp.AndroidProject1.provider.recents";
		}
		case 2:
		{
			return "com.valleskeyp.AndroidProject1.provider.movie";
		}
		default:
			throw new IllegalArgumentException("UNSUPPORTED URI" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	//  When i fully implement query, then this replace getRecents() in MainActivity
	public boolean onCreate() {
		Context context = getContext();
		Object content = FileStuff.ReadObjectFile(context, "recent", false);
		if (content == null) {
			Log.i("RECENTS", "NO RECENTS FOUND");
			_recent = new HashMap<String, String>();
		} else {
			_recent = (HashMap<String, String>) content;
		}
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		MatrixCursor result = new MatrixCursor(new String[] {"_id","title","rating","critics","synopsis"});
		// if 1 returned, then the query is to set the MatrixCursor result to all recently viewed movies for the spinner
		if (uriMatcher.match(uri) == 1) {
			int id = 0;
			for (Map.Entry<String, String> entry : _recent.entrySet()) { // loop over HashMap to fill MatrixCursor
				String value = entry.getValue();
				try {
					JSONObject data = new JSONObject(value);
					result.addRow(new Object[] {id,data.getString("title"),data.getString("mpaa_rating"),data.getString("critics_consensus"),data.getString("synopsis")});
				} catch (JSONException e) {
					Log.i("JSON ERROR", "INVALID JSON OBJECT");
				}
			}
		}
		// if 2 returned, then the query is to set the MatrixCursor result to the movie picked from the spinner
		else {
			String title = uri.getPathSegments().get(0).toString();  // get movie title to search HashMap
			String movieData = _recent.get(title);
			if (movieData != null) {
				try {
					JSONObject data = new JSONObject(movieData);
					result.addRow(new Object[] {0,data.getString("title"),data.getString("mpaa_rating"),data.getString("critics_consensus"),data.getString("synopsis")});
				} catch (JSONException e) {
					Log.i("JSON ERROR", "INVALID JSON OBJECT");
				}
			}
		}
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (uriMatcher.match(uri) == 2) {
			String title = uri.getPathSegments().get(0).toString();
			// save the HashMap with the key:value with the title and json-string of the searched movie
			_recent.put(title, selection);
			FileStuff.storeObjectFile(getContext(), "recent", _recent, false);
			Log.i("MOVIE_PROVIDER", "MOVIE HAS BEEN SAVED");
			return 1;
		} else {
			return 0;
		}
		
	} 
}

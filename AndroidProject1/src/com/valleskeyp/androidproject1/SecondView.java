package com.valleskeyp.androidproject1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class SecondView extends Activity {
	Context _context;
	ArrayAdapter<String> _nameArray;
	ListView _listView;
	String _result;
	String _finishWithTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Black);
		setContentView(R.layout.second);

		_context = this;
		_listView = (ListView) findViewById(R.id.listView1);
		_nameArray = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
		
		Intent i = getIntent();
		_result = i.getStringExtra("JSONdata");
		
		//Setup list view with JSON data passed from MainActivity
		try {

			JSONObject json = new JSONObject(_result);
			JSONArray ary = json.getJSONArray("movies");
			for (int tmp = 0; tmp < ary.length(); tmp++) {
				JSONObject object = ary.getJSONObject(tmp);
				String str = object.getString("title") + "  (" + object.getString("year") + ")";
				_nameArray.add(str);
			}
			_listView.setAdapter(_nameArray);

		} catch (JSONException e) {
			Log.e("JSON", "JSON OBJECT EXCEPTION");
		}
		
		_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				try {

					JSONObject json = new JSONObject(_result);
					JSONArray ary = json.getJSONArray("movies");
					JSONObject object = ary.getJSONObject(position);
					_finishWithTitle = object.getString("title");
				} catch (JSONException e) {
					Log.e("JSON", "JSON OBJECT EXCEPTION");
				}
				finish();
			}

		});
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent data = new Intent();
		data.putExtra("result", _result);
		data.putExtra("title", _finishWithTitle);
		
		setResult(RESULT_OK, data);
		super.finish();
	}
}
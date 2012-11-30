package com.valleskeyp.androidproject1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.valleskeyp.androidproject1.SecondFragment.SecondListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class SecondView extends Activity implements SecondListener {
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
		setContentView(R.layout.second_fragment);

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
			Log.e("JSON", "JSON OBJECT EXCEPTION / NO DATA");
		}
		
		
	}
	//pass back the user choice along with data for ease of reading and manipulating
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent data = new Intent();
		data.putExtra("result", _result);
		data.putExtra("title", _finishWithTitle);
		
		setResult(RESULT_OK, data);
		super.finish();
	}
	@Override
	public void onListClick(int position, String movieTitle) {
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
}

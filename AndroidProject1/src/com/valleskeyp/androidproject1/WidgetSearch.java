package com.valleskeyp.androidproject1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class WidgetSearch extends Activity {
	Context _context;
	
	public WidgetSearch() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_search);
		_context = this;
		
		EditText textView = (EditText ) findViewById(R.id.searchText);
		textView.requestFocus();
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		
		Button searchButton = (Button) findViewById(R.id.searchGo);
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText searchText = (EditText) findViewById(R.id.searchText);
				String str = searchText.getText().toString().trim();
				if (!str.equals("")) {
					
					Intent i = new Intent(_context, MainActivity.class);
					i.putExtra("WidgetSearch", str);
					startActivityForResult(i, 1);
				}
			}
		});
	}
}

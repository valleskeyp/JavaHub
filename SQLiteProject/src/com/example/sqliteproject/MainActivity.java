package com.example.sqliteproject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sqliteDB.EmployeeDataSource;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


public class MainActivity extends Activity {
	
	EmployeeDataSource datasource;
	Context _context;
	ListView employeeList;
	Button buttonFilter;
	Button synchButton;
	Spinner spinnerFilter;
	
	ParseObject employee;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Parse.initialize(this, "nLkl1a4taGsRO3hOzCaf5vm1TABc5zrnmnd7MT2A", "8xJw5oO6K9RMtchHiM3ImfgZ7S0gdCKcfeEIjFzR"); 
		ParseAnalytics.trackAppOpened(getIntent());
		datasource = new EmployeeDataSource(this);
		
		_context = this;
		employeeList = (ListView) findViewById(R.id.list_database);
		employeeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				Pattern p = Pattern.compile("(\\d+)");
				Matcher m = p.matcher((CharSequence) employeeList.getItemAtPosition(position));

				if (m.find()) {
					Log.i("TESTING_CLICK", m.group(1));
					String str = m.group(1);
					//m.group(1) is the COLUMN_ID, pass to create view to use for UPDATING
					Intent i = new Intent(_context, SecondActivity.class);
					i.putExtra("column_id", str);
					startActivity(i);
				}
			}
		});
		
		employeeList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Pattern p = Pattern.compile("(\\d+)");
				Matcher m = p.matcher((CharSequence) employeeList.getItemAtPosition(position));

				if (m.find()) {
					final String str = m.group(1);
					//m.group(1) is the COLUMN_ID, use to DELETE from database
					
					AlertDialog.Builder builder = new AlertDialog.Builder(_context);
					builder.setCancelable(true);
					builder.setTitle("Confirm Delete");
					builder.setInverseBackgroundForced(true);
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					  @Override
					  public void onClick(DialogInterface dialog, int which) {
						  deleteItem(str);
						  dialog.dismiss();
					  }
					});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					  @Override
					  public void onClick(DialogInterface dialog, int which) {
					    dialog.dismiss();
					  }
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
				return true;
			}
		});
		
		synchButton = (Button) findViewById(R.id.synch);
		synchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// for object* obj in bigArray, if object byKey state='delete' ||and|| objectId=null RUN DELETE
			    // else if object byKey objectId=null RUN CREATE
			    // else if object byKey updatedAt=null RUN EDIT
				ArrayList<HashMap<String, String>> list = datasource.findEntries();
				for (final HashMap<String, String> hashMap : list) {
					if (hashMap.get("state").equals("delete") 
						 && !hashMap.get("objectId").equals("null")) {
						//Delete
						ParseQuery query = new ParseQuery("employee_table");
						query.getInBackground(hashMap.get("objectId"), new GetCallback() {
							
							@Override
							public void done(ParseObject object, ParseException e) {
								object.deleteInBackground();
							}
						});
					} else if (hashMap.get("objectId").equals("null") 
								&& hashMap.get("updatedAt").equals("null") 
								|| hashMap.get("updatedAt").equals("updated")) {
						//Create
						ParseObject person = new ParseObject("employee_table");
						person.put("first_name", hashMap.get("firstName"));
		            	person.put("last_name", hashMap.get("lastName"));
		            	person.put("gender", hashMap.get("gender"));
		            	person.put("city", hashMap.get("city"));
		            	person.put("state", hashMap.get("state").toUpperCase());
		            	person.put("salary", Integer.parseInt(hashMap.get("salary")));
		            	person.saveInBackground();
					} else if (hashMap.get("updatedAt").equals("updated") 
								&& !hashMap.get("objectId").equals("null")) {
						//Update
						ParseQuery query = new ParseQuery("employee_table");
						query.whereEqualTo("objectId", hashMap.get("objectId"));
						query.findInBackground(new FindCallback() {
							
							@Override
							public void done(List<ParseObject> objects, ParseException e) {
								if (e == null) {
						            for (ParseObject person : objects) {
						            	person.put("first_name", hashMap.get("firstName"));
						            	person.put("last_name", hashMap.get("lastName"));
						            	person.put("gender", hashMap.get("gender"));
						            	person.put("city", hashMap.get("city"));
						            	person.put("state", hashMap.get("state").toUpperCase());
						            	person.put("salary", Integer.parseInt(hashMap.get("salary")));
						            	person.saveInBackground();
									}
						        } else {
						            Log.d("PARSE_SYNC", "Error: " + e.getMessage());
						        }
							}
						});
					}
				}
					
				datasource.deleteRecords();
				// ---------------------------------------------------------------- DOWNLOAD DATA FROM PARSE AND UPDATE SQL DB
				employee = new ParseObject("employee_table");
				ParseQuery query = new ParseQuery("employee_table");
				query.whereExists("objectId");
				query.findInBackground(new FindCallback() {
					
					@Override
					public void done(List<ParseObject> objects, ParseException e) {
						if (e == null) {
				            Log.d("PARSE_SYNC", "Retrieved " + objects.size() + " employees");
				            // run through Parse results then update local SQL database
				            for (ParseObject person : objects) {
				            	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				            	String updated = df.format(person.getUpdatedAt());
				            	String[] items = new String[] { 
				            			person.getString("first_name"),
				            			person.getString("last_name"),
				            			person.getString("gender"),
				            			person.getString("city"),
				            			person.getString("state"),
				            			Integer.toString(person.getInt("salary")),
				            			person.getObjectId(),
				            			updated
				            	};
				            	datasource.createParse(items);
							}
				            refreshList();
				        } else {
				            Log.d("PARSE_SYNC", "Error: " + e.getMessage());
				        }
					}
				});
			}
		});
		
		spinnerFilter = (Spinner) findViewById(R.id.spinnerFilter);
		ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,
		        R.array.filter, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnerFilter.setAdapter(spinAdapter);
		
		refreshList();
		
		Button create = (Button) findViewById(R.id.add_person);
		create.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(_context, SecondActivity.class);
				startActivity(i);
			}
		});
		
		buttonFilter = (Button) findViewById(R.id.buttonFilter);
		buttonFilter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (spinnerFilter.getSelectedItemPosition()) {
				case 0:
					updateFilter("gender = 'Male' AND state != 'delete'", "gender ASC");
					break;
				case 1:
					updateFilter("gender = 'Female' AND state != 'delete'", "gender ASC");
					break;
				case 2:
					updateFilter("state != 'delete'", "lastName ASC");
					break;
				case 3:
					updateFilter("state != 'delete'", "lastName DESC");
					break;
				case 4:
					updateFilter("state = 'FL' OR state = 'fl'", "state ASC");
					break;
				default:
					break;
				}
				
			}
		});
	}
	
	public void deleteItem(String selection) {
		datasource.deleteEntry(selection);
		refreshList();
	}
	
	public void refreshList() {
		datasource.open();
		
		List<String> employees = datasource.findAll();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, employees);
		employeeList.setAdapter(adapter);
	}
	
	public void updateFilter(String selection, String orderBy) {
		datasource.open();
		
		List<String> employees = datasource.findFilter(selection, orderBy);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, employees);
		employeeList.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		refreshList();
	}
	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
	}
	
}

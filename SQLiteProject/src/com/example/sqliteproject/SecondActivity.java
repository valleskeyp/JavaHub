package com.example.sqliteproject;

import java.util.List;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import sqliteDB.EmployeeDataSource;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class SecondActivity extends Activity {

	EmployeeDataSource datasource;
	EditText fName;
	EditText lName;
	RadioButton genderMale;
	RadioButton genderFemale;
	EditText city;
	EditText state;
	EditText salary;
	
	String emp_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		emp_ID = "empty";
		
		Parse.initialize(this, "nLkl1a4taGsRO3hOzCaf5vm1TABc5zrnmnd7MT2A", "8xJw5oO6K9RMtchHiM3ImfgZ7S0gdCKcfeEIjFzR"); 
		ParseAnalytics.trackAppOpened(getIntent());
		
		fName = (EditText) findViewById(R.id.first_name);
		lName = (EditText) findViewById(R.id.last_name);
		genderMale = (RadioButton) findViewById(R.id.male);
		genderFemale = (RadioButton) findViewById(R.id.female);
		city = (EditText) findViewById(R.id.city);
		state = (EditText) findViewById(R.id.state);
		salary = (EditText) findViewById(R.id.salary);

		datasource = new EmployeeDataSource(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			emp_ID = extras.getString("column_id");
			fillData(emp_ID);
		}
		
		Button submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// get data from fields --- put into string array and pass into datasource
				String[] items = null;
				
				if (fName.length() > 0) {
					if (lName.length() > 0) {
						if (genderMale.isChecked()) {
							if (city.length() > 0) {
								if (state.length() > 0) {
									if (salary.length() > 0) {  // MALE EMPLOYEE
										items = new String[] {emp_ID,
															  fName.getText().toString(),
															  lName.getText().toString(),
															  "Male",
															  city.getText().toString(),
															  state.getText().toString(),
															  salary.getText().toString()};
										if (!emp_ID.equals("empty")) {
											//UPDATING
											datasource.updateEmployee(items);
											datasource.setEdited(emp_ID);
										} else {
											//CREATING
											long id = datasource.create(items);
										}
										finish();
									}
								}
							}
						} else if (!genderMale.isChecked()) {  // FEMALE EMPLOYEE
							if (city.length() > 0) {
								if (state.length() > 0) {
									if (salary.length() > 0) {
										items = new String[] {emp_ID,
												fName.getText().toString(),
												lName.getText().toString(),
												"Female",
												city.getText().toString(),
												state.getText().toString(),
												salary.getText().toString()};
										if (!emp_ID.equals("empty")) {
											//UPDATING
											datasource.updateEmployee(items);

											ParseQuery query = new ParseQuery("employee_table");
											query.whereEqualTo("employee_Id", Integer.parseInt(emp_ID));
											query.setLimit(1);
											query.findInBackground(new FindCallback() {

												@Override
												public void done(List<ParseObject> objects, ParseException e) {
													if (e == null) {
														for (ParseObject person : objects) {
															person.put("first_name", fName.getText().toString());
															person.put("last_name", lName.getText().toString());
															person.put("gender", "Female");
															person.put("city", city.getText().toString());
															person.put("state", state.getText().toString().toUpperCase());
															person.put("salary", Integer.parseInt(salary.getText().toString()));
															person.saveInBackground();
														}
													} else {
														Log.d("PARSE_SYNC", "Error: " + e.getMessage());
													}
												}
											});
										} else {
											//CREATING
											long id = datasource.create(items);
											datasource.close();

											ParseObject employee = new ParseObject("employee_table");
											employee.put("employee_Id", id);
											employee.put("first_name", fName.getText().toString());
											employee.put("last_name", lName.getText().toString());
											employee.put("gender", "Female");
											employee.put("city", city.getText().toString());
											employee.put("state", state.getText().toString().toUpperCase());
											employee.put("salary", Integer.parseInt(salary.getText().toString()));
											employee.saveInBackground();
										}
										finish();
									}
								}
							}
						}
					}
				}
			}
		});
		
		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
	}

	public void fillData(String column_id) {
		datasource.open();

		String[] employee = datasource.findByID(column_id);
		fName.setText(employee[0]);
		lName.setText(employee[1]);
		if (employee[2].equals("Male")) {
			genderMale.setChecked(true);
		} else {
			genderFemale.setChecked(true);
		}
		salary.setText(employee[3]);
		city.setText(employee[4]);
		state.setText(employee[5]);
		
		datasource.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		datasource.open();
	}
	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
	}
	
	
}
package com.valleskeyp.tubehub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class EditActivity extends Activity {
	private ResponseReceiver receiver;
	String _path;
	String _accessToken;
	
	EditText _title;
	EditText _description;
	Spinner _category;
	RadioButton _geolocation;
	RadioButton _privateVideo;
	String _id;
	
	GPSTracker gps;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        
        _title = (EditText) this.findViewById(R.id.edit_title);
        _description = (EditText) this.findViewById(R.id.edit_description);
        _category = (Spinner) this.findViewById(R.id.edit_category);
        
        _geolocation = (RadioButton) this.findViewById(R.id.edit_geo_true);
        _privateVideo = (RadioButton) this.findViewById(R.id.edit_private_true);

        Button deleteButton = (Button) this.findViewById(R.id.edit_delete);
        Button submitButton = (Button) this.findViewById(R.id.edit_submit);
        
        Intent intent = getIntent();
        _accessToken = intent.getStringExtra("accessToken");
        if (intent.getStringExtra("path") != null) {
        	// User has entered view to upload a new video to YouTube
			_path = intent.getStringExtra("path");
		} else {
			try {
				_id = intent.getStringExtra("id");
			} catch (NullPointerException e) {
				_id = "";
			}
			// User has entered view to edit a video
			// - parse video data, fill fields with data, retain video id for edit call
			deleteButton.setVisibility(View.VISIBLE);
			
			Toast msg = Toast.makeText(EditActivity.this, "Retreiving video data", Toast.LENGTH_LONG);
			msg.setGravity(Gravity.TOP, msg.getXOffset() / 2, msg.getYOffset() / 2);
			msg.show();
			
			Intent msgIntent = new Intent(this, VideoInfoService.class);
			msgIntent.putExtra("id", _id);
			msgIntent.putExtra("accessToken", _accessToken);
			startService(msgIntent);
			
		}
        
        
        submitButton.setOnClickListener(new View.OnClickListener() {
			
        	@Override
        	public void onClick(View v) {
        		// on submit, validate all of the fields.  if valid submit data to upload service
        		gps = new GPSTracker(EditActivity.this);

        		if (_geolocation.isChecked()) {
        			if(gps.canGetLocation()){

        				double latitude = gps.getLatitude();
        				double longitude = gps.getLongitude();

        				if (validation()) {
        					if (_id == null) {
        						Intent msgIntent = new Intent(EditActivity.this, UploadService.class);
        						msgIntent.putExtra(UploadService.PARAM_IN_MSG, _accessToken);
        						msgIntent.putExtra("path", _path);
        						msgIntent.putExtra("title", _title.getText().toString());
        						msgIntent.putExtra("description", _description.getText().toString());
        						msgIntent.putExtra("category", _category.getItemAtPosition(_category.getSelectedItemPosition()).toString());
        						msgIntent.putExtra("geolocation", _geolocation.isChecked());
        						msgIntent.putExtra("latitude", latitude);
        						msgIntent.putExtra("longitude", longitude);
        						msgIntent.putExtra("private", _privateVideo.isChecked());
        						
        						Toast msg = Toast.makeText(EditActivity.this, "Uploading", Toast.LENGTH_LONG);
        						msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
        						msg.show();

        						startService(msgIntent);
        					} else {
        						Toast msg = Toast.makeText(EditActivity.this, "Updating", Toast.LENGTH_LONG);
        						msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
        						msg.show();
        						
        						Intent msgIntent = new Intent(EditActivity.this, SingleVideoService.class);
        						msgIntent.putExtra("accessToken", _accessToken);
        						msgIntent.putExtra("id", _id);
        						msgIntent.putExtra("title", _title.getText().toString());
        						msgIntent.putExtra("description", _description.getText().toString());
        						msgIntent.putExtra("category", _category.getItemAtPosition(_category.getSelectedItemPosition()).toString());
        						msgIntent.putExtra("geolocation", _geolocation.isChecked());
        						msgIntent.putExtra("latitude", latitude);
        						msgIntent.putExtra("longitude", longitude);
        						msgIntent.putExtra("private", _privateVideo.isChecked());
        						
        						startService(msgIntent);
        					}
        				}
        			}else{
        				gps.showSettingsAlert();
        			}
        		} else {
        			if (validation()) {

        				if (_id == null) {
    						Intent msgIntent = new Intent(EditActivity.this, UploadService.class);
    						msgIntent.putExtra(UploadService.PARAM_IN_MSG, _accessToken);
    						msgIntent.putExtra("path", _path);
    						msgIntent.putExtra("title", _title.getText().toString());
    						msgIntent.putExtra("description", _description.getText().toString());
    						msgIntent.putExtra("category", _category.getItemAtPosition(_category.getSelectedItemPosition()).toString());
    						msgIntent.putExtra("geolocation", _geolocation.isChecked());
    						msgIntent.putExtra("private", _privateVideo.isChecked());
    						
    						Toast msg = Toast.makeText(EditActivity.this, "Uploading", Toast.LENGTH_LONG);
    						msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
    						msg.show();

    						startService(msgIntent);
    					} else {
    						Toast msg = Toast.makeText(EditActivity.this, "Updating", Toast.LENGTH_LONG);
    						msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
    						msg.show();
    						
    						Intent msgIntent = new Intent(EditActivity.this, SingleVideoService.class);
    						msgIntent.putExtra("accessToken", _accessToken);
    						msgIntent.putExtra("id", _id);
    						msgIntent.putExtra("title", _title.getText().toString());
    						msgIntent.putExtra("description", _description.getText().toString());
    						msgIntent.putExtra("category", _category.getItemAtPosition(_category.getSelectedItemPosition()).toString());
    						msgIntent.putExtra("geolocation", _geolocation.isChecked());
    						msgIntent.putExtra("private", _privateVideo.isChecked());
    						
    						startService(msgIntent);
    					}
        			}

        		}
        	}
        });
        
        deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// will sent delete request to youtube for video in editing - use ID or something
				OpenDialog();
			}
			private void OpenDialog() {
				DialogFragment deleteDialogFragment = DeleteDialogFragment.newInstance();
			    deleteDialogFragment.show(getFragmentManager(), "deleteDialogFragment");
			}
		});
        
        
    }
    
    public void okClicked() {
    	// delete video
    	Intent msgIntent = new Intent(EditActivity.this, DeleteService.class);
		msgIntent.putExtra("accessToken", _accessToken);
		msgIntent.putExtra("id", _id);
		startService(msgIntent);
		
    	startActivity(new Intent(this, MainActivity.class));
    }
    
    public boolean validation() {
    	if (_title.getText().toString().length() < 1) {
    		Toast msg = Toast.makeText(EditActivity.this, "Please enter a Title", Toast.LENGTH_LONG);
			msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
			msg.show();
    		return false;
    	} else if (_description.getText().toString().length() < 1) {
    		Toast msg = Toast.makeText(EditActivity.this, "Please enter a description", Toast.LENGTH_LONG);
			msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
			msg.show();
    		return false;
    	} else {
    		return true;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode)
        {
        case 999:
            setResult(999);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public class ResponseReceiver extends BroadcastReceiver {
    	public static final String ACTION_SINGLE = "com.valleskeyp.intent.action.ACTION_DELETE";
    	public static final String ACTION_UPDATE = "com.valleskeyp.intent.action.ACTION_UPDATE";
    	public static final String ACTION_INFO = "com.valleskeyp.intent.action.ACTION_INFO";
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_SINGLE)) {
				// sent video edit
				String data = intent.getStringExtra("data");
				Log.i("EDIT_RECEIVE", data);
			} else if (intent.getAction().equals(ACTION_INFO)) {
				String data = intent.getStringExtra("info");
				try {
					Log.i("EDIT_RECEIVE", data);
					JSONObject json = new JSONObject(data);
					JSONArray videos = json.getJSONArray("items");
					JSONObject item = videos.getJSONObject(0);
					_title.setText(item.getJSONObject("snippet").getString("title"));
					_description.setText(item.getJSONObject("snippet").getString("description"));
					
					try {
						int category = Integer.parseInt(item.getJSONObject("snippet").getString("categoryId"));

						switch (category) {
						case 22:
							_category.setSelection(0);
							break;
						case 15:
							_category.setSelection(1);
							break;
						case 17:
							_category.setSelection(2);
							break;
						case 23:
							_category.setSelection(3);
							break;
						case 25:
							_category.setSelection(4);
							break;
						default:
							break;
						}
						if (true) {
							
						}
					} catch(NumberFormatException nfe) {
						nfe.printStackTrace();
					}
					
					if (item.getJSONObject("status").getString("privacyStatus").equals("private")) {
						_privateVideo.setChecked(true);
					}
					
					if (item.getJSONObject("recordingDetails") != null) {
						_geolocation.setChecked(true);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}
    }
    
    @Override
    protected void onResume() {
    	IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_UPDATE);
    	filter.addCategory(Intent.CATEGORY_DEFAULT);
    	receiver = new ResponseReceiver();
    	registerReceiver(receiver, filter);
    	
    	IntentFilter filter2 = new IntentFilter(ResponseReceiver.ACTION_INFO);
    	filter2.addCategory(Intent.CATEGORY_DEFAULT);
    	registerReceiver(receiver, filter2);
    	
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	unregisterReceiver(receiver);
    	super.onPause();
    }
}

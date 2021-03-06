package com.valleskeyp.tubehub;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;


public class MainActivity extends Activity {
	private ResponseReceiver receiver;
	private ResponseReceiver receiver2;
	Context _context;
	String _access_token;
	String _refresh_token;
	ListView _listView;
	SharedPreferences _pref;
	ArrayList<String> _titles = new ArrayList<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_view);
        _context = this;
        
        
        _pref = getApplicationContext().getSharedPreferences("Login", 0);
        _access_token = _pref.getString("access_token", null);
        _refresh_token = _pref.getString("refresh_token", null);

        _listView = (ListView) this.findViewById(R.id.list_content);
        
        ImageButton searchButton = (ImageButton) this.findViewById(R.id.search);
        Button uploadButton = (Button) this.findViewById(R.id.upload_button);
        Button recordButton = (Button) this.findViewById(R.id.record_button);
        
        recordButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			    startActivityForResult(takeVideoIntent, 100);
				
			}
		});
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		uploadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
				intent.setType("video/*"); 
				startActivityForResult(Intent.createChooser(intent,"Select Picture"), 22);
			}
		});
		
		_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent msgIntent = new Intent(MainActivity.this, EditActivity.class);
				msgIntent.putExtra("accessToken", _access_token);
				msgIntent.putExtra("id", _titles.get(position));
				Log.i("CLICK",_titles.get(position));
				startActivity(msgIntent);
			}
		});
    }
    
    // Logout Functionality
    
    public void dropdownMenuClick(View button) {
        PopupMenu dropdown = new PopupMenu(_context, button);
        dropdown.getMenuInflater().inflate(R.menu.dropdown, dropdown.getMenu());

        dropdown.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
        	public boolean onMenuItemClick(MenuItem item) {
            	OpenDialog();
                return true;
            }

			private void OpenDialog() {
				MyDialogFragment myDialogFragment = MyDialogFragment.newInstance();
			    myDialogFragment.show(getFragmentManager(), "myDialogFragment");
			}
        });
        dropdown.show();
    }
    
    public void okClicked() {
    	  // clear preferences, move to login, clear backstack
    	SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", 0);
    	Editor editor = pref.edit();
    	editor.clear();
    	editor.commit();
    	
    	setResult(999);
    	startActivity(new Intent(this, Login_Activity.class));
    	finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		if (requestCode == 22) {
    			Uri selectedVideoUri = data.getData();
    			String selectedVideoPath = getPath(selectedVideoUri);
    			
    			Intent msgIntent = new Intent(MainActivity.this, EditActivity.class);
				msgIntent.putExtra("accessToken", _access_token);
				msgIntent.putExtra("path", selectedVideoPath);
				startActivity(msgIntent);

    		}
    	}
    }
    
    public String getPath(Uri uri) {
    	Cursor cursor = getContentResolver().query(uri, null, null, null, null); 
    	cursor.moveToFirst(); 
    	int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
    	return cursor.getString(idx);
    }
    		
    public class ResponseReceiver extends BroadcastReceiver {
    	public static final String ACTION_RESPONSE = "com.valleskeyp.intent.action.ACTION_RESPONSE";
    	public static final String ACTION_UPLOAD = "com.valleskeyp.intent.action.ACTION_UPLOAD";
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_RESPONSE)) {
				String data = intent.getStringExtra(YouTubeService.PARAM_OUT_MSG);
				_access_token = intent.getStringExtra("access_token");
				Editor editor = _pref.edit();
				editor.putString("access_token", _access_token);
				editor.commit();

				ArrayList<String> videoListView = new ArrayList<String>();
				try {
					JSONObject json = new JSONObject(data);
					JSONArray videos = json.getJSONArray("items");
					for (int i = 0; i < videos.length(); i++) {
						JSONObject videoJSON = videos.getJSONObject(i);
						//Log.i("VIDEO_WHOLE_INFO", videoJSON.toString());
						//Log.i("VIDEO_NAME", videoJSON.getJSONObject("snippet").getString("title"));
						//Log.i("VIDEO_ID", videoJSON.getJSONObject("snippet").getJSONObject("resourceId").getString("videoId"));

						_titles.add(videoJSON.getJSONObject("snippet").getJSONObject("resourceId").getString("videoId"));
						videoListView.add(videoJSON.getJSONObject("snippet").getString("title"));
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, android.R.id.text1, videoListView);
					_listView.setAdapter(adapter);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(ACTION_RESPONSE)) {
				if (intent.getBooleanExtra(UploadService.PARAM_OUT_MSG, false)) {
					Toast msg = Toast.makeText(MainActivity.this, "Video uploaded.", Toast.LENGTH_LONG);
					msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
					msg.show();
				} else {
					Toast msg = Toast.makeText(MainActivity.this, "Video did not upload.", Toast.LENGTH_LONG);
					msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
					msg.show();
				}
			}
		}
    	
    }
    
    @Override
    protected void onResume() {
    	IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESPONSE);
    	filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);
        
        IntentFilter updateFilter = new IntentFilter(ResponseReceiver.ACTION_UPLOAD);
        updateFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver2 = new ResponseReceiver();
        registerReceiver(receiver2, updateFilter);
        
        Intent msgIntent = new Intent(this, YouTubeService.class);
		msgIntent.putExtra(YouTubeService.PARAM_IN_MSG, _refresh_token);
		startService(msgIntent);
		
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	unregisterReceiver(receiver);
    	
    	super.onPause();
    }
}

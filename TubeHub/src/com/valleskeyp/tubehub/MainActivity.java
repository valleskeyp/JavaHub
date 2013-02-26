package com.valleskeyp.tubehub;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;


public class MainActivity extends Activity {
	
	Context _context;
	String _access_token;
	String _refresh_token;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_view);
        _context = this;
        
        
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", 0);
        _access_token = pref.getString("access_token", null);
        _refresh_token = pref.getString("refresh_token", null);
        
        // get fresh token when loading data
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.RequestTokenRefresh(_refresh_token);
        
        Button submitButton = (Button) this.findViewById(R.id.temp_button);
        
		submitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(_context, EditActivity.class);
				
				startActivityForResult(i, 1);
				
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
    
    public class ResponseReceiver extends BroadcastReceiver {
    	public static final String ACTION_RESPONSE = "com.valleskeyp.intent.action.ACTION_RESPONSE";
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			String accessToken = intent.getStringExtra("token");
			
			SharedPreferences pref = getSharedPreferences("Login", 0);
			Editor editor = pref.edit();
			editor.putString("access_token", accessToken);
			editor.commit();
			
			RequestData requestData = new RequestData();
			requestData.AsyncRequest(accessToken);
			
		}
    	
    }
    
    
    
}

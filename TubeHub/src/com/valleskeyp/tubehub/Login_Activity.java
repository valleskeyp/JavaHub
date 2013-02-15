package com.valleskeyp.tubehub;

// Developer Key: AI39si4KF5b83r2FRRpRNeqzhvFeMbv1yZRig8OPeQNiUn8NBRMELTlEYrCUr41wDf-rDQjPjQYdreO9ZGguOBRHnttvBJBW6A //

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gdata.client.*;
import com.google.gdata.client.youtube.*;
import com.google.gdata.data.*;
import com.google.gdata.data.geo.impl.*;
import com.google.gdata.data.media.*;
import com.google.gdata.data.media.mediarss.*;
import com.google.gdata.data.youtube.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.io.File;
import java.net.URL;

public class Login_Activity extends Activity {
	Context _context;
    EditText _accountField;
    EditText _passwordField;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        _context = this;
		
        
		Button submitButton = (Button) this.findViewById(R.id.login_button);
        
		_accountField = (EditText) this.findViewById(R.id.user_account);
        _passwordField = (EditText) this.findViewById(R.id.user_password);
        
		submitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if (_accountField.getText().toString().length() < 1) { 
//					if (_passwordField.getText().toString().length() < 1) {
//						// attempt to authenticate credentials
//						
//						
//						// when authenticating successful, go to main view
//						Intent i = new Intent(_context, MainActivity.class);
//						startActivityForResult(i, 1);
//					} else {
//						Toast.makeText(_context, "Please enter a password", Toast.LENGTH_SHORT).show();
//					}
//				} else {
//					Toast.makeText(_context, "Please enter an account", Toast.LENGTH_SHORT).show();
//				}
				Intent i = new Intent(_context, MainActivity.class);
				startActivityForResult(i, 1);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_activity, menu);
        return true;
    }
}

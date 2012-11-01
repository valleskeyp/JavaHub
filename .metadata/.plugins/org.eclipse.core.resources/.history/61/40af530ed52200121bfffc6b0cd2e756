package com.valleskeyp.androidproject1;

import com.valleskeyp.lib.FormMethods;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout entryBox = FormMethods.textEntryWithSideButton(this, "Enter Text Here", "Go");
        
        LinearLayout ll = new LinearLayout(this);
        ll.addView(entryBox);
        
        setContentView(ll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    // aZ-Kc5CFB2MMj16qkAZOlQ Yelp Key
}

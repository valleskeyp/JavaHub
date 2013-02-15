package com.valleskeyp.tubehub;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends Activity {
	Context _context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_view);
        _context = this;
        
        Button submitButton = (Button) this.findViewById(R.id.temp_button);
        
		submitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(_context, EditActivity.class);
				
				startActivityForResult(i, 1);
				
			}
		});
    }

    public void dropdownMenuClick(View button) {
        PopupMenu dropdown = new PopupMenu(_context, button);
        dropdown.getMenuInflater().inflate(R.menu.dropdown, dropdown.getMenu());

        dropdown.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
        	public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(_context, "Clicked popup menu item " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        dropdown.show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}

package com.valleskeyp.androidproject1;

import com.valleskeyp.lib.FormMethods;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout entryBox = FormMethods.textEntryWithSideButton(this, "Search Movies...", "Go");
        LinearLayout textView = FormMethods.textView(this, "testing");
        
        LinearLayout ll = new LinearLayout(this);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		ll.setLayoutParams(lp);
		
        Button fieldButton = (Button) entryBox.findViewById(2);
        final TextView textField = (TextView) textView.findViewById(1);
        
        fieldButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//gets edittext tag from button reference
				EditText field = (EditText) v.getTag();
				//Log.i("Button clicked: ", field.getText().toString());
				//use text to get movie but for now just pull movie from movieData resource
				String title = getString(R.string.title);
				String rating = getString(R.string.mpaa_rating);
				String critics = getString(R.string.critics_consensus);
				String synopsis = getString(R.string.synopsis);
				textField.setText(title + "\r\n" +
								"Rating: " + rating + "\r\n" +
								"Critics Consensus: " + critics + "\r\n" +
								"Synopsis" + synopsis);
			}
		});
        
        ll.addView(entryBox);
        ll.addView(textView);
        
        setContentView(ll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    // q9gq656wf8xzsacnfza7ndtm rotten tomatoes Key
}

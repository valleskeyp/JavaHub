package com.valleskeyp.lib;


import com.valleskeyp.androidproject1.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.ImageButton;
import android.widget.TextView;

public class FormMethods extends Activity {
	// holds methods to help with Form layout
	
	
	
	// NO LONGER USED.  Converted to XML layouts
	// Will leave here as a reference.
	
	
	
	 
	public static LinearLayout textEntryWithSideButton(Context context, String hint, String ButtonText) {
		LinearLayout ll = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		ll.setLayoutParams(lp);
		
		EditText et = new EditText(context);
		lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
		et.setHint(hint);
		et.setLayoutParams(lp);
		et.setId(1);
		
		Button b = new Button(context);
		b.setText(ButtonText);
		b.setId(2);
		//tagging the editText to the button so the onClick will be have a valid reference
		b.setTag(et);
		
		ll.addView(et);
		ll.addView(b);
		
		return ll;
	}
	
	public static LinearLayout textView(Context context, String data) {
		LinearLayout ll = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,1.0f);
		ll.setLayoutParams(lp);

		TextView tv = new TextView(context);
			
		
		tv.setText(data);
		tv.setId(1);
		tv.setLayoutParams(lp);

		ll.addView(tv);
		
		return ll;
	}
	
	public static LinearLayout RecentDisplay(Context context) {
		LinearLayout ll = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		
		Spinner recents = new Spinner(context);
		recents.setId(1);
		recents.setLayoutParams(lp);
		
		ll.addView(recents);
		
		return ll;
	}
	
	public static LinearLayout ImageButton(Context context) {
		LinearLayout ll = new LinearLayout(context);
		
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LayoutParams lp2 = new LayoutParams(0,LayoutParams.WRAP_CONTENT,0.3f);
		
		TextView txt = new TextView(context);
		txt.setLayoutParams(lp2);
		TextView txt2 = new TextView(context);
		txt2.setLayoutParams(lp2);
		
		ImageButton button = new ImageButton(context);
		button.setId(1);
		button.setImageResource(R.drawable.rotten_tomatoes_logo);
		button.setBackgroundColor(Color.BLACK);
		button.setLayoutParams(lp);
		
		ll.addView(txt);
		ll.addView(button);
		ll.addView(txt2);
		
		return ll;
	}
}

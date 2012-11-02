package com.valleskeyp.lib;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class FormMethods {
	// holds methods to help with Form layout
	 /* 
     * this layout singlehandedly wiped out the majority of the time I spent working on this
     * app.  i assumed i could add another method to the FormMethods class that would be
     * able to add a textview right after the edittext and button.  with changing the
     * layout paramaters, i was able to show what the app currently is - all elements on one row.
     * i suppose now i should not have spent so much time trying to make it work, especially 
     * when i'm not comfortable with the process yet....  It's gonna be a busy week ahead.
    */
	public static LinearLayout textEntryWithSideButton(Context context, String hint, String ButtonText) {
		LinearLayout ll = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
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
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		ll.setLayoutParams(lp);

		TextView tv = new TextView(context);
		tv.setText(data);
		tv.setId(1);
		tv.setLayoutParams(lp);

		ll.addView(tv);
		
		return ll;
	}
}

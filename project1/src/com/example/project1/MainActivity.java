package com.example.project1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	LinearLayout ll;
	LinearLayout.LayoutParams lp;
	TextView result;
	EditText et;
	
	int purchasedItem1;
	int purchasedItem2;
	int purchasedItem3;
	int purchasedItem4;
	
	void purchase(String type) {
		String str = et.getText().toString();
		if (str.length() > 0) {
			int amount = Integer.parseInt(et.getText().toString());
			if (type.equals(getString(R.string.item1))) {
				purchasedItem1 += amount;
			} else if (type.equals(getString(R.string.item2))) {
				purchasedItem2 += amount;
			} else if (type.equals(getString(R.string.item3))) {
				purchasedItem3 += amount;
			} else if (type.equals(getString(R.string.item4))) {
				purchasedItem4 += amount;
			}
			result.setText("You Currently have:\r\n< " + purchasedItem1 + " " + getString(R.string.item1) + " >   < " + purchasedItem2 + " " + getString(R.string.item2) + "> \r\n< "+ purchasedItem3 + " " + getString(R.string.item3) + " >   < "+ purchasedItem4 + " " + getString(R.string.item4) + " >");
		}
	};
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        purchasedItem1 = 0;
        purchasedItem2 = 0;
        purchasedItem3 = 0;
        purchasedItem4 = 0;
        
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        
        TextView tv = new TextView(this);
        tv.setText("Enter a number below to make a purchase");
        ll.addView(tv);
        
        et = new EditText(this);
        et.setHint("0");
        ll.addView(et);
        
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.HORIZONTAL);
        form.setLayoutParams(lp);
        
        LinearLayout form2 = new LinearLayout(this);
        form2.setOrientation(LinearLayout.HORIZONTAL);
        form2.setLayoutParams(lp);
        
        for (int i = 1; i < 5; i++) {
	        Button b = new Button(this);
	        if (i == 1) {
        		b.setText(getString(R.string.item1));
			} else if (i == 2) {
				b.setText(getString(R.string.item2));
			} else if (i == 3) {
				b.setText(getString(R.string.item3));
			} else if (i == 4) {
				b.setText(getString(R.string.item4));
			}
	        b.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Button b = (Button)v;
				    String item = b.getText().toString();
					purchase(item);
				}
			});
	        if (i == 1 || i == 2) {
	        	form.addView(b);
			} else {
				form2.addView(b);
			}
	        
        }
        
        ll.addView(form);
        ll.addView(form2);
        
        result = new TextView(this);
        result.setLayoutParams(lp);
        result.setText("You Currently have:\r\n" + "< 0 " + getString(R.string.item1) + " >   < 0 " + getString(R.string.item2) + " >\r\n< 0 " + getString(R.string.item3) + " >   < 0 " + getString(R.string.item4) + " >");
        ll.addView(result);
        
        setContentView(ll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
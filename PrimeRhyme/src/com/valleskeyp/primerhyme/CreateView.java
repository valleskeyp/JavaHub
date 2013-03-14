package com.valleskeyp.primerhyme;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.valleskeyp.primerhyme.db.RhymeTable;
import com.valleskeyp.primerhyme.db.MyRhymeContentProvider;

public class CreateView extends Activity {
	
	private Context _context;
	private EditText _rhymeTitle;
	private EditText _rhymeBody;
	private EditText _rhymeWord;
	private Button _rhymeButton;
	private ListView _rhymeListResults;
	
	private Uri rhymeUri;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_create);
		
		_context = this;
		_rhymeTitle = (EditText) findViewById(R.id.rhyme_title);
		_rhymeBody = (EditText) findViewById(R.id.rhyme_body);
		_rhymeWord = (EditText) findViewById(R.id.rhyme_word);
		_rhymeButton = (Button) findViewById(R.id.rhyme_button_search);
		_rhymeListResults = (ListView) findViewById(R.id.rhyme_list_results);
		
		Bundle extras = getIntent().getExtras();

	    // Check from the saved Instance
	    rhymeUri = (bundle == null) ? null : (Uri) bundle
	        .getParcelable(MyRhymeContentProvider.CONTENT_ITEM_TYPE);

	    // Or passed from the other activity
	    if (extras != null) {
	      rhymeUri = extras
	          .getParcelable(MyRhymeContentProvider.CONTENT_ITEM_TYPE);

	      fillData(rhymeUri);
	    }
	    
	    _rhymeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// search for rhyming words
			    if (TextUtils.isEmpty(_rhymeWord.getText().toString())) {
					Toast.makeText(CreateView.this, "Please enter a word to rhyme", Toast.LENGTH_LONG).show();
			    } else {
			    	LinearLayout createLayout = (LinearLayout)findViewById(R.id.create_layout);
			    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    	imm.hideSoftInputFromWindow(createLayout.getWindowToken(), 0);
			    	
			    	Toast.makeText(CreateView.this, "Searching words that rhyme with " + _rhymeWord.getText().toString() + "...", Toast.LENGTH_LONG).show();
			    }
			}
		});
	  }

	private void fillData(Uri uri) {
		String[] projection = {	RhymeTable.COLUMN_CONTENT, RhymeTable.COLUMN_TITLE };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();

			_rhymeTitle.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(RhymeTable.COLUMN_TITLE)));
			_rhymeBody.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(RhymeTable.COLUMN_CONTENT)));

			// Always close the cursor
			cursor.close();
		}
	}
	
	private void saveState() {
		String title = _rhymeTitle.getText().toString();
		String body = _rhymeBody.getText().toString();

		// Only save if either title or body
		// is available

		if (body.length() == 0 && title.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(RhymeTable.COLUMN_TITLE, title);
		values.put(RhymeTable.COLUMN_CONTENT, body);

		if (rhymeUri == null) {
			// New rhyme
			rhymeUri = getContentResolver().insert(MyRhymeContentProvider.CONTENT_URI, values);
		} else {
			// Update rhyme
			getContentResolver().update(rhymeUri, values, null, null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.new_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_save:
			if (TextUtils.isEmpty(_rhymeTitle.getText().toString())) {
				Toast.makeText(CreateView.this, "Please enter a title", Toast.LENGTH_LONG).show();
			} else if (TextUtils.isEmpty(_rhymeBody.getText().toString())) {
				Toast.makeText(CreateView.this, "Please enter your rhymes", Toast.LENGTH_LONG).show();
			} else {
				setResult(RESULT_OK);
				saveState();
				finish();
			}
			break;
		case R.id.menu_about:
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}

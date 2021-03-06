package com.valleskeyp.primerhyme;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.valleskeyp.primerhyme.db.RhymeTable;
import com.valleskeyp.primerhyme.db.MyRhymeContentProvider;

public class CreateView extends Activity {
	private ResponseReceiver receiver;
	
	private Context _context;
	private EditText _rhymeTitle;
	private EditText _rhymeBody;
	private EditText _rhymeWord;
	private Button _rhymeButton;
	private TextView _rhymeText;
	private ListView _rhymeListResults;
	ArrayList<String> _list;
	
	private Uri rhymeUri;
	
	static final String RHYME_WORD = "rhymeWord";
	private String THE_WORD;
	static final String RHYME_LIST = "rhymeList";
	
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
		_rhymeText = (TextView) findViewById(R.id.rhyme_title_desc);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "FultonsHand_Regular.ttf");
	    _rhymeTitle.setTypeface(font);
	    _rhymeBody.setTypeface(font);
	    _rhymeWord.setTypeface(font);
	    _rhymeButton.setTypeface(font);
	    _rhymeText.setTypeface(font);
	    
		if (!_rhymeWord.toString().isEmpty()) {
			THE_WORD = _rhymeWord.getText().toString();
		}
		
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
			    	
			    	// clear out listview, then begin search process
			    	_rhymeListResults.setAdapter(null);
			    	
			    	Intent msgIntent = new Intent(_context, RhymeService.class);
			    	msgIntent.putExtra("word", _rhymeWord.getText().toString());
			    	startService(msgIntent);
			    }
			}
		});
	    
	    _rhymeListResults.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				String selectedFromList =(_rhymeListResults.getItemAtPosition(position).toString());
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("simple text",selectedFromList);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(CreateView.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
				return false;
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
			Intent i2 = new Intent(_context, AboutView.class);
			startActivity(i2);
			break;
		case R.id.menu_share:
			shareIt();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	public void shareIt() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, _rhymeBody.getText().toString());
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	public class ResponseReceiver extends BroadcastReceiver {
		public static final String RHYME_RESPONSE = "com.valleskeyp.intent.action.RHYME_RESPONSE";
		@Override
		public void onReceive(Context context, Intent intent) {
			_list = new ArrayList<String>();
			int check = 0;
			if(intent.getAction().equals(RHYME_RESPONSE)) {
				String data = intent.getStringExtra("rhymeList");
				try {
					JSONArray json = new JSONArray(data);
					
					for (int i = 0; i < json.length(); i++) {
						JSONObject word = json.getJSONObject(i);
						int myNum = 0;
						try {
						    myNum = Integer.parseInt(word.getString("score"));
						} catch(NumberFormatException nfe) {}
						if (word.getString("flags").contains("a")) {
							// word is considered offensive, skip it
							Log.i("PRIME_RHYME", word.getString("word"));
							Log.i("PRIME_RHYME", word.getString("flags"));
							Log.i("PRIME_RHYME", "----OFFENSIVE_WORD-----");
						} else if (myNum > 299) {
							// skips words that aren't perfect rhymes
							Log.i("PRIME_RHYME", word.getString("word"));
							Log.i("PRIME_RHYME", word.getString("score"));
							Log.i("PRIME_RHYME", "---------------");
							check++;
							_list.add(word.getString("word"));
						}
					}
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				if (check == 0) {
					_list.add("No rhyming matches found");
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, android.R.id.text1, _list) {
					public View getView(int position, View v, android.view.ViewGroup parent) {
						TextView textView = (TextView) super.getView(position, v, parent);
						Typeface font = Typeface.createFromAsset(getAssets(), "FultonsHand_Regular.ttf");
			            textView.setTypeface(font);
						
						return textView;
					};
				};

				_rhymeListResults.setAdapter(adapter);
				THE_WORD = _rhymeWord.getText().toString();
			}
			
		}
		
	}
	
	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(ResponseReceiver.RHYME_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);
        
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		
		super.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(RHYME_WORD, THE_WORD);
		outState.putStringArrayList(RHYME_LIST, _list);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		_rhymeWord.setText(savedInstanceState.getString(RHYME_WORD));
		THE_WORD = savedInstanceState.getString(RHYME_WORD);
		_list = savedInstanceState.getStringArrayList(RHYME_LIST);
		if (_list != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, android.R.id.text1, _list) {
				public View getView(int position, View v, android.view.ViewGroup parent) {
					TextView textView = (TextView) super.getView(position, v, parent);
					Typeface font = Typeface.createFromAsset(getAssets(), "FultonsHand_Regular.ttf");
		            textView.setTypeface(font);
					
					return textView;
				};
			};
			_rhymeListResults.setAdapter(adapter);
		}
	}
}

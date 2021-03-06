package com.valleskeyp.primerhyme;

import com.valleskeyp.primerhyme.db.MyRhymeContentProvider;
import com.valleskeyp.primerhyme.db.RhymeTable;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	Context _context;
	ListView _listview;
	

	private static final int DELETE_ID = Menu.FIRST + 1;
	private SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		_context = this;
		_listview = (ListView) findViewById(R.id.list_rhymes);
		
	    
		fillData();
		registerForContextMenu(_listview);
		
		_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				Intent i = new Intent(_context, CreateView.class);
			    Uri todoUri = Uri.parse(MyRhymeContentProvider.CONTENT_URI + "/" + id);
			    i.putExtra(MyRhymeContentProvider.CONTENT_ITEM_TYPE, todoUri);
			    
			    startActivity(i);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list, menu);
		return true;
	}
	@Override
	  public boolean onContextItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case DELETE_ID:
	      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	          .getMenuInfo();
	      Uri uri = Uri.parse(MyRhymeContentProvider.CONTENT_URI + "/"
	          + info.id);
	      getContentResolver().delete(uri, null, null);
	      fillData();
	      return true;
	    }
	    return super.onContextItemSelected(item);
	  }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_create:
			Intent i = new Intent(_context, CreateView.class);
			startActivity(i);
			break;
		case R.id.menu_about:
			Intent i2 = new Intent(_context, AboutView.class);
			startActivity(i2);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	  

	  private void fillData() {

	    // Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] { RhymeTable.COLUMN_TITLE };
	    // Fields on the UI to which we map
	    int[] to = new int[] { R.id.label };

	    getLoaderManager().initLoader(0, null, this);
	    adapter = new SimpleCursorAdapter(this, R.layout.rhyme_row, null, from,
	        to, 0);
	    final Typeface font = Typeface.createFromAsset(getAssets(), "FultonsHand_Regular.ttf");
	    adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				TextView textView = (TextView) view;
				textView.setTypeface(font);
				return false;
			}
		});
	    _listview.setAdapter(adapter);
	    
	  }
	  
	  @Override
	  public void onCreateContextMenu(ContextMenu menu, View v,
	      ContextMenu.ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    AdapterView.AdapterContextMenuInfo info =
	            (AdapterView.AdapterContextMenuInfo) menuInfo;
	    String selectedWord = ((TextView) info.targetView.findViewById(R.id.label)).getText().toString();
	    menu.setHeaderTitle("Do you wish to delete: " + selectedWord);
	    menu.add(0, DELETE_ID, 0, "Yes");
	    menu.add(0, 0, 0, "No");
	  }

	  // Creates a new loader after the initLoader () call
	  @Override
	  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	    String[] projection = { RhymeTable.COLUMN_ID, RhymeTable.COLUMN_TITLE };
	    CursorLoader cursorLoader = new CursorLoader(this,
	        MyRhymeContentProvider.CONTENT_URI, projection, null, null, null);
	    return cursorLoader;
	  }

	  @Override
	  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	    adapter.swapCursor(data);
	  }

	  @Override
	  public void onLoaderReset(Loader<Cursor> loader) {
	    // data is not available anymore, delete reference
	    adapter.swapCursor(null);
	  }
	  
}

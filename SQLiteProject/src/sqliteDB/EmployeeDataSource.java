package sqliteDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class EmployeeDataSource {
	
	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
	
	private static final String[] allColumns = {
		DatabaseAdapter.COLUMN_ID,
		DatabaseAdapter.FIRST_NAME,
		DatabaseAdapter.LAST_NAME,
		DatabaseAdapter.GENDER,
		DatabaseAdapter.CITY,
		DatabaseAdapter.STATE,
		DatabaseAdapter.SALARY,
		DatabaseAdapter.UPDATEDAT,
		DatabaseAdapter.OBJECTID
	};
	
	public EmployeeDataSource(Context context) {
		dbhelper = new DatabaseAdapter(context);
	}
	
	public void open() {
		database = dbhelper.getWritableDatabase();
	}
	
	public void close() {
		dbhelper.close();
	}
	
	public long create(String[] items) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseAdapter.FIRST_NAME, items[1]);
		values.put(DatabaseAdapter.LAST_NAME, items[2]);
		values.put(DatabaseAdapter.GENDER, items[3]);
		values.put(DatabaseAdapter.CITY, items[4]);
		values.put(DatabaseAdapter.STATE, items[5]);
		values.put(DatabaseAdapter.SALARY, items[6]);
		long id = database.insert(DatabaseAdapter.TABLE_EMPLOYEE, null, values);
		return id;
	}
	
	public long createParse(String[] items) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseAdapter.FIRST_NAME, items[0]);
		values.put(DatabaseAdapter.LAST_NAME, items[1]);
		values.put(DatabaseAdapter.GENDER, items[2]);
		values.put(DatabaseAdapter.CITY, items[3]);
		values.put(DatabaseAdapter.STATE, items[4]);
		values.put(DatabaseAdapter.SALARY, items[5]);
		values.put(DatabaseAdapter.OBJECTID, items[6]);
		values.put(DatabaseAdapter.UPDATEDAT, items[7]);
		long id = database.insert(DatabaseAdapter.TABLE_EMPLOYEE, null, values);
		return id;
	}
	
	public List<String> findAll() {
		
		Cursor cursor = database.query(DatabaseAdapter.TABLE_EMPLOYEE, allColumns, "state != 'delete'", null, null, null, null);
		List<String> employees = cursorToList(cursor);
		return employees;
	}
	public List<String> findFilter(String selection, String orderBy) {
		
		Cursor cursor = database.query(DatabaseAdapter.TABLE_EMPLOYEE, allColumns, selection, null, null, null, orderBy);
		List<String> employees = cursorToList(cursor);
		return employees;
	}
	
	public boolean deleteEntry(String selection) {
		// mark for deletion
		ContentValues values = new ContentValues();
		values.put(DatabaseAdapter.STATE, "delete");
		database.update(DatabaseAdapter.TABLE_EMPLOYEE, values, DatabaseAdapter.COLUMN_ID + "=" + selection, null);
		return true;
	}
	
	private List<String> cursorToList(Cursor cursor) {
		List<String> employees = new ArrayList<String>();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String str = 				 
						cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ID)) + ": " +
						cursor.getString(cursor.getColumnIndex(DatabaseAdapter.FIRST_NAME)) + " " +
						cursor.getString(cursor.getColumnIndex(DatabaseAdapter.LAST_NAME)) + " " +
						cursor.getString(cursor.getColumnIndex(DatabaseAdapter.GENDER)) + "\n" +
						"$" + cursor.getString(cursor.getColumnIndex(DatabaseAdapter.SALARY)) + "\n" +
						cursor.getString(cursor.getColumnIndex(DatabaseAdapter.CITY)) + " " +
						cursor.getString(cursor.getColumnIndex(DatabaseAdapter.STATE));
				employees.add(str);
			}
		}
		return employees;
	}
	
	public String[] findByID(String column_id) {
		String selection = DatabaseAdapter.COLUMN_ID + "=" + column_id;
		Cursor cursor = database.query(DatabaseAdapter.TABLE_EMPLOYEE, allColumns, selection, null, null, null, null);
		String[] array = null;
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
		String state = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.STATE)).toUpperCase();
		
		array = new String[] {cursor.getString(cursor.getColumnIndex(DatabaseAdapter.FIRST_NAME)),
						  cursor.getString(cursor.getColumnIndex(DatabaseAdapter.LAST_NAME)),
						  cursor.getString(cursor.getColumnIndex(DatabaseAdapter.GENDER)),
						  cursor.getString(cursor.getColumnIndex(DatabaseAdapter.SALARY)),
						  cursor.getString(cursor.getColumnIndex(DatabaseAdapter.CITY)),
						  state};
			}
		}
		
		return array;
	}
	
	public ArrayList<HashMap<String, String>> findEntries() {
		Cursor cursor = database.query(DatabaseAdapter.TABLE_EMPLOYEE, allColumns, null, null, null, null, null);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Map <String,String> emp =  new HashMap<String,String>();

				String state = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.STATE)).toUpperCase();

				emp.put("firstName", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.FIRST_NAME)));
				emp.put("lastName", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.LAST_NAME)));
				emp.put("gender", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.GENDER)));
				emp.put("city", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.CITY)));
				emp.put("state", state);
				emp.put("salary", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.SALARY)));
				
				if (cursor.getString(cursor.getColumnIndex(DatabaseAdapter.UPDATEDAT)) != null) {
					emp.put("updatedAt", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.UPDATEDAT)));
				} else {
					emp.put("updatedAt", "null");
				}
				
				if (cursor.getString(cursor.getColumnIndex(DatabaseAdapter.OBJECTID)) != null) {
					emp.put("objectId", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.OBJECTID)));
				} else {
					emp.put("objectId", "null");
				}
				
				list.add((HashMap<String, String>) emp);
			}
		}
		return list;
	}
	
	public void updateEmployee(String[] items) {
		ContentValues values = new ContentValues();
		values.put(DatabaseAdapter.FIRST_NAME, items[1]);
		values.put(DatabaseAdapter.LAST_NAME, items[2]);
		values.put(DatabaseAdapter.GENDER, items[3]);
		values.put(DatabaseAdapter.CITY, items[4]);
		values.put(DatabaseAdapter.STATE, items[5]);
		values.put(DatabaseAdapter.SALARY, items[6]);
		database.update(DatabaseAdapter.TABLE_EMPLOYEE, values, DatabaseAdapter.COLUMN_ID + "=" + items[0], null);	
	}
	
	public void setEdited(String ID) {
		ContentValues values = new ContentValues();
		values.put(DatabaseAdapter.UPDATEDAT, "updated");
		database.update(DatabaseAdapter.TABLE_EMPLOYEE, values, DatabaseAdapter.COLUMN_ID + "=" + ID, null);
	}
	
	public void deleteRecords() {
		database.delete(DatabaseAdapter.TABLE_EMPLOYEE, null, null);
	}
}

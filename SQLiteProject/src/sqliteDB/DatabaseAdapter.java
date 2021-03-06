package sqliteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapter extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "finalEmployee.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_EMPLOYEE = "employees";
	public static final String COLUMN_ID = "employeeId";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String GENDER = "gender";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String SALARY = "salary";
	public static final String UPDATEDAT = "updatedAt";
	public static final String OBJECTID = "objectId";
	
	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_EMPLOYEE + " (" +
								COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
								FIRST_NAME + " TEXT, " +
								LAST_NAME + " TEXT, " +
								GENDER + " TEXT, " +
								CITY + " TEXT, " +
								STATE + " TEXT, " +
								SALARY + " INT," +
								UPDATEDAT + " TEXT, " +
								OBJECTID + " TEXT) ";
	
	public DatabaseAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE);
		onCreate(db);
	}

}

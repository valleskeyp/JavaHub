package com.valleskeyp.primerhyme.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RhymeTable {

  // Database table
  public static final String TABLE_RHYME = "rhyme";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_CONTENT = "content";

  // Database creation SQL statement
  private static final String DATABASE_CREATE = "create table " 
      + TABLE_RHYME
      + "(" 
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_TITLE + " text not null," 
      + COLUMN_CONTENT
      + " text not null" 
      + ");";

  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
      int newVersion) {
    Log.w(RhymeTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_RHYME);
    onCreate(database);
  }
} 

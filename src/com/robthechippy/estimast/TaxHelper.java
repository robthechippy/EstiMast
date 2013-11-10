package com.robthechippy.estimast;


import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


class TaxHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="estimast.db";
	private static final int SCHEMA_VERSION=1;


	public TaxHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ContentValues cv=new ContentValues();

		db.execSQL("CREATE TABLE tax (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, title TEXT, percent FLOAT);");

		cv.put("title", "GST");
		cv.put("percent", 10);

		getWritableDatabase().insert("tax", "title", cv);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// no-op, since will not be called until 2nd schema
		// version exists
	}



	/* Used to load current jobs into the list
	 public Cursor getCurrent() {
	 return(getReadableDatabase().rawQuery("SELECT * FROM jobs WHERE status<6 ORDER BY status",
	 null));
	 } */

	//Used to load all the catagories into the list
	public Cursor getAll() {
		return(getReadableDatabase()
            .rawQuery("SELECT * FROM tax",
                      null));
	}

	//Adds a new catagory
	public int insert(String title, Float percent) {
		ContentValues cv=new ContentValues();
		Long ID;

		cv.put("title", title);
		cv.put("percent", percent);

		ID=getWritableDatabase().insert("tax", "title", cv);
		//ID=ID-1;
		return(ID.intValue());
	}

	//Update the catagory record
	public void update(String[] id, String name,Float percent) {
		ContentValues cv=new ContentValues();

		cv.put("title", name);
		cv.put("percent", percent);

		getWritableDatabase().update("tax", cv, "_id=?", id);
	}

	public void delete(String[] id) {
		//This will delete the catagory.
		getWritableDatabase().delete("tax", "_id=?", id);


	}

	//Read individual column data

	public String[] getID(Cursor c) {
		String id[]=new String[1];
		id[0]=Integer.toString(c.getInt(0));
		return(id);
	}

	public int getIDint(Cursor c) {
		return(c.getInt(0));
	}

	public String getTitle(Cursor c) {
		return(c.getString(1));

	}
	
	public Float getapercent(Cursor c) {
		return(c.getFloat(2));
	}


}

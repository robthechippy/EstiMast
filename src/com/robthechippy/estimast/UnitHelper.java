package com.robthechippy.estimast;


import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


class UnitHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="estimast.db";
	private static final int SCHEMA_VERSION=1;
	
	
	public UnitHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ContentValues cv=new ContentValues();
		
		db.execSQL("CREATE TABLE unit (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, title TEXT, hasparts INTEGER);");

		cv.put("title", "each");
		cv.put("hasparts", 0);
		getWritableDatabase().insert("unit", "title", cv);
		
		cv.put("title", "lm");
		cv.put("hasparts", 0);
		getWritableDatabase().insert("unit", "title", cv);
		
		cv.put("title", "Sqr m");
		cv.put("hasparts", 0);
		getWritableDatabase().insert("unit", "title", cv);
		
		cv.put("title", "Cube m");
		cv.put("hasparts", 0);
		getWritableDatabase().insert("unit", "title", cv);
		
		cv.put("title", "Pkt");
		cv.put("hasparts", 1);
		getWritableDatabase().insert("unit", "title", cv);
		
		cv.put("title", "Kg");
		cv.put("hasparts", 1);
		getWritableDatabase().insert("unit", "title", cv);
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
            .rawQuery("SELECT * FROM unit",
                      null));
	}
	
			//Adds a new catagory
	public int insert(String title, Boolean hasparts) {
		ContentValues cv=new ContentValues();
		Long ID;

		cv.put("title", title);
		cv.put("hasparts", hasparts);

		ID=getWritableDatabase().insert("unit", "title", cv);
		//ID=ID-1;
		return(ID.intValue());
	}
	
			//Update the catagory record
	public void update(String[] id, String name, Boolean hasparts) {
		ContentValues cv=new ContentValues();

		cv.put("title", name);
		cv.put("hasparys", hasparts);

		getWritableDatabase().update("unit", cv, "_id=?", id);
	}
	
	public void delete(String[] id) {
		//This will delete the catagory.
		getWritableDatabase().delete("unit", "_id=?", id);
		
	
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
	
	public Boolean getHasparts(Cursor c) {
		
		int x=c.getInt(2);
		if (x==0) {
			return(false);
			}
			
		else {
			return(true);
		}
	}

	
}

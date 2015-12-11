package com.robthechippy.estimast;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


class TaxHelper {
	//private static final String DATABASE_NAME="estimast.db";
	//private static final int SCHEMA_VERSION=1;
	private dbMaster db=null;


	public TaxHelper(Context context) {
		//super(context, DATABASE_NAME, null, SCHEMA_VERSION);
		db = dbMaster.getInstance(context);

	}


	// Used to load current tax percent
	 public String getCurrent(String taxtype) {
	 
		 String[] sel=new String[1];
		 sel[0]= taxtype;
		 Cursor c=db.getReadableDatabase().rawQuery("SELECT * FROM tax WHERE title=?",
	 	sel);
		c.moveToFirst();
		return(Float.toString( getpercent(c)));
	 } 

	//Used to load all the catagories into the list
	public Cursor getAll() {
		return(db.getReadableDatabase()
            .rawQuery("SELECT * FROM tax",
                      null));
	}

	//Adds a new catagory
	public int insert(String title, Float percent) {
		ContentValues cv=new ContentValues();
		Long ID;

		cv.put("title", title);
		cv.put("percent", percent);

		ID=db.getWritableDatabase().insert("tax", "title", cv);
		//ID=ID-1;
		return(ID.intValue());
	}

	//Update the catagory record
	public void update(String[] id, String name,Float percent) {
		ContentValues cv=new ContentValues();

		cv.put("title", name);
		cv.put("percent", percent);

		db.getWritableDatabase().update("tax", cv, "_id=?", id);
	}

	public void delete(String[] id) {
		//This will delete the catagory.
		db.getWritableDatabase().delete("tax", "_id=?", id);


	}
	
	/**
	* Getting all labels
	* returns list of labels
	*
	*/
	
	public List<String> getAllLabels(){
	
	//public void getAllLabels(){
		List<String> labels = new ArrayList<String>();
		// Select All Query
		Cursor cursor = db.getReadableDatabase().rawQuery("SELECT * FROM tax", null);
		//SQLiteDatabase db = this.db.getReadableDatabase();
		//Cursor cursor = db.rawQuery("SELECT * FROM tax", null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(1));
			}
			while (cursor.moveToNext());
			}
		// closing connection
		cursor.close();
		//db.close();
		// returning lables 
		return labels;
	}

    public void close(){
        db.close();
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
	
	public Float getpercent(Cursor c) {
		return(c.getFloat(2));
	}


}

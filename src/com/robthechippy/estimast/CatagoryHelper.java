package com.robthechippy.estimast;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;


class CatagoryHelper {
	private dbMaster db=null;

	
	public CatagoryHelper(Context context) {
		db = new dbMaster(context);
		
	}

			/* Used to load current jobs into the list
	public Cursor getCurrent() {
		return(getReadableDatabase().rawQuery("SELECT * FROM jobs WHERE status<6 ORDER BY status",
			null));
	} */
	
			//Used to load all the catagories into the list
	public Cursor getAll() {
		return(db.getReadableDatabase()
            .rawQuery("SELECT * FROM catagories",
                      null));
	}
	
			//Adds a new catagory
	public int insert(String title) {
		ContentValues cv=new ContentValues();
		Long ID;

		cv.put("title", title);

		ID=db.getWritableDatabase().insert("catagories", "title", cv);
		//ID=ID-1;
		return(ID.intValue());
	}
	
			//Update the catagory record
	public void update(String[] id, String name) {
		ContentValues cv=new ContentValues();

		cv.put("title", name);

		db.getWritableDatabase().update("catagories", cv, "_id=?", id);
	}
	
	public void delete(String[] id) {
		//This will delete the catagory.
		db.getWritableDatabase().delete("catagories", "_id=?", id);
		
	
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
		Cursor cursor = db.getReadableDatabase().rawQuery("SELECT * FROM catagories", null);
		//SQLiteDatabase db = this.getReadableDatabase();
		//Cursor cursor = db.rawQuery("SELECT * FROM catagories", null);
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

	
}

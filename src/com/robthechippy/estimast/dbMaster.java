package com.robthechippy.estimast;


import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class dbMaster extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="estimast.db";
	private static final int SCHEMA_VERSION=1;


	public dbMaster(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ContentValues cv=new ContentValues();

		/* Mainitems table */
		db.execSQL("CREATE TABLE items (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, catagory TEXT, code TEXT, description TEXT, unit TEXT, unitQty FLOAT, unitCost FLOAT, markup FLOAT, taxable INTEGER, taxtype TEXT, itemType TEXT, itemLen FLOAT, itemLenFrac FLOAT, itemWidth FLOAT, itemWidthFrac FLOAT, itemHeight FLOAT, itemHeightFrac FLOAT, availableSizes TEXT, supplier INTEGER, dateChecked TEXT, stockOnHand INTEGER, stockOnOrder INTEGER, barcode TEXT, location TEXT, photo Text)");
		cv.put("catagory", "Misc");
		cv.put("code", "Test1");
		cv.put("description", "This is just an item for testing");
		cv.put("unit", "each");
		cv.put("unitQty", 1);
		cv.put("unitCost", 23.45);
		cv.put("markup", 10);
		cv.put("taxable", 1);
		cv.put("taxtype", "GST");
		cv.put("itemType", "Non inventory");
		cv.put("itemLen", 1.0);
		cv.put("itemLenFrac", 0.3);
		cv.put("itemWidth", 2.0);
		cv.put("itemWidthFrac",0.25);
		cv.put("itemHeight", 0.3);
		cv.put("itemHeightFrac", 0.6);
		cv.put("availableSizes", "");
		cv.put("supplier", 1);
		cv.put("dateChecked", "27/12/2013");
		cv.put("stockOnHand", 1);
		cv.put("stockOnOrder", 0);
		cv.put("barcode", "");
		cv.put("location", "A14");
		cv.put("photo", "");
		db.insert("items", null, cv);
		
		cv.put("catagory", "Service");
		cv.put("code", "Sevice");
		cv.put("description", "This is just an service item for testing");
		cv.put("unit", "lm");
		cv.put("unitQty", 1);
		cv.put("unitCost", 23.45);
		cv.put("markup", 10);
		cv.put("taxable", 0);
		cv.put("taxtype", "None");
		cv.put("itemType", 2);
		cv.put("itemLen", 1.0);
		cv.put("itemLenFrac", 0.3);
		cv.put("itemWidth", 2.0);
		cv.put("itemWidthFrac",0.25);
		cv.put("itemHeight", 0.3);
		cv.put("itemHeightFrac", 1);
		cv.put("availableSizes", "");
		cv.put("supplier", 1);
		cv.put("dateChecked", "27/12/2013");
		cv.put("stockOnHand", 0);
		cv.put("stockOnOrder", 0);
		cv.put("barcode", "");
		cv.put("location", "");
		cv.put("photo", "");
		db.insert("items", null, cv);
		cv.clear();
		
		/* Catagory table */
		db.execSQL("CREATE TABLE catagories (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, title TEXT);");

		cv.put("title", "Misc");
		db.insert("catagories", null, cv);
		
		cv.put("title", "Service");
		db.insert("catagories", null, cv);
		cv.clear();
		
		/* Tax table */
		db.execSQL("CREATE TABLE tax (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, title TEXT, percent FLOAT);");

		cv.put("title", "None");
		cv.put("percent", 0);
		db.insert("tax", null, cv);

		cv.put("title", "GST");
		cv.put("percent", 10.0);
		db.insert("tax", null, cv);
		cv.clear();
		
		/* unit types table */
		db.execSQL("CREATE TABLE unit (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, title TEXT, hasparts INTEGER);");

		cv.put("title", "each");
		cv.put("hasparts", 0);
		db.insert("unit", null, cv);

		cv.put("title", "lm");
		cv.put("hasparts", 0);
		db.insert("unit", null, cv);

		cv.put("title", "Sqr m");
		cv.put("hasparts", 0);
		db.insert("unit", null, cv);

		cv.put("title", "Cube m");
		cv.put("hasparts", 0);
		db.insert("unit", null, cv);

		cv.put("title", "Pkt");
		cv.put("hasparts", 1);
		db.insert("unit", null, cv);

		cv.put("title", "Kg");
		cv.put("hasparts", 1);
		db.insert("unit", null, cv);
		cv.clear();
		
		/* itemtype table */
		db.execSQL("CREATE TABLE itemtype (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, title TEXT, type INTEGER);");

		cv.put("title", "Non inventory");
		cv.put("type", 0);
		db.insert("itemtype", null, cv);

		cv.put("title", "Inventory");
		cv.put("type", 1);
		db.insert("itemtype", null, cv);

		cv.put("title", "Service");
		cv.put("type", 2);
		db.insert("itemtype", null, cv);

		cv.put("title", "Shipping");
		cv.put("type", 3);
		db.insert("itemtype", null, cv);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// no-op, since will not be called until 2nd schema
		// version exists
	}
}

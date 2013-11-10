package com.robthechippy.estimast;

import java.util.Calendar;
import java.text.DateFormat;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

class ItemHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="estimast.db";
	private static final int SCHEMA_VERSION=1;
	private Calendar dateAndTime=Calendar.getInstance();
	private DateFormat fmtDateAndTime=DateFormat.getDateInstance();

	public ItemHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE items (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, catagory INTEGER, code TEXT, description TEXT, unit INTEGER, unitQty FLOAT, unitCost FLOAT, markup FLOAT, taxable INTEGER, taxtype INTEGER, itemType INTEGER, itemLen FLOAT, itemLenFrac FLOAT, itemWidth FLOAT, itemWidthFrac FLOAT, itemHeight FLOAT, itemHeightFrac FLOAT, availableSizes TEXT, supplier INTEGER, dateChecked TEXT, stockOnHand INTEGER, stockOnOrder INTEGER, barcode TEXT, location TEXT, photo TEXT);");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// no-op, since will not be called until 2nd schema
		// version exists
	}




	//Used to load current catagory items into the list
	public Cursor getCurrentCat(String[] cat) {
		return(getReadableDatabase().rawQuery("SELECT * FROM items WHERE catagory=?",
											  cat));
	}

	//Used to load all the items into the list
	public Cursor getAllItems() {
		return(getReadableDatabase()
            .rawQuery("SELECT * FROM items",
                      null));
	}

	//Adds a new item
	public int insertItem(String code) {
		ContentValues cv=new ContentValues();
		Long ID;

		cv.put("catagory", 1);
		cv.put("code", code);
		cv.put("unit", 1);
		cv.put("itemType", 1);
		cv.put("dateChecked", fmtDateAndTime.format(dateAndTime.getTime()));
		

		ID=getWritableDatabase().insert("items", "description", cv);
		//ID=ID-1;
		return(ID.intValue());
	}

	//Update the item record
	public void updateItem(String[] id, int catagory, String code, String description, int unit, float unitQty,
				float unitCost, float markup, int taxable, int taxtype, int itemType, float itemLen,
				float itemLenFrac, float itemWidth, float itemWidthFrac, float itemHeight, float itemHeightFrac,
				String availableSizes, int supplier, String dateChecked, int stockOnHand, int stockOnOrder,
				String barcode, String location, String photo) {
		ContentValues cv=new ContentValues();

		cv.put("catagory", catagory);
		cv.put("code", code);
		cv.put("description", description);
		cv.put("unit", unit);
		cv.put("unitQty", unitQty);
		cv.put("unitCost", unitCost);
		cv.put("markup", markup);
		cv.put("taxable", taxable);
		cv.put("taxtype", taxtype);
		cv.put("itemType", itemType);
		cv.put("itemLen", itemLen);
		cv.put("itemLenFrac", itemLenFrac);
		cv.put("itemWidth", itemWidth);
		cv.put("itemWidthFrac",itemWidthFrac);
		cv.put("itemHeight", itemHeight);
		cv.put("itemHeightFrac", itemHeightFrac);
		cv.put("availableSizes", availableSizes);
		cv.put("supplier", supplier);
		cv.put("dateChecked", dateChecked);
		cv.put("stockOnHand", stockOnHand);
		cv.put("stockOnOrder", stockOnHand);
		cv.put("barcode", barcode);
		cv.put("location", location);
		cv.put("photo", photo);

		getWritableDatabase().update("items", cv, "_id=?", id);
	}

	public void deleteItem(String[] id) {
		//This will delete the job.
		getWritableDatabase().delete("items", "_id=?", id);

		
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

	public String getCode(Cursor c) {
		return(c.getString(1));

	}

	public String getDescription(Cursor c) {
		return(c.getString(2));
	}

	public int getUnit(Cursor c) {
		return(c.getInt(3));
	}

	public float getUnitCost(Cursor c) {
		return(c.getFloat(4));
	}

	public float getMarkup(Cursor c) {
		return(c.getFloat(5));
	}

	public int getTaxable(Cursor c) {
		return(c.getInt(6));
	}

	public int getTaxtype(Cursor c) {
		return(c.getInt(7));
	}

	public int getItemType(Cursor c) {
		return(c.getInt(8));
	}
	
	public float getItemLen(Cursor c) {
		return(c.getFloat(9));
	}
	
	public float getItemLenFrac(Cursor c) {
		return(c.getFloat(10));
	}
	
	public float getItemWidth(Cursor c) {
		return(c.getFloat(11));
	}
	
	public float getItemWidthFrac(Cursor c) {
		return(c.getFloat(12));
	}
	public float getItemHeight(Cursor c) {
		return(c.getFloat(13));
	}
	
	public float getItemHeightFrac(Cursor c) {
		return(c.getFloat(14));
	}
	
	public String getItemAvailableSizes(Cursor c) {
		return(c.getString(15));
	}
	
	public int getItemSupplier(Cursor c) {
		return(c.getInt(16));
	}
	
	public String getItemDateChecked(Cursor c) {
		return(c.getString(17));
	}
	
	public int getItemStockOnHand(Cursor c) {
		return(c.getInt(18));
	}
	
	public int getItemStockOnOrder(Cursor c) {
		return(c.getInt(19));
	}
	
	public String getItemBarcode(Cursor c) {
		return(c.getString(20));
	}
	
	public String getItemLocation(Cursor c) {
		return(c.getString(21));
	}
	
	public String getItemPhoto(Cursor c) {
		return(c.getString(22));
	}

	
}

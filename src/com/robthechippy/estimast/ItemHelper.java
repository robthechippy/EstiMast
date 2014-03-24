package com.robthechippy.estimast;

import java.util.Calendar;
import java.text.DateFormat;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.database.sqlite.SQLiteDatabase;

class ItemHelper {
	private Calendar dateAndTime=Calendar.getInstance();
	private DateFormat fmtDateAndTime=DateFormat.getDateInstance();
	private dbMaster db=null;

	public ItemHelper(Context context) {
		
		db = new dbMaster(context);

	}
	
	//Get all of the items in the cataloge
	public Cursor getAllItems() {
		return(db.getReadableDatabase()
				.rawQuery("SELECT _id, catagory, code, description FROM items", null));
	}

	//Get a single item by id.
	public Cursor getCurrentItem(String _id) {
		//convert _id to string array
		String[] id={_id};
		return(db.getReadableDatabase().rawQuery("SELECT * FROM items WHERE _id=?", id));
	}
	
	//Used to load current catagory items into the list
	public Cursor getCurrentCat(String[] cat) {
		return(db.getReadableDatabase().rawQuery("SELECT * FROM items WHERE catagory=?",
											  cat));
	}

	//Used to load all the items into the list
	/*public Cursor getAllItems() {
		return(db.getReadableDatabase()
            .rawQuery("SELECT * FROM items",
                      null));
	}*/

	//Adds a new blank item
	public int insertItem(String code) {
		ContentValues cv=new ContentValues();
		Long ID;

		cv.put("catagory", 1);
		cv.put("code", code);
		cv.put("unit", 1);
		cv.put("itemType", 1);
		cv.put("dateChecked", fmtDateAndTime.format(dateAndTime.getTime()));
		

		ID=db.getWritableDatabase().insert("items", "description", cv);
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

		db.getWritableDatabase().update("items", cv, "_id=?", id);
	}

	public void deleteItem(String[] id) {
		//This will delete the job.
		db.getWritableDatabase().delete("items", "_id=?", id);

		
	}

	//Read individual column data

	public String getID(Cursor c) {
		//String id[]=new String[1];
		//id[0]=Integer.toString(c.getInt(0));
		return(Integer.toString(c.getInt(0)));
	}

	public int getIDint(Cursor c) {
		return(c.getInt(0));
	}

	public String getCatagory(Cursor c) {
		return(c.getString(1));
	}
	
	public String getCode(Cursor c) {
		return(c.getString(2));

	}

	public String getDescription(Cursor c) {
		return(c.getString(3));
	}

	public String getUnit(Cursor c) {
		return(c.getString(4));
	}

	public float getUnitCost(Cursor c) {
		return(c.getFloat(6));
	}

	public float getMarkup(Cursor c) {
		return(c.getFloat(7));
	}

	public int getTaxable(Cursor c) {
		return(c.getInt(8));
	}

	public String getTaxtype(Cursor c) {
		return(c.getString(9));
	}

	public String getItemType(Cursor c) {
		return(c.getString(10));
	}
	
	public float getItemLen(Cursor c) {
		return(c.getFloat(11));
	}
	
	public float getItemLenFrac(Cursor c) {
		return(c.getFloat(12));
	}
	
	public float getItemWidth(Cursor c) {
		return(c.getFloat(13));
	}
	
	public float getItemWidthFrac(Cursor c) {
		return(c.getFloat(14));
	}
	public float getItemHeight(Cursor c) {
		return(c.getFloat(15));
	}
	
	public float getItemHeightFrac(Cursor c) {
		return(c.getFloat(16));
	}
	
	public String getItemAvailableSizes(Cursor c) {
		return(c.getString(17));
	}
	
	public int getItemSupplier(Cursor c) {
		return(c.getInt(18));
	}
	
	public String getItemDateChecked(Cursor c) {
		return(c.getString(19));
	}
	
	public int getItemStockOnHand(Cursor c) {
		return(c.getInt(20));
	}
	
	public int getItemStockOnOrder(Cursor c) {
		return(c.getInt(21));
	}
	
	public String getItemBarcode(Cursor c) {
		return(c.getString(22));
	}
	
	public String getItemLocation(Cursor c) {
		return(c.getString(23));
	}
	
	public String getItemPhoto(Cursor c) {
		return(c.getString(24));
	}

	
}

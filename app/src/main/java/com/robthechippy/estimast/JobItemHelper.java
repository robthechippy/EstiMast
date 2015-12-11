package com.robthechippy.estimast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.lang.annotation.*;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.database.sqlite.SQLiteDatabase;

class JobItemHelper {
	private Calendar dateAndTime=Calendar.getInstance();
	private DateFormat fmtDateAndTime=DateFormat.getDateInstance();
	private dbMaster db=null;

	public JobItemHelper(Context context) {
		
		db = dbMaster.getInstance(context);

	}
	
	//Get all of the items in the cataloge
	public Cursor getAllItems(String sectionID) {
        String[] st = {sectionID};
		return(db.getReadableDatabase()
				.rawQuery("SELECT _id, catagory, code, description, total FROM jobitems WHERE sectionId=?", st));
	}

    public Cursor getSearchItemsList(String sectionId, String search) {

        String sql;

        if(search.equals("")){
            sql="SELECT _id, catagory, code, description, total FROM jobitems WHERE sectionID=" + sectionId;
        }else {
            sql = "SELECT _id, catagory, code, description, total FROM jobitems WHERE (sectionId=" + sectionId + " AND (code LIKE '%" + search + "%' OR description LIKE '%" + search + "%'))";
        }
        return(db.getReadableDatabase().rawQuery(sql, null));
    }

    public Cursor getSearchJobItemsList(String sectionId, String search) {

        String sql;

        if(search.equals("")){
            sql="SELECT * FROM jobitems WHERE sectionID=" + sectionId;
        }else {
            sql = "SELECT * FROM jobitems WHERE (sectionId=" + sectionId + " AND (code LIKE '%" + search + "%' OR description LIKE '%" + search + "%'))";
        }
        return(db.getReadableDatabase().rawQuery(sql, null));
    }

	//Get a single item by id.
	public Cursor getCurrentItem(String _id) {
		//convert _id to string array
		String[] id={_id};
		return(db.getReadableDatabase().rawQuery("SELECT * FROM jobitems WHERE _id=?", id));
	}
	
	//Used to load current catagory items into the list
	public Cursor getCurrentCat(String[] cat) {
		return(db.getReadableDatabase().rawQuery("SELECT * FROM jobitems WHERE catagory=?",
											  cat));
	}
	
	//Used to load in item newly added to a section
	public Cursor getNewItems(String ids) {
		String sql ="SELECT * FROM jobitems WHERE " + ids;
		return(db.getReadableDatabase().rawQuery(sql, null));
	}


	//Adds a new blank item
	public int insertItem(String code, String clientId, String jobId, String sectionId) {
		ContentValues cv=new ContentValues();
		Long ID;

		cv.put("sectionId",sectionId);
        cv.put("jobId", jobId);
        cv.put("clientId", clientId);
        cv.put("catagory", "Misc");
		cv.put("code", code);
		cv.put("unit", "each");
		cv.put("unitQty", 1);
		cv.put("total", 0.0);
        cv.put("unitCost", 0.0);
		cv.put("itemType", "Inventory");
		cv.put("dateChecked", fmtDateAndTime.format(dateAndTime.getTime()));
		

		ID=db.getWritableDatabase().insert("jobitems", "description", cv);
		//ID=ID-1;
		return(ID.intValue());
	}

	//Update the item record
	public void updateItem(String[] id, String catagory, String code, String description, float itemQty, float total, String unit,
				float unitQty, float unitCost, float markup, int taxable, String taxtype, String itemType, float itemLen,
				float itemLenFrac, float itemWidth, float itemWidthFrac, float itemHeight, float itemHeightFrac,
				String availableSizes, int supplier, String dateChecked, int stockOnHand, int stockOnOrder,
				String barcode, String location, String photo) {
		ContentValues cv=new ContentValues();

		cv.put("catagory", catagory);
		cv.put("code", code);
		cv.put("description", description);
		cv.put("itemQty", itemQty);
		cv.put("total", total);
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

		db.getWritableDatabase().update("jobitems", cv, "_id=?", id);
	}

	public String copyItems(ArrayList<Integer> chosen, String sectionId, String jobId, String clientId){

		String s;
		ContentValues cv = new ContentValues();
		Cursor c;
		Long ID;
		String newIDList = "";
		String tmp = "";
		
		//Scroll through the items and copy them into the jobitem database.
		for(int id : chosen){
			//Select each item frommthe catalogue one at a time.
			s="SELECT * FROM items WHERE _id=" + id;
            c = db.getReadableDatabase().rawQuery(s, null);
			c.moveToFirst();
			
			//Now load these values into a contenet value.
			
			cv.put("clientId", clientId);
			cv.put("jobId", jobId);
			cv.put("sectionId", sectionId);
			cv.put("catagory", c.getString(1));
			cv.put("code", c.getString(2));
			cv.put("description", c.getString(3));
			cv.put("itemQty", 0.0);
			cv.put("total", 0.0);
			cv.put("unit", c.getString(4));
			cv.put("unitQty", c.getString(5));
			cv.put("unitCost", c.getString(6));
			cv.put("markup", c.getString(7));
			cv.put("taxable", c.getString(8));
			cv.put("taxtype", c.getString(9));
			cv.put("itemType", c.getString(10));
			cv.put("itemLen", c.getString(11));
			cv.put("itemLenFrac", c.getFloat(12));
			cv.put("itemWidth", c.getFloat(13));
			cv.put("itemWidthFrac",c.getFloat(14));
			cv.put("itemHeight", c.getFloat(15));
			cv.put("itemHeightFrac", c.getFloat(16));
			cv.put("availableSizes", c.getString(17));
			cv.put("supplier", c.getInt(18));
			cv.put("dateChecked", c.getString(19));
			cv.put("stockOnHand", c.getInt(20));
			cv.put("stockOnOrder", c.getInt(21));
			cv.put("barcode", c.getString(22));
			cv.put("location", c.getString(23));
			
			//Put this item into the jobitem table
			ID = db.getWritableDatabase().insert("jobitems", "description", cv);
			tmp = ID.toString();
			//TODO check ID is valid and iem saved.
			if (chosen.indexOf(id) == 0) {
			    newIDList=newIDList + "(_id = " + tmp + " )";
			}
			else {
				newIDList = newIDList + " OR (_id = " + tmp + " )";
			}
		}
		
		return(newIDList);
	}

	public void deleteItem(String[] id) {
		//This will delete the item.
		db.getWritableDatabase().delete("jobitems", "_id=?", id);

	}

    public void close(){db.close();}

	//Read individual column data

	public String getID(Cursor c) {
		return(Integer.toString(c.getInt(0)));
	}

	public int getIDint(Cursor c) {
		return(c.getInt(0));
	}
	
	public int getClientId(Cursor c) {
		return(c.getInt(1));
	}
	
	public int getJobtId(Cursor c) {
		return(c.getInt(2));
	}
	
	public int getSectionId(Cursor c) {
		return(c.getInt(3));
	}
	
	public String getCatagory(Cursor c) {
		return(c.getString(4));
	}

    public String getCatagoryList(Cursor c) {return(c.getString(1));}
	
	public String getCode(Cursor c) {return(c.getString(5));}

    public String getCodeList(Cursor c) {return(c.getString(2));}

	public String getDescription(Cursor c) {
		return(c.getString(6));
	}

    public String getDescriptionList(Cursor c) {return(c.getString(3));}

	public float getItemQty(Cursor c){return(c.getFloat(7)); }

	public float getTotal(Cursor c){return(c.getFloat(8)); }
	
	public float getTotalList(Cursor c){return(c.getFloat(4)); }

	public String getUnit(Cursor c) {
		return(c.getString(9));
	}
	
	public int getUnitQty( Cursor c) {
		return(c.getInt(10));
	}

	public float getUnitCost(Cursor c) {return(c.getFloat(11));
	}

	public float getMarkup(Cursor c) {
		return(c.getFloat(12));
	}

	public int getTaxable(Cursor c) {
		return(c.getInt(13));
	}

	public String getTaxtype(Cursor c) {
		return(c.getString(14));
	}

	public String getItemType(Cursor c) {
		return(c.getString(15));
	}
	
	public float getItemLen(Cursor c) {
		return(c.getFloat(16));
	}
	
	public float getItemLenFrac(Cursor c) {
		return(c.getFloat(17));
	}
	
	public float getItemWidth(Cursor c) {
		return(c.getFloat(18));
	}
	
	public float getItemWidthFrac(Cursor c) {
		return(c.getFloat(19));
	}

	public float getItemHeight(Cursor c) {
		return(c.getFloat(20));
	}
	
	public float getItemHeightFrac(Cursor c) {
		return(c.getFloat(21));
	}
	
	public String getItemAvailableSizes(Cursor c) {
		return(c.getString(22));
	}
	
	public int getItemSupplier(Cursor c) {
		return(c.getInt(23));
	}
	
	public String getItemDateChecked(Cursor c) {
		return(c.getString(24));
	}
	
	public int getItemStockOnHand(Cursor c) {
		return(c.getInt(25));
	}
	
	public int getItemStockOnOrder(Cursor c) {
		return(c.getInt(26));
	}
	
	public String getItemBarcode(Cursor c) {
		return(c.getString(27));
	}
	
	public String getItemLocation(Cursor c) {
		return(c.getString(28));
	}
	
	public String getItemPhoto(Cursor c) {
		return(c.getString(29));
	}

	public String path() {
		return db.getReadableDatabase().getPath();
	}
}

package com.robthechippy.estimast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Jarvises on 11/01/2015.
 */
public class ClientHelper {
        private Calendar dateAndTime=Calendar.getInstance();
        private DateFormat fmtDateAndTime=DateFormat.getDateInstance();
        private dbMaster db=null;

        public ClientHelper(Context context) {

            db = dbMaster.getInstance(context);

        }

        //Get all of the items in the cataloge
        public Cursor getAllClients() {
            return(db.getReadableDatabase()
                    .rawQuery("SELECT _id, name, address FROM clients", null));
        }

        //Get a single item by id.
        public Cursor getCurrentClient(String _id) {
            //convert _id to string array
            String[] id={_id};
            return(db.getReadableDatabase().rawQuery("SELECT * FROM clients WHERE _id=?", id));
        }

        //Searching for data
        public Cursor searchClient(String s) {
            //Convert s to double string array
            s="SELECT _id, name, address FROM clients WHERE name LIKE '%"+s+"%' OR address like'%"+s+"%'";

            return db.getReadableDatabase().rawQuery(s, null);
        }

        //Adds a new blank item
        public int insertClient(String code) {
            ContentValues cv=new ContentValues();
            Long ID;

            cv.put("name", code);

            ID=db.getWritableDatabase().insert("clients", "address", cv);
            //ID=ID-1;
            return(ID.intValue());
        }

        //Update the item record
        public void updateClient(String[] id, String name, String address, String mob, String ph, String email, String notes) {
            ContentValues cv=new ContentValues();

            cv.put("name", name);
            cv.put("address", address);
            cv.put("mob", mob);
            cv.put("ph", ph);
            cv.put("email", email);
            cv.put("notes", notes);
            db.getWritableDatabase().update("clients", cv, "_id=?", id);
        }

        public void deleteClient(String ids) {
            String[] id = {ids};

            db.getWritableDatabase().delete("jobitems", "jobId=?", id);
            db.getWritableDatabase().delete("sections", "jobId=?", id);

            //This will delete the client.
            db.getWritableDatabase().delete("clients", "_id=?", id);

            //Delete any jobs as well.
            db.getWritableDatabase().delete("jobs", "clientId=?", id);

            //TODO section and items


        }

        //Read individual column data

        public String getID(Cursor c) {
            return(Integer.toString(c.getInt(0)));
        }

        public int getIDint(Cursor c) {
            return(c.getInt(0));
        }

        public String getName(Cursor c) {
            return(c.getString(1));
        }

        public String getAddress(Cursor c) {
            return(c.getString(2));

        }

        public String getMob(Cursor c) {
            return(c.getString(3));
        }

        public String getPh(Cursor c) {
            return(c.getString(4));
        }

        public String getEmail( Cursor c) {
            return(c.getString(5));
        }

        public String getNotes(Cursor c) {
            return(c.getString(6));
        }

        public void close() {
            db.close();
            db=null;
        }

    }


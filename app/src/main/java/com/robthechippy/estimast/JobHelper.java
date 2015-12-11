package com.robthechippy.estimast;

/**
 * Created by Jarvises on 25/01/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class JobHelper {

    private Calendar dateAndTime=Calendar.getInstance();
    private DateFormat fmtDateAndTime=DateFormat.getDateInstance();
    private dbMaster db=null;

    public JobHelper(Context context) {

        db = dbMaster.getInstance(context);

    }

    //Get all of the job for this client
    public Cursor getAllJobs(String _idClient) {
        //String id = "SELECT _id, clientId, location FROM jobs WHERE clientId=" + _idClient;
        String[] id = {_idClient};
        return(db.getReadableDatabase()
                .rawQuery("SELECT _id, clientId, location, status, total FROM jobs WHERE clientId=?", id));

    }

    //Get a single job by id.
    public Cursor getCurrentJob(String _id) {
        //convert _id to string array
        String[] id={_id};
        return(db.getReadableDatabase().rawQuery("SELECT * FROM jobs WHERE _id=?", id));
    }

    //Searching for data
    public Cursor searchJob(String clientId, String status, String searchTxt) {
        //Convert s to double string array
        String s="";
        if(status=="") {
            s = "SELECT _id, clientId, location, status, total FROM jobs WHERE ( clientId=" + clientId + " AND location LIKE '%" + searchTxt + "%')";
        } else {
            s = "SELECT _id, clientId, location, status, total FROM jobs WHERE ( clientId=" + clientId + " AND status='" + status + "' AND location LIKE '%" + searchTxt + "%')";
        }
        return db.getReadableDatabase().rawQuery(s, null);
    }

    public Cursor getTotal(String id) {
        String s="SELECT total(total) FROM sections WHERE jobId=" + id;
        return(db.getReadableDatabase().rawQuery(s, null));
    }

    //Adds a new blank job
    public int insertJob(String location, String clientId) {
        ContentValues cv=new ContentValues();
        Long ID;

        cv.put("clientId", clientId);
        cv.put("location", location);
        cv.put("status", "New Job");

        ID=db.getWritableDatabase().insert("jobs", "note", cv);
        //ID=ID-1;
        return(ID.intValue());
    }

    //Update the job record
    public void updateJob(String[] id, String clientId, String location, String note, String status , String viewedDate, Float total, String dueDate) {
        ContentValues cv=new ContentValues();

        cv.put("clientId", clientId);
        cv.put("location", location);
        cv.put("note", note);
        cv.put("status", status);
        cv.put("viewed", viewedDate);
        cv.put("total", total);
        cv.put("duedate", dueDate);
        db.getWritableDatabase().update("jobs", cv, "_id=?", id);
    }

    public void deleteJob(String id) {
        //This will delete the job.
        String[] ida = {id};
        db.getWritableDatabase().delete("jobitems", "jobId=?", ida);
        db.getWritableDatabase().delete("sections", "jobId=?", ida);
        db.getWritableDatabase().delete("jobs", "_id=?", ida);

    }

   //Load data from the status table
    public List<String> loadStatus() {

        //Select all items
        Cursor c = db.getReadableDatabase().rawQuery("SELECT * FROM status", null);

        List<String> labels = new ArrayList<String>();

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                labels.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        return labels;
    }

    public void close() {
        db.close();
        db=null;
    }

    //Read individual column data

    public String getID(Cursor c) {
        return(Integer.toString(c.getInt(0)));
    }

    public int getIDint(Cursor c) {
        return(c.getInt(0));
    }

    public String getClientId(Cursor c) {
        return(c.getString(1));
    }

    public String getLocation(Cursor c) { return(c.getString(2)); }

    public String getnote(Cursor c) {
        return(c.getString(3));
    }

    public String getSearchStatus(Cursor c) { return(c.getString(3)); }

    public String getstatus(Cursor c) {
        return(c.getString(4));
    }

    public Float getSearchTotal(Cursor c) { return c.getFloat(4); }

    public String getviewedDate( Cursor c) {
        return(c.getString(5));
    }

    public Float getTotal(Cursor c) {
        return(c.getFloat(6));
    }

    public String getDueDate(Cursor c) {
        return(c.getString(7));
    }


}


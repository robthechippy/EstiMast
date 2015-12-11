package com.robthechippy.estimast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jarvises on 15/02/2015.
 */
public class SectionHelper {

    private dbMaster db=null;

    public SectionHelper(Context context) {

        db = dbMaster.getInstance(context);

    }

    //Get all of the job for this client
    public Cursor getAllSections(String _idJob) {
        String[] id = {_idJob};
        return(db.getReadableDatabase().rawQuery("SELECT _id, clientId, jobId, section, extra, total FROM sections WHERE jobId=?", id));

    }

    //Get a single job by id.
    public Cursor getCurrentSection(String _id) {
        //convert _id to string array
        String[] id={_id};
        return(db.getReadableDatabase().rawQuery("SELECT * FROM sections WHERE _id=?", id));
    }

    //Searching for data
    public Cursor searchSection(String jobId, String searchTxt) {
        //Convert s to double string array
        String s="";
        s = "SELECT _id, clientId, jobId, section, extra, total FROM sections WHERE ( jobId=" + jobId + " AND section LIKE '%" + searchTxt + "%')";
        return db.getReadableDatabase().rawQuery(s, null);
    }

    //Return the total amount for this section.
    public Cursor getSecTotal(String _id){
        //String[] id={_id};
        String s  = "SELECT total(total) FROM jobitems WHERE sectionID=" + _id;
        return db.getReadableDatabase().rawQuery(s, null);
    }

    //Adds a new blank section

    public int insertSection(String section, Boolean extra, String clientId, String jobId) {
        ContentValues cv=new ContentValues();
        Long ID;

        cv.put("clientId", clientId);
        cv.put("section", section);
        cv.put("jobId", jobId);
        cv.put("extra", extra);

        ID=db.getWritableDatabase().insert("sections", "note", cv);
        //ID=ID-1;
        return(ID.intValue());
    }

    //Update the job record
    public void updateSection(String[] id, String clientId, String jobId, String section, String note, Float length , Float width, Float total, Float height, Boolean extra,
                                        String lenPost, String widthPost, String heightPost ) {
        ContentValues cv=new ContentValues();

        cv.put("clientId", clientId);
        cv.put("jobId", jobId);
        cv.put("section",section);
        cv.put("note", note);
        cv.put("length", length);
        cv.put("width", width);
        cv.put("height", height);
        cv.put("total", total);
        if(extra){
            cv.put("extra", 1);
        }else{
            cv.put("extra", 0);
        }
        cv.put("lenPost", lenPost);
        cv.put("widthPost", widthPost);
        cv.put("heightPost", heightPost);
        db.getWritableDatabase().update("sections", cv, "_id=?", id);
    }

    public void deleteSection(String id) {
        //This will delete the job.
        String[] ida = {id};
        db.getWritableDatabase().delete("jobitems", "jobId=?", ida);
        db.getWritableDatabase().delete("sections", "_id=?", ida);

    }

    public void close() {
        db.close();
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

    public String getJobId(Cursor c) { return(c.getString(2));}

    public String getSection(Cursor c) { return(c.getString(3)); }

    public String getnote(Cursor c) {
        return(c.getString(4));
    }

    public int getSearchExtra(Cursor c) { return(c.getInt(4));}

    public Float getLength(Cursor c) { return(c.getFloat(5)); }

    public Float getSearchTotal(Cursor c) {return(c.getFloat(5));}

    public Float getWidth(Cursor c) {
        return(c.getFloat(6));
    }

    public Float getHeight(Cursor c) { return c.getFloat(7); }

    public Float getTotal( Cursor c) {
        return(c.getFloat(8));
    }

    public int getExtra(Cursor c) {
        return(c.getInt(9));
    }

    public String getLenPost(Cursor c) {return(c.getString(10));}

    public String getWidthPost(Cursor c) {return(c.getString(11));}

    public String getHeightPost(Cursor c) {return(c.getString(12));}
}

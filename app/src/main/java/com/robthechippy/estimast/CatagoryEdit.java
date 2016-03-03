package com.robthechippy.estimast;

import android.app.Activity;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;


public class CatagoryEdit extends Activity {
	
	private CatagoryHelper catagoryHelper = null;
	private Cursor cCat = null;
	private ListView catList = null;
	private ListViewAdapter catAdapter = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.catalogueedit);
		
		catagoryHelper = new CatagoryHelper(this);
		catList = (ListView)findViewById(R.id.lst_cat);
		cCat = catagoryHelper.getAll();
        //Start managing list
        catAdapter = new ListViewAdapter(cCat);
        catList.setAdapter(catAdapter);

        //Set up listener for client selection.
        //catList.setOnItemClickListener(onCatSelect);
		
		}
		
	//*********** Helper Classes

    private class ListViewAdapter extends CursorAdapter{

        ListViewAdapter(Cursor c) {
            super(CatagoryEdit.this, c);
        }

        @Override
        public void bindView(View row, Context ctxt, Cursor c) {
            RowHolder rowHolder=(RowHolder)row.getTag();
            rowHolder.populateFrom(c, catagoryHelper);
        }

        @Override
        public View newView(Context ctxt, Cursor c, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.twoelementrow, parent, false);
            RowHolder rowHolder=new RowHolder(row);

            row.setTag(rowHolder);

            return(row);
        }
    }

    //  This class acts as the row holder, and sets it up.
    private static class RowHolder {
        private TextView name=null;
        private TextView address=null;

        RowHolder(View row) {
            name=(TextView)row.findViewById(R.id.rowItem_left);
            address=(TextView)row.findViewById(R.id.rowItem_right);

        }

        void populateFrom(Cursor c, CatagoryHelper h) {
            name.setText("   ");
            address.setText(h.getTitle(c));
        }
    }
	
}

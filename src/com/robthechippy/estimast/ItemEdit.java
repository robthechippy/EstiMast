package com.robthechippy.estimast;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.view.*;
import android.widget.Spinner;
import android.widget.Adapter;
import android.widget.*;


public class ItemEdit extends Activity
{

	Spinner spnCat = null;
	CatagoryHelper catHelper = null;
	//Cursor catCursor = null;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);
	
		catHelper=new CatagoryHelper(this);
		
		spnCat=(Spinner)findViewById(R.id.spn_catagory);
		
		/* Setup tabs */
		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		
		tabs.setup();
		
		TabHost.TabSpec spec=tabs.newTabSpec("tag1");
		
		spec.setContent(R.id.tabA);
		spec.setIndicator("Main");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag2");
		
		spec.setContent(R.id.tabB);
		spec.setIndicator("Sizes");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag3");
		
		spec.setContent(R.id.tabC);
		spec.setIndicator("Supplier");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag4");
		
		spec.setContent(R.id.tabD);
		spec.setIndicator("Stock");
		tabs.addTab(spec);
		
		/* Setup catagory spinner */
		loadSpinnerData();
		
		
    }
	
	
	private void loadSpinnerData() {
		// database handler
		//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		// Spinner Drop down elements
		//List<String> lables = 
		catHelper.getAllLabels();
		// Creating adapter for spinner
		//ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
			// Drop down layout style - list view with radio button
			//dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		//spnCat.setAdapter(dataAdapter);
		
	}
	
}

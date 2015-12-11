package com.robthechippy.estimast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jarvises on 8/06/2015.
 */
public class jobItemsSelect extends Activity {

    private ItemHelper itemHelper;
    private Cursor cItems;
    private ListView listView;
    private ListViewAdapter itemAdapter;
    public ArrayList<Integer> chosenList = new ArrayList<Integer>();
    private String searchtxt = "";
    private String category = "";
	private String clientId = "";
	private String jobId = "";
    private String sectionId = "";
	private String newItemsId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_item_select);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        itemHelper = new ItemHelper(this);

        if(savedInstanceState != null){
            chosenList=savedInstanceState.getIntegerArrayList("cList");
            searchtxt = savedInstanceState.getString("searchtxt");
            category = savedInstanceState.getString("category");
			clientId = savedInstanceState.getString("clientId");
			jobId = savedInstanceState.getString("jobId");
			sectionId = savedInstanceState.getString("sectionId");
			
        }

        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            String[] data = intent.getStringArrayExtra(intent.EXTRA_EMAIL);
			clientId = data[0];
			jobId = data[1];
			sectionId = data[2];

        }

        Spinner spinner = (Spinner) findViewById(R.id.spn_cat);
        // Spinner click listener
        spinner.setOnItemSelectedListener(cat_listener);

        CatagoryHelper catagoryHelper=new CatagoryHelper(this);
        // Spinner Drop down elements
        List<String> labels = catagoryHelper.getAllLabels();
        labels.add(0,"All");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        catagoryHelper.close();

        //Set up the list for the clients
        listView=(ListView)findViewById(R.id.lst_select_item);
        //listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cItems=itemHelper.getSearchItemsList(category, searchtxt);
        //Start managing list
        itemAdapter = new ListViewAdapter(cItems);
        listView.setAdapter(itemAdapter);
        listView.setOnItemClickListener(onItemSelect);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current chosenList
        savedInstanceState.putIntegerArrayList("cList", chosenList);
        savedInstanceState.putString("searchtxt", searchtxt);
        savedInstanceState.putString("category", category);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Handles search view widget
    private SearchView.OnQueryTextListener search = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            cItems.close();
            searchtxt = newText;
            cItems=itemHelper.getSearchItemsList(category, newText);
            itemAdapter=new ListViewAdapter(cItems);
            listView.setAdapter(itemAdapter);
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_select, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_items).getActionView();
        searchView.setOnQueryTextListener(search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       switch (id) {
            case R.id.action_settings:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //Status spinner handler
    private AdapterView.OnItemSelectedListener cat_listener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void  onItemSelected (AdapterView<?> parent, View view, int position, long id){

            Object item = parent.getItemAtPosition(position);
            category = item.toString();
            cItems.close();
            cItems=itemHelper.getSearchItemsList(category, searchtxt);
            itemAdapter=new ListViewAdapter(cItems);
            listView.setAdapter(itemAdapter);
            //itemHelper.close();
        }

        @Override
        public void  onNothingSelected (AdapterView<?> parent){

        }
    };

    public void onDoneClicked(View view){

        if(chosenList.size()>0){
			//Exit if no items chosen
		
			//Save all selected data into section
        	if(sectionId != null){
        	    JobItemHelper jh=new JobItemHelper(this);
        	    newItemsId = jh.copyItems(chosenList, sectionId, jobId, clientId);
				//TODO Save new id fields to use fo loading
        	}
		
			//TODO check it saved items
		
			//TODO Open the jobitem screen with these items in the cursor.
		
			String sec = "-1";
			Intent intent = new Intent(this, JobItemsEdit.class);
			String[] c = {sec, clientId, jobId, sectionId, "", newItemsId};
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_EMAIL, c);
			intent.setType("text/plain");
			startActivity(intent);
			itemHelper=null;
			cItems.close();
			this.finish();
			
		}
    }

    public void onCancelClick(View view){
        this.finish();
    }

    //*********** Helper Classes

    private class ListViewAdapter extends CursorAdapter {

        ListViewAdapter(Cursor c) {
            super(jobItemsSelect.this, c);
        }

        @Override
        public void bindView(View row, Context ctxt, Cursor c) {
            RowHolder rowHolder=(RowHolder)row.getTag();
            rowHolder.populateFrom(c, itemHelper, chosenList);
        }


        @Override
        public View newView(Context ctxt, Cursor c, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.check_text_item, parent, false);
            RowHolder rowHolder=new RowHolder(row);

            row.setTag(rowHolder);

            return(row);
        }
    }

    //  This class acts as the row holder, and sets it up.
    private static class RowHolder {
        private TextView code=null;
        private TextView desc = null;
        private CheckBox chk=null;

        RowHolder(View row) {
            code= (TextView) row.findViewById(R.id.txt_Large);
            desc = (TextView)row.findViewById(R.id.txt_Med);
            chk = (CheckBox) row.findViewById(R.id.checkBox);
        }

        void populateFrom(Cursor c, ItemHelper h, ArrayList<Integer> chosen) {
            code.setText(h.getCode(c));
            desc.setText(h.getDescription(c));
            if(chosen.contains(h.getIDint(c))) {
                chk.setChecked(true);
            }else{
                chk.setChecked(false);
            }
        }
    }

    //Handle a client selection within the listview.
    private AdapterView.OnItemClickListener onItemSelect = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int Position, long id) {

            CheckBox chk = (CheckBox)view.findViewById(R.id.checkBox);
            //Start by loading the id of the item.
            cItems.moveToPosition(Position);
            Integer itemId=itemHelper.getIDint(cItems);

            //Check if the item has been chosen already.
            if(chosenList.contains(itemId)) {
                //Remove item from list
                chosenList.remove(itemId);
                chk.setChecked(false);
            } else {
                //Otherwise add the item to the list.
                chosenList.add(itemId);
                chk.setChecked(true);
            }


        }
    };


}

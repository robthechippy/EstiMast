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

import static com.robthechippy.estimast.R.id.lst_Client;
import static com.robthechippy.estimast.R.id.rowItem_left;
import static com.robthechippy.estimast.R.id.rowItem_right;



public class ClientSelect extends Activity {

    //Derfinitions
    private ClientHelper clientHelper = null;
    private Cursor cClient = null;
    private ListView clientList = null;
    private ListViewAdapter clientAdapter = null;
    private Boolean on=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientselect);

        clientHelper = new ClientHelper(this);

        //Set up the list for the clients
        clientList=(ListView)findViewById(lst_Client);
        cClient = clientHelper.getAllClients();
        //Start managing list
        clientAdapter = new ListViewAdapter(cClient);
        clientList.setAdapter(clientAdapter);

        //Set up listener for client selection.
        clientList.setOnItemClickListener(onClientSelect);


    }

    //Handles search view widget
    private SearchView.OnQueryTextListener search = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            cClient.close();
            cClient=clientHelper.searchClient(newText);
            clientAdapter=new ListViewAdapter(cClient);
            clientList.setAdapter(clientAdapter);
            return true;
        }
    };

    //Look after toggleButton presses
    public void onToggleClicked(View view) {
        // Is the toggle on?
        on = ((ToggleButton) view).isChecked();

    }


    private void startIntent(String c, String form) {

        Intent intent=null;

        if(form == "details" | on ){
            intent=new Intent(this, ClientDetails.class);
        } else {
            intent=new Intent(this, JobList.class);
            //intent=new Intent(this, ItemEdit.class);
        }

        //clientHelper.close();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, c);
        intent.setType("text/plain");
        startActivity(intent);
        //cClient.close();
        //clientHelper=null;
    }

    //Handle a client selection within the listview.
    private AdapterView.OnItemClickListener onClientSelect = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int Position, long id) {

            cClient.moveToPosition(Position);
            String client=clientHelper.getID(cClient);
            startIntent(client, "select");

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_select, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_client).getActionView();
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

            case R.id.action_new:
                //Add a new client to the data base.
                int cid=clientHelper.insertClient("New");
                //Open details form.
                startIntent(Integer.toString(cid), "details");
                //cClient.close();
                //clientHelper=null;
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //*********** Helper Classes

    private class ListViewAdapter extends CursorAdapter{

        ListViewAdapter(Cursor c) {
            super(ClientSelect.this, c);
        }

        @Override
        public void bindView(View row, Context ctxt, Cursor c) {
            RowHolder rowHolder=(RowHolder)row.getTag();
            rowHolder.populateFrom(c, clientHelper);
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
            name=(TextView)row.findViewById(rowItem_left);
            address=(TextView)row.findViewById(rowItem_right);

        }

        void populateFrom(Cursor c, ClientHelper h) {
            name.setText(h.getName(c));
            address.setText(h.getAddress(c));
        }
    }

    public void onCatClick(View view) {

        Intent intent=new Intent(this, Catalogue.class);

        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
        //clientHelper=null;
        //cClient.close();
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        clientHelper = new ClientHelper(this);
        cClient = clientHelper.getAllClients();
        clientAdapter = new ListViewAdapter(cClient);
        clientList.setAdapter(clientAdapter);

        // Activity being restarted from stopped state
        //cClient.requery();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        cClient.close();
        cClient=null;
        clientHelper.close();
        clientHelper=null;
        clientAdapter=null;
        clientList.setAdapter(null);

        super.onSaveInstanceState(savedInstanceState);
    }
}

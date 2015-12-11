package com.robthechippy.estimast;

import android.app.Activity;
import android.content.ClipData;
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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;
import java.util.Objects;


public class
        JobList extends Activity {

    public  String clientID;
    private JobHelper jobHelper = null;
    private Cursor cJob = null;
    private ListView jobList = null;
    private ListViewAdapter jobAdapter = null;
    private String status="All";
    private String searchTxt="";
    private Boolean on=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_list);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        jobHelper = new JobHelper(this);

        //Check if returning with a saved instance.
        if (savedInstanceState != null) {
            //Reload saved client id.
            clientID=savedInstanceState.getString("clientID");
            status=savedInstanceState.getString("status");
            searchTxt=savedInstanceState.getString("searchTxt");
        }
        TextView tmp = null;
        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            clientID = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (clientID != null) {
                //Load the cursor with the client data.
                tmp=(TextView)findViewById(R.id.txtClientName);
                ClientHelper clientHelper = new ClientHelper(this);
                Cursor cClient=clientHelper.getCurrentClient(clientID);
                cClient.moveToFirst();
                tmp.setText(clientHelper.getName(cClient));

            }
        }
        cJob=jobHelper.getAllJobs(clientID);

        jobList=(ListView)findViewById(R.id.lstJobList);
        jobAdapter = new ListViewAdapter(cJob);
        jobList.setAdapter(jobAdapter);
        jobList.setOnItemClickListener(onJobSelect);

        Spinner spinner = (Spinner) findViewById(R.id.spn_job_status);
        // Spinner click listener
        spinner.setOnItemSelectedListener(status_listener);

        // Spinner Drop down elements
        List<String> labels = jobHelper.loadStatus();
        labels.add(0,"All");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


    }

    //Handle a client selection within the listview.
    private AdapterView.OnItemClickListener onJobSelect = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int Position, long id) {

            cJob.moveToPosition(Position);
            String job=jobHelper.getID(cJob);
            String client = jobHelper.getClientId(cJob);
            String[] data = {client, job};
            startIntent(data);

        }
    };

    private void startIntent(String[] c) {

        Intent intent=null;
        intent=new Intent(this, JobDetails.class);

        //jobHelper=null;
        cJob.close();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, c);
        intent.setType("text/plain");
        startActivity(intent);
    }


    //Status spinner handler
    private AdapterView.OnItemSelectedListener status_listener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void  onItemSelected (AdapterView<?> parent, View view, int position, long id){

            Object item = parent.getItemAtPosition(position);
            status = item.toString();
            if (status=="All"){status="";}
            cJob.close();
            cJob=jobHelper.searchJob(clientID, status, searchTxt);
            jobAdapter=new ListViewAdapter(cJob);
            jobList.setAdapter(jobAdapter);
        }

        @Override
        public void  onNothingSelected (AdapterView<?> parent){

        }
    };


    //Search widget handler.
    private SearchView.OnQueryTextListener search = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            cJob.close();
            searchTxt = newText;
            cJob=jobHelper.searchJob(clientID, status, searchTxt);
            jobAdapter=new ListViewAdapter(cJob);
            jobList.setAdapter(jobAdapter);
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jobs_list, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_job).getActionView();
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
            case R.id.action_new:
                int jid = jobHelper.insertJob("New", clientID);
                String[] dets = {clientID, Integer.toString(jid)};
                startIntent(dets);
                //jobHelper=null;
                cJob.close();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //*********** Helper Classes

    private class ListViewAdapter extends CursorAdapter {

        ListViewAdapter(Cursor c) {
            super(JobList.this, c);
        }

        @Override
        public void bindView(View row, Context ctxt, Cursor c) {
            RowHolder rowHolder=(RowHolder)row.getTag();
            rowHolder.populateFrom(c, jobHelper);
        }

        @Override
        public View newView(Context ctxt, Cursor c, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.threeelementrow, parent, false);
            RowHolder rowHolder=new RowHolder(row);

            row.setTag(rowHolder);

            return(row);
        }
    }

    //  This class acts as the row holder, and sets it up.
    private static class RowHolder {
        private TextView location=null;
        private TextView total=null;
        private TextView stat = null;

        RowHolder(View row) {
            location=(TextView)row.findViewById(R.id.txt_item_Left);
            total=(TextView)row.findViewById(R.id.txt_item_Center);
            stat = (TextView)row.findViewById(R.id.txt_item_Right);

        }

        void populateFrom(Cursor c, JobHelper h) {
            location.setText(h.getLocation(c));
            total.setText(Float.toString(h.getSearchTotal(c)));
            stat.setText(h.getSearchStatus(c));
        }
    }

    //Look after toggleButton presses
    public void onToggleClicked(View view) {
        // Is the toggle on?
        on = ((ToggleButton) view).isChecked();

    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        jobHelper=new JobHelper(this);
        cJob=jobHelper.searchJob(clientID, status, searchTxt);
        jobAdapter = new ListViewAdapter(cJob);
        jobList.setAdapter(jobAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        cJob.close();
        cJob=null;
        jobHelper.close();
        jobHelper=null;
        jobAdapter=null;
        savedInstanceState.putString("clientID", clientID);
        savedInstanceState.putString("status",status);
        savedInstanceState.putString("searchTxt",searchTxt);

        super.onSaveInstanceState(savedInstanceState);
    }

        @Override
    public void onDestroy(){


        super.onDestroy();

    }
}

package com.robthechippy.estimast;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jarvises on 7/02/2015.
 */
public class Fragment_Job_Details extends Fragment {

    private static final String KEY_POSITION="position";
    private static String jobId = "";
    private static String clientId="";
    private View view=null;
    private Boolean changed=false;
    private Menu men=null;
    private Calendar dateAndTime = Calendar.getInstance();
    private DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private EditText currentDateField=null;
    private int loaded=0;


    static Fragment_Job_Details newInstance(int position, String jobID, String clientID) {

        Fragment_Job_Details frag=new Fragment_Job_Details();

        jobId=jobID;
        clientId=clientID;

        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_job_details, container, false);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        loaded = 0;

        if (savedInstanceState != null) {
            //Reload saved client id.
            clientId=savedInstanceState.getString("clientID");
            jobId=savedInstanceState.getString("jobID");
        }

        //Load the clients name at top of page.
        TextView tmp=(TextView)view.findViewById(R.id.txtJobDetailsClient);
        ClientHelper clientHelper = new ClientHelper(getActivity());
        Cursor cClient=clientHelper.getCurrentClient(clientId);
        cClient.moveToFirst();
        tmp.setText(clientHelper.getName(cClient));
        clientHelper.close();

        Spinner spinner = (Spinner) view.findViewById(R.id.spnJobDetails);
        // Spinner Drop down elements
        JobHelper jobHelper = new JobHelper(getActivity());
        List<String> labels = jobHelper.loadStatus();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        loadPage(view);

        /************** Text change handler for search box ********/
        TextWatcher textChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do the database searching in here

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(loaded > 0) {
                    changed = true;
                    onPrepareOptionsMenu(men);
                }

            }
        };
        //Set text watchers
        EditText temp=(EditText)view.findViewById(R.id.txtJobLocation);
        temp.addTextChangedListener(textChanged);

        temp=(EditText)view.findViewById(R.id.txtJobNote);
        temp.addTextChangedListener(textChanged);

        temp=(EditText)view.findViewById(R.id.txtJobViewDate);
        temp.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if(event.getActionMasked()==MotionEvent.ACTION_DOWN) {
                    entDate(v);
                    changed=true;
                    onPrepareOptionsMenu(men);
                    return true;
                }
                return false;
             }
        });

        temp=(EditText)view.findViewById(R.id.txtJobDueDate);
        temp.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    entDate(v);
                    changed = true;
                    onPrepareOptionsMenu(men);
                    return true;
                }
                return false;
            }
        });

        // Spinner click listener
        spinner.setOnItemSelectedListener(status_listener);

        return(view);
    }

	static  String getTitle( int position){
        return("Details");
    }

    private void loadPage(View view){

        //Set up data
        JobHelper jobHelper = new JobHelper(getActivity());
        Cursor cJob=jobHelper.getCurrentJob(jobId);
        cJob.moveToFirst();

        //Load the data into text boxes
        EditText tmp=(EditText)view.findViewById(R.id.txtJobLocation);
        tmp.setText(jobHelper.getLocation(cJob));

        tmp=(EditText)view.findViewById(R.id.txtJobViewDate);
        tmp.setText(jobHelper.getviewedDate(cJob));

        tmp=(EditText)view.findViewById(R.id.txtJobDueDate);
        tmp.setText(jobHelper.getDueDate(cJob));

        tmp=(EditText)view.findViewById(R.id.txtJobNote);
        tmp.setText(jobHelper.getnote(cJob));

        TextView temp = (TextView)view.findViewById(R.id.txtJobTotal);
        //temp.setText(Float.toString(jobHelper.getTotal(cJob)));
        Cursor t=jobHelper.getTotal(jobId);
        t.moveToFirst();
        temp.setText(t.getString(0));
        if(jobHelper.getTotal(cJob)!=t.getFloat(0)){
            saveData();
        }

        Spinner spinner = (Spinner) view.findViewById(R.id.spnJobDetails);
        spinner.setSelection(getIndex(spinner, jobHelper.getstatus(cJob)));

        jobHelper.close();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_client_details, menu);
        MenuItem item=menu.findItem(R.id.action_save);
        item.setVisible(false);
        item=menu.findItem(R.id.action_undo);
        item.setVisible(false);
        men=menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                return true;

            case R.id.action_save:
                saveData();
                onPrepareOptionsMenu(men);
                return true;

            case R.id.action_undo:
                //Simply reload the client data
                loadPage(view);
                changed=false;
                onPrepareOptionsMenu(men);
                //invalidateOptionsMenu();
                return true;

            case R.id.action_delete:
                JobHelper jobHelper1 = new JobHelper(getActivity());
                jobHelper1.deleteJob(jobId);
                getActivity().finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {

        MenuItem itemSave = menu.findItem(R.id.action_save);
        MenuItem itemUndo = menu.findItem(R.id.action_undo);

        if(!(itemSave == null)) {
            if (changed) {
                itemSave.setVisible(true);
                itemUndo.setVisible(true);
            } else {
                itemSave.setVisible(false);
                itemUndo.setVisible(false);
            }
        }
        super.onPrepareOptionsMenu(menu);
        //return true;
    }

    private void  saveData(){
        //Update the Job details to the database.
        JobHelper jobHelper = new JobHelper(getActivity());
        EditText location=(EditText)view.findViewById(R.id.txtJobLocation);
        EditText viewed=(EditText)view.findViewById(R.id.txtJobViewDate);
        EditText due=(EditText)view.findViewById(R.id.txtJobDueDate);
        EditText note=(EditText)view.findViewById(R.id.txtJobNote);
        TextView total=(TextView)view.findViewById(R.id.txtJobTotal);
        Spinner status=(Spinner)view.findViewById(R.id.spnJobDetails);
        Object obj = status.getSelectedItem();

        String[] jid={jobId};
        jobHelper.updateJob(jid,
                clientId,
                location.getText().toString(),
                note.getText().toString(),
                obj.toString(),
                viewed.getText().toString(),
                Float.valueOf(total.getText().toString()),
                due.getText().toString());
        changed=false;
        jobHelper.close();
    }

    public void entDate(View v) {

        currentDateField = (EditText) v;

        try{
            dateAndTime.setTime(fmtDateAndTime.parse(currentDateField.getText().toString()));
        } catch (ParseException e) {
            dateAndTime.getTime();
        }

        new DatePickerDialog(getActivity(), dateCallBack, dateAndTime.get(Calendar.YEAR),
                                dateAndTime.get(Calendar.MONTH),
                                dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener dateCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            currentDateField.setText(sdf.format(dateAndTime.getTime()));
        }
    };

    //Status spinner handler
    private AdapterView.OnItemSelectedListener status_listener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void  onItemSelected (AdapterView<?> parent, View view, int position, long id){

            if(loaded > 0) {

                String tmp = ((TextView) view).getText().toString();
                JobHelper jobHelper = new JobHelper(getActivity());
                Cursor cJob = jobHelper.getCurrentJob(jobId);
                cJob.moveToFirst();
                if (!(tmp.equals(jobHelper.getstatus(cJob)))) {
                    changed = true;
                    onPrepareOptionsMenu(men);
                }
                jobHelper.close();
            }else {
                loaded = loaded + 1;
            }

        }

        @Override
        public void  onNothingSelected (AdapterView<?> parent){

        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current clientId
        savedInstanceState.putString("clientID", clientId);
        savedInstanceState.putString("jobID", jobId);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        loadPage(view);

    }

}

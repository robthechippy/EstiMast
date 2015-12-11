package com.robthechippy.estimast;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


public class Fragment_Section_Details extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String jobId = "";
    private static String clientId = "";
    private static String sectionId = "";
    private View view=null;
    private Boolean changed=false;
    private Menu men=null;
    private int loaded;

    public static Fragment_Section_Details newInstance(int position, String jobID, String clientID, String sectionID) {
        Fragment_Section_Details fragment = new Fragment_Section_Details();

        jobId = jobID;
        clientId = clientID;
        sectionId = sectionID;

        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Section_Details() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_section_details, container, false);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        loaded = 0;

        if (savedInstanceState != null) {
            //Reload saved client id.
            clientId=savedInstanceState.getString("clientID");
            jobId=savedInstanceState.getString("jobID");
            sectionId = savedInstanceState.getString("sectionId");
        }

        //Load the clients name at top of page.
        TextView tmp=(TextView)view.findViewById(R.id.txtSecClientName);
        ClientHelper clientHelper = new ClientHelper(getActivity());
        Cursor cClient=clientHelper.getCurrentClient(clientId);
        cClient.moveToFirst();
        tmp.setText(clientHelper.getName(cClient));
        clientHelper.close();

        //Load the jobs name at top of page
        tmp = (TextView)view.findViewById(R.id.txtSecJobName);
        JobHelper jobHelper = new JobHelper(getActivity());
        Cursor cJob = jobHelper.getCurrentJob(jobId);
        cJob.moveToFirst();
        tmp.setText(jobHelper.getLocation(cJob));
        jobHelper.close();


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.lengths, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        Spinner spinnerA = (Spinner) view.findViewById(R.id.spnSecLen);
        spinnerA.setAdapter(adapter);

        Spinner spinnerB = (Spinner) view.findViewById(R.id.spnSecWidth);
        spinnerB.setAdapter(adapter);

        Spinner spinnerC = (Spinner) view.findViewById(R.id.spnSecHeight);
        spinnerC.setAdapter(adapter);

        loadPage(view);
        spinnerA.setOnItemSelectedListener(status_listener);
        spinnerB.setOnItemSelectedListener(status_listener);
        spinnerC.setOnItemSelectedListener(status_listener);

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

                if(loaded==3){
                    changed = true;
                    onPrepareOptionsMenu(men);
                }
            }
        };

        //Set text watchers
        EditText temp=(EditText)view.findViewById(R.id.txtSecName);
        temp.addTextChangedListener(textChanged);

        temp=(EditText)view.findViewById(R.id.txtSecLength);
        temp.addTextChangedListener(textChanged);

        temp=(EditText)view.findViewById(R.id.txtSecWidth);
        temp.addTextChangedListener(textChanged);

        temp=(EditText)view.findViewById(R.id.txtSecHeight);
        temp.addTextChangedListener(textChanged);

        temp=(EditText)view.findViewById(R.id.txtSecNote);
        temp.addTextChangedListener(textChanged);


        return(view);
    }

    private void loadPage(View view) {
        SectionHelper sectionHelper = new SectionHelper(getActivity());
        Cursor cSection = sectionHelper.getCurrentSection(sectionId);
        cSection.moveToFirst();

        //Load the data into text boxes
        EditText tmp=(EditText)view.findViewById(R.id.txtSecName);
        tmp.setText(sectionHelper.getSection(cSection));

        tmp=(EditText)view.findViewById(R.id.txtSecLength);
        tmp.setText(sectionHelper.getLength(cSection).toString());

        tmp=(EditText)view.findViewById(R.id.txtSecWidth);
        tmp.setText(sectionHelper.getWidth(cSection).toString());

        tmp=(EditText)view.findViewById(R.id.txtSecHeight);
        tmp.setText(sectionHelper.getHeight(cSection).toString());

        TextView temp = (TextView)view.findViewById(R.id.txtSecNote);
        temp.setText(sectionHelper.getnote(cSection));

        //TODO calculate the total
        Cursor t=sectionHelper.getSecTotal(sectionId);
        t.moveToFirst();
        temp=(TextView)view.findViewById(R.id.txtSecTotal);
        temp.setText(t.getString(0));
        if(t.getFloat(0)!=sectionHelper.getTotal(cSection)){
            saveData();
        }

        CheckBox chk = (CheckBox)view.findViewById(R.id.chkSecExtra);
        int x = sectionHelper.getExtra(cSection);

        if(x==1){
            chk.setChecked(true);
        }else{
            chk.setChecked(false);
        }

        Spinner spinner = (Spinner) view.findViewById(R.id.spnSecLen);
        spinner.setSelection(getIndex(spinner, sectionHelper.getLenPost(cSection)));

        spinner = (Spinner) view.findViewById(R.id.spnSecWidth);
        spinner.setSelection(getIndex(spinner, sectionHelper.getWidthPost(cSection)));

        spinner = (Spinner) view.findViewById(R.id.spnSecHeight);
        spinner.setSelection(getIndex(spinner, sectionHelper.getHeightPost(cSection)));

        sectionHelper.close();

    }

    //Status spinner handler
    private AdapterView.OnItemSelectedListener status_listener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void  onItemSelected (AdapterView<?> parent, View v, int position, long id){

            if(loaded==3) {

                String postfix=null;
                String tmp = ((TextView) v).getText().toString();
                SectionHelper sectionHelper = new SectionHelper(getActivity());
                Cursor cSec = sectionHelper.getCurrentSection(sectionId);
                cSec.moveToFirst();
                if(((Spinner)parent).getId()==view.findViewById(R.id.spnSecLen).getId()){
                    postfix=sectionHelper.getLenPost(cSec);
                }
                if(((Spinner)parent).getId()==view.findViewById(R.id.spnSecWidth).getId()){
                    postfix=sectionHelper.getWidthPost(cSec);
                }
                if(((Spinner)parent).getId()==view.findViewById(R.id.spnSecHeight).getId()){
                    postfix=sectionHelper.getHeightPost(cSec);
                }
                if(!(postfix.equals(tmp))) {
                    changed = true;
                    onPrepareOptionsMenu(men);
                }
                sectionHelper.close();
            }else{
                loaded=loaded+1;
            }
        }

        @Override
        public void  onNothingSelected (AdapterView<?> parent){

        }
    };

    static  String getTitle( int position){
        return("Details");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current clientId
        savedInstanceState.putString("clientID", clientId);
        savedInstanceState.putString("jobID", jobId);
        savedInstanceState.putString("sectionId", sectionId);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
                //Update the section details to the database.
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
                SectionHelper sechelp = new SectionHelper(getActivity());
                sechelp.deleteSection(sectionId);
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

    private void saveData(){
        SectionHelper sectionHelper = new SectionHelper(getActivity());
        EditText section=(EditText)view.findViewById(R.id.txtSecName);
        EditText length=(EditText)view.findViewById(R.id.txtSecLength);
        EditText width=(EditText)view.findViewById(R.id.txtSecWidth);
        EditText height=(EditText)view.findViewById(R.id.txtSecHeight);
        TextView total=(TextView)view.findViewById(R.id.txtSecTotal);
        Spinner spn=(Spinner)view.findViewById(R.id.spnSecLen);
        Object len = spn.getSelectedItem();
        spn=(Spinner)view.findViewById(R.id.spnSecWidth);
        Object w = spn.getSelectedItem();
        spn=(Spinner)view.findViewById(R.id.spnSecHeight);
        Object h = spn.getSelectedItem();
        TextView note=(TextView)view.findViewById(R.id.txtSecNote);
        CheckBox extra=(CheckBox)view.findViewById(R.id.chkSecExtra);

        String[] sid={sectionId};
        sectionHelper.updateSection(sid,
                clientId,
                jobId,
                section.getText().toString(),
                note.getText().toString(),
                Float.valueOf(length.getText().toString()),
                Float.valueOf(width.getText().toString()),
                Float.valueOf(total.getText().toString()),
                Float.valueOf(height.getText().toString()),
                extra.isChecked(),
                len.toString(),
                w.toString(),
                h.toString()
        );

        changed=false;
        sectionHelper.close();
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

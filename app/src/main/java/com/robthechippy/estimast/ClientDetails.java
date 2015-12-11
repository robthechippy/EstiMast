package com.robthechippy.estimast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ClientDetails extends Activity {

    private ClientHelper clientHelper = null;
    private Cursor cClient = null;
    public String clientID;
    private Boolean changed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_details);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        clientHelper = new ClientHelper(this);

        //Check if returning with a saved instance.
        if (savedInstanceState != null) {
            //Reload saved client id.
            clientID=savedInstanceState.getString("clientID");
        }

        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
           clientID = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (clientID != null) {
            //Load the cursor with the client data.
            cClient=clientHelper.getCurrentClient(clientID);
            cClient.moveToFirst();
            loadPage();
            }
        }


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
                changed = true;
                invalidateOptionsMenu();

            }
        };

        EditText name=(EditText)findViewById(R.id.txt_cName);
        name.addTextChangedListener(textChanged);
        EditText address=(EditText)findViewById(R.id.txt_cAddress);
        address.addTextChangedListener(textChanged);
        EditText ph=(EditText)findViewById(R.id.txt_cPhone);
        ph.addTextChangedListener(textChanged);
        EditText mob=(EditText)findViewById(R.id.txt_cMob);
        mob.addTextChangedListener(textChanged);
        EditText email=(EditText)findViewById(R.id.txt_cEmail);
        email.addTextChangedListener(textChanged);
        EditText note=(EditText)findViewById(R.id.txt_cNote);
        note.addTextChangedListener(textChanged);

    }

    //Load the page data here.
    private void loadPage() {

        //Start by setting the text of the text views.
        EditText name=(EditText)findViewById(R.id.txt_cName);
        name.setText(clientHelper.getName(cClient));
        EditText address=(EditText)findViewById(R.id.txt_cAddress);
        address.setText(clientHelper.getAddress(cClient));
        EditText ph=(EditText)findViewById(R.id.txt_cPhone);
        ph.setText(clientHelper.getPh(cClient));
        EditText mob=(EditText)findViewById(R.id.txt_cMob);
        mob.setText(clientHelper.getMob(cClient));
        EditText email=(EditText)findViewById(R.id.txt_cEmail);
        email.setText(clientHelper.getEmail(cClient));
        EditText note=(EditText)findViewById(R.id.txt_cNote);
        note.setText(clientHelper.getNotes(cClient));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_details, menu);
        MenuItem item=menu.findItem(R.id.action_save);
        item.setVisible(false);
        item=menu.findItem(R.id.action_undo);
        item.setVisible(false);
        return true;
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
                //Update the client details to the database.
                EditText name=(EditText)findViewById(R.id.txt_cName);
                EditText address=(EditText)findViewById(R.id.txt_cAddress);
                EditText ph=(EditText)findViewById(R.id.txt_cPhone);
                EditText mob=(EditText)findViewById(R.id.txt_cMob);
                EditText email=(EditText)findViewById(R.id.txt_cEmail);
                EditText note=(EditText)findViewById(R.id.txt_cNote);
                String[] cid={clientID};
                clientHelper.updateClient(cid,
                                         name.getText().toString(),
                                         address.getText().toString(),
                                         mob.getText().toString(),
                                         ph.getText().toString(),
                                         email.getText().toString(),
                                         note.getText().toString());
                changed=false;
                invalidateOptionsMenu();
                return true;

            case R.id.action_undo:
                //Simply reload the client data
                loadPage();
                changed=false;
                invalidateOptionsMenu();
                return true;

            case R.id.action_delete:

                //Delete the client details, all jobs, items etc
                clientHelper.deleteClient(clientID);
                finish();

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean  onPrepareOptionsMenu (Menu menu){

        MenuItem itemSave=menu.findItem(R.id.action_save);
        MenuItem itemUndo=menu.findItem(R.id.action_undo);

        if(changed){
            itemSave.setVisible(true);
            itemUndo.setVisible(true);
        } else {
            itemSave.setVisible(false);
            itemUndo.setVisible(false);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current clientId
        savedInstanceState.putString("clientID", clientID);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onJobsClick(View v) {

        Intent intent=new Intent(this, JobList.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, clientID);
        intent.setType("text/plain");
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        clientHelper.close();
    }
}

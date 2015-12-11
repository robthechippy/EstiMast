package com.robthechippy.estimast;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;


public class SectionDetails extends Activity {

    ViewPager mViewPager;

    private String clientID;
    private String jobId;
    private String sectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_details);
        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        //Check if returning with a saved instance.
        if (savedInstanceState != null) {
            //Reload saved client id.
            clientID=savedInstanceState.getString("clientID");
            jobId=savedInstanceState.getString("jobID");
            sectionId = savedInstanceState.getString("sectionId");
        }

        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            String[] data = intent.getStringArrayExtra(Intent.EXTRA_EMAIL);
            if (data != null) {
                clientID = data[0];
                jobId = data[1];
                sectionId = data[2];

            }
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new sectionDetailsAdapter(getFragmentManager(), jobId, clientID, sectionId));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_section_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current clientId
        savedInstanceState.putString("clientID", clientID);
        savedInstanceState.putString("jobID", jobId);
        savedInstanceState.putString("sectionId", sectionId);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}

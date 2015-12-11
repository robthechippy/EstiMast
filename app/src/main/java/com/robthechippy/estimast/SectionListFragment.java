package com.robthechippy.estimast;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;



public class SectionListFragment extends ListFragment implements OnItemClickListener {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String KEY_POSITION="position";
    private static String clientId;
    private static String jobId;
    private View view;

    //private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListViewAdapter mAdapter=null;
    private SectionHelper sectionHelper;
    private Cursor cSection;

    // TODO: Rename and change types of parameters
    public static SectionListFragment newInstance(int position, String jobID, String clientID) {
        SectionListFragment fragment = new SectionListFragment();

        clientId = clientID;
        jobId = jobID;

        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;

    }

    static  String getTitle( int position){
        return("Sections");
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SectionListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_section_list, container, false);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        //Check if returning with a saved instance.
        if (savedInstanceState != null) {
            //Reload saved client id.
            clientId=savedInstanceState.getString("clientID");
            jobId=savedInstanceState.getString("jobID");
        }

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        sectionHelper = new SectionHelper(getActivity());
        cSection = sectionHelper.getAllSections(jobId);
        mAdapter = new ListViewAdapter(cSection);
        setListAdapter(mAdapter);
        //getListView().setOnItemClickListener(this);


        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener((AdapterView.OnItemClickListener) onSecSelect);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


        //setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_client_select, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_client).getActionView();
        searchView.setOnQueryTextListener(search);
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

            case R.id.action_new:
                //TODO check if section is extra
                //Add a new section to the database
                int sid = sectionHelper.insertSection("New", false, clientId, jobId);
                String sidS=Integer.toString(sid);
                Intent intent = new Intent(getActivity(), SectionDetails.class);
                String[] c = {clientId, jobId, sidS};
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, c);
                intent.setType("text/plain");
                startActivity(intent);
                sectionHelper=null;
                cSection.close();

        }

        return super.onOptionsItemSelected(item);
    }

    //Handles search view widget
    private SearchView.OnQueryTextListener search = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            cSection.close();
            cSection=sectionHelper.searchSection(jobId,newText);
            mAdapter=new ListViewAdapter(cSection);
            mListView.setAdapter(mAdapter);
            return true;
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    //private AdapterView.OnItemClickListener onSecSelect = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            cSection.moveToPosition(position);
            String sec = sectionHelper.getID(cSection);
            Intent intent = new Intent(getActivity(), SectionDetails.class);
            String[] c = {clientId, jobId, sec};
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, c);
            intent.setType("text/plain");
            startActivity(intent);
            sectionHelper=null;
            cSection.close();

        }



    //*********** Helper Classes

    private class ListViewAdapter extends CursorAdapter {

        ListViewAdapter(Cursor c) {
            super(getActivity(), c);
        }

        @Override
        public void bindView(View row, Context ctxt, Cursor c) {
            RowHolder rowHolder=(RowHolder)row.getTag();
            rowHolder.populateFrom(c, sectionHelper);
        }

        @Override
        public View newView(Context ctxt, Cursor c, ViewGroup parent) {
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View row=inflater.inflate(R.layout.sectionrow, parent, false);
            RowHolder rowHolder=new RowHolder(row);

            row.setTag(rowHolder);

            return(row);
        }
    }

    //  This class acts as the row holder, and sets it up.
    private static class RowHolder {
        private TextView section=null;
        private TextView total=null;
        private TextView extra = null;

        RowHolder(View row) {
            section=(TextView)row.findViewById(R.id.txtJobSecSec);
            total=(TextView)row.findViewById(R.id.txtJobSecTotal);
            extra = (TextView)row.findViewById(R.id.chkJobSecExtra);

        }

        void populateFrom(Cursor c, SectionHelper h) {
            section.setText(h.getSection(c));
            total.setText(Float.toString(h.getSearchTotal(c)));
            int tmp = h.getSearchExtra(c);
            if(tmp==1){
                extra.setText("Extra");
            }else{
                extra.setText("");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current clientId
        savedInstanceState.putString("clientID", clientId);
        savedInstanceState.putString("jobID", jobId);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Activity being restarted from stopped state
        //cSection.requery();
		sectionHelper=new SectionHelper(getActivity());
        cSection=sectionHelper.getAllSections(jobId);
        mAdapter = new ListViewAdapter(cSection);
      	setListAdapter(mAdapter);
    
    }

}

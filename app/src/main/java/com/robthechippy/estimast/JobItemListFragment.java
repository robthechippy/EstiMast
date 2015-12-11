package com.robthechippy.estimast;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


public class JobItemListFragment extends ListFragment implements OnItemClickListener {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String KEY_POSITION="position";
    private static String clientId;
    private static String jobId;
    private static String sectionId;
    private View view;
    private static String searchTxt;

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
    private JobItemHelper jobItemHelper;
    private Cursor cItem;
	
	
    public static JobItemListFragment newInstance(int position, String jobID, String clientID, String sectionID) {
        JobItemListFragment fragment = new JobItemListFragment();

        clientId = clientID;
        jobId = jobID;
        sectionId = sectionID;
        searchTxt="";

        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;

    }

    static  String getTitle( int position){
        return("Items");
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JobItemListFragment() {
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
            sectionId=savedInstanceState.getString("sectionID");
        }

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        jobItemHelper = new JobItemHelper(getActivity());
        cItem = jobItemHelper.getAllItems(sectionId);
        mAdapter = new ListViewAdapter(cItem);
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
        inflater.inflate(R.menu.menu_job_item_select, menu);
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
                //Add a new item to the database
                int sid = jobItemHelper.insertItem("New", clientId, jobId, sectionId);
                String iId=Integer.toString(sid);
                Intent intent = new Intent(getActivity(), JobItemsEdit.class);
                String[] c = {iId, clientId, jobId, sectionId, searchTxt, ""};
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, c);
                intent.setType("text/plain");
                startActivity(intent);
                jobItemHelper=null;
                cItem.close();
                return true;

            case R.id.action_select:
                Intent intent1 = new Intent(getActivity(), jobItemsSelect.class);
                String[] c1 = {clientId, jobId, sectionId};
				intent1.setAction(intent1.ACTION_SEND);
                intent1.putExtra(Intent.EXTRA_EMAIL, c1);
                intent1.setType("text/plain");
                startActivity(intent1);
				jobItemHelper = null;
				cItem.close();
                return true;

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
            cItem.close();
            cItem=jobItemHelper.getSearchItemsList(sectionId,newText);
            searchTxt=newText;
            mAdapter=new ListViewAdapter(cItem);
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

            cItem.moveToPosition(position);
            String sec = jobItemHelper.getID(cItem);
            Intent intent = new Intent(getActivity(), JobItemsEdit.class);
            String[] c = {sec, clientId, jobId, sectionId, searchTxt, ""};
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, c);
            intent.setType("text/plain");
            startActivity(intent);
            jobItemHelper=null;
            cItem.close();

        }



    //*********** Helper Classes

    private class ListViewAdapter extends CursorAdapter {

        ListViewAdapter(Cursor c) {
            super(getActivity(), c);
        }

        @Override
        public void bindView(View row, Context ctxt, Cursor c) {
            RowHolder rowHolder=(RowHolder)row.getTag();
            rowHolder.populateFrom(c, jobItemHelper);
        }

        @Override
        public View newView(Context ctxt, Cursor c, ViewGroup parent) {
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View row=inflater.inflate(R.layout.threeelementrow, parent, false);
            RowHolder rowHolder=new RowHolder(row);

            row.setTag(rowHolder);

            return(row);
        }
    }

    //  This class acts as the row holder, and sets it up.
    private static class RowHolder {
        private TextView code=null;
        private TextView desc=null;
        private TextView total = null;

        RowHolder(View row) {
            code=(TextView)row.findViewById(R.id.txt_item_Left);
            desc=(TextView)row.findViewById(R.id.txt_item_Center);
            total = (TextView)row.findViewById(R.id.txt_item_Right);

        }

        void populateFrom(Cursor c, JobItemHelper h) {
            code.setText(h.getCodeList(c));
            desc.setText(h.getDescriptionList(c));
            total.setText(String.valueOf(h.getTotalList(c)));

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current clientId
        savedInstanceState.putString("clientID", clientId);
        savedInstanceState.putString("jobID", jobId);
        savedInstanceState.putString("sectionID", sectionId);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        jobItemHelper=new JobItemHelper(getActivity());
        cItem=jobItemHelper.getAllItems(sectionId);
        mAdapter = new ListViewAdapter(cItem);
      	setListAdapter(mAdapter);
		
    }

}

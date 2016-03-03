package com.robthechippy.estimast;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;


public class fragmentcatalogueitems extends ListFragment implements OnItemClickListener {

    private View view;
    private ListView mListView;
    private ListViewAdapter mAdapter=null;
    private ItemHelper itemHelper;
    private Cursor cItems;
    private String category="";
    private String searchtxt = "";


    public static fragmentcatalogueitems newInstance() {
        fragmentcatalogueitems fragment = new fragmentcatalogueitems();

        return fragment;
    }

    public fragmentcatalogueitems() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_catalogue_items, container, false);

        setHasOptionsMenu(true);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        itemHelper = new ItemHelper(getActivity());
        cItems = itemHelper.getAllItems();
        mAdapter = new ListViewAdapter(cItems);
        setListAdapter(mAdapter);
        //itemHelper.close();

        return(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        Spinner spinner = (Spinner) view.findViewById(R.id.spnItemListCat);
        // Spinner click listener
        spinner.setOnItemSelectedListener(cat_listener);

        CatagoryHelper catagoryHelper=new CatagoryHelper(getActivity());
        // Spinner Drop down elements
        List<String> labels = catagoryHelper.getAllLabels();
        labels.add(0,"All");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        catagoryHelper.close();
        catagoryHelper=null;

        //setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);

    }


    //*********** Helper Classes

    private class ListViewAdapter extends CursorAdapter {

        ListViewAdapter(Cursor c) {
            super(getActivity(), c);
        }

        @Override
        public void bindView(View row, Context ctxt, Cursor c) {
            RowHolder rowHolder=(RowHolder)row.getTag();
            rowHolder.populateFrom(c, itemHelper);
        }

        @Override
        public View newView(Context ctxt, Cursor c, ViewGroup parent) {
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View row=inflater.inflate(R.layout.twoelementrow, parent, false);
            RowHolder rowHolder=new RowHolder(row);

            row.setTag(rowHolder);

            return(row);
        }
    }

    //  This class acts as the row holder, and sets it up.
    private static class RowHolder {
        private TextView code=null;
        private TextView desc=null;


        RowHolder(View row) {
            code=(TextView)row.findViewById(R.id.rowItem_left);
            desc=(TextView)row.findViewById(R.id.rowItem_right);


        }

        void populateFrom(Cursor c, ItemHelper h) {
            code.setText(h.getCode(c));
            desc.setText(h.getDescription(c));

        }
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
                //Add a new item to the database
                int itemid = itemHelper.insertItem("New");
                String itemTxt=Integer.toString(itemid);
                Intent intent = new Intent(getActivity(), ItemEdit.class);
                String[] c = {itemTxt, category, searchtxt};
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, c);
                intent.setType("text/plain");
                startActivity(intent);
                itemHelper=null;
                cItems.close();
				return(true);
				
			case R.id.action_catedit:
				//Open the catagory efit screen.
				Intent intente = new Intent(getActivity(),CatagoryEdit.class);
				intente.setAction(Intent.ACTION_VIEW);
				startActivity(intente);
				return(true);

        }

        return super.onOptionsItemSelected(item);
    }

    //private AdapterView.OnItemClickListener onSecSelect = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        cItems.moveToPosition(position);
        String item = itemHelper.getID(cItems);
        Intent intent = new Intent(getActivity(), ItemEdit.class);
        String[] c = {item, category, searchtxt};
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, c);
        intent.setType("text/plain");
        startActivity(intent);
        cItems.close();
        itemHelper=null;
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
            mAdapter=new ListViewAdapter(cItems);
            mListView.setAdapter(mAdapter);
            return true;
        }
    };

    //Status spinner handler
    private AdapterView.OnItemSelectedListener cat_listener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void  onItemSelected (AdapterView<?> parent, View view, int position, long id){

            Object item = parent.getItemAtPosition(position);
            category = item.toString();
            cItems.close();
            cItems=itemHelper.getSearchItemsList(category, searchtxt);
            mAdapter=new ListViewAdapter(cItems);
            mListView.setAdapter(mAdapter);
            //itemHelper.close();
        }

        @Override
        public void  onNothingSelected (AdapterView<?> parent){

        }
    };

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        mListView = (ListView) view.findViewById(android.R.id.list);
        if(itemHelper==null){
            itemHelper = new ItemHelper(getActivity());
        }
        cItems=itemHelper.getSearchItemsList(category, searchtxt);
        mAdapter = new ListViewAdapter(cItems);
        setListAdapter(mAdapter);

    }

    static String getTitle(){
        return("Items");
    }
}

package com.robthechippy.estimast;

import java.util.List;
import android.app.*;
import android.os.Bundle;
import android.database.Cursor;
import android.view.*;
import android.widget.*;
import android.text.*;

public class ItemEdit extends Activity
{

	//Define the spinners.
	Spinner spnCat = null;
	CatagoryHelper catHelper = null;
	
	Spinner spnTax = null;
	TaxHelper taxHelper = null;
	
	Spinner spnUnit = null;
	UnitHelper unitHelper = null;
	
	Spinner spnType = null;
	ItemTypeHelper  typeHelper= null;
	
	ItemHelper itemHelper = null;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);
	
		/******* Start setting up the spinners. *******/
        
        catHelper=new CatagoryHelper(this);
		spnCat=(Spinner)findViewById(R.id.spn_catagory);
		
		taxHelper = new TaxHelper(this);
		spnTax = (Spinner)findViewById(R.id.spn_item_taxType);
		spnTax.setOnItemSelectedListener(onTaxSelect);
		
		unitHelper = new UnitHelper(this);
		spnUnit = (Spinner)findViewById(R.id.spn_itemUnit);
		spnUnit.setOnItemSelectedListener(onUnitSelect);
		
		typeHelper = new ItemTypeHelper(this);
		spnType = (Spinner)findViewById(R.id.spn_item_type);
		
		itemHelper = new ItemHelper(this);
		
		/******* Setup tabs *******/
		
		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		
		tabs.setup();
		
		TabHost.TabSpec spec=tabs.newTabSpec("tag1");
		
		spec.setContent(R.id.tabA);
		spec.setIndicator("Main");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag2");
		
		spec.setContent(R.id.tabB);
		spec.setIndicator("Sizes");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag3");
		
		spec.setContent(R.id.tabC);
		spec.setIndicator("Supplier");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag4");
		
		spec.setContent(R.id.tabD);
		spec.setIndicator("Stock");
		tabs.addTab(spec);
		
		/******* Define some text change handlers. *******/
		
		TextWatcher costChanged=new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				
				/******* Look after what happens when the unit cost changes. *******/
				
				float unitcost;
				float markupPercent;
				float taxpercent;
				EditText markup=(EditText)findViewById(R.id.txt_markup);
				EditText taxamount=(EditText)findViewById(R.id.txt_item_tax_amount);
				EditText saleprice=(EditText)findViewById(R.id.txt_sale_price);
				EditText profit=(EditText)findViewById(R.id.txt_markup_profits);
				EditText taxitempercent=(EditText)findViewById(R.id.txt_tax_percent);
				
				if (s.length() == 0) {
					taxamount.setText("0.00");
					saleprice.setText("0.00");
					profit.setText("0.00");
				}
				else {
					unitcost=Float.parseFloat(s.toString());
					// Check that markup percent is set.
					if (markup.getText().length() == 0) {
						markupPercent=0;
					}
					else {
						markupPercent= Float.parseFloat(markup.getText().toString());
						profit.setText("0.00");
					}
					// Check if there are any taxs
					CheckBox tax=(CheckBox)findViewById(R.id.chk_taxable);
					if (tax.isChecked()) {
						taxpercent=Float.parseFloat(taxitempercent.getText().toString());
					}
					else {
						taxpercent = 0;
						taxamount.setText("0.00");
					}
					calcSalePrice( unitcost, markupPercent, taxpercent);
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		EditText cost=(EditText)findViewById(R.id.txt_cost_unit);
		cost.addTextChangedListener(costChanged);
		
		TextWatcher markupChanged=new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				
				/******* Look after what happens when the markup percent changes. *******/
				
				float unitcost;
				float markupPercent;
				float taxpercent;
				EditText unitcostamount=(EditText)findViewById(R.id.txt_cost_unit);
				EditText taxamount=(EditText)findViewById(R.id.txt_item_tax_amount);
				//EditText saleprice=(EditText)findViewById(R.id.txt_sale_price);
				EditText profit=(EditText)findViewById(R.id.txt_markup_profits);
				EditText taxitempercent=(EditText)findViewById(R.id.txt_tax_percent);
				
				if (s.length() == 0) {
					markupPercent=0;
					profit.setText("0.00");
				}
				else {
					markupPercent=Float.parseFloat(s.toString());
					}
				// Check that the unitcost is set.
				if (unitcostamount.getText().length() == 0) {
					unitcost=0;
					unitcostamount.setText("0.00");
				}
				else {
					unitcost= Float.parseFloat(unitcostamount.getText().toString());
					
				}
				// Check if there are any taxs
				CheckBox tax=(CheckBox)findViewById(R.id.chk_taxable);
				if (tax.isChecked()) {
					taxpercent=Float.parseFloat(taxitempercent.getText().toString());
				}
				else {
					taxpercent = 0;
					taxamount.setText("0.00");
				}
				calcSalePrice( unitcost, markupPercent, taxpercent);
				
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		EditText markup=(EditText)findViewById(R.id.txt_markup);
		markup.addTextChangedListener(markupChanged);
		
		
		loadPage();
		
		
    }
	
	public void onTaxClicked(View view){
		
		boolean checked = ((CheckBox) view).isChecked();
		float unitcost;
		float markupPercent;
		float taxpercent;
		EditText unitcostamount=(EditText)findViewById(R.id.txt_cost_unit);
		EditText taxamount=(EditText)findViewById(R.id.txt_item_tax_amount);
		//EditText saleprice=(EditText)findViewById(R.id.txt_sale_price);
		//EditText profit=(EditText)findViewById(R.id.txt_markup_profits);
		EditText taxitempercent=(EditText)findViewById(R.id.txt_tax_percent);
		EditText markup=(EditText)findViewById(R.id.txt_markup);
		
		if (checked) {
			TextView tmp= (TextView)findViewById(R.id.lbl_taxType);
			tmp.setVisibility(View.VISIBLE);
			tmp= (TextView)findViewById(R.id.lbl_tax_percent);
			tmp.setVisibility(View.VISIBLE);
			tmp= (TextView)findViewById(R.id.txt_tax_percent);
			tmp.setVisibility(View.VISIBLE);
			Spinner spn= (Spinner)findViewById(R.id.spn_item_taxType);
			spn.setVisibility(View.VISIBLE);
			taxpercent=Float.parseFloat(taxitempercent.getText().toString());
		}
		else {
			TextView tmp= (TextView)findViewById(R.id.lbl_taxType);
			tmp.setVisibility(View.INVISIBLE);
			tmp= (TextView)findViewById(R.id.lbl_tax_percent);
			tmp.setVisibility(View.INVISIBLE);
			tmp= (TextView)findViewById(R.id.txt_tax_percent);
			tmp.setVisibility(View.INVISIBLE);
			Spinner spn= (Spinner)findViewById(R.id.spn_item_taxType);
			spn.setVisibility(View.INVISIBLE);
			taxpercent=0;
		}
		
		if (unitcostamount.getText().length() == 0) {
			unitcost=0;
			unitcostamount.setText("0.00");
		}
		else {
			unitcost= Float.parseFloat(unitcostamount.getText().toString());

		}
		
		// Check that markup percent is set.
		if (markup.getText().length() == 0) {
			markupPercent=0;
		}
		else {
			markupPercent= Float.parseFloat(markup.getText().toString());
		}
		
		calcSalePrice( unitcost, markupPercent, taxpercent);
	}
	
	private void calcSalePrice( Float unitCost, Float markup, Float taxPercent) {

		/******* Do the actual calculations to work out the amount of markup,
			tax to charge, and the final sale price. *******/
		
		CheckBox tax=(CheckBox)findViewById(R.id.chk_taxable);
		EditText taxamount=(EditText)findViewById(R.id.txt_item_tax_amount);
		EditText saleprice=(EditText)findViewById(R.id.txt_sale_price);
		EditText profit=(EditText)findViewById(R.id.txt_markup_profits);
		double profitAmount;
		double taxprice;
		
		// Work out any markup first.
		if(markup > 0 ) {
				profitAmount = (markup/100.00) * unitCost;
			}
			else {
				profitAmount = 0.0;
				profit.setText("0.00");
			}
		profit.setText(Double.toString(profitAmount));
		
		// Now work out what tax to pay.
		if (tax.isChecked()) {
			taxprice=(taxPercent/100.00)*(unitCost+profitAmount);
		}
		else {
			taxprice=0.00;
		}
		taxamount.setText(Double.toString(taxprice));
		
		//Now we know the final price.
		saleprice.setText(Double.toString( unitCost + profitAmount + taxprice));
		
	}
	
	
	public void loadPage() {
		
		/* Setup catagory spinner */
		loadSpnCatData();

		/* Setup tax spinner */
		loadSpnTaxData();

		/* Setup unit spinner */
		loadSpnUnitData();
		
		/* Setup item type spinner */
		loadSpnTypeData();
		
	}
	
	private void loadSpnCatData() {
		
		/******* Load the Category spinner with data. *******/
		// Spinner Drop down elements
		List<String> lables = catHelper.getAllLabels();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
			// Drop down layout style - list view with radio button
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spnCat.setAdapter(dataAdapter);
		
	}
	
	private void loadSpnTaxData() {
		
		/******* Load the different tax options. *******/
		// Spinner Drop down elements
		List<String> lables = taxHelper.getAllLabels();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
			// Drop down layout style - list view with radio button
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spnTax.setAdapter(dataAdapter);
		
	}
	
	/******* Handle the tax spinner being selected. *******/
	
	private AdapterView.OnItemSelectedListener onTaxSelect=new AdapterView.OnItemSelectedListener() {

		public void onItemSelected( AdapterView<?> parent, View view, int pos, long id) {
			
			EditText txtTaxPercent=(EditText)findViewById(R.id.txt_tax_percent);
			float taxpercent;
			
			//Find item selected using
			String taxtype=( parent.getItemAtPosition(pos).toString());
			txtTaxPercent.setText( taxHelper.getCurrent(taxtype));
			taxpercent=Float.parseFloat( txtTaxPercent.getText().toString());
			
			//Now alter the sales price to suit.
			float unitcost;
			float markupPercent;
			
			EditText unitcostamount=(EditText)findViewById(R.id.txt_cost_unit);
			//EditText taxamount=(EditText)findViewById(R.id.txt_item_tax_amount);
			EditText markup=(EditText)findViewById(R.id.txt_markup);
			//EditText profit=(EditText)findViewById(R.id.txt_markup_profits);
			//EditText taxitempercent=(EditText)findViewById(R.id.txt_tax_percent);

			
			// Check that the unitcost is set.
			if (unitcostamount.getText().length() == 0) {
				unitcost=0;
				unitcostamount.setText("0.00");
			}
			else {
				unitcost= Float.parseFloat(unitcostamount.getText().toString());

			}
			
			if (markup.getText().length() == 0) {
				markupPercent=0;
			}
			else {
				markupPercent= Float.parseFloat(markup.getText().toString());
			}
			
			calcSalePrice( unitcost, markupPercent, taxpercent);
			
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	
	/******** Handle the change of unit types *******/
	
	private AdapterView.OnItemSelectedListener onUnitSelect=new AdapterView.OnItemSelectedListener() {

		public void onItemSelected( AdapterView<?> parent, View view, int pos, long id) {

			// If the unit hasparts then show pkt text box.
			TextView pktQtyLbl=(TextView)findViewById(R.id.lbl_pkt_qty);
			EditText pktQtyTxt=(EditText)findViewById(R.id.txt_pkt_qty);
			
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	
	
	private void loadSpnUnitData() {

		/******* Load in the different unit options. *******/
		// Spinner Drop down elements
		List<String> lables = unitHelper.getAllLabels();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spnUnit.setAdapter(dataAdapter);

	}
	
	private void loadSpnTypeData() {

		/******* Load in the type of item options. *******/
		// Spinner Drop down elements
		List<String> lables = typeHelper.getAllLabels();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spnType.setAdapter(dataAdapter);

	}
	
}

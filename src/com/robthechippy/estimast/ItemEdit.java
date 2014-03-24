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
		
		/* Start of cost changed. */
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
			
		};		/* End of cost changed. */
		
		// Set costchanged to the text box.
		EditText cost=(EditText)findViewById(R.id.txt_cost_unit);
		cost.addTextChangedListener(costChanged);
		
		/* Start of markup changed. */
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
			
		};	/* End of markup changed. */
		
		// Set markupchanged to the text box.
		EditText markup=(EditText)findViewById(R.id.txt_markup);
		markup.addTextChangedListener(markupChanged);
		
		//Now load the actual data into the page.
		loadPage();
		
		
    }
	
	public void onTaxClicked(View view){
		
		boolean checked = ((CheckBox) view).isChecked();
		float unitcost;
		float markupPercent;
		float taxpercent;
		EditText unitcostamount=(EditText)findViewById(R.id.txt_cost_unit);
		//EditText taxamount=(EditText)findViewById(R.id.txt_item_tax_amount);
		//EditText saleprice=(EditText)findViewById(R.id.txt_sale_price);
		//EditText profit=(EditText)findViewById(R.id.txt_markup_profits);
		EditText taxitempercent=(EditText)findViewById(R.id.txt_tax_percent);
		EditText markup=(EditText)findViewById(R.id.txt_markup);
		
		if (checked) {
			TextView tmp= (TextView)findViewById(R.id.lbl_taxType);
			tmp.setVisibility(View.VISIBLE);
			tmp= (TextView)findViewById(R.id.lbl_tax_percent);
			tmp.setVisibility(View.VISIBLE);
			//tmp= (EditView)findViewById(R.id.txt_tax_percent);
			taxitempercent.setVisibility(View.VISIBLE);
			Spinner spn= (Spinner)findViewById(R.id.spn_item_taxType);
			spn.setVisibility(View.VISIBLE);
			taxpercent=Float.parseFloat(taxitempercent.getText().toString());
		}
		else {
			TextView tmp= (TextView)findViewById(R.id.lbl_taxType);
			tmp.setVisibility(View.INVISIBLE);
			tmp= (TextView)findViewById(R.id.lbl_tax_percent);
			tmp.setVisibility(View.INVISIBLE);
			//taxitempercent= (EditView)findViewById(R.id.txt_tax_percent);
			taxitempercent.setVisibility(View.INVISIBLE);
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
	
	//public void loadPage(Cursor item) {
	public void loadPage() {
		
		//For testing only
		Cursor itemList=null;
		Cursor item=null;
		itemList=itemHelper.getAllItems();
		itemList.moveToFirst();
		String id=itemHelper.getID(itemList);
		item=itemHelper.getCurrentItem(id);
		item.moveToFirst();
		//end of testing code
		
		/* Setup catagory spinner */
		loadSpnCatData();

		/* Setup tax spinner */
		loadSpnTaxData();

		/* Setup unit spinner */
		loadSpnUnitData();
		
		/* Setup item type spinner */
		loadSpnTypeData();
		
		//Now load the actual data into the fields Starting with text boxes.
		EditText tmpTxt=(EditText)findViewById(R.id.txt_item_code);
		tmpTxt.setText(itemHelper.getCode(item));
		tmpTxt=(EditText)findViewById(R.id.txt_item_desc);
		tmpTxt.setText(itemHelper.getDescription(item));
		tmpTxt=(EditText)findViewById(R.id.txt_item_checked_date);
		tmpTxt.setText(itemHelper.getItemDateChecked(item));
		tmpTxt=(EditText)findViewById(R.id.txt_item_stock_barcode);
		tmpTxt.setText(itemHelper.getItemBarcode(item));
		tmpTxt=(EditText)findViewById(R.id.txt_item_stock_location);
		tmpTxt.setText(itemHelper.getItemLocation(item));
		
		//Now some floating values
		tmpTxt=(EditText)findViewById(R.id.txt_cost_unit);
		tmpTxt.setText(Float.toString( itemHelper.getUnitCost(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_markup);
		tmpTxt.setText(Float.toString( itemHelper.getMarkup(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_cost_unit);
		tmpTxt.setText(Float.toString( itemHelper.getUnitCost(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_item_len);
		tmpTxt.setText(Float.toString( itemHelper.getItemLen(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_item_width);
		tmpTxt.setText(Float.toString( itemHelper.getItemWidth(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_item_hgt);
		tmpTxt.setText(Float.toString( itemHelper.getItemHeight(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_item_stock_onhand);
		tmpTxt.setText(Float.toString( itemHelper.getItemStockOnHand(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_item_stock_onorder);
		tmpTxt.setText(Float.toString( itemHelper.getItemStockOnOrder(item)));
		
		//Now the rest of the data that needs some thought.
		//First up is the item catagory
		spnCat.setSelection(getIndex(spnCat, itemHelper.getCatagory(item)));
		spnTax.setSelection(getIndex(spnTax, itemHelper.getTaxtype(item)));
		spnUnit.setSelection(getIndex(spnUnit, itemHelper.getUnit(item)));
		spnType.setSelection(getIndex(spnType, itemHelper.getItemType(item)));
		
		//Tax check box
		int taxable=itemHelper.getTaxable(item);
		CheckBox tmpChk=(CheckBox)findViewById(R.id.chk_taxable);
		if (taxable > 0) {
			tmpChk.setChecked(true);
		} else tmpChk.setChecked(false);
		
		//Now all the fraction radio buttons
		RadioButton tmpRad=null;
		String frac = Float.toString(itemHelper.getItemLenFrac(item));
		Boolean changed = false;
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_len_1);
		if(frac.matches("1.0")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_len_25);
		if(frac.matches("0.25")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_len_3);
		if (frac.matches("0.3")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_len_5);
		if(frac.matches("0.5")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		if (changed) {
			tmpTxt=(EditText)findViewById(R.id.txt_item_frac_len_other);
			tmpTxt.setText("");
		}
		else {
			tmpTxt=(EditText)findViewById(R.id.txt_item_frac_len_other);
			tmpTxt.setText(frac);
		}
		
		//Width group
		frac = Float.toString(itemHelper.getItemWidthFrac(item));
		changed = false;
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_width_1);
		if(frac.matches("1.0")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_width_25);
		if(frac.matches("0.25")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_width_3);
		if (frac.matches("0.3")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_width_5);
		if(frac.matches("0.5")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		if (changed) {
			tmpTxt=(EditText)findViewById(R.id.txt_item_frac_width_other);
			tmpTxt.setText("");
		}
		else {
			tmpTxt=(EditText)findViewById(R.id.txt_item_frac_width_other);
			tmpTxt.setText(frac);
		}
		
		//Height group
		frac = Float.toString(itemHelper.getItemHeightFrac(item));
		changed = false;
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_hgt_1);
		if(frac.matches("1.0")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_hgt_25);
		if(frac.matches("0.25")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_hgt_3);
		if (frac.matches("0.3")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_item_frac_hgt_5);
		if(frac.matches("0.5")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		if (changed) {
			tmpTxt=(EditText)findViewById(R.id.txt_item_frac_hgt_other);
			tmpTxt.setText("");
		}
		else {
			tmpTxt=(EditText)findViewById(R.id.txt_item_frac_hgt_other);
			tmpTxt.setText(frac);
		}
		
		
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
			TextView lblSubCost=(TextView)findViewById(R.id.lbl_item_subCost);
			EditText txtSubCost=(EditText)findViewById(R.id.txt_item_subCost);
			CheckBox chkUseSub=(CheckBox)findViewById(R.id.chk_item_useSub);
			
			//Find item selected using
			String unit=( parent.getItemAtPosition(pos).toString());
			Boolean hasParts = unitHelper.getCurrentHasParts(unit);
			
			//Now use this info to show or hide fields.
			if (hasParts) {
				pktQtyLbl.setVisibility(view.VISIBLE);
				pktQtyTxt.setVisibility(view.VISIBLE);
				lblSubCost.setVisibility(view.VISIBLE);
				txtSubCost.setVisibility(view.VISIBLE);
				chkUseSub.setVisibility(view.VISIBLE);
			}
			else {
				pktQtyLbl.setVisibility(view.INVISIBLE);
				pktQtyTxt.setVisibility(view.INVISIBLE);
				lblSubCost.setVisibility(view.INVISIBLE);
				txtSubCost.setVisibility(view.INVISIBLE);
				chkUseSub.setVisibility(view.INVISIBLE);
			}
			
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	
	/* Load the item unit spinner with its data. */
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
	
	/* Load the item type spinner with data. */
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

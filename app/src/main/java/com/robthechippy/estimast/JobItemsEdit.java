package com.robthechippy.estimast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.view.*;
import android.widget.*;
import android.text.*;
import android.app.ActionBar;
//import android.app.Activity;

public class
        JobItemsEdit extends Activity
{

	//Define the spinners.
	Spinner spnCat = null;
	//CatagoryHelper catHelper = null;
	
	Spinner spnTax = null;
	//TaxHelper taxHelper = null;
	
	Spinner spnUnit = null;
	//UnitHelper unitHelper = null;
	
	Spinner spnType = null;
	//ItemTypeHelper  typeHelper= null;
	
	JobItemHelper itemHelper = null;

    private String clientId;
    private String jobId;
    private String sectionId;

	
	Cursor itemList=null;
	Cursor item=null;

    String currentID = "";
    String category = "";
    String searchTxt = "";
	String newItemsList = "";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_edit_item);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //Check if returning with a saved instance.
        //if (savedInstanceState != null) {
            //Reload saved client id.
        //  clientId=savedInstanceState.getString("clientID");
        //}

        /******* Start setting up the spinners. *******/
        
        //catHelper=new CatagoryHelper(this);
		spnCat=(Spinner)findViewById(R.id.spn_jobitem_catagory);
		
		//taxHelper = new TaxHelper(this);
		spnTax = (Spinner)findViewById(R.id.spn_jobitem_taxType);
		spnTax.setOnItemSelectedListener(onTaxSelect);
		
		//unitHelper = new UnitHelper(this);
		spnUnit = (Spinner)findViewById(R.id.spn_jobitem_Unit);
		spnUnit.setOnItemSelectedListener(onUnitSelect);
		
		//typeHelper = new ItemTypeHelper(this);
		spnType = (Spinner)findViewById(R.id.spn_jobitem_type);
		
		itemHelper = new JobItemHelper(this);
		
		/******* Setup tabs *******/
		
		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		
		tabs.setup();
		
		TabHost.TabSpec spec=tabs.newTabSpec("tag1");
		
		spec.setContent(R.id.tabA);
		spec.setIndicator("Main");
		tabs.addTab(spec);

        spec=tabs.newTabSpec("tag2");

        spec.setContent(R.id.tabCalc);
        spec.setIndicator("Calcs");
        tabs.addTab(spec);


        spec=tabs.newTabSpec("tag3");
		
		spec.setContent(R.id.tabB);
		spec.setIndicator("Sizes");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag4");
		
		spec.setContent(R.id.tabC);
		spec.setIndicator("Supplier");
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag5");
		
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
                String tmp=null;
				EditText markup=(EditText)findViewById(R.id.txt_jobitem_markup);
				EditText taxamount=(EditText)findViewById(R.id.txt_jobitem_tax_amount);
				EditText saleprice=(EditText)findViewById(R.id.txt_jobitem_sale_price);
				EditText profit=(EditText)findViewById(R.id.txt_jobitem_markup_profits);
				EditText taxitempercent=(EditText)findViewById(R.id.txt_jobitem_tax_percent);
				
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
					CheckBox tax=(CheckBox)findViewById(R.id.chk_jobitem_taxable);
					if (tax.isChecked()) {
                        tmp=taxitempercent.getText().toString();
                        if(tmp.length()==0){
                            taxpercent=0;
                        }else{
						taxpercent=Float.parseFloat(tmp);
                        }
					}
					else {
						taxpercent = 0;
						taxamount.setText("0.00");
					}
					calcSalePrice( unitcost, markupPercent, taxpercent);
                    calcTotal();
				}
				
			}


			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				
			}
			
		};		/* End of cost changed. */
		
		// Set costchanged to the text box.
		EditText cost=(EditText)findViewById(R.id.txt_jobitem_cost_unit);
		cost.addTextChangedListener(costChanged);
		
		/* Start of markup changed. */
		TextWatcher markupChanged=new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				
				/******* Look after what happens when the markup percent changes. *******/

				float unitcost;
				float markupPercent;
				float taxpercent;
                String tmp=null;
				EditText unitcostamount=(EditText)findViewById(R.id.txt_jobitem_cost_unit);
				EditText taxamount=(EditText)findViewById(R.id.txt_jobitem_tax_amount);
				//EditText saleprice=(EditText)findViewById(R.id.txt_sale_price);
				EditText profit=(EditText)findViewById(R.id.txt_jobitem_markup_profits);
				EditText taxitempercent=(EditText)findViewById(R.id.txt_jobitem_tax_percent);
				
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
				CheckBox tax=(CheckBox)findViewById(R.id.chk_jobitem_taxable);
				if (tax.isChecked()) {
                    tmp=taxitempercent.getText().toString();
                    if(tmp.length()==0){
                        taxpercent=0;
                    }else{
                        taxpercent=Float.parseFloat(tmp);
                    }
                }
				else {
					taxpercent = 0;
					taxamount.setText("0.00");
				}
				calcSalePrice( unitcost, markupPercent, taxpercent);
                calcTotal();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}
			
		};	/* End of markup changed. */
		
		// Set markupchanged to the text box.
		EditText markup=(EditText)findViewById(R.id.txt_jobitem_markup);
		markup.addTextChangedListener(markupChanged);
		
		/* Start of qty changed. */
		TextWatcher qtyChanged=new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				EditText qty=(EditText)findViewById(R.id.txt_jobitem_qty);
				if(qty.hasFocus()){
					//Adjust other values to suit, else do nothing.
					//First test if the box is blank or = 0
					String costTmp;
					costTmp = qty.getText().toString();
					if(qty.getText().length() == 0) {
						//Set item total to 0
						EditText tot = (EditText)findViewById(R.id.txt_jobitem_total);
						tot.setText("0.0");
					} else if((Float.parseFloat(costTmp))==0) {
						EditText tot = (EditText)findViewById(R.id.txt_jobitem_total);
						tot.setText("0.0");
					}else {
						calcTotal();
					}

				}
			}
				
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {


			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {

			}

		};	/* End of markup changed. */

		// Set markupchanged to the text box.
		EditText qty=(EditText)findViewById(R.id.txt_jobitem_qty);
		qty.addTextChangedListener(qtyChanged);
		

        //setProgressBarIndeterminateVisibility(true);

        //Check if returning with a saved instance.
        if (savedInstanceState != null) {
            //Reload saved client id.
            currentID=savedInstanceState.getString("currentID");
            clientId=savedInstanceState.getString("clientId");
            jobId = savedInstanceState.getString("jobId");
            sectionId = savedInstanceState.getString("sectionId");
			searchTxt = savedInstanceState.getString("searchTxt");
			newItemsList = savedInstanceState.getString("newItemsList");
        }else if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            String[] data = intent.getStringArrayExtra(Intent.EXTRA_EMAIL);
            if (data != null) {
                currentID = data[0];
                clientId = data[1];
                jobId = data[2];
                sectionId = data[3];
                searchTxt= data[4];
				newItemsList = data[5];

            }
        }

        //progressDialog.show();
        
		if(!currentID.equals("-1")) {
			//Do this for a normal load.
			itemList=itemHelper.getSearchJobItemsList(sectionId, searchTxt);
			itemList.moveToFirst();
            while (!currentID.equals(itemHelper.getID(itemList))){
                itemList.moveToNext();
			}
        }
		else {
			//Come to here when new items h]ave been aadede to the job.
			itemList=itemHelper.getNewItems(newItemsList);
			itemList.moveToFirst();
			currentID = itemHelper.getID(itemList);
		}
        loadPage();

         //setProgressBarIndeterminateVisibility(false);


    }

    private void calcTotal() {

        EditText qty=(EditText)findViewById(R.id.txt_jobitem_qty);
        EditText cost=(EditText)findViewById(R.id.txt_jobitem_sale_price);
        EditText tot=(EditText)findViewById(R.id.txt_jobitem_total);

        Float costTmp = Float.parseFloat(cost.getText().toString());
        Float qtyTmp = Float.parseFloat(qty.getText().toString());

        if ((costTmp == 0)||(qtyTmp == 0)) {
            tot.setText("0.00");
        } else {
            costTmp = costTmp * qtyTmp;
            tot.setText(new BigDecimal(costTmp.toString()).setScale(2, RoundingMode.HALF_UP).toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current clientId
        savedInstanceState.putString("currentID", currentID);
        savedInstanceState.putString("clientId", clientId);
		savedInstanceState.putString("jobId", jobId);
		savedInstanceState.putString("sectionId", sectionId);
        savedInstanceState.putString("searchTxt",searchTxt);
		savedInstanceState.putString("newItemsList", newItemsList);
        //itemHelper.close();
        //item.close();
        //itemList.close();

        // Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//Inflate the items for use in the action bar
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.item_edit_actions, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem mItem) {
    	//Handle selections on the action bar.
    	switch (mItem.getItemId()) {
    		case R.id.action_previous:
    			//Save data first
    			save_data();
    			prev_item();
    			return true;	
    		
    		case R.id.action_next:
    			//Save data first
    			save_data();
    			next_item();
    			return true;
    			
    		case R.id.action_delete:
    			String[] id={itemHelper.getID(itemList)};
    			itemHelper.deleteItem(id);
                itemList=itemHelper.getSearchJobItemsList(sectionId, searchTxt);
    			//itemList=itemHelper.getAllItems(sectionId);
                if (itemList.getCount() < 1) {
                    this.finish();
                } else {
				//TODO check if there is still any valid data
    			itemList.moveToFirst();
                currentID = itemHelper.getID(itemList);
    			loadPage();
                }
    			return true;
    		   
    		case R.id.action_new:
    			//Save data first
    			save_data();
    			int Iid=itemHelper.insertItem("New", clientId,  jobId, sectionId);
    			itemList=itemHelper.getAllItems(sectionId);
    			itemList.moveToFirst();
    			while((!itemList.isAfterLast()) && (!(itemHelper.getIDint(itemList)==Iid))) {
    				itemList.moveToNext();
					currentID = itemHelper.getID(itemList);
    				loadPage();
    			}
    			return true;
    			
    		case R.id.action_undo:
    			//Simply reload the page.
    			loadPage();
    			return true;
    			
    		default:
    			return super.onOptionsItemSelected(mItem);
    	}
    } 
    	
    public void onTaxClicked(View view){
		
		boolean checked = ((CheckBox) view).isChecked();
		float unitcost;
		float markupPercent;
		float taxpercent;
		EditText unitcostamount=(EditText)findViewById(R.id.txt_jobitem_cost_unit);
		//EditText taxamount=(EditText)findViewById(R.id.txt_jobitem_tax_amount);
		//EditText saleprice=(EditText)findViewById(R.id.txt_sale_price);
		//EditText profit=(EditText)findViewById(R.id.txt_markup_profits);
		EditText taxitempercent=(EditText)findViewById(R.id.txt_jobitem_tax_percent);
		EditText markup=(EditText)findViewById(R.id.txt_jobitem_markup);
		
		if (checked) {
			TextView tmp= (TextView)findViewById(R.id.lbl_jobitem_taxType);
			tmp.setVisibility(View.VISIBLE);
			tmp= (TextView)findViewById(R.id.lbl_jobitem_tax_percent);
			tmp.setVisibility(View.VISIBLE);
			//tmp= (EditView)findViewById(R.id.txt_tax_percent);
			taxitempercent.setVisibility(View.VISIBLE);
			Spinner spn= (Spinner)findViewById(R.id.spn_jobitem_taxType);
			spn.setVisibility(View.VISIBLE);
			taxpercent=Float.parseFloat(taxitempercent.getText().toString());
		}
		else {
			TextView tmp= (TextView)findViewById(R.id.lbl_jobitem_taxType);
			tmp.setVisibility(View.INVISIBLE);
			tmp= (TextView)findViewById(R.id.lbl_jobitem_tax_percent);
			tmp.setVisibility(View.INVISIBLE);
			//taxitempercent= (EditView)findViewById(R.id.txt_tax_percent);
			taxitempercent.setVisibility(View.INVISIBLE);
			Spinner spn= (Spinner)findViewById(R.id.spn_jobitem_taxType);
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
		calcTotal();
	}
	
	private void calcSalePrice( Float unitCost, Float markup, Float taxPercent) {

		/******* Do the actual calculations to work out the amount of markup,
			tax to charge, and the final sale price. *******/
		
		CheckBox tax=(CheckBox)findViewById(R.id.chk_jobitem_taxable);
		EditText taxamount=(EditText)findViewById(R.id.txt_jobitem_tax_amount);
		EditText saleprice=(EditText)findViewById(R.id.txt_jobitem_sale_price);
		EditText profit=(EditText)findViewById(R.id.txt_jobitem_markup_profits);
		double profitAmount;
		double taxprice;
		
		// Work out any markup first.
		if(markup > 0 ) {
				profitAmount = (markup/100.00) * unitCost;
			}
			else {
				profitAmount = 0.0;
				// profit.setText("0.00");
			}
		profit.setText(new BigDecimal(profitAmount).setScale(2, RoundingMode.HALF_UP).toString());
		
		// Now work out what tax to pay.
		if (tax.isChecked()) {
			taxprice=(taxPercent/100.00)*(unitCost+profitAmount);
		}
		else {
			taxprice=0.00;
		}
		taxamount.setText(new BigDecimal(taxprice).setScale(2, RoundingMode.HALF_UP).toString());
		
		//Now we know the final price.
		//saleprice.setText(Double.toString( unitCost + profitAmount + taxprice));
        saleprice.setText(new BigDecimal(unitCost + profitAmount + taxprice).setScale(2, RoundingMode.HALF_UP).toString());
	}
	
	public void first_item() {
		itemList.moveToFirst();
		currentID = itemHelper.getID(itemList);
		loadPage();
	}
	
	public void last_item() {
		itemList.moveToLast();
		currentID = itemHelper.getID(itemList);
		loadPage();
	}
	
	public void next_item() {
		itemList.moveToNext();
		if(itemList.isAfterLast()) {
			first_item();
			currentID = itemHelper.getID(itemList);
		}
		else {
			currentID = itemHelper.getID(itemList);
			loadPage();
		}
	}
	
	public void prev_item() {
		itemList.moveToPrevious();
		if(itemList.isBeforeFirst()) {
			last_item();
			currentID = itemHelper.getID(itemList);
		}
		else {
			currentID = itemHelper.getID(itemList);
			loadPage();
		}
	}
	
	//public void loadPage(Cursor item) {
	public void loadPage() {
		
		//String id=itemHelper.getID(itemList);
		item=itemHelper.getCurrentItem(currentID);
		item.moveToFirst();
		
		/* Setup catagory spinner */
		loadSpnCatData();

		/* Setup tax spinner */
		loadSpnTaxData();

		/* Setup unit spinner */
		loadSpnUnitData();
		
		/* Setup item type spinner */
		loadSpnTypeData();
		
		//Now load the actual data into the fields Starting with text boxes.
		EditText tmpTxt=(EditText)findViewById(R.id.txt_jobitem_code);
		tmpTxt.setText(itemHelper.getCode(item));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_desc);
		tmpTxt.setText(itemHelper.getDescription(item));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_checked_date);
		tmpTxt.setText(itemHelper.getItemDateChecked(item));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_stock_barcode);
		tmpTxt.setText(itemHelper.getItemBarcode(item));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_stock_location);
		tmpTxt.setText(itemHelper.getItemLocation(item));
		
		//Now some floating values
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_qty);
		tmpTxt.setText(Float.toString(itemHelper.getItemQty(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_total);
		tmpTxt.setText(Float.toString(itemHelper.getTotal(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_cost_unit);
		tmpTxt.setText(Float.toString( itemHelper.getUnitCost(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_markup);
		tmpTxt.setText(Float.toString( itemHelper.getMarkup(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_pkt_qty);
		tmpTxt.setText(Float.toString( itemHelper.getUnitQty(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_len);
		tmpTxt.setText(Float.toString( itemHelper.getItemLen(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_width);
		tmpTxt.setText(Float.toString( itemHelper.getItemWidth(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_hgt);
		tmpTxt.setText(Float.toString( itemHelper.getItemHeight(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_stock_onhand);
		tmpTxt.setText(Integer.toString( itemHelper.getItemStockOnHand(item)));
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_stock_onorder);
		tmpTxt.setText(Integer.toString( itemHelper.getItemStockOnOrder(item)));

		//Now the rest of the data that needs some thought.
		//First up is the item catagory
		spnCat.setSelection(getIndex(spnCat, itemHelper.getCatagory(item)));
		spnTax.setSelection(getIndex(spnTax, itemHelper.getTaxtype(item)));
		spnUnit.setSelection(getIndex(spnUnit, itemHelper.getUnit(item)));
		spnType.setSelection(getIndex(spnType, itemHelper.getItemType(item)));
		
		//Tax check box
		int taxable=itemHelper.getTaxable(item);
		CheckBox tmpChk=(CheckBox)findViewById(R.id.chk_jobitem_taxable);
		if (taxable > 0) {
			tmpChk.setChecked(true);
			TextView tmp= (TextView)findViewById(R.id.lbl_jobitem_taxType);
			tmp.setVisibility(View.VISIBLE);
			tmp= (TextView)findViewById(R.id.lbl_jobitem_tax_percent);
			tmp.setVisibility(View.VISIBLE);
			TextView taxitempercent= (TextView)findViewById(R.id.txt_jobitem_tax_percent);
			Spinner spn= (Spinner)findViewById(R.id.spn_jobitem_taxType);
			spn.setVisibility(View.VISIBLE);

		} else {
			tmpChk.setChecked(false);
			TextView tmp= (TextView)findViewById(R.id.lbl_jobitem_taxType);
			tmp.setVisibility(View.INVISIBLE);
			tmp= (TextView)findViewById(R.id.lbl_jobitem_tax_percent);
			tmp.setVisibility(View.INVISIBLE);
			TextView taxitempercent= (TextView)findViewById(R.id.txt_jobitem_tax_percent);
			taxitempercent.setVisibility(View.INVISIBLE);
			Spinner spn= (Spinner)findViewById(R.id.spn_jobitem_taxType);
			spn.setVisibility(View.INVISIBLE);

		}
		
		//Now all the fraction radio buttons
		RadioButton tmpRad=null;
		String frac = Float.toString(itemHelper.getItemLenFrac(item));
		Boolean changed = false;
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_5);
		tmpRad.setChecked(true);
		
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_1);
		tmpRad.setChecked(true);
		if(frac.matches("1.0") | frac.matches("1")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_25);
		if(frac.matches("0.25")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_3);
		if (frac.matches("0.3")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_5);
		if(frac.matches("0.5")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		
		if (changed) {
			tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_len_other);
			tmpTxt.setText("");
		}
		else {
			tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_len_other);
			tmpTxt.setText(frac);
		}
		
		//Width group
		frac = Float.toString(itemHelper.getItemWidthFrac(item));
		changed = false;
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_width_1);
		if(frac.matches("1.0") | frac.matches("1")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_width_25);
		if(frac.matches("0.25")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_width_3);
		if (frac.matches("0.3")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_width_5);
		if(frac.matches("0.5")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		if (changed) {
			tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_width_other);
			tmpTxt.setText("");
		}
		else {
			tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_width_other);
			tmpTxt.setText(frac);
		}
		
		//Height group
		frac = Float.toString(itemHelper.getItemHeightFrac(item));
		changed = false;
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_hgt_1);
		if(frac.matches("1.0") | frac.matches("1")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_hgt_25);
		if(frac.matches("0.25")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_hgt_3);
		if (frac.matches("0.3")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_hgt_5);
		if(frac.matches("0.5")) {
			tmpRad.setChecked(true);
			changed = true;
		}
		else {
			tmpRad.setChecked(false);
		}
		if (changed) {
			tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_hgt_other);
			tmpTxt.setText("");
		}
		else {
			tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_hgt_other);
			tmpTxt.setText(frac);
		}
		
		
	}
	
	private void save_data() {
		/* updateItem(String[] id, String catagory, String code, String description, String unit, float unitQty,
				float unitCost, float markup, int taxable, String taxtype, String itemType, float itemLen,
				float itemLenFrac, float itemWidth, float itemWidthFrac, float itemHeight, float itemHeightFrac,
				String availableSizes, int supplier, String dateChecked, int stockOnHand, int stockOnOrder,
				String barcode, String location, String photo) */
		
		String[] id={itemHelper.getID(item)};
		
		EditText tmpTxt=(EditText)findViewById(R.id.txt_jobitem_code);
		String code = tmpTxt.getText().toString();
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_desc);
		String description = tmpTxt.getText().toString();
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_checked_date);
		String dateChecked = tmpTxt.getText().toString();
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_stock_barcode);
		String barcode = tmpTxt.getText().toString();
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_stock_location);
		String location = tmpTxt.getText().toString();
		
		//Now some floating values
        tmpTxt=(EditText)findViewById(R.id.txt_jobitem_qty);
        float itemQty = Float.valueOf(tmpTxt.getText().toString());

        tmpTxt=(EditText)findViewById(R.id.txt_jobitem_total);
        float total = Float.valueOf(tmpTxt.getText().toString());

        tmpTxt=(EditText)findViewById(R.id.txt_jobitem_cost_unit);
		float unitCost = Float.valueOf(tmpTxt.getText().toString());
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_markup);
		float markup = Float.valueOf(tmpTxt.getText().toString());
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_pkt_qty);
		float unitQty = Float.valueOf(tmpTxt.getText().toString());

		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_len);
		float itemLen = Float.valueOf(tmpTxt.getText().toString());
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_width);
		float itemWidth = Float.valueOf(tmpTxt.getText().toString());
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_hgt);
		float itemHeight = Float.valueOf(tmpTxt.getText().toString());
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_stock_onhand);
		int stockOnHand = Integer.parseInt(tmpTxt.getText().toString());
		
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_stock_onorder);
		int stockOnOrder = Integer.parseInt(tmpTxt.getText().toString());
		
		//Now the rest of the data that needs some thought.
		//First up is the item catagory
		int tmpI = spnCat.getSelectedItemPosition();
		String catagory = spnCat.getItemAtPosition(tmpI).toString();
		
		tmpI = spnTax.getSelectedItemPosition();
		String taxtype = spnTax.getItemAtPosition(tmpI).toString();
		
		tmpI = spnUnit.getSelectedItemPosition();
		String unit = spnUnit.getItemAtPosition(tmpI).toString();
		
		tmpI = spnType.getSelectedItemPosition();
		String itemType = spnType.getItemAtPosition(tmpI).toString();
		 
		//Tax check box
		int taxable;
		CheckBox tmpChk=(CheckBox)findViewById(R.id.chk_jobitem_taxable);
		if (tmpChk.isChecked()) {
			taxable = 1;
		} else {
			taxable = 0;
		}
		
		
		//Now all the fraction radio buttons
		RadioButton tmpRad=null;
		Boolean changed = false;
		float itemLenFrac = 0;
		tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_len_other);
		if (tmpTxt.getText().length() == 0) {
			itemLenFrac = (float)1.0;
			
			tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_1);
			if(tmpRad.isChecked()) {
				itemLenFrac = (float) 1.0;
				changed = true;
			}
			tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_25);
			if(tmpRad.isChecked()) {
				itemLenFrac = (float) 0.25;
				changed = true;
			}
			tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_3);
			if (tmpRad.isChecked()) {
				itemLenFrac = (float) 0.3;
				changed = true;
			}
			tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_len_5);
			if(tmpRad.isChecked()) {
				itemLenFrac = (float) 0.5;
				changed = true;
			}		
		}
		else itemLenFrac = Float.valueOf(tmpTxt.getText().toString());
		
		
		//Width group
		float itemWidthFrac = 0;
		changed = false;
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_width_1);
		if(tmpRad.isChecked()) {
			itemWidthFrac = (float) 1.0;
			changed = true;
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_width_25);
		if(tmpRad.isChecked()) {
			itemWidthFrac = (float) 0.25;
			changed = true;
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_width_3);
		if (tmpRad.isChecked()) {
			itemWidthFrac = (float) 0.3;
			changed = true;
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_width_5);
		if(tmpRad.isChecked()) {
			itemWidthFrac = (float) 0.5;
			changed = true;
		}
		if (!changed) {
			tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_width_other);
			if (tmpTxt.getText().length() == 0) itemWidthFrac = (float)1.0;
			else itemWidthFrac = Float.valueOf(tmpTxt.getText().toString());
		}
		
		//Height group
		float itemHeightFrac = 0;
		changed = false;
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_hgt_1);
		if(tmpRad.isChecked()) {
			itemHeightFrac = (float) 1.0;
			changed = true;
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_hgt_25);
		if(tmpRad.isChecked()) {
			itemHeightFrac = (float) 0.25;
			changed = true;
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_hgt_3);
		if (tmpRad.isChecked()) {
			itemHeightFrac = (float) 0.3;
			changed = true;
		}
		tmpRad=(RadioButton)findViewById(R.id.rad_jobitem_frac_hgt_5);
		if(tmpRad.isChecked()) {
			itemHeightFrac = (float) 0.5;
			changed = true;
		}
		if (!changed) {
			tmpTxt=(EditText)findViewById(R.id.txt_jobitem_frac_hgt_other);
			if (tmpTxt.getText().length() == 0) {itemHeightFrac = (float)1.0;}
			else {itemHeightFrac = Float.valueOf(tmpTxt.getText().toString());}
		}
		
		String availableSizes = "";
		int supplier = 0;
		String photo = "";
		
		itemHelper.updateItem(id, catagory, code, description, itemQty, total, unit, unitQty,
				unitCost, markup, taxable, taxtype, itemType, itemLen,
				itemLenFrac, itemWidth, itemWidthFrac, itemHeight, itemHeightFrac,
				availableSizes, supplier, dateChecked, stockOnHand, stockOnOrder,
				barcode, location, photo);
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
        CatagoryHelper catHelper=new CatagoryHelper(this);
		// Spinner Drop down elements
		List<String> lables = catHelper.getAllLabels();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
			// Drop down layout style - list view with radio button
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spnCat.setAdapter(dataAdapter);
        catHelper.close();
        catHelper=null;
		
	}
	
	private void loadSpnTaxData() {
		
		/******* Load the different tax options. *******/
        TaxHelper taxHelper = new TaxHelper(this);
		// Spinner Drop down elements
		List<String> lables = taxHelper.getAllLabels();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
			// Drop down layout style - list view with radio button
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spnTax.setAdapter(dataAdapter);
        taxHelper.close();
        taxHelper=null;
		
	}
	
	/******* Handle the tax spinner being selected. *******/
	
	private AdapterView.OnItemSelectedListener onTaxSelect=new AdapterView.OnItemSelectedListener() {

		public void onItemSelected( AdapterView<?> parent, View view, int pos, long id) {
			
			EditText txtTaxPercent=(EditText)findViewById(R.id.txt_jobitem_tax_percent);
			float taxpercent;

            TaxHelper taxHelper = new TaxHelper(getApplication());
			//Find item selected using
			String taxtype=( parent.getItemAtPosition(pos).toString());
			txtTaxPercent.setText( taxHelper.getCurrent(taxtype));
			taxpercent=Float.parseFloat( txtTaxPercent.getText().toString());
			
			//Now alter the sales price to suit.
			float unitcost;
			float markupPercent;
			
			EditText unitcostamount=(EditText)findViewById(R.id.txt_jobitem_cost_unit);
			//EditText taxamount=(EditText)findViewById(R.id.txt_jobitem_tax_amount);
			EditText markup=(EditText)findViewById(R.id.txt_jobitem_markup);
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
            calcTotal();
            taxHelper.close();
            taxHelper=null;
			
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	
	/******** Handle the change of unit types *******/
	
	private AdapterView.OnItemSelectedListener onUnitSelect=new AdapterView.OnItemSelectedListener() {

		public void onItemSelected( AdapterView<?> parent, View view, int pos, long id) {

            UnitHelper unitHelper = new UnitHelper(getApplication());
			// If the unit hasparts then show pkt text box.
			TextView pktQtyLbl=(TextView)findViewById(R.id.lbl_jobitem_pkt_qty);
			EditText pktQtyTxt=(EditText)findViewById(R.id.txt_jobitem_pkt_qty);
			TextView lblSubCost=(TextView)findViewById(R.id.lbl_jobitem_subCost);
			EditText txtSubCost=(EditText)findViewById(R.id.txt_jobitem_subCost);
			CheckBox chkUseSub=(CheckBox)findViewById(R.id.chk_jobitem_useSub);
			
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

            unitHelper.close();
            unitHelper=null;
			
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	
	/* Load the item unit spinner with its data. */
	private void loadSpnUnitData() {

		/******* Load in the different unit options. *******/
        UnitHelper unitHelper=new UnitHelper(this);
		// Spinner Drop down elements
		List<String> lables = unitHelper.getAllLabels();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spnUnit.setAdapter(dataAdapter);
        unitHelper.close();
        unitHelper=null;

	}
	
	/* Load the item type spinner with data. */
	private void loadSpnTypeData() {

		/******* Load in the type of item options. *******/
        ItemTypeHelper typeHelper=new ItemTypeHelper(this);
		// Spinner Drop down elements
		List<String> lables = typeHelper.getAllLabels();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		spnType.setAdapter(dataAdapter);
        typeHelper.close();
        typeHelper=null;

	}

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        save_data();
    }

}

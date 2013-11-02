package com.robthechippy.estimast;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.TabHost;

public class ItemEdit extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);
		
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
		
		spec.setContent(R.id.tabC);
		spec.setIndicator("Stock");
		tabs.addTab(spec);
		
    }
}

package com.android.mobsec;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ModSec extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	private Button btNetFw;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.mob_sec);
    	
    	btNetFw = (Button) findViewById(R.id.btNetFw);
    	btNetFw.setOnClickListener(this);
    }
    
    public void onClick(View v) {
    	Button bt = (Button)v;
    	
    	if(bt.getId() == R.id.btNetFw) {
    		try {
    			startActivity (new Intent(this, policyList.class));
    		}
    		catch (ActivityNotFoundException e) {
    			e.getCause();
    		}
    	}
    }
}
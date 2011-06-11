package com.android.mobsec;

import com.android.mobsec.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class policyPref extends PreferenceActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);
	}
}

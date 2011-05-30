package com.android.mobsec;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TextView;

public class policyStatus extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Policy status");
        setContentView(textview);
    }

}
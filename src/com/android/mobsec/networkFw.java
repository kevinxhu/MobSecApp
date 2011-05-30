package com.android.mobsec;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.content.Intent;

public class networkFw extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("Status")
                .setContent(new Intent(this, policyStatus.class)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("Policy List")
                .setContent(new Intent(this, policyList.class)));
    }
}
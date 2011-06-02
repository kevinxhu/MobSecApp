package com.android.mobsec;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.content.Intent;
import android.content.res.Resources;

public class networkFw extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();
        Resources res = getResources(); // Resource object to get Drawables

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("Status", res.getDrawable(R.drawable.ic_tab_status))
                .setContent(new Intent(this, policyStatus.class)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("Policy List", res.getDrawable(R.drawable.ic_tab_policy))
                .setContent(new Intent(this, policyList.class)));
    }
}
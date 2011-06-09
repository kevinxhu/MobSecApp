package com.android.mobsec;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TextView;

public class policyStatus extends Activity {
	
	public native int getFwStats(int[] stats);
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        String stats = new String();
        int statsData[] = new int[] {0, 0, 0, 0, 0, 0};
        
        getFwStats(statsData);
        
        /* convert raw data to string */
        stats += "Rx Packets:\t\t" + statsData[0] + "\n";
        stats += "Rx Bytes:  \t\t" + statsData[1] + "\n";
        stats += "Rx Drops:  \t\t" + statsData[2] + "\n\n";
        stats += "Tx packets:\t\t" + statsData[3] + "\n";
        stats += "Tx Bytes:  \t\t" + statsData[4] + "\n";
        stats += "Tx Drops:  \t\t" + statsData[5] + "";
        
        textview.setText(stats);
        setContentView(textview);
    }

	static {
        System.loadLibrary("msaFw");
    }
}
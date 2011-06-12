package com.android.mobsec;

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;   

import android.os.Bundle;   
import android.os.Handler;   
import android.os.Message;
import android.widget.TextView;

public class policyStatus extends Activity {
	
	public native int getFwStats(int[] stats);
	
	private void showFwStats() {
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

	Timer statsTimer = new Timer();
	
	Handler statsHandler = new Handler(){   
		public void handleMessage(Message msg) {   
			switch (msg.what) {       
				case 1:       
					showFwStats();  
					break;       
			}       
			super.handleMessage(msg);   
		}            
	};
	
	TimerTask statsTask = new TimerTask(){   
			public void run() {   
				Message message = new Message();       
				message.what = 1;       
				statsHandler.sendMessage(message);     
			}   
	}; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showFwStats();
        
        /* set timer to refresh status */
        statsTimer.schedule(statsTask, 1000, 3000);
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        
        showFwStats();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		statsTimer.cancel();
	}

	static {
        System.loadLibrary("msaFw");
    }
}
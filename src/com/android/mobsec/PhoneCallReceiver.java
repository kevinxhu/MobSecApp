package com.android.mobsec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

public class PhoneCallReceiver extends BroadcastReceiver {
	private static final String TAG = "THIRI THE WUT YEE";
	private ITelephony telephonyService;
	@Override
    public void onReceive(Context context, Intent intent) {
		
        Log.v(TAG, "Receving....");
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
        try 
        {
            @SuppressWarnings("rawtypes")
			Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);

            Toast tag = Toast.makeText(context, "Call is not allowed in the meeting!!!", Toast.LENGTH_LONG);
            tag.setDuration(25);
            tag.show();
            
            Bundle bundle = intent.getExtras();    
            String phoneNr= bundle.getString("incoming_number");        
            Log.v(TAG, "phoneNr: "+phoneNr);
            
            String[] phoneNumList = phoneBlockList.blockPhoneList;
            int phoneLen = phoneNr.length();
            for(int index = 0; index < phoneNumList.length; index++) {
            	int numberLen = phoneNumList[index].length();
            	if(numberLen <= 0) {
            		continue;
            	}
            	String subPhone = phoneNr.substring(phoneLen - numberLen);
            	if(subPhone.compareTo(phoneNumList[index]) == 0) {
                    telephonyService.silenceRinger();
                    telephonyService.endCall();
                    Log.v(TAG, "call is blocked");
                    break;
            	}
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }	
}
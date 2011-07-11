package com.android.mobsec;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class phoneNumber extends Activity {
    public static final int MENU_ITEM_SAVE = Menu.FIRST;
    public static final int MENU_ITEM_DISCARD = Menu.FIRST + 1;	
    
    private static final String PHONE_FILE = "phone_number.txt";
    
    private EditText txtPhoneNumber;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.phone_number);
        
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_SAVE, 0, R.string.menu_save)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_save);
        
        menu.add(0, MENU_ITEM_DISCARD, 0, R.string.menu_discard)
        .setShortcut('4', 'd')
        .setIcon(android.R.drawable.ic_menu_close_clear_cancel);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_SAVE:
        	// save phone number to file
        	onSaveFile();
        	setResult(RESULT_OK);
        	finish();
            break;  
        case MENU_ITEM_DISCARD:
        	// close this activity
            setResult(RESULT_CANCELED);
            finish();
        	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void onSaveFile() {
		OutputStream os;
		File path = getExternalFilesDir(null);
		File file = new File(path, PHONE_FILE);
		
		if(file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
		InputStream is;
		try {
		    is = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		byte[] data = null;
		try {
            data = new byte[is.available()];
            is.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
			
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
				
		try {
			os.write(data);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		String phNum =  txtPhoneNumber.getText().toString();
		String strEnter = new String("\n");
		try {
				os.write(phNum.getBytes());
				os.write(strEnter.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
    }
}
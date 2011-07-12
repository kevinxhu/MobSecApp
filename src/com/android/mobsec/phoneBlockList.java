package com.android.mobsec;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class phoneBlockList extends ListActivity {
    public static final int MENU_ITEM_DELETE = Menu.FIRST;
    public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;	
    
    public static String[] blockPhoneList = null;
    
    private static final String PHONE_FILE = "phone_number.txt";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.phone_list);
        
        ListView view = getListView();
        registerForContextMenu(view);
        blockPhoneList = onReadFile();
		if(blockPhoneList != null) {
	        setListAdapter(new ArrayAdapter(this, 
	                android.R.layout.simple_list_item_1, blockPhoneList));	
		}
		else {
			setListAdapter(null);
		}
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_phone_add)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_INSERT:
            // Launch activity to insert a new item
        	 try {
        		 startActivityForResult (new Intent(this, phoneNumber.class), 1); 
        	 }
        	 catch (android.content.ActivityNotFoundException e) {
        		  e.getClass();
        	 }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            return;
        }

        // Setup the menu header
        menu.setHeaderTitle(blockPhoneList[info.position]);

        // Add a menu item to delete the note
        menu.add(0, MENU_ITEM_DELETE, 1, R.string.menu_phone_del)
        .setIcon(android.R.drawable.ic_menu_delete);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            return false;
        }

        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
                onDelNumInFile(info.position);
                
           		blockPhoneList = onReadFile();
        		if(blockPhoneList != null) {
                setListAdapter(new ArrayAdapter(this, 
                        android.R.layout.simple_list_item_1, blockPhoneList));
        		}
        		else {
        			setListAdapter(null);
        		}
                return true;
            }
        }
        return false;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_OK) {
    		blockPhoneList = onReadFile();
    		if(blockPhoneList != null) {
            setListAdapter(new ArrayAdapter(this, 
                    android.R.layout.simple_list_item_1, blockPhoneList));
    		}
    		else {
    			setListAdapter(null);
    		}
    	}
    }
    
    private String[] onReadFile() {
		File path = getExternalFilesDir(null);
		File file = new File(path, PHONE_FILE);
		
		if(file.exists() == false) {
			return null;
		}   	
    	
		InputStream is;
		try {
		    is = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		byte[] data = null;
		try {
            data = new byte[is.available()];
            is.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  	
		
		String strData = new String(data);
		String[] listItem = strData.split("\n");
		return listItem;
    }
    
    private void onDelNumInFile(int index) {
		File path = getExternalFilesDir(null);
		File file = new File(path, PHONE_FILE);
		
		if(file.exists() == false) {
			return;
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
		
		String strData = new String(data);
		String[] listItem = strData.split("\n");
		OutputStream os;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		for(int i = 0; i < listItem.length; i++) {
			if(i != index) {
				try {
					os.write(listItem[i].getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
		}	
		
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
package com.android.mobsec;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public final class PolicyEntry extends Activity {
    // Menu item ids
    public static final int MENU_ITEM_SAVE = Menu.FIRST;
    public static final int MENU_ITEM_DISCARD = Menu.FIRST + 1;	
	
	 /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policy_entry);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_SAVE, 0, R.string.menu_save);
                //.setShortcut('4', 's')
                //.setIcon(android.R.drawable.ic_menu_save);
        //menu.add(0, MENU_ITEM_DISCARD, 1, R.string.menu_discard)
        //		.setShortcut('3', 'a')
        //		.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_SAVE:
        	break;
        case MENU_ITEM_DISCARD:
        	break;
        }
        return super.onOptionsItemSelected(item);
    }
}
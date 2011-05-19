package com.android.mobsec;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.view.KeyEvent;
import android.R.layout;
import android.text.Editable;
import android.text.InputFilter;

public final class PolicyEntry<KeyEvent> extends Activity {
    // Menu item ids
    public static final int MENU_ITEM_SAVE = Menu.FIRST;
    public static final int MENU_ITEM_DISCARD = Menu.FIRST + 1;	
    
    private Spinner mActionSpin;
    private EditText mIpEditTxt;
    private EditText mNameEditTxt;
    
    protected ArrayAdapter<CharSequence> mAdapter;
    
	 /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policy_entry);
        
        //name
        mNameEditTxt = (EditText) findViewById(R.id.txtPolicyName);
        // Action dropdown view
        mActionSpin = (Spinner) findViewById(R.id.entrySpinAction);
        // IP address input
        mIpEditTxt = (EditText) findViewById(R.id.entryTxtIP);
        mIpEditTxt.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == android.view.KeyEvent.ACTION_DOWN) &&
                    (keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                  // Perform action on key press
                  InputFilter[] filters = new InputFilter[1];
                  filters[0] = new InputFilter() {
  					public CharSequence filter(CharSequence source, int start,
							int end, android.text.Spanned dest, int dstart,
							int dend) {
                          if (end > start) {
                              String destTxt = dest.toString();
                              String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                              if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) { 
                                  return "";
                              } else {
                                  String[] splits = resultingTxt.split("\\.");
                                  for (int i=0; i<splits.length; i++) {
                                      if (Integer.valueOf(splits[i]) > 255) {
                                          return "";
                                      }
                                  }
                              }
                          }
                          return null;
                      }
                  };

                  mIpEditTxt.setFilters(filters);
                  return true;
                }
                return false;
            }
        });

        this.mAdapter = ArrayAdapter.createFromResource(
                this, R.array.actionArray, android.R.layout.simple_spinner_dropdown_item);
       
        mActionSpin.setAdapter(this.mAdapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        // This is our one standard application action -- inserting a
        // new note into the list.
        MenuItem mItem = menu.add(0, MENU_ITEM_SAVE, 0, R.string.menu_save);
        //mItem.setShortcut('4', 's');
        mItem.setIcon(android.R.drawable.ic_menu_save);
        mItem = menu.add(0, MENU_ITEM_DISCARD, 1, R.string.menu_discard);
        mItem.setShortcut('3', 'a');
        mItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_SAVE:
        	// save policy entry to data base 	 
        	setResult(RESULT_OK, this.getIntent());
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
}
package com.android.mobsec;

import com.android.mobsec.policyElem.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.view.KeyEvent;
import android.text.InputFilter;

@SuppressWarnings("hiding")
public final class PolicyEntry<KeyEvent> extends Activity {
    // Menu item ids
    public static final int MENU_ITEM_SAVE = Menu.FIRST;
    public static final int MENU_ITEM_DISCARD = Menu.FIRST + 1;	
    
    /**
     * Standard projection for the interesting columns of a normal note.
     */
    private static final String[] PROJECTION = new String[] {
            Elements._ID, // 0
            Elements.NAME, // 1
            Elements.TYPE, // 2
            Elements.IPADDR, //3
            Elements.NETMASK, //4

    };    
    /** The index of the note column */
    private static final int COLUMN_INDEX_ELEMENT = 1;
    
    private Spinner mActionSpin;
    private EditText mIpEditTxt;
    private EditText mNameEditTxt;
    private EditText mNetMaskEditTxt;
    private EditText mDomainEditTxt;
    private TextView mIpTxtView;
    private TextView mMaskTxtView;
    private RadioButton mIpRadioBt;
    private RadioButton mDomainRadBt;
    
    private Uri mUri;
    private Cursor mCursor;
    
    protected ArrayAdapter<CharSequence> mAdapter;
    
	 /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policy_entry);
        
        final Intent intent = getIntent();
        final String action = intent.getAction();
        
        //name
        mNameEditTxt = (EditText) findViewById(R.id.txtPolicyName);
        // Action dropdown view
        mActionSpin = (Spinner) findViewById(R.id.entrySpinAction);
        // IP address input
        mIpEditTxt = (EditText) findViewById(R.id.entryTxtIP);
    
        mDomainEditTxt = (EditText) findViewById(R.id.entryTxtDomain);
        
        mIpTxtView = (TextView) findViewById(R.id.entryStdIP);
        mMaskTxtView = (TextView) findViewById(R.id.entryStdMask);

        // network mask
        mNetMaskEditTxt = (EditText) findViewById(R.id.entryTxtMask);
        mNetMaskEditTxt.setOnKeyListener(keyListner);
        
        //Radio Group
        mIpRadioBt = (RadioButton) findViewById(R.id.radio_ip);
        mIpRadioBt.setOnClickListener(radioListener);

        mDomainRadBt = (RadioButton) findViewById(R.id.radio_domain);
        mDomainRadBt.setOnClickListener(radioListener);
        
        this.mAdapter = ArrayAdapter.createFromResource(
                this, R.array.actionArray, android.R.layout.simple_spinner_dropdown_item);
       
        mActionSpin.setAdapter(this.mAdapter);
        
        if (Intent.ACTION_EDIT.equals(action)) {
        	mUri = intent.getData();
            // Get the note!
            mCursor = managedQuery(mUri, PROJECTION, null, null, null);
            if (mCursor != null) {
                // Make sure we are at the one and only row in the cursor.
                mCursor.moveToFirst();
                
                String elem = mCursor.getString(COLUMN_INDEX_ELEMENT);
                mNameEditTxt.setText(elem);
                elem = mCursor.getString(COLUMN_INDEX_ELEMENT + 1);
                if(elem.compareTo("0") == 0) {
                	// IP address case
                	mIpRadioBt.setChecked(true);
                	
                	mIpTxtView.setText(R.string.txtIpAddr);
                	mDomainEditTxt.setVisibility(View.GONE);
                	mNetMaskEditTxt.setVisibility(View.VISIBLE);
                	mIpEditTxt.setVisibility(View.VISIBLE);
                	mMaskTxtView.setVisibility(View.VISIBLE);
                	
                    elem = mCursor.getString(COLUMN_INDEX_ELEMENT + 2);
                    mIpEditTxt.setText(elem);
                    elem = mCursor.getString(COLUMN_INDEX_ELEMENT + 3);
                    mNetMaskEditTxt.setText(elem);
                }
                else if(elem.compareTo("1") == 0) {
                	// domain name case
                	mDomainRadBt.setChecked(true);
                	
                	mNetMaskEditTxt.setVisibility(View.GONE);
                	mIpEditTxt.setVisibility(View.GONE);
                	mMaskTxtView.setVisibility(View.GONE);
                	mIpTxtView.setText(R.string.txtDomain);
                	mDomainEditTxt.setVisibility(View.VISIBLE);
                	
                    elem = mCursor.getString(COLUMN_INDEX_ELEMENT + 2);
                    mDomainEditTxt.setText(elem);
                }
            }
        }
        else if (Intent.ACTION_INSERT.equals(action)) {
            mIpEditTxt.setOnKeyListener(keyListner);
            mDomainEditTxt.setVisibility(View.GONE);
            mIpRadioBt.setChecked(true);
        }
    }
    
    private OnKeyListener keyListner = new OnKeyListener () {
        public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
            // If the event is a key-down event on the "enter" button
            EditText edtTxt = (EditText)v;
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

              edtTxt.setFilters(filters);
              return true;
            }
            return false;
        }    	
    };
    
    private OnClickListener radioListener = new OnClickListener() {
        public void onClick(View v) {
            // Perform action on clicks
            RadioButton rb = (RadioButton) v;
            if(rb.getId() == R.id.radio_domain) {
            	mIpTxtView.setText(R.string.txtDomain);
            	mDomainEditTxt.setVisibility(View.VISIBLE);
            	mNetMaskEditTxt.setVisibility(View.GONE);
            	mIpEditTxt.setVisibility(View.GONE);
            	mMaskTxtView.setVisibility(View.GONE);
            }
            else if(rb.getId() == R.id.radio_ip) {
            	mNetMaskEditTxt.setVisibility(View.VISIBLE);
            	mIpEditTxt.setVisibility(View.VISIBLE);
            	mMaskTxtView.setVisibility(View.VISIBLE);
            	mIpTxtView.setText(R.string.txtIpAddr);
            	mDomainEditTxt.setVisibility(View.GONE);
            }
        }
    };
    
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
        	String textName = mNameEditTxt.getText().toString();
        	Byte bType = 0;       
        	String textIpAddr = null;
        	String textNetMask = null;
        	
        	if(mIpRadioBt.isChecked()) {
        		bType = 0;
        		textIpAddr = mIpEditTxt.getText().toString();
        		textNetMask = mNetMaskEditTxt.getText().toString();
        	}
        	else {
        		bType = 1;
        		textIpAddr = mDomainEditTxt.getText().toString();
        		textNetMask = "";
        	}
        	if(textName.length() == 0 || textIpAddr.length() == 0) {
        		showAlertDialog(new String("Name and IP Address can not be empty!"));
        		return false;
        	}
        	final Intent intent = getIntent();
            try {
            	mUri = getContentResolver().insert(intent.getData(), null);
            }
            catch (Exception e) {
            	e.getCause();
            }
            if (mUri == null) {
                setResult(RESULT_CANCELED);
                finish();
                return false;
            }
        	  
            ContentValues values = new ContentValues();
            values.put(Elements.MODIFIED_DATE, System.currentTimeMillis());
            values.put(Elements.NAME, textName);
            values.put(Elements.TYPE, bType.toString());
            values.put(Elements.IPADDR, textIpAddr);
            values.put(Elements.NETMASK, textNetMask);
            // Commit all of our changes to persistent storage. When the update completes
            // the content provider will notify the cursor of the change, which will
            // cause the UI to be updated.
            getContentResolver().update(mUri, values, null, null);
            
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
    
    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
      }

}
package com.android.mobsec;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.android.mobsec.PolicyEntry;
import com.android.mobsec.policyPref;
import com.android.mobsec.policyElem.Elements;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class policyList extends ListActivity {
    /** Called when the activity is first created. */
    // Menu item ids
    public static final int MENU_ITEM_DELETE = Menu.FIRST;
    public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;	
    public static final int MENU_ITEM_PREFERENCE = Menu.FIRST + 2;
    
    private static boolean mPolSyncMode = true; // true for local mode and false for remote mode
    
    /**
     * Standard projection for the interesting columns of a normal note.
     */
    private static final String[] PROJECTION = new String[] {
            Elements._ID, // 0
            Elements.NAME, // 1
    };   
    private static final String[] PROJECTION1 = new String[] {
        Elements._ID, // 0
        Elements.NAME, // 1
        Elements.TYPE, // 2
        Elements.IPADDR, //3
        Elements.NETMASK, //4
    };   
    
    /** The index of the title column */
    private static final int COLUMN_INDEX_TITLE = 1;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // If no data was given in the intent (because we were started
        // as a MAIN activity), then use our default content provider.
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(Elements.CONTENT_URI);
        }
        
        // Inform the list we provide context menus for items
        getListView().setOnCreateContextMenuListener(this);
        
        // Perform a managed query. The Activity will handle closing and requerying the cursor
        // when needed.
        Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null,
                Elements.DEFAULT_SORT_ORDER);
        
        // Used to map notes entries from the database to views
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.policy_list, cursor,
                new String[] { Elements.NAME }, new int[] { R.id.policyList });
        setListAdapter(adapter);
    }
    
    @SuppressWarnings("unused")
	private byte[] onDownloadPolicy () {
        URL url;
		try {
			url = new URL("http://192.168.1.101/cacert.txt");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
        HttpURLConnection urlConnection;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		byte[] data = null;
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            data = new byte[in.available()];
            in.read(data);
        }
        catch(IOException e) {
        	e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return data;
    }
    
    public native int updateFwAcl(String configPath);
    
    private void onSaveFile() {
		OutputStream os;
		File path = getExternalFilesDir(null);
		File file = new File(path, "mobSec.txt");
		
		if(file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
			
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		//byte[] data = onDownloadPolicy();
		byte[] data = null;
		if(data != null) {
			try {
				os.write(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return;
		}
        Cursor cursor = managedQuery(getIntent().getData(), PROJECTION1, null, null,
                Elements.DEFAULT_SORT_ORDER);
        String name;
        String type;
        String ipAddr;
        String netMask;
		String strSpa = new String(" ");
		String strEnter = new String("\n");
		String strType0 = new String("0");
		if(cursor.moveToFirst() == false) {
			return;
		}
        while(cursor != null) {
        	name = cursor.getString(COLUMN_INDEX_TITLE);
        	type = cursor.getString(COLUMN_INDEX_TITLE + 1);
        	ipAddr = cursor.getString(COLUMN_INDEX_TITLE + 2);
        	netMask = cursor.getString(COLUMN_INDEX_TITLE + 3);
        
			try {
				os.write(name.getBytes());
				os.write(strSpa.getBytes());
				os.write(type.getBytes());
				os.write(strSpa.getBytes());
				os.write(ipAddr.getBytes());
				if (type.compareTo(strType0) == 0)
				{
					os.write(strSpa.getBytes());
					os.write(netMask.getBytes());
				}
				os.write(strEnter.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(cursor.moveToNext() == false) {
				break;
			}
        }
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		
		//inform msa firewall driver to update ACL configuration
		String configFile = file.getAbsolutePath();
		int ret;
		
		ret = updateFwAcl(configFile);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        String strMode;
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        strMode = prefs.getString("list_preference", new String(""));
        if(strMode.equalsIgnoreCase(new String("remote"))) {
        	mPolSyncMode = false;
            setListAdapter(null);
        }
        else {
        	mPolSyncMode = true;
            // Perform a managed query. The Activity will handle closing and requerying the cursor
            // when needed.
            Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null,
                    Elements.DEFAULT_SORT_ORDER);
            
            // Used to map notes entries from the database to views
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.policy_list, cursor,
                    new String[] { Elements.NAME }, new int[] { R.id.policyList });
            setListAdapter(adapter);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_OK) {
    		// update main UI list
    		data.getData();
    		onSaveFile();
    	}
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_add)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        menu.add(1, MENU_ITEM_PREFERENCE, 1, R.string.menu_pref)
        .setShortcut('4', 'p')
        .setIcon(android.R.drawable.ic_menu_preferences);

        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) 
    {
    	super.onPrepareOptionsMenu(menu);
        
        if(mPolSyncMode == false) {
        	menu.setGroupEnabled(0, false);
        }
    	return true;
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_INSERT:
            // Launch activity to insert a new item
        	 try {
        		 startActivityForResult (new Intent(Intent.ACTION_INSERT, getIntent().getData(), this, PolicyEntry.class), 1); 
        	 }
        	 catch (android.content.ActivityNotFoundException e) {
        		  e.getClass();
        	 }
            break;
        case MENU_ITEM_PREFERENCE:
        	try {
        		startActivity (new Intent(this, policyPref.class));
        	}
       	 	catch (android.content.ActivityNotFoundException e) {
       	 		e.getClass();
       	 	}
        	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        
	   	try {
	   		startActivityForResult (new Intent(Intent.ACTION_EDIT, uri, this, PolicyEntry.class), 1); 
	   	}
		catch (android.content.ActivityNotFoundException e) {
			e.getClass();
		}
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Setup the menu header
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

        // Add a menu item to delete the note
        menu.add(0, MENU_ITEM_DELETE, 1, R.string.menu_delete)
        .setShortcut('4', 'd')
        .setIcon(android.R.drawable.ic_menu_delete);
    }
    
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
                // Delete the note that the context menu is for
                Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
                getContentResolver().delete(noteUri, null, null);
                onSaveFile();
                return true;
            }
        }
        return false;
    }
    
    static {
        System.loadLibrary("msaFw");
    }
}
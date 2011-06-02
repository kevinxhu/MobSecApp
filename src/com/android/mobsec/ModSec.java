package com.android.mobsec;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class ModSec extends Activity{
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
                // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
      
        setContentView(R.layout.mob_sec);
        GridView gridview=(GridView)findViewById(R.id.gridview);
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();   
        for(int i=1;i<10;i++)   
        {   
          HashMap<String, Object> map = new HashMap<String, Object>();
          if(i==1){
                map.put("ItemImage", R.drawable.net_fw);
                map.put("ItemText", getResources().getString(R.string.gridview1));
          }
          if(i==2){  
              map.put("ItemImage", R.drawable.privatespace);
              map.put("ItemText", getResources().getString(R.string.gridview2));
          }
          if(i==3){  
              map.put("ItemImage", R.drawable.privatespace);
              map.put("ItemText", getResources().getString(R.string.gridview3));
          }
          if(i==4){  
              map.put("ItemImage", R.drawable.privatespace);
              map.put("ItemText", getResources().getString(R.string.gridview4));   
          }
          if(i==5){  
              map.put("ItemImage", R.drawable.privatespace);
              map.put("ItemText", getResources().getString(R.string.gridview5));
          }
          if(i==6){  
              map.put("ItemImage", R.drawable.privatespace);
              map.put("ItemText", getResources().getString(R.string.gridview6));
          }
          if(i==7){  
              map.put("ItemImage", R.drawable.privatespace);
              map.put("ItemText", getResources().getString(R.string.gridview7));
          }
          if(i==8){  
              map.put("ItemImage", R.drawable.privatespace);
              map.put("ItemText", getResources().getString(R.string.gridview8));
          }
          if(i==9){  
              map.put("ItemImage", R.drawable.privatespace);
              map.put("ItemText", getResources().getString(R.string.gridview9));
          }
          lstImageItem.add(map);
        }   

        SimpleAdapter saImageItems = new SimpleAdapter(this,
                                                  lstImageItem,
                                                  R.layout.grid_item,      
                                                  new String[] {"ItemImage","ItemText"},   
                                                  new int[] {R.id.ItemImage,R.id.ItemText});   
        gridview.setAdapter(saImageItems);   
        gridview.setOnItemClickListener(new ItemClickListener());   
    }
    
    public void launchNetFw() {
		   try {
			   startActivity (new Intent(this, networkFw.class));
		   }
		   catch (ActivityNotFoundException e) {
			   e.getCause();
		   }
    	
    }
    
    class  ItemClickListener implements OnItemClickListener   
    {   
       @SuppressWarnings("unchecked")
        public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened   
                                      View arg1,//The view within the AdapterView that was clicked   
                                      int arg2,//The position of the view in the adapter   
                                      long arg3//The row id of the item that was clicked   
                                    ) {   
    	   HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);   
   
    	   Object obj1 = item.get("ItemText");
    	   Object obj2 = getResources().getString(R.string.gridview1);
    	   if(obj1.equals(obj2)) {
    		   launchNetFw();
    	   }
       }   
    }
}
package com.chris.vocabularylist;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
 

/**
 * File name: 	MainMenu.java
 * Written by:	Christopher Dong
 * Date:		June 19, 2014
 * Purpose: 	This is the main page. User can import vocabulary list from a web server or access saved vocabulary list.
*/
public class MainMenu extends Activity {
 
	private EditText url;						// enter your computer IP address
    private Button btnViewProducts;				// view vocabulary list from the local web server
    private Button btnViewDatabase;				// view vocabulary list stored in SQLite
    private Button btnDeleteList;				// delete a vocabulary list stored in SQLite
    private Spinner spinnerList;				// show vocabulary list in SQLite
	//private TextView data;						// not being used
	private TextView  tvSpinner;				// let user know if there are lists saved on the app
	
	private static String directory;			// directory to the PHP files
	private String ipAddress;					// local computer IP address
	
	private List<String> list;					// store list names
	private ArrayAdapter<String> listAdapter;	// set the list adapter for the spinnerList
	
	private static String listName = "";		// not being used
	private static int listID = 0;				// store list ID
	ArrayList<GroupList> arraylist = null;		// array holds list ID and list name
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        
        btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
        btnViewDatabase = (Button) findViewById(R.id.btnViewDatabase);
        btnDeleteList = (Button) findViewById(R.id.btnDeleteList);
        spinnerList = (Spinner) findViewById(R.id.spinnerList);
        //data = (TextView) findViewById(R.id.textView1);
        tvSpinner = (TextView) findViewById(R.id.tvSpinner);
        
        //retrieve IP address
        url = (EditText) findViewById(R.id.etIPAddress);
        
        ipAddress = url.getText().toString();
 
        // combine IP address with directory path
        directory = ipAddress + "/webservice/vocabularylist/";

        // define the appear of the list item
        list = new ArrayList<String>();
    	listAdapter = new ArrayAdapter<String>(this,
    			android.R.layout.simple_spinner_item, list);

    	// retrieve vocabulary group list
    	Database2 entry = new Database2 (MainMenu.this);
		
		entry.open();
    	arraylist = Database2.getGroupArray();
    	entry.close();
    	
    	
    	// add vocabulary group list to drop down list
    	for (int i=0; i<arraylist.size(); i++)
		{
    		list.add(arraylist.get(i).getID() + "   " + arraylist.get(i).getListName());
		}
    	listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinnerList.setAdapter(listAdapter);

        
    	// Press to retrieve vocabulary from web server
        btnViewProducts.setOnClickListener(new View.OnClickListener() {
			@Override
            public void onClick(View view) {
            	if ( isNetworkConnected() == true)
            	{
        			do
        			{
        				// successful connection to web server
	        			if (CheckConnection.getURL() == 1)
	        			{
	        				// Loading vocabulary in Background Thread
	        	            Intent intent = new Intent(getApplicationContext(), AllProductsActivity.class);
	        	            startActivity(intent);
	        			}
	        			
	        			// failed to connect to web server
	        			else if (CheckConnection.getURL() == 2)
	        			{
	        				Toast.makeText(getApplicationContext(), "Failed to connect to server",
	        			               Toast.LENGTH_SHORT).show();
	        				CheckConnection2();
	        			}
        			} while (CheckConnection.getURL() == 0);
            	}
            	else
            	{
    				Toast.makeText(getApplicationContext(), "No Internet Connection",
 			               Toast.LENGTH_SHORT).show();
            	}
            }
        });
 
        // view products click event
        btnViewDatabase.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
            	
				if (spinnerList.getSelectedItem() != null)
				{
					int position = spinnerList.getSelectedItemPosition();
					listID = Integer.parseInt(arraylist.get(position).getID());
					
	                // Launching create new product activity
	                Intent intent = new Intent(getApplicationContext(), ListVocab.class);
	                intent.putExtra("getData", "sqlite");     
	                startActivity(intent);
				}
            }
        });
        
        btnDeleteList.setOnClickListener(new View.OnClickListener() {
        	 
            @Override
            public void onClick(View view) {
            	
				if (spinnerList.getSelectedItem() != null)
				{
					int position = spinnerList.getSelectedItemPosition();
					listID = Integer.parseInt(arraylist.get(position).getID());
					
					Database2 entry = new Database2 (MainMenu.this);
					
					entry.open();
					Database2.deleteList(listID);
			    	entry.close();
					
					
	                Intent intent = new Intent(getApplicationContext(), MainMenu.class);  
	                startActivity(intent);
				}
            }
        });
    }
    
    protected void onResume() {
        super.onResume();
        

       // new CheckConnection(this, data).execute(url_all_products);
        CheckConnection2();
        
        list.clear();
    	for (int i=0; i<arraylist.size(); i++)
		{
    		list.add(arraylist.get(i).getID() + "   " + arraylist.get(i).getListName());
		}
   
    	listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinnerList.setAdapter(listAdapter);
    	
    	
        // when sqlite is empty
        if (spinnerList.getSelectedItem() == null)
		{
			btnViewDatabase.setEnabled(false);
			btnViewDatabase.setBackgroundResource(R.drawable.custom_btn_seagull);
			btnViewDatabase.setTextAppearance(this, R.style.btnStyleSeagull);
			
			btnDeleteList.setEnabled(false);
			btnDeleteList.setBackgroundResource(R.drawable.custom_btn_seagull);
			btnDeleteList.setTextAppearance(this, R.style.btnStyleSeagull);
			
			spinnerList.setEnabled(false);
			tvSpinner.setText("You have no list saved on this app");
			tvSpinner.setTextColor(Color.parseColor("#0033CC"));

		}
    }

    //  this is replaced with getSelectedListID()
    public static String getSelectedListName()
    {
    	return listName;
    }
    
    public static int getSelectedListID()
    {
    	return listID;
    }
    
    
    public static String getDirectory() {
    	return directory;
    }
    
    /**
     * check if device can connection to web server when onResume method begins or when btnViewProducts is pressed
     */
    public void CheckConnection2()
    {
    	new CheckConnection(this).execute(directory); //, data).execute(directory);
    }
    
    /**
     * prevent pressing back button
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent a = new Intent(this, MainMenu.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * check for Internet connection
     * @return false if no network connection
     */
    private boolean isNetworkConnected() {
    	  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	  NetworkInfo ni = cm.getActiveNetworkInfo();
    	  if (ni == null) {
    	   // There are no active networks.
    	   return false;
    	  } else
    	   return true;
    }
}

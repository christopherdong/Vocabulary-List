package com.chris.vocabularylist;


import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class AllProductsActivity extends ListActivity implements OnClickListener  {
 
    // Progress Dialog
    private ProgressDialog pDialog;
    
    
    // Create an array of words
    private static WordObject[] wordArray = new WordObject[20];
    static int countWords = 0;
    private String listName = "Android";
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> productsList;
 
    // url to get all products list
    private static String url_all_products = MainMenu.getDirectory() + "get_all_words.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_ID = "ID";
    private static final String TAG_WORD = "Word";
    private static final String TAG_WORDTYPE = "WordType";
    private static final String TAG_DEFINITION = "Definition";
    private static final String TAG_EXAMPLE = "Example";
    
    // products JSONArray
    JSONArray products = null;
    
    
    Button btnMainActivity;		// view words in a expandable list along with speak buttons
    Button btnNewWord;			// add a new word to the web server
    Button btnSaveToDatabase;	// save words to sqlite
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_products);

        btnMainActivity = (Button) findViewById(R.id.btnWordList);
        btnNewWord = (Button) findViewById(R.id.btnAddWord);
        btnSaveToDatabase = (Button) findViewById(R.id.btnSaveToDatabase);
        
        btnSaveToDatabase.setOnClickListener(this);

        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        new LoadAllProducts().execute();
		
        // Get listview
        ListView lv = getListView();
 
        // on selecting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();
 
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditProductActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_ID, pid);
 
                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });
        
        // view products click event
        btnMainActivity.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching All products Activity
                Intent intent = new Intent(getApplicationContext(), ListVocab.class);
                intent.putExtra("getData", "JSON");
                startActivity(intent);
 
            }
        });
        
        
        // view products click event
        btnNewWord.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
                startActivity(i);
 
            }
        });
    }
 
    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllProductsActivity.this);
            pDialog.setMessage("Loading vocabulary list. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            System.out.println("dialog");
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            

            try {
                // getting JSON string from URL
                JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
         
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);
                  
                    countWords = products.length();

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String word = c.getString(TAG_WORD);
                        String wordtype = c.getString(TAG_WORDTYPE);
                        String definition = c.getString(TAG_DEFINITION);
                        String example = c.getString(TAG_EXAMPLE);
                        
                        wordArray[i] = new WordObject(id, word, wordtype, definition, example);
                        
                        wordtype = "Type: " + wordtype;
                        definition = "i.e. " + definition;
                        example = "e.g. " + example;
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_WORD, word);
                        map.put(TAG_WORDTYPE, wordtype);
                        map.put(TAG_DEFINITION, definition);
                        map.put(TAG_EXAMPLE, example);
 
                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);

                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                pDialog.dismiss();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            AllProductsActivity.this, productsList,
                            R.layout.list_item, new String[] { TAG_ID,
                            		TAG_WORD, TAG_WORDTYPE, TAG_DEFINITION, TAG_EXAMPLE},
                            new int[] { R.id.pid, R.id.word, R.id.wordType, R.id.definition, R.id.example});
                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }
    
    public static WordObject[] getWords() {
    	
    	return wordArray;
    }
    
    public static int getCountWords() {
    	return countWords;
    }
    
    // prevent pressing back button
    /*
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
    */
   
    public boolean CheckConnection()
    {
        try{
            URL myUrl = new URL(url_all_products);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		
		case R.id.btnSaveToDatabase:
	
			try {
				// give the list a name before saving the words in sqlite
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.custom_dialog);
				dialog.setTitle("Save Vocabulary List");
				
				TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
				tvMessage.setText("List Name");
				
				Button btnClose3 = (Button) dialog.findViewById(R.id.btnClose2);
				Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
				final EditText etListName = (EditText) dialog.findViewById(R.id.etListName);
				
				dialog.show();
				
				btnOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					
						listName = etListName.getText().toString();
						Database2 entry = new Database2 (AllProductsActivity.this);
	
						entry.open();
						entry.createGroup(listName, products.length());
						String listID = entry.getListID();
						for (int i = 0; i < products.length(); i++) {
							entry.createEntry(listID, wordArray[i].getWord(), wordArray[i].getWordtype(), wordArray[i].getDefinition(), wordArray[i].getExample());
						}
						entry.close();
						dialog.dismiss();
						
		                Intent intent = new Intent(getApplicationContext(), ListVocab.class);
		                intent.putExtra("getData", "JSON");
		                startActivity(intent);
					}
				});
				
				btnClose3.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {		
						dialog.dismiss();
					}
				});
				
				break;
			
			} catch (Exception e) {
			} finally {
			}
		}
	}
	
	   protected void onResume() {
	        super.onResume();
	   }
}
    


package com.chris.vocabularylist;

import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 
public class EditProductActivity extends Activity {
 
    EditText txtWord;
    EditText txtWordType;
    EditText txtDefinition;
    EditText txtExample;
    Button btnSave;
    Button btnDelete;
 
    //String pid;
    String id;
    String word = "word";
    String wordType = "wordType";
    String definition = "definition";
    String example = "example";
    
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
 
    // single word url
    private static final String url_product_detials = MainMenu.getDirectory() + "get_product_details.php";
 
    // url to update word
    private static final String url_update_product = MainMenu.getDirectory() + "update_product.php";
 
    // url to delete word
    private static final String url_delete_product = MainMenu.getDirectory() + "delete_product.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_ID = "ID";
    private static final String TAG_WORD = "Word";
    private static final String TAG_WORDTYPE = "WordType";
    private static final String TAG_DEFINITION = "Definition";
    private static final String TAG_EXAMPLE = "Example";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);
 
        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
 
        // getting product details from intent
        Intent i = getIntent();
 
        // getting product id (pid) from intent
        //pid = i.getStringExtra(TAG_PID);
        id = i.getStringExtra(TAG_ID);
 
        // Getting complete product details in background thread
        new GetProductDetails().execute();
 
        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                new SaveProductDetails().execute();
            }
        });
 
        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                new DeleteProduct().execute();
            }
        });
 
    }
 
    /**
     * Background Async Task to Get complete product details
     * */
    class GetProductDetails extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Loading word details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... args) {
     
                    // Check for success tag
                	List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                	params2.add(new BasicNameValuePair(TAG_ID, id));
                	JSONObject json = jsonParser.makeHttpRequest(url_product_detials, "GET", params2);
                    
                    try {
                    	int success = json.getInt(TAG_SUCCESS);

                        // check your log for json response
                        Log.d("Single Word Details", json.toString());

                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json.getJSONArray(TAG_PRODUCT); // JSON Array
                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // Edit Text
                            txtWord = (EditText) findViewById(R.id.inputWord);
                            txtWordType = (EditText) findViewById(R.id.inputWordType);
                            txtDefinition = (EditText) findViewById(R.id.inputDefinition);
                            txtExample = (EditText) findViewById(R.id.inputExample);
 
                            // display product data in EditText
                            System.out.println(product.getString(TAG_WORD));
                            
                            word = product.getString(TAG_WORD);
                            wordType = product.getString(TAG_WORDTYPE);
                            definition = product.getString(TAG_DEFINITION);
                            example = product.getString(TAG_EXAMPLE);
                        
                        } else {
                            // product with pid not found
                            Intent i = new Intent(getApplicationContext(),
                                    NewProductActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();

            txtWord.setText(word);
            txtWordType.setText(wordType);
            txtDefinition.setText(definition);
            txtExample.setText(example);
            
            
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }
    }
 
    /**
     * Background Async Task to Save product Details
     * */
    class SaveProductDetails extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Saving word ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {
 
            // getting updated data from EditTexts
            String word = txtWord.getText().toString();
            String wordType = txtWordType.getText().toString();
            String definition = txtDefinition.getText().toString();
            String example = txtExample.getText().toString();
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
           // params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_ID, id));
            params.add(new BasicNameValuePair(TAG_WORD, word));
            params.add(new BasicNameValuePair(TAG_WORDTYPE, wordType));
            params.add(new BasicNameValuePair(TAG_DEFINITION, definition));
            params.add(new BasicNameValuePair(TAG_EXAMPLE, example));
            
            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                    "POST", params);
 
            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                System.out.println("try");
 
                if (success == 1) {
                	System.out.println("success");
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                	
                    // failed to update product
                	System.out.println("failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product uupdated
            pDialog.dismiss();
        }
    }
 
    /*****************************************************************
     * Background Async Task to Delete Product
     * */
    class DeleteProduct extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Deleting word...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Deleting product
         * */
        protected String doInBackground(String... args) {
 
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
               // params.add(new BasicNameValuePair("pid", pid));
                params.add(new BasicNameValuePair(TAG_ID, id));
 
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_product, "POST", params);
 
                // check your log for json response
                Log.d("Delete Product", json.toString());
 
                // json success tag
                success = json.getInt(TAG_SUCCESS);
                
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
                    finish();
                    
                    System.out.println("success");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
 
        }
 
    }
}

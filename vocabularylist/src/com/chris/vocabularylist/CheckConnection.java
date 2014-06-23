package com.chris.vocabularylist;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * File name: 	CheckConnection.java
 * Written by:	Christopher Dong
 * Date:		June 19, 2014
 * Purpose: 	Check Internet and URL connections. Perform checking using background thread.
*/
public class CheckConnection extends  AsyncTask<String, String, String>{

   //private TextView dataField;
   private Context context;
   private static int accessURL = 0;
   
   public CheckConnection(Context context) { //, TextView dataField) {
      this.context = context;
      //this.dataField = dataField;
   }

   //check Internet connection.
   private void checkInternetConenction(){
      ConnectivityManager check = (ConnectivityManager) this.context.
      getSystemService(Context.CONNECTIVITY_SERVICE);
      if (check != null) 
      {
         NetworkInfo[] info = check.getAllNetworkInfo();
         if (info != null) 
         {
        	 System.out.println(info);
	           for (int i = 0; i <info.length; i++) 
	           {
		            if (info[i].getState() == NetworkInfo.State.CONNECTED)
		            {
		               Toast.makeText(context, "Internet is connected",
		               Toast.LENGTH_SHORT).show();
		            }
	           }
         }
      }
      
      else{
         Toast.makeText(context, "not conencted to internet",
         Toast.LENGTH_SHORT).show();
         
          }
   }
   
   @Override
   protected void onPreExecute(){
	   super.onPreExecute();
      checkInternetConenction();
   }
 
   // try connecting to URL
   @Override
   protected String doInBackground(String... arg0) {
      try{
    	 accessURL = 0;
         String link = (String)arg0[0];
         URL url = new URL(link);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setReadTimeout(3000);
         conn.setConnectTimeout(5000);
         conn.setRequestMethod("GET");
         conn.setDoInput(true);
         conn.connect();
         InputStream is = conn.getInputStream();
         accessURL = 1;
         BufferedReader reader = new BufferedReader(new InputStreamReader
         (is, "UTF-8") );
         String data = null;
         String webPage = "";
         
         while ((data = reader.readLine()) != null){
            webPage += data + "\n";
         }
         
         return webPage;
      } catch(Exception e){
    	  accessURL = 2;
         return new String("Exception: " + e.getMessage());
      }
   }
   
   @Override
   protected void onPostExecute(String result){
   }
   
   public static int getURL() {
	   return accessURL;
   }
}

package com.chris.vocabularylist;

import java.util.ArrayList;
import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

/**
* File Name:	ListVocab.java
* Written by:	Christopher Dong
* Date:			June 18, 2014
* Purpose: 		Create GroupList objects, so user can save more than one vocabulary list.
*/
public class ListVocab extends Activity {

	private CustomExpandableListAdapter mAdapter;	// adapter creates the structure of the list item
	private ExpandableListView expList;				
	private Button stopSpeech;
	
	WordObject[] wordArray = new WordObject[] {};	// hold the word information (word, type, definition, example)
	int countWords = 0;								// amount of words
	private String getData;							// sqlite or website
	private int listID;								// find the list ID if retrieving data from sqlite

	private static TextToSpeech tts;				// read text

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_vocab);
		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC); //control media sound
		expList = (ExpandableListView) findViewById(R.id.exp_voucher_info);
		Bundle bundle = getIntent().getExtras();
		getData = bundle.getString("getData"); // determine where to fetch data (sqlite or website)
		ArrayList<WordObject> data = InitData();
		
		mAdapter = new CustomExpandableListAdapter(this, data);
		
		expList.setAdapter(mAdapter); 
		
		stopSpeech = (Button) findViewById(R.id.btn_stop_speech);
		
		// Ver 1.3 Add text to speech
		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

			public void onInit(int status) {
				// TODO Auto-generated method stub

				if (status == TextToSpeech.SUCCESS) {

					int result = tts.setLanguage(Locale.ENGLISH);
					System.out.print(tts.getDefaultEngine());
					Log.e("TTS", "TTS  is available");
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e("TTS", "This Language is not supported");

						// missing data, install it
						Intent installIntent = new Intent();
						installIntent
								.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						// startActivity(installIntent);

					} else {
						// btnSpeak.setEnabled(true);
						// speakOut();
					}

				} else {
					Log.e("TTS", "Initilization Failed!");
				}
			}

		});

		// stop tts from speaking when button is pressed
		stopSpeech.setOnClickListener(new View.OnClickListener(){  	
        	public void onClick(View v){   
        		soundQ("");	// interrupt current speech with a blank speech
        	}
        });
	}
	
	protected void onResume() {
        super.onResume(); 
	}
	
	
	public static void soundQ(String sound) {
		tts.speak(sound, TextToSpeech.QUEUE_FLUSH, null);
	}

	private ArrayList<WordObject> InitData() {
		ArrayList<WordObject> retData = new ArrayList<WordObject>();

		WordObject[] vocab = new WordObject[20];
		
		if (getData.equals("sqlite"))
		{
			listID = MainMenu.getSelectedListID();
			long convertListID = listID;

			Database2 entry = new Database2 (ListVocab.this);
			entry.open();
			wordArray = entry.getWordArray(convertListID);
			countWords = entry.getCount();
			entry.close();
		}
		else
		{
			System.out.println("JSON");
			wordArray = AllProductsActivity.getWords();
			countWords = AllProductsActivity.getCountWords();
			
		}
		
		for (int i=0; i < countWords; i++)
		{
			vocab[i] = new WordObject(Integer.toString(i), wordArray[i].getWord(), wordArray[i].getWordtype(), 
					wordArray[i].getDefinition(), wordArray[i].getExample());
			retData.add(vocab[i]);
			
		}

		return retData;
	}
	
	// not implemented
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    // not implemented
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	return false;
    }
    
    // Must destroy tts object before leaving this Activity
    @Override
    protected void onDestroy() {
        //Close the Text to Speech Library
        if(tts != null) {

        	tts.stop();
        	tts.shutdown();
        }
        super.onDestroy();
    }
    
}
package com.chris.vocabularylist;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database2 {
	
	// word columns
	public static final String KEY_ROWID = "_id";
	public static final String KEY_LIST = "list_name";
	public static final String KEY_WORD = "word";
	public static final String KEY_TYPE = "word_type";
	public static final String KEY_DEFINITION = "definition";
	public static final String KEY_EXAMPLE = "example";
	
	// list columns
	public static final String KEY_GROUPID = "_groupid";
	public static final String KEY_LISTNAME = "list_name";
	public static final String KEY_LENGTH = "length";
	
	private static final String DATABASE_NAME = "HotOrNotdb";
	private static final String DATABASE_TABLE = "peopleTable";
	private static final String DATABASE_GROUP = "groupTable";
	private static final int DATABASE_VERSION = 1;
	
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private static SQLiteDatabase ourDatabase;
	
    // create an array of words
	// limit the array to 20 items
    private static GroupList[] groupList = new GroupList[20];
 
    // count the words 
    private static int countWords = 0;
   
    
	
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
			db.execSQL("CREATE TABLE " +  DATABASE_TABLE + " (" +
					KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_LIST + " TEXT NOT NULL, " +
					KEY_WORD + " TEXT NOT NULL, " +
					KEY_TYPE + " TEXT NOT NULL, " +
					KEY_DEFINITION + " TEXT NOT NULL, " +
					KEY_EXAMPLE + " TEXT NOT NULL);" 
			);
			
			db.execSQL("CREATE TABLE " +  DATABASE_GROUP + " (" +
					KEY_GROUPID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_LISTNAME + " TEXT NOT NULL, " +
					KEY_LENGTH + " TEXT NOT NULL);"
			);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_GROUP);
			onCreate(db);
		}
	}
	
	
	
	public Database2 (Context c) {
		ourContext = c;
	}
	
	public Database2 open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		ourHelper.close();
	}
	
	
	
	public long createEntry(String listID, String word, String wordType, String definition, String example) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_LIST, listID);
		cv.put(KEY_WORD, word);
		cv.put(KEY_TYPE, wordType);
		cv.put(KEY_DEFINITION, definition);
		cv.put(KEY_EXAMPLE, example);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
	}
	
	public long createGroup(String listName, String length)
	{
		ContentValues cv = new ContentValues();
		cv.put(KEY_LISTNAME, listName);
		cv.put(KEY_LENGTH, length);
		return ourDatabase.insert(DATABASE_GROUP, null, cv);
	}
	

	public WordObject[] getWordArray(long listID){
		String[] columns = new String[] { KEY_ROWID, KEY_LIST, KEY_WORD, KEY_TYPE, KEY_DEFINITION, KEY_EXAMPLE};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_LIST + "=" + listID, null, null, null, null);
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iName = c.getColumnIndex(KEY_WORD);
		int iHotness = c.getColumnIndex(KEY_TYPE);
		int iDefinition = c.getColumnIndex(KEY_DEFINITION);
		int iExample = c.getColumnIndex(KEY_EXAMPLE);
		
		WordObject[] wordArray = new WordObject[20];
		countWords = 0;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			wordArray[countWords] = new WordObject(c.getString(iRow), c.getString(iName), c.getString(iHotness), c.getString(iDefinition), c.getString(iExample));
			countWords++;
		}
		c.close();
		
		return wordArray;
	}
	
	public int getCount() {
		
		return countWords;
	}
	
	
	public long createGroup(String listName, int length)
	{
		ContentValues cv = new ContentValues();
		cv.put(KEY_LISTNAME, listName);
		cv.put(KEY_LENGTH, length);
		return ourDatabase.insert(DATABASE_GROUP, null, cv);
	}
	
	public static ArrayList<GroupList> getGroupArray() {
		
		ArrayList<GroupList> arraylist = new ArrayList<GroupList>();
		
		String[] columns = new String[] { KEY_GROUPID, KEY_LISTNAME, KEY_LENGTH};
		Cursor c = ourDatabase.query(DATABASE_GROUP, columns, null, null, null, null, null);

		int countList = 0;
		int iRow = c.getColumnIndex(KEY_GROUPID);
		int iListName = c.getColumnIndex(KEY_LISTNAME);
		int iLength = c.getColumnIndex(KEY_LENGTH);

		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			groupList[countList] = new GroupList(c.getString(iRow), c.getString(iListName), c.getString(iLength));
			arraylist.add(groupList[countList]);
			countList++;
		}
		
		c.close();
		
		return arraylist;
	}

	
	public String getListID() {
		String[] columns = new String[] { KEY_GROUPID, KEY_LISTNAME, KEY_LENGTH};
		Cursor c = ourDatabase.query(DATABASE_GROUP, columns, null, null, null, null, null);
		String groupID;
		c.moveToLast();
		groupID = c.getString(c.getColumnIndex(KEY_GROUPID));
		c.close();
		return groupID;
	}

	public static void deleteList(long id) throws SQLException {
		ourDatabase.delete(DATABASE_GROUP, KEY_GROUPID + "=" + id, null);
	}
}

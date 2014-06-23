package com.chris.vocabularylist;

/**
* File Name:	WordObject.java
* Written by:	Christopher Dong
* Date:			June 18, 2014
* Purpose: 		Create word objects.
*/
public class WordObject {
	String id;			// word ID
	String listName;	// lesson number
	String word;		// word
	String wordtype; 	// noun, verb or adjective
	String definition;	// word definition
	String example;		// word used in a sentence
	
	public WordObject(String id, String word, String wordtype, String definition, String example)
	{
		this.id = id;
		this.word = word;
		this.wordtype = wordtype;
		this.definition = definition;
		this.example = example;
	}
	
	public int getID() {
		return Integer.parseInt(id);
	}
	
	public String getList() {
		return listName;
	}
	
	public String getWord() {
		return word;
	}
	
	public String getWordtype() {
		return wordtype;
	}
	public String getDefinition() {
		return definition;
	}
	public String getExample() {
		return example;
	}

	// used for testing
	public void printWord() {
		 System.out.println("word:"+ word );
		 System.out.println("wordtype:"+ wordtype );
		
	}
}

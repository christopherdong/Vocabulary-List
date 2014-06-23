package com.chris.vocabularylist;


/**
 * File Name:	GroupList.java
 * Written by:	Christopher Dong
 * Date:		June 18, 2014
 * Purpose: 	Create GroupList objects, so user can save more than one vocabulary list.
 */
public class GroupList {
	String id;
	String listName;
	String length;
	
	public GroupList(String id, String listName, String length)
	{
		this.id = id;
		System.out.println(id);
		this.listName = listName;
		this.length = length;
	}
	
	public String getID() {
		return id;
	}
	
	public String getListName() {
		return listName;
	}
	
	public String getLength() {
		return length;
	}
	
}

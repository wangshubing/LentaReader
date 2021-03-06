package com.xeppaka.lentareader.data.db;


public abstract class NewsEntry extends NewsObjectEntry {
	private NewsEntry() {}

	public static final String TABLE_NAME = "News";
	
	public static final String SQL_CREATE_TABLE_COLUMNS = NewsObjectEntry.SQL_CREATE_TABLE_COLUMNS;

	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + SQL_CREATE_TABLE_COLUMNS + ")";
	public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

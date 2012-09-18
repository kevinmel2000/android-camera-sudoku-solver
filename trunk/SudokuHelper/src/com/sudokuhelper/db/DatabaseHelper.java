package com.sudokuhelper.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

	public static final String DATABASE_NAME = "sudokuHelper";
	public static final String TABLE_NAME = "sudoku";
	public static final String TAG = "DatabaseHelper";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 13);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + TABLE_NAME + "(_id integer primary key autoincrement,data Text unique,name Text,time Text,state integer);");
		insertSudoku(db, 1, "900200050076008040000400003060100004004090500200006070300004000020800430080005002");
		insertSudoku(db, 2, "027380010010006735000000029305692080000000000060174503640000000951800070080065340");
		insertSudoku(db, 3, "003900760040006009607010004200670090004305600010049007700090201300200040029008500");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ".");
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
	}
	public void insertSudoku(SQLiteDatabase db, long  sudokuId, String Data) {
		String sql = "insert into " + TABLE_NAME + " values (" + sudokuId + ", '" + Data + "' , 'ÌâÄ¿ " + sudokuId + "', '" + "2012-09-20 00:00:00" + "', null);";
		db.execSQL(sql);
	}
}

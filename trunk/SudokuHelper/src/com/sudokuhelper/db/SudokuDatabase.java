package com.sudokuhelper.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.sudokuhelper.game.CellCollection;
import com.sudokuhelper.game.SudokuGame;


public class SudokuDatabase {
private DatabaseHelper mOpenHelper;
    
    public SudokuDatabase(Context context) {
    	mOpenHelper = new DatabaseHelper(context);
    }
    
    /*******************************
     * 根据sudokuId返回相应的SudokuGame
     * @param sudokuId
     * @return SudokuGame
     */
    public SudokuGame getSudoku(long sudokuId) {
    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(DatabaseHelper.TABLE_NAME);
        qb.appendWhere("_id =" + sudokuId);
        
        // Get the database and run the query
        
        SQLiteDatabase db = null;
        Cursor c = null;
        SudokuGame s = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
        	
        	if (c.moveToFirst()) {
            	long id = c.getLong(c.getColumnIndex("_id"));
            	String data = c.getString(c.getColumnIndex("data"));
            	int state = c.getInt(c.getColumnIndex("state"));
            	//long time = c.getLong(c.getColumnIndex("time"));
            	//String note = c.getString(c.getColumnIndex("note"));
            	s = new SudokuGame();
            	s.setId(id);
            	//s.setCreated(created);
            	s.setCells(CellCollection.deserialize(data));
            	//s.setLastPlayed(lastPlayed);
            	s.setState(state);
            	//s.setTime(time);
            	//s.setNote(note);
        	}
        } catch (Exception e){
        	Log.e("QueryError","Query Error!");
        }finally {
        	if (c != null) c.close();
        }
        
        return s;
    }
    
    /**
     * 根据sudokuId返回相应的Data
     * @param sudokuId
     * @return
     */
    public String getLevelById(long sudokuId){
    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseHelper.TABLE_NAME);
        qb.appendWhere("_id =" + sudokuId);
        // Get the database and run the query
        SQLiteDatabase db = null;
        Cursor c = null;
        String data = "";
        try {
            db = mOpenHelper.getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
        	
        	if (c.moveToFirst()) {
            	data = c.getString(c.getColumnIndex("data"));
        	}
        } catch (Exception e){
        	Log.e("QueryError","Query Error!");
        }finally {
        	if (c != null) c.close();
        }
        return data;
    }
    /**
     * 返回所有的数独列表
     */
    public Cursor getSudokuList(){
    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    	qb.setTables(DatabaseHelper.TABLE_NAME);
        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c =  qb.query(db, null, null, null, null, null, null);
    	return c;
    }
    //关闭数据库
    public void close() {
		mOpenHelper.close();
	}
    /**
     * 添加数独数据到table
     * @param sudoku数独
     */
    public long insertSudoku(SudokuGame sudoku) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor c;
		c = db.rawQuery("select max(_id) as max from " + DatabaseHelper.TABLE_NAME,null);
		long max = 8;
		if(c.moveToFirst()) {
			max =c.getLong(c.getColumnIndex("max"));
		}
		System.out.println(max);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		
		ContentValues values = new ContentValues();
		values.put("data", sudoku.getCells().getDataFromCellCollection());
		values.put("name", "题目" + (max + 1));
		values.put("time", df.format(new Date()));// new Date()为获取当前系统时间
		values.put("state", sudoku.getState());
		long retVal = db.insert(DatabaseHelper.TABLE_NAME, null, values);
		return retVal;
    }
    /**
     * 删除数独数据到table
     * @param sudoku数独
     */
    public void deleteSudoku(long sudokuID) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.delete(DatabaseHelper.TABLE_NAME,"_id" + "=" + sudokuID, null);
	}
}

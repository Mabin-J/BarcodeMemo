package org.dlug.android.barcodememo;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	private String welcomeMessage;
	private List<String> changeLog;
	private String changeLogHeader;
	private int currentIdx;
	
	private SQLiteDatabase oSQLiteDB;

	public DBHelper(Context context, CursorFactory factory, int version, String welcomeMessage, String changeLogHeader, List<String> changeLog){
		super(context, "barcodememo.db", null, version);

		this.welcomeMessage = welcomeMessage;
		this.changeLogHeader = changeLogHeader;
		this.changeLog = changeLog;
		
		oSQLiteDB = this.getReadableDatabase();
		currentIdx = getLatestID(oSQLiteDB);
	}

	public int getLatestID(){
		return getLatestID(oSQLiteDB);
	}
	
	private int getLatestID(SQLiteDatabase tmpDB){
		int currentIdx = 0;
		
		try{
			Cursor result = tmpDB.rawQuery("SELECT _id FROM barcodememo ORDER BY _id DESC;", null);
			result.moveToFirst();
			currentIdx = Integer.valueOf(result.getString(0));
		} catch (Exception e){
			
		}
		return currentIdx;
	}
	
	public String getLatestData(){
		return getData(getLatestID());
	}
	
	public String getCurrentData(){
		return getData(currentIdx);
	}
	
	public void setWelcomeMessage(String welcomeMessage){
		this.welcomeMessage = welcomeMessage;
	}
	
	public void setChangeLog(List<String> changeLog){
		this.changeLog = changeLog;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE barcodememo (_id INTEGER PRIMARY KEY, data TEXT);");
		db.execSQL("INSERT INTO barcodememo (_id, data) VALUES (0, \"" + welcomeMessage + "\n\n\");");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		String backupString = changeLogHeader + "\n";
		for(int i = oldVersion; i < newVersion; i++){
			backupString += changeLog.get(i) + "\n\n";
		}

		int currentIdx = 0;
		
		currentIdx = getLatestID(db);
		
		try{
			Cursor result = db.rawQuery("SELECT data FROM barcodememo WHERE _id = " + currentIdx + ";", null);
			result.moveToFirst();
			backupString = result.getString(0) + "\n\n" + backupString;
		} catch(Exception e){
			
		}
//		db.execSQL("DROP TABLE IF EXISTS barcodememo");
//		db.execSQL("CREATE TABLE barcodememo (_id INTEGER PRIMARY KEY, data TEXT);");
		db.execSQL("INSERT INTO barcodememo (_id, data) VALUES (" + (currentIdx + 1) + ", \"" + backupString + "\n\");");
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
		
	}
	
	public void putData(String data){
		currentIdx++;
		oSQLiteDB.execSQL("INSERT INTO barcodememo (_id, data) VALUES (" + currentIdx + ", \"" + data + "\");");
	}
	
	public String getData(int idNumber){
		Cursor result = oSQLiteDB.rawQuery("SELECT data FROM barcodememo WHERE _id=" + idNumber + ";", null);
		result.moveToFirst();
		
		return result.getString(0);
	}
	
	public void delData(){
		oSQLiteDB.execSQL("DELETE FROM barcodememo WHERE _id = " + currentIdx + ";");
		currentIdx--;
	}
	
	public void clearAll(){
		oSQLiteDB.execSQL("DROP TABLE IF EXISTS barcodememo");
		oSQLiteDB.execSQL("CREATE TABLE barcodememo (_id INTEGER PRIMARY KEY, data TEXT);");
		oSQLiteDB.execSQL("INSERT INTO barcodememo (_id, data) VALUES (0, \"\");");
		currentIdx = 0;
	}
}
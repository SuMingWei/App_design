package com.example.app_design;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private Context nowContext;
    private final String uid = "ID";
    private final String account = "Account";
    private final String password = "Password";

    private static final String databaseName = "LocalDB";
    private static final int databaseVersion = 1;

    private final String tableName = "AccountList";

    private final String createTableSQL = "CREATE TABLE IF NOT EXISTS " + this.tableName
            + " ( " + this.uid + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + this.account + " VARCHAR(255),"
            + this.password + " VARCHAR(255) ) ;";

    private final String deleteTableSQL = "DROP TABLE IF EXISTS " + this.tableName + ";";

    public DBHelper(Context context){
        super(context,databaseName,null,databaseVersion);
        this.nowContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(this.createTableSQL);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(this.deleteTableSQL);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertToLocalDB(String account,String password){
        SQLiteDatabase myLocalDB = this.getReadableDatabase() ;

        ContentValues contentValues = new ContentValues();
        contentValues.put(this.account,account);
        contentValues.put(this.password,password);

        long nowID = myLocalDB.insert(this.tableName,null,contentValues);

        final String successLog = "新增成功！\n\n編號 ： " + nowID + "\n帳號 ： " + account + "\n密碼 ： " + password;
        Toast.makeText(this.nowContext,successLog,Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ContactInfo> getTotalContactInfo(){
        ArrayList<ContactInfo> totalContactInfo = new ArrayList<ContactInfo>();

        SQLiteDatabase myLocalDB = this.getReadableDatabase();
        String[] myColumn = {this.uid,this.account,this.password};

        Cursor myCursor = myLocalDB.query(this.tableName,myColumn,null,null,null,null,uid);

        while(myCursor.moveToNext()){
            int id = myCursor.getInt(myCursor.getColumnIndex(this.uid));
            String name = myCursor.getString(myCursor.getColumnIndex(this.account));
            String phone = myCursor.getString(myCursor.getColumnIndex(this.password));

            ContactInfo eachContactInfo = new ContactInfo();
            eachContactInfo.init(id,name,phone);

            totalContactInfo.add(eachContactInfo);
        }

        return totalContactInfo;
    }


}

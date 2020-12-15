package com.example.app_design;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Register extends AppCompatActivity {
    private EditText account, password;
    private Button confirm, login_btn;
    private TextView message;

    private TextToSpeech talk_object;

    ArrayList<ArrayList<String>> accountInfo = new ArrayList<ArrayList<String>>();
    String path = "/storage/emulated/0/DCIM/accountList.txt";

    private ArrayList<ContactInfo> contactList = new ArrayList<ContactInfo>();
    private ArrayList<ArrayList<String>> accountList = new ArrayList<ArrayList<String>>();
    private DBHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findObject();
        buttonClickEvent();

        //checkPermissions();
        //getStorageInfo();

        setAccountList();

    }

    public void findObject(){
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.register_btn);
        login_btn = findViewById(R.id.login_btn);
        message = findViewById(R.id.null_box);
    }

    // local storage
    public void checkPermissions(){
        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Register.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        int recordPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if(recordPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Register.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
    }

    public void getStorageInfo(){
        try {
            FileInputStream fis = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            ArrayList<String> tempList;
            String lineText = "";
            while((lineText=reader.readLine())!= null){
                tempList = new ArrayList<String>();
                tempList.add(lineText.split(" ")[0]);
                tempList.add(lineText.split(" ")[1]);
                accountInfo.add(tempList);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeStorageInfo(){
        try{
            FileOutputStream fos = new FileOutputStream(path);
            for(int i=0;i<accountInfo.size();i++){
                fos.write(accountInfo.get(i).get(0).getBytes());
                fos.write(" ".getBytes());
                fos.write(accountInfo.get(i).get(1).getBytes());
                fos.write("\n".getBytes());

            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkAccount(String user_account){
        for(int i=0;i<accountInfo.size();i++){
            if(user_account.equals(accountInfo.get(i).get(0))){
                return true;
            }
        }
        return false;
    }

    public void register(){
        String user_account = account.getText().toString();
        String user_password = password.getText().toString();
        if(checkAccount(user_account)){
            Toast.makeText(Register.this,"帳號已被註冊",Toast.LENGTH_SHORT).show();
        }else{
            // add new account
            ArrayList<String> newAccount = new ArrayList<String>();
            newAccount.add(user_account);
            newAccount.add(user_password);
            accountInfo.add(newAccount);
            writeStorageInfo();
            // voice API
            talk_object = initTalkObject();
            botSayChinese("註冊成功");
        }
    }

    // 中文語音合成
    public TextToSpeech initTalkObject(){
        talk_object = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    // 調整語調
                    talk_object.setPitch((float)1.0);
                    // 調整語速
                    talk_object.setSpeechRate((float)2.0);
                    // 地區
                    Locale locale = Locale.TAIWAN;
                    if(talk_object.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE){
                        talk_object.setLanguage(locale);
                    }
                }
            }
        });
        return talk_object;
    }

    public void botSayChinese(final  String s){
        Toast.makeText(Register.this,s,Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        // (要做什麼事，delay的豪秒數)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                talk_object.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                while(talk_object.isSpeaking()){}
            }
        },500);
    }

    // database
    public void setAccountList(){
        myDBHelper = new DBHelper(Register.this);
        contactList.addAll(myDBHelper.getTotalContactInfo());
        accountList = this.getTotalDataSource();
    }

    public ArrayList<ArrayList<String>> getTotalDataSource(){
        ArrayList<ArrayList<String>> totalAccountData = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempList;

        for(int index=0; index < this.contactList.size(); index++){
            ContactInfo eachPersonContactInfo = this.contactList.get(index);
            tempList = new ArrayList<String>();
            tempList.add(eachPersonContactInfo.getUserAccount());
            tempList.add(eachPersonContactInfo.getUserPassword());

            totalAccountData.add(tempList);
        }

        return totalAccountData;
    }

    public boolean checkAccount_db(String user_account){
        for(int i=0;i<accountList.size();i++){
            if(user_account.equals(accountList.get(i).get(0))){
                return true;
            }
        }
        return false;
    }

    public void register_db(){
        String user_account = account.getText().toString();
        String user_password = password.getText().toString();

        if(user_account.length() != 0 && user_password.length() != 0){
            if(checkAccount_db(user_account)){
                Toast.makeText(Register.this,"帳號已被註冊",Toast.LENGTH_SHORT).show();
            }else{
                // add new account
                ArrayList<String> newAccount = new ArrayList<String>();
                newAccount.add(user_account);
                newAccount.add(user_password);
                accountList.add(newAccount);

                this.myDBHelper.insertToLocalDB(user_account,user_password);
            }
        }else {
            Toast.makeText(Register.this,"您的資料不完整，請重新再試",Toast.LENGTH_SHORT).show();
        }

    }

    public void buttonClickEvent(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // register();
                register_db();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
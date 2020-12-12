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

    ArrayList<ArrayList<String>> accountInfo = new ArrayList<ArrayList<String>>();
    String path = "/storage/emulated/0/DCIM/accountList.txt";
    private TextToSpeech talk_object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findObject();
        checkPermissions();
        getStorageInfo();
        buttonClickEvent();
    }

    public void findObject(){
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.register_btn);
        login_btn = findViewById(R.id.login_btn);
        message = findViewById(R.id.null_box);
    }

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

    public void buttonClickEvent(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
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
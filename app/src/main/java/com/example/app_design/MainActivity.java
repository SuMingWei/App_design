package com.example.app_design;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText account, password;
    private Button confirm, register_btn;
    private TextView message;

    ArrayList<ArrayList<String>> accountInfo = new ArrayList<ArrayList<String>>();

    String path = "/storage/emulated/0/DCIM/accountList.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findObject();
        checkPermissions();
        buttonClickEvent();
    }

    public void findObject(){
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        message = findViewById(R.id.null_box);
    }

    public void checkPermissions(){
        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
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

    public boolean identify(String user_account,String user_password){
        for(int i=0;i<accountInfo.size();i++){
            if(user_account.equals(accountInfo.get(i).get(0)) && user_password.equals(accountInfo.get(i).get(1))){
                return true;
            }
        }
        return false;
    }

    public void login(){
        String user_account = account.getText().toString();
        String user_password = password.getText().toString();

        if(user_account.equals("") || user_password.equals("")){
            Toast.makeText(MainActivity.this,"請輸入完整資料",Toast.LENGTH_SHORT).show();
        }else {
            if(identify(user_account,user_password)){
                Intent intent = new Intent();
                intent.putExtra("user_account",user_account);
                intent.setClass(MainActivity.this,Home.class);
                startActivity(intent);
            }else{
                Toast.makeText(MainActivity.this,"帳號或密碼錯誤",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void buttonClickEvent(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStorageInfo();
                login();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });
    }
}
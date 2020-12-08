package com.example.app_design;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {
    private EditText account, password;
    private Button confirm;

    private String[] account_list = new String[3];
    private String[] password_list = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findObject();
        setValidUser();
        login();
    }

    public void findObject(){
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.login_btn);
    }

    public void setValidUser(){
        account_list[0] = "guest1";
        account_list[1] = "guest2";
        account_list[2] = "guest3";
        password_list[0] = "abc1234";
        password_list[1] = "areu8787";
        password_list[2] = "love6969";
    }

    public boolean identify(String user_account, String user_password){
        for(int i=0;i<3;i++){
            if(user_account.equals(account_list[i]) && user_password.equals(password_list[i])){
                return true;
            }
        }
        return false;
    }

    public void login(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account_string = account.getText().toString();
                String password_string = password.getText().toString();

                if(account_string.equals("") || password_string.equals("")){
                    Toast.makeText(MainActivity.this,"請輸入完整資料",Toast.LENGTH_SHORT).show();
                }else {
                    if(identify(account_string,password_string)){
                        Intent intent = new Intent();
                        intent.putExtra("user_account",account_string);
                        intent.setClass(MainActivity.this,Home.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this,"帳號或密碼錯誤",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
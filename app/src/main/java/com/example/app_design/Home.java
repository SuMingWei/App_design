package com.example.app_design;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {
    private TextView login_account,chinese_tv,taiwanese_tv;
    private Button back_btn,chinese_btn,taiwanese_btn;

    String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findObject();
        setLoginInfo();
        backToLogin();
    }

    public void findObject(){
        login_account = findViewById(R.id.account_tv);
        back_btn = findViewById(R.id.back_btn);
        chinese_tv = findViewById(R.id.chinese_tv);
        taiwanese_tv = findViewById(R.id.taiwanese_tv);
        chinese_btn = findViewById(R.id.chinese_btn);
        taiwanese_btn = findViewById(R.id.taiwanese_btn);
    }

    public void setLoginInfo(){
        account = getIntent().getStringExtra("user_account");
        login_account.append(String.valueOf(account));
    }

    public void backToLogin(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
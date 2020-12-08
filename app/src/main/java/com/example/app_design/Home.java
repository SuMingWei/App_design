package com.example.app_design;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {
    private TextView login_account;
    private Button back_btn;

    String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        login_account = findViewById(R.id.account_tv);
        back_btn = findViewById(R.id.back_btn);

        setLoginInfo();
        backToLogin();
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
package com.example.app_design;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AccountInfo extends AppCompatActivity {
    private TextView login_account;
    private Button back_btn;
    private ListView person_list;

    private String account;

    private ArrayList<ContactInfo> contactList = new ArrayList<ContactInfo>();
    private ArrayList<String> accountList = new ArrayList<String>();
    private ArrayAdapter listAdapter;
    private DBHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        findObject();
        setLoginInfo();
        setAccountList();
        buttonClickEvent();
    }

    public void findObject(){
        login_account = findViewById(R.id.account_tv);
        back_btn = findViewById(R.id.back_btn);
        person_list = findViewById(R.id.person_list);
    }

    public void setLoginInfo(){
        account = getIntent().getStringExtra("user_account");
        login_account.append(String.valueOf(account));
    }

    public void setAccountList(){
        myDBHelper = new DBHelper(AccountInfo.this);
        contactList.addAll(myDBHelper.getTotalContactInfo());
        accountList = this.getTotalDataSource();

        this.listAdapter = new ArrayAdapter(AccountInfo.this, android.R.layout.simple_list_item_1,this.accountList);
        person_list.setAdapter(this.listAdapter);
    }

    public ArrayList<String> getTotalDataSource(){
        ArrayList<String> totalListViewData = new ArrayList<String>();

        for(int index=0; index < this.contactList.size(); index++){
            ContactInfo eachPersonContactInfo = this.contactList.get(index);

            String eachListViewData = "[" + eachPersonContactInfo.getUserId() + "]" +
                    "帳號 : " + eachPersonContactInfo.getUserAccount() + " " +
                    "密碼 : " + eachPersonContactInfo.getUserPassword();

            totalListViewData.add(eachListViewData);
        }

        return totalListViewData;
    }

    public void buttonClickEvent() {
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
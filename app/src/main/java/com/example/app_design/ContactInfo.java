package com.example.app_design;

public class ContactInfo {
    private int uid;
    private String account;
    private String password;

    public void init(int id,String account,String password){
        this.uid = id;
        this.account = account;
        this.password = password;
    }

    public int getUserId(){
        return this.uid;
    }

    public String getUserAccount(){
        return this.account;
    }

    public String getUserPassword(){
        return this.password;
    }
}

package com.example.sandy.securitymanagement.model;

import org.json.JSONObject;

/**
 * Created by sandy on 27/11/17.
 */

public class LoginUser
{
    String username, password,id;


    public LoginUser(String username, String password, String id)
    {
        this.username = username;
        this.password = password;
        this.id=id;
    }

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }
    public String getId()
    {
        return id;
    }

}
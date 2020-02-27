package com.example.tubes.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void set(String usename,String password) {
        prefs.edit().putString("usename", usename).apply();
        prefs.edit().putString("password", password).apply();
    }

    public String getusename() {
        return prefs.getString("usename","");
    }

    public String getpassword() {
        return prefs.getString("password","");
    }
}

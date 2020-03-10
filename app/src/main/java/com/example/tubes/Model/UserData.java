package com.example.tubes.Model;

public class UserData {
    private String mUsername;
    private String mName;

    public UserData(String mUsername, String mName) {
        this.mUsername=mUsername;
        this.mName = mName;
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }
}

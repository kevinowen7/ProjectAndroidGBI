package com.example.tubes.Model;

public class UserData {
    private String mUsername;
    private String mName;
    private String mUrl;

    public UserData(String mUsername, String mName,String image_url) {
        this.mUsername=mUsername;
        this.mName = mName;
        this.mUrl = image_url;
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

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}

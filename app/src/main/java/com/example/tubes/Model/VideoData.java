package com.example.tubes.Model;

public class VideoData {
    private String mTitle;
    private String mDate;
    private String mUrl;

    public VideoData(String mTitle,String mDate,String mUrl){
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mUrl = mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}

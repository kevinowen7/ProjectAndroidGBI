package com.example.tubes.Model;

import android.media.MediaPlayer;

public class PodcastData {
    private String mTitle;
    private String mUrl;
    private int isPlay=-1;
    private MediaPlayer mPlayer=null;

    public PodcastData(String mTitle,String mUrl){
        this.mTitle = mTitle;
        this.mUrl = mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public MediaPlayer getPlayer() {
        return mPlayer;
    }

    public void setPlayer(MediaPlayer mPlayer) {
        this.mPlayer = mPlayer;
    }

    public int getIsPlay() {
        return isPlay;
    }

    public void setIsPlay(int isPlay) {
        this.isPlay = isPlay;
    }
}

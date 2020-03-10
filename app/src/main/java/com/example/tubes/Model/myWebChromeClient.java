package com.example.tubes.Model;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.example.tubes.R;

public class myWebChromeClient extends WebChromeClient {
    private Bitmap mDefaultVideoPoster;
    private View mVideoProgressView;

    private View mCustomView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private WebView videoItem;
    private Context mContext;
    private Activity mAcitivity;

    public myWebChromeClient(WebView videoItem, Context mContext, Activity mAcitivity) {
        this.videoItem = videoItem;
        this.mContext = mContext;
        this.mAcitivity = mAcitivity;
    }



    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        onShowCustomView(view, callback);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onShowCustomView(View view,CustomViewCallback callback) {

        mAcitivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        customViewContainer = mAcitivity.findViewById(R.id.customViewContainer);
        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }
        mCustomView = view;
        videoItem.setVisibility(View.GONE);
        customViewContainer.setVisibility(View.VISIBLE);
        customViewContainer.addView(view);
        customViewCallback = callback;
    }

    @Override
    public void onHideCustomView() {
        mAcitivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
        if (mCustomView == null)
            return;
        videoItem.setVisibility(View.VISIBLE);
        customViewContainer.setVisibility(View.GONE);

        // Hide the custom view.
        mCustomView.setVisibility(View.GONE);

        // Remove the custom view from its container.
        customViewContainer.removeView(mCustomView);
        customViewCallback.onCustomViewHidden();

        mCustomView = null;
    }
}

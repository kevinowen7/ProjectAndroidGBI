package com.example.tubes.Model;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class myWebViewClient extends WebViewClient {
    private LinearLayout loadItemProgress;

    public myWebViewClient(LinearLayout loadItem) {
        this.loadItemProgress = loadItem;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        loadItemProgress.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        loadItemProgress.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        super.onPageFinished(view, url);
    }
}

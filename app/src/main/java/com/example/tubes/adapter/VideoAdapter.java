package com.example.tubes.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tubes.Model.VideoData;
import com.example.tubes.Model.myWebChromeClient;
import com.example.tubes.Model.myWebViewClient;
import com.example.tubes.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public ArrayList<VideoData> mItemList;

    private Context mContext;


    WebView videoItem;
    private myWebChromeClient mWebChromeClient;
    private myWebViewClient mWebViewClient;
    private Activity mActiviy;


    public void addContext(Context context){
        mContext = context;
    }


    public void addActivity(Activity videoActivity) {
        this.mActiviy = videoActivity;
    }

    public VideoAdapter(ArrayList<VideoData> itemList) {

        mItemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_video, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView titleItem;
        TextView dateItem;
        LinearLayout loadItem;

        @SuppressLint("SetJavaScriptEnabled")
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            titleItem = itemView.findViewById(R.id.titleVideo);
            dateItem = itemView.findViewById(R.id.dateVideo);
            videoItem = itemView.findViewById(R.id.videoView);
            loadItem = itemView.findViewById(R.id.itemLoad);

            mWebViewClient = new myWebViewClient(loadItem);
            videoItem.setWebViewClient(mWebViewClient);

            mWebChromeClient = new myWebChromeClient(videoItem,mContext,mActiviy);
            videoItem.setWebChromeClient(mWebChromeClient);

            videoItem.getSettings().setJavaScriptEnabled(true);
            videoItem.getSettings().setAppCacheEnabled(true);
            videoItem.getSettings().setBuiltInZoomControls(true);
            videoItem.getSettings().setSaveFormData(true);


        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressLoad;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressLoad = itemView.findViewById(R.id.progressLoad);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(final ItemViewHolder viewHolder, int position) {

        String title = mItemList.get(position).getTitle();
        String date = mItemList.get(position).getDate();
        String url = mItemList.get(position).getUrl();

        viewHolder.titleItem.setText(title);
        viewHolder.dateItem.setText(date);
        videoItem.loadUrl(url);
    }



}

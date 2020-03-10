package com.example.tubes.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tubes.Model.PodcastData;
import com.example.tubes.R;
import com.example.tubes.activity.SplashScreenActivity;
import com.example.tubes.activity.VideoActivity;
import com.example.tubes.activity.WelcomeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PodcastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public ArrayList<PodcastData> mItemList;

    private Context mContext;
    private Activity mActiviy;


    public void addContext(Context context){
        mContext = context;
    }


    public void addActivity(Activity videoActivity) {
        this.mActiviy = videoActivity;
    }

    public PodcastAdapter(ArrayList<PodcastData> itemList) {

        mItemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_podcast, parent, false);
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
        Button buttonPlay;
        Button buttonPause;
        ProgressBar progressList;

        @SuppressLint("SetJavaScriptEnabled")
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            titleItem = itemView.findViewById(R.id.list_title_podcast);
            buttonPlay = itemView.findViewById(R.id.buttonPlay);
            buttonPause = itemView.findViewById(R.id.buttonPause);
            progressList = itemView.findViewById(R.id.progressList);

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

    private void populateItemRows(final ItemViewHolder viewHolder, final int position) {
        viewHolder.buttonPlay.setVisibility(View.GONE);
        viewHolder.buttonPause.setVisibility(View.GONE);
        viewHolder.progressList.setVisibility(View.VISIBLE);

        String title = mItemList.get(position).getTitle();
        String url = mItemList.get(position).getUrl();
        final MediaPlayer mp = new MediaPlayer();
        if (mItemList.get(position).getPlayer()==null) {
            mItemList.get(position).setPlayer(mp);

            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mp.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mp.prepare();
                        mActiviy.runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 viewHolder.progressList.setVisibility(View.GONE);
                                 viewHolder.buttonPlay.setVisibility(View.VISIBLE);

                                 if (mItemList.get(position).getIsPlay()==1){
                                     viewHolder.buttonPlay.setVisibility(View.GONE);
                                     viewHolder.buttonPause.setVisibility(View.VISIBLE);
                                 } else {
                                     viewHolder.buttonPlay.setVisibility(View.VISIBLE);
                                     viewHolder.buttonPause.setVisibility(View.GONE);
                                 }
                             }
                         });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            if (mItemList.get(position).getIsPlay()==1){
                viewHolder.progressList.setVisibility(View.GONE);
                viewHolder.buttonPlay.setVisibility(View.GONE);
                viewHolder.buttonPause.setVisibility(View.VISIBLE);
            } else {
                viewHolder.progressList.setVisibility(View.GONE);
                viewHolder.buttonPlay.setVisibility(View.VISIBLE);
                viewHolder.buttonPause.setVisibility(View.GONE);
            }
        }

        viewHolder.titleItem.setText(title);

        Log.d("populate", "populateItemRows: "+url);

        try{


            viewHolder.buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.buttonPlay.setVisibility(View.GONE);
                    viewHolder.buttonPause.setVisibility(View.VISIBLE);
                    mItemList.get(position).setIsPlay(1);
                    mItemList.get(position).getPlayer().start();
                }
            });
            viewHolder.buttonPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.buttonPlay.setVisibility(View.VISIBLE);
                    viewHolder.buttonPause.setVisibility(View.GONE);
                    mItemList.get(position).setIsPlay(0);
                    mItemList.get(position).getPlayer().pause();
                }
            });
        }catch(Exception e){
            Log.d("errorPlay", "populateItemRows: "+e.toString());
            e.printStackTrace();
        }


    }





}

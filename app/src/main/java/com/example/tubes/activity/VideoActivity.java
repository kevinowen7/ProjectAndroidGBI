package com.example.tubes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;

import com.example.tubes.Model.VideoData;
import com.example.tubes.R;
import com.example.tubes.adapter.MyHistoryAdapter;
import com.example.tubes.adapter.VideoAdapter;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    VideoAdapter adapter;
    ArrayList<VideoData> videoArrayList = new ArrayList<>();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initUI();
    }

    private void initUI() {
        populateData();
        recycleVideoListener();
        initScrollListener();
    }

    private void populateData() {
        int i = 0;
        while (i < 10) {
            videoArrayList.add(new VideoData("Title 1","title 2","https://www.youtube.com/embed/uhQ7mh_o_cM"));
            i++;
        }
    }

    private void recycleVideoListener() {
        recyclerView = (RecyclerView) findViewById(R.id.video_recycle);
        adapter = new VideoAdapter(videoArrayList);
        adapter.addContext(VideoActivity.this);
        adapter.addActivity(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == videoArrayList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore() {
        videoArrayList.add(null);
        adapter.notifyItemInserted(videoArrayList.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoArrayList.remove(videoArrayList.size() - 1);
                int scrollPosition = videoArrayList.size();
                adapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    videoArrayList.add(new VideoData("title 1","test23","<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/uhQ7mh_o_cM\" frameborder=\"0\" allowFullScreen></iframe>"));
                    currentSize++;
                }

                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);


    }
}

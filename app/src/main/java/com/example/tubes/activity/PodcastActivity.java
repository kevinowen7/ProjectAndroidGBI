package com.example.tubes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.tubes.Callback.ShowPodcastCallback;
import com.example.tubes.Callback.ShowVideoCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.Model.ConvertDate;
import com.example.tubes.Model.PodcastData;
import com.example.tubes.Model.VideoData;
import com.example.tubes.R;
import com.example.tubes.adapter.PodcastAdapter;
import com.example.tubes.adapter.VideoAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PodcastActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    PodcastAdapter adapter;
    ArrayList<PodcastData> podCastArrayList = new ArrayList<>();
    boolean isLoading = false;

    private ImageView mLoading;
    private ProgressBar mProgress;
    private ImageView mBack;
    private EditText mSearch;

    private int limit_bottom_new;
    private int limit_top_new;
    private int page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);
        initUI();
    }

    private void initUI() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mBack = findViewById(R.id.back_button);
        mSearch = findViewById(R.id.search_podcast);


        showLoading();
        populateData();

        //listener button
        backListener();

        //searchListener
        searchListener();
    }

    private void populateData() {
        final String limit_bottom="0";
        final String limit_top="10";
        new Thread(new Runnable() {
            @Override
            public void run() {

                //call http API untuk register
                final ArrayList<HashMap<String, String>> reqPodcast = RequestLoadPodcast(limit_bottom,limit_top,mSearch.getText().toString());

                Log.d("dataReq",reqPodcast.toString());

                if (Objects.equals(reqPodcast.get(0).get("success"), "1")){
                    //get data

                    for (int i=1;i<reqPodcast.size();i++){
                        podCastArrayList.add(new PodcastData(reqPodcast.get(i).get("judul_podcast"),reqPodcast.get(i).get("link_podcast")));
                    }

                    PodcastActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //recycle view listerner
                            recycleVideoListener();

                            initScrollListener();
                            //hide loading
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqPodcast.get(0).get("success"), "-1")){
                    //koneksi error
                    PodcastActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(reqPodcast.get(0).get("message"),"Error",PodcastActivity.this);
                            hideLoading();
                        }
                    });
                } else {
                    PodcastActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(reqPodcast.get(0).get("message"),"Error",PodcastActivity.this);
                            hideLoading();
                        }
                    });
                }
            }
        }).start();
    }

    private void searchData() {
        final String limit_bottom="0";
        final String limit_top="10";
        new Thread(new Runnable() {
            @Override
            public void run() {
                //call http API untuk register
                final ArrayList<HashMap<String, String>> reqPodcast = RequestLoadPodcast(limit_bottom,limit_top,mSearch.getText().toString());

                Log.d("dataReq",reqPodcast.toString());

                if (Objects.equals(reqPodcast.get(0).get("success"), "1")){
                    //get data

                    for (int i=1;i<reqPodcast.size();i++){
                        podCastArrayList.add(new PodcastData(reqPodcast.get(i).get("judul_podcast"),reqPodcast.get(i).get("link_podcast")));
                    }

                    PodcastActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //recycle view listerner
                            adapter.notifyDataSetChanged();
                            //hide loading
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqPodcast.get(0).get("success"), "-1")){
                    //koneksi error
                    PodcastActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(reqPodcast.get(0).get("message"),"Error",PodcastActivity.this);
                            hideLoading();
                        }
                    });
                } else {
                    PodcastActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(reqPodcast.get(0).get("message"),"Error",PodcastActivity.this);
                            hideLoading();
                        }
                    });
                }
            }
        }).start();
    }

    private void searchListener() {
        mSearch.addTextChangedListener(new TextWatcher() {
            private Timer timer=new Timer();
            private final long DELAY = 1000; // milliseconds

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                podCastArrayList.clear();
                                searchData();
                            }
                        },
                        DELAY
                );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadMore() {
        podCastArrayList.add(null);
        adapter.notifyItemInserted(podCastArrayList.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    podCastArrayList.remove(podCastArrayList.size() - 1);
                    int scrollPosition = podCastArrayList.size();
                    adapter.notifyItemRemoved(scrollPosition);
                    int currentSize = scrollPosition;

                    limit_bottom_new = 10 * page;
                    limit_top_new = 10;
                    //call http API untuk load more
                    final ArrayList<HashMap<String, String>> reqPodcast = RequestLoadPodcast(String.valueOf(limit_bottom_new), String.valueOf(limit_top_new),mSearch.getText().toString());

                    Log.d("videoList", reqPodcast.toString());

                    if (Objects.equals(reqPodcast.get(0).get("success"), "1") && reqPodcast.size() > 1) {

                        for (int i = 1; i < reqPodcast.size(); i++) {
                            podCastArrayList.add(new PodcastData(reqPodcast.get(i).get("judul_podcast"), reqPodcast.get(i).get("link_podcast")));
                        }

                        PodcastActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                page = page + 1;
                                //recycle view listerner
                                adapter.notifyDataSetChanged();
                                isLoading = false;
                            }
                        });
                    } else if (Objects.equals(reqPodcast.get(0).get("success"), "1")) {
                        //koneksi error
                        PodcastActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isLoading = false;
                            }
                        });
                    } else if (Objects.equals(reqPodcast.get(0).get("success"), "-1")) {
                        //koneksi error
                        PodcastActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertCustom.showDialog(reqPodcast.get(0).get("message"), "Error", PodcastActivity.this);
                                isLoading = false;
                            }
                        });
                    } else {
                        PodcastActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertCustom.showDialog(reqPodcast.get(0).get("message"), "Error", PodcastActivity.this);
                                isLoading = false;
                            }
                        });
                    }
                } catch (Exception e){
                    System.out.println(e.toString());
                };
            }
        }, 2000);

    }

    private ArrayList<HashMap<String, String>> RequestLoadPodcast(String limit_bottom, String limit_top, String title) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        ShowPodcastCallback login_req = new ShowPodcastCallback(PodcastActivity.this);
        //Log.d("CEKIDBOOK",id_book);
        try {
            arrayList = login_req.execute(
                    limit_bottom
                    , limit_top
                    , title
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
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
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == podCastArrayList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }


    private void recycleVideoListener() {
        recyclerView = (RecyclerView) findViewById(R.id.video_recycle);
        adapter = new PodcastAdapter(podCastArrayList);
        adapter.addContext(PodcastActivity.this);
        adapter.addActivity(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PodcastActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void showLoading(){
        //show loading
        mProgress.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        //hide loading
        mProgress.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
    }

    private void backListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

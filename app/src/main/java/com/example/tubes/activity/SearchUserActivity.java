package com.example.tubes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.tubes.Callback.ShowUserCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.Model.PodcastData;
import com.example.tubes.Model.UserData;
import com.example.tubes.R;
import com.example.tubes.adapter.PodcastAdapter;
import com.example.tubes.adapter.SearchUserAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SearchUserActivity extends AppCompatActivity implements SearchUserAdapter.OnDetailListener{
    private RecyclerView recyclerView;
    SearchUserAdapter adapter;
    ArrayList<UserData> jemaatArrayList = new ArrayList<>();
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
        setContentView(R.layout.activity_search_user);

        initUI();
    }

    private void initUI() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mBack = findViewById(R.id.back_button);
        mSearch = findViewById(R.id.search_jemaat);


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
                final ArrayList<HashMap<String, String>> reqUser = RequestLoadUser(limit_bottom,limit_top,mSearch.getText().toString());

                Log.d("dataReq",reqUser.toString());

                if (Objects.equals(reqUser.get(0).get("success"), "1")){
                    //get data

                    for (int i=1;i<reqUser.size();i++){
                        jemaatArrayList.add(new UserData(reqUser.get(i).get("username"),reqUser.get(i).get("name"),reqUser.get(i).get("image_url")));
                    }

                    SearchUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //recycle view listerner
                            recycleVideoListener();

                            initScrollListener();
                            //hide loading
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqUser.get(0).get("success"), "-1")){
                    //koneksi error
                    SearchUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(reqUser.get(0).get("message"),"Error",SearchUserActivity.this);
                            hideLoading();
                        }
                    });
                } else {
                    SearchUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(reqUser.get(0).get("message"),"Error",SearchUserActivity.this);
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
                final ArrayList<HashMap<String, String>> reqUser = RequestLoadUser(limit_bottom,limit_top,mSearch.getText().toString());

                Log.d("dataReq",reqUser.toString());

                if (Objects.equals(reqUser.get(0).get("success"), "1")){
                    //get data

                    for (int i=1;i<reqUser.size();i++){
                        jemaatArrayList.add(new UserData(reqUser.get(i).get("username"),reqUser.get(i).get("name"),reqUser.get(i).get("image_url")));
                    }

                    SearchUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //recycle view listerner
                            adapter.notifyDataSetChanged();
                            //hide loading
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqUser.get(0).get("success"), "-1")){
                    //koneksi error
                    SearchUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(reqUser.get(0).get("message"),"Error",SearchUserActivity.this);
                            hideLoading();
                        }
                    });
                } else {
                    SearchUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(reqUser.get(0).get("message"),"Error",SearchUserActivity.this);
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
                                jemaatArrayList.clear();
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
        jemaatArrayList.add(null);
        adapter.notifyItemInserted(jemaatArrayList.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    jemaatArrayList.remove(jemaatArrayList.size() - 1);
                    int scrollPosition = jemaatArrayList.size();
                    adapter.notifyItemRemoved(scrollPosition);
                    int currentSize = scrollPosition;

                    limit_bottom_new = 10 * page;
                    limit_top_new = 10;
                    //call http API untuk load more
                    final ArrayList<HashMap<String, String>> reqUser = RequestLoadUser(String.valueOf(limit_bottom_new), String.valueOf(limit_top_new),mSearch.getText().toString());

                    Log.d("videoList", reqUser.toString());

                    if (Objects.equals(reqUser.get(0).get("success"), "1") && reqUser.size() > 1) {

                        for (int i = 1; i < reqUser.size(); i++) {
                            jemaatArrayList.add(new UserData(reqUser.get(i).get("username"), reqUser.get(i).get("name"), reqUser.get(i).get("image_url")));
                        }

                        SearchUserActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                page = page + 1;
                                //recycle view listerner
                                adapter.notifyDataSetChanged();
                                isLoading = false;
                            }
                        });
                    } else if (Objects.equals(reqUser.get(0).get("success"), "1")) {
                        //koneksi error
                        SearchUserActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isLoading = false;
                            }
                        });
                    } else if (Objects.equals(reqUser.get(0).get("success"), "-1")) {
                        //koneksi error
                        SearchUserActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertCustom.showDialog(reqUser.get(0).get("message"), "Error", SearchUserActivity.this);
                                isLoading = false;
                            }
                        });
                    } else {
                        SearchUserActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertCustom.showDialog(reqUser.get(0).get("message"), "Error", SearchUserActivity.this);
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

    private ArrayList<HashMap<String, String>> RequestLoadUser(String limit_bottom, String limit_top, String title) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        ShowUserCallback login_req = new ShowUserCallback(SearchUserActivity.this);
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
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == jemaatArrayList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }


    private void recycleVideoListener() {
        recyclerView = (RecyclerView) findViewById(R.id.jemaat_recycle);
        adapter = new SearchUserAdapter(jemaatArrayList,this);
        adapter.addContext(SearchUserActivity.this);
        adapter.addActivity(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchUserActivity.this);
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

    @Override
    public void onDetailClick(int position) {
        jemaatArrayList.get(position);
        Intent intent = new Intent(this,);
    }
}

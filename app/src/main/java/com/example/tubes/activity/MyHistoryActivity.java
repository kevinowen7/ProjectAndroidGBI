package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tubes.Callback.LoginUserCallback;
import com.example.tubes.Callback.ShowPersembahanCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.Model.MyHistory;
import com.example.tubes.R;
import com.example.tubes.adapter.MyHistoryAdapter;
import com.example.tubes.fragment.DatePickerFragment;
import com.example.tubes.fragment.DatePickerMYFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MyHistoryActivity extends AppCompatActivity {
    private ImageView mLoading;
    private ProgressBar mProgress;
    private RecyclerView recyclerView;
    private MyHistoryAdapter adapter;
    private ArrayList<MyHistory> historyArrayList;
    private ImageView mBack;
    private SharedPreferences sp;
    private TextView mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);

        initUI();
    }

    private void initUI() {
        //init
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mBack = findViewById(R.id.back_button);
        mDate = findViewById(R.id.date_history);

        //add data + recycle listener
        addDataHistory();

        //listener button
        backListener();

        //listener date
        datePickerListener();

    }

    private void datePickerListener() {
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerMYFragment newFragment = new DatePickerMYFragment();
                newFragment.show(getFragmentManager(),"Date Picker");
            }
        });
    }

    private void recycleHistoryListener() {
        recyclerView = (RecyclerView) findViewById(R.id.history_recycle);
        adapter = new MyHistoryAdapter(historyArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyHistoryActivity.this);
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

    private ArrayList<HashMap<String, String>> RequestShowPersembahan(String username) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        ShowPersembahanCallback login_req = new ShowPersembahanCallback(MyHistoryActivity.this);
        //Log.d("CEKIDBOOK",id_book);
        try {
            arrayList = login_req.execute(
                    username
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private void backListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void addDataHistory() {
        showLoading();
        historyArrayList = new ArrayList<>();

        sp = getSharedPreferences("login",MODE_PRIVATE);
        final String shUser = sp.getString("username", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //call http API untuk register
                ArrayList<HashMap<String, String>> reqShow = RequestShowPersembahan(shUser);


                if (Objects.equals(reqShow.get(0).get("success"), "1")) {
                    for (int i=1;i<reqShow.size();i++){
                        //get data
                        historyArrayList.add(new MyHistory(reqShow.get(i).get("tanggal"),reqShow.get(i).get("jenis_persembahan"),Integer.parseInt(Objects.requireNonNull(reqShow.get(i).get("jumlah_persembahan")))));
                    }

                    MyHistoryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //recycle view listerner
                            recycleHistoryListener();
                            //hide loading
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqShow.get(0).get("success"), "-1")) {
                    //Internal error
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqShow;
                    MyHistoryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"),MyHistoryActivity.this);
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqShow.get(0).get("success"), "0")) {
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqShow;
                    MyHistoryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"),MyHistoryActivity.this);
                            hideLoading();
                        }
                    });
                }
            }
        }).start();
    }
}

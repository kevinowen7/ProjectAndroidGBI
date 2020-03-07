package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.tubes.Model.MyHistory;
import com.example.tubes.R;
import com.example.tubes.adapter.MyHistoryAdapter;

import java.util.ArrayList;

public class MyHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyHistoryAdapter adapter;
    private ArrayList<MyHistory> historyArrayList;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);

        initUI();
    }

    private void initUI() {
        //init
        mBack = findViewById(R.id.back_button);
        //add data
        addDataHistory();

        //recycle view
        recyclerView = (RecyclerView) findViewById(R.id.history_recycle);
        adapter = new MyHistoryAdapter(historyArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyHistoryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //listener button
        backListener();

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
        historyArrayList = new ArrayList<>();
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agust-2020", "Persembahan", 100000));
    }
}

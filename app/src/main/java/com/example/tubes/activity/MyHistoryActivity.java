package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.tubes.Model.MyHistory;
import com.example.tubes.R;
import com.example.tubes.adapter.MyHistoryAdapter;

import java.util.ArrayList;

public class MyHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyHistoryAdapter adapter;
    private ArrayList<MyHistory> historyArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);

        initUI();
    }

    private void initUI() {
        addDataHistory();

        recyclerView = (RecyclerView) findViewById(R.id.history_recycle);
        adapter = new MyHistoryAdapter(historyArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyHistoryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void addDataHistory() {
        historyArrayList = new ArrayList<>();
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
        historyArrayList.add(new MyHistory("03-Agustus-2020", "Persembahan", 100000));
    }
}

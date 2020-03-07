package com.example.tubes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tubes.Model.MyHistory;
import com.example.tubes.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.MyHistoryViewHolder>{

    private ArrayList<MyHistory> dataList;

    public MyHistoryAdapter(ArrayList<MyHistory> dataList) {
        this.dataList = dataList;
    }

    @Override
    public MyHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_my_history, parent, false);
        return new MyHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHistoryViewHolder holder, int position) {
        holder.txtTanggal.setText(dataList.get(position).getTanggal());
        holder.txtJenis.setText(dataList.get(position).getJenis());
        holder.txtJumlah.setText(String.valueOf(dataList.get(position).getJumlah()));
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class MyHistoryViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTanggal, txtJenis, txtJumlah;

        public MyHistoryViewHolder(View itemView) {
            super(itemView);
            txtTanggal = (TextView) itemView.findViewById(R.id.list_history_date);
            txtJenis = (TextView) itemView.findViewById(R.id.list_history_jenis);
            txtJumlah = (TextView) itemView.findViewById(R.id.list_history_jumlah);
        }
    }
}

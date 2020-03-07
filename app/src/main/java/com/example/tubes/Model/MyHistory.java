package com.example.tubes.Model;

import android.content.Intent;

public class MyHistory {
    private String tanggal;
    private String jenis;
    private int jumlah;

    public MyHistory(String tanggal,String jenis, int jumlah){
        this.tanggal = tanggal;
        this.jenis = jenis;
        this.jumlah = jumlah;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}

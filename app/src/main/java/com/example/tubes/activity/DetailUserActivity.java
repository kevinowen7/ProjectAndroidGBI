package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tubes.Callback.EditUserCallBack;
import com.example.tubes.Callback.GetUserDataCallBack;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DetailUserActivity extends AppCompatActivity {
    private ImageView mLoading;
    private ProgressBar mProgress;
    private ImageView mBack;

    private TextView mNama;
    private TextView mEmail;
    private TextView mNo_hp;
    private TextView mUsername;
    private TextView mBirthdate;
    private TextView mAlamat;
    private TextView mPekerjaan;
    private ImageView mProfileImg;

    String namaData , emailData , hpData , usernameData , birthData , alamatData , pekerjaanData , profileImgData ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);


        Intent intent = getIntent();
        usernameData = intent.getStringExtra("username");
        initUI();
    }

    private void initUI() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);

        mNama = findViewById(R.id.nameAccount);
        mEmail = findViewById(R.id.emailAccount);
        mNo_hp = findViewById(R.id.contactAccount);
        mUsername = findViewById(R.id.usernameAccount);
        mBirthdate = findViewById(R.id.birthAccount);
        mAlamat = findViewById(R.id.alamatAccount);
        mPekerjaan = findViewById(R.id.pekerjaanAccount);
        mProfileImg = findViewById(R.id.profileImg);
        mBack = findViewById(R.id.back_button);

        pullDataFromServer();
        backListener();
    }

    private void pullDataFromServer() {
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {

                //call http API untuk register
                final ArrayList<HashMap<String, String>> reqGetUserData = RequestGetUserData(usernameData);

                Log.d("HasilAkhir", reqGetUserData.toString());

                if (Objects.equals(reqGetUserData.get(0).get("success"), "1")) {
                    hideLoading();
                    DetailUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DetailUserActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //get data
                                    namaData = reqGetUserData.get(1).get("nama");
                                    emailData = reqGetUserData.get(1).get("email");
                                    hpData = reqGetUserData.get(1).get("no_hp");
                                    usernameData = reqGetUserData.get(1).get("username");
                                    birthData = reqGetUserData.get(1).get("tgl_lahir");
                                    alamatData = reqGetUserData.get(1).get("alamat");
                                    pekerjaanData = reqGetUserData.get(1).get("pekerjaan");
                                    profileImgData = reqGetUserData.get(1).get("image_url");

                                    if (usernameData!="") {
                                        //fill data
                                        mNama.setText(namaData);
                                        mEmail.setText(emailData);
                                        mNo_hp.setText(hpData);
                                        mUsername.setText(usernameData);
                                        mBirthdate.setText(birthData);
                                        mAlamat.setText(alamatData);
                                        mPekerjaan.setText(pekerjaanData);

                                        //show data
                                        mNama.setVisibility(View.VISIBLE);
                                        mEmail.setVisibility(View.VISIBLE);
                                        mNo_hp.setVisibility(View.VISIBLE);
                                        mUsername.setVisibility(View.VISIBLE);
                                        mBirthdate.setVisibility(View.VISIBLE);
                                        mAlamat.setVisibility(View.VISIBLE);
                                        mPekerjaan.setVisibility(View.VISIBLE);
                                        mProfileImg.setVisibility(View.VISIBLE);
                                        //Change Picture Profile if exist
                                        if (!profileImgData.equals("null")){
                                            Picasso.get().load(getString(R.string.WEB_SERVER)+profileImgData).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(mProfileImg);
                                        }
                                    } else {
                                        mNama.setVisibility(View.INVISIBLE);
                                        mEmail.setVisibility(View.INVISIBLE);
                                        mNo_hp.setVisibility(View.INVISIBLE);
                                        mUsername.setVisibility(View.INVISIBLE);
                                        mAlamat.setVisibility(View.INVISIBLE);
                                        mBirthdate.setVisibility(View.INVISIBLE);
                                        mPekerjaan.setVisibility(View.INVISIBLE);
                                        mProfileImg.setVisibility(View.INVISIBLE);
                                    }

                                    hideLoading();
                                }
                            });
                        }
                    });
                } else if (Objects.equals(reqGetUserData.get(0).get("success"), "-1")) {
                    //Internal error
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqGetUserData;
                    DetailUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", DetailUserActivity.this);
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqGetUserData.get(0).get("success"), "0")) {
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqGetUserData;
                    DetailUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", DetailUserActivity.this);
                            hideLoading();
                        }
                    });
                }
            }
        }).start();
    }

    private void backListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

    private ArrayList<HashMap<String, String>> RequestGetUserData(String username) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        GetUserDataCallBack update_book = new GetUserDataCallBack(DetailUserActivity.this);
        try {
            arrayList = update_book.execute(
                    username
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

}

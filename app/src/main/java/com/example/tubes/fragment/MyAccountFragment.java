package com.example.tubes.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tubes.Callback.LoginUserCallback;
import com.example.tubes.R;
import com.example.tubes.activity.MainActivity;
import com.example.tubes.activity.WelcomeActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {
    private ProgressBar mProgress;

    private Button mLogout;
    private Button mEdit;
    private TextView mNama;
    private TextView mEmail;
    private TextView mNo_hp;
    private TextView mUsername;
    private TextView mBirthdate;
    private TextView mAlamat;
    private TextView mPekerjaan;
    private ImageView mProfileImg;

    private SharedPreferences sp;
    String namaData , emailData , hpData , usernameData , birthData , alamatData , pekerjaanData , profileImgData ="";

    public MyAccountFragment(String namaData , String emailData , String hpData ,String usernameData ,String birthData , String alamatData , String pekerjaanData, String profileImgData) {
        this.namaData = namaData;
        this.emailData = emailData;
        this.hpData = hpData;
        this.usernameData = usernameData;
        this.birthData = birthData;
        this.alamatData = alamatData;
        this.pekerjaanData = pekerjaanData;
        this.profileImgData = profileImgData;

    }

    public static MyAccountFragment newInstance(String namaData , String emailData ,String hpData, String usernameData ,String birthData , String alamatData , String pekerjaanData, String profileImgData){
        MyAccountFragment frg = new MyAccountFragment(namaData,emailData,hpData,usernameData,birthData,alamatData,pekerjaanData,profileImgData);

        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initUI();
    }

    private void initUI() {
        sp = getActivity().getSharedPreferences("login",MODE_PRIVATE);
        mLogout = Objects.requireNonNull(getView()).findViewById(R.id.logout);
        mEdit = Objects.requireNonNull(getView()).findViewById(R.id.editProfile);
        mProgress = Objects.requireNonNull(getView()).findViewById(R.id.progressBar);
        mNama = Objects.requireNonNull(getView()).findViewById(R.id.nameAccount);
        mEmail = Objects.requireNonNull(getView()).findViewById(R.id.emailAccount);
        mNo_hp = Objects.requireNonNull(getView()).findViewById(R.id.contactAccount);
        mUsername = Objects.requireNonNull(getView()).findViewById(R.id.usernameAccount);
        mBirthdate = Objects.requireNonNull(getView()).findViewById(R.id.birthAccount);
        mAlamat = Objects.requireNonNull(getView()).findViewById(R.id.alamatAccount);
        mPekerjaan = Objects.requireNonNull(getView()).findViewById(R.id.pekerjaanAccount);
        mProfileImg = Objects.requireNonNull(getView()).findViewById(R.id.profileImg);

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

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().clear().apply();
                startActivity(new Intent(getActivity(), WelcomeActivity.class));
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void validateLogin() {
        sp = Objects.requireNonNull(this.getActivity()).getSharedPreferences("login",MODE_PRIVATE);
        String shUser = sp.getString("username", "");
        String shPass = sp.getString("password", "");

        //call http API untuk register
        ArrayList<HashMap<String, String>> reqLogin = null;
        reqLogin = RequestLoginUser(shUser,shPass);

        Log.d("HasilAkhir",reqLogin.toString());

        if (Objects.equals(reqLogin.get(0).get("success"), "1") && reqLogin.size()>1){
            //get data
            String namaData = reqLogin.get(1).get("nama");
            String emailData = reqLogin.get(1).get("email");
            String no_hpData = reqLogin.get(1).get("no_hp");
            String usernameData = reqLogin.get(1).get("username");
            String birthData = reqLogin.get(1).get("tgl_lahir");
            String alamatData = reqLogin.get(1).get("alamat");
            String pekerjaanData = reqLogin.get(1).get("pekerjaan");

            //fill data
            mNama.setText(namaData);
            mEmail.setText(emailData);
            mNo_hp.setText(no_hpData);
            mUsername.setText(usernameData);
            mBirthdate.setText(birthData);
            mAlamat.setText(alamatData);
            mPekerjaan.setText(pekerjaanData);

            //show data
            mLogout.setVisibility(View.VISIBLE);
            mNama.setVisibility(View.VISIBLE);
            mEmail.setVisibility(View.VISIBLE);
            mNo_hp.setVisibility(View.VISIBLE);
            mUsername.setVisibility(View.VISIBLE);
            mBirthdate.setVisibility(View.VISIBLE);
            mAlamat.setVisibility(View.VISIBLE);
            mPekerjaan.setVisibility(View.VISIBLE);
            mProfileImg.setVisibility(View.VISIBLE);

            //hide loading
            mProgress.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(getContext(), "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show();
            //hide loading
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<HashMap<String, String>> RequestLoginUser(String username, String pass) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        String ret = "";

        Location driver = new Location("");
        LatLng latLng =null;

        LoginUserCallback login_req = new LoginUserCallback(getContext());
        //Log.d("CEKIDBOOK",id_book);
        try {
            arrayList = login_req.execute(
                    username
                    , pass
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

}

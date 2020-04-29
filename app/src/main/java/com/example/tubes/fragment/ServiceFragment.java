package com.example.tubes.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tubes.R;
import com.example.tubes.activity.ActivityBaptis;
import com.example.tubes.activity.CellActivity;
import com.example.tubes.activity.PelayanActivity;
import com.example.tubes.activity.SearchUserActivity;
import com.example.tubes.activity.VideoActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends Fragment {
    private ImageView mCariJemaat,mPelayan,mBaptis,mCel;
    private String mUsername;

    public ServiceFragment(String mUsername) {
        // Required empty public constructor
        this.mUsername = mUsername;
    }

    public static ServiceFragment newInstance(String mUsername){
        ServiceFragment frg = new ServiceFragment(mUsername);
        return frg;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI();

    }

    private void initUI() {
        mCariJemaat = getView().findViewById(R.id.menu_cari_jemaat);
        mPelayan = getView().findViewById(R.id.menu_pelayanan);
        mBaptis = getView().findViewById(R.id.menu_baptisan);
        mCel = getView().findViewById(R.id.menu_sel_group);

        onClickCariJemaat();
        onClickPelayan();
        onClickBaptis();
        onClickCel();
    }

    private void onClickCel() {
        mCel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CellActivity.class);
                intent.putExtra("username",mUsername);
                startActivity(intent);
            }
        });
    }

    private void onClickBaptis() {
        mBaptis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityBaptis.class);
                intent.putExtra("username",mUsername);
                startActivity(intent);
            }
        });
    }

    private void onClickCariJemaat() {
        mCariJemaat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onClickPelayan() {
        mPelayan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PelayanActivity.class);
                intent.putExtra("username",mUsername);
                startActivity(intent);
            }
        });
    }

}

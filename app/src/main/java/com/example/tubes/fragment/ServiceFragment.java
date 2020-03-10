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
import com.example.tubes.activity.SearchUserActivity;
import com.example.tubes.activity.VideoActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends Fragment {
    private ImageView mCariJemaat;

    public ServiceFragment() {
        // Required empty public constructor
    }

    public static ServiceFragment newInstance(){
        ServiceFragment frg = new ServiceFragment();
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

        onClickCariJemaat();
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

}

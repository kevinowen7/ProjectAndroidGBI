package com.example.tubes.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tubes.R;
import com.example.tubes.activity.PodcastActivity;
import com.example.tubes.activity.VideoActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RelativeLayout mMainContainer;
    private static final String TAG = "HomeFragment";
    private String namaData="";
    private TextView mNama;
    private ImageView mVideo,mPodcast,mImageHome;

    public HomeFragment(String namaData) {
        // Required empty public constructor
        this.namaData = namaData;
    }

    public static HomeFragment newInstance(String namaData){
        HomeFragment frg = new HomeFragment(namaData);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI();

    }

    private static String capitaliseFirstLetter(String name){
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void initUI() {
        mNama = Objects.requireNonNull(getView()).findViewById(R.id.text_welcome);
        mNama.setText("Hi "+capitaliseFirstLetter(namaData)+",");

        mVideo = getView().findViewById(R.id.menu_video);
        mPodcast = getView().findViewById(R.id.menu_podcast);
        mImageHome = getView().findViewById(R.id.imageCarousel);
        Picasso.get().load(getString(R.string.WEB_SERVER)+"home.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).into(mImageHome);

        onClickVideo();
        onClickPodcast();
    }

    private void onClickPodcast() {
        mPodcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PodcastActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onClickVideo() {
        mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                startActivity(intent);
            }
        });
    }
}

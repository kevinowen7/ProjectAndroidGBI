package com.example.tubes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tubes.Callback.LoginUserCallback;
import com.example.tubes.fragment.CalendarFragment;
import com.example.tubes.R;
import com.example.tubes.fragment.GivingFragment;
import com.example.tubes.fragment.HomeFragment;
import com.example.tubes.fragment.MyAccountFragment;
import com.example.tubes.fragment.ServiceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ImageView mLoading;
    private ProgressBar mProgress;

    private FrameLayout mFrameLayout;
    private ConstraintLayout mainContainer;
    private Fragment mSelected;
    private ActionBar mActionBar;

    private boolean doubleBackToExitPressedOnce = false;

    String mNama , mEmail , mHp , mUsername , mBirthdate , mAlamat , mPekerjaan , mProfileImg ="";

    private SharedPreferences sp;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            return showFragment(menuItem.getItemId());
        }
    };

    private boolean showFragment(int itemId) {
        switch (itemId){
            case R.id.home:
                mFrameLayout.setVisibility(View.VISIBLE);
                mSelected = HomeFragment.newInstance(mNama);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,mSelected).commit();
                mainContainer.setBackgroundResource(R.drawable.bg_home);
                // atur ulang toolbar tittle
                mActionBar.setTitle(R.string.home);
                return true;
            case R.id.calendar:
                mFrameLayout.setVisibility(View.VISIBLE);
                mSelected = CalendarFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,mSelected).commit();
                mainContainer.setBackgroundResource(R.drawable.bg_home);
                mActionBar.setTitle(R.string.calendar);
                return true;
            case R.id.service:
                mFrameLayout.setVisibility(View.VISIBLE);
                mSelected = ServiceFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,mSelected).commit();
                mainContainer.setBackgroundResource(R.drawable.bg_home);
                mActionBar.setTitle(R.string.service);
                return true;
            case R.id.giving:
                mFrameLayout.setVisibility(View.VISIBLE);
                mSelected = GivingFragment.newInstance(mUsername);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,mSelected).commit();
                mainContainer.setBackgroundResource(R.drawable.bg_home);
                mActionBar.setTitle(R.string.giving);
                return true;
            case R.id.my_account:
                mFrameLayout.setVisibility(View.VISIBLE);
                mSelected = MyAccountFragment.newInstance(mNama,mEmail,mHp,mUsername,mBirthdate,mAlamat,mPekerjaan,mProfileImg);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,mSelected).commit();
                mainContainer.setBackgroundResource(R.drawable.bg_my_account);
                mActionBar.setTitle(R.string.my_account);
                return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContainer = findViewById(R.id.mainContainer);
        mFrameLayout = findViewById(R.id.frame_container);

        //validasi sharedPreferences login
        validateLogin();

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            mActionBar = getSupportActionBar();
        }


        //listener Frame Layout
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(mOnNav);
    }

    private void validateLogin() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);

        mProgress.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        final String shUser = sp.getString("username", "");
        final String shPass = sp.getString("password", "");


        new Thread(new Runnable() {
            @Override
            public void run() {
                //call http API untuk register
                ArrayList<HashMap<String, String>> reqLogin = RequestLoginUser(shUser,shPass);

                Log.d("Logchecker",reqLogin.toString());

                if (Objects.equals(reqLogin.get(0).get("success"), "1") && reqLogin.size()>1){
                    //get data
                    mNama = reqLogin.get(1).get("nama");
                    mEmail = reqLogin.get(1).get("email");
                    mHp = reqLogin.get(1).get("no_hp");
                    mUsername = reqLogin.get(1).get("username");
                    mBirthdate = reqLogin.get(1).get("tgl_lahir");
                    mAlamat = reqLogin.get(1).get("alamat");
                    mPekerjaan = reqLogin.get(1).get("pekerjaan");

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        //hide loading
                        mProgress.setVisibility(View.INVISIBLE);
                        mLoading.setVisibility(View.INVISIBLE);
                        showFragment(R.id.home);
                        }
                    });
                } else if (Objects.equals(reqLogin.get(0).get("success"), "-1")){
                    //koneksi error
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        Toast.makeText(MainActivity.this, "Pastikan Anda Terhubung ke Internet", Toast.LENGTH_SHORT).show();
                        //hide reqLogin
                        mProgress.setVisibility(View.INVISIBLE);
                        mLoading.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sp.edit().clear().apply();
                            Toast.makeText(MainActivity.this, "Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
                                }
                            },2000);
                        }
                    });
                }
            }
        }).start();

    }

    private ArrayList<HashMap<String, String>> RequestLoginUser(String username, String pass) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        LoginUserCallback login_req = new LoginUserCallback(MainActivity.this);
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik back dua kali untuk keluar dari aplikasi", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}

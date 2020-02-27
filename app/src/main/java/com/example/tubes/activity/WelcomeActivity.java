package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tubes.R;

public class WelcomeActivity extends AppCompatActivity {
    private Button mLogin;
    private Button mRegister;
    private boolean doubleBackToExitPressedOnce = false;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        //check apakah sudah pernah login
        Log.d("logCheck", String.valueOf(sp.getBoolean("logged",false)));
        if(sp.getBoolean("logged",false)){
            goToMainActivity();
        }
        
        initUI();

    }

    private void goToMainActivity() {
        Intent i = new Intent(WelcomeActivity.this,MainActivity.class);
        startActivity(i);
    }

    private void initUI() {
        mLogin = findViewById(R.id.login);
        mRegister = findViewById(R.id.register);

        moveToLogin();
        moveToRegister();
    }

    private void moveToLogin() {
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: validasi login

                //pindah ke login
                Intent mvLogin = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(mvLogin);
            }
        });
    }

    private void moveToRegister() {
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: validasi Register

                //pindah ke register
                Intent mvRegister = new Intent(WelcomeActivity.this,RegisterActivity.class);
                startActivity(mvRegister);
            }
        });
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

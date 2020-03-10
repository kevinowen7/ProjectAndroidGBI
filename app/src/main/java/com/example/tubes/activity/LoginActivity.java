package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tubes.Callback.LoginUserCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private ImageView mLoading;
    private ProgressBar mProgress;
    private Button mLogin;
    private TextView mRegis;

    private EditText mUsername;
    private EditText mPassword;
    private TextView mForgot;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
    }



    private void initUI() {
        mLogin = findViewById(R.id.login);
        mRegis = findViewById(R.id.register_here);
        mForgot = findViewById(R.id.forgot);

        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        sp = getSharedPreferences("login",MODE_PRIVATE);

        //listener
        buttonLogin();
        buttonRegis();
        buttonForgot();
    }

    private void buttonForgot() {
        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    private void buttonRegis() {
        mRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    private void showLoading(){
        //show loading
        mLogin.setEnabled(false);
        mProgress.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mLogin.setEnabled(true);
        //hide loading
        mProgress.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
    }

    private void buttonLogin() {
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //parse form
                final String mUsernameData = mUsername.getText().toString();
                final String mPassData = mPassword.getText().toString();

                //validasi form
                if (mUsernameData.length() == 0) {
                    mUsername.setError("Masukan Username!");
                } else if (mPassData.length() == 0) {
                    mUsername.setError(null);
                    mPassword.setError("Masukan Password!");
                } else {
                    mPassword.setError(null);
                    showLoading();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //call http API untuk register
                            ArrayList<HashMap<String, String>> reqLogin = RequestLoginUser(mUsernameData, mPassData);

                            Log.d("HasilAkhir", reqLogin.toString());

                            if (Objects.equals(reqLogin.get(0).get("success"), "1") && reqLogin.size() > 1) {
                                //update shared pre
                                sp.edit().putBoolean("logged", true).apply();
                                sp.edit().putString("username", mUsernameData).apply();
                                sp.edit().putString("password", mPassData).apply();
                                // pindah halaman ke main activity (login berhasil)
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else if (Objects.equals(reqLogin.get(0).get("success"), "-1")) {
                                //koneksi error
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqLogin;
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error", LoginActivity.this);
                                        hideLoading();
                                    }
                                });
                            } else if (Objects.equals(reqLogin.get(0).get("success"), "0")) {
                                //form kosong
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqLogin;
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error", LoginActivity.this);
                                        hideLoading();
                                    }
                                });
                            } else {
                                //username or pass error
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog("Username atau Password Salah", "Error", LoginActivity.this);
                                        hideLoading();
                                    }
                                });
                            }
                        }

                    }).start();
                }

            }
        });
    }


    private ArrayList<HashMap<String, String>> RequestLoginUser(String username, String pass) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        LoginUserCallback login_req = new LoginUserCallback(LoginActivity.this);

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

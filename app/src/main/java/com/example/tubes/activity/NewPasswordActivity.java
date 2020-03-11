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

import com.example.tubes.Callback.NewPassCallback;
import com.example.tubes.Callback.VerifCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class NewPasswordActivity extends AppCompatActivity {
    private EditText mPass;
    private EditText mRePass;
    private ImageView mLoading;
    private ProgressBar mProgress;
    private Button mConfirm;
    private SharedPreferences sp;
    private ImageView mBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        initUI();
    }

    private void initUI() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mPass = findViewById(R.id.password);
        mRePass = findViewById(R.id.re_password);
        mConfirm = findViewById(R.id.confirmNew);
        mBack = findViewById(R.id.back_button);

        Intent intent = getIntent();
        sp = getSharedPreferences("login",MODE_PRIVATE);

        confirmListener(intent.getStringExtra("username"));
        //listener button
        backListener();
    }

    private void backListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void confirmListener(final String username) {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mPassString = mPass.getText().toString();
                final String mRePassString = mRePass.getText().toString();
                //validasi form

                if( mPassString.length() == 0 ) {
                    mPass.setError("Isi Kolom ini!");
                } else if (mPassString.length() <6 ){
                    mRePass.setError("Password kurang kuat!");
                } else if (!mPassString.equals(mRePassString)){
                    mRePass.setError("Password tidak sama!");
                } else {
                    showLoading();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //call http API untuk register
                            ArrayList<HashMap<String, String>> reqRegis = RequestNewPass(username,mPassString);

                            Log.d("HasilAkhir", reqRegis.toString());

                            if (Objects.equals(reqRegis.get(0).get("success"), "1")) {
                                //update shared pre
                                sp.edit().putBoolean("logged", true).apply();
                                sp.edit().putString("username", username).apply();
                                sp.edit().putString("password", mPassString).apply();
                                NewPasswordActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoading();
                                    }
                                });
                                startActivity(new Intent(NewPasswordActivity.this, MainActivity.class));
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "0")) {
                                //Internal error
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                NewPasswordActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",NewPasswordActivity.this);
                                        hideLoading();
                                    }
                                });
                            } else  {
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                NewPasswordActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",NewPasswordActivity.this);
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

    private ArrayList<HashMap<String, String>> RequestNewPass(String username, String pass) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        NewPassCallback update_book = new NewPassCallback(NewPasswordActivity.this);
        try {
            arrayList = update_book.execute(
                    username
                    , pass
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
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
}

package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.tubes.Callback.ForgotCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ForgotActivity extends AppCompatActivity {
    private EditText mData;
    private Button mConfirm;
    private ImageView mLoading;
    private ProgressBar mProgress;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        initUI();
    }

    private void initUI() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mData = findViewById(R.id.username_or_mail);
        mConfirm = findViewById(R.id.verif);
        mBack = findViewById(R.id.back_button);

        confirmListener();
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

    private void confirmListener() {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mDataString = mData.getText().toString();
                //validasi form
                if( mDataString.length() == 0 ) {
                    mData.setError("Isi Kolom ini!");
                } else if (!isValidEmail(mDataString)){
                    mData.setError("Email tidak Valid");
                } else {
                    showLoading();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //call http API untuk register
                            ArrayList<HashMap<String, String>> reqRegis = RequestForgot(mDataString);

                            Log.d("HasilAkhir", reqRegis.toString());

                            if (Objects.equals(reqRegis.get(0).get("success"), "1")) {
                                //update shared pre
                                ForgotActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoading();
                                        // pindah halaman ke main activity (login berhasil)
                                        startActivity(new Intent(ForgotActivity.this, VerificationActivity.class).putExtra("email",mDataString));
                                    }
                                });
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "-1")) {
                                //Internal error
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                ForgotActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",ForgotActivity.this);
                                        hideLoading();
                                    }
                                });
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "0")) {
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                ForgotActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",ForgotActivity.this);
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

    private ArrayList<HashMap<String, String>> RequestForgot(String data) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        ForgotCallback update_book = new ForgotCallback(ForgotActivity.this);
        try {
            arrayList = update_book.execute(
                    data
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}

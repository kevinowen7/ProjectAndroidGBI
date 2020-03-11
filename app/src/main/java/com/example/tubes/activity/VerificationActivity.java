package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tubes.Callback.ForgotCallback;
import com.example.tubes.Callback.VerifCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class VerificationActivity extends AppCompatActivity {
    private EditText mData;
    private Button mConfirm;
    private ImageView mLoading;
    private ProgressBar mProgress;
    private ImageView mBack;
    private TextView mResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        initUI();
    }

    private void initUI() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mData = findViewById(R.id.code_verif);
        mConfirm = findViewById(R.id.verif);
        mResend = findViewById(R.id.resend);
        mBack = findViewById(R.id.back_button);
        Intent intent = getIntent();

        verifListener(intent.getStringExtra("email"));
        resendListener(intent.getStringExtra("email"));
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
    private void resendListener(final String email) {
        mResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //call http API untuk register
                        ArrayList<HashMap<String, String>> reqRegis = RequestForgot(email);

                        Log.d("HasilAkhir", reqRegis.toString());

                        if (Objects.equals(reqRegis.get(0).get("success"), "1")) {
                            VerificationActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog("Check Your Email","Notification",VerificationActivity.this);
                                    hideLoading();
                                }
                            });
                        } else if (Objects.equals(reqRegis.get(0).get("success"), "-1")) {
                            //Internal error
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                            VerificationActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",VerificationActivity.this);
                                    hideLoading();
                                }
                            });
                        } else if (Objects.equals(reqRegis.get(0).get("success"), "0")) {
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                            VerificationActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",VerificationActivity.this);
                                    hideLoading();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    private ArrayList<HashMap<String, String>> RequestForgot(String data) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        ForgotCallback update_book = new ForgotCallback(VerificationActivity.this);
        try {
            arrayList = update_book.execute(
                    data
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private void verifListener(final String email) {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mDataString = mData.getText().toString();
                //validasi form
                if( mDataString.length() == 0 ) {
                    mData.setError("Isi Kolom ini!");
                } else {
                    showLoading();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //call http API untuk register
                            final ArrayList<HashMap<String, String>> reqRegis = RequestVerif(mDataString,email);

                            Log.d("HasilAkhir", reqRegis.toString());

                            if (Objects.equals(reqRegis.get(0).get("success"), "1")&& reqRegis.size() > 1) {
                                VerificationActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoading();
                                        startActivity(new Intent(VerificationActivity.this, NewPasswordActivity.class).putExtra("username",reqRegis.get(1).get("username")));
                                    }
                                });

                            } else if (Objects.equals(reqRegis.get(0).get("success"), "1")) {
                                //Internal error
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                VerificationActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog("Kode Verif Salah , atau sudah Expired. Silahkan Coba Lagi","Error",VerificationActivity.this);
                                        hideLoading();
                                    }
                                });
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "0")) {
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                VerificationActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",VerificationActivity.this);
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

    private ArrayList<HashMap<String, String>> RequestVerif(String data,String email) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        VerifCallback update_book = new VerifCallback(VerificationActivity.this);
        try {
            arrayList = update_book.execute(
                    data
                    , email
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

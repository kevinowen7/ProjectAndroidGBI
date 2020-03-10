package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tubes.Callback.EditUserCallBack;
import com.example.tubes.Callback.LoginUserCallback;
import com.example.tubes.Callback.RegisterUserCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class EditUserActivity extends AppCompatActivity {
    private ImageView mLoading;
    private ProgressBar mProgress;
    private SharedPreferences sp;

    private String mNama , mEmail , mHp , mAlamat , mPekerjaan , mProfileImg ="";
    private Button mConfirm;
    private Button mCancel;
    private EditText mNameAccount,mEmailAccount,mHpAccount,mAlamatAccount,mPekerjaanAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        initUI();
        validateLogin();
    }

    private void initUI() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mConfirm = findViewById(R.id.confirm);
        mCancel = findViewById(R.id.cancel);

        mNameAccount = findViewById(R.id.nameAccount);
        mEmailAccount = findViewById(R.id.emailAccount);
        mHpAccount = findViewById(R.id.contactAccount);
        mAlamatAccount = findViewById(R.id.alamatAccount);
        mPekerjaanAccount = findViewById(R.id.pekerjaanAccount);

        buttonCancelListener();

        buttonSubmitListener();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void buttonSubmitListener() {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // parse Object dari Form ke Object String
                final String mNameData = mNameAccount.getText().toString();
                final String mMailData = mEmailAccount.getText().toString();
                final String mHpData = mHpAccount.getText().toString();
                final String mLocData = mAlamatAccount.getText().toString();
                final String mJobData = mPekerjaanAccount.getText().toString();

                //validasi form
                if( mNameData.length() == 0 ) {
                    mNameAccount.setError("Masukan Nama!");
                } else if( mMailData.length() == 0 ) {
                    mNameAccount.setError(null);
                    mEmailAccount.setError("Masukan Email!");
                } else if( !isValidEmail(mMailData)) {
                    mEmailAccount.setError("Email Tidak Valid!");
                } else if( mHpData.length() == 0 ) {
                    mEmailAccount.setError(null);
                    mHpAccount.setError("Masukan No Hp!");
                }else if( mLocData.length() == 0 ) {
                    mHpAccount.setText(null);
                    mAlamatAccount.setError("Masukan Alamat!");
                } else if( mJobData.length() == 0 ) {
                    mAlamatAccount.setError(null);
                    mPekerjaanAccount.setError("Masukan Pekerjaan!");
                } else {
                    mPekerjaanAccount.setError(null);
                    showLoading();
                    sp = getSharedPreferences("login",MODE_PRIVATE);
                    final String shUser = sp.getString("username", "");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //call http API untuk register
                            ArrayList<HashMap<String, String>> reqRegis = RequestEditUser(mNameData, mMailData, mHpData, mLocData, mJobData,shUser);

                            Log.d("HasilAkhir", reqRegis.toString());

                            if (Objects.equals(reqRegis.get(0).get("success"), "1")) {
                                // pindah halaman ke main activity (login berhasil)
                                startActivity(new Intent(EditUserActivity.this, MainActivity.class));
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "-1")) {
                                //Internal error
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                EditUserActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",EditUserActivity.this);
                                        hideLoading();
                                    }
                                });
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "0")) {
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                EditUserActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",EditUserActivity.this);
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

    private ArrayList<HashMap<String, String>> RequestEditUser(String name, String mail, String hp, String loc, String job,String user) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        EditUserCallBack update_book = new EditUserCallBack(EditUserActivity.this);
        try {
            arrayList = update_book.execute(
                    name
                    , mail
                    , hp
                    , loc
                    , job
                    , user
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private void buttonCancelListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {
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

    private void validateLogin() {
        showLoading();

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
                    mAlamat = reqLogin.get(1).get("alamat");
                    mPekerjaan = reqLogin.get(1).get("pekerjaan");

                    EditUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNameAccount.setText(mNama);
                            mEmailAccount.setText(mEmail);
                            mHpAccount.setText(mHp);
                            mAlamatAccount.setText(mAlamat);
                            mPekerjaanAccount.setText(mPekerjaan);
                            //hide loading
                            mProgress.setVisibility(View.INVISIBLE);
                            mLoading.setVisibility(View.INVISIBLE);
                        }
                    });
                } else if (Objects.equals(reqLogin.get(0).get("success"), "-1")) {
                    //koneksi error
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqLogin;
                    EditUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error", EditUserActivity.this);
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqLogin.get(0).get("success"), "0")) {
                    //form kosong
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqLogin;
                    EditUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error", EditUserActivity.this);
                            hideLoading();
                        }
                    });
                } else {
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqLogin;
                    EditUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error", EditUserActivity.this);
                            hideLoading();
                        }
                    });
                }
            }
        }).start();

    }

    private ArrayList<HashMap<String, String>> RequestLoginUser(String username, String pass) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        LoginUserCallback login_req = new LoginUserCallback(EditUserActivity.this);
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

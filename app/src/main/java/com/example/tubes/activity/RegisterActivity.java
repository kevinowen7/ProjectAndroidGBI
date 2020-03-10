package com.example.tubes.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tubes.Callback.RegisterUserCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.R;
import com.example.tubes.fragment.DatePickerFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private ImageView mLoading;
    private ProgressBar mProgress;

    private EditText mName;
    private EditText mMail;
    private EditText mHp;
    private EditText mUsername;
    private EditText mPass;
    private TextView mBirthDate;
    private EditText mLoc;
    private EditText mJob;

    private ImageView mShowPassword;
    private ImageView mHidePassword;

    private Button mRegister;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
    }

    private void initUI() {
        mName = findViewById(R.id.name);
        mMail = findViewById(R.id.email);
        mHp = findViewById(R.id.contact);
        mUsername = findViewById(R.id.username);
        mPass = findViewById(R.id.password);
        mBirthDate = findViewById(R.id.birth);
        mLoc = findViewById(R.id.alamat);
        mJob = findViewById(R.id.pekerjaan);
        mRegister = findViewById(R.id.register);

        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mShowPassword = findViewById(R.id.shPassword);
        mHidePassword = findViewById(R.id.hdPassword);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        //fragment datepicker
        birthDateListener();

        //listener show password
        buttonShowPassword();

        //listener hide password
        buttonHidePassword();

        //listener register
        buttonRegister();
    }

    private void buttonHidePassword() {
        mHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide
                mPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mPass.setTypeface(null, Typeface.BOLD);
                mShowPassword.setVisibility(View.VISIBLE);
                mHidePassword.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void buttonShowPassword() {
        mShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show
                mPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mPass.setTypeface(null, Typeface.BOLD);
                mShowPassword.setVisibility(View.INVISIBLE);
                mHidePassword.setVisibility(View.VISIBLE);
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void showLoading(){
        //show loading
        mRegister.setEnabled(false);
        mProgress.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mRegister.setEnabled(true);
        //hide loading
        mProgress.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
    }

    private void buttonRegister() {
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // parse Object dari Form ke Object String
                final String mNameData = mName.getText().toString();
                final String mMailData = mMail.getText().toString();
                final String mHpData = mHp.getText().toString();
                final String mUsernameData = mUsername.getText().toString();
                final String mPassData = mPass.getText().toString();
                final String mBirthDateData = mBirthDate.getText().toString();
                final String mLocData = mLoc.getText().toString();
                final String mJobData = mJob.getText().toString();

                //validasi form
                if( mNameData.length() == 0 ) {
                    mName.setError("Masukan Nama!");
                } else if( mMailData.length() == 0 ) {
                    mName.setError(null);
                    mMail.setError("Masukan Email!");
                } else if( !isValidEmail(mMailData)) {
                    mMail.setError("Email Tidak Valid!");
                } else if( mHpData.length() == 0 ) {
                    mMail.setError(null);
                    mHp.setError("Masukan No Hp!");
                } else if( mUsernameData.length() == 0 ) {
                    mHp.setError(null);
                    mUsername.setError("Masukan Username!");
                } else if( mPassData.length() == 0 ) {
                    mUsername.setError(null);
                    mPass.setError("Masukan Password!");
                } else if( mPassData.length() <= 8 ) {
                    mPass.setError("Password Minimal 8 Digit!");
                } else if( mBirthDateData.length() ==0 ) {
                    mPass.setError(null);
                    mBirthDate.setError("Masukan Tanggal Lahir!");
                } else if( mLocData.length() == 0 ) {
                    mBirthDate.setText(null);
                    mLoc.setError("Masukan Alamat!");
                } else if( mJobData.length() == 0 ) {
                    mLoc.setError(null);
                    mJob.setError("Masukan Pekerjaan!");
                } else {
                    mJob.setError(null);
                    showLoading();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //call http API untuk register
                            ArrayList<HashMap<String, String>> reqRegis = RequestRegisterUser(mNameData, mMailData, mHpData, mUsernameData, mPassData, mBirthDateData, mLocData, mJobData);

                            Log.d("HasilAkhir", reqRegis.toString());

                            if (Objects.equals(reqRegis.get(0).get("success"), "1")) {
                                //update shared pre
                                sp.edit().putBoolean("logged", true).apply();
                                sp.edit().putString("username", mUsernameData).apply();
                                sp.edit().putString("password", mPassData).apply();
                                // pindah halaman ke main activity (login berhasil)
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "-1")) {
                                //Internal error
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",RegisterActivity.this);
                                        hideLoading();
                                    }
                                });
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "0")) {
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",RegisterActivity.this);
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


    private ArrayList<HashMap<String, String>> RequestRegisterUser(String name, String mail, String hp, String username, String pass, String birthDate, String loc, String job) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        RegisterUserCallback update_book = new RegisterUserCallback(RegisterActivity.this);
        try {
            arrayList = update_book.execute(
                    name
                    , mail
                    , hp
                    , username
                    , pass
                    , birthDate
                    , loc
                    , job
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private void birthDateListener() {
        mBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(),"Date Picker");
            }
        });
    }

}

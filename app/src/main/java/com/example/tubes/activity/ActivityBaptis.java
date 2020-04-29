package com.example.tubes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.tubes.Callback.AddBaptisCallBack;
import com.example.tubes.Callback.AddPelayanCallBack;
import com.example.tubes.Callback.LoginUserCallback;
import com.example.tubes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ActivityBaptis extends AppCompatActivity {
    private ImageView mBack;
    private EditText mWaktu;
    private Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baptis);

        initUI();
    }

    private void initUI() {
        mWaktu = findViewById(R.id.waktu);
        mBack = findViewById(R.id.back_button);
        mSubmit = findViewById(R.id.submit);
        //listener button
        backListener();
        dropDownWaktu();
        submitListener();
    }

    private void submitListener() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //call http API untuk register
                        ArrayList<HashMap<String, String>> reqAddPersembahan = RequestAddBaptis(mWaktu.getText().toString(),getIntent().getStringExtra("username"));
                        Log.d("HasilAkhir", reqAddPersembahan.toString());

                        if (Objects.equals(reqAddPersembahan.get(0).get("success"), "1")) {
                            ActivityBaptis.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityBaptis.this, "Request anda berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (Objects.equals(reqAddPersembahan.get(0).get("success"), "-1")) {
                            //Internal error
                            final ArrayList<HashMap<String, String>> finalReqPersembahan = reqAddPersembahan;
                            ActivityBaptis.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityBaptis.this, finalReqPersembahan.get(0).get("message"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (Objects.equals(reqAddPersembahan.get(0).get("success"), "0")) {
                            final ArrayList<HashMap<String, String>> finalReqPersembahan = reqAddPersembahan;
                            ActivityBaptis.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityBaptis.this, finalReqPersembahan.get(0).get("message"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    private ArrayList<HashMap<String, String>> RequestAddBaptis(String jenis, String username) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        AddBaptisCallBack login_req = new AddBaptisCallBack(ActivityBaptis.this);
        //Log.d("CEKIDBOOK",id_book);
        try {
            arrayList = login_req.execute(
                    jenis
                    , username
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private void backListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void dropDownWaktu() {
        mWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ActivityBaptis.this ,mWaktu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_waktu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mWaktu.setText(item.getTitle());
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }
}

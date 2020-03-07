package com.example.tubes.fragment;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tubes.Callback.AddPersembahanCallback;
import com.example.tubes.Model.NumberTextWatcherForThousand;
import com.example.tubes.R;
import com.example.tubes.activity.MyHistoryActivity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class GivingFragment extends Fragment {
    private ImageView mLoading;
    private ProgressBar mProgress;
    private TextView mDate;
    private EditText mJenisPersembahan;
    private EditText mJumlahPersembahan;

    private Button mSepuluh;
    private Button mLimaPuluh;
    private Button mSubmit;
    private Button mHistory;

    private String mUsername;

    private GivingFragment(String mUsername) {
        // Required empty public constructor
        this.mUsername = mUsername;
    }

    public static GivingFragment newInstance(String mUsername){
        return new GivingFragment(mUsername);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_giving, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initUI();
    }

    private void initUI() {
        mProgress = Objects.requireNonNull(getActivity()).findViewById(R.id.progressBar);
        mLoading = getActivity().findViewById(R.id.loadingBackround);
        mJenisPersembahan = Objects.requireNonNull(getView()).findViewById(R.id.jenis_persembahan);
        mJumlahPersembahan = Objects.requireNonNull(getView()).findViewById(R.id.jmlh_persembahan);
        mSepuluh = Objects.requireNonNull(getView()).findViewById(R.id.jmlh_sepuluh);
        mLimaPuluh = Objects.requireNonNull(getView()).findViewById(R.id.jmlh_lima_puluh);
        mSubmit = Objects.requireNonNull(getView()).findViewById(R.id.submit_persembahan);
        mDate=getView().findViewById(R.id.tanggal_giving);
        mHistory = getView().findViewById(R.id.history);

        myHistoryListener();
        dropDownJenisPersembahan();
        dateListener();
        onChangeJumlahPersembahan();
        sepuluhListener();
        limaPuluhListener();
        submitListener();
    }

    private void myHistoryListener() {
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyHistoryActivity.class);
                startActivity(intent);
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

    private void submitListener() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validasi form
                if( mDate.getText().toString().length() == 0 ) {
                    mDate.setError("Masukan Tanggal Persembahan!");
                } else if( mJumlahPersembahan.getText().toString().length() == 0 ) {
                    mDate.setError(null);
                    mJumlahPersembahan.setError("Masukan Jumlah Persembahan!");
                } else if( mJenisPersembahan.getText().toString().length() == 0 ) {
                    mJumlahPersembahan.setError(null);
                    mJenisPersembahan.setError("Masukan Jenis Persembahan!");
                } else {
                    mDate.setError(null);
                    mJumlahPersembahan.setError(null);
                    mJenisPersembahan.setError(null);
                    showLoading();
                    //push data to server
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //call http API untuk register
                            ArrayList<HashMap<String, String>> reqAddPersembahan = RequestAddPersembahan(mUsername,mDate.getText().toString(),mJumlahPersembahan.getText().toString(),mJenisPersembahan.getText().toString());
                            Log.d("HasilAkhir", reqAddPersembahan.toString());

                            if (Objects.equals(reqAddPersembahan.get(0).get("success"), "1")) {
                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    Toast.makeText(getActivity(), "Persembahan anda berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                    resetForm();
                                    hideLoading();
                                    }
                                });
                            } else if (Objects.equals(reqAddPersembahan.get(0).get("success"), "-1")) {
                                //Internal error
                                final ArrayList<HashMap<String, String>> finalReqPersembahan = reqAddPersembahan;
                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    Toast.makeText(getActivity(), finalReqPersembahan.get(0).get("message"), Toast.LENGTH_SHORT).show();
                                    hideLoading();
                                    }
                                });
                            } else if (Objects.equals(reqAddPersembahan.get(0).get("success"), "0")) {
                                final ArrayList<HashMap<String, String>> finalReqPersembahan = reqAddPersembahan;
                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    Toast.makeText(getActivity(), finalReqPersembahan.get(0).get("message"), Toast.LENGTH_SHORT).show();
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

    private void resetForm() {
        mDate.setText("");
        mJumlahPersembahan.setText("");
        mJenisPersembahan.setText("");
    }

    private ArrayList<HashMap<String, String>> RequestAddPersembahan(String username, String date,String jumlah,String jenis) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        jumlah = jumlah.replaceAll("\\.", "");
        AddPersembahanCallback persembahan_req = new AddPersembahanCallback(getActivity());

        try {
            arrayList = persembahan_req.execute(
                    username
                    , date
                    , jumlah
                    , jenis
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private void limaPuluhListener() {
        mLimaPuluh.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                mJumlahPersembahan.setText("50.000");
            }
        });
    }

    private void sepuluhListener() {
        mSepuluh.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                mJumlahPersembahan.setText("10.000");
            }
        });
    }

    private void onChangeJumlahPersembahan() {
        mJumlahPersembahan.addTextChangedListener(new NumberTextWatcherForThousand(mJumlahPersembahan));
    }

    private void dateListener() {
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(Objects.requireNonNull(getActivity()).getFragmentManager(),"Date Picker");
            }
        });
    }

    private void dropDownJenisPersembahan() {
        mJenisPersembahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), mJenisPersembahan);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_persembahan, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mJenisPersembahan.setText(item.getTitle());
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }
}

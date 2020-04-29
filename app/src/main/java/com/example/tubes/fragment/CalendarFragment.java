package com.example.tubes.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.tubes.Callback.GetMonthPelayananCallback;
import com.example.tubes.Callback.GetPelayananCallback;
import com.example.tubes.Callback.LoginUserCallback;
import com.example.tubes.Model.ConvertDate;
import com.example.tubes.Model.MyHistory;
import com.example.tubes.R;
import com.example.tubes.activity.MainActivity;
import com.example.tubes.activity.WelcomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    private ImageView mLoading;
    private ProgressBar mProgress;
    private EditText mJenisPelayanan;
    private CalendarView mCalendarView;
    private TextView mTheme, mPembicara, mNamaPelayanan;
    private String mWL,mMusic,mSinger,mMultimedia,mDancer,mBanner,mChoir;
    private int mCurrentMonth;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(){
        CalendarFragment frg = new CalendarFragment();
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        try {
            initUI();
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
    }

    private void initUI() throws OutOfDateRangeException {
        mCalendarView = Objects.requireNonNull(getActivity()).findViewById(R.id.calendarItem);
        mProgress = Objects.requireNonNull(getActivity()).findViewById(R.id.progressBar);
        mLoading = getActivity().findViewById(R.id.loadingBackround);
        mTheme = getActivity().findViewById(R.id.theme);
        mPembicara = getActivity().findViewById(R.id.pembicara);
        mNamaPelayanan = getActivity().findViewById(R.id.nama_pelayan);

        mJenisPelayanan = Objects.requireNonNull(getView()).findViewById(R.id.jenis_pelayanan);

        dropDownJenisPersembahan();
        calendarListener();
    }

    private void calendarListener() throws OutOfDateRangeException {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar dateNow = Calendar.getInstance();
        dateNow.set(currentYear, currentMonth, currentDay);
        mCalendarView.setDate(calendar);
        callGetCalendarData(calendar);
        mCurrentMonth = currentMonth;
        callGetMonthCalendarData(mCurrentMonth);


        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                try {
                    mCalendarView.setDate(clickedDayCalendar);
                    callGetCalendarData(clickedDayCalendar);
                } catch (OutOfDateRangeException e) {
                    e.printStackTrace();
                }
            }
        });

        mCalendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                if (mCurrentMonth==1){
                    mCurrentMonth = 12;
                } else {
                    mCurrentMonth = mCurrentMonth - 1;
                }
                callGetMonthCalendarData(mCurrentMonth);
            }
        });

        mCalendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                if (mCurrentMonth==12){
                    mCurrentMonth = 1;
                } else {
                    mCurrentMonth = mCurrentMonth + 1;
                }
                callGetMonthCalendarData(mCurrentMonth);
            }
        });

    }

    private void callGetMonthCalendarData(int month) {
        mJenisPelayanan.setText(null);
        mNamaPelayanan.setText(null);

        List<EventDay> calendars = new ArrayList<>();
        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                //call http API untuk register
                ArrayList<HashMap<String, String>> reqLogin = RequestGetMonthPelayanan(String.valueOf(month));

                Log.d("Logchecker",reqLogin.toString());

                if (Objects.equals(reqLogin.get(0).get("success"), "1") && reqLogin.size()>1){
                    //get data
                    for (int i=1;i<reqLogin.size();i++){
                        //validasi Tanggal yang di minta
                        String[] dateCurr = Objects.requireNonNull(reqLogin.get(i).get("date")).split("-");

                        Calendar date = Calendar.getInstance();
                        date.set(Integer.parseInt(dateCurr[0]),Integer.parseInt(dateCurr[1])-1,Integer.parseInt(dateCurr[2]));
                        calendars.add(new EventDay(date, R.drawable.icon_calendar));
                    }

                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //hide loading
                            mCalendarView.setEvents(calendars);
                            hideLoading();
                        }
                    });
                } else {
                    //koneksi error
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //hide reqLogin
                            hideLoading();
                        }
                    });
                }
            }
        }).start();
    }

    private ArrayList<HashMap<String, String>> RequestGetMonthPelayanan(String month) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        GetMonthPelayananCallback login_req = new GetMonthPelayananCallback(getContext());
        //Log.d("CEKIDBOOK",id_book);
        try {
            arrayList = login_req.execute(
                    month
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }


    private void callGetCalendarData(Calendar clickedDayCalendar) {
        mJenisPelayanan.setText(null);
        mNamaPelayanan.setText(null);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = format1.format(clickedDayCalendar.getTime());
        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                //call http API untuk register
                ArrayList<HashMap<String, String>> reqLogin = RequestGetPelayanan(formatted);

                Log.d("Logchecker",reqLogin.toString());

                if (Objects.equals(reqLogin.get(0).get("success"), "1") && reqLogin.size()>1){
                    //get data
                    mTheme.setText("Theme : "+reqLogin.get(1).get("tema"));
                    mPembicara.setText("Pembicara : "+reqLogin.get(1).get("pembicara"));
                    mWL = reqLogin.get(1).get("wl");
                    mMusic = reqLogin.get(1).get("music");
                    mSinger = reqLogin.get(1).get("singer");
                    mMultimedia = reqLogin.get(1).get("multimedia");
                    mDancer = reqLogin.get(1).get("dancer");
                    mBanner = reqLogin.get(1).get("banner");
                    mChoir = reqLogin.get(1).get("choir");

                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //hide loading
                            hideLoading();
                        }
                    });
                } else {
                    //koneksi error
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTheme.setText("");
                            mPembicara.setText("");
                            mWL = "";
                            mMusic = "";
                            mSinger = "";
                            mMultimedia = "";
                            mDancer = "";
                            mBanner = "";
                            mChoir = "";
                            //hide reqLogin
                            hideLoading();
                        }
                    });
                }
            }
        }).start();
    }

    private ArrayList<HashMap<String, String>> RequestGetPelayanan(String date) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        GetPelayananCallback login_req = new GetPelayananCallback(getContext());
        //Log.d("CEKIDBOOK",id_book);
        try {
            arrayList = login_req.execute(
                    date
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private void dropDownJenisPersembahan() {
        mJenisPelayanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), mJenisPelayanan);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_pelayanan, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mNamaPelayanan.setSingleLine(false);
                        mJenisPelayanan.setText(item.getTitle());
                        if(item.getTitle().equals("WL")){
                            mNamaPelayanan.setText(mWL.replaceAll(",","\n"));
                        } else if (item.getTitle().equals("Music")){
                            mNamaPelayanan.setText(mMusic.replaceAll(",","\n"));
                        } else if (item.getTitle().equals("Singer")){
                            mNamaPelayanan.setText(mSinger.replaceAll(",","\n"));
                        } else if (item.getTitle().equals("Multimedia")){
                            mNamaPelayanan.setText(mMultimedia.replaceAll(",","\n"));
                        } else if (item.getTitle().equals("Dancer")){
                            mNamaPelayanan.setText(mDancer.replaceAll(",","\n"));
                        } else if (item.getTitle().equals("Banner")){
                            mNamaPelayanan.setText(mBanner.replaceAll(",","\n"));
                        } else if (item.getTitle().equals("Choir")){
                            mNamaPelayanan.setText(mChoir.replaceAll(",","\n"));
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
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

}

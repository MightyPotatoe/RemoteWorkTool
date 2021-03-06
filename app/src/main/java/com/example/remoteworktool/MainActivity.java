package com.example.remoteworktool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    //---******---Elementy UI ---******---
    //--- Card 1
    private TextView card1_subtitle2;
    private TextView card1_subtitle3;
    private TextView card1_body1;
    private TextView card1_body2;
    private ProgressBar card1_progressBar;
    private ProgressBar card1_progressBarBackground;
    private TextView card1_actionButton;
    private TextView card1_progress_value;

    //Klasa obslugi baz danych
    private DatabaseHelper dbHelper;

    //---Shared Preferences
    private SharedPreferences sharedPreferences;

    //---Status Aplikacji---
    private String currentAppStatus;

    //---IntentFilter---
    //Do obsługi eventow typu TimeTick
    private IntentFilter s_intentFilter = new IntentFilter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicjowanie elementów UI
        card1_subtitle2 = findViewById(R.id.ma_card1_subtitle2);
        card1_subtitle3 = findViewById(R.id.ma_card1_subtitle3);
        card1_body1 = findViewById(R.id.ma_card1_body1);
        card1_body2 = findViewById(R.id.ma_card1_body2);
        card1_progressBar = findViewById(R.id.ma_card1_progressBar);
        card1_progressBarBackground = findViewById(R.id.ma_card1_progressBarInactive);
        card1_actionButton = findViewById(R.id.ma_card1_actionButton);
        card1_progress_value = findViewById(R.id.ma_card1_progress_value);

        //---Inicjowanie obsługi eventow typu TimeTick
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(timeChangedReceiver, s_intentFilter);

        //inicjowanie db
        dbHelper = new DatabaseHelper(this);

        //---Inicjowanie SharedPreferences---
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        //---Pobranie aktualnego statusu aplikacji - DefaultValue: IDLE ---
        currentAppStatus = sharedPreferences.getString("APP_STATUS", Dictionary.STATUS_IDLE);

        //Update widoku
        updateView();
    }

    //---Broadcast Reciever - ACTION_TIME_TICK ---
    private final BroadcastReceiver timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String ACTION  = intent.getAction();
            if(ACTION != null) {
                if (ACTION.equals(Intent.ACTION_TIME_TICK)) {
                    updateView();
                }
            }
            else {
                System.out.println("---Intent.getAction() zwróciło wartość NULL");
            }
        }
    };


    //---------------------------------Obsługa Activity---------------------------
    public void updateView(){
        switch (currentAppStatus){
            case Dictionary.STATUS_IDLE:
                updateViewIdle();
                break;
        }
    }

    public void updateViewIdle(){
        //Sprawdzenie czy w dniu dzisiejszym byla rozpoczynana sesja
        String date_yyyyMMdd = Conversions.formatDateTo_yyyy_MM_dd(LocalDateTime.now());
        String startingHour = dbHelper.getStartHourOfTheDay(date_yyyyMMdd);
        String holidayName = dbHelper.getHolidayName(date_yyyyMMdd);
        if(startingHour == null){
            //Jeżeli nie, sprawdz czy dzisiaj jest dzień wolny
            final DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
            if(dayOfWeek.equals(DayOfWeek.SATURDAY) ||  dayOfWeek.equals(DayOfWeek.SUNDAY) || holidayName != null){
                String text = getResources().getString(R.string.dzienWolnyOdPracy);
                if(holidayName != null){
                    text += holidayName + ".";
                }
                else if(dayOfWeek.equals(DayOfWeek.SATURDAY)){
                    text += "sobota.";
                }
                else{
                    text += "niedziela.";
                }
                setViewCard1_holiday(text);
            }
            else{
                //jeżeli dzisiaj jest normalny dzień pracujący:
                setViewCard1_normalDay();
            }

        }
    }



    //----------------PRESETY UI----------------
    //----CARD 1 -- NORMALNY DZIEN PRACUJACY
    public void setViewCard1_normalDay(){
        //ustaw przepracowany czas na 0
        card1_subtitle2.setVisibility(View.VISIBLE);
        card1_body1.setVisibility(View.VISIBLE);
        card1_body1.setText(R.string.time0);
        //Ustaw pozostały czas pracy na 8h
        card1_subtitle3.setVisibility(View.VISIBLE);
        card1_body2.setVisibility(View.VISIBLE);
        card1_body2.setText(R.string.time8h);

        card1_progress_value.setVisibility(View.VISIBLE);
        card1_progress_value.setText(R.string.progress0);
        card1_progressBar.setProgress(0);
        card1_progressBar.setVisibility(View.VISIBLE);
        card1_progressBarBackground.setVisibility(View.VISIBLE);
        card1_actionButton.setText(R.string.rozpocznijSesje);
    }

    //----CARD 1 -- SOBOTY, NIEDZIELE, SWIETA
    public void setViewCard1_holiday(String holidayMessage){
        card1_subtitle2.setVisibility(View.VISIBLE);
        card1_subtitle2.setText(holidayMessage);
        card1_body1.setVisibility(View.INVISIBLE);
        card1_subtitle3.setVisibility(View.INVISIBLE);
        card1_body2.setVisibility(View.INVISIBLE);
        card1_progress_value.setVisibility(View.INVISIBLE);
        card1_progressBar.setVisibility(View.INVISIBLE);
        card1_progressBarBackground.setVisibility(View.INVISIBLE);
        card1_actionButton.setText(R.string.rozpocznijSesje);

    }

}
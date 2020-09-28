package com.example.remoteworktool;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;

@RunWith(AndroidJUnit4.class)
public class MainMenuCard1Tests {


    //--- Card 1
    private TextView card1_subtitle2;
    private TextView card1_subtitle3;
    private TextView card1_body1;
    private TextView card1_body2;
    private TextView card1_progress_value;
    private ProgressBar card1_progressBar;
    private ProgressBar card1_progressBarBackground;
    private TextView card1_actionButton;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mainActivity = null;

    @Before
    public void setUp(){
        mainActivity = mainActivityActivityTestRule.getActivity();
        //Inicjowanie elementów UI
        card1_subtitle2 = mainActivity.findViewById(R.id.ma_card1_subtitle2);
        card1_subtitle3 = mainActivity.findViewById(R.id.ma_card1_subtitle3);
        card1_body1 = mainActivity.findViewById(R.id.ma_card1_body1);
        card1_body2 = mainActivity.findViewById(R.id.ma_card1_body2);
        card1_progress_value = mainActivity.findViewById(R.id.ma_card1_progress_value);
        card1_progressBar = mainActivity.findViewById(R.id.ma_card1_progressBar);
        card1_progressBarBackground = mainActivity.findViewById(R.id.ma_card1_progressBarInactive);
        card1_actionButton = mainActivity.findViewById(R.id.ma_card1_actionButton);
    }

    //-----------------TEST 1------------------------------

    /**
     * 1) Uruchomienie aplikacji
     * 2) Sprawdzenie czy element został wyświetlony na ekranie
     */
    @Test
    public void uruchomienieAplikacji() {
        View view = mainActivity.findViewById(R.id.ma_card1);
        Assert.assertNotNull(view);
    }

    //-----------------TEST 2------------------------------

    /**
     * Przygotowanie:
     * 1) Usunac ewentualne swieto z tabeli 'Holidays'
     * ------------------------------------------------
     * Test:
     * 1) Test uruchamiany w dzień roboczy
     * 2) Uruchamionie aplikacji
     * 3) Sprawdzenie czy wartości są wyświetlane poprawnie:
     * ma_card1_subtitle2 = Przepracowano
     * ma_card1_body1 = 0h 00min
     * ma_card1_subtitle3 = Pozostało
     * ma_card1_body2 = 8h 00min
     * card1_progress_value = 0%
     * ma_card1_progressBar.progress = 0
     * ma_card1_actionButton = Przejdź do sesji
     * progressBardbackgroun -> VISIBLE
     */
    @Test
    public void uruchomienieAplikacji_pierwszeUruchomienie_dzienRoboczy_KartaCzasuPracy(){

        //----------PRZYGOTOWANIE TESTU------------
        //zamkniecie aplikacji
        mainActivity.finish();
        Intent intent = mainActivity.getIntent();
        //Przygotowanie bazy danych
        DatabaseHelper databaseHelper = new DatabaseHelper(mainActivity);
        String todayIs = Conversions.formatDateTo_yyyy_MM_dd(LocalDateTime.now());
        System.out.println("Dzien dzisiejszy: " + todayIs);
        databaseHelper.deleteHolidayFromHolidayTable(todayIs);

        //Restart aplikacji
        mainActivity = mainActivityActivityTestRule.launchActivity(intent);
        setUp();

        //----------TEST------------
        Assert.assertEquals(View.VISIBLE, card1_subtitle2.getVisibility());
        Assert.assertEquals(mainActivity.getResources().getString(R.string.przepracawoano), card1_subtitle2.getText());

        Assert.assertEquals(View.VISIBLE, card1_body1.getVisibility());
        Assert.assertEquals(mainActivity.getResources().getString(R.string.time0), card1_body1.getText());

        Assert.assertEquals(View.VISIBLE, card1_subtitle3.getVisibility());
        Assert.assertEquals(mainActivity.getResources().getString(R.string.pozostalo), card1_subtitle3.getText());

        Assert.assertEquals(View.VISIBLE, card1_body2.getVisibility());
        Assert.assertEquals(mainActivity.getResources().getString(R.string.time8h), card1_body2.getText());

        Assert.assertEquals(View.VISIBLE, card1_progress_value.getVisibility());
        Assert.assertEquals(mainActivity.getResources().getString(R.string.progress0), card1_progress_value.getText());

        Assert.assertEquals(View.VISIBLE, card1_progressBar.getVisibility());
        Assert.assertEquals(0, card1_progressBar.getProgress());

        Assert.assertEquals(View.VISIBLE, card1_actionButton.getVisibility());
        Assert.assertEquals(mainActivity.getResources().getString(R.string.rozpocznijSesje), card1_actionButton.getText());

        Assert.assertEquals(View.VISIBLE, card1_progressBarBackground.getVisibility());
    }

    //-----------------TEST 3------------------------------

    /**
     * Przygotowanie:
     * 1) Sprawdzenie czy dzisiaj nie wypada jakies swieto.
     * 2) Jeżeni nie => dodanie do bazy danych 'holidays' wpisu o swiecie w dniu wykonania testu
     * ------------------------------------------------
     * Test:
     * 1) Uruchamionie aplikacji
     * 2) Sprawdzenie czy wartości są wyświetlane poprawnie:
     * ma_card1_subtitle2 = Dzisiejszy dzień jest dniem wolnym od pracy. Dzisiaj jest nazwa_święta.
     * ma_card1_body1 => INVISIBLE
     * ma_card1_subtitle3 => INVISIBLE
     * ma_card1_body2 => INVISIBLE
     * card1_progress_value => INVISIBLE
     * ma_card1_progressBar.progress => INVISIBLE
     * ma_card1_actionButton = Przejdź do sesji
     * progressBardbackgroun => INVISIBLE
     */
    @Test
    public void uruchomienieAplikacji_pierwszeUruchomienie_swieto_KartaCzasuPracy(){
        //----------PRZYGOTOWANIE TESTU------------
        //zamkniecie aplikacji
        mainActivity.finish();
        Intent intent = mainActivity.getIntent();

        //Sprawdzenie czy nie ma już wpisu ze świętem w dniu dzisiejszym
        DatabaseHelper databaseHelper = new DatabaseHelper(mainActivity);
        String todayIs = Conversions.formatDateTo_yyyy_MM_dd(LocalDateTime.now());
        System.out.println("Dzien dzisiejszy: " + todayIs);

        String holidayName = databaseHelper.getHolidayName(todayIs);
        if (holidayName == null) {
            holidayName = "Swieto nad świętami";
            Assert.assertTrue(databaseHelper.insertHolidayToHolidayTable(todayIs, holidayName));
        }
        //Uruchomienie aplikacji
        mainActivity = mainActivityActivityTestRule.launchActivity(intent);
        setUp();

        //----------TEST------------
        Assert.assertEquals(View.VISIBLE, card1_subtitle2.getVisibility());
        final String HOLIDAY_STRING = mainActivity.getResources().getString(R.string.dzienWolnyOdPracy) + holidayName + ".";
        Assert.assertEquals(HOLIDAY_STRING, card1_subtitle2.getText());

        Assert.assertEquals(View.INVISIBLE, card1_body1.getVisibility());

        Assert.assertEquals(View.INVISIBLE, card1_subtitle3.getVisibility());

        Assert.assertEquals(View.INVISIBLE, card1_body2.getVisibility());

        Assert.assertEquals(View.INVISIBLE, card1_progress_value.getVisibility());

        Assert.assertEquals(View.INVISIBLE, card1_progressBar.getVisibility());

        Assert.assertEquals(View.VISIBLE, card1_actionButton.getVisibility());
        Assert.assertEquals(mainActivity.getResources().getString(R.string.rozpocznijSesje), card1_actionButton.getText());

        Assert.assertEquals(View.INVISIBLE, card1_progressBarBackground.getVisibility());
    }

    @After
    public void tearDown(){
        mainActivity = null;
    }
}

package com.example.remoteworktool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "working_hours.db"; //nazwa bazy danych
    //Tabela WorkingHours
    private static final String TABLE_NAME = "working_hours"; //nazwa tabeli w BD
    private static final String ID_COL = "ID"; //nazwa kolumny 0
    private static final String DATE_COL = "SESSION_DATE"; //nazwa kolumny 1
    private static final String START_HOUR_COL = "START_HOUR"; //nazwa kolumny 2
    private static final String END_HOUR_COL = "END_HOUR"; //nazwa kolumny 3
    private static final String TIME_WORKED_COL = "TIME_WORKED"; //nazwa kolumny 4

    //Tabela Holidays
    private static final String HOLIDAYS_TABLE_NAME = "holidays"; //nazwa tabeli w BD
    private static final String HOLIDAYS_ID_COL = "ID"; //nazwa kolumny 0
    private static final String HOLIDAYS_DATE_COL = "HOLIDAY_DATE"; //nazwa kolumny 1
    private static final String HOLIDAY_NAME = "HOLIDAY_NAME"; //nazwa kolumny 2


    /**
     * Konstruktor klasy DatabaseHelper
     *
     * @param context - context użycia
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * Tworzy tabelę o nazwie TABLE_NAME
     * Kolumny:
     * ID
     * DATE
     * START_HOUR
     * END_HOUR
     * TIME_WORKED
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_WORKING_HOURS_TABLE = "create table " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DATE_COL + " DATE, "
                + START_HOUR_COL + " DATE, "
                + END_HOUR_COL + " DATE, "
                + TIME_WORKED_COL + " INTEGER)";

        final String CREATE_HOLIDAY_TABLE = "create table " + HOLIDAYS_TABLE_NAME + " ("
                + HOLIDAYS_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HOLIDAYS_DATE_COL + " DATE, "
                + HOLIDAY_NAME + " STRING)";

        db.execSQL(CREATE_HOLIDAY_TABLE);
        db.execSQL(CREATE_WORKING_HOURS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HOLIDAYS_TABLE_NAME);
        onCreate(db);
    }


    public String getStartHourOfTheDay(String date_yyyyMMdd) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + START_HOUR_COL +
                " FROM " + TABLE_NAME + "" +
                " WHERE " + DATE_COL + " = '" + date_yyyyMMdd + "' " +
                "ORDER BY date(" + START_HOUR_COL + ") ASC LIMIT 1", null);
        if (!cursor.moveToNext()) {
            return null;
        }
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }


    //----------------HOLIDAY DB-------------------

    /**
     * Pobiera nazwę święta z dnia podanego jako paramter
     *
     * @param date_yyyyMMdd - data w formacie yyyy-MM-dd
     * @return String - nazwa święta
     */
    public String getHolidayName(String date_yyyyMMdd) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + HOLIDAY_NAME +
                " FROM " + HOLIDAYS_TABLE_NAME + "" +
                " WHERE " + HOLIDAYS_DATE_COL + " = '" + date_yyyyMMdd + "'", null);
        if (!cursor.moveToNext()) {
            return null;
        }
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    /**
     * Sprawdza czy w dacie podanej jako argument występuje jakieś święto
     *
     * @param date_yyyyMMdd - data w formacie yyyy-MM-dd
     * @return true/false
     */
    public boolean isDayHoliday(String date_yyyyMMdd) {
        String holidayName = getHolidayName(date_yyyyMMdd);
        return holidayName != null;
    }

    /**
     * Dodanie nowego święta do bazy danych
     *
     * @param date_yyyyMMdd - dzien dodania
     * @param holidayName   - nazwa swieta
     * @return true/false
     */
    public boolean insertHolidayToHolidayTable(String date_yyyyMMdd, String holidayName) {
        if (!isDayHoliday(date_yyyyMMdd)) {
            SQLiteDatabase db = this.getWritableDatabase();
            final String SQL_QUERY = "INSERT INTO " + HOLIDAYS_TABLE_NAME +
                    "(" + HOLIDAYS_DATE_COL + ", " + HOLIDAY_NAME + ")" +
                    "VALUES('" + date_yyyyMMdd + "', '" + holidayName + "')";
            db.execSQL(SQL_QUERY);
            return true;
        } else return false;
    }

    /**
     * Dodanie nowego święta do bazy danych
     *
     * @param date_yyyyMMdd - dzien dodania
     */
    public void deleteHolidayFromHolidayTable(String date_yyyyMMdd) {
        SQLiteDatabase db = this.getWritableDatabase();
        final String SQL_QUERY = "DELETE FROM " + HOLIDAYS_TABLE_NAME +
                " WHERE " + HOLIDAYS_DATE_COL + "= '" + date_yyyyMMdd + "'";
        System.out.println("Executing SQL_QUERY: " + SQL_QUERY);
        db.execSQL(SQL_QUERY);
    }

}

package com.example.xuefeng.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by xuefeng on 2016/1/13.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    private final String CREATE_PROVICE ="create table Province("+
            "id integer primary key autoincrement,"+
            "provinceName text,"+
            "provinceCode text)";

    private final String CREATE_CITY ="create table City("+
            "id integer primary key autoincrement,"+
            "cityName text,"+
            "cityCode text,"+
            "provinceId integer)";
    private final String CREATE_COUNTY ="create table County("+
            "id integer primary key autoincrement,"+
            "countyName text,"+
            "countyCode text,"+
            "cityId integer)";

    Context m_contex;
    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        m_contex=context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(m_contex,"make db success",Toast.LENGTH_SHORT).show();
        db.execSQL(CREATE_PROVICE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:

            case 2:

                break;
        }
    }
}

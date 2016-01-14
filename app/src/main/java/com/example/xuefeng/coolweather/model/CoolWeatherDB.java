package com.example.xuefeng.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.xuefeng.coolweather.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.validation.Validator;

/**
 * Created by xuefeng on 2016/1/13.
 */
public class CoolWeatherDB {
    /*
    * 数据库名
    * */
    public static final String DB_NAME = "cool_weather.db";
    /*
    * 数据库版本
    * */
    public static final int VERSION =1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;
    /*
    * 将构造方法私有化,变成单例类
    * */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper openHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = openHelper.getWritableDatabase();
    }

    /*
    * 获取CoolWeatherDB的实例
    * */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB(context);

        }
        return coolWeatherDB;
    }

    /*
    * 将Province实例存储到数据库中
    * */
    public void saveProvince(Province province){
        if(null == province){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("provinceName", province.getProvinceName());
        values.put("provinceCode", province.getProvinceCode());


        db.insert("Province",null,values);
    }
    /*
    * 把数据库中的数据取出放入Province中
    * */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                province.setId((cursor.getInt(cursor.getColumnIndex("id"))));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("provinceCode")));
                list.add(province);
                Log.e("ou", province.getProvinceCode());
                Log.e("ou", province.getProvinceName());
            }while (cursor.moveToNext());
        }
        return list;
    }
    /**
     * 将City实例存储到数据库。
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("cityName", city.getCityName());
            values.put("cityCode", city.getCityCode());
            values.put("provinceId", city.getProvinceId());
            db.insert("City", null, values);
        }
    }
    /**
     * 从数据库读取某省下所有的城市信息。
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "provinceId = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("cityCode")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void saveCounty(County county){
        if(null == county){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("countyName",county.getCountyName());
        values.put("countyCode",county.getCountyCode());
        values.put("cityId",county.getCityId());
        db.insert("County",null,values);
    }

    public List<County> loadCounties(int cityId){
        Cursor cursor = db.query("County",null,"cityId = ?",
                                    new String[]{String.valueOf(cityId)},
                                    null,null,null);
        List<County> list = new ArrayList<>();

        if(cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("cityId")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("countyCode")));
                county.setCountyName((cursor.getString(cursor.getColumnIndex("countyName"))));
                list.add(county);
            }while (cursor.moveToNext());
        }

        return list;
    }



}

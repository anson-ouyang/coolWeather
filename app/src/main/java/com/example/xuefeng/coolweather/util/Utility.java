package com.example.xuefeng.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.xuefeng.coolweather.model.City;
import com.example.xuefeng.coolweather.model.CoolWeatherDB;
import com.example.xuefeng.coolweather.model.County;
import com.example.xuefeng.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xuefeng on 2016/1/13.
 */
public class Utility {
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB weatherDB,
                                                               String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            Province province = new Province();
            for(String s : allProvinces){
                String[] array = s.split("\\|");
                province.setProvinceCode(array[0]);
                province.setProvinceName(array[1]);
                Log.e("ou", array[0]);
                Log.e("ou", array[1]);
                weatherDB.saveProvince(province);
            }
            return true;
        }
        return false;
    }

    public synchronized static boolean handleCityResponse(CoolWeatherDB weatherDB ,
                                                          String response,
                                                          int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities.length != 0){
                for(String s:allCities) {
                    String[] array = s.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean handleCountyResponse(CoolWeatherDB weatherDB,
                                                            String response,
                                                            int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(0 != allCounties.length){
                for(String s : allCounties){
                    String[] array = s.split("\\|");
                    County county = new County();
                    county.setCountyName(array[1]);
                    county.setCountyCode(array[0]);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
     */
    public static void handleWeatherResponse(Context context, String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
        String cityName = weatherInfo.getString("city");
        String cityId = weatherInfo.getString("cityid");
        String temp1 = weatherInfo.getString("temp1");
        String temp2 =weatherInfo.getString("temp2");
        String weather = weatherInfo.getString("weather");
        String publishTime = weatherInfo.getString("ptimme");
        saveWeatherInfo(context,cityName,cityId,temp1,temp2,weather,publishTime);

    }
    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp, String
                                               publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
                Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}

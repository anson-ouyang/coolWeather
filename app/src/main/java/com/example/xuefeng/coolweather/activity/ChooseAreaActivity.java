package com.example.xuefeng.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xuefeng.coolweather.R;
import com.example.xuefeng.coolweather.model.City;
import com.example.xuefeng.coolweather.model.CoolWeatherDB;
import com.example.xuefeng.coolweather.model.County;
import com.example.xuefeng.coolweather.model.Province;
import com.example.xuefeng.coolweather.util.HttpCallbackListener;
import com.example.xuefeng.coolweather.util.HttpUtil;
import com.example.xuefeng.coolweather.util.Utility;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity{

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private int currentLevel;
    TextView tv_title;
    ListView listView;
    List<Province> list_province;
    List<City> list_city;
    List<County> list_county;
    List<String> dataList = new ArrayList<>();
    String address = "";
    CoolWeatherDB weatherDB;
    ArrayAdapter<String> adapter;
    Province selectedProvince;
    City selectedCity;
    /**
     * 是否从WeatherActivity中跳转过来。
     */
    private boolean isFromWeatherActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);

        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        // 已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到WeatherActivity
        if (prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        tv_title = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,dataList);
        weatherDB = CoolWeatherDB.getInstance(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = list_province.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = list_city.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyCode = list_county.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,
                            WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    public void queryProvinces(){
        list_province = weatherDB.loadProvinces();
        if (list_province.size() > 0){
            dataList.clear();
            for (Province province:list_province){
                dataList.add(province.getProvinceName());
                Log.e("ou", province.getProvinceCode());
                Log.e("ou", province.getProvinceName());
            }
//            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
            tv_title.setText("中国");

        }else {
            queryFromServer(null,"province");
        }
    }

    public void queryCities(){
        list_city = weatherDB.loadCities(selectedProvince.getId());
        if( list_city.size() > 0){
            dataList.clear();
            for (City city:list_city){
                dataList.add(city.getCityName());
            }
//            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
            tv_title.setText(selectedProvince.getProvinceName());
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
    public void queryCounties(){
        list_county = weatherDB.loadCounties(selectedCity.getId());
        if(list_county.size()>0){
            dataList.clear();
            for (County county :
                    list_county) {
                dataList.add(county.getCountyName());
            }
//            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
            listView.setSelection(0);
            tv_title.setText(selectedCity.getCityName());
        }else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(weatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(weatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(weatherDB, response, selectedCity.getId());
                }
                if (result) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
            // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        }
            finish();
        }
    }
}

package com.example.xuefeng.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.xuefeng.coolweather.service.WeatherService;

/**
 * Created by xuefeng on 2016/1/14.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, WeatherService.class);
        context.startService(i);
    }
}

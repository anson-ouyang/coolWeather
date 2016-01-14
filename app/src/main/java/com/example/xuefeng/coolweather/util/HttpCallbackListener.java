package com.example.xuefeng.coolweather.util;

/**
 * Created by xuefeng on 2016/1/13.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}

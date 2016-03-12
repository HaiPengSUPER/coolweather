package com.haipeng.coolweather.utils;

public interface HttpCallbackListener {
void onFinish(String response);
void onError(Exception e);
}

package com.abudarevskiy.fyberchallenge.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import volley.Config_URL;


public class FyberRequest implements Serializable{
    private String apiKey=null;
    private Map<String, String> requestData =null;
    public FyberRequest() {
    }

    public Map<String, String> getRequestData() {
        return requestData;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String makeRequestParamsString(String UUID, final String appId, final String apiKey, final String pub0, final String uid){
        this.apiKey = apiKey;
        requestData = getDefaultParameters(UUID, appId, pub0, uid);

        String uriParams = "";
        SortedSet<String> keys = new TreeSet<String>(requestData.keySet());
        for (String key : keys) {
            String value = requestData.get(key);
            uriParams += key+"="+value+"&";
        }
        String uriParamsSHA1 = Config_URL.getSha1Hex(uriParams+apiKey);

        requestData.put("hashkey", uriParamsSHA1.toLowerCase());

        return uriParams+"hashkey="+uriParamsSHA1.toLowerCase();
    }

    public Map<String, String> makeRequestParams(String UUID, final String appId, final String apiKey, final String pub0, final String uid){
        this.apiKey = apiKey;
        requestData = getDefaultParameters(UUID, appId, pub0, uid);

        String uriParams = "";
        SortedSet<String> keys = new TreeSet<String>(requestData.keySet());
        for (String key : keys) {
            String value = requestData.get(key);
            uriParams += key+"="+value+"&";
        }
        String uriParamsSHA1 = Config_URL.getSha1Hex(uriParams+apiKey);
        requestData.put("hashkey", uriParamsSHA1.toLowerCase());

        return requestData;
    }

    @NonNull
    protected Map<String, String> getDefaultParameters(String uuid, String appId, String pub0, String uid) {

        requestData = new HashMap<String,String>();
        requestData.put("appid",appId);
        requestData.put("pub0",pub0);
        requestData.put("uid",uid);
//        requestData.put("ip", Config_URL.getLocalIpAddress());
        requestData.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
        requestData.put("os_version", Config_URL.getAndroidVersion());
        requestData.put("locale","DE");
        requestData.put("device_id", uuid);
//        requestData.put("offer_types","112");
        return requestData;
    }
}

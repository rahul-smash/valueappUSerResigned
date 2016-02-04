package com.signity.bonbon.network;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Rajinder on 5/10/15.
 */
public class NetworkAdaper {
    public static NetworkAdaper cInstance;
    public ApiService apiService;

    /* Static 'instance' method */
    public static NetworkAdaper getInstance() {
        return cInstance;
    }

    public static void initInstance() {
        if (cInstance == null) {
            cInstance = new NetworkAdaper();
            cInstance.setupRetrofitClient();
        }
    }

    public void setupRetrofitClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(2, TimeUnit.MINUTES);
        client.setReadTimeout(2, TimeUnit.MINUTES);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(client)).setEndpoint(NetworkConstant.BASE).setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getNetworkServices() {
        return apiService;
    }
}

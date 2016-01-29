package com.elromantico.client;

import android.app.Application;
import android.content.Context;

public class RitualsApplication extends Application {

    private static RitualsApplication instance;

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}

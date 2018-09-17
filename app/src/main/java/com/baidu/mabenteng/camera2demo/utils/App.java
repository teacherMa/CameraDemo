package com.baidu.mabenteng.camera2demo.utils;

import android.app.Application;
import android.content.Context;

/**
 * @author mabenteng
 * @version v10.10.0
 * Created by AndroidStudio
 * @since 2018/9/17
 */
public class App extends Application {

    private static Context sContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;
    }

    public static Context getContext() {
        return sContext;
    }
}

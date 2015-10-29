package com.prey.actions.retrievals;

import android.content.Intent;

/**
 * Created by oso on 28-10-15.
 */
public class WebServerServiceSingleton {


    private static WebServerServiceSingleton instance=null;
    private WebServerServiceSingleton(){}
    private Intent intent ;


    public static WebServerServiceSingleton getInstance(){
        if(instance==null)
            instance=new WebServerServiceSingleton();
        return instance;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}

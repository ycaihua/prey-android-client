/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.PreyStatus;
import com.prey.R;

public class DeviceReadyActivity extends Activity {


    @Override
    public void onResume() {
        PreyLogger.d("onResume of DeviceReadyActivity");
        super.onResume();

    }

    @Override
    public void onPause() {
        PreyLogger.d("onPause of DeviceReadyActivity");
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.device_ready);
        PreyLogger.i("onCreate of DeviceReadyActivity");

        TextView textView6=(TextView) findViewById(R.id.textView6);
        Typeface titilliumWebRegular = Typeface.createFromAsset(getAssets(), "fonts/Titillium_Web/TitilliumWeb-Regular.ttf");
        Typeface titilliumWebBold = Typeface.createFromAsset(getAssets(), "fonts/Titillium_Web/TitilliumWeb-Bold.ttf");
        Typeface magdacleanmonoRegular = Typeface.createFromAsset(getAssets(), "fonts/MagdaClean/magdacleanmono-regular.ttf");

        TextView textView1=(TextView)findViewById(R.id.textView1);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        TextView textView3_1=(TextView)findViewById(R.id.textView3_1);
        TextView textView3_2=(TextView)findViewById(R.id.textView3_2);
        TextView textView4_1=(TextView)findViewById(R.id.textView4_1);
        TextView textView4_2=(TextView)findViewById(R.id.textView4_2);
        TextView textView5_1=(TextView)findViewById(R.id.textView5_1);
        TextView textView5_2=(TextView)findViewById(R.id.textView5_2);

        textView1.setTypeface(magdacleanmonoRegular);
        textView2.setTypeface(magdacleanmonoRegular);
        textView3_1.setTypeface(magdacleanmonoRegular);
        textView3_2.setTypeface(titilliumWebBold);
        textView4_1.setTypeface(magdacleanmonoRegular);
        textView4_2.setTypeface(titilliumWebBold);
        textView5_1.setTypeface(magdacleanmonoRegular);
        textView5_2.setTypeface(titilliumWebBold);
        textView6.setTypeface(titilliumWebBold);

        LinearLayout linearLayout1=(LinearLayout)findViewById(R.id.linearLayout1);
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = PreyConfig.getPreyConfig(getApplication()).getPreyPanelUrl();
                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                    startActivity(browserIntent);
                    finish();
                } catch (Exception e) {
                    PreyLogger.i("error:"+e.getMessage());
                }
            }
        });

        LinearLayout linearLayout2=(LinearLayout)findViewById(R.id.linearLayout2);
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (!PreyStatus.getInstance().isPreyConfigurationActivityResume()) {
                    intent = new Intent(getApplication(), CheckPasswordActivity.class);
                } else {
                    intent = new Intent(getApplication(), PreyConfigurationActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });

        LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);






        if(PreyConfig.getPreyConfig(getApplication()).getProtectTour()) {
            linearLayout3.setVisibility(View.GONE);
            textView6.setVisibility(View.VISIBLE);

            textView6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = PreyConfig.getPreyConfig(getApplication()).getPreyUninstallUrl();

                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                    startActivity(browserIntent);

                    finish();
                }
            });
        }else{

            linearLayout3.setVisibility(View.VISIBLE);
            textView6.setVisibility(View.GONE);
            try {

                linearLayout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplication(), TourActivity1.class);
                        Bundle b = new Bundle();
                        b.putInt("id", 1);
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    }
                });
            }catch (Exception e){

            }
        }
    }


}

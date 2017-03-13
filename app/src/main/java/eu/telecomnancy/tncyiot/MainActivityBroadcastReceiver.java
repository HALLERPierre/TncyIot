package eu.telecomnancy.tncyiot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Entity.LightRecords;
import eu.telecomnancy.tncyiot.Util.MovingAverage;

/**
 * Created by Florian on 25/01/2017.
 */

public class MainActivityBroadcastReceiver extends BroadcastReceiver {
    private MainActivity activity;
    private HashMap<String, MovingAverage> objAvg;


    public MainActivityBroadcastReceiver(MainActivity activity) {
        this.activity = activity;
        this.objAvg = new HashMap<String, MovingAverage>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MainActivityBroadcastR",
                "onReceive"
        );
        if (intent.getAction().equals(MainService.PUBLISH_RESULT)){
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                LightRecords listLight = new LightRecords(new LightRecords.ChangeListener() {
                    @Override
                    public void onChange(Light light) {
                        System.out.println(light.getLabel() + " switched on");
                        return;
                    }
                }, objAvg);

                //TODO : find why I can't cast directly to my Object
                ArrayList<Light> listTmp = (ArrayList<Light>)bundle.getSerializable(MainService.OUTPUT_LIGHTS_RECORDS);

                listLight.addAll(listTmp);
                //refresh activity
                activity.updateLightListView(listLight);
            }
        }
    }


}

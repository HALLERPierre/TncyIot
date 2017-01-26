package eu.telecomnancy.tncyiot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Entity.LightRecords;
import eu.telecomnancy.tncyiot.Entity.LightsRecordsData;

/**
 * Created by Florian on 25/01/2017.
 */

public class MainActivityBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MainActivityBroadcastReceiver",
                "onReceive"
        );
        if (intent.getAction().equals(MainService.PUBLISH_RESULT)){
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //TODO : improve
                HashMap<String,LightRecords> hashMap = (HashMap<String,LightRecords>)bundle.getSerializable(MainService.OUTPUT_LIGHTS_RECORDS);
                LightsRecordsData hashMap2 = new LightsRecordsData();
                hashMap2.putAll(hashMap);
                Log.d("MainActivity","hashmap on view"+hashMap2.size());
            }
        }
    }
}

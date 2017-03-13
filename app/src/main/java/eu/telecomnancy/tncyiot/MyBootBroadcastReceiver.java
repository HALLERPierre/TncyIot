package eu.telecomnancy.tncyiot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by haller7u on 26/01/17.
 */
public class MyBootBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_START_ON_BOOT = "android.intent.action.BOOT_COMPLETED";
    //Key of checkBox
    String startOnBoot = "eu.telecomnancy.tncyiot.app.startOnBoot";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyBootBroadcastReceiver",
                "onReceive boot : " + intent.getAction()
        );

        //get app prefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isStartChecked = prefs.getBoolean(startOnBoot, false);
        Intent intentStart = new Intent(context, MainService.class);
        intentStart.setAction(ACTION_START_ON_BOOT);
        // Start on boot
        if(isStartChecked && intent.getAction().equals(ACTION_START_ON_BOOT)){
            context.startService(intentStart);
        }
    }
}

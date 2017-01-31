package eu.telecomnancy.tncyiot;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import eu.telecomnancy.tncyiot.Entity.LightRecords;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MainService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_START_ON_BOOT = "eu.telecomnancy.tncyiot.action.STARTONBOOT";

    public static final String PUBLISH_RESULT = "eu.telecomnancy.tncyiot.service.receiver";
    // TODO: get url from activy
    public static final String INPUT_REST_URL = "eu.telecomnancy.tncyiot.extra.PARAM1";
    public static final String OUTPUT_LIGHTS_RECORDS = "eu.telecomnancy.tncyiot.extra.PARAM2";

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private static Timer myTimer;
    private static TimerTask timerTask;
    private boolean isThreadRunning = false;


    public MainService() {
        super("MainService");
        timerTask = initTimerTask();
    }

    public LightTimerTask initTimerTask(){
        return new LightTimerTask() {
            @Override
            public void myTimerTaskCallback(LightRecords lightsRecordsList) {
                publishResults(lightsRecordsList);
            }

            @Override
            public Context myTimerTaskContext() {
                return getApplicationContext();
            }

            @Override
            public String myTimerTaskUrl() {
                return "http://iotlab.telecomnancy.eu/rest/data/1/light1/last";
            }
        };
    }
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        MainService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MainService.this;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("IntentService",
                "onHandleIntent called"
        );
    }

    /**
     * start thread and timer
     */
    public void handleActionOn(String param1) {
        Log.d("IntentService",
                "handleActionOn called"
        );
        if (!isThreadRunning) {
            timerTask = initTimerTask();
            myTimer = new Timer();
            myTimer.scheduleAtFixedRate(timerTask, 0, 10000);
            isThreadRunning = true;
        }
    }

    /**
     * Stop thread and purge timer
     */
    public void handleActionOff() {
        Log.d("IntentService",
                "handleActionOff called"
        );
        if(isThreadRunning) {
            timerTask.cancel();
            myTimer.purge();
            myTimer.cancel();
            isThreadRunning = false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("IntentService",
                "onCreate called : service créé"
        );

        super.onStartCommand(intent, startId, startId);
        //Start on boot if checked
        String action = intent.getAction();
        if (ACTION_START_ON_BOOT.equals(action)) {
            handleActionOn(null);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("IntentService",
                "onBind called : service créé"
        );
        return mBinder;
    }


    @Override
    public void onDestroy() {
        Log.d("IntentService",
                "onDestroy called : service detruit"
        );
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void publishResults(LightRecords lightsRecordsList) {
        Intent intent = new Intent(PUBLISH_RESULT);
        Bundle extras = new Bundle();
        extras.putSerializable(OUTPUT_LIGHTS_RECORDS, lightsRecordsList);
        intent.putExtras(extras);
        sendBroadcast(intent);

    }

}
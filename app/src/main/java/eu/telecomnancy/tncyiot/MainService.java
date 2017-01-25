package eu.telecomnancy.tncyiot;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Entity.LightRecords;
import eu.telecomnancy.tncyiot.Entity.LightsRecordsData;
import eu.telecomnancy.tncyiot.Entity.RestResult;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MainService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_ON = "eu.telecomnancy.tncyiot.action.ON";
    public static final String ACTION_OFF = "eu.telecomnancy.tncyiot.action.OFF";
    public static final String PUBLISH_RESULT = "eu.telecomnancy.tncyiot.service.receiver";

    // TODO: get url from activy
    public static final String INPUT_REST_URL = "eu.telecomnancy.tncyiot.extra.PARAM1";
    public static final String OUTPUT_LIGHTS_RECORDS = "eu.telecomnancy.tncyiot.extra.PARAM2";

    //


    private static Timer myTimer;
    private static TimerTask timerTask;
    final Handler handler = new Handler();


    public MainService() {
        super("MainService");

        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        try {
                            RestTask task = new RestTask(getApplicationContext(),new RestTask.TaskListener() {
                                @Override
                                public void onFinished(String jsonresult) {
                                    // Do Something after the task has finished
                                    Log.i("MainService", "RESULT = " + jsonresult);
                                    LightsRecordsData lightsRecordsDataMap = new LightsRecordsData();


                                    final Gson gson = new GsonBuilder().create();
                                    Type castType = new TypeToken<RestResult<Light>>(){}.getType();

                                    RestResult<Light> restresult = gson.fromJson(jsonresult, castType);
                                    //todo:check if data not null
                                    for(Light l : restresult.data){
                                        if (! lightsRecordsDataMap.containsKey(l.getLabel())){
                                            lightsRecordsDataMap.put(l.getLabel(),new LightRecords(new LightRecords.ChangeListener() {
                                                @Override
                                                public void onChange(Light light) {
                                                    Date date = new Date(light.getTimestamp());
                                                    // S is the millisecond
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");


                                                    Calendar calendar = Calendar.getInstance();
                                                    int hourOfDay  = calendar.get(Calendar.HOUR_OF_DAY);
                                                    if (hourOfDay >= 18 && hourOfDay<= 23)
                                                        LightNotification.notify(getApplicationContext(),light.getLabel(),simpleDateFormat.format(date));
                                                    else {
                                                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                        intent.setType("message/rfc822");
                                                        intent.putExtra(Intent.EXTRA_EMAIL, "f.kromer54@gmail.com");
                                                        intent.setData(Uri.parse("mailto:"+"f.kromer54@gmail.com"));
                                                        intent.putExtra(Intent.EXTRA_SUBJECT, "azert");
                                                        intent.putExtra(Intent.EXTRA_TEXT, "azertyuuyfcryxxu");
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                                        try {

                                                            startActivity(intent);
                                                        } catch (android.content.ActivityNotFoundException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                            Log.d("Email error:",e.toString());
                                                        }
                                                    }
                                                }
                                            }));
                                        }
                                        lightsRecordsDataMap.get(l.getLabel()).add(l);
//                                        Log.i("MainService", l.toString());
                                    }
                                    publishResults(lightsRecordsDataMap);
                                }
                            });
                            task.execute(new URL("http://iotlab.telecomnancy.eu/rest/data/1/light1/last"));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        myTimer = new Timer();
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("IntentService",
                "onHandleIntent called"
        );
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ON.equals(action)) {
                final String param1 = intent.getStringExtra(INPUT_REST_URL);
                handleActionOn(param1);
            } else if (ACTION_OFF.equals(action)){
                handleActionOff();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionOn(String param1) {
        Log.d("IntentService",
                "handleActionOn called"
        );

        timerTask.run();
        myTimer.scheduleAtFixedRate(timerTask, 0, 5000);

    }

    private void handleActionOff(){
        Log.d("IntentService",
                "handleActionOff called"
        );
        timerTask.cancel();
        myTimer.purge();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        return START_STICKY;
    }

    @Override
    public  void onCreate (){
        super.onCreate();
    }

    private void publishResults(LightsRecordsData lightsRecordsDataMap) {
        Intent intent = new Intent(PUBLISH_RESULT);
        Bundle extras = new Bundle();
        extras.putSerializable(OUTPUT_LIGHTS_RECORDS, lightsRecordsDataMap);
        intent.putExtras(extras);
        sendBroadcast(intent);

    }

}
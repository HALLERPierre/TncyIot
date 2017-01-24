package eu.telecomnancy.tncyiot;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import eu.telecomnancy.tncyiot.Entity.Light;
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

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "eu.telecomnancy.tncyiot.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "eu.telecomnancy.tncyiot.extra.PARAM2";


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
                            RestTask task = new RestTask(new RestTask.TaskListener() {
                                @Override
                                public void onFinished(String jsonresult) {
                                    // Do Something after the task has finished
                                    Log.i("MainService", "RESULT = " + jsonresult);
                                    final Gson gson = new GsonBuilder().create();
                                    Type castType = new TypeToken<RestResult<Light>>(){}.getType();

                                    RestResult<Light> restresult = gson.fromJson(jsonresult, castType);

                                    Log.i("IntentService", "RESPONSE = " + restresult.toString());
                                    for(Light l : restresult.data){
                                        Log.i("MainService", l.toString());

                                    }
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


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(ACTION_ON);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("IntentService",
                "onHandleIntent called"
        );
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ON.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionOn(param1, param2);
            } else if (ACTION_OFF.equals(action)){
                handleActionOff();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionOn(String param1, String param2) {
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

}
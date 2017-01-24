package eu.telecomnancy.tncyiot;

        import android.app.IntentService;
        import android.content.BroadcastReceiver;
        import android.content.Intent;
        import android.content.Context;
        import android.content.IntentFilter;
        import android.os.Handler;
        import android.util.Log;
        import android.widget.Toast;

        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.reflect.TypeToken;

        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpUriRequest;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.InputStreamReader;
        import java.io.Reader;
        import java.lang.reflect.Type;
        import java.net.URI;
        import java.net.URISyntaxException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
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

    private static final String ACTION_FOR_INTENT_CALLBACK = "REST_CALL_API_LIGHT";

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


    public MainService() {
        super("MainService");
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());

                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
                        toast.show();
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
        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
//                        //get the current timeStamp
//                        Calendar calendar = Calendar.getInstance();
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
//                        final String strDate = simpleDateFormat.format(calendar.getTime());
//
//                        //show the toast
//                        int duration = Toast.LENGTH_SHORT;
//                        Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
//                        toast.show();

                        RestTask task = new RestTask(getApplicationContext(), ACTION_FOR_INTENT_CALLBACK);
                        try {
                            HttpGet httpGet = new HttpGet(new URI("http://iotlab.telecomnancy.eu/rest/data/1/light1/last"));
                            httpGet.setHeader("Content-type", "application/json");
                            task.execute(httpGet);

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
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

    /**
     * Our Broadcast Receiver. We get notified that the data is ready this way.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            Log.i("IntentService", "RESPONSE = " + response);

            final Gson gson = new GsonBuilder().create();

            RestResult result = gson.fromJson(response, RestResult.class);
            Log.i("IntentService", "RESPONSE = " + result);


//            Type listType = new TypeToken<ArrayList<Light>>(){}.getType();
//
//            List<Light> yourClassList = new Gson().fromJson(response, listType);

            //
            // my old json code was here. this is where you will parse it.
            //
        }
    };

}
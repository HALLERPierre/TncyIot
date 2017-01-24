package eu.telecomnancy.tncyiot;

        import android.app.IntentService;
        import android.content.Intent;
        import android.content.Context;
        import android.os.Handler;
        import android.util.Log;
        import android.widget.Toast;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Timer;
        import java.util.TimerTask;

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


    private static TimerTask timerTask;
    private static Timer myTimer;

    final Handler handler = new Handler();

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
}
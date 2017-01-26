package eu.telecomnancy.tncyiot;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class MainActivity extends ActionBarActivity {
    private BroadcastReceiver receiver = new MainActivityBroadcastReceiver();

    //Key of checkBox
    String startOnBoot = "eu.telecomnancy.tncyiot.app.startOnBoot";

    private MainService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //authorize email sending
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        setContentView(R.layout.activity_main);
        Log.d("MainActivity",
                "Création de l'activité"
        );

        //get app prefs
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Button buttonOn= (Button)findViewById(R.id.button);
        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.handleActionOn(null);
            }
        });
        Button buttonOff= (Button)findViewById(R.id.button2);
        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.handleActionOff();
            }
        });


        CheckBox checkBoxStart= (CheckBox) findViewById(R.id.checkBoxStart);
        checkBoxStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("MainActivity",
                        "checkbox changed : " + b
                );
                prefs.edit().putBoolean(startOnBoot, b).apply();
            }
        });

        boolean isStartChecked = prefs.getBoolean(startOnBoot, false);
        checkBoxStart.setChecked(isStartChecked);

        // Create intent & Bind to MainService
        Intent serviceIntent = new Intent(getApplicationContext(), MainService.class);
        // Start on boot
        if (isStartChecked) {
            serviceIntent.setAction(MainService.ACTION_START_ON_BOOT);
        }
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onDestroy() {
        Log.d("MainActivity",
                "onDestroy called"
        );
        super.onDestroy();
        unbindService(mConnection);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(MainService.PUBLISH_RESULT));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MainService.LocalBinder binder = (MainService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
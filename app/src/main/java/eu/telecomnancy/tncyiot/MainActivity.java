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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Entity.LightRecords;
import eu.telecomnancy.tncyiot.UI.LightAdapter;


public class MainActivity extends ActionBarActivity {
    //Key of checkBox
    private static final String startOnBoot = "eu.telecomnancy.tncyiot.app.startOnBoot";

    /**  Define callback for lights data refresh    */
    private MainActivityBroadcastReceiver receiver = new MainActivityBroadcastReceiver(this);
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        boolean mBound = false;
        @Override
        public void onServiceConnected(ComponentName className,  IBinder service) {
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

    //
    private MainService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //authorize email sending
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // add button and controllers
        buildUI();

        // Create intent & Bind to MainService
        Intent serviceIntent = new Intent(getApplicationContext(), MainService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

    }

    /*
    add controler on buttonOn, buttonOff, checkbox
     */
    private void buildUI() {
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
    }

    /*
    update the listview with lights data
     */
    public void updateLightListView(ArrayList<Light> lights){
        Log.d("MainActivity",
                lights.size()+""
        );
        ListView mListView = (ListView) findViewById(R.id.listView);
        LightAdapter adapter = new LightAdapter(MainActivity.this, lights);
        mListView.setAdapter(adapter);
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
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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


}
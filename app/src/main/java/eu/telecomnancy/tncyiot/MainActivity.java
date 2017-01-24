package eu.telecomnancy.tncyiot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity",
                "Création de l'activité"
        );

        Button buttonOn= (Button)findViewById(R.id.button);
        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), MainService.class);
                serviceIntent.putExtra(MainService.EXTRA_PARAM1, "toto");
                serviceIntent.putExtra(MainService.EXTRA_PARAM2, "titi");
                serviceIntent.setAction(MainService.ACTION_ON);
                startService(serviceIntent);
            }
        });
        Button buttonOff= (Button)findViewById(R.id.button2);
        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(),MainService.class);
                serviceIntent.setAction(MainService.ACTION_OFF);
                startService(serviceIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onDestroy() {
        Intent serviceIntent = new Intent(getApplicationContext(),MainService.class);
        stopService(serviceIntent);
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
}
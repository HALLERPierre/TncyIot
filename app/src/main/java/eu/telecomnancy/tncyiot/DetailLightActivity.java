package eu.telecomnancy.tncyiot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Map;
import java.util.Timer;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Entity.LightRecords;
import eu.telecomnancy.tncyiot.Entity.Room;

import static android.R.attr.x;

public class DetailLightActivity extends ActionBarActivity {
    public static final String ACTION_SHOW_DETAIL = "eu.telecomnancy.tncyiot.action.SHOW_DETAIL";
    public static final String INPUT_MOTE = "eu.telecomnancy.tncyiot.extra.MOTE";
    public static final String INPUT_LABEL = "eu.telecomnancy.tncyiot.extra.LABEL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_light);

        // On récupère l'intent qui a lancé cette activité
        Intent i = getIntent();

        final String mote = i.getStringExtra(INPUT_MOTE);
        final String label = i.getStringExtra(INPUT_LABEL);

        LightTimerTask timerTask = new LightTimerTask() {
            @Override
            public void myTimerTaskCallback(LightRecords lightsRecordsList) {
                updateView(lightsRecordsList);
            }

            @Override
            public Context myTimerTaskContext() {
                return getApplicationContext();
            }

            @Override
            public String myTimerTaskUrl() {
                return "http://iotlab.telecomnancy.eu/rest/data/1/"+label+"/5/"+mote;
            }
        };
        timerTask.run();

    }

    private void updateView(LightRecords lightsRecordsList) {
        Map<String,Room> map =  Room.loadMoteInRooms();


        final RelativeLayout rr = (RelativeLayout) findViewById(R.id.activity_detail_light);
        Light lr = lightsRecordsList.get(0);
            if (map.containsKey(lr.getMote())){
                Room r = map.get(lr.getMote());
                ImageView iv = new ImageView(getApplicationContext());
                iv.setX(r.getPosX());
                iv.setY(r.getPosY() -70);
                if (lr.isSwitchOn())
                    iv.setImageDrawable(getResources().getDrawable(R.drawable.light_bulb_ok));
                else
                    iv.setImageDrawable(getResources().getDrawable(R.drawable.light_bulb_ko));
                ((ViewGroup) rr).addView(iv);

            }


        Log.d("Detail",lightsRecordsList.size()+"");
    }

}

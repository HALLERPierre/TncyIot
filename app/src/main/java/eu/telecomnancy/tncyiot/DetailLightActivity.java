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

/**
 * show more information about a light with a map of the school
 */
public class DetailLightActivity extends ActionBarActivity {
    public static final String ACTION_SHOW_DETAIL = "eu.telecomnancy.tncyiot.action.SHOW_DETAIL";
    public static final String INPUT_MOTE = "eu.telecomnancy.tncyiot.extra.MOTE";
    public static final String INPUT_LABEL = "eu.telecomnancy.tncyiot.extra.LABEL";
    public static final String INPUT_SWITCH = "eu.telecomnancy.tncyiot.extra.SWITCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_light);

        // On récupère l'intent qui a lancé cette activité
        Intent i = getIntent();

        Bundle bundle = i.getBundleExtra("bundle");
        Light l = null;
        if (bundle != null) {
            l = (Light)bundle.getSerializable("light");
        }

        updateView(l);

    }

    private void updateView(Light lr) {
        Map<String,Room> map =  Room.loadMoteInRooms();


        final RelativeLayout rr = (RelativeLayout) findViewById(R.id.activity_detail_light);
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


    }

}

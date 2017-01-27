package eu.telecomnancy.tncyiot.UI;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import eu.telecomnancy.tncyiot.Entity.Light;

/**
 * Created by Florian on 27/01/2017.
 */

public class LightViewHolder {
    public TextView label;
    public TextView description;
    public ImageView icon;


    public void setDescriptionText(long timestamp){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");

        description.setText(simpleDateFormat.format(new Date(timestamp)));
    }

    public void setIcon(boolean isSwitchOn) {
        if (isSwitchOn)
            this.icon.setImageDrawable(new ColorDrawable(Color.YELLOW));
        else
            this.icon.setImageDrawable(new ColorDrawable(Color.GRAY));

    }
}

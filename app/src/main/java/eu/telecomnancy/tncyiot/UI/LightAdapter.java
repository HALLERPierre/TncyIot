package eu.telecomnancy.tncyiot.UI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.R;

/**
 * Created by Florian on 27/01/2017.
 * Allow to list the light in the front page by mapping UI and light entity
 */

public class LightAdapter extends ArrayAdapter<Light> {

        //tweets est la liste des models à afficher
        public LightAdapter(Context context, List<Light> lights) {
            super(context, 0, lights);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_light,parent, false);
            }

            LightViewHolder viewHolder = (LightViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new LightViewHolder();
                viewHolder.label = (TextView) convertView.findViewById(R.id.label_light);
                viewHolder.description = (TextView) convertView.findViewById(R.id.desc_light);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_light);
                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<Lights>
            Light light = getItem(position);
            Log.d("Ligth", light.isSwitchOn()+"");

            //il ne reste plus qu'à remplir notre vue
            viewHolder.label.setText(light.getMote());
            viewHolder.setDescriptionText(light.getTimestamp());
            viewHolder.setIcon(light.isSwitchOn());

            return convertView;
        }

    }
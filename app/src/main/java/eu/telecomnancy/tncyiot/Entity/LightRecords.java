package eu.telecomnancy.tncyiot.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import eu.telecomnancy.tncyiot.Util.MovingAverage;

/**
 * Created by Florian on 25/01/2017.
 * list of the lights
 */

public class LightRecords extends ArrayList<Light>  {

    /**
     * threshold in percent
     */
    public final double AVG_THRESHOLD = 1.50;
    /**
     * listerner called when we add a new item
     */
    private ChangeListener listener;
    /**
     * average lumen for each mote
     */
    private HashMap<String,MovingAverage> objectAverage;

    public LightRecords(ChangeListener listener) {

        this.listener = listener;
        this.objectAverage = new HashMap<String,MovingAverage>();
    }

    @Override
    /**
     * When light is added, check if light is on
     */
    public boolean add(Light object) {
        String mote = object.getMote();
        if(!objectAverage.containsKey(object.getMote())){
            objectAverage.put(mote, new MovingAverage(5));
        }
        MovingAverage mvgAvg = objectAverage.get(mote);
        //get oldAvg & newAvg
        double oldAvg = mvgAvg.getAvg();
        mvgAvg.newNum(object.getValue());
        double newAvg = mvgAvg.getAvg();
        System.out.println("old avg : " + oldAvg + " || new avg : " + newAvg);
        //if 50% diff in new & old avg, switch on
        if (oldAvg*AVG_THRESHOLD < newAvg && (oldAvg > 0 && newAvg > 0)){
            object.setSwitchOn(true);
            listener.onChange(object);
        }
        return super.add(object);
    }

    @Override
    public boolean addAll(Collection<? extends Light> list){
        boolean isAddOk = true;
        for (Light l : list) {
            if(!this.add(l)){
                isAddOk = false;
            }
        }
        return isAddOk;
    }

    public interface ChangeListener {
        void onChange(Light light);
    }
}

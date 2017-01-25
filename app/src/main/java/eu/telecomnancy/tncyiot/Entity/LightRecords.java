package eu.telecomnancy.tncyiot.Entity;

import java.util.ArrayList;

/**
 * Created by Florian on 25/01/2017.
 */

public class LightRecords extends ArrayList<Light>  {

    public final double AVG_THRESHOLD = 1.30;
    private ChangeListener listener;

    public LightRecords(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean add(Light object) {
        if (calculateAverage()*AVG_THRESHOLD < object.getValue()){
            listener.onChange(object);
        }
        return super.add(object);
    }

    private double calculateAverage() {
        Double sum = 0.0;
        if(!this.isEmpty()) {
            for (Light light : this) {
                sum += light.getValue();
            }
            return sum.doubleValue() / this.size();
        }
        return sum;
    }
    public interface ChangeListener {
        void onChange(Light light);
    }
}

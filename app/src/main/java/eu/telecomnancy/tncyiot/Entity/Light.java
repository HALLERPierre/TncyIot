package eu.telecomnancy.tncyiot.Entity;

/**
 * Created by kromer1u on 24/01/17.
 */
public class Light {
    private long timestamp;
    private String label;
    private double value;
    private String mote;

    @Override
    public String toString() {
        return "Light{" +
                "timestamp=" + timestamp +
                ", label='" + label + '\'' +
                ", value=" + value +
                ", mote='" + mote + '\'' +
                '}';
    }
}

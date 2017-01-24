package eu.telecomnancy.tncyiot.Entity;

/**
 * Created by kromer1u on 24/01/17.
 */
public class Light {
    private long timestamp;
    private String label;
    private double value;
    private String mote;


    public Light() {
    }

    public Light(long timestamp, String label, double value, String mote) {
        this.timestamp = timestamp;
        this.label = label;
        this.value = value;
        this.mote = mote;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

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

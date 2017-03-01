package eu.telecomnancy.tncyiot.Entity;

/**
 * Created by kromer1u on 24/01/17.
 * describe a light with properties from api
 */
public class Light implements java.io.Serializable {

    private long timestamp;
    private String label;
    private double value;
    private String mote;
    private boolean switchOn;

    public Light() {
    }

    public Light(long timestamp, String label, double value, String mote) {
        this.timestamp = timestamp;
        this.label = label;
        this.value = value;
        this.mote = mote;
    }

    public String getMote() {
        return mote;
    }

    public String getLabel() {
        return label;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    public boolean isSwitchOn() {
        return switchOn;
    }

    public void setSwitchOn(boolean switchOn) {
        this.switchOn = switchOn;
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

package lt.vu.mif.nosql.rest_example.dto;



public class Meter {
    String meterID;
    double value;

    public Meter(String meterID, double value) {
        this.meterID = meterID;
        this.value = value;
    }

    public Meter() {
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getMeterID() {
        return meterID;
    }

    public void setMeterID(String meterID) {
        this.meterID = meterID;
    }
}

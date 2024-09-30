package lt.vu.mif.nosql.rest_example.dto;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {

    private String fullName;
    private String address;
    private  int id = 0;
    HashMap<String, Double> meters;


    public Client(String address, String fullName) {
        this.address = address;
        this.fullName = fullName;
        id++;
        meters = new HashMap<String, Double>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public  int getId() {
        return id;
    }

    public  void setId(int id) {
        this.id = id;
    }

    public Client(HashMap<String, Double> meters) {
        this.meters = meters;
    }

    public HashMap<String, Double> getMeters() {
        return meters;
    }

    public void setMeters(HashMap<String, Double> meters) {
        this.meters = meters;
    }

    public  boolean appendMeter (String meterID, double meterValue){
        if(this.meters.put(meterID, meterValue) == null){ // new value added
            return true;
        }else { // value updated
            return false;
        }
    }
/* WIP
    public void removeMeter(String meterID){
        return this.meters.remove(meterID);
    }

 */
}

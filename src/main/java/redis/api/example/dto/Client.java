package redis.api.example.dto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;



public class Client {

    private String fullName;
    private String address;


    private  int id;
    Map<String, Double> meters;

    public Client(String fullName, String address, int id, Map<String, Double> meters) {
        this.fullName = fullName;
        this.address = address;
        this.id = id;
        this.meters = meters;
    }

    public Client() {
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

    public Client(Map<String, Double> meters) {
        this.meters = meters;
    }

    public Map<String, Double> getMeters() {
        return meters;
    }

    public void setMeters(Map<String, Double> meters) {
        this.meters = meters;
    }

    public  boolean appendMeter (String meterID, double meterValue){
        if(meters == null){
            return false;
        } else if(this.meters.put(meterID, meterValue) == null){ // new value added
            return true;
        }else { // value updated
            return false;
        }
    }

    public Double getMeterValue (String meterID){
        return this.meters.get(meterID);
    }
/* WIP
    public void removeMeter(String meterID){
        return this.meters.remove(meterID);
    }

 */
}

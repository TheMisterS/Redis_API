package lt.vu.mif.nosql.rest_example.dto;

public class Client {

    private String fullName;
    private String address;
    private  int id = 0;
    private Meter[] meters;

    public Client(String address, String fullName) {
        this.address = address;
        this.fullName = fullName;
        id++;
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

    public Meter[] getMeters() {
        return meters;
    }

    public void setMeters(Meter[] meters) {
        this.meters = meters;
    }
}

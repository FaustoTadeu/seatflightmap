package ie.com.faustoalves.seatflightmap.model;

public class Passenger {

    private int group;
    private String codPassenger;

    public Passenger(int group, String codPassenger) {
        this.group = group;
        this.codPassenger = codPassenger;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getCodPassenger() {
        return codPassenger;
    }

    public void setCodPassenger(String codPassenger) {
        this.codPassenger = codPassenger;
    }
}

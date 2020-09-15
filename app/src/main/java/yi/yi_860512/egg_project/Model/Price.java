package yi.yi_860512.egg_project.Model;

public class Price {
    private String CID;
    private String PID;
    private String price;

    public Price(String CID, String PID, String price) {
        this.CID = CID;
        this.PID = PID;
        this.price = price;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

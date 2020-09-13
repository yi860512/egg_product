package yi.yi_860512.egg_project.Model;

public class Cust {
    private String name;
    private String id;
    private String gmail;
    private String phone;
    private String address;

    public Cust(String name, String id, String gmail, String phone,String address) {
        this.name = name;
        this.address = address;
        this.id = id;
        this.gmail = gmail;
        this.phone = phone;
    }

    public Cust(String name, String gmail, String phone,String address) {
        this.name = name;
        this.gmail = gmail;
        this.phone = phone;
        this.address = address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getGmail() {
        return this.gmail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}

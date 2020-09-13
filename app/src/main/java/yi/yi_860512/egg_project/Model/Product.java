package yi.yi_860512.egg_project.Model;

public class Product {
    private String name;
    private String id;
    private String price;

    public Product(String name, String id, String price) {
        this.name = name;
        this.id = id;
        this.price = price;
    }

    public Product(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return this.price;
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

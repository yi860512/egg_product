package yi.yi_860512.egg_project.Model;

public class Team {

    private String name, id;

    public Team(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public Team(String name) {
        this.name = name;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

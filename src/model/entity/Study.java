package model.entity;

/**
 * Created by emilstepanian on 19/11/2016.
 * Specifies the Study model object.
 */
public class Study {

    private  int id;
    private  String name;
    private  String shortname;

    public Study (int id, String name, String shortname){
        this.id = id;
        this.shortname = shortname;
        this.name = name;
    }

    public Study() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getShortname() {
        return shortname;
    }

    @Override
    public String toString() {
        return "Study{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortname='" + shortname + '\'' +
                '}';
    }
}

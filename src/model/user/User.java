package model.user;

import java.io.Serializable;

/**
 * Created by emilstepanian on 19/11/2016.
 *  * Specifies the User model object.
 */
public class User implements Serializable {

    private int id;
    private String cbsMail, password, type, firstName, lastName;

    private static final long serialVersionUID = 1L;

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCbsMail() {
        return cbsMail;
    }

    public void setCbsMail(String cbsMail) {
        this.cbsMail = cbsMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}


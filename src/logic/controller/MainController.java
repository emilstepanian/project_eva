package logic.controller;

import dal.DBWrapper;
import model.user.User;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class MainController {

    public MainController(){

    }

    /**
     * Passes the requested login to the database and verifies login.
     * Returns user information in an User object.
     * @param cbs_mail The CBS mail of the requested login.
     * @param password The password of the requested login.
     * @return User object containing the verified user's information.
     */
    public User authenticate(String cbs_mail, String password) {

        try {
            Map<String,String> whereParams = new HashMap<String, String>();
            whereParams.put("cbs_mail", cbs_mail);
            whereParams.put("password", password);

            CachedRowSet rowSet = DBWrapper.getRecords("user", null, whereParams, null);

            while (rowSet.next()){

                User user = new User();
                user.setId(rowSet.getInt("id"));
                user.setCbsMail(rowSet.getString("cbs_mail"));
                user.setType(rowSet.getString("type"));
                user.setFirstName(rowSet.getString("firstName"));
                user.setLastName(rowSet.getString("lastName"));

                System.out.println("User found");
                return user;
            }

        } catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        System.out.println("User not found");
        return null;
    }

}

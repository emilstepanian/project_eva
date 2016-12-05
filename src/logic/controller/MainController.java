package logic.controller;

import dal.DBWrapper;
import logic.misc.ConfigLoader;
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
            whereParams.put(ConfigLoader.USER_CBSMAIL_COLUMN, cbs_mail);
            whereParams.put(ConfigLoader.USER_PASSWORD_COLUMN, password);

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.USER_TABLE, null, whereParams, null);

            while (rowSet.next()){

                User user = new User();
                user.setId(rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
                user.setCbsMail(rowSet.getString(ConfigLoader.USER_CBSMAIL_COLUMN));
                user.setType(rowSet.getString(ConfigLoader.USER_TYPE_COLUMN));
                user.setFirstName(rowSet.getString(ConfigLoader.USER_FIRSTNAME_COLUMN));
                user.setLastName(rowSet.getString(ConfigLoader.USER_LASTNAME_COLUMN));

                return user;
            }

        } catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

}

package dal;

import com.sun.rowset.CachedRowSetImpl;
import logic.misc.ConfigLoader;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Kasper on 17/10/2016.
 * Class implements the MYSQL Driver to talk to a MYSQL database.
 */
public class MYSQLDriver {
    private static final String URL = ConfigLoader.DB_TYPE+ConfigLoader.DB_HOST+ConfigLoader.DB_PORT+"/"+ConfigLoader.DB_NAME+"?autoReconnect=true&useSSL=false";
    private static final String USERNAME = ConfigLoader.DB_USER;
    private static final String PASSWORD = ConfigLoader.DB_PASS;


    public MYSQLDriver(){

    }

    /**
     * Method used to retrieve information from the database.
     * @param sql The SQL statement to execute in the database.
     * @return The data retrieved in a CachedRowSet
     */
    public static CachedRowSet executeSQL(String sql) throws SQLException {
        ResultSet result = null;
        Connection dbConnection = null;
        CachedRowSet cr = new CachedRowSetImpl();
        try {
            dbConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            result = dbConnection.prepareStatement(sql).executeQuery();
            cr.populate(result);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.close();

        }
        return cr;

    }

    /**
     * Method used to update and insert information in the database
     * @param sql The SQL statement to execuste in the database
     */
    public static void updateSQL(String sql) throws SQLException{
        Connection dbConnection = null;

        try{
            dbConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            dbConnection.prepareStatement(sql).executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            dbConnection.close();
        }
    }

}

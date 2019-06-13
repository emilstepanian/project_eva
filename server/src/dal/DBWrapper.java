package dal;

import logic.misc.CustomLogger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kasper on 17/10/2016.
 * Class used to generate all possible SQL statements called from logic and send
 * them to any chosen database management system using SQL.
 */
public class DBWrapper {

    public DBWrapper(){
    }

    /**
     * A generic method that builds a SELECT String from the parameters that is passed to it.
     * This collapses every possible SELECT situation in the system into this one method.
     *
     * @param table The table that is wished to retrieve data from.
     * @param attributes A String Array with the attributes from the table that is wished returned. Enter 'null' if all attributes should be returned.
     * @param whereStmts A Map of Strings that contains the 'keys' and 'values' in the WHERE clause. e.g. "WHERE 'key' = 'value'". Leave 'null' if no WHERE clause is needed.
     * @param joinStmts A Map of Strings that contains 'keys' and 'values' in the JOIN clause. e.g. "JOIN key ON value". Leave 'null' if no JOIN clause is needed.
     * @return the data in a CachedRowSet.
     */
    public static CachedRowSet getRecords (String table, String[] attributes, Map whereStmts, Map joinStmts) {
        CachedRowSet records = null;

        String sql = "SELECT ";

        sql = appendAttributes(sql, attributes, table);


        if(joinStmts != null ){
            sql = joinOn(table, joinStmts, sql);
        }

        if(whereStmts != null){
            sql = buildWhere(whereStmts, sql);
        }

        sql += ";";
        try {
            records = MYSQLDriver.executeSQL(sql);


        } catch(SQLException ex){
            CustomLogger.log(ex, 2, ex.getMessage());

        }
        return records;
    }

    /**
     * This method is only used by getRecords() to build the first part of the SQL-statement.
     * Is there no specific attributes wished to be returned, return all
     * Else, it builds the attributes into the SQL-statement and returns it
     *
     * @param sql The current SQL-statement that is being build.
     * @param attributes A String Array with the attributes from the table that is wished returned. Enter 'null' if all attributes should be returned.
     * @param table The table that is wished to retrieve data from.
     * @return a String with the SQL-Statement, to the "FROM" clause.
     */
    private static String appendAttributes(String sql, String[] attributes, String table) {

        StringBuilder builder = new StringBuilder(sql);

        if(attributes == null) {

            builder.append(" * ");

        } else {

            for(int i=0;i<attributes.length;i++) {

                builder.append(attributes[i]);

                if (i != attributes.length -1){
                    builder.append(", ");
                }
            }


        }

        builder.append(" FROM ");
        builder.append(table);

        return builder.toString();
    }


    /**
     * @deprecated
     * IT IS UNUSED AND NOT WORKING. POSSIBLY REQUIRES FIXING IF A CLIENT IS
     * IMPLEMENTED DIFFERENTLY THAN eva_client.
     *
     * This method is only used by getRecords() to build the second part of the SQL-statement.
     * Is there no JOINs wished, this is ignored. Otherwise, it builds the "JOIN" clause on the SQL-Statement.
     *
     * @param joins A <code>Map of Strings</code> that contains 'keys' and 'values' in the JOIN clause. e.g. "JOIN key ON value". Leave 'null' if no JOIN clause is needed.
     * @param sql The current SQL-statement that is being build.
     * @return a String with the SQL-Statement, with the "JOIN" clause.
     */

    private static String joinOn(String table, Map<String, String> joins, String sql){

        StringBuilder builder = new StringBuilder(sql);

        if(!joins.isEmpty()){

            Iterator iterator = joins.entrySet().iterator();

            while(iterator.hasNext())
            {
                builder.append(" JOIN ");
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                builder.append(entry.getKey());
                builder.append(" ON ");
                builder.append(table);
                builder.append(".id");
                builder.append(" = ");
                builder.append(entry.getKey());
                builder.append(".");
                builder.append(entry.getValue());
            }

        }

        return builder.toString();
    }


    /**
     * This method is only used by getRecords() to build the third part of the SQL-statement.
     * Is there no "WHERE" clause wished, this is ignored. Otherwise, it builds the "WHERE" clause on the SQL-Statement.
     * @param params A Map of Strings that contains the 'keys' and 'values' in the WHERE clause. e.g. "WHERE 'key' = 'value'". Leave 'null' if no WHERE clause is needed.
     * @param sql The current SQL-statement that is being build.
     * @return a String with the SQL-Statement, with the "WHERE" clause.
     */
    private static String buildWhere(Map<String, String> params, String sql){

        StringBuilder builder = new StringBuilder(sql);

        if(!params.isEmpty()){

            builder.append(" WHERE ");

            Iterator iterator = params.entrySet().iterator();

            while(iterator.hasNext()){

                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                builder.append(entry.getKey());
                builder.append(" = ");
                builder.append("'");
                builder.append(entry.getValue());
                builder.append("'");

                if(iterator.hasNext()){
                    builder.append(" AND ");
                }
            }

        }
        return builder.toString();
    }


    /**
     * A generic method (don't confuse it with Java Generics) that builds a INSERT String from the parameters that is passed to it.
     * This collapses every possible INSERT situation in the system into this one method.
     * @param table The table that is wished to INSERT data into.
     * @param values A Map of Strings that contains 'keys' and 'values' in the INSERT clause. - "INSERT INTO (key) VALUES ("value")".
     */
    public static void insertIntoRecords(String table, Map<String, String> values) throws SQLException {

        String sql = "INSERT INTO ";
        StringBuilder builder = new StringBuilder();

        builder.append(sql);
        builder.append(table);
        builder.append(appendValues(values));
        builder.append(";");

        MYSQLDriver.updateSQL(builder.toString());
    }

    /**
     * This method is only used by insertIntoRecords() to build the VALUES clause.
     * @param values A Map of Strings that contains 'keys' and 'values' in the INSERT clause. - "INSERT INTO (key) VALUES ("value")".
     * @return a String with the SQL-Statement, with the "VALUES" clause.
     */
    private static String appendValues(Map<String, String> values){

        StringBuilder builder = new StringBuilder();
        builder.append(" (");

        for(Iterator iterator = values.entrySet().iterator(); iterator.hasNext();){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            builder.append(entry.getKey());

            if(iterator.hasNext()){
                builder.append(", ");
            }
        }

        builder.append(")");
        builder.append(" VALUES ");
        builder.append(" (");

        for(Iterator iterator = values.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            builder.append("'");
            builder.append(entry.getValue());
            builder.append("'");

            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }

        builder.append(")");
        return builder.toString();

    }

    /**
     * A generic method that builds a UPDATE String from the parameters that is passed to it.
     * This collapses every possible UPDATE situation in the system into this one method.
     * @param table The table that is wished to UPDATE data into.
     * @param fields A Map of Strings that contains 'keys' and 'values' in the UPDATE clause. - "SET 'key'='value'".
     * @param whereStmts A Map of Strings that contains the 'keys' and 'values' in the WHERE clause. e.g. "WHERE 'key' = 'value'". Leave 'null' if no WHERE clause is needed.
     */
    public static void updateRecords(String table, Map fields, Map whereStmts) {

        String sql = "UPDATE " + table;
        String updateString = createUpdateSQLStmt(sql, fields);
        StringBuilder builder = new StringBuilder(buildWhere(whereStmts, updateString));
        builder.append(";");


        try {

            MYSQLDriver.updateSQL(builder.toString());


        } catch(SQLException ex){
            CustomLogger.log(ex, 2, ex.getMessage());

        }
    }

    /**
     * This method is only used by updateRecords() to build the SET clause.
     * @param sql The current SQL-statement that is being build.
     * @param fields A Map of Strings that contains 'keys' and 'values' in the UPDATE clause. - "SET 'key'='value'".
     * @return a String with the SQL-Statement, with the "SET" clause.
     */
    private static String createUpdateSQLStmt(String sql, Map<String, String> fields) {
        StringBuilder builder = new StringBuilder(sql);

        builder.append(" SET ");

        for(Iterator iterator = fields.entrySet().iterator(); iterator.hasNext();){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            builder.append(entry.getKey());

            builder.append(" = ");

            builder.append("'");
            builder.append(entry.getValue());
            builder.append("'");



            if(iterator.hasNext()){
                builder.append(", ");
            }
        }


        return builder.toString();

    }


    /**
     * A generic method that builds a DELETE String from the parameters that is passed to it.
     * This collapses every possible DELETE situation in the system into this one method.
     * @param table The table that is wished to UPDATE data into.
     * @param whereStmts A Map of Strings that contains the 'keys' and 'values' in the WHERE clause. e.g. "WHERE 'key' = 'value'". Leave 'null' if no WHERE clause is needed.
     */
    public static void deleteRecords(String table, Map<String, String> whereStmts){
        String sql = "DELETE FROM " + table;
        StringBuilder builder = new StringBuilder(buildWhere(whereStmts, sql));
        builder.append(";");

        try {

            MYSQLDriver.updateSQL(builder.toString());


        } catch(SQLException ex){
            CustomLogger.log(ex, 2, ex.getMessage());
        }
    }



}

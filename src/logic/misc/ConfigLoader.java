package logic.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.util.Map;
import java.util.Set;

/**
 * ConfigLoader is used to load the config.file into the system.
 * The config.file also contains the names of the tables and columns of the database,
 * if a specific implementation of the server requires a similar database, however with other
 * table and column names
 */
public class ConfigLoader {

    /*
    System Settings
     */
    public static String SERVER_TITLE;
    public static String SERVER_VERSION;
    public static String DB_TYPE;
    public static String DB_HOST;
    public static String DB_PORT;
    public static String DB_NAME;
    public static String DB_USER;
    public static String DB_PASS;
    public static String CBS_API_LINK;
    public static String COURSES_JSON;
    public static String STUDY_DATA_JSON;
    public static String HASH_SALT;
    public static String ENCRYPT_KEY;
    public static String SERVER_ADDRESS;
    public static String SERVER_PORT;
    public static String DEBUG;
    public static String ENCRYPTION;
    public static String LANGUAGE;


    /*
    Table informations
     */
    public static String ID_COLUMN_OF_ALL_TABLES;

    //Table and column names of table containing users
    public static String USER_TABLE;
    public static String USER_FIRSTNAME_COLUMN;
    public static String USER_LASTNAME_COLUMN;
    public static String USER_CBSMAIL_COLUMN;
    public static String USER_TYPE_COLUMN;
    public static String USER_PASSWORD_COLUMN;
    public static String USER_TYPE_VALUE_STUDENT;
    public static String USER_TYPE_VALUE_TEACHER;
    public static String USER_TYPE_VALUE_ADMIN;

    //Table and column names of table containing courses
    public static String COURSE_TABLE;
    public static String COURSE_STUDY_ID_COLUMN;
    public static String COURSE_CODE_COLUMN;
    public static String COURSE_NAME_COLUMN;

    //Table and column names of table containing the connections between users and the courses they are attending:
    public static String COURSEATTENDANTS_TABLE;
    public static String COURSEATTENDANTS_USER_ID_COLUMN;
    public static String COURSEATTENDANTS_COURSE_ID_COLUMN;

    //Table and column names of table containing studies
    public static String STUDY_TABLE;
    public static String STUDY_SHORTNAME_COLUMN;
    public static String STUDY_NAME_COLUMN;

    //Table and column names of table containing reviews
    public static String REVIEW_TABLE;
    public static String REVIEW_USER_ID_COLUMN;
    public static String REVIEW_LECTURE_ID_COLUMN;
    public static String REVIEW_RATING_COLUMN;
    public static String REVIEW_COMMENT_COLUMN;
    public static String REVIEW_IS_DELETED_COLUMN;
    public static String REVIEW_IS_DELETED_VALUE_FALSE;
    public static String REVIEW_IS_DELETED_VALUE_TRUE;

    //Table and column names of table containing lectures
    public static String LECTURE_TABLE;
    public static String LECTURE_COURSE_CODE_COLUMN;
    public static String LECTURE_START_DATE_COLUMN;
    public static String LECTURE_END_DATE_COLUMN;
    public static String LECTURE_TYPE_COLUMN;
    public static String LECTURE_LOCATION_COLUMN;
    public static String LECTURE_DESCRIPTION_COLUMN;

    //Attributes specific to CBS' API
    public static String CBS_STUDY_SHORTNAME;
    public static String CBS_STUDY_STUDY_NAME;



    /**
     * Not more than one ConfigLoader can be instantiated, why it is a SINGLETON.
     * The ConfigLoader HAS to be instantiated, before the static variables can be called.
     */
    private static final ConfigLoader SINGLETON = new ConfigLoader();

    public ConfigLoader getInstance() {
        return SINGLETON;
    }

    /*
      Parses the config as it is instantiated at initialization.
     */
    private ConfigLoader() {
        parseConfig();
    }

    /*
      Parses the config.file into the system
     */
    public static void parseConfig() {
        JsonParser jparser = new JsonParser();
        JsonReader jsonReader;

        try {
            jsonReader = new JsonReader(new FileReader("config.json"));
            JsonObject jsonObject = jparser.parse(jsonReader).getAsJsonObject();

            Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();

            for (Map.Entry<String, JsonElement> entry : entries) {
                try {
                    ConfigLoader.class.getDeclaredField(entry.getKey()).set(SINGLETON, entry.getValue().getAsString());

                } catch (Exception ex) {
                    CustomLogger.log(ex, 2, ex.getMessage());

                }
            }

            /*
            Parses the chosen language of the system
             */
            I18NLoader.parseLanguage();

        } catch (Exception ex) {
            ex.printStackTrace();
            CustomLogger.log(ex, 3, ex.getMessage());
        }
    }
}


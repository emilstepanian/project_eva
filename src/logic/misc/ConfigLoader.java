package logic.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.util.Map;
import java.util.Set;

/**
 * ConfigLoader is used to load the config.file into the system
 */
public class ConfigLoader {

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

    /**
     * Not more than one ConfigLoader can be instantiated, why it is a SINGLETON.
     * The ConfigLoader HAS to be instantiated, before the static variables can be called.
     *
     */
    private static final ConfigLoader SINGLETON = new ConfigLoader();

    public ConfigLoader getInstance() {
        return SINGLETON;
    }

    /**
     * Parses the config as it is instantiated at initialization.
     */
    private ConfigLoader() {
        parseConfig();
    }

    /**
     * Parses the config.file into the system
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

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


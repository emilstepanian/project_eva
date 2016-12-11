package logic.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.util.Map;
import java.util.Set;

/**
 * Created by emilstepanian on 26/11/2016.
 * I18NLoader is used to load the chosen i18n language file into the system.
 * Every possible system message in the console is referencing these variables,
 * to easily be able to change the language of the server. If a new language is
 * desired, simply copy and translate the i18n_GB.dist.json file and rename it to your
 * country's abbreviation and specify it in the config.json file.
 */
public class I18NLoader {


    public static String IS_SUCCESFULLY_REGISTERED;
    public static String COULD_NOT_BE_REGISTERED;
    public static String AN_ERROR_HAS_OCCURRED;
    public static String REVERTING_TO_MAINMENU;

    public static String COULD_NOT_RETRIEVE_COURSES;
    public static String COULD_NOT_RETRIEVE_LECTURES;
    public static String COULD_NOT_RETRIEVE_REVIEWS;
    public static String COULD_NOT_SOFT_DELETE_REVIEW_WITH_REVIEWID;
    public static String COULD_NOT_ADD_REVIEW;

    //ClientEndpoint messages
    public static String USER_LOGIN_DENIED;
    public static String FAILED_RESOURCE_NOT_FOUND;
    public static String REVIEW_DELETED;
    public static String REVIEW_NOT_DELETED;

    //Logger messages
    public static String FINEST_ERROR;
    public static String FINE_ERROR;
    public static String SEVERE_ERROR;

    //Statistics messages
    public static String COURSE_ATTENDANTS_MSG;
    public static String REVIEW_PARTICIPATION_MSG;
    public static String LECTURE_AVERAGE_MSG;
    public static String COURSE_AVERAGE_MSG;


    //Single words
    public static String MESSAGE_WORD;
    public static String PRESS_WORD;
    public static String REGISTER_WORD;
    public static String STUDENT_WORD;
    public static String TEACHER_WORD;
    public static String ADMIN_WORD;
    public static String REGISTERING_WORD;
    public static String ENTER_WORD;
    public static String PASSWORD_WORD;
    public static String MAIL_WORD;
    public static String LOCATION_WORD;
    public static String DATE_WORD;
    public static String TO_WORD;
    public static String YES_WORD;
    public static String NO_WORD;
    public static String RATING_WORD;
    public static String COMMENT_WORD;
    public static String RUNNING_WORD;
    public static String VISIT_WORD;
    public static String USER_WORD;
    public static String REVIEW_WORD;
    public static String REVIEWS_WORD;
    public static String LECTURE_WORD;
    public static String ID_ABBREVIATION;



    //AdminView messages
    public static String TO_REGISTER_A_NEW_USER;
    public static String TO_ASSIGN_COURSES_TO_A_USER;
    public static String TO_DELETE_A_USER;
    public static String TO_DELETE_A_REVIEW;
    public static String TO_LOG_OUT;
    public static String TO_ASSIGN_COURSES_BY_STUDY;
    public static String TO_ASSIGN_A_SINGLE_COURSE;
    public static String YOU_ARE_LOGGED_IN_AS;
    public static String YOU_HAVE_CHOSEN_STUDENT;
    public static String YOU_HAVE_CHOSEN_STUDY;
    public static String YOU_HAVE_CHOSEN_USER;
    public static String YOU_HAVE_CHOSEN_COURSE;
    public static String YOU_WANT_TO_ASSIGN_THE_COURSES_FOR;
    public static String YOU_WANT_TO_DELETE;
    public static String YOU_WANT_TO_DELETE_REVIEW;
    public static String LOGGING_OUT;
    public static String WRONG_INPUT_TRY_AGAIN;
    public static String FIRST_NAME;
    public static String LAST_NAME;
    public static String INVALID_PASSWORD_TRY_AGAIN;
    public static String SCHOOL_ABBREVIATION;
    public static String ID_OF_THE_STUDENT;
    public static String ID_OF_THE_STUDY;
    public static String ID_OF_THE_USER;
    public static String ID_OF_THE_COURSE;
    public static String ID_OF_THE_USER_TO_DELETE;
    public static String ID_OF_THE_REVIEW_TO_DELETE;
    public static String COURSES_SUCCESSFULLY_ASSIGNED;
    public static String INVALID_INPUT;
    public static String DID_NOT_ASSIGN_COURSES;
    public static String INVALID_KEY_PRESSED;
    public static String USER_SUCCESFULLY_DELETED;
    public static String DO_YOU_KNOW_THE_ID_OF_THE;
    public static String IF_YOU_DO;
    public static String IF_YOU_DO_NOT;
    public static String NAVIGATE_TO_THE;
    public static String ENTER_THE_ID_OF_THE_COURSE_CONTAINING_THE_LECTURE_AND_SPECIFIC_REVIEW;


    //MainView messages
    public static String TO_LOG_IN_AS_ADMIN;
    public static String TO_STOP_SERVER;
    public static String SERVER_SHUTTING_DOWN;
    public static String SERVER_STOPPED;
    public static String LOGIN_GRANTED;
    public static String LOGGING_IN;
    public static String ADMIN_NOT_FOUND;
    public static String USER_NOT_ADMIN;


    /**
     * Not more than one ConfigLoader can be instantiated, why it is a SINGLETON.
     * The ConfigLoader HAS to be instantiated, before the static variables can be called.
     */
    private static final I18NLoader SINGLETON = new I18NLoader();

    public I18NLoader getInstance() {
        return SINGLETON;
    }

    /*
      Parses the language as it is instantiated at ConfigLoader.
     */
    private I18NLoader() {
        parseLanguage();
    }

    /*
      Parses the chosen i18n.file into the system
     */
    public static void parseLanguage() {
        JsonParser jparser = new JsonParser();
        JsonReader jsonReader;

        try {
            jsonReader = new JsonReader(new FileReader("resources/i18n/" + "i18n_" + ConfigLoader.LANGUAGE + ".json"));
            JsonObject jsonObject = jparser.parse(jsonReader).getAsJsonObject();

            Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();

            for (Map.Entry<String, JsonElement> entry : entries) {
                try {
                    I18NLoader.class.getDeclaredField(entry.getKey()).set(SINGLETON, entry.getValue().getAsString());

                } catch (Exception ex) {
                    CustomLogger.log(ex, 2, ex.getMessage());


                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            CustomLogger.log(ex, 2, ex.getMessage());

        }
    }

}

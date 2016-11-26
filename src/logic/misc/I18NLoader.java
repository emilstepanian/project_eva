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
 */
public class I18NLoader {


    public static String IS_SUCCESFULLY_REGISTERED; //add space //ad \n
    public static String COULD_NOT_BE_REGISTERED;// " could not be registered"
    public static String AN_ERROR_HAS_OCCURRED;  //"An error has occured."
    public static String REVERTING_TO_MAINMENU; //"\nReverting back to main menu...\n"

    public static String COULD_NOT_RETRIEVE_COURSES; //Could not retrieve courses at getCourses();
    public static String COULD_NOT_RETRIEVE_LECTURES; // Could not retrieve lectures at getLectures()
    public static String COULD_NOT_RETRIEVE_REVIEWS; //Could not retrieve reviews from getReviews()
    public static String COULD_NOT_SOFT_DELETE_REVIEW_WITH_REVIEWID;//"(softDeleteReview() could not soft delete review with reviewId

    //ClientEndpoint messages
    public static String USER_LOGIN_DENIED; //User login denied
    public static String FAILED_RESOURCE_NOT_FOUND; //Failed. Resource not found
    public static String REVIEW_DELETED; //Review successfully deleted
    public static String REVIEW_NOT_DELETED; //Review did not successfully get deleted. Internal server error

    //Logger messages
    public static String FINEST_ERROR; //finest error
    public static String FINE_ERROR; //fine error
    public static String SEVERE_ERROR; //severe error

    //Statistics messages
    public static String COURSE_ATTENDANTS_MSG; //Course Attendants
    public static String REVIEW_PARTICIPATION_MSG; //Review Participation
    public static String LECTURE_AVERAGE_MSG; //Lecture Average
    public static String COURSE_AVERAGE_MSG; //Course Average


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
    public static String TO_WORD;
    public static String YES_WORD;
    public static String NO_WORD;
    public static String RATING_WORD;
    public static String COMMENT_WORD;
    public static String RUNNING_WORD; //running
    public static String VISIT_WORD;



    //AdminView messages
    public static String TO_REGISTER_A_NEW_USER;//"\n to register a new user"
    public static String TO_ASSIGN_COURSES_TO_A_USER; //to assign courses to a user
    public static String TO_DELETE_A_USER; //to delete a user
    public static String TO_DELETE_A_REVIEW; //to delete a review
    public static String TO_LOG_OUT; //to log out
    public static String TO_ASSIGN_COURSES_BY_STUDY; //to assign courses by study (used for students)
    public static String TO_ASSIGN_A_SINGLE_COURSE; //to assign a single course (used for teachers)
    public static String YOU_ARE_LOGGED_IN_AS; //You are logged in as
    public static String YOU_HAVE_CHOSEN_STUDENT; //You have chosen student
    public static String YOU_HAVE_CHOSEN_STUDY; //You have chosen Study
    public static String YOU_HAVE_CHOSEN_USER; //You have chosen user
    public static String YOU_HAVE_CHOSEN_COURSE; //You have chosen course
    public static String YOU_WANT_TO_ASSIGN_THE_COURSES_FOR; //Are you sure you want to assign the courses for
    public static String YOU_WANT_TO_DELETE; //are you sure you want to delete
    public static String YOU_WANT_TO_DELETE_REVIEW; //Are you sure, you want to delete review
    public static String LOGGING_OUT; //logging out
    public static String WRONG_INPUT_TRY_AGAIN; //Wrong input, try again
    public static String FIRST_NAME; //Enter first name
    public static String LAST_NAME;
    public static String INVALID_PASSWORD_TRY_AGAIN; //Invalid password. Please try again
    public static String SCHOOL_ABBREVIATION; //CBS
    public static String ID_OF_THE_STUDENT; //ID of the student
    public static String ID_OF_THE_STUDY; //ID of the study
    public static String ID_OF_THE_USER; //ID of the user
    public static String ID_OF_THE_COURSE; //ID of the course
    public static String ID_OF_THE_USER_TO_DELETE; //ID of the user you want to delete
    public static String ID_OF_THE_REVIEW_TO_DELETE; //ID of the review you want to delete
    public static String COURSES_SUCCESSFULLY_ASSIGNED; //Courses successfully assigned
    public static String INVALID_INPUT; //Invalid input
    public static String DID_NOT_ASSIGN_COURSES; //Did not assign courses
    public static String INVALID_KEY_PRESSED; //Invalid key pressed
    public static String USER_SUCCESFULLY_DELETED; //User successfully deleted


    //MainView messages
    public static String TO_LOG_IN_AS_ADMIN; //to log in as admin
    public static String TO_STOP_SERVER; //to stop server
    public static String SERVER_SHUTTING_DOWN; //Server shutting down
    public static String SERVER_STOPPED; //Server stopped
    public static String LOGIN_GRANTED; //Log in granted
    public static String LOGGING_IN; //Logging in
    public static String ADMIN_NOT_FOUND; //Admin not found
    public static String USER_NOT_ADMIN; //User not admin






    /**
     * Not more than one ConfigLoader can be instantiated, why it is a SINGLETON.
     * The ConfigLoader HAS to be instantiated, before the static variables can be called.
     *
     */
    private static final I18NLoader SINGLETON = new I18NLoader();

    public I18NLoader getInstance() {
        return SINGLETON;
    }

    /**
     * Parses the config as it is instantiated at initialization.
     */
    private I18NLoader() {
        parseLanguage();
    }

    /**
     * Parses the config.file into the system
     */
    public static void parseLanguage() {
        JsonParser jparser = new JsonParser();
        JsonReader jsonReader;

        try {
            jsonReader = new JsonReader(new FileReader("resources/i18n/" + ConfigLoader.LANGUAGE));
            JsonObject jsonObject = jparser.parse(jsonReader).getAsJsonObject();

            Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();

            for (Map.Entry<String, JsonElement> entry : entries) {
                try {
                    I18NLoader.class.getDeclaredField(entry.getKey()).set(SINGLETON, entry.getValue().getAsString());

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

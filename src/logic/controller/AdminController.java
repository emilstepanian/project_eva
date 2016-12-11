package logic.controller;

import dal.DBWrapper;
import logic.misc.ConfigLoader;
import logic.misc.CustomLogger;
import logic.misc.I18NLoader;
import model.entity.Course;
import model.entity.Review;
import model.entity.Study;
import model.user.User;
import security.Digester;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emilstepanian on 19/11/2016.
 * Class extends UserController and controls an admin's functionality in the server,
 * and adds any specific logic for the admin
 */
public class AdminController extends UserController {

    /**
     * Creates a new user.
     * @param newUser A User object containing information about the new user.
     */
    public void createUser(User newUser){

        try {

            /*
            Check's whether the email is already used in the database,
            before registering the new user
             */

            if(userExists(newUser.getCbsMail()) == false){

                Map<String, String> userInfo = new HashMap<String, String>();

                userInfo.put(ConfigLoader.USER_FIRSTNAME_COLUMN, newUser.getFirstName());
                userInfo.put(ConfigLoader.USER_LASTNAME_COLUMN, newUser.getLastName());
                userInfo.put(ConfigLoader.USER_CBSMAIL_COLUMN, newUser.getCbsMail());
                userInfo.put(ConfigLoader.USER_PASSWORD_COLUMN, Digester.hash(Digester.hash(newUser.getPassword())));
                userInfo.put(ConfigLoader.USER_TYPE_COLUMN, newUser.getType());

                DBWrapper.insertIntoRecords(ConfigLoader.USER_TABLE, userInfo);
                System.out.println(newUser.getFirstName() + " " + newUser.getLastName() + I18NLoader.IS_SUCCESFULLY_REGISTERED);
            } else {
                System.out.println(newUser.getFirstName() + " " + newUser.getLastName() + I18NLoader.COULD_NOT_BE_REGISTERED);
                catchMessage(null);

            }


        } catch(SQLException ex){

            System.out.println(newUser.getFirstName() + " " + newUser.getLastName() + I18NLoader.COULD_NOT_BE_REGISTERED);
            catchMessage(ex);

        }



    }

    /**
     * Assigns a study's courses to a user.
     * @param userId The user ID who is assigning
     * @param studyId The study ID the user is assigning to.
     */
    public void assignStudy(int userId, int studyId){
        try {
            Map<String, String> whereParams = new HashMap<String, String>();

            whereParams.put(ConfigLoader.COURSE_STUDY_ID_COLUMN, String.valueOf(studyId));
            String[] attributes = {ConfigLoader.ID_COLUMN_OF_ALL_TABLES};

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.COURSE_TABLE, attributes, whereParams, null);

            while(rowSet.next()){
                assignSingleCourse(userId, rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            }

        } catch(Exception ex){
            catchMessage(ex);

        }


    }

    /**
     * Assigns a single course to a user. Typically used for teachers,
     * as they do not sign up to whole studies.
     * @param userId The user ID who is assigning
     * @param courseId The course ID the user is assigning to
     */
    public void assignSingleCourse(int userId, int courseId){

        try {
            Map<String, String> updateParams = new HashMap<String, String>();
            updateParams.put(ConfigLoader.COURSEATTENDANTS_USER_ID_COLUMN, String.valueOf(userId));
            updateParams.put(ConfigLoader.COURSEATTENDANTS_COURSE_ID_COLUMN, String.valueOf(courseId));
            DBWrapper.insertIntoRecords(ConfigLoader.COURSEATTENDANTS_TABLE, updateParams);

        } catch(SQLException ex){
            catchMessage(ex);

        }

    }

    /**
     * Deletes a user from the system by first deleting all records in the course_attendant table regarding the user,
     * and then all reviews in the review table
     * @param userId The ID of the user who needs to get deleted
     */
    public void deleteUser(int userId){
        try {
            //First, delete from course_attendant table
            Map<String, String> courseAttendantWhereParam = new HashMap<String, String>();
            courseAttendantWhereParam.put(ConfigLoader.COURSEATTENDANTS_USER_ID_COLUMN, String.valueOf(userId));
            DBWrapper.deleteRecords(ConfigLoader.COURSEATTENDANTS_TABLE, courseAttendantWhereParam);

            //Next, delete any reviews written by the user from review table
            Map<String, String> reviewsWhereParam = new HashMap<String, String>();
            reviewsWhereParam.put(ConfigLoader.REVIEW_USER_ID_COLUMN, String.valueOf(userId));
            DBWrapper.deleteRecords(ConfigLoader.REVIEW_TABLE, reviewsWhereParam);

            //Ultimately, delete from user table
            Map<String, String > userWhereParam = new HashMap<String, String>();
            userWhereParam.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(userId));
            DBWrapper.deleteRecords(ConfigLoader.USER_TABLE, userWhereParam);

        } catch(Exception ex){

            catchMessage(ex);
        }

    }



    /**
     * Returns a single record of a specified object type
     * @param id ID of the specified object
     * @param modification '1' for User object,
     *                     '2' for Study object,
     *                     '3' for Course object,
     *                     '4' for Review object
     * @return Returns the record in the specified object type.
     */
    public Object getSingleRecord(int id, int modification){
        switch (modification){
            case 1:
                return getSingleUser(id);
            case 2:
                return getSingleStudy(id);
            case 3:
                return getSingleCourse(id);
            case 4:
                return getSingleReview(id);
            default:
                return null;

        }
    }


    /**
     * Used by getSingleRecord() to return a User
     * @param userId ID of the user to return
     * @return the specified User.
     */
    private User getSingleUser(int userId){
        User user = new User();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(userId));

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.USER_TABLE, null, whereParam, null);
            rowSet.next();
            user.setId(rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            user.setFirstName(rowSet.getString(ConfigLoader.USER_FIRSTNAME_COLUMN));
            user.setLastName(rowSet.getString(ConfigLoader.USER_LASTNAME_COLUMN));
            user.setCbsMail(rowSet.getString(ConfigLoader.USER_CBSMAIL_COLUMN));
            user.setType(rowSet.getString(ConfigLoader.USER_TYPE_COLUMN));

        } catch(SQLException ex){
            catchMessage(ex);

        }
        return user;
    }

    /**
     * Used by getSingleRecord() to return a Study
     * @param studyId ID of the study to return
     * @return the specified Study.
     */
    private Study getSingleStudy(int studyId){
            Study study = new Study();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(studyId));

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.STUDY_TABLE, null, whereParam, null);
            rowSet.next();
            study.setId(rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            study.setShortname(rowSet.getString(ConfigLoader.STUDY_SHORTNAME_COLUMN));
            study.setName(rowSet.getString(ConfigLoader.STUDY_NAME_COLUMN));

        } catch(SQLException ex){
            catchMessage(ex);

        }
        return study;
    }

    /**
     * Used by getSingleRecord() to return a Study
     * @param courseId ID of the course to return
     * @return the specified Course.
     */
    private Course getSingleCourse(int courseId){
        Course course = new Course();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(courseId));

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.COURSE_TABLE, null, whereParam, null);
            rowSet.next();
            course.setId(rowSet.getString(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            course.setCode(rowSet.getString(ConfigLoader.COURSE_CODE_COLUMN));
            course.setDisplaytext(rowSet.getString(ConfigLoader.COURSE_NAME_COLUMN));


        } catch(SQLException ex){
            catchMessage(ex);


        }
        return course;
    }

    /**
     * Used by getSingleRecord() to return a Review.
     * @param reviewId ID of the review to return
     * @return the specified Review
     */
    private Review getSingleReview(int reviewId){
        Review review = new Review();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(reviewId));

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.REVIEW_TABLE, null, whereParam, null);
            rowSet.next();
            review.setId(rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            review.setUserId(rowSet.getInt(ConfigLoader.REVIEW_USER_ID_COLUMN));
            review.setLectureId(rowSet.getInt(ConfigLoader.REVIEW_LECTURE_ID_COLUMN));
            review.setRating(rowSet.getInt(ConfigLoader.REVIEW_RATING_COLUMN));
            review.setComment(rowSet.getString(ConfigLoader.REVIEW_COMMENT_COLUMN));


        } catch(SQLException ex){
            catchMessage(ex);

        }
        return review;
    }


    /**
     * Checks whether a User exists or does not in the database
     * @param cbs_mail the CBS Mail address of the user
     * @return a boolean value indicating if user exists or not.
     */
    private boolean userExists(String cbs_mail){
        Boolean userExists = false;

        Map<String, String> whereParam = new HashMap<String, String>();
        whereParam.put(ConfigLoader.USER_CBSMAIL_COLUMN, cbs_mail);

        try {

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.USER_TABLE, null, whereParam, null);

            if(rowSet.size() != 0){
                userExists = true;
            }
        } catch(Exception ex){

            catchMessage(ex);

        }
        return userExists;
    }


    /**
     * Catches and prints any exceptions thrown together with convenient "view" information for the admin
     * @param ex the exception thrown
     */
    private void catchMessage(Exception ex){
        System.out.println(I18NLoader.AN_ERROR_HAS_OCCURRED);
        CustomLogger.log(ex, 2, ex.getMessage());
        System.out.println("\n" + I18NLoader.REVERTING_TO_MAINMENU + "\n");

    }

}

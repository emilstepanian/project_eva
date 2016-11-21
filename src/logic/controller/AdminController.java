package logic.controller;

import dal.DBWrapper;
import model.entity.Course;
import model.entity.Lecture;
import model.entity.Review;
import model.entity.Study;
import model.user.User;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class AdminController extends UserController {

    /**
     * Creates a new user.
     * @param newUser A User object containing information about the new user.
     */
    public void createUser(User newUser){

        try {

            if(userExists(newUser.getCbsMail()) == false){

                Map<String, String> userInfo = new HashMap<String, String>();

                userInfo.put("firstName", newUser.getFirstName());
                userInfo.put("lastName", newUser.getLastName());
                userInfo.put("cbs_mail", newUser.getCbsMail());
                userInfo.put("password", newUser.getPassword());
                userInfo.put("type", newUser.getType());

                DBWrapper.insertIntoRecords("user", userInfo);
                System.out.println(newUser.getFirstName() + " " + newUser.getLastName() + " is successfully registered.\n");
            } else {
                System.out.println("An error has occurred.");
                System.out.println(newUser.getFirstName() + " " + newUser.getLastName() + " could not be registered");
                System.out.println("\nReverting back to main menu...\n");

            }


        } catch(SQLException ex){

            System.out.println("An error has occured.");
            System.out.println(newUser.getFirstName() + " " + newUser.getLastName() + " could not be registered");
            System.out.println(ex.getMessage());
            System.out.println("\nReverting back to main menu...\n");

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

            whereParams.put("study_id", String.valueOf(studyId));
            String[] attributes = {"id"};

            CachedRowSet rowSet = DBWrapper.getRecords("course", attributes, whereParams, null);

            while(rowSet.next()){
                assignSingleCourse(userId, rowSet.getInt("id"));
            }

        } catch(Exception ex){

            System.out.println(ex.getMessage());

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
            updateParams.put("user_id", String.valueOf(userId));
            updateParams.put("course_id", String.valueOf(courseId));
            DBWrapper.insertIntoRecords("course_attendant", updateParams);

        } catch(SQLException ex){
            System.out.println(ex.getMessage());

        }

    }

    /**
     * Soft deletes a user.
     * @param userId The ID of the user who needs to get deleted
     */
    public void deleteUser(int userId){
        try {
            //First, delete from course_attendant table
            Map<String, String> courseAttendantWhereParam = new HashMap<String, String>();
            courseAttendantWhereParam.put("user_id", String.valueOf(userId));
            DBWrapper.deleteRecords("course_attendant", courseAttendantWhereParam);

            //Next, delete from user table
            Map<String, String > userWhereParam = new HashMap<String, String>();
            userWhereParam.put("id", String.valueOf(userId));
            DBWrapper.deleteRecords("user", userWhereParam);

        } catch(Exception ex){

            System.out.println(ex.getMessage());
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
     * @return Returns the specified User.
     */
    private User getSingleUser(int userId){
        User user = new User();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put("id", String.valueOf(userId));

            CachedRowSet rowSet = DBWrapper.getRecords("user", null, whereParam, null);
            rowSet.next();
            user.setId(rowSet.getInt("id"));
            user.setFirstName(rowSet.getString("firstName"));
            user.setLastName(rowSet.getString("lastName"));
            user.setCbsMail(rowSet.getString("cbs_mail"));
            user.setType(rowSet.getString("type"));

        } catch(SQLException ex){
            System.out.println(ex.getMessage());

        }
        return user;
    }

    /**
     * Used by getSingleRecord() to return a Study
     * @param studyId ID of the study to return
     * @return Returns the specified Study.
     */
    private Study getSingleStudy(int studyId){
            Study study = new Study();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put("id", String.valueOf(studyId));

            CachedRowSet rowSet = DBWrapper.getRecords("study", null, whereParam, null);
            rowSet.next();
            study.setId(rowSet.getInt("id"));
            study.setShortname(rowSet.getString("shortname"));
            study.setName(rowSet.getString("name"));

        } catch(SQLException ex){
            System.out.println(ex.getMessage());

        }
        return study;
    }

    /**
     * Used by getSingleRecord() to return a Study
     * @param courseId ID of the course to return
     * @return Returns the specified Course.
     */
    private Course getSingleCourse(int courseId){
        Course course = new Course();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put("id", String.valueOf(courseId));

            CachedRowSet rowSet = DBWrapper.getRecords("course", null, whereParam, null);
            rowSet.next();
            course.setId(rowSet.getString("id"));
            //course.setEvents(rowSet.getString("shortname"));
            course.setCode(rowSet.getString("code"));
            course.setDisplaytext(rowSet.getString("name"));


        } catch(SQLException ex){
            System.out.println(ex.getMessage());

        }
        return course;
    }

    /**
     * Used by getSingleRecord() to return a Review.
     * @param reviewId ID of the review to return
     * @return Returns the specified Review
     */
    private Review getSingleReview(int reviewId){
        Review review = new Review();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put("id", String.valueOf(reviewId));

            CachedRowSet rowSet = DBWrapper.getRecords("review", null, whereParam, null);
            rowSet.next();
            review.setId(rowSet.getInt("id"));
            review.setUserId(rowSet.getInt("user_id"));
            review.setLectureId(rowSet.getInt("lecture_id"));
            review.setRating(rowSet.getInt("rating"));
            review.setComment(rowSet.getString("comment"));


        } catch(SQLException ex){
            System.out.println(ex.getMessage());

        }
        return review;
    }


    /**
     * Checks whether a User exists or does not in the database
     * @param cbs_mail the CBS Mail address of the user
     * @return Returns a boolean value indicating if user exists or not.
     */
    private boolean userExists(String cbs_mail){
        Boolean userExists = false;

        Map<String, String> whereParam = new HashMap<String, String>();
        whereParam.put("cbs_mail", cbs_mail);

        try {

            CachedRowSet rowSet = DBWrapper.getRecords("user", null, whereParam, null);

            if(rowSet.size() != 0){
                userExists = true;
            }
        } catch(Exception ex){

            System.out.println(ex.getMessage());
        }
        return userExists;
    }

}

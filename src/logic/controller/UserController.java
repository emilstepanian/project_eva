package logic.controller;

import dal.DBWrapper;
import logic.misc.ConfigLoader;
import logic.misc.CustomLogger;
import logic.misc.I18NLoader;
import model.entity.Course;
import model.entity.Lecture;
import model.entity.Review;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emilstepanian on 21/11/2016.
 */
public abstract class UserController {


    /**
     * Retrieves courses a specific user is assigned to.
     * @param userId ID of the user that retrieves the courses.
     * @return ArrayList containing Course objects.
     */
    public ArrayList<Course> getCourses(int userId) {

        ArrayList<Course> courses = new ArrayList<Course>();
        try {

            Map<String, String> whereParams = new HashMap<String, String>();
            Map<String, String> joins = new HashMap<String, String>();

            whereParams.put(ConfigLoader.COURSEATTENDANTS_TABLE + "." + ConfigLoader.COURSEATTENDANTS_USER_ID_COLUMN, String.valueOf(userId));
            joins.put(ConfigLoader.COURSEATTENDANTS_TABLE, ConfigLoader.COURSEATTENDANTS_COURSE_ID_COLUMN);


            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.COURSE_TABLE, null, whereParams, joins);

            while (rowSet.next()){
                Course course = new Course();

                course.setDisplaytext(rowSet.getString(ConfigLoader.COURSE_NAME_COLUMN));
                course.setCode(ConfigLoader.COURSE_CODE_COLUMN);
                course.setEvents(getLectures(course.getCode()));

                courses.add(course);
            }

        } catch(SQLException ex){
            System.out.println(ex.getMessage());
            CustomLogger.log(ex,2, I18NLoader.COULD_NOT_RETRIEVE_COURSES);
        }

        return courses;
    }


    /**
     * Parses lectures into a course's Lecture-Array.
     * @param courseCode The coursecode(code) for the course
     * @return Array with lectures
     */
    public Lecture[] getLectures(String courseCode) {
        ArrayList<Lecture> lectureArrayList = new ArrayList<Lecture>();
        Lecture[] lectureArray = null;

        try {
            Map<String,String> whereParams = new HashMap<String, String>();

            whereParams.put(ConfigLoader.LECTURE_COURSE_ID_COLUMN, courseCode);

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.LECTURE_TABLE, null, whereParams, null);

            while (rowSet.next()){
                Lecture lecture = new Lecture();

                lecture.setLectureId(rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
                lecture.setCourseId(rowSet.getString(ConfigLoader.LECTURE_COURSE_ID_COLUMN));
                lecture.setStartDate(rowSet.getTimestamp(ConfigLoader.LECTURE_START_DATE_COLUMN));
                lecture.setEndDate(rowSet.getTimestamp(ConfigLoader.LECTURE_END_DATE_COLUMN));
                lecture.setType(rowSet.getString(ConfigLoader.LECTURE_TYPE_COLUMN));
                lecture.setLocation(rowSet.getString(ConfigLoader.LECTURE_LOCATION_COLUMN));
                lecture.setDescription(rowSet.getString(ConfigLoader.LECTURE_DESCRIPTION_COLUMN));

                lectureArrayList.add(lecture);
            }

            lectureArray = lectureArrayList.toArray(new Lecture[lectureArrayList.size()]);

        } catch (SQLException ex){
            System.out.println(ex.getMessage());
            CustomLogger.log(ex, 2, I18NLoader.COULD_NOT_RETRIEVE_LECTURES);
        }

        return lectureArray;
    }

    /**
     * Retrieves reviews for a specific lecture
     * @param lectureId ID of the lecture
     * @return ArrayList containing Review objects
     */
    public ArrayList<Review> getLectureReviews(int lectureId) {
        Map<String, String> whereParams = new HashMap<String, String>();
        whereParams.put(ConfigLoader.REVIEW_LECTURE_ID_COLUMN, String.valueOf(lectureId));

        return getReviews(whereParams);
    }


    /**
     * Retrieves reviews for any given WHERE parameters.
     * @param whereParams the WHERE parameter wished to match on.
     * @return ArrayList containing Review objects
     */
    protected ArrayList<Review> getReviews(Map<String, String> whereParams){
        ArrayList<Review> reviews = new ArrayList<Review>();

        try {

            whereParams.put(ConfigLoader.REVIEW_IS_DELETED_COLUMN, ConfigLoader.REVIEW_IS_DELETED_VALUE_FALSE);
            String[] attributes = {ConfigLoader.ID_COLUMN_OF_ALL_TABLES, ConfigLoader.REVIEW_USER_ID_COLUMN,
                    ConfigLoader.REVIEW_LECTURE_ID_COLUMN, ConfigLoader.REVIEW_RATING_COLUMN, ConfigLoader.REVIEW_COMMENT_COLUMN};

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.REVIEW_TABLE, attributes, whereParams, null);

            while (rowSet.next()) {
                Review review = new Review();
                review.setId(rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
                review.setUserId(rowSet.getInt(ConfigLoader.REVIEW_USER_ID_COLUMN));
                review.setLectureId(rowSet.getInt(ConfigLoader.REVIEW_LECTURE_ID_COLUMN));
                review.setRating(rowSet.getInt(ConfigLoader.REVIEW_RATING_COLUMN));
                review.setComment(rowSet.getString(ConfigLoader.REVIEW_COMMENT_COLUMN));

                reviews.add(review);
            }

        } catch(SQLException ex){
            System.out.println(ex.getMessage());
            CustomLogger.log(ex, 2, I18NLoader.COULD_NOT_RETRIEVE_REVIEWS);

        }

        return reviews;
    }


    /**
     * Soft deletes a specified review. Can be used in two situations.
     * Method used when a student wishes to delete his own review,
     * or when a teacher or an admin wishes to delete any review.
     * @param userId Used for first use-case. The specific student's userId. Pass '0' if teacher or admin.
     * @param reviewId The review ID of the review that is wished deleted.
     * @return Boolean value indicating whether review is deleted succesfully or not.
     */
    public boolean softDeleteReview(int userId, int reviewId) {
        boolean isSoftDeleted = false;

        try {
            Map<String, String> isDeletedField = new HashMap<String, String>();

            isDeletedField.put(ConfigLoader.REVIEW_IS_DELETED_COLUMN, ConfigLoader.REVIEW_IS_DELETED_VALUE_TRUE);

            Map<String, String> whereParams = new HashMap<String, String>();

            /*
            If-statement added to be able to use method in multiple situations.
            If Admin is deleting, he simply passes 0 as reviewId parameter.
             */
            if(userId != 0) {
                whereParams.put(ConfigLoader.REVIEW_USER_ID_COLUMN, String.valueOf(userId));
            }

            whereParams.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(reviewId));

            DBWrapper.updateRecords(ConfigLoader.REVIEW_TABLE, isDeletedField, whereParams);
            isSoftDeleted = true;
        } catch(Exception ex){

            System.out.println(ex.getMessage());
            CustomLogger.log(ex, 2, I18NLoader.COULD_NOT_SOFT_DELETE_REVIEW_WITH_REVIEWID + String.valueOf(reviewId));
            isSoftDeleted = false;

        }

        return isSoftDeleted;
    }
}

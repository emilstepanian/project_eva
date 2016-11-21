package logic.controller;

import dal.DBWrapper;
import logic.misc.CustomLogger;
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

            whereParams.put("course_attendant.user_id", String.valueOf(userId));
            joins.put("course_attendant", "course_id");


            CachedRowSet rowSet = DBWrapper.getRecords("course", null, whereParams, joins);

            while (rowSet.next()){
                Course course = new Course();

                course.setDisplaytext(rowSet.getString("name"));
                course.setCode("code");
                course.setEvents(getLectures(course.getCode()));

                courses.add(course);
            }

        } catch(SQLException ex){
            System.out.println(ex.getMessage());
            CustomLogger.log(ex,2,"Could not retrieve courses at getCourses()");
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

            whereParams.put("course_id", courseCode);

            CachedRowSet rowSet = DBWrapper.getRecords("lecture", null, whereParams, null);

            while (rowSet.next()){
                Lecture lecture = new Lecture();

                lecture.setLectureId(rowSet.getInt("id"));
                lecture.setCourseId(rowSet.getString("course_id"));
                lecture.setStartDate(rowSet.getTimestamp("start"));
                lecture.setEndDate(rowSet.getTimestamp("end"));
                lecture.setType(rowSet.getString("type"));
                lecture.setLocation(rowSet.getString("location"));
                lecture.setDescription(rowSet.getString("description"));

                lectureArrayList.add(lecture);
            }

            lectureArray = lectureArrayList.toArray(new Lecture[lectureArrayList.size()]);

        } catch (SQLException ex){
            System.out.println(ex.getMessage());
            CustomLogger.log(ex, 2, "Could not retrieve lectures at getLectures()");
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
        whereParams.put("lecture_id", String.valueOf(lectureId));

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

            whereParams.put("is_deleted", "0");
            String[] attributes = {"id", "user_id", "lecture_id", "rating", "comment"};

            CachedRowSet rowSet = DBWrapper.getRecords("review", attributes, whereParams, null);

            while (rowSet.next()) {
                Review review = new Review();
                review.setId(rowSet.getInt("id"));
                review.setUserId(rowSet.getInt("user_id"));
                review.setLectureId(rowSet.getInt("lecture_id"));
                review.setRating(rowSet.getInt("rating"));
                review.setComment(rowSet.getString("comment"));

                reviews.add(review);
            }

        } catch(SQLException ex){
            System.out.println(ex.getMessage());
            CustomLogger.log(ex, 2, "Could not retrieve reviews from getReviews()");

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

            isDeletedField.put("is_deleted", "1");

            Map<String, String> whereParams = new HashMap<String, String>();

            /*
            If-statement added to be able to use method in multiple situations.
            If Admin is deleting, he simply passes 0 as reviewId parameter.
             */
            if(userId != 0) {
                whereParams.put("user_id", String.valueOf(userId));
            }

            whereParams.put("id", String.valueOf(reviewId));

            DBWrapper.updateRecords("review", isDeletedField, whereParams);
            isSoftDeleted = true;
        } catch(Exception ex){

            System.out.println(ex.getMessage());
            CustomLogger.log(ex, 2, "(softDeleteReview() could not " +
                    "soft delete review with reviewId " +String.valueOf(reviewId));
            isSoftDeleted = false;

        }

        return isSoftDeleted;
    }
}

package logic.misc;

import dal.DBWrapper;
import logic.controller.ClientController;
import model.entity.Lecture;
import model.entity.Review;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emilstepanian on 21/11/2016.
 * Class used to handle the logic for calculating statistics needed from the client.
 */
public class Statistics {

    /**
     * Calculates and returns statistics for a lecture
     * @param clientCtrl ClientController instance
     * @param courseId ID of the lecture's course
     * @param lectureId ID of the lecture
     * @return Returns a Map containing the statistics
     */
    public static Map<String, String> getLectureStatistics(ClientController clientCtrl, int courseId, int lectureId){
        Map<String, String> lectureStatistics = null;

        try {
            lectureStatistics = new HashMap<String, String>();
            int courseAttendants = getNumberOfCourseParticipants(courseId);

            lectureStatistics.put(I18NLoader.COURSE_ATTENDANTS_MSG, String.valueOf(courseAttendants));
            lectureStatistics.put(I18NLoader.REVIEW_PARTICIPATION_MSG, String.valueOf(calculateReviewParticipation(clientCtrl, lectureId, courseAttendants)));
            lectureStatistics.put(I18NLoader.LECTURE_AVERAGE_MSG, String.valueOf(calculateLectureAverage(clientCtrl, lectureId)));

        } catch(Exception ex){
            CustomLogger.log(ex, 2, ex.getMessage());
        }

        return lectureStatistics;
    }



    /**
     * Calculates the amount of attendants for a course and thereby also its lectures
     * @param courseId ID of the course.
     * @return Returns an Integer with the amount of attendants
     */
    private static int getNumberOfCourseParticipants(int courseId) {
        String table = ConfigLoader.COURSEATTENDANTS_TABLE;
        Map<String, String> whereStmt = new HashMap<String, String>();
        whereStmt.put(ConfigLoader.COURSEATTENDANTS_COURSE_ID_COLUMN, String.valueOf(courseId));

        int courseAttendants = 0;

        try {
            CachedRowSet rowSet = DBWrapper.getRecords(table, null, whereStmt, null);
            courseAttendants = rowSet.size();

        } catch (Exception ex) {
            CustomLogger.log(ex, 2, ex.getMessage());

        }

        return courseAttendants;
    }

    /**
     * Calculates the percentage of how many have reviewed a lecture compared to the amount of attendants.
     * @param lectureId ID of the lecture.
     * @return Returns a double with the percentage of review participation.
     */
    private static double calculateReviewParticipation(ClientController clientCtrl, int lectureId, double courseAttendants) {

        double reviewParticipation = 0;

        try {

            //Find the amount of reviews for a given lecture
            ArrayList<Review> reviews = clientCtrl.getLectureReviews(lectureId);
            int reviewsOnLecture = reviews.size();


            reviewParticipation = (double) reviewsOnLecture / courseAttendants * 100;


        } catch (ArithmeticException ex){
            CustomLogger.log(ex, 2, ex.getMessage());

        }

        return reviewParticipation;

    }

    /**
     * Calculates the average rating for a lecture
     * @param lectureId ID of the lecture.
     * @return Returns a double with the average rating.
     */
    private static double calculateLectureAverage(ClientController clientCtrl, int lectureId) {
        ArrayList<Review> reviews = clientCtrl.getLectureReviews(lectureId);
        int total = 0;

        for (Review review : reviews) {
            total += review.getRating();
        }

        double average = (double) total / (double)reviews.size();
        return average;
    }


}

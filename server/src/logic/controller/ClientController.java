package logic.controller;

import dal.DBWrapper;
import logic.misc.ConfigLoader;
import logic.misc.CustomLogger;
import model.entity.Review;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emilstepanian on 19/11/2016.
 * Class extends the UserController and adds functionality for students of the system
 */
public class ClientController extends UserController {


    public ClientController() {}

    /**
     * Used to add a review object to the database
     * @param review the review to be added to the database
     * @return a boolean that tells whether or not the review was added
     */
    public boolean addReview(Review review) {
        boolean isAdded = true;

        try {
            Map<String, String> values = new HashMap();

            values.put(ConfigLoader.REVIEW_USER_ID_COLUMN, String.valueOf(review.getUserId()));
            values.put(ConfigLoader.REVIEW_LECTURE_ID_COLUMN, String.valueOf(review.getLectureId()));
            values.put(ConfigLoader.REVIEW_RATING_COLUMN, String.valueOf(review.getRating()));
            values.put(ConfigLoader.REVIEW_COMMENT_COLUMN, review.getComment());
            values.put(ConfigLoader.REVIEW_IS_DELETED_COLUMN, ConfigLoader.REVIEW_IS_DELETED_VALUE_FALSE);

            DBWrapper.insertIntoRecords(ConfigLoader.REVIEW_TABLE, values);
            return isAdded;

        } catch (SQLException ex) {
            ex.printStackTrace();
            CustomLogger.log(ex, 2, ex.getMessage());
            isAdded = false;
        }
        return isAdded;
    }
}

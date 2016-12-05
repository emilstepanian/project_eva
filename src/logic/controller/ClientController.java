package logic.controller;

import dal.DBWrapper;
import logic.misc.ConfigLoader;
import logic.misc.I18NLoader;
import model.entity.Review;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class ClientController extends UserController {

    public ClientController() {
    }


    /**
     * Retrieves all reviews from a specific user
     * @param userId ID of the user
     * @return ArrayList containing Review objects
     */
    public ArrayList<Review> getPersonalReviews(int userId) {
        Map<String, String> whereParams = new HashMap<String, String>();
        whereParams.put(ConfigLoader.REVIEW_USER_ID_COLUMN, String.valueOf(userId));

        return getReviews(whereParams);
    }

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

        } catch (SQLException e) {
            e.printStackTrace();
            isAdded = false;
        }
        return isAdded;
    }
}

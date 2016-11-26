package logic.controller;

import logic.misc.ConfigLoader;
import model.entity.Review;

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
}

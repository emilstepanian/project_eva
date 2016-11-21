package logic.controller;

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
        whereParams.put("user_id", String.valueOf(userId));

        return getReviews(whereParams);
    }
}

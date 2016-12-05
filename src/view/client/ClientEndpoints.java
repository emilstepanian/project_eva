package view.client;

import com.google.gson.Gson;
import logic.controller.MainController;
import logic.controller.ClientController;
import logic.misc.I18NLoader;
import logic.misc.Statistics;
import model.entity.Course;
import model.entity.Review;
import model.user.User;
import security.Digester;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by emilstepanian on 19/11/2016.
 */
@Path("api")
public class ClientEndpoints {

    ClientController clientCtrl = new ClientController();
    MainController mainCtrl;

    /**
     * Endpoint used to receive client data to authenticate a client
     * @param loginCredentials The login credentials of the client received as json
     * @return  The authenticated client as json
     */
    @POST
    @Consumes("application/json")
    @Path("/login")
    public Response authenticate(String loginCredentials){

        mainCtrl = new MainController();

        User clientReceived = new Gson().fromJson(loginCredentials, User.class);
        User clientReturned = mainCtrl.authenticate(clientReceived.getCbsMail(), Digester.hash(clientReceived.getPassword()));

        if(clientReturned != null){



            return successResponse(200, clientReturned);
        } else {
            return errorResponse(401, I18NLoader.USER_LOGIN_DENIED);
        }
    }


    /**
     * Endpoint used to retrieve courses of a specific client.
     * @param clientId The client's ID
     * @return An ArrayList with the client's courses as json
     */
    @GET
    @Consumes("application/json")
    @Path("course/user/{userId}")
    public Response getCourses(@PathParam("userId") int clientId){

        ArrayList<Course> courses = clientCtrl.getCourses(clientId);

        if (!courses.isEmpty()){
            return successResponse(200, courses);
        } else {
            return errorResponse(404, I18NLoader.FAILED_RESOURCE_NOT_FOUND);
        }
    }

    /**
     * Endpoint used to retrieve statistics for a specific course.
     * @param courseId The code of the course
     * @return Returns a Map with the course's statistics as json.
     */
    @GET
    @Consumes("application/json")
    @Path("course/entity/{courseId}")
    public Response getCourseStatistics(@PathParam("courseId")String courseId){
        Map<String, String> courseStatistics = Statistics.getCourseStatistics(clientCtrl, courseId);

        if(!courseStatistics.isEmpty() && courseStatistics == null){
            return successResponse(200, courseStatistics);
        } else {
            return errorResponse(404, I18NLoader.FAILED_RESOURCE_NOT_FOUND);
        }
    }

    /**
     * Endpoint used to retrieve statistics for a specific lecture
     * @param lectureId The ID of the lecture
     * @param courseId The ID of that lecture's course
     * @return Returns a Map with the lecture's statistics as json.
     */
    @GET
    @Consumes("application/json")
    @Path("course/entity/{courseId}/{lectureId}")
    public Response getLectureStatistics(@PathParam("courseId") int courseId, @PathParam("lectureId")int lectureId){
        Map<String, String> lectureStatistics = Statistics.getLectureStatistics(clientCtrl, courseId, lectureId);

        if(!lectureStatistics.isEmpty() && lectureStatistics != null){
            return successResponse(200, lectureStatistics);
        } else {
            return errorResponse(404, I18NLoader.FAILED_RESOURCE_NOT_FOUND);
        }
    }

    /**
     * Endpoint used to retrieve reviews of a specific lecture.
     * @param lectureId the lecture's ID
     * @return An ArrayList with the lecture's reviews as json
     */
    @GET
    @Consumes("application/json")
    @Path("review/lecture/{lectureId}")
    public Response getLectureReviews(@PathParam("lectureId") int lectureId){

        ArrayList<Review> reviews = clientCtrl.getLectureReviews(lectureId);

        if (!reviews.isEmpty()) {
            return successResponse(200, reviews);
        } else {
            return errorResponse(404, I18NLoader.FAILED_RESOURCE_NOT_FOUND);
        }
    }


    /**
     * Endpoint used to retrieve all personal reviews.
     * @param studentId The ID of the student
     * @return returns an ArrayList with all the review Objects as json
     */
    @GET
    @Consumes("application/json")
    @Path("review/user/{studentId}")
    public Response getPersonalReviews(@PathParam("studentId") int studentId){

        ArrayList<Review> reviews = clientCtrl.getPersonalReviews(studentId);

        if (!reviews.isEmpty()) {
            return successResponse(200, reviews);
        } else {
            return errorResponse(404, I18NLoader.FAILED_RESOURCE_NOT_FOUND);
        }
    }

    /**
     * Endpoint used to soft delete reviews. Pass '0' as user ID,
     * if requester is a teacher.
     * @param reviewInJson the review to delete in a JSON string
     * @return Returns a server response saying whether the delete was successful or not.
     */
    @POST
    @Consumes("application/json")
    @Path("review/delete")
    public Response softDeleteReview(String reviewInJson){
        Gson gson = new Gson();
        Review review = gson.fromJson(reviewInJson, Review.class);

            if(clientCtrl.softDeleteReview(review.getId())){
                return successResponse(200, I18NLoader.REVIEW_DELETED);
            } else {
                return errorResponse(500, I18NLoader.REVIEW_NOT_DELETED);
            }


    }


        @POST
        @Consumes("application/json")
        @Path("/review/add")
        public Response addReview(String json) {

            System.out.println(json);

            Gson gson = new Gson();
            Review review = gson.fromJson(json, Review.class);

            boolean isAdded = clientCtrl.addReview(review);

            if (isAdded) {
                String toJson = gson.toJson(Digester.encrypt(gson.toJson(isAdded)));

                return successResponse(200, toJson);

            } else {
                return errorResponse(404, "Failed. Couldn't get reviews.");
            }
        }


    /**
     * Used to generate an error response back to client
     * @param status HTTP status
     * @param message Error message to be displayed
     * @return The generated response with the error message
     */
    protected Response errorResponse(int status, String message) {
        return Response.status(status).entity(new Gson().toJson(Digester.encrypt("{\"" + I18NLoader.MESSAGE_WORD + "\": \"" + message + "\"}"))).build();
    }

    /**
     * Used to generate a successful response back to client
     * @param status HTTP status
     * @param data The data to be converted to json and returned to client
     * @return The generated response with the data
     */
    protected Response successResponse(int status, Object data) {
        Gson gson = new Gson();
        return Response.status(status).entity(gson.toJson(Digester.encrypt(gson.toJson(data)))).build();
    }

}

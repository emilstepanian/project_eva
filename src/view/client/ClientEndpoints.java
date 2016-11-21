package view.client;

import com.google.gson.Gson;
import logic.controller.MainController;
import logic.controller.ClientController;
import logic.misc.Statistics;
import model.entity.Course;
import model.entity.Review;
import model.user.User;
import security.Digester;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
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
        User clientReturned = mainCtrl.authenticate(clientReceived.getCbsMail(), clientReceived.getPassword());

        if(clientReturned != null){
            return successResponse(200, clientReturned);
        } else {
            return errorResponse(401, "User login denied");
        }
    }


    /**
     * Endpoint used to retrieve courses of a specific client.
     * @param clientType Type of client
     * @param clientId The client's ID
     * @return An ArrayList with the client's courses as json
     */
    @GET
    @Consumes("application/json")
    @Path("course/{userType}/{userId}")
    public Response getCourses(@PathParam("userType") String clientType, @PathParam("userId") int clientId){

        ArrayList<Course> courses = clientCtrl.getCourses(clientId);

        if (!courses.isEmpty()){
            return successResponse(200, courses);
        } else {
            return errorResponse(404, "Failed. Resource not found");
        }
    }

    /**
     * Endpoint used to retrieve statistics for a specific course.
     * @param courseCode The code of the course
     * @return Returns a Map with the course's statistics as json.
     */
    @GET
    @Consumes("application/json")
    @Path("course/{courseCode}")
    public Response getCourseStatistics(@PathParam("courseCode")String courseCode){
        Map<String, String> courseStatistics = Statistics.getCourseStatistics(clientCtrl, courseCode);

        if(!courseStatistics.isEmpty() && courseStatistics == null){
            return successResponse(200, courseStatistics);
        } else {
            return errorResponse(404, "Failed. Resource not found");
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
    @Path("course/{lectureId}/{courseId}")
    public Response getLectureStatistics(@PathParam("lectureId")int lectureId, @PathParam("courseId") int courseId){
        Map<String, String> lectureStatistics = Statistics.getLectureStatistics(clientCtrl, courseId, lectureId);

        if(!lectureStatistics.isEmpty() && lectureStatistics == null){
            return successResponse(200, lectureStatistics);
        } else {
            return errorResponse(404, "Failed. Resource not found");
        }
    }



    /**
     * Endpoint used to retrieve reviews of a specific lecture.
     * @param clientType Type of client
     * @param lectureId the lecture's ID
     * @return An ArrayList with the lecture's reviews as json
     */
    @GET
    @Consumes("application/json")
    @Path("review/lecture/{userType}/{lectureId}")
    public Response getLectureReviews(@PathParam("userType") String clientType, @PathParam("lectureId") int lectureId){

        ArrayList<Review> reviews = clientCtrl.getLectureReviews(lectureId);

        if (!reviews.isEmpty()) {
            return successResponse(200, reviews);
        } else {
            return errorResponse(404, "Failed. Resource not found.");
        }
    }


    @GET
    @Consumes("application/json")
    @Path("review/personal/{userId}")
    public Response getPersonalReviews(@PathParam("userId") int clientId){

        ArrayList<Review> reviews = clientCtrl.getPersonalReviews(clientId);

        if (!reviews.isEmpty()) {
            return successResponse(200, reviews);
        } else {
            return errorResponse(404, "Failed. Resource not found.");
        }
    }



    /**
     * Used to generate an error response back to client
     * @param status HTTP status
     * @param message Error message to be displayed
     * @return The generated response with the error message
     */
    protected Response errorResponse(int status, String message) {
        return Response.status(status).entity(new Gson().toJson(Digester.encrypt("{\"message\": \"" + message + "\"}"))).build();
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

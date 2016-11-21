package logic.controller;

import dal.DBWrapper;
import model.entity.Course;
import model.entity.Lecture;
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
                System.out.println("An error has occured.");
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


    public Object getSingleRecord(int id, int modification){

    }



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

    private Course getSingleCourse(int courseId){
        Course course = new Course();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put("id", String.valueOf(courseId));

            CachedRowSet rowSet = DBWrapper.getRecords("study", null, whereParam, null);
            rowSet.next();
            course.setId(rowSet.getString("id"));
            //course.setEvents(rowSet.getString("shortname"));
            course.setCode(rowSet.getString("code"));
            course.setDisplaytext(rowSet.getString("displaytext"));


        } catch(SQLException ex){
            System.out.println(ex.getMessage());

        }
        return study;
    }


    private Lecture getSingleLecture(int lectureId){
        Lecture lecture = new Lecture();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put("id", String.valueOf(lectureId));

            CachedRowSet rowSet = DBWrapper.getRecords("user", null, whereParam, null);
            rowSet.next();
            lecture.setLectureId(rowSet.getInt("id"));
            lecture.setCourseId(rowSet.getString("course_id"));
            lecture.setType(rowSet.getString("type"));
            lecture.setStartDate(rowSet.getDate("start"));
            lecture.setEndDate(rowSet.getDate("end"));
            lecture.setLocation(rowSet.getString("location"));


        } catch(SQLException ex){
            System.out.println(ex.getMessage());

        }
        return lecture;
    }


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

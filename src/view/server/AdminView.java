package view.server;

import dal.DBWrapper;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import logic.controller.AdminController;
import logic.misc.ConfigLoader;
import logic.misc.I18NLoader;
import model.entity.Course;
import model.entity.Lecture;
import model.entity.Review;
import model.entity.Study;
import model.user.User;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class AdminView {

    User currentAdmin;
    AdminController adminCtrl;
    Scanner input;

    public AdminView(){

    }

    /**
     * Loads the admin who is authenticated and logged in and runs the main menu.
     * @param currentAdmin The logged in admin
     * @param adminCtrl The controller from MainView.
     * @param input Scanner object from MainView.
     */
    public void loadAdmin(User currentAdmin, AdminController adminCtrl, Scanner input){
        Boolean keepLoggedIn = true;
        this.currentAdmin = currentAdmin;
        this.adminCtrl = adminCtrl;
        this.input = input;

        System.out.println(I18NLoader.YOU_ARE_LOGGED_IN_AS + " " +
                currentAdmin.getFirstName() + " " + currentAdmin.getLastName());

        do{

            System.out.println("\n" + I18NLoader.PRESS_WORD +" [ 1 ] " + I18NLoader.TO_REGISTER_A_NEW_USER);
            System.out.println(I18NLoader.PRESS_WORD +" [ 2 ] " + I18NLoader.TO_ASSIGN_COURSES_TO_A_USER);
            System.out.println(I18NLoader.PRESS_WORD +" [ 3 ] " + I18NLoader.TO_DELETE_A_USER);
            System.out.println(I18NLoader.PRESS_WORD +" [ 4 ] " + I18NLoader.TO_DELETE_A_REVIEW);
            System.out.println(I18NLoader.PRESS_WORD +" [ 0 ] " + I18NLoader.TO_LOG_OUT);

            int choice = input.nextInt();

            switch (choice){
                case 0:
                    System.out.println("\n" + I18NLoader.LOGGING_OUT+ "...\n");
                    keepLoggedIn = false;
                    break;
                case 1:
                    createUserView();
                    break;
                case 2:
                    assignCoursesView();
                    break;
                case 3:
                    deleteUserView();
                    break;
                case 4:
                    deleteReviewView();
                    break;
                default:
                    System.out.println(I18NLoader.WRONG_INPUT_TRY_AGAIN);
                    break;
            }

        } while(keepLoggedIn);

    }

    /**
     * View for creating a user
     */
    private void createUserView(){
        User newUser = new User();
        try {
            do {
                input.nextLine();
                System.out.println(I18NLoader.REGISTER_WORD + ": ");
                System.out.println("[ 1 ] " + I18NLoader.STUDENT_WORD);
                System.out.println("[ 2 ] " + I18NLoader.TEACHER_WORD);
                System.out.println("[ 3 ] " + I18NLoader.ADMIN_WORD);

                int choice = input.nextInt();
                switch (choice) {
                    case 1:
                        newUser.setType(ConfigLoader.USER_TYPE_VALUE_STUDENT);
                        break;
                    case 2:
                        newUser.setType(ConfigLoader.USER_TYPE_VALUE_TEACHER);
                        break;
                    case 3:
                        newUser.setType(ConfigLoader.USER_TYPE_VALUE_ADMIN);
                        break;
                    default:
                        System.out.println(I18NLoader.WRONG_INPUT_TRY_AGAIN);

                }

            } while (newUser.getType() == null);
            input.nextLine(); //These random nextLine() calls are to fix scanner problem, as we need it to jump to new line

            System.out.println(I18NLoader.REGISTERING_WORD + " " + newUser.getType() + "...");
            System.out.print(I18NLoader.ENTER_WORD + " " + I18NLoader.FIRST_NAME + ": ");
            newUser.setFirstName(input.nextLine());

            System.out.print(I18NLoader.ENTER_WORD + " " + I18NLoader.LAST_NAME + ": ");
            newUser.setLastName(input.nextLine());

            System.out.print(I18NLoader.ENTER_WORD + " " + I18NLoader.SCHOOL_ABBREVIATION + " " + I18NLoader.MAIL_WORD + ": ");
            newUser.setCbsMail(input.nextLine());

            do {
                System.out.print(I18NLoader.ENTER_WORD + " " + I18NLoader.PASSWORD_WORD + ": ");
                newUser.setPassword(input.nextLine());
                if (!newUser.getPassword().matches(".*[a-zA-Z]+.*")) {
                    System.out.println(I18NLoader.INVALID_PASSWORD_TRY_AGAIN +"\n");
                }
            } while (!newUser.getPassword().matches(".*[a-zA-Z]+.*"));

            adminCtrl.createUser(newUser);
        } catch(Exception ex) {
            System.out.println(I18NLoader.AN_ERROR_HAS_OCCURRED);
            System.out.println(ex.getMessage());
            System.out.println("\n" + I18NLoader.REVERTING_TO_MAINMENU + "\n");
        }
    }

    /**
     * View for assigning courses to a user
     */
    private void assignCoursesView(){
        input.nextLine();
        System.out.println("\n" + I18NLoader.PRESS_WORD + " [ 1 ] " + I18NLoader.TO_ASSIGN_COURSES_BY_STUDY);
        System.out.println(I18NLoader.PRESS_WORD + " [ 2 ] " + I18NLoader.TO_ASSIGN_A_SINGLE_COURSE + "\n");
        int choice = input.nextInt();

        switch (choice) {
            case 1:
                try {
                    input.nextLine();


                    System.out.print(I18NLoader.ENTER_WORD + " " + I18NLoader.ID_OF_THE_STUDENT + ": ");
                    int studentId = input.nextInt();
                    User user = (User) adminCtrl.getSingleRecord(studentId, 1);

                    System.out.println(I18NLoader.YOU_HAVE_CHOSEN_STUDENT + ": " + user.getFirstName() + " " + user.getLastName());

                    System.out.print(I18NLoader.ENTER_WORD + " " + I18NLoader.ID_OF_THE_STUDY + ": ");
                    int studyId = input.nextInt();

                    Study study = (Study) adminCtrl.getSingleRecord(studyId, 2);
                    System.out.println(I18NLoader.YOU_HAVE_CHOSEN_STUDY + ": " + study.getName());
                    System.out.println(I18NLoader.YOU_WANT_TO_ASSIGN_THE_COURSES_FOR + " " +
                            study.getName() + I18NLoader.TO_WORD + user.getFirstName() + "?");
                    System.out.println("[ 1 ] " + I18NLoader.YES_WORD + " \n[ 2 ] " + I18NLoader.NO_WORD);
                    int confirm = input.nextInt();
                    input.nextLine();
                    if(confirm == 1){
                        adminCtrl.assignStudy(studentId, studyId);
                        System.out.println(I18NLoader.COURSES_SUCCESSFULLY_ASSIGNED);
                        break;
                    } else if(confirm == 2) {
                        System.out.println(I18NLoader.REVERTING_TO_MAINMENU);
                        break;
                    } else {
                        System.out.println(I18NLoader.INVALID_INPUT + ".\n " + I18NLoader.REVERTING_TO_MAINMENU);
                        break;
                    }



                } catch(Exception ex){
                    input.nextLine();
                    System.out.println(I18NLoader.AN_ERROR_HAS_OCCURRED + " . " + I18NLoader.DID_NOT_ASSIGN_COURSES);
                    System.out.println(I18NLoader.REVERTING_TO_MAINMENU);
                    break;
                }

                case 2:
                    try {

                        System.out.print(I18NLoader.ENTER_WORD + " " + I18NLoader.ID_OF_THE_USER + ": ");
                        int studentId = input.nextInt();

                        User user = (User) adminCtrl.getSingleRecord(studentId, 1);

                        System.out.println(I18NLoader.YOU_HAVE_CHOSEN_USER + ": " + user.getFirstName() + " " + user.getLastName());


                        System.out.print(I18NLoader.ENTER_WORD + " " + I18NLoader.ID_OF_THE_COURSE + ": ");
                        int courseId = input.nextInt();
                        Course course = (Course) adminCtrl.getSingleRecord(courseId, 3);
                        System.out.println(I18NLoader.YOU_HAVE_CHOSEN_COURSE + ": " + course.getDisplaytext());
                        System.out.println(I18NLoader.YOU_WANT_TO_ASSIGN_THE_COURSES_FOR + " " + user.getFirstName() + "?");
                        System.out.println("[ 1 ] " + I18NLoader.YES_WORD + " \n[ 2 ] " + I18NLoader.NO_WORD);
                        int confirm = input.nextInt();
                        if(confirm == 1){
                            adminCtrl.assignSingleCourse(studentId, courseId);
                            System.out.println(I18NLoader.COURSES_SUCCESSFULLY_ASSIGNED);
                            break;
                        } else if(confirm == 2) {
                            System.out.println(I18NLoader.REVERTING_TO_MAINMENU);
                            break;
                        } else {
                            System.out.println(I18NLoader.INVALID_INPUT + ".\n " + I18NLoader.REVERTING_TO_MAINMENU);
                            break;
                        }

                    } catch(Exception ex){
                        input.nextLine();
                        System.out.println(I18NLoader.AN_ERROR_HAS_OCCURRED + ". " + I18NLoader.DID_NOT_ASSIGN_COURSES);
                        System.out.println("\n" + I18NLoader.REVERTING_TO_MAINMENU + "\n");

                        break;
                    }
            default:
                System.out.println( I18NLoader.INVALID_KEY_PRESSED + ". \n" + I18NLoader.REVERTING_TO_MAINMENU + "...\n");
                return;
        }
    }

    /**
     * View for deleting a user
     */
    private void deleteUserView() {
        input.nextLine();
        try {
            System.out.println("Do you know the ID of the user? \nPress [ 1 ] if you do,\nPress [ 2 ] if you do not. (navigate to the user)");

            int choice = input.nextInt();

            if(choice == 1) {

                deleteUser();

            } else if(choice == 2) {

                CachedRowSet userRowSet = DBWrapper.getRecords(ConfigLoader.USER_TABLE, null, null, null);

                while (userRowSet.next()){

                    System.out.println("ID: " + userRowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES)
                            + " - " + userRowSet.getString(ConfigLoader.USER_FIRSTNAME_COLUMN) + " " +
                    userRowSet.getString(ConfigLoader.USER_LASTNAME_COLUMN) + ", " + userRowSet.getString(ConfigLoader.USER_CBSMAIL_COLUMN));

                }

                deleteUser();


            } else {
            System.out.println(I18NLoader.INVALID_INPUT + ".\n" + I18NLoader.REVERTING_TO_MAINMENU);
        }


        } catch (Exception ex) {
            System.out.println(I18NLoader.AN_ERROR_HAS_OCCURRED);
            System.out.println(ex.getMessage());
            System.out.println("\n" + I18NLoader.REVERTING_TO_MAINMENU + "\n");
        }
        input.nextLine();
    }


    private void deleteUser() {
        try {
            System.out.print("\n" + I18NLoader.ENTER_WORD + " " + I18NLoader.ID_OF_THE_USER_TO_DELETE + ": ");

            User user = (User) adminCtrl.getSingleRecord(input.nextInt(), 1);

            System.out.println(I18NLoader.YOU_WANT_TO_DELETE + " " + user.getFirstName() + " " + user.getLastName() + "?");
            System.out.println("[ 1 ] " + I18NLoader.YES_WORD + " \n[ 2 ] " + I18NLoader.NO_WORD);

            int confirm = input.nextInt();

            if (confirm == 1) {
                adminCtrl.deleteUser(user.getId());
                System.out.println(I18NLoader.USER_SUCCESFULLY_DELETED + ".");
            } else if (confirm == 2) {
                System.out.println(I18NLoader.REVERTING_TO_MAINMENU);
            } else {
                System.out.println(I18NLoader.INVALID_INPUT + ".\n" + I18NLoader.REVERTING_TO_MAINMENU);
            }
        } catch (Exception ex) {
            System.out.println(I18NLoader.AN_ERROR_HAS_OCCURRED);
            System.out.println(ex.getMessage());
            System.out.println("\n" + I18NLoader.REVERTING_TO_MAINMENU + "\n");
        }
    }

    /**
     * View for deleting a review
     */
    private void deleteReviewView() {
        input.nextLine();
        try {


            System.out.println("Do you know the ID of the review? \nPress [ 1 ] if you do,\nPress [ 2 ] if you do not. (navigate to the review)");
            int choice = input.nextInt();

            if(choice == 1) {

                deleteReview();

            } else if(choice == 2){

                CachedRowSet coursesRowSet = DBWrapper.getRecords(ConfigLoader.COURSE_TABLE, null, null, null);

                while (coursesRowSet.next()){
                    Thread.sleep(1);


                    System.out.println("ID: " + coursesRowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES)
                            + " - " + coursesRowSet.getString(ConfigLoader.COURSE_NAME_COLUMN));

                }

                System.out.println("\n\nEnter the ID of the course, containing the lecture and the review: ");
                int chosenCourseId = input.nextInt();

                coursesRowSet.restoreOriginal();
                while(coursesRowSet.next()){
                    if(coursesRowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES) == chosenCourseId){
                        Map<String, String> whereParam = new HashMap<String, String>();
                        whereParam.put(ConfigLoader.LECTURE_COURSE_CODE_COLUMN, coursesRowSet.getString(ConfigLoader.COURSE_CODE_COLUMN));

                        CachedRowSet lecturesRowSet = DBWrapper.getRecords(ConfigLoader.LECTURE_TABLE, null, whereParam, null);

                        while(lecturesRowSet.next()){
                            Thread.sleep(50);

                            System.out.println("\nLecture - ID:" + lecturesRowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES) +  "\n - " + lecturesRowSet.getString(ConfigLoader.LECTURE_DESCRIPTION_COLUMN) +
                            "\n - Location: " + lecturesRowSet.getString(ConfigLoader.LECTURE_LOCATION_COLUMN)+
                            "\n - Date: " + lecturesRowSet.getDate(ConfigLoader.LECTURE_START_DATE_COLUMN));
                            System.out.println(" - Reviews:");

                            Map<String, String> reviewWhereParams = new HashMap<String, String>();
                            reviewWhereParams.put(ConfigLoader.REVIEW_IS_DELETED_COLUMN, ConfigLoader.REVIEW_IS_DELETED_VALUE_FALSE);
                            reviewWhereParams.put(ConfigLoader.REVIEW_LECTURE_ID_COLUMN, String.valueOf(lecturesRowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES)));

                            CachedRowSet lectureReviewsRowSet = DBWrapper.getRecords(ConfigLoader.REVIEW_TABLE, null, reviewWhereParams, null);

                            while (lectureReviewsRowSet.next()){

                                System.out.println("     - ID: " + lectureReviewsRowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES) +
                                ": Rating: (" + lectureReviewsRowSet.getInt(ConfigLoader.REVIEW_RATING_COLUMN) + "/5)" +
                                " - Comment: " + lectureReviewsRowSet.getString(ConfigLoader.REVIEW_COMMENT_COLUMN));

                            }
                        }
                        deleteReview();
                    }
                }


            } else {
                System.out.println(I18NLoader.INVALID_INPUT + ".\n" + I18NLoader.REVERTING_TO_MAINMENU);
            }


        } catch (Exception ex){
            input.nextLine();
            System.out.println(I18NLoader.AN_ERROR_HAS_OCCURRED);
            System.out.println(ex.getMessage());
            System.out.println("\n" + I18NLoader.REVERTING_TO_MAINMENU + "\n");
        }
    }


    private void deleteReview() {

        System.out.print("\n" + I18NLoader.ENTER_WORD + " " + I18NLoader.ID_OF_THE_REVIEW_TO_DELETE +  ": ");

        Review review = (Review) adminCtrl.getSingleRecord(input.nextInt(), 4);

        System.out.println(I18NLoader.YOU_WANT_TO_DELETE_REVIEW + "\n" + I18NLoader.RATING_WORD + ":" + review.getRating() +
                "\n" + I18NLoader.COMMENT_WORD + ": " + review.getComment());
        System.out.println("[ 1 ] " + I18NLoader.YES_WORD + " \n[ 2 ] " + I18NLoader.NO_WORD);

        int confirm = input.nextInt();

        if (confirm == 1) {
            if (adminCtrl.softDeleteReview(review.getId())) {
                System.out.println(I18NLoader.REVIEW_DELETED + ".");
            } else {
                System.out.println(I18NLoader.REVIEW_NOT_DELETED + ".\n" + I18NLoader.REVERTING_TO_MAINMENU);
            }
        } else if (confirm == 2) {
            System.out.println(I18NLoader.REVERTING_TO_MAINMENU);
        } else {
            System.out.println(I18NLoader.INVALID_INPUT + ".\n" + I18NLoader.REVERTING_TO_MAINMENU);
        }

    }

}

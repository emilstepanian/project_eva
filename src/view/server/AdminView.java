package view.server;

import logic.controller.AdminController;
import model.entity.Course;
import model.entity.Review;
import model.entity.Study;
import model.user.User;

import java.sql.SQLException;
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

    public void loadAdmin(User currentAdmin, AdminController adminCtrl, Scanner input){
        Boolean keepLoggedIn = true;
        this.currentAdmin = currentAdmin;
        this.adminCtrl = adminCtrl;
        this.input = input;

        System.out.println("You are logged in as " +
                currentAdmin.getFirstName() + " " + currentAdmin.getLastName());

        do{

            System.out.println("\nPress [ 1 ] to register a new user");
            System.out.println("Press [ 2 ] to assign courses to a user");
            System.out.println("Press [ 3 ] to delete a user");
            System.out.println("Press [ 4 ] to delete a review");
            System.out.println("Press [ 0 ] to log out");

            int choice = input.nextInt();

            switch (choice){
                case 0:
                    System.out.println("\nLogging out..\n");
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
                    System.out.println("Wrong input, try again");
                    break;
            }

        } while(keepLoggedIn);

    }

    private void createUserView(){
        User newUser = new User();
        do{
            input.nextLine();
            System.out.println("Register: ");
            System.out.println("[ 1 ] Student");
            System.out.println("[ 2 ] Teacher");
            System.out.println("[ 3 ] Admin");

            int choice = input.nextInt();
            switch (choice){
                case 1:
                    newUser.setType("student");
                    break;
                case 2:
                    newUser.setType("teacher");
                    break;
                case 3:
                    newUser.setType("admin");
                    break;
                default:
                    System.out.println("Wrong input, try again.");

            }

        } while(newUser.getType() != null);

        System.out.println("Registering " + newUser.getType() + "...");
        System.out.println("Enter first name: ");
        newUser.setFirstName(input.nextLine());

        System.out.println("Enter last name: ");
        newUser.setLastName(input.nextLine());

        System.out.println("Enter CBS email adresse:");
        newUser.setCbsMail(input.nextLine());

        do {
            System.out.println("Enter password: ");
            newUser.setPassword(input.nextLine());
            if(!newUser.getPassword().matches(".*[a-zA-Z]+.*")){
                System.out.println("Invalid password. Please try again. \n");
            }
        } while(!newUser.getPassword().matches(".*[a-zA-Z]+.*"));

        adminCtrl.createUser(newUser);
    }


    private void assignCoursesView(){
        input.nextLine();
        System.out.println("\nPress [ 1 ] to assign courses by study (used for students)");
        System.out.println("Press [ 2 ] to assign a single course (used for teachers\n");
        int choice = input.nextInt();

        switch (choice) {
            case 1:
                try {

                    System.out.println("Enter ID of the student: ");
                    int studentId = input.nextInt();
                    User user = (User) adminCtrl.getSingleRecord(studentId, 1);

                    System.out.println("You have chosen student: " + user.getFirstName() + " " + user.getLastName());

                    System.out.println("Enter ID of the study: ");
                    int studyId = input.nextInt();

                    Study study = (Study) adminCtrl.getSingleRecord(studyId, 2);
                    System.out.println("You have chosen Study: " + study.getName());
                    System.out.println("Are you sure you want to assign the courses for " +
                            study.getName() + " to " + user.getFirstName() + "?");
                    System.out.println("[ 1 ] Yes \n[ 2 ] No");
                    int confirm = input.nextInt();
                    if(confirm == 1){
                        adminCtrl.assignStudy(studentId, studyId);
                        System.out.println("Courses successfully assigned.");
                        break;
                    } else if(confirm == 2) {
                        System.out.println("Reverting back to main menu...");
                        break;
                    } else {
                        System.out.println("Invalid input.\n Reverting back to main menu");
                        break;
                    }



                } catch(Exception ex){
                    System.out.println("An Error has occurred. Did not assign courses.");
                    System.out.println("Reverting to main menu...");
                    break;
                }

                case 2:
                    try {

                        System.out.println("Enter ID of the user: ");
                        int studentId = input.nextInt();

                        User user = (User) adminCtrl.getSingleRecord(studentId, 1);

                        System.out.println("You have chosen user: " + user.getFirstName() + " " + user.getLastName());


                        System.out.println("Enter ID of the course: ");
                        int courseId = input.nextInt();
                        Course course = (Course) adminCtrl.getSingleRecord(courseId, 3);
                        System.out.println("You have chosen course: " + course.getDisplaytext());
                        System.out.println("Are you sure you want to assign the course to " + user.getFirstName() + "?");
                        System.out.println("[ 1 ] Yes \n[ 2 ] No");
                        int confirm = input.nextInt();
                        if(confirm == 1){
                            adminCtrl.assignSingleCourse(studentId, courseId);
                            System.out.println("Courses successfully assigned.");
                            break;
                        } else if(confirm == 2) {
                            System.out.println("Reverting back to main menu...");
                            break;
                        } else {
                            System.out.println("Invalid input.\n Reverting back to main menu");
                            break;
                        }

                    } catch(Exception ex){
                        System.out.println("An Error has occurred. Did not assign the course.");
                        System.out.println("Reverting to main menu...");
                        break;
                    }
            default:
                System.out.println("Invalid key pressed. \nRevering to main menu...\n");
                return;
        }
    }

    private void deleteUserView() {
        input.nextLine();
        System.out.println("\nEnter ID of the user you want to delete: ");

        User user = (User) adminCtrl.getSingleRecord(input.nextInt(),1);

        System.out.println("Are you sure, you want to delete " + user.getFirstName() + " " + user.getLastName() + "?");
        System.out.println("[ 1 ] Yes \n[ 2 ] No");

        int confirm = input.nextInt();

        if(confirm == 1){
            adminCtrl.deleteUser(user.getId());
            System.out.println("User successfully deleted.");
        } else if(confirm == 2) {
            System.out.println("Reverting back to main menu...");
        } else {
            System.out.println("Invalid input.\n Reverting back to main menu");
        }
    }

    private void deleteReviewView() {
        input.nextLine();

        System.out.println("\nEnter ID of the review you want to delete: ");

        Review review = (Review) adminCtrl.getSingleRecord(input.nextInt(), 5);

        System.out.println("Are you sure, you want to delete review\nRating:" + review.getRating() + "\nComment:  " + review.getComment());
        System.out.println("[ 1 ] Yes \n[ 2 ] No");

        int confirm = input.nextInt();

        if(confirm == 1){
            if(adminCtrl.softDeleteReview(0, review.getId())) {
                System.out.println("Review successfully deleted.");
            } else {
                System.out.println("Review did not successfully get deleted.\nReverting back to main menu...");
            }
        } else if(confirm == 2) {
            System.out.println("Reverting back to main menu...");
        } else {
            System.out.println("Invalid input.\n Reverting back to main menu");
        }
    }
}

package view.server;

import logic.controller.AdminController;
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





            }

        } while(keepLoggedIn);

    }

    public void createUserView(){
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


    public void assignCoursesView(){
        input.nextLine();
        System.out.println("\nPress [ 1 ] to assign courses by study (used for students)");
        System.out.println("Press [ 2 ] to assign a single course (used for teachers\n");
        int choice = input.nextInt();

        switch (choice) {
            case 1:
                try {

                    System.out.println("Enter ID of the student: ");
                    int studentId = input.nextInt();

                    System.out.println("Enter ID of the study: ");
                    int studyId = input.nextInt();
                    adminCtrl.assignStudy(studentId, studyId);
                    break;

                } catch(Exception ex){
                    System.out.println("An Error has occurred. Did not assign courses.");
                    System.out.println("Reverting to main menu...");
                    break;
                }

                case 2:
                    try {

                        System.out.println("Enter ID of the student: ");
                        int studentId = input.nextInt();

                        System.out.println("Enter ID of the course: ");
                        int courseId = input.nextInt();
                        adminCtrl.assignSingleCourse(studentId, courseId);
                        break;

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

    public void deleteUserView() {
        input.nextLine();
        System.out.println("\nEnter ID of the user you want to delete: ");
        int userId = input.nextInt();

        System.out.println("Are you sure, you want to delete ");

    }


}

package view.server;

import logic.controller.AdminController;
import logic.controller.MainController;
import logic.misc.ConfigLoader;
import model.user.User;
import security.Digester;

import java.util.Scanner;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class MainView {

    public MainView() {
        main();
    }

    private void main(){
        MainController mainCtrl = new MainController();
        AdminController adminCtrl = new AdminController();
        AdminView adminView = new AdminView();
        Scanner input = new Scanner(System.in);
        Boolean keepSystemRunning = true;

        System.out.println("Project_eva v. 1.0 running");
        System.out.println("Visit: http://" + ConfigLoader.SERVER_ADDRESS + ":" + ConfigLoader.SERVER_PORT + "/");
        System.out.println("Press [ 1 ] to log in as admin");
        System.out.println("Press [ 0 ] to stop server\n");

        while(keepSystemRunning){
            System.out.println("Press [ 1 ] to log in as admin");
            System.out.println("Press [ 0 ] to stop server\n");
            int choice = input.nextInt();

            switch (choice) {
                case 0:
                    System.out.println("Server shutting down.");
                    System.out.println("Server stopped.");
                    keepSystemRunning = false;
                    System.exit(0);
                    break;

                case 1:
                    input.nextLine();
                    System.out.println("Enter mail: ");
                    String mail = input.nextLine();

                    System.out.println("Enter password: ");
                    String password = Digester.hash(input.nextLine());

                    try {

                        User admin = mainCtrl.authenticate(mail, password);

                        if (admin.getType().equals("admin")) {
                            System.out.println("\nLog in granted.");
                            System.out.println("Logging in..\n");

                            adminView.loadAdmin(admin, adminCtrl, input);

                        } else if (admin == null){
                            System.out.println("Admin not found.\n");

                        } else {
                            System.out.println("User not admin.\n");
                        }

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());

                    }
                    break;
                default:
                    System.out.println("Wrong key pressed.\n");

            }
        }


    }

}

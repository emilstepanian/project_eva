package view.server;

import logic.controller.AdminController;
import logic.controller.MainController;
import logic.misc.ConfigLoader;
import logic.misc.I18NLoader;
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

    /**
     * The main menu of the server.
     */
    private void main(){
        MainController mainCtrl = new MainController();
        AdminController adminCtrl = new AdminController();
        AdminView adminView = new AdminView();
        Scanner input = new Scanner(System.in);
        Boolean keepSystemRunning = true;

        System.out.println(ConfigLoader.SERVER_TITLE + " v. " + ConfigLoader.SERVER_VERSION + " " + I18NLoader.RUNNING_WORD);
        System.out.println(I18NLoader.VISIT_WORD + ": http://" + ConfigLoader.SERVER_ADDRESS + ":" + ConfigLoader.SERVER_PORT + "/");

        while(keepSystemRunning){
            System.out.println(I18NLoader.PRESS_WORD + " [ 1 ] " + I18NLoader.TO_LOG_IN_AS_ADMIN);
            System.out.println(I18NLoader.PRESS_WORD + " [ 0 ] " + I18NLoader.TO_STOP_SERVER);
            int choice = input.nextInt();

            switch (choice) {
                case 0:
                    System.out.println(I18NLoader.SERVER_SHUTTING_DOWN + "...");
                    System.out.println(I18NLoader.SERVER_STOPPED + ".");
                    keepSystemRunning = false;
                    System.exit(0);
                    break;

                case 1:
                    input.nextLine();
                    System.out.println(I18NLoader.ENTER_WORD + " " + I18NLoader.MAIL_WORD+ ": ");
                    String mail = input.nextLine();

                    System.out.println(I18NLoader.ENTER_WORD + " " + I18NLoader.PASSWORD_WORD+ ": ");
                    String password = input.nextLine();
                    String hashedPassword = Digester.hash(password);

                    try {

                        User admin = mainCtrl.authenticate(mail, hashedPassword);

                        if (admin.getType().equals(ConfigLoader.USER_TYPE_VALUE_ADMIN)) {
                            System.out.println("\n" + I18NLoader.LOGIN_GRANTED + ".");
                            System.out.println(I18NLoader.LOGGING_IN + "..\n");

                            adminView.loadAdmin(admin, adminCtrl, input);

                        } else if (admin == null){
                            System.out.println(I18NLoader.ADMIN_NOT_FOUND + ".\n");

                        } else {
                            System.out.println(I18NLoader.USER_NOT_ADMIN + ".\n");
                        }

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());

                    }
                    break;
                default:
                    System.out.println(I18NLoader.INVALID_KEY_PRESSED + ".\n");

            }
        }


    }

}

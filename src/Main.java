import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import logic.misc.CBSParser;
import logic.misc.ConfigLoader;
import logic.misc.CustomLogger;
import logic.misc.I18NLoader;
import view.server.MainView;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class Main {

    public static void main(String[] args) {
        I18NLoader.parseLanguage();
        ConfigLoader.parseConfig();
        String sUrl = "http://" + ConfigLoader.SERVER_ADDRESS + ":" + ConfigLoader.SERVER_PORT + "/";
        HttpServer server = null;

        try {
            PrintStream stdOut = System.out;
            System.setOut(stdOut);

            server = HttpServerFactory.create(sUrl);
            server.start();

            CustomLogger.initiateLog(ConfigLoader.DEBUG);

            //CBSParser.parseCBSData();
            new MainView();




        } catch(Exception ex){

            System.out.println(ex.getMessage());

        }
    }
}

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;
import logic.misc.CBSParser;
import logic.misc.ConfigLoader;
import logic.misc.CustomLogger;
import logic.misc.I18NLoader;
import view.client.CORSResponseFilter;
import view.server.MainView;

import javax.ws.rs.Uri;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Collections;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 * Created by emilstepanian on 19/11/2016.
 * The main class that instantiates everything and starts the server
 */
public class Main {


    public static void main(String[] args) {

        String sUrl = "http://" + ConfigLoader.SERVER_ADDRESS + ":" + ConfigLoader.SERVER_PORT + "/";
        ConfigLoader.parseConfig();
        HttpServer server = null;


        try {
            PrintStream stdOut = System.out;
            System.setOut(stdOut);

            server = createHttpServer(sUrl);

            /*
            Turn of Jersey logging, as the Jersey logger for some reason keep displaying 'INFO' messages that aren't errors.
            Simply outcomment the for-loop, if one needs to see the console-messages
             */
            for (String l :
                    Collections.list(LogManager.getLogManager().getLoggerNames())) {
                if (l.startsWith("com.sun.jersey")) {
                        Logger.getLogger(l).setLevel(Level.OFF);
                }
            }

            server.start();

            /*
            Initialize the custom logger
             */
            CustomLogger.initiateLog(ConfigLoader.DEBUG);

            /*
            Initialize and run the CBSParser thread once every day
             */
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new CBSParser(), 0, 1, TimeUnit.DAYS);

            /*
            Instantiate the main view of the server
             */
            new MainView();


        } catch(Exception ex){
            CustomLogger.log(ex, 3, ex.getMessage());
        }
    }

    /**
     * Method used to get the server URL and add the CORSResponseFilter, before creating and starting the server.
     * @param sUrl the URL of the server
     * @return the HTTPServer object that has been created by the HTTPServerFactory
     */
    private static HttpServer createHttpServer(String sUrl) throws IOException {
        ResourceConfig customResponseConfig = new PackagesResourceConfig("view.client");
        customResponseConfig.getContainerResponseFilters().add(new CORSResponseFilter());
        return HttpServerFactory.create(sUrl, customResponseConfig);
    }
}

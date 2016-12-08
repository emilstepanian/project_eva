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
 */
public class Main {


    public static void main(String[] args) {

        String sUrl = "http://" + ConfigLoader.SERVER_ADDRESS + ":" + ConfigLoader.SERVER_PORT + "/";
        I18NLoader.parseLanguage();
        ConfigLoader.parseConfig();
        HttpServer server = null;


        try {
            PrintStream stdOut = System.out;
            System.setOut(stdOut);

            server = createHttpServer(sUrl);

            //Turn of Jersey logging.
            for (String l :
                    Collections.list(LogManager.getLogManager().getLoggerNames())) {
                if (l.startsWith("com.sun.jersey")) {
                        Logger.getLogger(l).setLevel(Level.OFF);
                }
            }
            server.start();

            CustomLogger.initiateLog(ConfigLoader.DEBUG);

            //Runs the CBSParser once every day
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new CBSParser(), 0, 1, TimeUnit.DAYS);

            new MainView();





        } catch(Exception ex){

            System.out.println(ex.getMessage());

        }
    }
    private static HttpServer createHttpServer(String sUrl) throws IOException {
        ResourceConfig customResponseConfig = new PackagesResourceConfig("view.client");
        customResponseConfig.getContainerResponseFilters().add(new CORSResponseFilter());
        return HttpServerFactory.create(sUrl, customResponseConfig);
    }
}

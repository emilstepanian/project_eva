package logic.misc;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by emilstepanian on 19/11/2016.
 * CustomLogger creates a custom logger for the system to assist in debugging,
 * should any system shut downs or errors occur
 */
public class CustomLogger {

    public static final Logger logger = Logger.getLogger("Logger");

    /**
     * Sets the level of debugging, the user specifies
     * in his Config.json file.
     * @param debugLevel Specified debug level.
     */
    public static void initiateLog(String debugLevel) {
        if(debugLevel.equals("1")){
            logger.setLevel(Level.FINEST);
        }else if(debugLevel.equals("2")){
            logger.setLevel(Level.FINE);
        }else {
            logger.setLevel(Level.SEVERE);
        }
    }

    /**
     * Is called if an exception is thrown,
     * so the system can log it in application.log.
     * @param ex Exception thrown.
     * @param level Specified debug level.
     * @param msg Error-message the exception throws.
     */
    public static void log(Exception ex, int level, String msg) {
        FileHandler fileHandler = null;

        try {
            fileHandler = new FileHandler("application.log", true);
            logger.addHandler(fileHandler);

            switch (level){
                case 1:
                    logger.log(Level.FINEST, msg, ex);
                    if(!msg.equals("")){
                        System.out.println(msg + " " + I18NLoader.FINEST_ERROR);
                    }
                    break;
                case 2:
                    logger.log(Level.FINE, msg, ex);
                    if(!msg.equals("")){
                        System.out.println(msg + " " + I18NLoader.FINE_ERROR);
                    }
                    break;
                case 3:
                    logger.log(Level.SEVERE, msg, ex);
                    if(!msg.equals("")){
                        System.out.println(msg + " " + I18NLoader.SEVERE_ERROR);
                    }
                    break;
                default:
                    logger.log(Level.CONFIG, msg, ex);
                    break;
            }

        } catch(IOException IOEx) {
            logger.log(Level.SEVERE, null, IOEx);
        } catch (SecurityException secEx) {
            logger.log(Level.SEVERE, null, secEx);

        } finally {
            if(fileHandler != null) {
                fileHandler.close();
            }
        }
    }

}

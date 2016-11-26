package logic.misc;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import dal.DBWrapper;
import model.entity.Course;
import model.entity.Lecture;

import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Kasper on 15/10/2016.
 * Class that retrieves and reads data from CBS' Calendar API in JSON format.
 * It parses the JSON data into objects inside the system and thereafter parses
 * them into the database
 */
public class CBSParser {
    private static Course[] courseArray;
    private static Gson gson = new Gson();


    /**
     * Is called to parse the data from CBS into the database.
     * Aggregates all the private methods in the class
     */
    public static void parseCBSData() {
        try {

            parseCoursesToArray();
            parseStudiesToDatabase();
            parseCoursesToDatabase();
            parseLecturesToDatabase();

        } catch (SQLException ex){
            System.out.println(ex.getMessage());

        }

    }

    /**
     * Fills a courseArray with Course objects created from CBS' API which
     * can be found locally
     */
    private static void parseCoursesToArray() {
        try {
            //Reads the JSON file and creates an array with Course objects
            JsonReader reader = new JsonReader(new FileReader(ConfigLoader.COURSES_JSON));
            courseArray = gson.fromJson(reader, Course[].class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Reads the JSON file with study data and creates an array of JSON objects.
     * Then it adds entries into the Study tabel inside the database
     */
    private static void parseStudiesToDatabase() {
        JsonReader jsonReader;
        JsonParser jparser = new JsonParser();

        Set<String> duplicatesCheck = new HashSet<String>();

        try {
            jsonReader = new JsonReader(new FileReader(ConfigLoader.STUDY_DATA_JSON));
            JsonArray jArray = jparser.parse(jsonReader).getAsJsonArray();

            Iterator iterator = jArray.iterator();


            while(iterator.hasNext()){

                JsonObject obj = (JsonObject) iterator.next();
                Map<String, String> studyValues = new HashMap<String, String>();


                /*
                Check if the Study is already in the database.
                If not, then insert it. Values are specific to CBS' API, and therefore not interchangeable.
                 */
                if(!duplicatesCheck.contains(obj.get("shortname").toString().replace("\"", "").substring(0,5))){

                    studyValues.put("shortname",obj.get("shortname").toString().replace("\"", "").substring(0,5));
                    studyValues.put("name",obj.get("study-name").toString().replace("\"", ""));
                    duplicatesCheck.add(obj.get("shortname").toString().replace("\"", "").substring(0,5));
                    DBWrapper.insertIntoRecords("study", studyValues);
                }
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Parses and inserts Courses into the database
     */
    private static void parseCoursesToDatabase() {
        try {

            Map<String, String> studyAttributes = new HashMap<String, String>();
            Map<String, String> courseMap = new HashMap<String, String>();

            CachedRowSet rs = DBWrapper.getRecords(ConfigLoader.STUDY_TABLE, new String[]{ConfigLoader.ID_COLUMN_OF_ALL_TABLES,ConfigLoader.STUDY_SHORTNAME_COLUMN}, null, null);


        /*
           Gennemløb listen af studier hentet fra databasen og gem de første 5 bogstaver af dens shortname, samt det
           tilhørende id fra databasen i et HashMap.
           NOTE: (Overvej at lave denne til en metode for sig selv da den også bruges i parseLecturesToDatabase());
        */
            while(rs.next()){


                String substring = rs.getString(ConfigLoader.STUDY_SHORTNAME_COLUMN).substring(0,5);

                studyAttributes.put(substring, rs.getString(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            }


        /*
           Løb arrayet af kurser igennem og tjek om der findes et match mellem kurset og studieretninger gemt i
           studyAttributes hashmappet. Hvis ja, opret da kurset i databasen med det tilhørende study_id.
         */
            for (Course course : courseArray){

                String substring = course.getId().substring(0,5);

                if(studyAttributes.containsKey(substring)){

                    courseMap.put(ConfigLoader.COURSE_CODE_COLUMN, course.getDisplaytext());
                    courseMap.put(ConfigLoader.COURSE_NAME_COLUMN, course.getId());
                    courseMap.put(ConfigLoader.COURSE_STUDY_ID_COLUMN, studyAttributes.get(substring));

                    DBWrapper.insertIntoRecords(ConfigLoader.COURSE_TABLE, courseMap);
                }
            }


        } catch(SQLException ex){

            System.out.println(ex.getMessage());


        }
    }

    /**
     * Opretter entries i datbasens Lecture tabel.
     * @throws SQLException
     */
    private static void parseLecturesToDatabase() throws SQLException{

        String urlPrefix = ConfigLoader.CBS_API_LINK;
        URL url;
        HttpURLConnection conn;
        BufferedReader br;
        Map<String, String> lectureMap;

        CachedRowSet rs = DBWrapper.getRecords(ConfigLoader.COURSE_TABLE, new String[]{ConfigLoader.ID_COLUMN_OF_ALL_TABLES, ConfigLoader.COURSE_NAME_COLUMN}, null, null);

        try{

            /*
                For every Course in the database:
                Go through every course in courseArray and find courses that matches on "name".
                If match, retrieve data about the course's lectures from CBS' API and fill the course's events-Array with these.
                Hereafter, go through every single Lecture in events-array and insert them as entries in the "lectures" table
            */

            while(rs.next()){


                String name = rs.getString(ConfigLoader.COURSE_NAME_COLUMN);


                for (Course course : courseArray){

                    if(course.getId().equals(name)){

                        url = new URL(urlPrefix + course.getId());
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        //Empty Course object necessary to read JSON
                        Course tempCourse = gson.fromJson(br, Course.class);
                        course.setEvents(tempCourse.getEvents());

                        //Insert Lecture for each Lecture object inside every Course object's events.
                        for (Lecture lecture : course.getEvents()){
                            lectureMap = new HashMap<String, String>();

                            lectureMap.put(ConfigLoader.LECTURE_COURSE_ID_COLUMN, course.getId());
                            lectureMap.put(ConfigLoader.LECTURE_TYPE_COLUMN, lecture.getType());
                            lectureMap.put(ConfigLoader.LECTURE_DESCRIPTION_COLUMN, lecture.getDescription());

                            lectureMap.put(ConfigLoader.LECTURE_START_DATE_COLUMN, convertToDateTime(lecture.getStart()));
                            lectureMap.put(ConfigLoader.LECTURE_END_DATE_COLUMN, convertToDateTime(lecture.getEnd()));
                            lectureMap.put(ConfigLoader.LECTURE_LOCATION_COLUMN, lecture.getLocation());

                            DBWrapper.insertIntoRecords(ConfigLoader.LECTURE_TABLE, lectureMap);

                        }
                    }
                }
            }
        }
        catch(MalformedURLException ex){
            ex.printStackTrace();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

    }


    /**
     * Converts the dates of Lectures into a single string, as they are parsed as Arrays.
     * @param dateData Array of date-fragments in String-format.
     * @return A string that matches the DateTime format for SQL Date objects.
     */
    private static String convertToDateTime(List<String> dateData ){
        StringBuilder dateBuilder = new StringBuilder();

        //Adds a missing '0' in date-parts with a single number
        for (int i = 0; i < dateData.size(); i++){
            if (dateData.get(i).length() < 2){
                dateData.set(i, "0" + dateData.get(i));
            }
        }

        //Builds the String so it matches with the DateTime object
        dateBuilder.append(dateData.get(0));
        dateBuilder.append("-");
        //increments the month of the date by one, as there is an error in CBS' API showing dates that are a month behind.
        int month = Integer.parseInt(dateData.get(1))+1;
        dateBuilder.append(String.valueOf(month));
        dateBuilder.append("-");
        dateBuilder.append(dateData.get(2));
        dateBuilder.append(" ");
        dateBuilder.append(dateData.get(3));
        dateBuilder.append(":");
        dateBuilder.append(dateData.get(4));
        dateBuilder.append(":");
        dateBuilder.append("00");

        return dateBuilder.toString();

    }

}
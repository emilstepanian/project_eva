package logic.misc;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import dal.DBWrapper;
import model.entity.Course;
import model.entity.Lecture;
import model.entity.Study;

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
 * them into the database. CBSParser implements Runnable, as it needs to run on a
 * separate thread that continuously once every day updates the objects from CBS' API.
 */
public class CBSParser implements Runnable {
    private static Course[] courseArray;
    private static Gson gson = new Gson();



    /**
     * A thread that is called to parse the data from CBS into the database.
     * Aggregates all the private methods in this class.
     */
    public void run() {
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
     * Then it adds entries into the Study table inside the database
     */
    private static void parseStudiesToDatabase() {

        JsonReader jsonReader;
        JsonParser jparser = new JsonParser();

        Set<String> duplicatesCheck = new HashSet<String>();

        /*
            Get studies from the database, if any are registered
         */
        Set<String> databaseCheck = retrieveCurrentEntityRecords(ConfigLoader.STUDY_TABLE, ConfigLoader.STUDY_SHORTNAME_COLUMN);

        try {
            jsonReader = new JsonReader(new FileReader(ConfigLoader.STUDY_DATA_JSON));
            JsonArray jArray = jparser.parse(jsonReader).getAsJsonArray();

            Iterator iterator = jArray.iterator();


            while(iterator.hasNext()){

                JsonObject obj = (JsonObject) iterator.next();
                Map<String, String> studyValues = new HashMap<String, String>();


                /*
                Check if the Study is already in the duplicates check set.
                If not, then insert it. Values are specific to CBS' API, and therefore not interchangeable.
                The replace method from the String class is used to remove the '"' characters from the strings.
                 */
                if(!duplicatesCheck.contains(obj.get(ConfigLoader.CBS_STUDY_SHORTNAME).toString().replace("\"", "").substring(0,5))){

                    /*
                    Next it checks whether or not the study is already registered in the database. If not, create it, else update it.
                     */
                    if(!databaseCheck.contains(obj.get(ConfigLoader.CBS_STUDY_SHORTNAME).toString().replace("\"", "").substring(0,5))){

                        studyValues.put(ConfigLoader.STUDY_SHORTNAME_COLUMN,obj.get(ConfigLoader.CBS_STUDY_SHORTNAME).toString().replace("\"", "").substring(0,5));
                        studyValues.put(ConfigLoader.STUDY_NAME_COLUMN,obj.get(ConfigLoader.CBS_STUDY_STUDY_NAME).toString().replace("\"", ""));
                        duplicatesCheck.add(obj.get(ConfigLoader.CBS_STUDY_SHORTNAME).toString().replace("\"", "").substring(0,5));
                        DBWrapper.insertIntoRecords(ConfigLoader.STUDY_TABLE, studyValues);

                    } else {

                        Study studyToUpdate = (Study) getSingleRecord(ConfigLoader.STUDY_SHORTNAME_COLUMN, obj.get(ConfigLoader.CBS_STUDY_SHORTNAME).toString().replace("\"", "").substring(0, 5),  1);

                        Map<String, String> updatedStudyValues = new HashMap<String, String>();

                        updatedStudyValues.put(ConfigLoader.STUDY_SHORTNAME_COLUMN, obj.get(ConfigLoader.CBS_STUDY_SHORTNAME).toString().replace("\"", "").substring(0, 5));
                        updatedStudyValues.put(ConfigLoader.STUDY_NAME_COLUMN, obj.get(ConfigLoader.CBS_STUDY_STUDY_NAME).toString().replace("\"", ""));

                        Map<String, String> whereParam = new HashMap<String, String>();

                        whereParam.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(studyToUpdate.getId()));

                        DBWrapper.updateRecords(ConfigLoader.STUDY_TABLE, updatedStudyValues, whereParam);

                        duplicatesCheck.add(obj.get(ConfigLoader.CBS_STUDY_SHORTNAME).toString().replace("\"", "").substring(0, 5));


                    }


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
            Get courses from the database, if any are registered
             */
            Set<String> databaseCheck = retrieveCurrentEntityRecords(ConfigLoader.COURSE_TABLE, ConfigLoader.COURSE_CODE_COLUMN);

           /*
           Go through the list of studies pulled from the database and save the five first characters of its shortname
           together with the matching ID from the database in the studyAttributes hashmap
           */
            while(rs.next()){


                String substring = rs.getString(ConfigLoader.STUDY_SHORTNAME_COLUMN).substring(0,5);

                studyAttributes.put(substring, rs.getString(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            }


           /*
           Go through the array of courses and check if there is any matches between the courses and studies in the studyAttribute hashmap.
           If so, insert the course in the database with the matching study_id
           */
            for (Course course : courseArray) {

                /*
                However, first it checks whether or not the course is already registered in the database. If not, create it, else update it
                 */
                if (!databaseCheck.contains(course.getId())) {

                    String substring = course.getId().substring(0, 5);

                    if (studyAttributes.containsKey(substring)) {

                        courseMap.put(ConfigLoader.COURSE_NAME_COLUMN, course.getDisplaytext());
                        courseMap.put(ConfigLoader.COURSE_CODE_COLUMN, course.getId());
                        courseMap.put(ConfigLoader.COURSE_STUDY_ID_COLUMN, studyAttributes.get(substring));

                        DBWrapper.insertIntoRecords(ConfigLoader.COURSE_TABLE, courseMap);
                    }
                } else {

                    Course courseToUpdate = (Course) getSingleRecord(ConfigLoader.COURSE_CODE_COLUMN, course.getId(), 2);


                    Map<String, String> updatedCourseValues = new HashMap<String, String>();

                    updatedCourseValues.put(ConfigLoader.COURSE_NAME_COLUMN, course.getDisplaytext());

                    Map<String, String> whereParam = new HashMap<String, String>();

                    whereParam.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(courseToUpdate.getId()));

                    DBWrapper.updateRecords(ConfigLoader.COURSE_TABLE, updatedCourseValues, whereParam);

                }
            }


        } catch(SQLException ex){

            System.out.println(ex.getMessage());


        }
    }

    /**
     * Parses and inserts records in the lecture table in the database.
     */
    private static void parseLecturesToDatabase() throws SQLException{

        String urlPrefix = ConfigLoader.CBS_API_LINK;
        URL url;
        HttpURLConnection conn;
        BufferedReader br;
        Map<String, String> lectureMap;

        CachedRowSet rs = DBWrapper.getRecords(ConfigLoader.COURSE_TABLE, new String[]{ConfigLoader.ID_COLUMN_OF_ALL_TABLES, ConfigLoader.COURSE_CODE_COLUMN}, null, null);


        try{

            /*
                For every Course in the database:
                Go through every course in courseArray and find courses that matches on "name".
                If match, retrieve data about the course's lectures from CBS' API and fill the course's events-Array with these.
                Hereafter, go through every single Lecture in events-array and insert them as entries in the "lectures" table
            */

            while(rs.next()){


                String name = rs.getString(ConfigLoader.COURSE_CODE_COLUMN);


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
                            for (Lecture lecture : course.getEvents()) {

                                /*
                                Error found here. As it is not possible to get the ID from the incoming lectures from CBS, as it is auto-incremented in our own database,
                                this was thought as a solution to pinpoint the specific lecture in the database, using the other values of the it.
                                However, the error is that these values are changed if an update to a lecture happens, and therefore it will never find a match in the database.
                                This cancels out the whole idea of this algorithm in the first place. It is however left for future improvements.
                                 */

                                lectureMap = new HashMap<String, String>();

                                    lectureMap.put(ConfigLoader.LECTURE_COURSE_CODE_COLUMN, course.getId());
                                    lectureMap.put(ConfigLoader.LECTURE_TYPE_COLUMN, lecture.getType());
                                    lectureMap.put(ConfigLoader.LECTURE_DESCRIPTION_COLUMN, lecture.getDescription());

                                    lectureMap.put(ConfigLoader.LECTURE_START_DATE_COLUMN, convertToDateTime(lecture.getStart()));
                                    lectureMap.put(ConfigLoader.LECTURE_END_DATE_COLUMN, convertToDateTime(lecture.getEnd()));
                                    lectureMap.put(ConfigLoader.LECTURE_LOCATION_COLUMN, lecture.getLocation());

                                    CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.LECTURE_TABLE, null, lectureMap, null);

                                    if(rowSet.size() < 1){
                                        DBWrapper.insertIntoRecords(ConfigLoader.LECTURE_TABLE, lectureMap);

                                    } else {
                                        rowSet.next();

                                    Map<String, String> updatedLectureValues = new HashMap<String, String>();

                                    updatedLectureValues.put(ConfigLoader.LECTURE_COURSE_CODE_COLUMN, course.getId());
                                    updatedLectureValues.put(ConfigLoader.LECTURE_TYPE_COLUMN, lecture.getType());
                                    updatedLectureValues.put(ConfigLoader.LECTURE_DESCRIPTION_COLUMN, lecture.getDescription());

                                    updatedLectureValues.put(ConfigLoader.LECTURE_START_DATE_COLUMN, convertToDateTime(lecture.getStart()));
                                    updatedLectureValues.put(ConfigLoader.LECTURE_END_DATE_COLUMN, convertToDateTime(lecture.getEnd()));
                                    updatedLectureValues.put(ConfigLoader.LECTURE_LOCATION_COLUMN, lecture.getLocation());


                                    Map<String, String> whereParam = new HashMap<String, String>();

                                    whereParam.put(ConfigLoader.ID_COLUMN_OF_ALL_TABLES, String.valueOf(rowSet.getInt("id")));

                                    DBWrapper.updateRecords(ConfigLoader.LECTURE_TABLE, updatedLectureValues, whereParam);

                                }
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

    /**
     * Method used to retrieve the needed entities from the database in any specific situation in the CBSParser
     * @param table the table to retrieve data from
     * @param column the column inside the table that is needed to use as a "already registered" checker.
     *               As the ID of the entities are not coming from the CBS' API, the system is not able to do the check by IDs
     * @return The set of records of the one column
     */
    private static Set<String> retrieveCurrentEntityRecords(String table, String column){
        Set<String> dbEntities = new HashSet<String>();
        String[] attributes = {column};


        try {


            CachedRowSet rowSet = DBWrapper.getRecords(table, attributes, null, null);

            if(table.equals(ConfigLoader.STUDY_TABLE)) {
                while (rowSet.next()) {
                    dbEntities.add(rowSet.getString(column));
                }
            } else if (table.equals(ConfigLoader.COURSE_TABLE)) {
                while (rowSet.next()) {
                    dbEntities.add(rowSet.getString(column));
                }
            } else if (table.equals(ConfigLoader.LECTURE_TABLE)){
                while (rowSet.next()) {
                    dbEntities.add(rowSet.getString(column));
                    }
            }


        } catch (SQLException ex) {


        }

        return dbEntities;
    }


    /**
     * Used by the parser to get the single record that matches an entity coming from the CBS' API, to be able to update it
     * @param column the column inside the table that is needed to use as a "already registered" checker.
     *               As the ID of the entities are not coming from the CBS' API, the system is not able to do the check by IDs
     * @param value The specific value from the column that is needed to construct the WHERE param
     * @param modification specifies what kind of entity is wished to be returned
     * @return the specific entity object that was selected by the modification
     */
    private static Object getSingleRecord(String column, String value, int modification){
        switch (modification){
            case 1:
                return getSingleStudy(column, value);
            case 2:
                return getSingleCourse(column, value);
            case 3:
                return getSingleLecture(column, value);
            default:
                return null;

        }
    }

    /**
     * Used by getSingleRecord() to return a Study
     * @return the specified Study.
     */
    private static Study getSingleStudy(String column, String value){
        Study study = new Study();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put(column, value);

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.STUDY_TABLE, null, whereParam, null);
            rowSet.next();
            study.setId(rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            study.setShortname(rowSet.getString(ConfigLoader.STUDY_SHORTNAME_COLUMN));
            study.setName(rowSet.getString(ConfigLoader.STUDY_NAME_COLUMN));

        } catch(SQLException ex){
            ex.getMessage();

        }
        return study;
    }

    /**
     * Used by getSingleRecord() to return a Course
     * @return the specified Course.
     */
   private static Course getSingleCourse(String column, String value){
        Course course = new Course();

        try {
            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put(column, value);

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.COURSE_TABLE, null, whereParam, null);
            rowSet.next();
            course.setId(rowSet.getString(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));
            course.setCode(rowSet.getString(ConfigLoader.COURSE_CODE_COLUMN));
            course.setDisplaytext(rowSet.getString(ConfigLoader.COURSE_NAME_COLUMN));


        } catch(SQLException ex){
            ex.getMessage();


        }
        return course;
    }
    /**
     * Used by getSingleRecord() to return a Lecture
     * @return the specified Lecture.
     */
    private static Lecture getSingleLecture(String column, String value) {
        Lecture lecture = new Lecture();

        try {

            Map<String, String> whereParam = new HashMap<String, String>();
            whereParam.put(column, value);

            CachedRowSet rowSet = DBWrapper.getRecords(ConfigLoader.LECTURE_TABLE, null, whereParam, null);
            rowSet.next();

            lecture.setLectureId(rowSet.getInt(ConfigLoader.ID_COLUMN_OF_ALL_TABLES));

        } catch (SQLException ex) {
            ex.getMessage();
        }

        return lecture;
    }



}
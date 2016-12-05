package model.entity;

/**
 * Created by emilstepanian on 19/11/2016.
 */

public class Course {

    /**
     * Events is our collection of Lecture objects.
     * It is called events to avoid conflicts in the parsing of JSON data,
     * as the data received from CBS specifices them as "events.
     */
    private Lecture[] events;
    private int databaseId;
    private String id;
    private String code;
    private String displaytext;

    public Course() {
    }

    public Course(String id, String displaytext, String code) {
        this.id = id;
        this.displaytext = displaytext;
        this.code = code;
    }

    /**
     * Is called to get the lectures for the course.
     * @return Array of Lecture objects
     */
    public Lecture[] getEvents() {
        return events;
    }

    /**
     * Fills Lecture objects into a Course object's 'events' array variable.
     * @param events The array of Lecture objects that is going to be filled
     *               into the array-variable "events" in the Course object.
     */
    public void setEvents(Lecture[] events) {
        this.events = events;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplaytext() {
        return displaytext;
    }

    public void setDisplaytext(String displaytext) {
        this.displaytext = displaytext;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



    @Override
    public String toString() {
        return "Course{" +
                "lectures=" + events +
                ", id=" + id +
                ", displaytext='" + displaytext + '\'' +
                '}';
    }
}
package model.entity;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class Lecture {

    private int id;
    private String courseCode;
    private String type;
    private String description;
    private List<String> start = new ArrayList<String>();
    private List<String> end = new ArrayList<String>();
    private Date startDate;
    private Date endDate;
    private String location;

    public Lecture() {
    }


    public Lecture(String courseCode, String type, String description, Date startDate, Date endDate, String location, int id) {
        this.courseCode = courseCode;
        this.type = type;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.id = id;
    }

    public int getId() {
        return id;
    }



    public int getLectureId() {
        return id;
    }

    public void setLectureId(int id) {
        this.id = id;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getStart() {
        return start;
    }

    public void setStart(List<String> start) {
        this.start = start;
    }

    public List<String> getEnd() {
        return end;
    }

    public void setEnd(List<String> end) {
        this.end = end;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "\nLecture{" +
                ",\n type='" + type + '\'' +
                ",\n description='" + description + '\'' +
                ",\n start=" + start +
                ",\n end=" + end +
                ",\n location='" + location + '\'' +
                '}';
    }
}

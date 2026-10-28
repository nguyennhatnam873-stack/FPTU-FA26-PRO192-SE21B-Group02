/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import studentgrademanager.models.Course;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.models.PracticeCourse;

public class CourseManager {

    private final Map<String, Course> courses =
            new LinkedHashMap<String, Course>();


    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException(
                    "Mon hoc khong duoc null.");
        }

        String code = course.getCode()
                .trim().toUpperCase(Locale.ROOT);

        if (courses.containsKey(code)) {
            throw new IllegalArgumentException(
                    "Ma mon hoc da ton tai: " + code);
        }

        courses.put(code, course);
    }


    public void addTheoryCourse(
            String code, String name, int credits) {

        Course course = new TheoryCourse(code, name, credits);
        addCourse(course);
    }

    public void addPracticeCourse(
            String code, String name, int credits) {

        Course course = new PracticeCourse(code, name, credits);
        addCourse(course);
    }

    public Course findCourse(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        return courses.get(
                code.trim().toUpperCase(Locale.ROOT));
    }


    public List<Course> getAllCourses() {
        return Collections.unmodifiableList(
                new ArrayList<Course>(courses.values()));
    }

  
    public boolean existsCourse(String code) {
        if (code == null) {
            return false;
        }

        return courses.containsKey(
                code.trim().toUpperCase(Locale.ROOT));
    }

    public int getCourseCount() {
        return courses.size();
    }
    
public void loadCourses(List<Course> data) {
    courses.clear();

    for (Course course : data) {
        addCourse(course);
    }
}

}

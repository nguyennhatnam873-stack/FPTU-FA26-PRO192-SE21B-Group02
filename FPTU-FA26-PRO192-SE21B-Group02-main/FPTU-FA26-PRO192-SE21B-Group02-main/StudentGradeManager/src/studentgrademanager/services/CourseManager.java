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

    public synchronized void addCourse(String type, String courseId, String courseName, int credits) {
        this.addCourse(createCourse(type, courseId, courseName, credits));
    }
    public Course findCourse(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return courses.get(id.trim().toUpperCase(Locale.ROOT));
    }
    public Course findCourseById(String id) {
        return findCourse(id);
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
    public synchronized void clear() {
        this.courses.clear();
    }
    public interface CourseUsageChecker {
        boolean hasGrades(String courseId);
    }
    private CourseUsageChecker usageChecker;
    public synchronized void setUsageChecker(CourseUsageChecker checker) {
        this.usageChecker = checker;
    }
    public synchronized boolean canModifyCourse(String courseId) {
        if (usageChecker == null) return true;
        return !usageChecker.hasGrades(courseId);
    }
    public synchronized boolean updateCourseCredits(String courseId, int newCredits) {
        if (!canModifyCourse(courseId)) {
            throw new IllegalStateException("Khong the sua tin chi cua mon hoc da co sinh vien dang ky!");
        }
        Course c = findCourse(courseId);
        if (c == null) return false;
        c.setCredits(newCredits);
        return true;
    }
    public static int parseAndValidateCredits(String rawCredits) {
        if (rawCredits == null || rawCredits.trim().isEmpty()) {
            throw new IllegalArgumentException("So tin chi khong duoc de trong.");
        }
        try {
            int credits = Integer.parseInt(rawCredits.trim());
            if (credits < Course.MIN_CREDITS || credits > Course.MAX_CREDITS) {
                throw new IllegalArgumentException("So tin chi phai tu " + Course.MIN_CREDITS + " den " + Course.MAX_CREDITS + ".");
            }
            return credits;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("So tin chi phai la so nguyen hop le.");
        }
    }
    public static Course createCourse(String type, String courseId, String courseName, int credits) {
        if (type == null) throw new IllegalArgumentException("Loai mon hoc khong duoc de trong.");
        String cleanType = type.trim().toUpperCase(Locale.ROOT);
        if ("THEORY".equals(cleanType) || "LY THUYET".equals(cleanType) || "LY_THUYET".equals(cleanType)) {
            return new TheoryCourse(courseId, courseName, credits);
        } else if ("PRACTICE".equals(cleanType) || "THUC HANH".equals(cleanType) || "THUC_HANH".equals(cleanType)) {
            return new PracticeCourse(courseId, courseName, credits);
        } else {
            throw new IllegalArgumentException("Loai mon hoc khong hop le.");
        }
    }
}

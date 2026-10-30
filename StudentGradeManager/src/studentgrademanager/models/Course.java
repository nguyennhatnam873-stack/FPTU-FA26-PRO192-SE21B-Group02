<<<<<<< HEAD
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.models;

import java.io.Serializable;
import java.util.Locale;

public abstract class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String code;
    private final String name;
    private final int credits;

    public Course(String code, String name, int credits) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ma mon khong duoc rong."
            );
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ten mon khong duoc rong."
            );
        }

        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException(
                "Tin chi phai tu 1 den 6."
            );
        }

        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.name = name.trim();
        this.credits = credits;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
=======
package studentgrademanager.models;

import java.io.Serializable;
import java.util.Objects;

public abstract class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MIN_CREDITS = 1;
    public static final int MAX_CREDITS = 6;

    private String courseId;
    private String courseName;
    private int credits;

    public Course(String courseId, String courseName, int credits) {
        if (!isValidCourseId(courseId)) {
            throw new IllegalArgumentException("Ma mon hoc khong đuoc đe trong.");
        }
        if (!isValidCourseName(courseName)) {
            throw new IllegalArgumentException("Ten mon hoc khong đuoc đe trong..");
        }
        if (!isValidCredits(credits)) {
            throw new IllegalArgumentException("So tin chi phai la so nguyen tu " + MIN_CREDITS + " đen " + MAX_CREDITS + ".");
        }

        this.courseId = courseId.trim().toUpperCase();
        this.courseName = courseName.trim();
        this.credits = credits;
    }

    public String getCourseId() {
        return this.courseId;
    }

    public String getCourseName() {
        return this.courseName;
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
    }

    public int getCredits() {
        return this.credits;
    }

<<<<<<< HEAD
    public abstract double calculateFinalGrade(
        double processScore,
        double examScore
    );

    public abstract String getCourseType();
=======
    public abstract double calculateFinalScore(double process, double exam);

    public abstract String getCourseType();

    public abstract String getTypeCode();

    public abstract double getProcessWeight();

    public abstract double getFinalExamWeight();

    public Double calculateFinalScore(Double process, Double exam) {
        if (process == null || exam == null) {
            return null;
        }
        return this.calculateFinalScore(process.doubleValue(), exam.doubleValue());
    }

    public Double calculateFinalGrade(Double processGrade, Double finalExamGrade) {
        return this.calculateFinalScore(processGrade, finalExamGrade);
    }

    public static boolean isValidCourseId(String courseId) {
        return courseId != null && !courseId.trim().isEmpty();
    }

    public static boolean isValidCourseName(String courseName) {
        return courseName != null && !courseName.trim().isEmpty();
    }

    public static boolean isValidCredits(int credits) {
        return credits >= MIN_CREDITS && credits <= MAX_CREDITS;
    }

    public void setCourseName(String courseName) {
        if (!isValidCourseName(courseName)) {
            throw new IllegalArgumentException("Ten mon hoc khong đuoc đe trong.");
        }
        this.courseName = courseName.trim();
    }

    public void setCredits(int credits) {
        if (!isValidCredits(credits)) {
            throw new IllegalArgumentException("So tin chi phai la so nguyen tu " + MIN_CREDITS + " đen " + MAX_CREDITS + ".");
        }
        this.credits = credits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Course)) {
            return false;
        }
        Course course = (Course) o;
        return Objects.equals(this.courseId, course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.courseId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d tín chỉ)", 
                this.getCourseType(), this.courseId, this.courseName, this.credits);
    }
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
}

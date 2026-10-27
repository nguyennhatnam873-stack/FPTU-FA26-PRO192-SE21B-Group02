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
            throw new IllegalArgumentException("Mã môn học không được để trống.");
        }
        if (!isValidCourseName(courseName)) {
            throw new IllegalArgumentException("Tên môn học không được để trống.");
        }
        if (!isValidCredits(credits)) {
            throw new IllegalArgumentException("Số tín chỉ phải là số nguyên từ " + MIN_CREDITS + " đến " + MAX_CREDITS + ".");
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
    }

    public int getCredits() {
        return this.credits;
    }

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
            throw new IllegalArgumentException("Tên môn học không được để trống.");
        }
        this.courseName = courseName.trim();
    }

    public void setCredits(int credits) {
        if (!isValidCredits(credits)) {
            throw new IllegalArgumentException("Số tín chỉ phải là số nguyên từ " + MIN_CREDITS + " đến " + MAX_CREDITS + ".");
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
}

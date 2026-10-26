package studentgrademanager.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Lớp trừu tượng Course theo đúng Class Diagram đề tài P05.
 * Phụ trách: Người 2 — Môn học & đánh giá
 */
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

    // ==================== CÁC PHƯƠNG THỨC THEO ĐÚNG CLASS DIAGRAM ====================

    public String getCourseId() {
        return this.courseId;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public int getCredits() {
        return this.credits;
    }

    /**
     * Phương thức đa hình tính điểm tổng kết theo đúng Class Diagram:
     * + calculateFinalScore(process: double, exam: double): double
     */
    public abstract double calculateFinalScore(double process, double exam);

    /**
     * Phương thức đa hình lấy loại môn học theo đúng Class Diagram:
     * + getCourseType(): String
     */
    public abstract String getCourseType();

    // ==================== HỖ TRỢ ĐIỂM THIẾU (NULL) & TƯƠNG THÍCH ====================

    /**
     * Nạp chồng calculateFinalScore hỗ trợ xử lý khi điểm thành phần bị thiếu (null),
     * trả về null khi thiếu một thành phần (theo đúng yêu cầu checklist đề P05).
     */
    public Double calculateFinalScore(Double process, Double exam) {
        if (process == null || exam == null) {
            return null;
        }
        return this.calculateFinalScore(process.doubleValue(), exam.doubleValue());
    }

    /**
     * Alias phương thức tính điểm để tương thích toàn diện với các module.
     */
    public Double calculateFinalGrade(Double processGrade, Double finalExamGrade) {
        return this.calculateFinalScore(processGrade, finalExamGrade);
    }

    public String getTypeCode() {
        return "LÝ THUYẾT".equalsIgnoreCase(this.getCourseType()) || "THEORY".equalsIgnoreCase(this.getCourseType()) 
                ? "THEORY" : "PRACTICE";
    }

    // ==================== VALIDATION & SETTERS ====================

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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Course)) {
            return false;
        }
        Course other = (Course) obj;
        return this.courseId.equalsIgnoreCase(other.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.courseId.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d tín chỉ)", 
                this.getCourseType(), this.courseId, this.courseName, this.credits);
    }
}

package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentgrademanager.models.Course;
import studentgrademanager.models.PracticeCourse;
import studentgrademanager.models.TheoryCourse;

public class CourseManager implements Serializable {
    private static final long serialVersionUID = 1L;

    public interface CourseUsageChecker {
        boolean hasGrades(String courseId);
    }

    private List<Course> courses;
    private transient CourseUsageChecker usageChecker;

    public CourseManager() {
        this.courses = new ArrayList<Course>();
        this.usageChecker = null;
    }

    public synchronized void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Đối tượng môn học không được null.");
        }

        if (this.findCourse(course.getCourseId()) != null) {
            throw new IllegalArgumentException("Mã môn học '" + course.getCourseId() + "' đã tồn tại trong hệ thống. Không thể thêm trùng.");
        }

        this.courses.add(course);
    }

    public Course findCourse(String id) {
        if (id == null) {
            return null;
        }
        String cleanId = id.trim();
        for (Course c : this.courses) {
            if (c.getCourseId().equalsIgnoreCase(cleanId)) {
                return c;
            }
        }
        return null;
    }

    public List<Course> getAllCourses() {
        return Collections.unmodifiableList(new ArrayList<Course>(this.courses));
    }

    public Course findCourseById(String id) {
        return this.findCourse(id);
    }

    public boolean exists(String id) {
        return this.findCourse(id) != null;
    }

    public int getCourseCount() {
        return this.courses.size();
    }

    public synchronized void clear() {
        this.courses.clear();
    }

    public void setUsageChecker(CourseUsageChecker checker) {
        this.usageChecker = checker;
    }

    public boolean canModifyCourse(String courseId) {
        if (this.usageChecker == null) {
            return true;
        }
        return !this.usageChecker.hasGrades(courseId);
    }

    public synchronized void updateCourseCredits(String courseId, int newCredits) {
        Course course = this.findCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Không tìm thấy môn học có mã '" + courseId + "'.");
        }

        if (!this.canModifyCourse(courseId)) {
            throw new IllegalStateException("Không được phép thay đổi số tín chỉ vì môn '" 
                    + courseId + "' đã có sinh viên ghi nhận kết quả điểm!");
        }

        course.setCredits(newCredits);
    }

    public synchronized void updateCourseName(String courseId, String newName) {
        Course course = this.findCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Không tìm thấy môn học có mã '" + courseId + "'.");
        }
        course.setCourseName(newName);
    }

    public static int parseAndValidateCredits(String creditsInput) {
        if (creditsInput == null || creditsInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Số tín chỉ không được để trống.");
        }

        String trimmed = creditsInput.trim();
        if (trimmed.contains(".") || trimmed.contains(",")) {
            throw new IllegalArgumentException("Số tín chỉ phải là số nguyên (từ chối số thập phân: " + trimmed + ").");
        }

        int credits;
        try {
            credits = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Số tín chỉ phải là số nguyên hợp lệ: " + trimmed);
        }

        if (!Course.isValidCredits(credits)) {
            throw new IllegalArgumentException("Số tín chỉ phải nằm trong khoảng từ " 
                    + Course.MIN_CREDITS + " đến " + Course.MAX_CREDITS + " (giá trị nhập: " + credits + ").");
        }

        return credits;
    }

    public static Course createCourse(String type, String courseId, String courseName, int credits) {
        if (type == null) {
            throw new IllegalArgumentException("Loại môn học không được để trống.");
        }
        String cleanType = type.trim().toUpperCase();

        if ("THEORY".equals(cleanType) || "LÝ THUYẾT".equalsIgnoreCase(cleanType) || "LY THUET".equalsIgnoreCase(cleanType) || "LT".equalsIgnoreCase(cleanType)) {
            return new TheoryCourse(courseId, courseName, credits);
        } else if ("PRACTICE".equals(cleanType) || "THỰC HÀNH".equalsIgnoreCase(cleanType) || "THUC HANH".equalsIgnoreCase(cleanType) || "TH".equalsIgnoreCase(cleanType)) {
            return new PracticeCourse(courseId, courseName, credits);
        } else {
            throw new IllegalArgumentException("Loại môn học không hợp lệ: '" + type + "'. Chỉ chấp nhận 'THEORY' hoặc 'PRACTICE'.");
        }
    }
}

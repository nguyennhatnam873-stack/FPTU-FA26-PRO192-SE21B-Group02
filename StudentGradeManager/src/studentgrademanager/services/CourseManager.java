package studentgrademanager.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentgrademanager.models.Course;
import studentgrademanager.models.PracticeCourse;
import studentgrademanager.models.TheoryCourse;

/**
 * Lớp CourseManager theo đúng Class Diagram đề tài P05.
 * Phụ trách: Người 2 — Môn học & đánh giá
 * 
 * Thuộc tính:
 *   - courses: List<Course>
 * Phương thức:
 *   + addCourse(course: Course): void
 *   + findCourse(id: String): Course
 *   + getAllCourses(): List<Course>
 */
public class CourseManager {

    /**
     * Interface kiểm tra môn học đã có kết quả điểm hay chưa (phối hợp Người 3 & Người 4).
     */
    public interface CourseUsageChecker {
        boolean hasGrades(String courseId);
    }

    // Thuộc tính chuẩn theo Class Diagram: - courses: List<Course>
    private List<Course> courses;
    private CourseUsageChecker usageChecker;

    /**
     * Khởi tạo CourseManager.
     */
    public CourseManager() {
        this.courses = new ArrayList<Course>();
        this.usageChecker = null;
    }

    // ==================== CÁC PHƯƠNG THỨC THEO ĐÚNG CLASS DIAGRAM ====================

    /**
     * Thêm môn học vào hệ thống theo đúng Class Diagram:
     * + addCourse(course: Course): void
     * 
     * Kiểm tra tính duy nhất của mã môn học (case-insensitive).
     * @param course Đối tượng môn học cần thêm
     * @throws IllegalArgumentException nếu course null hoặc trùng mã
     */
    public synchronized void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Đối tượng môn học không được null.");
        }

        if (this.findCourse(course.getCourseId()) != null) {
            throw new IllegalArgumentException("Mã môn học '" + course.getCourseId() + "' đã tồn tại trong hệ thống. Không thể thêm trùng.");
        }

        this.courses.add(course);
    }

    /**
     * Tìm kiếm môn học theo mã theo đúng Class Diagram:
     * + findCourse(id: String): Course
     * 
     * @param id Mã môn học cần tìm (tự động trim, không phân biệt hoa thường)
     * @return Đối tượng Course nếu tìm thấy, hoặc null nếu không tồn tại
     */
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

    /**
     * Lấy toàn bộ danh sách môn học theo đúng Class Diagram:
     * + getAllCourses(): List<Course>
     * 
     * @return Danh sách các môn học hiện có
     */
    public List<Course> getAllCourses() {
        return Collections.unmodifiableList(new ArrayList<Course>(this.courses));
    }

    // ==================== CÁC TIỆN ÍCH NGHIỆP VỤ BỔ TRỢ ====================

    /**
     * Alias tìm kiếm môn học để tương thích với các module khác.
     */
    public Course findCourseById(String id) {
        return this.findCourse(id);
    }

    /**
     * Kiểm tra mã môn học đã tồn tại hay chưa.
     */
    public boolean exists(String id) {
        return this.findCourse(id) != null;
    }

    /**
     * Số lượng môn học trong hệ thống.
     */
    public int getCourseCount() {
        return this.courses.size();
    }

    /**
     * Xóa sạch danh sách môn học (khi reload dữ liệu).
     */
    public synchronized void clear() {
        this.courses.clear();
    }

    /**
     * Đăng ký bộ kiểm tra trạng thái sử dụng của môn học.
     */
    public void setUsageChecker(CourseUsageChecker checker) {
        this.usageChecker = checker;
    }

    /**
     * Kiểm tra xem môn học có được phép sửa (tín chỉ) hay không.
     */
    public boolean canModifyCourse(String courseId) {
        if (this.usageChecker == null) {
            return true;
        }
        return !this.usageChecker.hasGrades(courseId);
    }

    /**
     * Cập nhật số tín chỉ (bảo vệ: không cho phép đổi khi môn học đã có kết quả điểm).
     */
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

    /**
     * Cập nhật tên môn học.
     */
    public synchronized void updateCourseName(String courseId, String newName) {
        Course course = this.findCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Không tìm thấy môn học có mã '" + courseId + "'.");
        }
        course.setCourseName(newName);
    }

    /**
     * Kiểm tra và phân tích số tín chỉ nguyên từ 1 đến 6.
     * Từ chối số thập phân (như "1.5"), số âm, 0 hoặc ngoài khoảng 1-6.
     */
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

    /**
     * Factory tạo môn học đúng loại (THEORY / PRACTICE) phục vụ phối hợp Người 5.
     */
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

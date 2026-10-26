package studentgrademanager.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import studentgrademanager.models.Course;
import studentgrademanager.models.PracticeCourse;
import studentgrademanager.models.TheoryCourse;

/**
 * Lớp dịch vụ quản lý danh mục Môn học.
 * Phụ trách: Người 2 — Môn học & đánh giá
 * 
 * Nghiệp vụ hoàn toàn tách biệt khỏi giao diện console/menu:
 * - Kiểm tra tính hợp lệ: Mã duy nhất, tên không rỗng, tín chỉ nguyên 1–6.
 * - Thêm, tìm kiếm và lấy danh sách môn học.
 * - Bảo vệ: Không cho phép đổi loại môn hoặc thay đổi tín chỉ khi môn học đã có kết quả ghi nhận.
 * - Hỗ trợ tạo đúng đối tượng (Factory) phục vụ việc lưu/đọc file của Người 5.
 */
public class CourseManager {

    /**
     * Interface kiểm tra môn học đã có kết quả điểm hay chưa.
     * Cho phép các module khác (GradeManager / Transcript) cắm vào mà không gây phụ thuộc vòng.
     */
    public interface CourseUsageChecker {
        boolean hasGrades(String courseId);
    }

    private final Map<String, Course> courses;
    private CourseUsageChecker usageChecker;

    /**
     * Khởi tạo CourseManager.
     */
    public CourseManager() {
        this.courses = new LinkedHashMap<String, Course>();
        this.usageChecker = null;
    }

    /**
     * Đăng ký bộ kiểm tra trạng thái sử dụng của môn học (đã có kết quả hay chưa).
     */
    public void setUsageChecker(CourseUsageChecker checker) {
        this.usageChecker = checker;
    }

    // ==================== NGHIỆP VỤ VALIDATION ĐẦU VÀO ====================

    /**
     * Kiểm tra và ép kiểu số tín chỉ từ chuỗi nhập vào.
     * Từ chối số thập phân (như "1.5"), số âm, số 0, hoặc số ngoài khoảng 1-6.
     * 
     * @param creditsInput Chuỗi nhập từ người dùng hoặc file
     * @return Số tín chỉ nguyên hợp lệ (1–6)
     * @throws IllegalArgumentException nếu chuỗi không hợp lệ
     */
    public static int parseAndValidateCredits(String creditsInput) {
        if (creditsInput == null || creditsInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Số tín chỉ không được để trống.");
        }

        String trimmed = creditsInput.trim();

        // Kiểm tra nếu người dùng nhập số thực có dấu chấm hoặc dấu phẩy (như 1.5 hoặc 1,5)
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
     * Tạo đối tượng môn học theo loại tương ứng (Factory method).
     * Phục vụ phối hợp Người 5 khi đọc dữ liệu từ file hoặc UI khi nhập mới.
     * 
     * @param type Mã loại ("THEORY" / "Lý thuyết" hoặc "PRACTICE" / "Thực hành")
     * @param courseId Mã môn học
     * @param courseName Tên môn học
     * @param credits Số tín chỉ (1–6)
     * @return Đối tượng Course cụ thể (TheoryCourse hoặc PracticeCourse)
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

    // ==================== CÁC PHƯƠNG THỨC NGHIỆP VỤ ====================

    /**
     * Thêm môn học vào hệ thống.
     * Kiểm tra tính duy nhất của mã môn học (case-insensitive).
     * 
     * @param course Đối tượng môn học cần thêm
     * @throws IllegalArgumentException nếu môn học null hoặc mã môn đã tồn tại
     */
    public synchronized void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Đối tượng môn học không được null.");
        }

        String key = course.getCourseId().toUpperCase();
        if (this.courses.containsKey(key)) {
            throw new IllegalArgumentException("Mã môn học '" + course.getCourseId() + "' đã tồn tại trong hệ thống. Không thể thêm trùng.");
        }

        this.courses.put(key, course);
    }

    /**
     * Tiện ích thêm nhanh môn học bằng các tham số cơ bản.
     */
    public synchronized Course addCourse(String type, String courseId, String courseName, int credits) {
        Course course = createCourse(type, courseId, courseName, credits);
        this.addCourse(course);
        return course;
    }

    /**
     * Tìm kiếm môn học theo mã (không phân biệt chữ hoa/chữ thường, tự động trim).
     * 
     * @param courseId Mã môn học cần tìm
     * @return Đối tượng Course nếu tìm thấy, hoặc null nếu không tồn tại
     */
    public Course findCourseById(String courseId) {
        if (courseId == null) {
            return null;
        }
        return this.courses.get(courseId.trim().toUpperCase());
    }

    /**
     * Kiểm tra xem mã môn học đã tồn tại trong hệ thống hay chưa.
     */
    public boolean exists(String courseId) {
        if (courseId == null) {
            return false;
        }
        return this.courses.containsKey(courseId.trim().toUpperCase());
    }

    /**
     * Lấy toàn bộ danh sách môn học hiện có.
     * Trả về danh sách bất biến (read-only) để đảm bảo an toàn dữ liệu.
     */
    public List<Course> getAllCourses() {
        return Collections.unmodifiableList(new ArrayList<Course>(this.courses.values()));
    }

    /**
     * Số lượng môn học trong hệ thống.
     */
    public int getCourseCount() {
        return this.courses.size();
    }

    /**
     * Xóa sạch danh sách môn học (thường dùng khi reload/nạp lại từ file).
     */
    public synchronized void clear() {
        this.courses.clear();
    }

    // ==================== BẢO VỆ DỮ LIỆU ĐÃ CÓ KẾT QUẢ ====================

    /**
     * Kiểm tra xem một môn học có được phép sửa đổi thông tin (tín chỉ/loại) hay không.
     * Nếu môn học đã có kết quả điểm, thao tác sửa tín chỉ sẽ bị từ chối.
     */
    public boolean canModifyCourse(String courseId) {
        if (this.usageChecker == null) {
            return true;
        }
        return !this.usageChecker.hasGrades(courseId);
    }

    /**
     * Cập nhật số tín chỉ cho môn học.
     * Đảm bảo KHÔNG cho phép thay đổi khi môn học đã có kết quả ghi nhận (yêu cầu D8–D11).
     * 
     * @param courseId Mã môn học
     * @param newCredits Số tín chỉ mới (1–6)
     * @throws IllegalStateException nếu môn học đã có kết quả
     * @throws IllegalArgumentException nếu không tìm thấy môn hoặc tín chỉ sai
     */
    public synchronized void updateCourseCredits(String courseId, int newCredits) {
        Course course = this.findCourseById(courseId);
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
        Course course = this.findCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Không tìm thấy môn học có mã '" + courseId + "'.");
        }
        course.setCourseName(newName);
    }
}

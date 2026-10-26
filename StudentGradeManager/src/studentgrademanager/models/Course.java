package studentgrademanager.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Lớp trừu tượng đại diện cho một môn học chung trong hệ thống.
 * Phụ trách: Người 2 — Môn học & đánh giá
 * 
 * Quy ước:
 * - Mọi thuộc tính đều là private (tính đóng gói 100%).
 * - Sử dụng 'this.' tường minh khi truy xuất thuộc tính và phương thức.
 * - Cung cấp phương thức trừu tượng calculateFinalGrade để các lớp con cài đặt đa hình.
 */
public abstract class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MIN_CREDITS = 1;
    public static final int MAX_CREDITS = 6;

    private String courseId;
    private String courseName;
    private int credits;

    /**
     * Khởi tạo một môn học.
     * 
     * @param courseId Mã môn học (không rỗng, duy nhất)
     * @param courseName Tên môn học (không rỗng)
     * @param credits Số tín chỉ nguyên từ 1 đến 6
     * @throws IllegalArgumentException nếu các giá trị không hợp lệ
     */
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

    /**
     * Phương thức đa hình cốt lõi tính điểm tổng kết của môn học dựa trên hai điểm thành phần.
     * 
     * @param processGrade Điểm quá trình (từ 0.0 đến 10.0, hoặc null nếu chưa có)
     * @param finalExamGrade Điểm cuối kỳ (từ 0.0 đến 10.0, hoặc null nếu chưa có)
     * @return Điểm tổng kết (chưa làm tròn), hoặc null nếu thiếu một trong hai điểm thành phần
     */
    public abstract Double calculateFinalGrade(Double processGrade, Double finalExamGrade);

    /**
     * Lấy tên loại môn học (tiếng Việt có dấu để hiển thị trên giao diện bảng điểm).
     * 
     * @return Tên loại môn: "Lý thuyết" hoặc "Thực hành"
     */
    public abstract String getCourseType();

    /**
     * Lấy mã loại môn học (chuẩn hóa viết hoa dùng cho lưu trữ file CSV/Text).
     * 
     * @return "THEORY" hoặc "PRACTICE"
     */
    public abstract String getTypeCode();

    /**
     * Tỷ trọng điểm quá trình của môn học.
     */
    public abstract double getProcessWeight();

    /**
     * Tỷ trọng điểm cuối kỳ của môn học.
     */
    public abstract double getFinalExamWeight();

    // ==================== KIỂM TRA TÍNH HỢP LỆ (VALIDATION) ====================

    /**
     * Kiểm tra mã môn học có hợp lệ không (không null, sau khi trim không rỗng).
     */
    public static boolean isValidCourseId(String courseId) {
        return courseId != null && !courseId.trim().isEmpty();
    }

    /**
     * Kiểm tra tên môn học có hợp lệ không (không null, sau khi trim không rỗng).
     */
    public static boolean isValidCourseName(String courseName) {
        return courseName != null && !courseName.trim().isEmpty();
    }

    /**
     * Kiểm tra số tín chỉ có thuộc miền nguyên 1–6 hay không.
     */
    public static boolean isValidCredits(int credits) {
        return credits >= MIN_CREDITS && credits <= MAX_CREDITS;
    }

    // ==================== GETTER & SETTER ====================

    public String getCourseId() {
        return this.courseId;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        if (!isValidCourseName(courseName)) {
            throw new IllegalArgumentException("Tên môn học không được để trống.");
        }
        this.courseName = courseName.trim();
    }

    public int getCredits() {
        return this.credits;
    }

    public void setCredits(int credits) {
        if (!isValidCredits(credits)) {
            throw new IllegalArgumentException("Số tín chỉ phải là số nguyên từ " + MIN_CREDITS + " đến " + MAX_CREDITS + ".");
        }
        this.credits = credits;
    }

    // ==================== EQUALS & HASHCODE & TOSTRING ====================

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
                this.getTypeCode(), this.courseId, this.courseName, this.credits);
    }
}

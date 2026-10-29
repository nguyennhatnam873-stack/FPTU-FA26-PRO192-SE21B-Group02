package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

/**
 * Lớp dịch vụ quản lý Điểm số và Kết quả học tập.
 * Phụ trách: Người 4 — Nhập & cập nhật điểm; Người 3 — Cặp SV–Môn
 */
public class GradeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final double MIN_SCORE = 0.0;
    public static final double MAX_SCORE = 10.0;

    public static final String STATUS_COMPLETE = "DU_DIEM";
    public static final String STATUS_INCOMPLETE = "CHUA_DU_DIEM";
    public static final String DU_DIEM = STATUS_COMPLETE;
    public static final String CHUA_DU_DIEM = STATUS_INCOMPLETE;

    private final List<GradeRecord> records;

    public GradeManager() {
        this.records = new ArrayList<GradeRecord>();
    }

    // ==================== VALIDATION MIỀN ĐIỂM ====================

    /**
     * Kiểm tra tính hợp lệ của giá trị điểm số:
     * - Null đại diện cho chưa có điểm (hợp lệ)
     * - Nếu có điểm: nằm trong đoạn [0.0, 10.0], không NaN, không Infinite
     */
    public static boolean isValidScore(Double score) {
        if (score == null) {
            return true;
        }
        return score >= MIN_SCORE && score <= MAX_SCORE && !Double.isNaN(score) && !Double.isInfinite(score);
    }

    public static void validateScore(Double score) {
        if (!isValidScore(score)) {
            throw new IllegalArgumentException("Điểm số phải nằm trong khoảng [" + MIN_SCORE + ", " + MAX_SCORE + "] hoặc để trống.");
        }
    }

    public static Double parseScore(String input) {
        if (input == null || input.trim().isEmpty() || "-".equals(input.trim()) 
                || "EMPTY".equalsIgnoreCase(input.trim()) || "null".equalsIgnoreCase(input.trim())) {
            return null;
        }
        double val = Double.parseDouble(input.trim().replace(',', '.'));
        validateScore(val);
        return val;
    }

    // ==================== CÁC PHƯƠNG THỨC NGHIỆP VỤ ====================

    /**
     * Tìm bản ghi điểm theo MSSV và Mã môn.
     */
    public GradeRecord findRecord(String studentId, String courseId) {
        if (studentId == null || courseId == null) {
            return null;
        }
        String cleanStudentId = studentId.trim();
        String cleanCourseId = courseId.trim();
        for (GradeRecord r : this.records) {
            if (r.getStudent() != null && r.getCourse() != null &&
                r.getStudent().getStudentId().equalsIgnoreCase(cleanStudentId) &&
                r.getCourse().getCourseId().equalsIgnoreCase(cleanCourseId)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Thêm mới hoặc cập nhật điểm cho cặp Sinh viên - Môn học.
     * Đảm bảo không tạo bản ghi trùng lặp cho cùng một cặp.
     */
    public synchronized GradeRecord createOrUpdateRecord(Student student, Course course, Double processScore, Double examScore) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh viên không được để null.");
        }
        if (course == null) {
            throw new IllegalArgumentException("Môn học không được để null.");
        }
        if (!isValidScore(processScore)) {
            throw new IllegalArgumentException("Điểm quá trình phải nằm trong đoạn [0, 10] hoặc null.");
        }
        if (!isValidScore(examScore)) {
            throw new IllegalArgumentException("Điểm cuối kỳ phải nằm trong đoạn [0, 10] hoặc null.");
        }

        GradeRecord existing = this.findRecord(student.getStudentId(), course.getCourseId());
        if (existing != null) {
            existing.setProcessScore(processScore);
            existing.setExamScore(examScore);
            return existing;
        }

        GradeRecord newRecord = new GradeRecord(student, course, processScore, examScore);
        this.records.add(newRecord);
        return newRecord;
    }

    /**
     * Tương thích ngược với tên gọi addOrUpdateGrade.
     */
    public synchronized GradeRecord addOrUpdateGrade(Student student, Course course, Double processScore, Double examScore) {
        return this.createOrUpdateRecord(student, course, processScore, examScore);
    }

    /**
     * Thêm một bản ghi GradeRecord (phục vụ nạp từ file).
     */
    public synchronized void addRecord(GradeRecord record) {
        if (record == null) {
            return;
        }
        GradeRecord existing = this.findRecord(record.getStudent().getStudentId(), record.getCourse().getCourseId());
        if (existing != null) {
            existing.setProcessScore(record.getProcessScore());
            existing.setExamScore(record.getExamScore());
        } else {
            this.records.add(record);
        }
    }

    /**
     * Lấy danh sách điểm theo MSSV.
     */
    public List<GradeRecord> getRecordsByStudent(String studentId) {
        if (studentId == null) {
            return Collections.emptyList();
        }
        String cleanId = studentId.trim();
        List<GradeRecord> result = new ArrayList<GradeRecord>();
        for (GradeRecord r : this.records) {
            if (r.getStudent() != null && r.getStudent().getStudentId().equalsIgnoreCase(cleanId)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Lấy danh sách điểm theo Mã môn học.
     */
    public List<GradeRecord> getRecordsByCourse(String courseId) {
        if (courseId == null) {
            return Collections.emptyList();
        }
        String cleanId = courseId.trim();
        List<GradeRecord> result = new ArrayList<GradeRecord>();
        for (GradeRecord r : this.records) {
            if (r.getCourse() != null && r.getCourse().getCourseId().equalsIgnoreCase(cleanId)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Lấy toàn bộ danh sách bản ghi điểm (chế độ chỉ đọc).
     */
    public List<GradeRecord> getAllRecords() {
        return Collections.unmodifiableList(new ArrayList<GradeRecord>(this.records));
    }

    /**
     * Số lượng bản ghi điểm hiện có trong hệ thống.
     */
    public int getRecordCount() {
        return this.records.size();
    }

    /**
     * Kiểm tra môn học đã phát sinh điểm hay chưa (dùng để chặn xóa/sửa môn học).
     */
    public boolean hasGrades(String courseId) {
        if (courseId == null) {
            return false;
        }
        String cleanId = courseId.trim();
        for (GradeRecord r : this.records) {
            if (r.getCourse() != null && r.getCourse().getCourseId().equalsIgnoreCase(cleanId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tương thích ngược với tên gọi hasGradesForCourse.
     */
    public boolean hasGradesForCourse(String courseId) {
        return this.hasGrades(courseId);
    }

    /**
     * Xóa toàn bộ dữ liệu điểm trong bộ nhớ.
     */
    public synchronized void clear() {
        this.records.clear();
    }
}

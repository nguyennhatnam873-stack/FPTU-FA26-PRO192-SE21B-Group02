package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

/**
 * Lớp dịch vụ quản lý Điểm số và Kết quả học tập.
 * Phụ trách: Người 4 — Nhập & cập nhật điểm
 * 
 * Chuẩn môn học: JDK 8, NetBeans 17, mọi thuộc tính private,
 * viết tường minh this. và super. khi truy cập thành viên.
 */
public class GradeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final double MIN_SCORE = 0.0;
    public static final double MAX_SCORE = 10.0;
    public static final String CHUA_DU_DIEM = "CHUA_DU_DIEM";
    public static final String DU_DIEM = "DU_DIEM";

    private final List<GradeRecord> records;

    public GradeManager() {
        this.records = new ArrayList<GradeRecord>();
    }

    // ==================== VALIDATION MIỀN ĐIỂM ====================

    /**
     * Kiểm tra tính hợp lệ của giá trị điểm số.
     * Điểm hợp lệ phải:
     * - Có thể null (đại diện cho điểm thiếu / chưa có).
     * - Điểm 0.0 là hợp lệ và khác hoàn toàn với điểm thiếu.
     * - Nếu không null: không NaN, không vô cực, thuộc đoạn [0.0, 10.0].
     * 
     * @param score Giá trị điểm số cần kiểm tra
     * @throws IllegalArgumentException nếu giá trị không hợp lệ
     */
    public static void validateScore(Double score) {
        if (score == null) {
            return; // Hợp lệ (điểm thiếu)
        }
        if (score.isNaN() || score.isInfinite()) {
            throw new IllegalArgumentException("Điểm số không được là NaN hoặc vô cực.");
        }
        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException(String.format(Locale.US,
                    "Điểm số phải nằm trong khoảng [%.1f, %.1f] (giá trị nhập: %s).",
                    MIN_SCORE, MAX_SCORE, score));
        }
    }

    /**
     * Parse điểm từ chuỗi nhập vào.
     * Chuỗi null, rỗng, "-", "EMPTY", "NULL" -> trả về null (điểm thiếu).
     * Điểm số có thể nhập dùng dấu chấm (.) hoặc dấu phẩy (,).
     * Từ chối chuỗi sai kiểu ("abc", "NaN", "Infinity", v.v.).
     * 
     * @param input Chuỗi nhập vào
     * @return Double hợp lệ trong [0.0, 10.0] hoặc null nếu là điểm thiếu
     * @throws IllegalArgumentException nếu định dạng sai hoặc ngoài miền điểm
     */
    public static Double parseScore(String input) {
        if (input == null) {
            return null;
        }
        String clean = input.trim();
        if (clean.isEmpty() || "-".equals(clean) || "EMPTY".equalsIgnoreCase(clean) || "NULL".equalsIgnoreCase(clean)) {
            return null; // Điểm thiếu
        }

        // Chặn chuỗi NaN / Infinity trước khi parseDouble nhận dạng đặc biệt
        if ("NaN".equalsIgnoreCase(clean) || "Infinity".equalsIgnoreCase(clean)
                || "+Infinity".equalsIgnoreCase(clean) || "-Infinity".equalsIgnoreCase(clean)) {
            throw new IllegalArgumentException("Điểm số không được là NaN hoặc vô cực.");
        }

        clean = clean.replace(',', '.');
        double val;
        try {
            val = Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Định dạng điểm không hợp lệ: '" + input + "'");
        }

        validateScore(val);
        return val;
    }

    /**
     * Alias hỗ trợ tương thích với các module gọi tên parseAndValidateScore.
     */
    public static Double parseAndValidateScore(String input) {
        return parseScore(input);
    }

    // ==================== THAO TÁC KẾT QUẢ ====================

    /**
     * Tìm bản ghi kết quả của cặp (MSSV, Mã môn).
     * 
     * @param studentId MSSV
     * @param courseId Mã môn
     * @return GradeRecord nếu tìm thấy, hoặc null nếu chưa có
     */
    public GradeRecord findRecord(String studentId, String courseId) {
        if (studentId == null || courseId == null) {
            return null;
        }
        String cleanStudentId = studentId.trim();
        String cleanCourseId = courseId.trim();

        for (GradeRecord r : this.records) {
            if (r.getStudent() != null && r.getCourse() != null) {
                if (r.getStudent().getStudentId().equalsIgnoreCase(cleanStudentId)
                        && r.getCourse().getCourseId().equalsIgnoreCase(cleanCourseId)) {
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * Tạo mới hoặc cập nhật kết quả cho cặp (Sinh viên, Môn học).
     * Đảm bảo:
     * - Mỗi cặp SV–Môn chỉ có DUY NHẤT một bản ghi (không tạo bản ghi trùng).
     * - KIỂM TRA ĐIỀU KIỆN TRƯỚC KHI GÁN (Atomic): Cả 2 điểm phải hợp lệ thì mới gán.
     *   Nếu 1 điểm sai thì không điểm nào bị thay đổi.
     * 
     * @param student Đối tượng sinh viên
     * @param course Đối tượng môn học
     * @param processScore Điểm quá trình (hợp lệ hoặc null)
     * @param examScore Điểm cuối kỳ (hợp lệ hoặc null)
     * @return Bản ghi kết quả sau khi tạo mới hoặc cập nhật
     */
    public synchronized GradeRecord createOrUpdateRecord(Student student, Course course, Double processScore, Double examScore) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh viên không tồn tại trong hệ thống.");
        }
        if (course == null) {
            throw new IllegalArgumentException("Môn học không tồn tại trong hệ thống.");
        }

        // Kiểm tra toàn bộ điều kiện TRƯỚC khi gán (đảm bảo tính nguyên tử)
        validateScore(processScore);
        validateScore(examScore);

        GradeRecord existing = this.findRecord(student.getStudentId(), course.getCourseId());
        if (existing != null) {
            existing.setProcessScore(processScore);
            existing.setExamScore(examScore);
            return existing;
        } else {
            GradeRecord newRecord = new GradeRecord(student, course, processScore, examScore);
            this.records.add(newRecord);
            return newRecord;
        }
    }

    /**
     * Alias hỗ trợ tương thích với các module gọi tên addOrUpdateGrade.
     */
    public synchronized GradeRecord addOrUpdateGrade(Student student, Course course, Double processScore, Double examScore) {
        return this.createOrUpdateRecord(student, course, processScore, examScore);
    }

    /**
     * Thêm trực tiếp bản ghi điểm (thường dùng khi nạp từ file lưu trữ).
     * 
     * @param record Bản ghi điểm cần thêm
     */
    public synchronized void addRecord(GradeRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Bản ghi điểm không được null.");
        }
        GradeRecord existing = this.findRecord(record.getStudent().getStudentId(), record.getCourse().getCourseId());
        if (existing != null) {
            existing.setProcessScore(record.getProcessScore());
            existing.setExamScore(record.getExamScore());
        } else {
            this.records.add(record);
        }
    }

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
        return Collections.unmodifiableList(result);
    }

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
        return Collections.unmodifiableList(result);
    }

    public List<GradeRecord> getAllRecords() {
        return Collections.unmodifiableList(new ArrayList<GradeRecord>(this.records));
    }

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

    public boolean hasGradesForCourse(String courseId) {
        return this.hasGrades(courseId);
    }

    public boolean hasGradesForStudent(String studentId) {
        if (studentId == null) {
            return false;
        }
        String cleanId = studentId.trim();
        for (GradeRecord r : this.records) {
            if (r.getStudent() != null && r.getStudent().getStudentId().equalsIgnoreCase(cleanId)) {
                return true;
            }
        }
        return false;
    }

    public int getRecordCount() {
        return this.records.size();
    }

    public synchronized void clear() {
        this.records.clear();
    }
}

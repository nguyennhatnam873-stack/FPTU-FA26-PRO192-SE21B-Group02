package studentgrademanager.services;

<<<<<<< HEAD
import java.io.Serializable;
=======
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

<<<<<<< HEAD
public class GradeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<GradeRecord> records;
=======
/**
 * Lớp dịch vụ quản lý Điểm số và Kết quả học tập.
 * Phụ trách: Người 4 — Nhập & cập nhật điểm; Người 3 — Cặp SV–Môn
 */
public class GradeManager {
    public static final double MIN_SCORE = 0.0;
    public static final double MAX_SCORE = 10.0;

    private final List<GradeRecord> records;
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27

    public GradeManager() {
        this.records = new ArrayList<GradeRecord>();
    }

<<<<<<< HEAD
    public synchronized GradeRecord addOrUpdateGrade(Student student, Course course, Double processScore, Double examScore) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh viên không được để null.");
        }
        if (course == null) {
            throw new IllegalArgumentException("Môn học không được để null.");
        }
        if (!GradeRecord.isValidScore(processScore)) {
            throw new IllegalArgumentException("Điểm quá trình phải nằm trong đoạn [0, 10] hoặc null.");
        }
        if (!GradeRecord.isValidScore(examScore)) {
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

=======
    // ==================== VALIDATION MIỀN ĐIỂM ====================

    /**
     * Kiểm tra tính hợp lệ của giá trị điểm số.
     * Điểm hợp lệ phải:
     * - Có thể null (đại diện cho điểm thiếu / chưa thi).
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
            throw new IllegalArgumentException(String.format(
                    "Điểm số phải nằm trong khoảng [%.1f, %.1f] (giá trị nhập: %s).",
                    MIN_SCORE, MAX_SCORE, score));
        }
    }

    /**
     * Parse điểm từ chuỗi nhập vào.
     * Chuỗi rỗng hoặc null hoặc "EMPTY" -> trả về null (điểm thiếu).
     * 
     * @param input Chuỗi nhập
     * @return Double hợp lệ hoặc null
     */
    public static Double parseScore(String input) {
        if (input == null || input.trim().isEmpty() || "EMPTY".equalsIgnoreCase(input.trim()) || "NULL".equalsIgnoreCase(input.trim())) {
            return null;
        }
        String clean = input.trim().replace(',', '.');
        double val;
        try {
            val = Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Định dạng điểm không hợp lệ: '" + input + "'");
        }
        validateScore(val);
        return val;
    }

    // ==================== THAO TÁC KẾT QUẢ ====================

    /**
     * Tìm bản ghi kết quả của cặp (MSSV, Mã môn).
     */
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
    public GradeRecord findRecord(String studentId, String courseId) {
        if (studentId == null || courseId == null) {
            return null;
        }
        String cleanStudentId = studentId.trim();
        String cleanCourseId = courseId.trim();
<<<<<<< HEAD
        for (GradeRecord r : this.records) {
            if (r.getStudent().getStudentId().equalsIgnoreCase(cleanStudentId) &&
                r.getCourse().getCourseId().equalsIgnoreCase(cleanCourseId)) {
                return r;
=======

        for (GradeRecord r : this.records) {
            if (r.getStudent() != null && r.getCourse() != null) {
                if (r.getStudent().getStudentId().equalsIgnoreCase(cleanStudentId)
                        && r.getCourse().getCourseId().equalsIgnoreCase(cleanCourseId)) {
                    return r;
                }
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
            }
        }
        return null;
    }

<<<<<<< HEAD
    public List<GradeRecord> getAllRecords() {
        return Collections.unmodifiableList(new ArrayList<GradeRecord>(this.records));
    }

    public List<GradeRecord> getRecordsByStudent(String studentId) {
        if (studentId == null) {
            return Collections.emptyList();
        }
        String cleanId = studentId.trim();
        List<GradeRecord> result = new ArrayList<GradeRecord>();
        for (GradeRecord r : this.records) {
            if (r.getStudent().getStudentId().equalsIgnoreCase(cleanId)) {
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
            if (r.getCourse().getCourseId().equalsIgnoreCase(cleanId)) {
                result.add(r);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public boolean hasGradesForCourse(String courseId) {
=======
    /**
     * Tạo mới hoặc cập nhật kết quả cho cặp (Sinh viên, Môn học).
     * Đảm bảo:
     * - Mỗi cặp SV–Môn chỉ có DUY NHẤT một bản ghi (không tạo dòng thứ hai).
     * - Thao tác kiểm tra tính hợp lệ trước khi gán.
     */
    public synchronized GradeRecord createOrUpdateRecord(Student student, Course course, Double processScore, Double examScore) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh viên không tồn tại trong hệ thống.");
        }
        if (course == null) {
            throw new IllegalArgumentException("Môn học không tồn tại trong hệ thống.");
        }

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
     * Thêm trực tiếp bản ghi điểm (thường dùng khi nạp từ file).
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
        List<GradeRecord> result = new ArrayList<GradeRecord>();
        if (studentId == null) {
            return result;
        }
        String cleanId = studentId.trim();
        for (GradeRecord r : this.records) {
            if (r.getStudent() != null && r.getStudent().getStudentId().equalsIgnoreCase(cleanId)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<GradeRecord> getRecordsByCourse(String courseId) {
        List<GradeRecord> result = new ArrayList<GradeRecord>();
        if (courseId == null) {
            return result;
        }
        String cleanId = courseId.trim();
        for (GradeRecord r : this.records) {
            if (r.getCourse() != null && r.getCourse().getCourseId().equalsIgnoreCase(cleanId)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<GradeRecord> getAllRecords() {
        return Collections.unmodifiableList(this.records);
    }

    public boolean hasGrades(String courseId) {
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
        if (courseId == null) {
            return false;
        }
        String cleanId = courseId.trim();
        for (GradeRecord r : this.records) {
<<<<<<< HEAD
            if (r.getCourse().getCourseId().equalsIgnoreCase(cleanId)) {
=======
            if (r.getCourse() != null && r.getCourse().getCourseId().equalsIgnoreCase(cleanId)) {
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
                return true;
            }
        }
        return false;
    }

<<<<<<< HEAD
    public static Double parseAndValidateScore(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty() || "-".equals(trimmed)) {
            return null;
        }

        double val;
        try {
            val = Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Điểm số phải là số thực hợp lệ: " + trimmed);
        }

        if (Double.isNaN(val) || Double.isInfinite(val)) {
            throw new IllegalArgumentException("Điểm không được là NaN hoặc vô cực.");
        }
        if (val < 0.0 || val > 10.0) {
            throw new IllegalArgumentException("Điểm phải nằm trong khoảng từ 0.0 đến 10.0 (giá trị: " + val + ").");
        }

        return val;
=======
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
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
    }

    public synchronized void clear() {
        this.records.clear();
    }
}

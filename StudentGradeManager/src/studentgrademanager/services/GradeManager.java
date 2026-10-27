package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

public class GradeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<GradeRecord> records;

    public GradeManager() {
        this.records = new ArrayList<GradeRecord>();
    }

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

    public GradeRecord findRecord(String studentId, String courseId) {
        if (studentId == null || courseId == null) {
            return null;
        }
        String cleanStudentId = studentId.trim();
        String cleanCourseId = courseId.trim();
        for (GradeRecord r : this.records) {
            if (r.getStudent().getStudentId().equalsIgnoreCase(cleanStudentId) &&
                r.getCourse().getCourseId().equalsIgnoreCase(cleanCourseId)) {
                return r;
            }
        }
        return null;
    }

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
        if (courseId == null) {
            return false;
        }
        String cleanId = courseId.trim();
        for (GradeRecord r : this.records) {
            if (r.getCourse().getCourseId().equalsIgnoreCase(cleanId)) {
                return true;
            }
        }
        return false;
    }

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
    }

    public synchronized void clear() {
        this.records.clear();
    }
}

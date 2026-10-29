/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

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

    public static void validateScore(Double score) {
        if (score == null) {
            return;
        }

        if (score.isNaN() || score.isInfinite()) {
            throw new IllegalArgumentException("Diem so khong duoc la NaN hoac vo cuc.");
        }

        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException(String.format(Locale.US,
                    "Diem so phai nam trong khoang [%.1f, %.1f] (gia tri nhap: %s).",
                    MIN_SCORE, MAX_SCORE, score));
        }
    }

    public static Double parseScore(String input) {
        if (input == null) {
            return null;
        }

        String clean = input.trim();
        if (clean.isEmpty() || "-".equals(clean) || "EMPTY".equalsIgnoreCase(clean) || "NULL".equalsIgnoreCase(clean)) {
            return null;
        }

        if ("NaN".equalsIgnoreCase(clean) || "Infinity".equalsIgnoreCase(clean)
                || "+Infinity".equalsIgnoreCase(clean) || "-Infinity".equalsIgnoreCase(clean)) {
            throw new IllegalArgumentException("Diem so khong duoc la NaN hoac vo cuc.");
        }

        clean = clean.replace(',', '.');
        double val;
        try {
            val = Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Dinh dang diem khong hop le: '" + input + "'");
        }

        validateScore(val);
        return val;
    }

    public static Double parseAndValidateScore(String input) {
        return parseScore(input);
    }

    public GradeRecord findRecord(String studentId, String courseId) {
        if (studentId == null || courseId == null) {
            return null;
        }

        String cleanStudentId = studentId.trim();
        String cleanCourseId = courseId.trim();

        for (GradeRecord r : this.records) {
            if (r.getStudent() != null && r.getCourse() != null) {
                if (r.getStudent().getStudentId().equalsIgnoreCase(cleanStudentId)
                        && r.getCourse().getCode().equalsIgnoreCase(cleanCourseId)) {
                    return r;
                }
            }
        }
        return null;
    }

    public synchronized GradeRecord createOrUpdateRecord(Student student, Course course, Double processScore, Double examScore) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh vien khong ton tai trong he thong.");
        }
        if (course == null) {
            throw new IllegalArgumentException("Mon hoc khong ton tai trong he thong.");
        }

        validateScore(processScore);
        validateScore(examScore);

        GradeRecord existing = this.findRecord(student.getStudentId(), course.getCode());
        if (existing != null) {
            existing.updateProcessScore(processScore);
            existing.updateExamScore(examScore);
            return existing;
        } else {
            GradeRecord newRecord = new GradeRecord(student, course);
            newRecord.updateProcessScore(processScore);
            newRecord.updateExamScore(examScore);
            this.records.add(newRecord);
            return newRecord;
        }
    }

    public synchronized GradeRecord addOrUpdateGrade(Student student, Course course, Double processScore, Double examScore) {
        return this.createOrUpdateRecord(student, course, processScore, examScore);
    }

    public synchronized void addRecord(GradeRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Ban ghi diem khong duoc null.");
        }
        GradeRecord existing = this.findRecord(record.getStudent().getStudentId(), record.getCourse().getCode());
        if (existing != null) {
            existing.updateProcessScore(record.getProcessScore());
            existing.updateExamScore(record.getExamScore());
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

    public List<GradeRecord> getGradesByStudent(String studentId) {
        return this.getRecordsByStudent(studentId);
    }

    public List<GradeRecord> getRecordsByCourse(String courseId) {
        if (courseId == null) {
            return Collections.emptyList();
        }
        String cleanId = courseId.trim();
        List<GradeRecord> result = new ArrayList<GradeRecord>();
        for (GradeRecord r : this.records) {
            if (r.getCourse() != null && r.getCourse().getCode().equalsIgnoreCase(cleanId)) {
                result.add(r);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public List<GradeRecord> getGradesByCourse(String courseId) {
        return this.getRecordsByCourse(courseId);
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
            if (r.getCourse() != null && r.getCourse().getCode().equalsIgnoreCase(cleanId)) {
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

    public void loadGrades(List<GradeRecord> data) {
        this.records.clear();
        if (data != null) {
            for (GradeRecord record : data) {
                if (record != null) {
                    this.records.add(record);
                }
            }
        }
    }
}

package studentgrademanager.services;

import java.util.ArrayList;
import java.util.List;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;

/**
 * Lớp dịch vụ tổng hợp Bảng điểm sinh viên.
 * Phụ trách: Người 1 — Bảng điểm & Điều phối
 */
public class TranscriptService {
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    public TranscriptService(StudentManager studentManager, GradeManager gradeManager) {
        if (studentManager == null || gradeManager == null) {
            throw new IllegalArgumentException("StudentManager và GradeManager không được null.");
        }
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    /**
     * Tạo bảng điểm cho sinh viên theo MSSV.
     * 
     * @param studentId MSSV
     * @return Đối tượng Transcript nếu tìm thấy sinh viên, hoặc null nếu không tìm thấy.
     */
    public Transcript getTranscript(String studentId) {
        Student student = this.studentManager.findStudent(studentId);
        if (student == null) {
            return null;
        }
        List<GradeRecord> records = this.gradeManager.getRecordsByStudent(studentId);
        return new Transcript(student, records);
    }

    /**
     * Lấy toàn bộ bảng điểm của tất cả sinh viên.
     */
    public List<Transcript> getAllTranscripts() {
        List<Transcript> list = new ArrayList<Transcript>();
        for (Student s : this.studentManager.getAllStudents()) {
            list.add(this.getTranscript(s.getStudentId()));
        }
        return list;
    }
}

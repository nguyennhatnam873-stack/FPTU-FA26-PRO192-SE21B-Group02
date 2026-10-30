/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.services;

<<<<<<< HEAD
import java.util.List;

=======
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;

<<<<<<< HEAD
public class TranscriptService {

    private final GradeManager gradeManager;

    public TranscriptService(GradeManager gradeManager) {
        if (gradeManager == null) {
            throw new IllegalArgumentException(
                    "GradeManager khong duoc null.");
=======
/**
 * Lớp dịch vụ tổng hợp Bảng điểm sinh viên.
 * Phụ trách: Người 1 — Bảng điểm & Điều phối
 */
public class TranscriptService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    public TranscriptService(StudentManager studentManager, GradeManager gradeManager) {
        if (studentManager == null || gradeManager == null) {
            throw new IllegalArgumentException("StudentManager và GradeManager không được null.");
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
        }

        this.gradeManager = gradeManager;
    }

<<<<<<< HEAD
    public Transcript createTranscript(String studentId) {
        Student student = null;
        List<GradeRecord> records =
                gradeManager.getGradesByStudent(studentId);

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma sinh vien khong duoc rong.");
=======
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
        List<GradeRecord> records = this.gradeManager.getRecordsByStudent(student.getStudentId());
        return new Transcript(student, records);
    }

    /**
     * Tương thích ngược với tên gọi generateTranscript.
     */
    public Transcript generateTranscript(String studentId) {
        return this.getTranscript(studentId);
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

    /**
     * In bảng điểm chi tiết của sinh viên ra console.
     */
    public void printTranscript(String studentId) {
        Transcript transcript = this.getTranscript(studentId);
        if (transcript == null) {
            System.out.println("Không tìm thấy sinh viên có mã: " + studentId);
            return;
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
        }

        if (records.isEmpty()) {
            throw new IllegalArgumentException(
                    "Khong tim thay bang diem cho sinh vien nay.");
        }

        student = records.get(0).getStudent();

<<<<<<< HEAD
        return new Transcript(student, records);
    }

    public void displayTranscript(String studentId) {
        Transcript transcript = createTranscript(studentId);
        transcript.display();
    }

    public double calculateAverage(String studentId) {
        Transcript transcript = createTranscript(studentId);
        return transcript.calculateAverage();
=======
    private static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
    }
}

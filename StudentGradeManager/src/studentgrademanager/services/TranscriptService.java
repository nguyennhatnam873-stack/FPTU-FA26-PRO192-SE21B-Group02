package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;

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
        }

        Student s = transcript.getStudent();
        System.out.println("=========================================================================================");
        System.out.printf(" BẢNG ĐIỂM SINH VIÊN: %s - %s\n", s.getStudentId(), s.getFullName());
        System.out.println("=========================================================================================");
        System.out.printf("%-10s | %-25s | %-12s | %-4s | %-7s | %-7s | %-7s | %-12s\n",
                "MÃ MÔN", "TÊN MÔN HỌC", "LOẠI MÔN", "TC", "ĐIỂM QT", "ĐIỂM CK", "ĐIỂM TK", "TRẠNG THÁI");
        System.out.println("-----------------------------------------------------------------------------------------");

        List<GradeRecord> records = transcript.getRecords();
        if (records.isEmpty()) {
            System.out.println("                  Sinh viên chưa đăng ký môn học nào.");
        } else {
            for (GradeRecord r : records) {
                String processStr = r.getProcessScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getProcessScore());
                String examStr = r.getExamScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getExamScore());
                Double finalScore = r.getFinalScore();
                String finalStr = finalScore == null ? "-" : String.format(Locale.US, "%.2f", finalScore);

                System.out.printf("%-10s | %-25s | %-12s | %-4d | %-7s | %-7s | %-7s | %-12s\n",
                        r.getCourse().getCourseId(),
                        truncate(r.getCourse().getCourseName(), 25),
                        r.getCourse().getCourseType(),
                        r.getCourse().getCredits(),
                        processStr,
                        examStr,
                        finalStr,
                        r.getStatus());
            }
        }

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("Tổng số môn: %d | Số môn hoàn thành: %d | Tín chỉ tích lũy: %d/%d\n",
                records.size(),
                transcript.getCompleteRecords().size(),
                transcript.getCompletedCredits(),
                transcript.getTotalEnrolledCredits());
        System.out.printf("Điểm trung bình tích lũy (GPA): %s\n", transcript.getFormattedGpa());
        System.out.println("=========================================================================================\n");
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}

package studentgrademanager.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;

/**
 * Dịch vụ xử lý bảng điểm.
 * Phụ trách: Người 1 (Trưởng nhóm) — Bảng điểm & Điều phối
 */
public class TranscriptService {

    public TranscriptService() {
    }

    /**
     * Tạo đối tượng Transcript cho một sinh viên từ danh sách toàn bộ bản ghi điểm.
     */
    public Transcript buildTranscript(Student student, List<GradeRecord> allRecords) {
        if (student == null) {
            return null;
        }

        List<GradeRecord> studentRecords = new ArrayList<>();
        if (allRecords != null) {
            for (GradeRecord r : allRecords) {
                if (r.getStudent().getStudentId().equalsIgnoreCase(student.getStudentId())) {
                    studentRecords.add(r);
                }
            }
        }

        return new Transcript(student, studentRecords);
    }

    /**
     * In bảng điểm chi tiết ra Console theo đúng quy cách của đề bài.
     */
    public void displayTranscript(Transcript transcript) {
        if (transcript == null) {
            System.out.println(">> Lỗi: Không tìm thấy dữ liệu bảng điểm!");
            return;
        }

        Student s = transcript.getStudent();
        System.out.println("\n=========================================================================================");
        System.out.println("                                  BẢNG ĐIỂM SINH VIÊN                                    ");
        System.out.println("=========================================================================================");
        System.out.printf("Mã số sinh viên: %-15s Họ và tên: %s\n", s.getStudentId(), s.getFullName());
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-24s | %-10s | %-7s | %-9s | %-8s | %-8s | %-12s\n",
                "Mã môn", "Tên môn học", "Loại", "Tín chỉ", "Quá trình", "Cuối kỳ", "Tổng kết", "Trạng thái");
        System.out.println("-----------------------------------------------------------------------------------------");

        if (transcript.getRecords().isEmpty()) {
            System.out.println("                        (Sinh viên chưa có kết quả môn học nào)                  ");
        } else {
            for (GradeRecord r : transcript.getRecords()) {
                String processStr = (r.getProcessScore() != null) ? formatScore(r.getProcessScore()) : "-";
                String examStr = (r.getExamScore() != null) ? formatScore(r.getExamScore()) : "-";
                
                String finalStr;
                String statusStr;

                if (r.isComplete()) {
                    finalStr = formatScore(r.getFinalScore());
                    statusStr = "ĐỦ ĐIỂM";
                } else {
                    finalStr = "-";
                    statusStr = Transcript.CHUA_DU_DIEM;
                }

                System.out.printf("%-8s | %-24s | %-10s | %-7d | %-9s | %-8s | %-8s | %-12s\n",
                        r.getCourse().getCourseId(),
                        r.getCourse().getCourseName(),
                        r.getCourse().getCourseType(),
                        r.getCourse().getCredits(),
                        processStr,
                        examStr,
                        finalStr,
                        statusStr);
            }
        }

        System.out.println("-----------------------------------------------------------------------------------------");
        
        // Hiển thị dòng kết quả tổng kết (Xử lý chặt chẽ theo ca D02, D03, D04)
        String avg = transcript.getFormattedAverage();
        String summary = transcript.getCompletionSummary();

        if (avg.equals(Transcript.CHUA_CO_DIEM_TRUNG_BINH)) {
            System.out.printf(">> Điểm trung bình: %s (%s)\n", avg, summary);
        } else if (transcript.hasIncompleteCourses()) {
            // Có môn thiếu điểm ➔ ghi rõ là "trung bình tạm tính" (Đúng ca D02, D06)
            System.out.printf(">> Điểm trung bình: %s (trung bình tạm tính; %s)\n", avg, summary);
        } else {
            // Đủ 100% môn
            System.out.printf(">> Điểm trung bình: %s (%s)\n", avg, summary);
        }
        System.out.println("=========================================================================================\n");
    }

    /**
     * Hàm phụ trợ làm tròn 2 chữ số thập phân cho điểm thành phần/tổng kết.
     */
    public static String formatScore(Double score) {
        if (score == null) return "";
        BigDecimal bd = new BigDecimal(Double.toString(score));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }
}

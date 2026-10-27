package studentgrademanager.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lớp đại diện cho Bảng điểm của một sinh viên.
 * Phụ trách: Người 1 (Trưởng nhóm) — Bảng điểm & Điều phối
 */
public class Transcript implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String CHUA_CO_DIEM_TRUNG_BINH = "CHUA_CO_DIEM_TRUNG_BINH";
    public static final String CHUA_DU_DIEM = "CHUA_DU_DIEM";

    private Student student;
    private List<GradeRecord> records;

    public Transcript(Student student, List<GradeRecord> records) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh viên không được để null khi tạo bảng điểm.");
        }
        this.student = student;
        this.records = (records != null) ? new ArrayList<>(records) : new ArrayList<GradeRecord>();
    }

    public Student getStudent() {
        return this.student;
    }

    public List<GradeRecord> getRecords() {
        return Collections.unmodifiableList(this.records);
    }

    // Tổng số môn học đã ghi nhận kết quả
    public int getTotalRegisteredCourses() {
        return this.records.size();
    }

    // Số môn đã đủ cả 2 điểm thành phần
    public int getCompletedCoursesCount() {
        int count = 0;
        for (GradeRecord r : this.records) {
            if (r.isComplete()) {
                count++;
            }
        }
        return count;
    }

    // Tổng số tín chỉ của tất cả môn đã ghi nhận
    public int getTotalRegisteredCredits() {
        int total = 0;
        for (GradeRecord r : this.records) {
            total += r.getCourse().getCredits();
        }
        return total;
    }

    // Tổng số tín chỉ của các môn đã đủ điểm
    public int getCompletedCredits() {
        int total = 0;
        for (GradeRecord r : this.records) {
            if (r.isComplete()) {
                total += r.getCourse().getCredits();
            }
        }
        return total;
    }

    /**
     * Tính điểm trung bình thô có trọng số tín chỉ từ các môn đủ điểm.
     * CÔNG THỨC: Tổng (điểm tổng kết chưa làm tròn * tín chỉ) / Tổng tín chỉ môn đủ điểm.
     * 
     * @return Điểm trung bình dạng Double chưa làm tròn, hoặc null nếu chưa có môn đủ điểm.
     */
    public Double calculateRawWeightedAverage() {
        double sumWeightedScore = 0.0;
        int totalValidCredits = 0;

        for (GradeRecord r : this.records) {
            if (r.isComplete()) {
                Double finalScore = r.getFinalScore();
                if (finalScore != null) {
                    int credits = r.getCourse().getCredits();
                    sumWeightedScore += finalScore * credits;
                    totalValidCredits += credits;
                }
            }
        }

        if (totalValidCredits == 0) {
            return null; // Tránh chia cho 0 (Ca D03)
        }

        return sumWeightedScore / totalValidCredits;
    }

    /**
     * Định dạng điểm trung bình hiển thị: làm tròn nửa lên (HALF_UP) 2 chữ số thập phân.
     * Nếu chưa có môn đủ điểm, trả về hằng số "CHUA_CO_DIEM_TRUNG_BINH".
     */
    public String getFormattedAverage() {
        Double rawAvg = this.calculateRawWeightedAverage();
        if (rawAvg == null) {
            return CHUA_CO_DIEM_TRUNG_BINH;
        }

        BigDecimal bd = new BigDecimal(Double.toString(rawAvg));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }

    // Tóm tắt tiến độ: ví dụ "đủ 2/3 môn, 5/7 tín chỉ" (Phục vụ ca D02, D04, D06)
    public String getCompletionSummary() {
        return String.format("đủ %d/%d môn, %d/%d tín chỉ",
                this.getCompletedCoursesCount(),
                this.getTotalRegisteredCourses(),
                this.getCompletedCredits(),
                this.getTotalRegisteredCredits());
    }

    // Kiểm tra xem có môn nào chưa đủ điểm hay không
    public boolean hasIncompleteCourses() {
        return this.getCompletedCoursesCount() < this.getTotalRegisteredCourses();
    }
}

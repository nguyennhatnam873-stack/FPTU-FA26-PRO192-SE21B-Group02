package studentgrademanager.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lớp đại diện cho Bảng điểm của một Sinh viên.
 * Phụ trách: Người 1 — Bảng điểm & Điều phối
 */
public class Transcript {
    public static final String CHUA_CO_DIEM_TB = "CHUA_CO_DIEM_TRUNG_BINH";
    public static final String CHUA_DU_DIEM = "CHUA_DU_DIEM";

    private final Student student;
    private final List<GradeRecord> records;

    public Transcript(Student student, List<GradeRecord> records) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh viên không được null.");
        }
        this.student = student;
        this.records = (records != null) ? new ArrayList<GradeRecord>(records) : new ArrayList<GradeRecord>();
    }

    public Student getStudent() {
        return this.student;
    }

    public List<GradeRecord> getRecords() {
        return Collections.unmodifiableList(this.records);
    }

    public int getTotalCourses() {
        return this.records.size();
    }

    public int getCompletedCourses() {
        int count = 0;
        for (GradeRecord r : this.records) {
            if (r.isComplete()) {
                count++;
            }
        }
        return count;
    }

    public int getTotalCredits() {
        int credits = 0;
        for (GradeRecord r : this.records) {
            if (r.getCourse() != null) {
                credits += r.getCourse().getCredits();
            }
        }
        return credits;
    }

    public int getCompletedCredits() {
        int credits = 0;
        for (GradeRecord r : this.records) {
            if (r.isComplete() && r.getCourse() != null) {
                credits += r.getCourse().getCredits();
            }
        }
        return credits;
    }

    /**
     * Tính điểm trung bình tích lũy có trọng số tín chỉ.
     * TB = Σ(tổng kết × tín chỉ) / Σ tín chỉ (chỉ tính từ các môn đủ điểm).
     * 
     * @return Điểm trung bình (chưa làm tròn), hoặc null nếu chưa có môn nào đủ điểm.
     */
    public Double calculateWeightedGPA() {
        double totalWeightedScore = 0.0;
        int completedCredits = 0;

        for (GradeRecord r : this.records) {
            if (r.isComplete() && r.getCourse() != null) {
                Double finalScore = r.getFinalScore();
                if (finalScore != null) {
                    int c = r.getCourse().getCredits();
                    totalWeightedScore += finalScore * c;
                    completedCredits += c;
                }
            }
        }

        if (completedCredits == 0) {
            return null; // Chưa có môn đủ điểm -> CHUA_CO_DIEM_TRUNG_BINH
        }

        return totalWeightedScore / completedCredits;
    }

    public String getFormattedGPA() {
        Double gpa = this.calculateWeightedGPA();
        if (gpa == null) {
            return CHUA_CO_DIEM_TB;
        }
        return String.format(java.util.Locale.US, "%.2f", gpa);
    }
}

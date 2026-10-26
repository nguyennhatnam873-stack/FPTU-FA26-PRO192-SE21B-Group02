package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lop Transcript (Bang diem ca nhan cua Sinh vien).
 * Phu trach chinh: Nguoi 1 (Truong nhom / Bang diem) - Ca test: D02, D04, D06, D07.
 * 
 * Quy tac nghiep vu:
 * - Tinh diem trung binh co trong so tin chi tu cac mon DU DIEM:
 *   Diem TB = Tong(diem tong ket chua lam tron * tin chi) / Tong(tin chi cac mon du diem).
 * - Neu chua co mon nao du diem: hien thi CHUA_CO_DIEM_TRUNG_BINH (khong chia cho 0, khong hien 0.0).
 * - Tinh tong so mon va tin chi du tren tong so da ghi nhan.
 */
public class Transcript {
    public static final String CHUA_CO_DIEM_TRUNG_BINH = "CHUA_CO_DIEM_TRUNG_BINH";

    private Student student;
    private List<GradeRecord> records;

    public Transcript() {
        this.records = new ArrayList<>();
    }

    public Transcript(Student student, List<GradeRecord> records) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh vien khong duoc null trong Bang diem.");
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

    public void addRecord(GradeRecord record) {
        if (record != null) {
            this.records.add(record);
        }
    }

    /**
     * Tinh diem trung binh co trong so tu cac mon du diem.
     * @return Double diem trung binh chua lam tron, hoac null neu chua co mon nao du diem.
     */
    public Double calculateAverage() {
        double weightedSum = 0.0;
        int completedCredits = 0;

        for (GradeRecord r : this.records) {
            Double finalScore = r.getFinalScore();
            if (finalScore != null) {
                int credits = r.getCourse().getCredits();
                weightedSum += finalScore * credits;
                completedCredits += credits;
            }
        }

        if (completedCredits == 0) {
            return null; // Khong chia cho 0, the hien CHUA_CO_DIEM_TRUNG_BINH
        }

        return weightedSum / completedCredits;
    }

    /**
     * Dem so luong mon da co du ca 2 diem thanh phan.
     */
    public int getCompletedCourseCount() {
        int count = 0;
        for (GradeRecord r : this.records) {
            if (r.getFinalScore() != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Tong so mon hoc da dang ky trong bang diem.
     */
    public int getTotalCourseCount() {
        return this.records.size();
    }

    /**
     * Tong so tin chi cua cac mon da du diem.
     */
    public int getCompletedCredits() {
        int credits = 0;
        for (GradeRecord r : this.records) {
            if (r.getFinalScore() != null) {
                credits += r.getCourse().getCredits();
            }
        }
        return credits;
    }

    /**
     * Tong so tin chi cua tat ca cac mon da ghi nhan.
     */
    public int getTotalCredits() {
        int credits = 0;
        for (GradeRecord r : this.records) {
            credits += r.getCourse().getCredits();
        }
        return credits;
    }

    /**
     * Chuoi hien thi diem trung binh lam tron 2 chu so thap phan hoac thong bao chua co.
     */
    public String getFormattedAverage() {
        Double avg = this.calculateAverage();
        if (avg == null) {
            return CHUA_CO_DIEM_TRUNG_BINH;
        }
        return String.format("%.2f", avg);
    }
}

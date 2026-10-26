package model;

/**
 * Lop GradeRecord: Ket qua hoc tap cho mot cap Sinh vien - Mon hoc.
 * Phuc vu yeu cau cua Thanh vien 3 (Nguoi 3) & Thanh vien 4 - PRO192 FA26 P05.
 * 
 * Ranh gioi nghiep vu quan trong:
 * - Diem thieu duoc bieu dien bang Double null.
 * - Diem 0.0 la hop le, tuyet doi khong dung 0 de dai dien cho diem thieu.
 * - Goi da hinh tinh diem thong qua doi tuong Course.
 */
public class GradeRecord {
    public static final String CHUA_DU_DIEM = "CHUA_DU_DIEM";

    private Student student;
    private Course course;
    private Double processScore;
    private Double examScore;

    public GradeRecord() {
    }

    public GradeRecord(Student student, Course course, Double processScore, Double examScore) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh vien khong duoc null.");
        }
        if (course == null) {
            throw new IllegalArgumentException("Mon hoc khong duoc null.");
        }
        this.student = student;
        this.course = course;
        this.setProcessScore(processScore);
        this.setExamScore(examScore);
    }

    public Student getStudent() {
        return this.student;
    }

    public Course getCourse() {
        return this.course;
    }

    public Double getProcessScore() {
        return this.processScore;
    }

    public void setProcessScore(Double score) {
        if (score != null) {
            this.validateScore(score);
        }
        this.processScore = score;
    }

    public Double getExamScore() {
        return this.examScore;
    }

    public void setExamScore(Double score) {
        if (score != null) {
            this.validateScore(score);
        }
        this.examScore = score;
    }

    /**
     * Kiem tra mien diem hop le [0; 10], khong am, khong lon hon 10, khong NaN, khong vo cuc.
     */
    private void validateScore(Double score) {
        if (Double.isNaN(score) || Double.isInfinite(score) || score < 0.0 || score > 10.0) {
            throw new IllegalArgumentException("Diem phai la so thuc huu han trong doan [0; 10].");
        }
    }

    /**
     * Tinh diem tong ket thong qua loi goi da hinh toi Course.
     * Tra ve null neu chua du ca 2 diem thanh phan.
     */
    public Double getFinalScore() {
        if (this.processScore == null || this.examScore == null) {
            return null;
        }
        return this.course.calculateFinalScore(this.processScore, this.examScore);
    }

    /**
     * Lay trang thai ket qua (DU_DIEM hoac CHUA_DU_DIEM).
     */
    public String getStatus() {
        if (this.processScore == null || this.examScore == null) {
            return CHUA_DU_DIEM;
        }
        return "DU_DIEM";
    }

    @Override
    public String toString() {
        String processStr = (this.processScore == null) ? "Thieu" : String.format("%.2f", this.processScore);
        String examStr = (this.examScore == null) ? "Thieu" : String.format("%.2f", this.examScore);
        Double finalScore = this.getFinalScore();
        String finalStr = (finalScore == null) ? CHUA_DU_DIEM : String.format("%.2f", finalScore);

        return String.format("GradeRecord [SV: %s, Mon: %s, QT: %s, CK: %s, Tong ket: %s, Trang thai: %s]",
                this.student.getStudentId(), this.course.getCourseId(), processStr, examStr, finalStr, this.getStatus());
    }
}

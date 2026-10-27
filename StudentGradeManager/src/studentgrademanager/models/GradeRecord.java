package studentgrademanager.models;

/**
 * Phụ trách: Người 3 (liên kết SV-Môn) & Người 4 (nghiệp vụ điểm)
 */
public class GradeRecord {
    // TODO: Thành viên tự viết code tại đây
    private Student student;
    private Course course;
    private Double processScore; // Dùng Double đối tượng để có thể mang giá trị null (thiếu điểm)
    private Double examScore;    // null nếu chưa thi

    public GradeRecord(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.processScore = null; // Khởi tạo ban đầu là chưa có điểm
        this.examScore = null;
    }

    public GradeRecord(Student student, Course course, Double processScore, Double examScore) {
        this.student = student;
        this.course = course;
        this.processScore = processScore;
        this.examScore = examScore;
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

    public void setProcessScore(Double processScore) {
        this.processScore = processScore;
    }

    public Double getExamScore() {
        return this.examScore;
    }

    public void setExamScore(Double examScore) {
        this.examScore = examScore;
    }

    // Kiểm tra đã đủ 2 đầu điểm chưa
    public boolean isComplete() {
        return this.processScore != null && this.examScore != null;
    }

    // Tính điểm tổng kết qua lời gọi ĐA HÌNH tới Course (Course tự tính theo loại của nó)
    public Double getFinalScore() {
        if (!this.isComplete()) {
            return null; // Thiếu 1 trong 2 điểm thì chưa có điểm tổng kết (CHUA_DU_DIEM)
        }
        return this.course.calculateFinalScore(this.processScore, this.examScore);
    }
}



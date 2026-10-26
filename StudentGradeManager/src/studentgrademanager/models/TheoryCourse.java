package studentgrademanager.models;

/**
 * Lớp TheoryCourse theo đúng Class Diagram đề tài P05.
 * Phụ trách: Người 2 — Môn học & đánh giá
 * 
 * Công thức: 0.30 * process + 0.70 * exam
 */
public class TheoryCourse extends Course {
    private static final long serialVersionUID = 1L;

    public static final double PROCESS_WEIGHT = 0.30;
    public static final double FINAL_EXAM_WEIGHT = 0.70;

    public TheoryCourse(String courseId, String courseName, int credits) {
        super(courseId, courseName, credits);
    }

    /**
     * Cài đặt phương thức tính điểm tổng kết theo đúng Class Diagram:
     * + calculateFinalScore(process: double, exam: double): double
     */
    @Override
    public double calculateFinalScore(double process, double exam) {
        return (process * PROCESS_WEIGHT) + (exam * FINAL_EXAM_WEIGHT);
    }

    /**
     * Cài đặt phương thức lấy loại môn học theo đúng Class Diagram:
     * + getCourseType(): String
     */
    @Override
    public String getCourseType() {
        return "Lý thuyết";
    }
}

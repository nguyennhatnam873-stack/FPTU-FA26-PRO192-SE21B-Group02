package studentgrademanager.models;

/**
 * Lớp đại diện cho Môn học Lý thuyết.
 * Phụ trách: Người 2 — Môn học & đánh giá
 * 
 * Công thức tính điểm tổng kết:
 *   Điểm tổng kết = 0.30 * Điểm quá trình + 0.70 * Điểm cuối kỳ
 *   Nếu thiếu một trong hai điểm thành phần (null), trả về null.
 */
public class TheoryCourse extends Course {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_CODE = "THEORY";
    public static final String TYPE_NAME = "Lý thuyết";

    public static final double PROCESS_WEIGHT = 0.30;
    public static final double FINAL_EXAM_WEIGHT = 0.70;

    /**
     * Khởi tạo môn học Lý thuyết.
     * 
     * @param courseId Mã môn học (ví dụ: LT101)
     * @param courseName Tên môn học
     * @param credits Số tín chỉ (1–6)
     */
    public TheoryCourse(String courseId, String courseName, int credits) {
        super(courseId, courseName, credits);
    }

    /**
     * Cài đặt đa hình công thức tính điểm môn Lý thuyết:
     * 0.30 * quá trình + 0.70 * cuối kỳ.
     * 
     * @param processGrade Điểm quá trình
     * @param finalExamGrade Điểm cuối kỳ
     * @return Điểm tổng kết chưa làm tròn, hoặc null nếu thiếu một trong hai thành phần.
     */
    @Override
    public Double calculateFinalGrade(Double processGrade, Double finalExamGrade) {
        if (processGrade == null || finalExamGrade == null) {
            return null;
        }
        return (processGrade * PROCESS_WEIGHT) + (finalExamGrade * FINAL_EXAM_WEIGHT);
    }

    @Override
    public String getCourseType() {
        return TYPE_NAME;
    }

    @Override
    public String getTypeCode() {
        return TYPE_CODE;
    }

    @Override
    public double getProcessWeight() {
        return PROCESS_WEIGHT;
    }

    @Override
    public double getFinalExamWeight() {
        return FINAL_EXAM_WEIGHT;
    }
}

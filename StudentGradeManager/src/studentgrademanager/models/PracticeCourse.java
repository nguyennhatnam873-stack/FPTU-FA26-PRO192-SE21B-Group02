package studentgrademanager.models;

/**
 * Lớp đại diện cho Môn học Thực hành.
 * Phụ trách: Người 2 — Môn học & đánh giá
 * 
 * Công thức tính điểm tổng kết:
 *   Điểm tổng kết = 0.50 * Điểm quá trình + 0.50 * Điểm cuối kỳ
 *   Nếu thiếu một trong hai điểm thành phần (null), trả về null.
 */
public class PracticeCourse extends Course {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_CODE = "PRACTICE";
    public static final String TYPE_NAME = "Thực hành";

    public static final double PROCESS_WEIGHT = 0.50;
    public static final double FINAL_EXAM_WEIGHT = 0.50;

    /**
     * Khởi tạo môn học Thực hành.
     * 
     * @param courseId Mã môn học (ví dụ: TH101)
     * @param courseName Tên môn học
     * @param credits Số tín chỉ (1–6)
     */
    public PracticeCourse(String courseId, String courseName, int credits) {
        super(courseId, courseName, credits);
    }

    /**
     * Cài đặt đa hình công thức tính điểm môn Thực hành:
     * 0.50 * quá trình + 0.50 * cuối kỳ.
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

package studentgrademanager.models;
<<<<<<< HEAD

/**
 * Lớp PracticeCourse theo đúng Class Diagram đề tài P05.
 * Phụ trách: Người 2 — Môn học & đánh giá
 * 
 * Công thức: 0.50 * process + 0.50 * exam
 */
=======
>>>>>>> 5a4875ab76ee3de56791cb70cad9640404e2f80f
public class PracticeCourse extends Course {
    private static final long serialVersionUID = 1L;

    public static final double PROCESS_WEIGHT = 0.50;
    public static final double FINAL_EXAM_WEIGHT = 0.50;

<<<<<<< HEAD
=======

>>>>>>> 5a4875ab76ee3de56791cb70cad9640404e2f80f
    public PracticeCourse(String courseId, String courseName, int credits) {
        super(courseId, courseName, credits);
    }

<<<<<<< HEAD
    /**
     * Cài đặt phương thức tính điểm tổng kết theo đúng Class Diagram:
     * + calculateFinalScore(process: double, exam: double): double
     */
=======
    
>>>>>>> 5a4875ab76ee3de56791cb70cad9640404e2f80f
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
        return "Thực hành";
    }
}

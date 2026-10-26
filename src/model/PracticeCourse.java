package model;

/**
 * Mon hoc Thuc hanh: Tong ket = 0.50 * Qua trinh + 0.50 * Cuoi ky.
 */
public class PracticeCourse extends Course {

    public PracticeCourse(String courseId, String courseName, int credits) {
        super(courseId, courseName, credits);
    }

    @Override
    public Double calculateFinalScore(Double process, Double exam) {
        if (process == null || exam == null) {
            return null;
        }
        return 0.50 * process + 0.50 * exam;
    }

    @Override
    public String getCourseType() {
        return "Thuc hanh";
    }
}

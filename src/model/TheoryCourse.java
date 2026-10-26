package model;

/**
 * Mon hoc Ly thuyet: Tong ket = 0.30 * Qua trinh + 0.70 * Cuoi ky.
 */
public class TheoryCourse extends Course {

    public TheoryCourse(String courseId, String courseName, int credits) {
        super(courseId, courseName, credits);
    }

    @Override
    public Double calculateFinalScore(Double process, Double exam) {
        if (process == null || exam == null) {
            return null;
        }
        return 0.30 * process + 0.70 * exam;
    }

    @Override
    public String getCourseType() {
        return "Ly thuyet";
    }
}

package studentgrademanager.models;

public class TheoryCourse extends Course {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_CODE = "THEORY";
    public static final String TYPE_NAME = "Ly thuyet";

    public static final double PROCESS_WEIGHT = 0.30;
    public static final double FINAL_EXAM_WEIGHT = 0.70;

    public TheoryCourse(String courseId, String courseName, int credits) {
        super(courseId, courseName, credits);
    }

    @Override
    public double calculateFinalScore(double process, double exam) {
        return (process * PROCESS_WEIGHT) + (exam * FINAL_EXAM_WEIGHT);
    }

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

<<<<<<< HEAD
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.models;

public class PracticeCourse extends Course {

    private static final long serialVersionUID = 1L;

    public PracticeCourse(String code, String name, int credits) {
        super(code, name, credits);
    }

    @Override
    public double calculateFinalGrade(
            double processScore, double examScore) {

        if (processScore < 0 || processScore > 10
                || examScore < 0 || examScore > 10) {
            throw new IllegalArgumentException(
                    "Diem phai tu 0 den 10.");
        }

        return 0.50 * processScore + 0.50 * examScore;
=======
package studentgrademanager.models;

public class PracticeCourse extends Course {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_CODE = "PRACTICE";
    public static final String TYPE_NAME = "Thuc hanh";

    public static final double PROCESS_WEIGHT = 0.50;
    public static final double FINAL_EXAM_WEIGHT = 0.50;

    public PracticeCourse(String courseId, String courseName, int credits) {
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
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
    }

    @Override
    public String getCourseType() {
<<<<<<< HEAD
        return "THUC_HANH";
    }
}
=======
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
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.models;

public class TheoryCourse extends Course {

    private static final long serialVersionUID = 1L;

    public TheoryCourse(String code, String name, int credits) {
        super(code, name, credits);
    }

    public double calculateFinalGrade(
            double processScore, double examScore) {

        if (processScore < 0 || processScore > 10|| examScore < 0 || examScore > 10) {
            throw new IllegalArgumentException("Diem phai tu 0 den 10.");
        }

        return 0.30 * processScore + 0.70 * examScore;
    }

    @Override
    public String getCourseType() {
        return "Lý thuyết";
    }
    @Override
    public double calculateFinalScore(double process, double exam) {
        return calculateFinalGrade(process, exam);
    }
    @Override
    public Double calculateFinalGrade(Double processGrade, Double finalExamGrade) {
        if (processGrade == null || finalExamGrade == null) return null;
        return calculateFinalGrade(processGrade.doubleValue(), finalExamGrade.doubleValue());
    }
    @Override public String getTypeCode() { return "THEORY"; }
    @Override public double getProcessWeight() { return 0.30; }
    @Override public double getFinalExamWeight() { return 0.70; }
}

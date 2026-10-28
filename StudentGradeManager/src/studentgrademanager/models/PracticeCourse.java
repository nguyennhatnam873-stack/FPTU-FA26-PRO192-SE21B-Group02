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
    }

    @Override
    public String getCourseType() {
        return "THUC_HANH";
    }
}
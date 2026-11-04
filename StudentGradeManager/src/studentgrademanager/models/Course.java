
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.models;

import java.io.Serializable;
import java.util.Locale;

public abstract class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String code;
    private final String name;
    private final int credits;

    public Course(String code, String name, int credits) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ma mon khong duoc rong."
            );
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ten mon khong duoc rong."
            );
        }

        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException(
                "Tin chi phai tu 1 den 6."
            );
        }

        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.name = name.trim();
        this.credits = credits;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
    public String getCourseId() {
        return this.code;
    }
    public String getCourseName() {
        return this.name;
    }
    public int getCredits() {
        return this.credits;
    }
    public static final int MIN_CREDITS = 1;
    public static final int MAX_CREDITS = 6;
    public abstract double calculateFinalScore(double process, double exam);
    public abstract String getCourseType();
    public abstract String getTypeCode();
    public abstract double getProcessWeight();
    public abstract double getFinalExamWeight();
    public Double calculateFinalScore(Double process, Double exam) {
        if (process == null || exam == null) return null;
        return calculateFinalScore(process.doubleValue(), exam.doubleValue());
    }
    public Double calculateFinalGrade(Double processGrade, Double finalExamGrade) {
        return calculateFinalScore(processGrade, finalExamGrade);
    }
    public void setCredits(int credits) {
        if (credits < MIN_CREDITS || credits > MAX_CREDITS) {
            throw new IllegalArgumentException("So tin chi phai tu " + MIN_CREDITS + " den " + MAX_CREDITS + ".");
        }
    }
}           
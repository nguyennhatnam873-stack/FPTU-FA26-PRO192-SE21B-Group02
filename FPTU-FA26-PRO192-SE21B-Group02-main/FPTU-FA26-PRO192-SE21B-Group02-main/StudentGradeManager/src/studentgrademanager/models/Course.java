package studentgrademanager.models;

import java.io.Serializable;
import java.util.Locale;

public abstract class Course implements Serializable {
    public static final int MIN_CREDITS = 1;
    public static final int MAX_CREDITS = 6;

    private final String code;
    private final String name;
    private int credits;

    public Course(String code, String name, int credits) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma mon khong duoc rong.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten mon khong duoc rong.");
        }

        if (credits < MIN_CREDITS || credits > MAX_CREDITS) {
            throw new IllegalArgumentException("Tin chi phai tu " + MIN_CREDITS + " den " + MAX_CREDITS + ".");
        }

        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.name = name.trim();
        this.credits = credits;
    }

    public String getCode() {
        return this.code;
    }

    public String getCourseId() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getCourseName() {
        return this.name;
    }

    public int getCredits() {
        return this.credits;
    }

    public void setCredits(int credits) {
        if (credits < MIN_CREDITS || credits > MAX_CREDITS) {
            throw new IllegalArgumentException("Tin chi phai tu " + MIN_CREDITS + " den " + MAX_CREDITS + ".");
        }
        this.credits = credits;
    }

    public String getTypeCode() {
        String type = getCourseType();
        if (type == null) return "THEORY";
        String upper = type.toUpperCase(Locale.ROOT);
        if ((upper.contains("THUC") || upper.contains("THá»°C") || type.contains("Thực")) || upper.contains("PRACTICE")) {
            return "PRACTICE";
        }
        return "THEORY";
    }

    public abstract double calculateFinalGrade(double processScore, double examScore);

    public Double calculateFinalGrade(Double processScore, Double examScore) {
        if (processScore == null || examScore == null) {
            return null;
        }
        return calculateFinalGrade(processScore.doubleValue(), examScore.doubleValue());
    }

    public double calculateFinalScore(double processScore, double examScore) {
        return calculateFinalGrade(processScore, examScore);
    }

    public Double calculateFinalScore(Double processScore, Double examScore) {
        return calculateFinalGrade(processScore, examScore);
    }

    public abstract String getCourseType();
}
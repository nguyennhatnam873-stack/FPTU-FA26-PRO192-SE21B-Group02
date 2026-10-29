package studentgrademanager.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Transcript implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String NO_GPA_MESSAGE = "CHUA_CO_DIEM_TRUNG_BINH";
    public static final String CHUA_CO_DIEM_TB = NO_GPA_MESSAGE;
    public static final String CHUA_DU_DIEM = "CHUA_DU_DIEM";

    private Student student;
    private List<GradeRecord> records;

    public Transcript(Student student, List<GradeRecord> records) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh viên không được để null.");
        }
        this.student = student;
        this.records = records == null ? new ArrayList<GradeRecord>() : new ArrayList<GradeRecord>(records);
    }

    public Student getStudent() {
        return this.student;
    }

    public List<GradeRecord> getRecords() {
        return Collections.unmodifiableList(this.records);
    }

    public List<GradeRecord> getCompleteRecords() {
        List<GradeRecord> list = new ArrayList<GradeRecord>();
        for (GradeRecord r : this.records) {
            if (r.isComplete()) {
                list.add(r);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public int getTotalEnrolledCredits() {
        int total = 0;
        for (GradeRecord r : this.records) {
            if (r.getCourse() != null) {
                total += r.getCourse().getCredits();
            }
        }
        return total;
    }

    public int getCompletedCredits() {
        int total = 0;
        for (GradeRecord r : this.records) {
            if (r.isComplete() && r.getCourse() != null) {
                total += r.getCourse().getCredits();
            }
        }
        return total;
    }

    public boolean hasAverageScore() {
        return this.getCompletedCredits() > 0;
    }

    public Double calculateRawWeightedGpa() {
        if (!this.hasAverageScore()) {
            return null;
        }

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (GradeRecord r : this.records) {
            if (r.isComplete() && r.getFinalScore() != null) {
                int credits = r.getCourse().getCredits();
                totalPoints += r.getFinalScore().doubleValue() * credits;
                totalCredits += credits;
            }
        }

        if (totalCredits == 0) {
            return null;
        }

        return totalPoints / totalCredits;
    }

    public String getFormattedGpa() {
        Double gpa = this.calculateRawWeightedGpa();
        if (gpa == null) {
            return NO_GPA_MESSAGE;
        }
        return String.format(Locale.US, "%.2f", gpa);
    }

    public int getTotalCourses() {
        return this.records.size();
    }

    public int getCompletedCourses() {
        return this.getCompleteRecords().size();
    }

    public int getTotalCredits() {
        return this.getTotalEnrolledCredits();
    }

    public Double calculateWeightedGPA() {
        return this.calculateRawWeightedGpa();
    }

    public String getFormattedGPA() {
        return this.getFormattedGpa();
    }
}

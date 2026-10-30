<<<<<<< HEAD
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transcript {

    private final Student student;
    private final List<GradeRecord> records;

    public Transcript(Student student,
                      List<GradeRecord> records) {
        if (student == null) {
            throw new IllegalArgumentException(
                    "Sinh vien khong duoc null.");
        }

        this.student = student;
        this.records = new ArrayList<GradeRecord>();

        if (records != null) {
            for (GradeRecord record : records) {
                if (record != null
                        && record.getStudent()
                            .getStudentId()
                            .equalsIgnoreCase(
                                student.getStudentId())) {
                    this.records.add(record);
                }
            }
        }
=======
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
    public static final String CHUA_CO_DIEM_TRUNG_BINH = NO_GPA_MESSAGE;
    public static final String CHUA_DU_DIEM = "CHUA_DU_DIEM";

    private Student student;
    private List<GradeRecord> records;

    public Transcript(Student student, List<GradeRecord> records) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh viên không được để null.");
        }
        this.student = student;
        this.records = records == null ? new ArrayList<GradeRecord>() : new ArrayList<GradeRecord>(records);
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
    }

    public Student getStudent() {
        return this.student;
    }

    public List<GradeRecord> getRecords() {
<<<<<<< HEAD
        return Collections.unmodifiableList(
                this.records);
    }

    public double calculateAverage() {
        double totalPoints = 0.0;
        int totalCredits = 0;

        for (GradeRecord record : records) {
            Double grade = record.getFinalGrade();

            if (grade != null) {
                int credits = record.getCourse().getCredits();

                totalPoints += grade * credits;
=======
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
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
                totalCredits += credits;
            }
        }

        if (totalCredits == 0) {
<<<<<<< HEAD
            return 0.0;
=======
            return null;
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
        }

        return totalPoints / totalCredits;
    }

<<<<<<< HEAD
    public int getCompletedCourseCount() {
        int count = 0;

        for (GradeRecord record : records) {
            if (record.getFinalGrade() != null) {
                count++;
            }
        }

        return count;
    }

    public int getTotalCourseCount() {
        return records.size();
    }

    public void display() {
        System.out.println("===== BANG DIEM =====");
        System.out.println("MSSV: "
                + student.getStudentId());
        System.out.println("Ho ten: "
                + student.getFullName());
        System.out.println("------------------------------------------");
        System.out.println(
                "Ma mon | Ten mon | TC | Qua trinh | Cuoi ky | Tong");

        for (GradeRecord record : records) {
            String process = record.getProcessScore() == null
                    ? "--"
                    : String.format("%.2f",
                            record.getProcessScore());

            String exam = record.getExamScore() == null
                    ? "--"
                    : String.format("%.2f",
                            record.getExamScore());

            String grade = record.getFinalGrade() == null
                    ? "Chua du diem"
                    : String.format("%.2f",
                            record.getFinalGrade());

            System.out.println(
                    record.getCourse().getCode() + " | "
                    + record.getCourse().getName() + " | "
                    + record.getCourse().getCredits() + " | "
                    + process + " | " + exam + " | " + grade);
        }

        System.out.printf(
                "Diem trung binh theo tin chi: %.2f%n",
                calculateAverage());

        System.out.println(
                "So mon da du diem: "
                + getCompletedCourseCount()
                + "/" + getTotalCourseCount());
=======
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
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
    }
}

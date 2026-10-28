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
    }

    public Student getStudent() {
        return this.student;
    }

    public List<GradeRecord> getRecords() {
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
                totalCredits += credits;
            }
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        return totalPoints / totalCredits;
    }

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
    }
}

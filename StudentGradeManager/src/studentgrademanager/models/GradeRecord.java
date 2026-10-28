/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.models;

import java.io.Serializable;

public class GradeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Student student;
    private final Course course;
    private Double processScore;
    private Double examScore;

    public GradeRecord(Student student, Course course) {
        if (student == null) {
            throw new IllegalArgumentException(
                    "Sinh vien khong duoc null.");
        }

        if (course == null) {
            throw new IllegalArgumentException(
                    "Mon hoc khong duoc null.");
        }

        this.student = student;
        this.course = course;
        this.processScore = null;
        this.examScore = null;
    }

    public Student getStudent() {
        return this.student;
    }

    public Course getCourse() {
        return this.course;
    }

    public Double getProcessScore() {
        return this.processScore;
    }

    public Double getExamScore() {
        return this.examScore;
    }

    public void updateProcessScore(Double score) {
        validateScore(score);
        this.processScore = score;
    }

    public void updateExamScore(Double score) {
        validateScore(score);
        this.examScore = score;
    }

    private void validateScore(Double score) {
        if (score != null
                && (score < 0.0 || score > 10.0)) {
            throw new IllegalArgumentException(
                    "Diem phai tu 0 den 10.");
        }
    }

    public boolean hasCompleteScores() {
        return this.processScore != null
                && this.examScore != null;
    }

    public Double getFinalGrade() {
        if (!hasCompleteScores()) {
            return null;
        }

        return this.course.calculateFinalGrade(
                this.processScore,
                this.examScore);
    }

    @Override
    public String toString() {
        String process = processScore == null
                ? "Chua co" : processScore.toString();

        String exam = examScore == null
                ? "Chua co" : examScore.toString();

        String result = getFinalGrade() == null
                ? "Chua du diem"
                : String.format("%.2f", getFinalGrade());

        return student.getStudentId() + " | "
                + course.getCode() + " | "
                + process + " | "
                + exam + " | " + result;
    }
}

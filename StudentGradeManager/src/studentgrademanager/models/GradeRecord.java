
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
=======
package studentgrademanager.models;

import java.io.Serializable;
import java.util.Objects;

public class GradeRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_COMPLETE = "DU_DIEM";
    public static final String STATUS_INCOMPLETE = "CHUA_DU_DIEM";

    private Student student;
    private Course course;

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

        this(student, course, null, null);
    }

    public GradeRecord(Student student, Course course, Double processScore, Double examScore) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh vien khong duoc de null.");
        }
        if (course == null) {
            throw new IllegalArgumentException("Mon hoc khong duoc de null.");
        }
        if (!isValidScore(processScore)) {
            throw new IllegalArgumentException("Diem qua trinh phai nam trong doan [0, 10] hoac null.");
        }
        if (!isValidScore(examScore)) {
            throw new IllegalArgumentException("Diem cuoi ki phai nam trong doan [0, 10] hoac null.");
        }
        this.student = student;
        this.course = course;
        this.processScore = processScore;
        this.examScore = examScore;
    }

    public static boolean isValidScore(Double score) {
        if (score == null) {
            return true;
        }
        return score >= 0.0 && score <= 10.0 && !Double.isNaN(score) && !Double.isInfinite(score);
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

    public void setProcessScore(Double processScore) {
        if (!isValidScore(processScore)) {
            throw new IllegalArgumentException("Diem qua trinh phai nam trong doan [0, 10] hoac null.");
        }
        this.processScore = processScore;
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
    public void setExamScore(Double examScore) {
        if (!isValidScore(examScore)) {
            throw new IllegalArgumentException("Diem cuoi ki phai nam trong doan [0, 10] hoac null.");
        }
        this.examScore = examScore;
    }

    public Double getProcessGrade() {
        return this.processScore;
    }

    public void setProcessGrade(Double processGrade) {
        this.setProcessScore(processGrade);
    }

    public Double getFinalExamGrade() {
        return this.examScore;
    }

    public void setFinalExamGrade(Double finalExamGrade) {
        this.setExamScore(finalExamGrade);
    }

    public boolean isComplete() {
        return this.processScore != null && this.examScore != null;
    }

    public String getStatus() {
        return this.isComplete() ? STATUS_COMPLETE : STATUS_INCOMPLETE;
    }

    public Double getFinalScore() {
        if (!this.isComplete()) {
            return null;
        }
        return this.course.calculateFinalScore(this.processScore, this.examScore);
    }

    public Double getFinalGrade() {
        return this.getFinalScore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GradeRecord that = (GradeRecord) o;
        return Objects.equals(this.student.getStudentId(), that.student.getStudentId()) &&
               Objects.equals(this.course.getCourseId(), that.course.getCourseId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.student.getStudentId(), this.course.getCourseId());
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
        String processStr = this.processScore == null ? "-" : String.format("%.2f", this.processScore);
        String examStr = this.examScore == null ? "-" : String.format("%.2f", this.examScore);
        Double finalScore = this.getFinalScore();
        String finalStr = finalScore == null ? "-" : String.format("%.2f", finalScore);
        return String.format("[%s] %s | %s | QT: %s | CK: %s | TK: %s",
                this.getStatus(), this.student.getStudentId(), this.course.getCourseId(),
                processStr, examStr, finalStr);

    }
}

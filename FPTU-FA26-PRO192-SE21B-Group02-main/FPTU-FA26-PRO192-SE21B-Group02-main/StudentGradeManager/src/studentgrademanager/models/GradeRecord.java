
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentgrademanager.models;

/**
 *
 * @author Admin
 */
import java.io.Serializable;
public class GradeRecord implements Serializable{
    private static final long serialVersionUID = 1L;

    private Student student;
    private Course course;
    private Double processScore;
    private Double examScore;

    public GradeRecord(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.processScore = null;
        this.examScore = null;
    }

    public GradeRecord(Student student, Course course, Double processScore, Double examScore) {
        this.student = student;
        this.course = course;
        this.processScore = processScore;
        this.examScore = examScore;
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
        this.processScore = processScore;
    }

    public Double getExamScore() {
        return this.examScore;
    }

    public void setExamScore(Double examScore) {
        this.examScore = examScore;
    }

    public void updateProcessScore(double score) {
        this.processScore = score;
    }

    public void updateExamScore(double score) {
        this.examScore = score;
    }

    public boolean isComplete() {
        return this.processScore != null && this.examScore != null;
    }

    public Double getFinalScore() {
        if (!this.isComplete()) {
            return null;
        }
        return this.course.calculateFinalGrade(this.processScore, this.examScore);
    }

    public String getStatus() {
        if (!this.isComplete()) {
            return "CHUA_DU_DIEM";
        }
        Double finalScore = this.getFinalScore();
        if (finalScore != null && finalScore >= 5.0) {
            return "DAT";
        }
        return "CHUA_DAT";
    }

}

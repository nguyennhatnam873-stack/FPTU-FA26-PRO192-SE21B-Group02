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

public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    private String studentId;
    private String fullName;

    public Student(String studentId, String fullName) {
        this.studentId = (studentId != null) ? studentId.trim() : "";
        this.fullName = (fullName != null) ? fullName.trim() : "";
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = (studentId != null) ? studentId.trim() : "";
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = (fullName != null) ? fullName.trim() : "";
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-25s", this.studentId, this.fullName);
    }

}

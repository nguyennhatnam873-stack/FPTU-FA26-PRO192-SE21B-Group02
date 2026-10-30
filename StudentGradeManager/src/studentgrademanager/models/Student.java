<<<<<<< HEAD
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.models;

import java.io.Serializable;
import java.util.Locale;

public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String studentId;
    private final String fullName;

    public Student(String studentId, String fullName) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma sinh vien khong duoc rong.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ho ten khong duoc rong.");
        }

        this.studentId =
                studentId.trim().toUpperCase(Locale.ROOT);
        this.fullName = fullName.trim();
    }

=======
package studentgrademanager.models;

import java.io.Serializable;
import java.util.Objects;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String fullName;

    public Student(String studentId, String fullName) {
        if (!isValidStudentId(studentId)) {
            throw new IllegalArgumentException("Ma so sinh vien khong duoc de trong.");
        }
        if (!isValidFullName(fullName)) {
            throw new IllegalArgumentException("Ho ten sinh vien khong duoc de trong.");
        }
        this.studentId = studentId.trim().toUpperCase();
        this.fullName = fullName.trim();
    }

    public static boolean isValidStudentId(String studentId) {
        return studentId != null && !studentId.trim().isEmpty();
    }

    public static boolean isValidFullName(String fullName) {
        return fullName != null && !fullName.trim().isEmpty();
    }

>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
    public String getStudentId() {
        return this.studentId;
    }

<<<<<<< HEAD
=======
    public void setStudentId(String studentId) {
        if (!isValidStudentId(studentId)) {
            throw new IllegalArgumentException("Ma so sinh vien khong duoc de trong.");
        }
        this.studentId = studentId.trim().toUpperCase();
    }

>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
    public String getFullName() {
        return this.fullName;
    }

<<<<<<< HEAD
    @Override
    public String toString() {
        return this.studentId + " | " + this.fullName;
    }
}
=======
    public void setFullName(String fullName) {
        if (!isValidFullName(fullName)) {
            throw new IllegalArgumentException("Ho ten sinh vien khong duoc de trong.");
        }
        this.fullName = fullName.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return Objects.equals(this.studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.studentId);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", this.studentId, this.fullName);
    }
}
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04

package studentgrademanager.models;

import java.io.Serializable;
import java.util.Objects;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String fullName;

    public Student(String studentId, String fullName) {
        if (!isValidStudentId(studentId)) {
            throw new IllegalArgumentException("Mã số sinh viên không được để trống.");
        }
        if (!isValidFullName(fullName)) {
            throw new IllegalArgumentException("Họ tên sinh viên không được để trống.");
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

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        if (!isValidStudentId(studentId)) {
            throw new IllegalArgumentException("Mã số sinh viên không được để trống.");
        }
        this.studentId = studentId.trim().toUpperCase();
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        if (!isValidFullName(fullName)) {
            throw new IllegalArgumentException("Họ tên sinh viên không được để trống.");
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

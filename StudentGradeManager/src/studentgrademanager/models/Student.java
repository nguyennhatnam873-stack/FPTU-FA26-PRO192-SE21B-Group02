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

    public String getStudentId() {
        return this.studentId;
    }

    public String getFullName() {
        return this.fullName;
    }

    @Override
    public String toString() {
        return this.studentId + " | " + this.fullName;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import studentgrademanager.models.Student;

public class StudentManager {

    private final Map<String, Student> students =
            new LinkedHashMap<String, Student>();

    public void addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException(
                    "Sinh vien khong duoc null.");
        }

        String id = student.getStudentId()
                .trim().toUpperCase(Locale.ROOT);

        if (students.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Ma sinh vien da ton tai: " + id);
        }

        students.put(id, student);
    }

    public Student findStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return null;
        }

        return students.get(
                studentId.trim().toUpperCase(Locale.ROOT));
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(
                new ArrayList<Student>(students.values()));
    }

    public int getStudentCount() {
        return students.size();
    }
    
public void loadStudents(List<Student> data) {
    students.clear();

    for (Student student : data) {
        addStudent(student);
    }
}

}

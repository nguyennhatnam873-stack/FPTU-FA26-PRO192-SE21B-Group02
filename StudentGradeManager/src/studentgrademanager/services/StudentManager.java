/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentgrademanager.services;

/**
 *
 * @author Admin
 */
import java.util.ArrayList;
import java.util.List;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

public class StudentManager {

    private List<Student> students;

    public StudentManager() {
        this.students = new ArrayList<>();
    }

    public boolean addStudent(String studentId, String fullName) {
        if (studentId == null || fullName == null) {
            return false;
        }
        String cleanId = studentId.trim();
        String cleanName = fullName.trim();

        if (cleanId.isEmpty() || cleanName.isEmpty()) {
            System.out.println("Loi: MSSV va Ho ten khong duoc de trong!");
            return false;
        }
        if (this.findStudent(cleanId) != null) {
            System.out.println("Loi: MSSV '" + cleanId + "' da ton tai!");
            return false;
        }
        this.students.add(new Student(cleanId, cleanName));
        return true;
    }

    public boolean addStudent(Student s) {
        if (s == null) {
            return false;
        }
        return this.addStudent(s.getStudentId(), s.getFullName());
    }

    public Student findStudent(String studentId) {
        if (studentId == null) {
            return null;
        }
        String cleanId = studentId.trim();
        for (Student s : this.students) {
            if (s.getStudentId().equalsIgnoreCase(cleanId)) {
                return s;
            }
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return this.students;
    }

    public void clear() {
        this.students.clear();
    }

    public boolean deleteStudent(String studentId, List<GradeRecord> allRecords) {
        Student s = this.findStudent(studentId);
        if (s == null) {
            System.out.println("Loi: Khong tim thay sinh vien de xoa!");
            return false;
        }
        if (allRecords != null) {
            for (GradeRecord record : allRecords) {
                if (record.getStudent().getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    System.out.println("Loi: Khong duoc xoa sinh vien da co ket qua hoc tap!");
                    return false;
                }
            }
        }
        this.students.remove(s);
        System.out.println("Xoa sinh vien thanh cong!");
        return true;
    }
}

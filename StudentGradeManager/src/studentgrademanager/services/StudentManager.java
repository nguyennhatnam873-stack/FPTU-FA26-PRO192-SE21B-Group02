package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

/**
 * Lớp dịch vụ quản lý Hồ sơ sinh viên.
 * Phụ trách: Người 3 — Sinh viên & cặp SV–môn
 */
public class StudentManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Student> students;

    public StudentManager() {
        this.students = new ArrayList<Student>();
    }

   
    public synchronized boolean addStudent(String studentId, String fullName) {
        if (studentId == null || fullName == null) {
            return false;
        }
        String cleanId = studentId.trim();
        String cleanName = fullName.trim();

        if (cleanId.isEmpty() || cleanName.isEmpty()) {
            System.out.println("Loi: MSSV va Ho ten khong duoc de trong");
            return false;
        }

        if (this.findStudent(cleanId) != null) {
            System.out.println("Loi: MSSV '" + cleanId + "' da ton tai!");
            return false;
        }

        this.students.add(new Student(cleanId, cleanName));
        return true;
    }

   
    public synchronized boolean addStudent(Student s) {
        if (s == null) {
            return false;
        }
        if (this.findStudent(s.getStudentId()) != null) {
            return false;
        }
        this.students.add(s);
        return true;
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
        return Collections.unmodifiableList(new ArrayList<Student>(this.students));
    }

    
    public int getStudentCount() {
        return this.students.size();
    }

    
    public boolean exists(String studentId) {
        return this.findStudent(studentId) != null;
    }


    public synchronized boolean deleteStudent(String studentId, List<GradeRecord> allRecords) {
        Student s = this.findStudent(studentId);
        if (s == null) {
            System.out.println("Loi: Khong tim thay sinh vien de xoa!");
            return false;
        }

        if (allRecords != null) {
            for (GradeRecord record : allRecords) {
                if (record.getStudent() != null && 
                    record.getStudent().getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    System.out.println("Loi: Khong duoc xoa sinh vien '" + s.getStudentId() + "' vi da co ket qua hoc tap!");
                    return false;
                }
            }
        }

        boolean removed = this.students.remove(s);
        if (removed) {
            System.out.println("Xoa sinh vien thanh cong!");
        }
        return removed;
    }

    public synchronized void clear() {
        this.students.clear();
    }
}

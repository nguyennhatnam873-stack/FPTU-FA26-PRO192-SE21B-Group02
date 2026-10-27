package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

public class StudentManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Student> students;

    public StudentManager() {
        this.students = new ArrayList<Student>();
    }

    public synchronized boolean addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Đối tượng sinh viên không được null.");
        }
        if (this.findStudent(student.getStudentId()) != null) {
            throw new IllegalArgumentException("Mã số sinh viên '" + student.getStudentId() + "' đã tồn tại.");
        }
        return this.students.add(student);
    }

    public synchronized boolean addStudent(String studentId, String fullName) {
        Student s = new Student(studentId, fullName);
        return this.addStudent(s);
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
            return false;
        }

        if (allRecords != null) {
            for (GradeRecord record : allRecords) {
                if (record.getStudent() != null && 
                    record.getStudent().getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    throw new IllegalStateException("Không được xóa sinh viên '" + s.getStudentId() + "' vì đã có kết quả học tập!");
                }
            }
        }

        return this.students.remove(s);
    }

    public synchronized void clear() {
        this.students.clear();
    }
}

package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.GradeRecord;
import model.Student;

/**
 * Lop quan ly danh sach Sinh vien (StudentManager).
 * Chuyen trach cua Thanh vien 3 (Nguoi 3) - PRO192 FA26 P05.
 * 
 * Nghiep vu:
 * 1. Them sinh vien (MSSV duy nhat, ho ten khong rong, trim khoang trang dau/cuoi).
 * 2. Tim sinh vien theo MSSV.
 * 3. Lay danh sach toan bo sinh vien.
 * 4. Rang buoc bao ve: Khong cho xoa sinh vien da co ket qua diem hoc tap.
 */
public class StudentManager {
    private List<Student> students;

    public StudentManager() {
        this.students = new ArrayList<>();
    }

    /**
     * Them mot sinh vien moi vao he thong.
     * @param student Doi tuong Student can them.
     * @return true neu them thanh cong.
     * @throws IllegalArgumentException neu trung MSSV hoac du lieu khong hop le.
     */
    public boolean addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Doi tuong sinh vien khong duoc null.");
        }
        
        String cleanId = student.getStudentId();
        if (this.findStudent(cleanId) != null) {
            throw new IllegalArgumentException("Loi: MSSV [" + cleanId + "] da ton tai trong he thong.");
        }

        return this.students.add(student);
    }

    /**
     * Tim sinh vien theo ma so sinh vien (MSSV).
     * @param studentId MSSV can tim.
     * @return Doi tuong Student neu tim thay, null neu khong ton tai.
     */
    public Student findStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return null;
        }
        String searchId = studentId.trim();
        for (Student s : this.students) {
            if (s.getStudentId().equalsIgnoreCase(searchId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Lay toan bo danh sach sinh vien (tra ve danh sach unmodifiable de bao ve tinh dong goi).
     */
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(this.students);
    }

    /**
     * Dem tong so sinh vien hien co.
     */
    public int getStudentCount() {
        return this.students.size();
    }

    /**
     * Kiem tra va xoa sinh vien khoi he thong.
     * Quy tac: Khong cho phep xoa sinh vien neu da co ket qua diem (GradeRecord).
     * @param studentId MSSV can xoa.
     * @param existingRecords Danh sach tat ca cac ban ghi diem trong he thong.
     * @return true neu xoa thanh cong.
     */
    public boolean deleteStudent(String studentId, List<GradeRecord> existingRecords) {
        Student s = this.findStudent(studentId);
        if (s == null) {
            throw new IllegalArgumentException("Khong tim thay sinh vien voi MSSV: " + studentId);
        }

        // Kiem tra xem sinh vien da co ket qua hoc tap chua
        if (existingRecords != null) {
            for (GradeRecord record : existingRecords) {
                if (record.getStudent().getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    throw new IllegalStateException("Khong duoc phep xoa sinh vien [" + studentId + "] vi da co ket qua hoc tap.");
                }
            }
        }

        return this.students.remove(s);
    }
}

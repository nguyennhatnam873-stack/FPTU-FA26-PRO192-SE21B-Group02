package model;

/**
 * Lop bieu dien Sinh vien trong he thong quan ly bang diem.
 * Phuc vu yeu cau cua Thanh vien 3 (Nguoi 3) - PRO192 FA26 P05.
 * 
 * Quy tac OOP:
 * - Thuoc tinh private 100%.
 * - Su dung 'this.' tuong minh khi truy cap thuoc tinh/phuong thuc.
 */
public class Student {
    private String studentId;
    private String fullName;

    public Student() {
    }

    public Student(String studentId, String fullName) {
        this.setStudentId(studentId);
        this.setFullName(fullName);
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("MSSV khong duoc de trong.");
        }
        this.studentId = studentId.trim();
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ho ten sinh vien khong duoc de trong.");
        }
        this.fullName = fullName.trim();
    }

    @Override
    public String toString() {
        return String.format("Student [MSSV: %s, Ho ten: %s]", this.studentId, this.fullName);
    }
}

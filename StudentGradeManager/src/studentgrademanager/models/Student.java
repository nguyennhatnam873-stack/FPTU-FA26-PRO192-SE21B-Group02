package studentgrademanager.models;

/**
 * Phụ trách: Người 3 — Sinh viên & cặp SV–môn
 */
public class Student {
    // TODO: Thành viên tự viết code tại đây
    private String studentId;
    private String fullName;

    public Student(String studentId, String fullName){
        this.studentId = studentId;
        this.fullName = fullName;
    }
    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Student{" + "MSSV='" + this.studentId + '\'' + ", HoTen='" + this.fullName + '\'' + '}';
    }
}



package studentgrademanager.services;

/**
 * Phụ trách: Người 3 — Sinh viên & cặp SV–môn
 */

import java.util.ArrayList;
import java.util.List;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
public class StudentManager {
    // TODO: Thành viên tự viết code tại đây
    private List<Student> students;

    public StudentManager() {
        this.students = new ArrayList<>();
    }

    // 1. Thêm sinh viên (Kiểm tra rỗng, trim, trùng mã)
    public boolean addStudent(String studentId, String fullName) {
        if (studentId == null || fullName == null) {
            return false;
        }
        String cleanId = studentId.trim();
        String cleanName = fullName.trim();

        // Kiểm tra không được rỗng
        if (cleanId.isEmpty() || cleanName.isEmpty()) {
            System.out.println("Lỗi: MSSV và Họ tên không được để trống!");
            return false;
        }

        // Kiểm tra trùng mã
        if (this.findStudent(cleanId) != null) {
            System.out.println("Lỗi: MSSV '" + cleanId + "' đã tồn tại!");
            return false;
        }

        // Hợp lệ -> thêm mới
        this.students.add(new Student(cleanId, cleanName));
        return true;
    }

    // 2. Tìm sinh viên theo mã
    public Student findStudent(String studentId) {
        if (studentId == null) return null;
        String cleanId = studentId.trim();
        for (Student s : this.students) {
            if (s.getStudentId().equalsIgnoreCase(cleanId)) {
                return s;
            }
        }
        return null;
    }

    // 3. Lấy danh sách toàn bộ sinh viên
    public List<Student> getAllStudents() {
        return this.students;
    }

    // 4. Kiểm tra điều kiện xóa (Không cho xóa SV đã có kết quả)
    public boolean deleteStudent(String studentId, List<GradeRecord> allRecords) {
        Student s = this.findStudent(studentId);
        if (s == null) {
            System.out.println("Lỗi: Không tìm thấy sinh viên để xóa!");
            return false;
        }

        // Kiểm tra xem sinh viên này đã có bản ghi điểm nào chưa
        if (allRecords != null) {
            for (GradeRecord record : allRecords) {
                if (record.getStudent().getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    System.out.println("Lỗi: Không được xóa sinh viên đã có kết quả học tập!");
                    return false;
                }
            }
        }

        this.students.remove(s);
        System.out.println("Xóa sinh viên thành công!");
        return true;
    }
}



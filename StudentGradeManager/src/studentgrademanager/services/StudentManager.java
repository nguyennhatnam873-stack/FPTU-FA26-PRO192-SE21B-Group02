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

    // 1. Thêm sinh viên từ chuỗi (Kiểm tra rỗng, trim, trùng mã)
    public synchronized boolean addStudent(String studentId, String fullName) {
        if (studentId == null || fullName == null) {
            return false;
        }
        String cleanId = studentId.trim();
        String cleanName = fullName.trim();

        if (cleanId.isEmpty() || cleanName.isEmpty()) {
            System.out.println("Lỗi: MSSV và Họ tên không được để trống!");
            return false;
        }

        if (this.findStudent(cleanId) != null) {
            System.out.println("Lỗi: MSSV '" + cleanId + "' đã tồn tại!");
            return false;
        }

        this.students.add(new Student(cleanId, cleanName));
        return true;
    }

    // 2. Thêm sinh viên từ đối tượng Student (phục vụ FileDataStore đọc từ file)
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

    // 3. Tìm sinh viên theo mã (không phân biệt hoa thường)
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

    // 4. Lấy danh sách toàn bộ sinh viên
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(new ArrayList<Student>(this.students));
    }

    // 5. Số lượng sinh viên hiện có
    public int getStudentCount() {
        return this.students.size();
    }

    // 6. Kiểm tra sinh viên có tồn tại hay không
    public boolean exists(String studentId) {
        return this.findStudent(studentId) != null;
    }

    // 7. Kiểm tra điều kiện xóa (Không cho xóa SV đã có kết quả)
    public synchronized boolean deleteStudent(String studentId, List<GradeRecord> allRecords) {
        Student s = this.findStudent(studentId);
        if (s == null) {
            System.out.println("Lỗi: Không tìm thấy sinh viên để xóa!");
            return false;
        }

        if (allRecords != null) {
            for (GradeRecord record : allRecords) {
                if (record.getStudent() != null && 
                    record.getStudent().getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    System.out.println("Lỗi: Không được xóa sinh viên '" + s.getStudentId() + "' vì đã có kết quả học tập!");
                    return false;
                }
            }
        }

        boolean removed = this.students.remove(s);
        if (removed) {
            System.out.println("Xóa sinh viên thành công!");
        }
        return removed;
    }

    // 8. Xóa danh sách sinh viên để nạp mới từ file
    public synchronized void clear() {
        this.students.clear();
    }
}

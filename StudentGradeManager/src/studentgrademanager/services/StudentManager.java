package studentgrademanager.services;

<<<<<<< HEAD
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
=======
import java.util.ArrayList;
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
import java.util.List;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

<<<<<<< HEAD
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
=======
/**
 * Phụ trách: Người 3 — Sinh viên & cặp SV–môn
 */
public class StudentManager {
    private List<Student> students;

    public StudentManager() {
        this.students = new ArrayList<>();
    }

    // 1. Thêm sinh viên từ chuỗi (Kiểm tra rỗng, trim, trùng mã)
    public boolean addStudent(String studentId, String fullName) {
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
    public boolean addStudent(Student s) {
        if (s == null) {
            return false;
        }
        return this.addStudent(s.getStudentId(), s.getFullName());
    }

    // 3. Tìm sinh viên theo mã (không phân biệt hoa thường)
    public Student findStudent(String studentId) {
        if (studentId == null) return null;
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
        String cleanId = studentId.trim();
        for (Student s : this.students) {
            if (s.getStudentId().equalsIgnoreCase(cleanId)) {
                return s;
            }
        }
        return null;
    }

<<<<<<< HEAD
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
=======
    // 4. Lấy danh sách toàn bộ sinh viên
    public List<Student> getAllStudents() {
        return this.students;
    }

    // 5. Xóa danh sách sinh viên để nạp mới từ file
    public void clear() {
        this.students.clear();
    }

    // 6. Kiểm tra điều kiện xóa (Không cho xóa SV đã có kết quả)
    public boolean deleteStudent(String studentId, List<GradeRecord> allRecords) {
        Student s = this.findStudent(studentId);
        if (s == null) {
            System.out.println("Lỗi: Không tìm thấy sinh viên để xóa!");
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
            return false;
        }

        if (allRecords != null) {
            for (GradeRecord record : allRecords) {
<<<<<<< HEAD
                if (record.getStudent() != null && 
                    record.getStudent().getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    throw new IllegalStateException("Không được xóa sinh viên '" + s.getStudentId() + "' vì đã có kết quả học tập!");
=======
                if (record.getStudent().getStudentId().equalsIgnoreCase(s.getStudentId())) {
                    System.out.println("Lỗi: Không được xóa sinh viên đã có kết quả học tập!");
                    return false;
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
                }
            }
        }

<<<<<<< HEAD
        return this.students.remove(s);
    }

    public synchronized void clear() {
        this.students.clear();
=======
        this.students.remove(s);
        System.out.println("Xóa sinh viên thành công!");
        return true;
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
    }
}

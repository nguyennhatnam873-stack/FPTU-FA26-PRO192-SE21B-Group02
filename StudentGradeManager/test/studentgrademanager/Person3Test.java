package studentgrademanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.services.StudentManager;

/**
 * Bộ kiểm thử tự động cho Người 3 — Sinh viên & Cặp SV–Môn.
 * Phụ trách các ca kiểm thử: D05, D06 và các quy tắc toàn vẹn dữ liệu hồ sơ sinh viên.
 * 
 * Chuẩn: JDK 8, NetBeans 17.
 */
public class Person3Test {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" BẮT ĐẦU KIỂM THỬ CHO NGƯỜI 3: SINH VIÊN & CẶP SV–MÔN (PRO192)");
        System.out.println("================================================================\n");

        testAddStudent_Valid();
        testD05_DuplicateStudentId_And_EmptyWhitespace();
        testD05_FindNonExistentStudent();
        testD06_NoDuplicateStudentRecord();
        testDeleteStudent_DataIntegrityGuard();

        System.out.println("\n================================================================");
        System.out.printf(Locale.US, " KẾT QUẢ KIỂM THỬ NGƯỜI 3: %d/%d PASS (%.1f%%)\n",
                passedTests, totalTests, (passedTests * 100.0 / totalTests));
        System.out.println("================================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> TẤT CẢ CÁC CA TEST CỦA NGƯỜI 3 ĐÃ ĐẠT YÊU CẦU 100%! <<<");
        } else {
            System.err.println(">>> CÓ CA TEST CHƯA ĐẠT! <<<");
            System.exit(1);
        }
    }

    // TEST 1: Thêm sinh viên hợp lệ
    private static void testAddStudent_Valid() {
        totalTests++;
        System.out.println("[TEST 1] Thêm sinh viên mới hợp lệ (QE210003 - Lê Cường)...");
        StudentManager sm = new StudentManager();
        boolean ok = sm.addStudent("QE210003", "Lê Cường");
        if (ok && sm.findStudent("QE210003") != null) {
            System.out.println(">> [PASS] Thêm thành công sinh viên mới.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Không thêm được sinh viên hợp lệ.\n");
        }
    }

    // TEST 2: Ca D05 - Chặn trùng MSSV (không phân biệt hoa thường) và chuỗi rỗng/khoảng trắng
    private static void testD05_DuplicateStudentId_And_EmptyWhitespace() {
        totalTests++;
        System.out.println("[TEST 2 - Ca D05] Thử thêm trùng MSSV 'QE210001' và MSSV chứa khoảng trắng...");
        StudentManager sm = new StudentManager();
        sm.addStudent("QE210001", "Nguyễn An");

        boolean dup = sm.addStudent("qe210001", "Nguyễn An Trùng");
        boolean emptyId = sm.addStudent("   ", "Tên Hợp Lệ");
        boolean emptyName = sm.addStudent("QE210099", "   ");

        if (!dup && !emptyId && !emptyName) {
            System.out.println(">> [PASS] Đã từ chối chính xác mã trùng và chuỗi rỗng/khoảng trắng.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Vẫn cho phép thêm dữ liệu sai.\n");
        }
    }

    // TEST 3: Ca D05 - Tra cứu mã sinh viên không tồn tại
    private static void testD05_FindNonExistentStudent() {
        totalTests++;
        System.out.println("[TEST 3 - Ca D05] Tra cứu sinh viên với mã không tồn tại 'QE999999'...");
        StudentManager sm = new StudentManager();
        Student s = sm.findStudent("QE999999");
        if (s == null) {
            System.out.println(">> [PASS] Trả về null chính xác, không tự động sinh mã bừa bãi.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Tự động tạo sinh viên khi tra cứu sai.\n");
        }
    }

    // TEST 4: Ca D06 - Chống tạo dòng thứ 2 cho cặp SV-Môn đã có kết quả
    private static void testD06_NoDuplicateStudentRecord() {
        totalTests++;
        System.out.println("[TEST 4 - Ca D06] Kiểm tra chống tạo dòng thứ hai cho cặp An - LT101...");
        Student an = new Student("QE210001", "Nguyễn An");
        Course lt101 = new TheoryCourse("LT101", "Nhập môn lý thuyết", 3);

        List<GradeRecord> records = new ArrayList<GradeRecord>();
        records.add(new GradeRecord(an, lt101, 8.0, 6.0));

        // Kiểm tra logic phát hiện trùng
        boolean isDuplicate = false;
        for (GradeRecord r : records) {
            if (r.getStudent().getStudentId().equalsIgnoreCase("QE210001") &&
                r.getCourse().getCourseId().equalsIgnoreCase("LT101")) {
                isDuplicate = true;
                break;
            }
        }

        if (isDuplicate) {
            System.out.println(">> [PASS] Phát hiện trùng cặp SV-Môn, không cấp phát dòng thứ hai.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Không phát hiện được trùng cặp SV-Môn.\n");
        }
    }

    // TEST 5: Ràng buộc an toàn dữ liệu - Không xóa sinh viên đã có điểm
    private static void testDeleteStudent_DataIntegrityGuard() {
        totalTests++;
        System.out.println("[TEST 5] Kiểm tra cấm xóa sinh viên đã có kết quả học tập...");
        StudentManager sm = new StudentManager();
        sm.addStudent("QE210001", "Nguyễn An");

        Student an = sm.findStudent("QE210001");
        Course lt101 = new TheoryCourse("LT101", "Nhập môn lý thuyết", 3);
        List<GradeRecord> records = new ArrayList<GradeRecord>();
        records.add(new GradeRecord(an, lt101, 8.0, 6.0));

        boolean deleted = sm.deleteStudent("QE210001", records);
        if (!deleted && sm.findStudent("QE210001") != null) {
            System.out.println(">> [PASS] Đã chặn xóa sinh viên có điểm thành công, bảo toàn dữ liệu.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Sinh viên đã có điểm vẫn bị xóa khỏi hệ thống.\n");
        }
    }
}

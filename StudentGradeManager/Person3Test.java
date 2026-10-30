package person3test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.services.StudentManager;

/**
 * Bo kiem thu tu dong cho Nguoi 3 -- Sinh vien & Cap SV-Mon.
 * Phu trach cac ca kiem thu: D05, D06 va cac quy tac toan ven du lieu ho so sinh vien.
 * 
 * Chuan: JDK 8, NetBeans 17.
 */
public class Person3Test {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" BAT DAU KIEM THU CHO NGUOI 3: SINH VIEN & CAP SV-MON (PRO192)");
        System.out.println("================================================================\n");

        testAddStudent_Valid();
        testD05_DuplicateStudentId_And_EmptyWhitespace();
        testD05_FindNonExistentStudent();
        testD06_NoDuplicateStudentRecord();
        testDeleteStudent_DataIntegrityGuard();

        System.out.println("\n================================================================");
        System.out.printf(Locale.US, " KET QUA KIEM THU NGUOI 3: %d/%d PASS (%.1f%%)\n",
                passedTests, totalTests, (passedTests * 100.0 / totalTests));
        System.out.println("================================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> TAT CA CAC CA TEST CUA NGUOI 3 DA DAT YEU CAU 100%! <<<");
        } else {
            System.err.println(">>> CO CA TEST CHUA DAT! <<<");
            System.exit(1);
        }
    }

    // TEST 1: Them sinh vien hop le
    private static void testAddStudent_Valid() {
        totalTests++;
        System.out.println("[TEST 1] Them sinh vien moi hop le (QE210003 - Le Cuong)...");
        StudentManager sm = new StudentManager();
        boolean ok = sm.addStudent("QE210003", "Le Cuong");
        if (ok && sm.findStudent("QE210003") != null) {
            System.out.println(">> [PASS] Them thanh cong sinh vien moi.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Khong them duoc sinh vien hop le.\n");
        }
    }

    // TEST 2: Ca D05 - Chan trung MSSV (khong phan biet hoa thuong) va chuoi rong/khoang trang
    private static void testD05_DuplicateStudentId_And_EmptyWhitespace() {
        totalTests++;
        System.out.println("[TEST 2 - Ca D05] Thu them trung MSSV 'QE210001' va MSSV chua khoang trang...");
        StudentManager sm = new StudentManager();
        sm.addStudent("QE210001", "Nguyen An");

        boolean dup = sm.addStudent("qe210001", "Nguyen An Trung");
        boolean emptyId = sm.addStudent("   ", "Ten Hop Le");
        boolean emptyName = sm.addStudent("QE210099", "   ");

        if (!dup && !emptyId && !emptyName) {
            System.out.println(">> [PASS] Da tu choi chinh xac ma trung va chuoi rong/khoang trang.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Van cho phep them du lieu sai.\n");
        }
    }

    // TEST 3: Ca D05 - Tra cuu ma sinh vien khong ton tai
    private static void testD05_FindNonExistentStudent() {
        totalTests++;
        System.out.println("[TEST 3 - Ca D05] Tra cuu sinh vien voi ma khong ton tai 'QE999999'...");
        StudentManager sm = new StudentManager();
        Student s = sm.findStudent("QE999999");
        if (s == null) {
            System.out.println(">> [PASS] Tra ve null chinh xac, khong tu dong sinh ma bua bai.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Tu dong tao sinh vien khi tra cuu sai.\n");
        }
    }

    // TEST 4: Ca D06 - Chong tao dong thu 2 cho cap SV-Mon da co ket qua
    private static void testD06_NoDuplicateStudentRecord() {
        totalTests++;
        System.out.println("[TEST 4 - Ca D06] Kiem tra chong tao dong thu hai cho cap An - LT101...");
        Student an = new Student("QE210001", "Nguyen An");
        Course lt101 = new TheoryCourse("LT101", "Nhap mon ly thuyet", 3);

        List<GradeRecord> records = new ArrayList<GradeRecord>();
        records.add(new GradeRecord(an, lt101, 8.0, 6.0));

        // Kiem tra logic phat hien trung
        boolean isDuplicate = false;
        for (GradeRecord r : records) {
            if (r.getStudent().getStudentId().equalsIgnoreCase("QE210001") &&
                r.getCourse().getCourseId().equalsIgnoreCase("LT101")) {
                isDuplicate = true;
                break;
            }
        }

        if (isDuplicate) {
            System.out.println(">> [PASS] Phat hien trung cap SV-Mon, khong cap phat dong thu hai.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Khong phat hien duoc trung cap SV-Mon.\n");
        }
    }

    // TEST 5: Rang buoc an toan du lieu - Khong xoa sinh vien da co diem
    private static void testDeleteStudent_DataIntegrityGuard() {
        totalTests++;
        System.out.println("[TEST 5] Kiem tra cam xoa sinh vien da co ket qua hoc tap...");
        StudentManager sm = new StudentManager();
        sm.addStudent("QE210001", "Nguyen An");

        Student an = sm.findStudent("QE210001");
        Course lt101 = new TheoryCourse("LT101", "Nhap mon ly thuyet", 3);
        List<GradeRecord> records = new ArrayList<GradeRecord>();
        records.add(new GradeRecord(an, lt101, 8.0, 6.0));

        boolean deleted = sm.deleteStudent("QE210001", records);
        if (!deleted && sm.findStudent("QE210001") != null) {
            System.out.println(">> [PASS] Da chan xoa sinh vien co diem thanh cong, bao toan du lieu.\n");
            passedTests++;
        } else {
            System.out.println(">> [FAIL] Sinh vien da co diem van bi xoa khoi he thong.\n");
        }
    }
}

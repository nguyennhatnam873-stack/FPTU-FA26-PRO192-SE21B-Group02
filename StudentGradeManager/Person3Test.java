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
            System.out.println(">> [FAIL

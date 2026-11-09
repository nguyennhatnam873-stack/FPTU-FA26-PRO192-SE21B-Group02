package studentgrademanager;

import java.util.List;
import java.util.Locale;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.PracticeCourse;
import studentgrademanager.models.Student;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.models.Transcript;
import studentgrademanager.services.GradeManager;

public class Person4Test {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" BAT DAU KIEM THU CHO NGUOI 4: NHAP & CAP NHAT DIEM (PRO192)");
        System.out.println("================================================================\n");

        testScoreValidation_DomainAndSpecialValues();
        testScoreParsing_ValidAndInvalidStrings();
        testAtomicCheckBeforeAssign();
        testNoDuplicateRecords();
        testD03_MissingScoreStateAndEmptyStudent();
        testD04_UpdateLT102_ZeroAndTen();
        testD05_RejectionOfInvalidInputPreservesSampleData();
        testD10_ZeroScoreVsMissingScore();

        System.out.println("\n================================================================");
        System.out.printf(Locale.US, " KET QUA KIEM THU NGUOI 4: %d/%d PASS (%.1f%%)\n",
                passedTests, totalTests, (passedTests * 100.0 / totalTests));
        System.out.println("================================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> TAT CA CAC CA TEST CUA NGUOI 4 DA DAT YEU CAU 100%! <<<");
        } else {
            System.err.println(">>> CO CA TEST CHUA DAT! <<<");
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition, String details) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.printf("[PASS] %s: %s\n", testName, details);
        } else {
            System.err.printf("[FAIL] %s: %s\n", testName, details);
        }
    }

    private static void testScoreValidation_DomainAndSpecialValues() {
        System.out.println("--- Test 1: Kiem tra mien diem [0.0; 10.0] va gia tri dac biet ---");

        try {
            GradeManager.validateScore(null);
            assertTrue("validateScore(null)", true, "null duoc chap nhan la diem thieu");
        } catch (Exception e) {
            assertTrue("validateScore(null)", false, "null khong duoc nem loi: " + e.getMessage());
        }

        try {
            GradeManager.validateScore(0.0);
            assertTrue("validateScore(0.0)", true, "Diem 0.0 la hop le");
        } catch (Exception e) {
            assertTrue("validateScore(0.0)", false, "0.0 bi loi: " + e.getMessage());
        }

        try {
            GradeManager.validateScore(10.0);
            assertTrue("validateScore(10.0)", true, "Diem 10.0 la hop le");
        } catch (Exception e) {
            assertTrue("validateScore(10.0)", false, "10.0 bi loi: " + e.getMessage());
        }

        boolean caughtNeg = false;
        try {
            GradeManager.validateScore(-0.01);
        } catch (IllegalArgumentException e) {
            caughtNeg = true;
        }
        assertTrue("validateScore(-0.01)", caughtNeg, "Tu choi so am -0.01");

        boolean caughtOver = false;
        try {
            GradeManager.validateScore(10.01);
        } catch (IllegalArgumentException e) {
            caughtOver = true;
        }
        assertTrue("validateScore(10.01)", caughtOver, "Tu choi so lon hon muoi: 10.01");

        boolean caughtNaN = false;
        try {
            GradeManager.validateScore(Double.NaN);
        } catch (IllegalArgumentException e) {
            caughtNaN = true;
        }
        assertTrue("validateScore(NaN)", caughtNaN, "Tu choi Double.NaN");

        boolean caughtPosInf = false;
        try {
            GradeManager.validateScore(Double.POSITIVE_INFINITY);
        } catch (IllegalArgumentException e) {
            caughtPosInf = true;
        }
        assertTrue("validateScore(+Infinity)", caughtPosInf, "Tu choi POSITIVE_INFINITY");

        boolean caughtNegInf = false;
        try {
            GradeManager.validateScore(Double.NEGATIVE_INFINITY);
        } catch (IllegalArgumentException e) {
            caughtNegInf = true;
        }
        assertTrue("validateScore(-Infinity)", caughtNegInf, "Tu choi NEGATIVE_INFINITY");
    }

    private static void testScoreParsing_ValidAndInvalidStrings() {
        System.out.println("\n--- Test 2: Phan tich cu phap chuoi diem so (parseScore) ---");

        assertTrue("parseScore(null) -> null", GradeManager.parseScore(null) == null, "Tra ve null");
        assertTrue("parseScore(\"\") -> null", GradeManager.parseScore("") == null, "Chuoi rong tra ve null");
        assertTrue("parseScore(\"   \") -> null", GradeManager.parseScore("   ") == null, "Khoang trang tra ve null");
        assertTrue("parseScore(\"-\") -> null", GradeManager.parseScore("-") == null, "Ky tu '-' tra ve null");
        assertTrue("parseScore(\"EMPTY\") -> null", GradeManager.parseScore("EMPTY") == null, "'EMPTY' tra ve null");
        assertTrue("parseScore(\"empty\") -> null", GradeManager.parseScore("empty") == null, "'empty' tra ve null");
        assertTrue("parseScore(\"NULL\") -> null", GradeManager.parseScore("NULL") == null, "'NULL' tra ve null");

        assertTrue("parseScore(\"0\") -> 0.0", Double.valueOf(0.0).equals(GradeManager.parseScore("0")), "So 0 la 0.0");
        assertTrue("parseScore(\"0.0\") -> 0.0", Double.valueOf(0.0).equals(GradeManager.parseScore("0.0")), "So 0.0 la 0.0");
        assertTrue("parseScore(\"10\") -> 10.0", Double.valueOf(10.0).equals(GradeManager.parseScore("10")), "So 10 la 10.0");
        assertTrue("parseScore(\"8.5\") -> 8.5", Double.valueOf(8.5).equals(GradeManager.parseScore("8.5")), "So 8.5 la 8.5");
        assertTrue("parseScore(\"8,5\") -> 8.5 (dau phay)", Double.valueOf(8.5).equals(GradeManager.parseScore("8,5")), "Ho tro dau phay 8,5 -> 8.5");

        assertParseThrows("abc", "Chuoi chu cai 'abc'");
        assertParseThrows("NaN", "Chuoi dac biet 'NaN'");
        assertParseThrows("nan", "Chuoi dac biet 'nan'");
        assertParseThrows("Infinity", "Chuoi 'Infinity'");
        assertParseThrows("+Infinity", "Chuoi '+Infinity'");
        assertParseThrows("-Infinity", "Chuoi '-Infinity'");
        assertParseThrows("-0.01", "So am dang chuoi '-0.01'");
        assertParseThrows("10.01", "So > 10 dang chuoi '10.01'");
    }

    private static void assertParseThrows(String input, String description) {
        boolean caught = false;
        try {
            GradeManager.parseScore(input);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("parseScore('" + input + "')", caught, "Tu choi " + description);
    }

    private static void testAtomicCheckBeforeAssign() {
        System.out.println("\n--- Test 3: Tinh nguyen tu - Kiem tra toan bo truoc khi gan ---");

        GradeManager gm = new GradeManager();
        Student s = new Student("SE01", "Test Student");
        Course c = new TheoryCourse("LT101", "Ly thuyet 1", 3);

        gm.createOrUpdateRecord(s, c, 8.0, 6.0);
        GradeRecord record = gm.findRecord("SE01", "LT101");
        assertTrue("Ban ghi ban dau", record != null && record.getProcessScore() == 8.0 && record.getExamScore() == 6.0, "QT=8.0, CK=6.0");

        boolean caughtError = false;
        try {
            gm.createOrUpdateRecord(s, c, 9.0, 12.0);
        } catch (IllegalArgumentException e) {
            caughtError = true;
        }
        assertTrue("Bi tu choi khi CK sai", caughtError, "Ngoai le khi CK = 12.0");

        assertTrue("QT giu nguyen 8.0", record.getProcessScore() == 8.0, "Diem QT cu duoc bao toan");
        assertTrue("CK giu nguyen 6.0", record.getExamScore() == 6.0, "Diem CK cu duoc bao toan");
    }

    private static void testNoDuplicateRecords() {
        System.out.println("\n--- Test 4: Khong tao ban ghi trung lap cho cung cap SV-Mon ---");

        GradeManager gm = new GradeManager();
        Student s = new Student("SE01", "Test Student");
        Course c = new TheoryCourse("LT101", "Ly thuyet 1", 3);

        gm.createOrUpdateRecord(s, c, 5.0, 5.0);
        gm.createOrUpdateRecord(s, c, 7.0, 8.0);
        gm.addOrUpdateGrade(s, c, 9.0, 10.0);

        assertTrue("Chi co 1 ban ghi duy nhat", gm.getRecordCount() == 1, "So ban ghi = " + gm.getRecordCount());
        GradeRecord r = gm.findRecord("SE01", "LT101");
        assertTrue("Diem cap nhat moi nhat", r.getProcessScore() == 9.0 && r.getExamScore() == 10.0, "QT=9.0, CK=10.0");
    }

    private static void testD03_MissingScoreStateAndEmptyStudent() {
        System.out.println("\n--- Test D03: Trang thai CHUA_DU_DIEM & Sinh vien chua co ket qua ---");

        GradeManager gm = new GradeManager();
        Student an = new Student("QE210001", "Nguyen Van An");
        Student binh = new Student("QE210002", "Tran Thi Binh");

        Course lt101 = new TheoryCourse("LT101", "Lap trinh Java can ban", 3);
        Course th101 = new PracticeCourse("TH101", "Thuc hanh lap trinh Java", 2);
        Course lt102 = new TheoryCourse("LT102", "Cau truc du lieu va giai thuat", 2);

        gm.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, lt102, 9.0, null); // CK thieu

        GradeRecord rLT102 = gm.findRecord("QE210001", "LT102");
        assertTrue("D03 - An LT102 chua hoan thanh", !rLT102.isComplete(), "isComplete() == false");
        String statusLT102 = rLT102.isComplete() ? GradeManager.DU_DIEM : GradeManager.CHUA_DU_DIEM;
        assertTrue("D03 - An LT102 trang thai CHUA_DU_DIEM", 
                GradeManager.CHUA_DU_DIEM.equals(statusLT102), "Status = " + statusLT102);
        assertTrue("D03 - An LT102 khong co diem tong ket", rLT102.getFinalScore() == null, "getFinalScore() == null");

        Transcript transcriptBinh = new Transcript(binh, gm.getRecordsByStudent(binh.getStudentId()));
        assertTrue("D03 - Binh 0/0 mon", transcriptBinh.getCompletedCourses() == 0 && transcriptBinh.getTotalCourses() == 0, "0/0 mon");
        assertTrue("D03 - Binh CHUA_CO_DIEM_TRUNG_BINH",
                !transcriptBinh.hasAverageScore() && Transcript.CHUA_CO_DIEM_TRUNG_BINH.equals(transcriptBinh.getFormattedGpa()),
                "GPA hien thi = " + transcriptBinh.getFormattedGpa());
    }

    private static void testD04_UpdateLT102_ZeroAndTen() {
        System.out.println("\n--- Test D04: Cap nhat LT102 QT 0, CK 10 -> Tong ket 7.00, GPA 6.83 ---");

        GradeManager gm = new GradeManager();
        Student an = new Student("QE210001", "Nguyen Van An");
        Course lt101 = new TheoryCourse("LT101", "Lap trinh Java can ban", 3);
        Course th101 = new PracticeCourse("TH101", "Thuc hanh lap trinh Java", 2);
        Course lt102 = new TheoryCourse("LT102", "Cau truc du lieu va giai thuat", 2);

        gm.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, lt102, 9.0, null);

        gm.createOrUpdateRecord(an, lt102, 0.0, 10.0);

        GradeRecord rLT102 = gm.findRecord("QE210001", "LT102");
        assertTrue("D04 - LT102 du diem", rLT102.isComplete(), "isComplete() == true");
        assertTrue("D04 - QT la 0.0 hop le", rLT102.getProcessScore() == 0.0, "QT = 0.0");
        assertTrue("D04 - CK la 10.0", rLT102.getExamScore() == 10.0, "CK = 10.0");

        Double finalScoreLT102 = rLT102.getFinalScore();
        boolean finalOk = finalScoreLT102 != null && Math.abs(finalScoreLT102 - 7.00) < 0.0001;
        assertTrue("D04 - Tong ket LT102 la 7.00", finalOk, String.format(Locale.US, "Diem TK = %.2f", finalScoreLT102));

        // Kiem tra GPA cua An: 47.8 / 7 = 6.82857 -> hien thi 6.83
        Transcript transcriptAn = new Transcript(an, gm.getRecordsByStudent(an.getStudentId()));
        assertTrue("D04 - Du 3/3 mon", transcriptAn.getCompletedCourses() == 3 && transcriptAn.getTotalCourses() == 3, "3/3 mon");

        double rawGPA = transcriptAn.calculateWeightedGpa();
        boolean gpaRawOk = Math.abs(rawGPA - (47.8 / 7.0)) < 0.0001;
        assertTrue("D04 - GPA tho = 47.8/7", gpaRawOk, String.format(Locale.US, "Raw GPA = %.5f", rawGPA));
        String formattedGPA = String.format(Locale.US, "%.2f", rawGPA);
        assertTrue("D04 - GPA lam tron hien thi 6.83", "6.83".equals(formattedGPA), "Formatted GPA: " + formattedGPA);
    }

    private static void testD05_RejectionOfInvalidInputPreservesSampleData() {
        System.out.println("\n--- Test D05: Tu choi cac thao tac sai pham, giu nguyen du lieu mau & GPA 6.76 ---");

        GradeManager gm = new GradeManager();
        Student an = new Student("QE210001", "Nguyen Van An");
        Course lt101 = new TheoryCourse("LT101", "Lap trinh Java can ban", 3);
        Course th101 = new PracticeCourse("TH101", "Thuc hanh lap trinh Java", 2);
        Course lt102 = new TheoryCourse("LT102", "Cau truc du lieu va giai thuat", 2);

        gm.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, lt102, 9.0, null);

        boolean c1 = false;
        try {
            Double val = GradeManager.parseScore("-0.01");
            gm.createOrUpdateRecord(an, lt101, val, 6.0);
        } catch (IllegalArgumentException e) {
            c1 = true;
        }
        assertTrue("D05 - Tu choi -0.01", c1, "Da bat loi -0.01");

        boolean c2 = false;
        try {
            Double val = GradeManager.parseScore("10.01");
            gm.createOrUpdateRecord(an, lt101, 8.0, val);
        } catch (IllegalArgumentException e) {
            c2 = true;
        }
        assertTrue("D05 - Tu choi 10.01", c2, "Da bat loi 10.01");

        boolean c3 = false;
        try {
            Double val = GradeManager.parseScore("NaN");
            gm.createOrUpdateRecord(an, lt101, val, 6.0);
        } catch (IllegalArgumentException e) {
            c3 = true;
        }
        assertTrue("D05 - Tu choi chuoi NaN", c3, "Da bat loi 'NaN'");

        boolean c4 = false;
        try {
            gm.createOrUpdateRecord(null, lt101, 8.0, 8.0);
        } catch (IllegalArgumentException e) {
            c4 = true;
        }
        assertTrue("D05 - Tu choi SV null", c4, "Khong cho phep sinh vien null");

        boolean c5 = false;
        try {
            gm.createOrUpdateRecord(an, null, 8.0, 8.0);
        } catch (IllegalArgumentException e) {
            c5 = true;
        }
        assertTrue("D05 - Tu choi Course null", c5, "Khong cho phep mon hoc null");

        GradeRecord r1 = gm.findRecord("QE210001", "LT101");
        GradeRecord r2 = gm.findRecord("QE210001", "TH101");
        GradeRecord r3 = gm.findRecord("QE210001", "LT102");

        assertTrue("D05 - LT101 giu nguyen 8.0, 6.0", r1.getProcessScore() == 8.0 && r1.getExamScore() == 6.0, "LT101 khong doi");
        assertTrue("D05 - TH101 giu nguyen 8.0, 6.0", r2.getProcessScore() == 8.0 && r2.getExamScore() == 6.0, "TH101 khong doi");
        assertTrue("D05 - LT102 giu nguyen 9.0, null", r3.getProcessScore() == 9.0 && r3.getExamScore() == null, "LT102 khong doi");

        Transcript t = new Transcript(an, gm.getRecordsByStudent(an.getStudentId()));
        String formattedGPA = String.format(Locale.US, "%.2f", t.calculateWeightedGpa());
        assertTrue("D05 - GPA An giu nguyen 6.76", "6.76".equals(formattedGPA), "GPA formatted: " + formattedGPA);
        assertTrue("D05 - Du 2/3 mon", t.getCompletedCourses() == 2 && t.getTotalCourses() == 3, "2/3 mon");
    }

    private static void testD10_ZeroScoreVsMissingScore() {
        System.out.println("\n--- Test D10: Diem 0 phan biet hoan toan voi diem thieu (An LT101: QT=0, CK=null) ---");

        GradeManager gm = new GradeManager();
        Student an = new Student("QE210001", "Nguyen Van An");
        Course lt101 = new TheoryCourse("LT101", "Lap trinh Java can ban", 3);
        Course th101 = new PracticeCourse("TH101", "Thuc hanh lap trinh Java", 2);
        Course lt102 = new TheoryCourse("LT102", "Cau truc du lieu va giai thuat", 2);

        gm.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, lt102, 9.0, null);

        gm.createOrUpdateRecord(an, lt101, 0.0, null);

        GradeRecord rLT101 = gm.findRecord("QE210001", "LT101");
        assertTrue("D10 - QT cua LT101 la 0.0", rLT101.getProcessScore() != null && rLT101.getProcessScore() == 0.0, "QT = 0.0 (khong null)");
        assertTrue("D10 - CK cua LT101 la thieu (null)", rLT101.getExamScore() == null, "CK = null");
        String statusLT101 = rLT101.isComplete() ? GradeManager.DU_DIEM : GradeManager.CHUA_DU_DIEM;
        assertTrue("D10 - LT101 trang thai CHUA_DU_DIEM", GradeManager.CHUA_DU_DIEM.equals(statusLT101), "Status = " + statusLT101);
        assertTrue("D10 - LT101 khong co diem tong ket", rLT101.getFinalScore() == null, "FinalScore = null");

        Transcript t = new Transcript(an, gm.getRecordsByStudent(an.getStudentId()));
        assertTrue("D10 - Du dung 1/3 mon", t.getCompletedCourses() == 1 && t.getTotalCourses() == 3, "1/3 mon");
        String formattedGPA = String.format(Locale.US, "%.2f", t.calculateWeightedGpa());
        assertTrue("D10 - GPA trung binh 7.00", "7.00".equals(formattedGPA), "GPA formatted: " + formattedGPA);
    }
}

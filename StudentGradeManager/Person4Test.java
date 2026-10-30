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

/**
 * Bộ kiểm thử tự động cho Người 4 — Nhập & cập nhật điểm.
 * Phụ trách các ca kiểm thử: D03, D04, D05, D10 và các quy tắc miền điểm [0, 10].
 * 
 * Chuẩn: JDK 8, NetBeans 17, kiểm tra tường minh các yêu cầu nghiệp vụ.
 */
public class Person4Test {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" BẮT ĐẦU KIỂM THỬ CHO NGƯỜI 4: NHẬP & CẬP NHẬT ĐIỂM (PRO192)");
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
        System.out.printf(Locale.US, " KẾT QUẢ KIỂM THỬ NGƯỜI 4: %d/%d PASS (%.1f%%)\n",
                passedTests, totalTests, (passedTests * 100.0 / totalTests));
        System.out.println("================================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> TẤT CẢ CÁC CA TEST CỦA NGƯỜI 4 ĐÃ ĐẠT YÊU CẦU 100%! <<<");
        } else {
            System.err.println(">>> CÓ CA TEST CHƯA ĐẠT! <<<");
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

    /**
     * Kiểm tra hàm validateScore(Double) độc lập:
     * Chấp nhận null, 0.0, 10.0; từ chối số âm, > 10, NaN, vô cực.
     */
    private static void testScoreValidation_DomainAndSpecialValues() {
        System.out.println("--- Test 1: Kiểm tra miền điểm [0.0; 10.0] và giá trị đặc biệt ---");

        // 1. null là điểm thiếu (hợp lệ)
        try {
            GradeManager.validateScore(null);
            assertTrue("validateScore(null)", true, "null được chấp nhận là điểm thiếu");
        } catch (Exception e) {
            assertTrue("validateScore(null)", false, "null không được ném lỗi: " + e.getMessage());
        }

        // 2. Biên dưới: 0.0 (hợp lệ)
        try {
            GradeManager.validateScore(0.0);
            assertTrue("validateScore(0.0)", true, "Điểm 0.0 là hợp lệ");
        } catch (Exception e) {
            assertTrue("validateScore(0.0)", false, "0.0 bị lỗi: " + e.getMessage());
        }

        // 3. Biên trên: 10.0 (hợp lệ)
        try {
            GradeManager.validateScore(10.0);
            assertTrue("validateScore(10.0)", true, "Điểm 10.0 là hợp lệ");
        } catch (Exception e) {
            assertTrue("validateScore(10.0)", false, "10.0 bị lỗi: " + e.getMessage());
        }

        // 4. Giá trị ngoài biên dưới: -0.01 (từ chối)
        boolean caughtNeg = false;
        try {
            GradeManager.validateScore(-0.01);
        } catch (IllegalArgumentException e) {
            caughtNeg = true;
        }
        assertTrue("validateScore(-0.01)", caughtNeg, "Từ chối số âm -0.01");

        // 5. Giá trị ngoài biên trên: 10.01 (từ chối)
        boolean caughtOver = false;
        try {
            GradeManager.validateScore(10.01);
        } catch (IllegalArgumentException e) {
            caughtOver = true;
        }
        assertTrue("validateScore(10.01)", caughtOver, "Từ chối số lớn hơn mười: 10.01");

        // 6. Giá trị đặc biệt: Double.NaN (từ chối)
        boolean caughtNaN = false;
        try {
            GradeManager.validateScore(Double.NaN);
        } catch (IllegalArgumentException e) {
            caughtNaN = true;
        }
        assertTrue("validateScore(NaN)", caughtNaN, "Từ chối Double.NaN");

        // 7. Giá trị đặc biệt: Dương vô cực (từ chối)
        boolean caughtPosInf = false;
        try {
            GradeManager.validateScore(Double.POSITIVE_INFINITY);
        } catch (IllegalArgumentException e) {
            caughtPosInf = true;
        }
        assertTrue("validateScore(+Infinity)", caughtPosInf, "Từ chối POSITIVE_INFINITY");

        // 8. Giá trị đặc biệt: Âm vô cực (từ chối)
        boolean caughtNegInf = false;
        try {
            GradeManager.validateScore(Double.NEGATIVE_INFINITY);
        } catch (IllegalArgumentException e) {
            caughtNegInf = true;
        }
        assertTrue("validateScore(-Infinity)", caughtNegInf, "Từ chối NEGATIVE_INFINITY");
    }

    /**
     * Kiểm tra hàm parseScore(String) độc lập:
     * Phân biệt chuỗi rỗng / thiếu với chuỗi số, chặn chuỗi sai kiểu.
     */
    private static void testScoreParsing_ValidAndInvalidStrings() {
        System.out.println("\n--- Test 2: Phân tích cú pháp chuỗi điểm số (parseScore) ---");

        // Điểm thiếu: null, rỗng, khoảng trắng, "-", "EMPTY", "null"
        assertTrue("parseScore(null) -> null", GradeManager.parseScore(null) == null, "Trả về null");
        assertTrue("parseScore(\"\") -> null", GradeManager.parseScore("") == null, "Chuỗi rỗng trả về null");
        assertTrue("parseScore(\"   \") -> null", GradeManager.parseScore("   ") == null, "Khoảng trắng trả về null");
        assertTrue("parseScore(\"-\") -> null", GradeManager.parseScore("-") == null, "Ký tự '-' trả về null");
        assertTrue("parseScore(\"EMPTY\") -> null", GradeManager.parseScore("EMPTY") == null, "'EMPTY' trả về null");
        assertTrue("parseScore(\"empty\") -> null", GradeManager.parseScore("empty") == null, "'empty' trả về null");
        assertTrue("parseScore(\"NULL\") -> null", GradeManager.parseScore("NULL") == null, "'NULL' trả về null");

        // Điểm hợp lệ: 0, 0.0, 10, 8.5, dùng dấu phẩy "8,5"
        assertTrue("parseScore(\"0\") -> 0.0", Double.valueOf(0.0).equals(GradeManager.parseScore("0")), "Số 0 là 0.0");
        assertTrue("parseScore(\"0.0\") -> 0.0", Double.valueOf(0.0).equals(GradeManager.parseScore("0.0")), "Số 0.0 là 0.0");
        assertTrue("parseScore(\"10\") -> 10.0", Double.valueOf(10.0).equals(GradeManager.parseScore("10")), "Số 10 là 10.0");
        assertTrue("parseScore(\"8.5\") -> 8.5", Double.valueOf(8.5).equals(GradeManager.parseScore("8.5")), "Số 8.5 là 8.5");
        assertTrue("parseScore(\"8,5\") -> 8.5 (dấu phẩy)", Double.valueOf(8.5).equals(GradeManager.parseScore("8,5")), "Hỗ trợ dấu phẩy 8,5 -> 8.5");

        // Chuỗi sai kiểu: "abc", "NaN", "Infinity", "-0.01", "10.01"
        assertParseThrows("abc", "Chuỗi chữ cái 'abc'");
        assertParseThrows("NaN", "Chuỗi đặc biệt 'NaN'");
        assertParseThrows("nan", "Chuỗi đặc biệt 'nan'");
        assertParseThrows("Infinity", "Chuỗi 'Infinity'");
        assertParseThrows("+Infinity", "Chuỗi '+Infinity'");
        assertParseThrows("-Infinity", "Chuỗi '-Infinity'");
        assertParseThrows("-0.01", "Số âm dạng chuỗi '-0.01'");
        assertParseThrows("10.01", "Số > 10 dạng chuỗi '10.01'");
    }

    private static void assertParseThrows(String input, String description) {
        boolean caught = false;
        try {
            GradeManager.parseScore(input);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("parseScore('" + input + "')", caught, "Từ chối " + description);
    }

    /**
     * Kiểm tra tính nguyên tử (Atomic Check-Before-Assign):
     * Nếu một trong hai điểm bị sai, bản ghi KHÔNG ĐƯỢC PHÉP thay đổi dở dang.
     */
    private static void testAtomicCheckBeforeAssign() {
        System.out.println("\n--- Test 3: Tính nguyên tử - Kiểm tra toàn bộ trước khi gán ---");

        GradeManager gm = new GradeManager();
        Student s = new Student("SE01", "Test Student");
        Course c = new TheoryCourse("LT101", "Lý thuyết 1", 3);

        // Ban đầu: QT = 8.0, CK = 6.0
        gm.createOrUpdateRecord(s, c, 8.0, 6.0);
        GradeRecord record = gm.findRecord("SE01", "LT101");
        assertTrue("Bản ghi ban đầu", record != null && record.getProcessScore() == 8.0 && record.getExamScore() == 6.0, "QT=8.0, CK=6.0");

        // Cố gắng cập nhật: QT mới = 9.0 (hợp lệ), CK mới = 12.0 (sai > 10)
        boolean caughtError = false;
        try {
            gm.createOrUpdateRecord(s, c, 9.0, 12.0);
        } catch (IllegalArgumentException e) {
            caughtError = true;
        }
        assertTrue("Bị từ chối khi CK sai", caughtError, "Ngoại lệ khi CK = 12.0");

        // Kiểm tra lại bản ghi: QT phải VẪN LÀ 8.0 (không bị đổi thành 9.0)
        assertTrue("QT giữ nguyên 8.0", record.getProcessScore() == 8.0, "Điểm QT cũ được bảo toàn");
        assertTrue("CK giữ nguyên 6.0", record.getExamScore() == 6.0, "Điểm CK cũ được bảo toàn");
    }

    /**
     * Đảm bảo không tạo bản ghi trùng cho cùng cặp SV-Môn.
     */
    private static void testNoDuplicateRecords() {
        System.out.println("\n--- Test 4: Không tạo bản ghi trùng lặp cho cùng cặp SV-Môn ---");

        GradeManager gm = new GradeManager();
        Student s = new Student("SE01", "Test Student");
        Course c = new TheoryCourse("LT101", "Lý thuyết 1", 3);

        gm.createOrUpdateRecord(s, c, 5.0, 5.0);
        gm.createOrUpdateRecord(s, c, 7.0, 8.0);
        gm.addOrUpdateGrade(s, c, 9.0, 10.0);

        assertTrue("Chỉ có 1 bản ghi duy nhất", gm.getRecordCount() == 1, "Số bản ghi = " + gm.getRecordCount());
        GradeRecord r = gm.findRecord("SE01", "LT101");
        assertTrue("Điểm cập nhật mới nhất", r.getProcessScore() == 9.0 && r.getExamScore() == 10.0, "QT=9.0, CK=10.0");
    }

    /**
     * Ca kiểm thử D03:
     * Dữ liệu mẫu: An có LT101 (QT 8, CK 6), TH101 (QT 8, CK 6), LT102 (QT 9, CK thiếu);
     * Bình chưa có kết quả nào.
     * Kỳ vọng: LT102 ra CHUA_DU_DIEM và không có điểm tổng kết;
     * Bình ra 0/0 môn, 0.0 điểm trung bình.
     */
    private static void testD03_MissingScoreStateAndEmptyStudent() {
        System.out.println("\n--- Test D03: Trạng thái CHUA_DU_DIEM & Sinh viên chưa có kết quả ---");

        GradeManager gm = new GradeManager();
        Student an = new Student("QE210001", "Nguyễn Văn An");
        Student binh = new Student("QE210002", "Trần Thị Bình");

        Course lt101 = new TheoryCourse("LT101", "Lập trình Java căn bản", 3);
        Course th101 = new PracticeCourse("TH101", "Thực hành lập trình Java", 2);
        Course lt102 = new TheoryCourse("LT102", "Cấu trúc dữ liệu và giải thuật", 2);

        gm.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, lt102, 9.0, null); // CK thiếu

        // Kiểm tra An - LT102
        GradeRecord rLT102 = gm.findRecord("QE210001", "LT102");
        assertTrue("D03 - An LT102 chưa hoàn thành", !rLT102.hasCompleteScores(), "hasCompleteScores() == false");
        String statusLT102 = rLT102.hasCompleteScores() ? GradeManager.DU_DIEM : GradeManager.CHUA_DU_DIEM;
        assertTrue("D03 - An LT102 trạng thái CHUA_DU_DIEM", 
                GradeManager.CHUA_DU_DIEM.equals(statusLT102), "Status = " + statusLT102);
        assertTrue("D03 - An LT102 không có điểm tổng kết", rLT102.getFinalGrade() == null, "getFinalGrade() == null");

        // Kiểm tra Bình (chưa có điểm)
        Transcript transcriptBinh = new Transcript(binh, gm.getRecordsByStudent(binh.getStudentId()));
        assertTrue("D03 - Bình 0/0 môn", transcriptBinh.getCompletedCourseCount() == 0 && transcriptBinh.getTotalCourseCount() == 0, "0/0 môn");
        assertTrue("D03 - Bình trung bình 0.0", transcriptBinh.calculateAverage() == 0.0, "average == 0.0");
    }

    /**
     * Ca kiểm thử D04:
     * Đặt LT102 của An thành QT 0, CK 10:
     * Tổng kết LT102 = 0*0.3 + 10*0.7 = 7.00;
     * GPA = 47.8 / 7 = 6.83, đủ 3/3 môn.
     */
    private static void testD04_UpdateLT102_ZeroAndTen() {
        System.out.println("\n--- Test D04: Cập nhật LT102 QT 0, CK 10 -> Tổng kết 7.00, GPA 6.83 ---");

        GradeManager gm = new GradeManager();
        Student an = new Student("QE210001", "Nguyễn Văn An");
        Course lt101 = new TheoryCourse("LT101", "Lập trình Java căn bản", 3);
        Course th101 = new PracticeCourse("TH101", "Thực hành lập trình Java", 2);
        Course lt102 = new TheoryCourse("LT102", "Cấu trúc dữ liệu và giải thuật", 2);

        gm.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, lt102, 9.0, null);

        // Cập nhật LT102: QT = 0.0, CK = 10.0
        gm.createOrUpdateRecord(an, lt102, 0.0, 10.0);

        GradeRecord rLT102 = gm.findRecord("QE210001", "LT102");
        assertTrue("D04 - LT102 đủ điểm", rLT102.hasCompleteScores(), "hasCompleteScores() == true");
        assertTrue("D04 - QT là 0.0 hợp lệ", rLT102.getProcessScore() == 0.0, "QT = 0.0");
        assertTrue("D04 - CK là 10.0", rLT102.getExamScore() == 10.0, "CK = 10.0");

        Double finalScoreLT102 = rLT102.getFinalGrade();
        boolean finalOk = finalScoreLT102 != null && Math.abs(finalScoreLT102 - 7.00) < 0.0001;
        assertTrue("D04 - Tổng kết LT102 là 7.00", finalOk, String.format(Locale.US, "Điểm TK = %.2f", finalScoreLT102));

        // Kiểm tra GPA của An: 47.8 / 7 = 6.82857 -> hiển thị 6.83
        Transcript transcriptAn = new Transcript(an, gm.getRecordsByStudent(an.getStudentId()));
        assertTrue("D04 - Đủ 3/3 môn", transcriptAn.getCompletedCourseCount() == 3 && transcriptAn.getTotalCourseCount() == 3, "3/3 môn");

        double rawGPA = transcriptAn.calculateAverage();
        boolean gpaRawOk = Math.abs(rawGPA - (47.8 / 7.0)) < 0.0001;
        assertTrue("D04 - GPA thô = 47.8/7", gpaRawOk, String.format(Locale.US, "Raw GPA = %.5f", rawGPA));
        String formattedGPA = String.format(Locale.US, "%.2f", rawGPA);
        assertTrue("D04 - GPA làm tròn hiển thị 6.83", "6.83".equals(formattedGPA), "Formatted GPA: " + formattedGPA);
    }

    /**
     * Ca kiểm thử D05:
     * Thử -0.01; 10.01; chuỗi NaN; mã SV hoặc môn không tồn tại.
     * Từng thao tác bị từ chối, dữ liệu mẫu giữ nguyên, trung bình An vẫn 6.76.
     */
    private static void testD05_RejectionOfInvalidInputPreservesSampleData() {
        System.out.println("\n--- Test D05: Từ chối các thao tác sai phạm, giữ nguyên dữ liệu mẫu & GPA 6.76 ---");

        GradeManager gm = new GradeManager();
        Student an = new Student("QE210001", "Nguyễn Văn An");
        Course lt101 = new TheoryCourse("LT101", "Lập trình Java căn bản", 3);
        Course th101 = new PracticeCourse("TH101", "Thực hành lập trình Java", 2);
        Course lt102 = new TheoryCourse("LT102", "Cấu trúc dữ liệu và giải thuật", 2);

        // Nạp dữ liệu mẫu ban đầu
        gm.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, lt102, 9.0, null);

        // 1. Thử -0.01
        boolean c1 = false;
        try {
            Double val = GradeManager.parseScore("-0.01");
            gm.createOrUpdateRecord(an, lt101, val, 6.0);
        } catch (IllegalArgumentException e) {
            c1 = true;
        }
        assertTrue("D05 - Từ chối -0.01", c1, "Đã bắt lỗi -0.01");

        // 2. Thử 10.01
        boolean c2 = false;
        try {
            Double val = GradeManager.parseScore("10.01");
            gm.createOrUpdateRecord(an, lt101, 8.0, val);
        } catch (IllegalArgumentException e) {
            c2 = true;
        }
        assertTrue("D05 - Từ chối 10.01", c2, "Đã bắt lỗi 10.01");

        // 3. Thử chuỗi "NaN"
        boolean c3 = false;
        try {
            Double val = GradeManager.parseScore("NaN");
            gm.createOrUpdateRecord(an, lt101, val, 6.0);
        } catch (IllegalArgumentException e) {
            c3 = true;
        }
        assertTrue("D05 - Từ chối chuỗi NaN", c3, "Đã bắt lỗi 'NaN'");

        // 4. Thử sinh viên không tồn tại (null)
        boolean c4 = false;
        try {
            gm.createOrUpdateRecord(null, lt101, 8.0, 8.0);
        } catch (IllegalArgumentException e) {
            c4 = true;
        }
        assertTrue("D05 - Từ chối SV null", c4, "Không cho phép sinh viên null");

        // 5. Thử môn học không tồn tại (null)
        boolean c5 = false;
        try {
            gm.createOrUpdateRecord(an, null, 8.0, 8.0);
        } catch (IllegalArgumentException e) {
            c5 = true;
        }
        assertTrue("D05 - Từ chối Course null", c5, "Không cho phép môn học null");

        // Kiểm tra dữ liệu mẫu được giữ nguyên hoàn toàn
        GradeRecord r1 = gm.findRecord("QE210001", "LT101");
        GradeRecord r2 = gm.findRecord("QE210001", "TH101");
        GradeRecord r3 = gm.findRecord("QE210001", "LT102");

        assertTrue("D05 - LT101 giữ nguyên 8.0, 6.0", r1.getProcessScore() == 8.0 && r1.getExamScore() == 6.0, "LT101 không đổi");
        assertTrue("D05 - TH101 giữ nguyên 8.0, 6.0", r2.getProcessScore() == 8.0 && r2.getExamScore() == 6.0, "TH101 không đổi");
        assertTrue("D05 - LT102 giữ nguyên 9.0, null", r3.getProcessScore() == 9.0 && r3.getExamScore() == null, "LT102 không đổi");

        // GPA của An vẫn là 6.76 ((6.6*3 + 7.0*2) / 5 = 33.8 / 5 = 6.76)
        Transcript t = new Transcript(an, gm.getRecordsByStudent(an.getStudentId()));
        String formattedGPA = String.format(Locale.US, "%.2f", t.calculateAverage());
        assertTrue("D05 - GPA An giữ nguyên 6.76", "6.76".equals(formattedGPA), "GPA formatted: " + formattedGPA);
        assertTrue("D05 - Đủ 2/3 môn", t.getCompletedCourseCount() == 2 && t.getTotalCourseCount() == 3, "2/3 môn");
    }

    /**
     * Ca kiểm thử D10:
     * Đặt LT101 của An thành QT 0, CK thiếu (null).
     * Kiểm tra: QT vẫn là 0.0, CK vẫn là thiếu; chỉ TH101 đủ điểm,
     * GPA = 7.00, đủ 1/3 môn.
     */
    private static void testD10_ZeroScoreVsMissingScore() {
        System.out.println("\n--- Test D10: Điểm 0 phân biệt hoàn toàn với điểm thiếu (An LT101: QT=0, CK=null) ---");

        GradeManager gm = new GradeManager();
        Student an = new Student("QE210001", "Nguyễn Văn An");
        Course lt101 = new TheoryCourse("LT101", "Lập trình Java căn bản", 3);
        Course th101 = new PracticeCourse("TH101", "Thực hành lập trình Java", 2);
        Course lt102 = new TheoryCourse("LT102", "Cấu trúc dữ liệu và giải thuật", 2);

        gm.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gm.createOrUpdateRecord(an, lt102, 9.0, null);

        // Đặt LT101 thành: QT = 0.0, CK = null (thiếu)
        gm.createOrUpdateRecord(an, lt101, 0.0, null);

        GradeRecord rLT101 = gm.findRecord("QE210001", "LT101");
        assertTrue("D10 - QT của LT101 là 0.0", rLT101.getProcessScore() != null && rLT101.getProcessScore() == 0.0, "QT = 0.0 (không null)");
        assertTrue("D10 - CK của LT101 là thiếu (null)", rLT101.getExamScore() == null, "CK = null");
        String statusLT101 = rLT101.hasCompleteScores() ? GradeManager.DU_DIEM : GradeManager.CHUA_DU_DIEM;
        assertTrue("D10 - LT101 trạng thái CHUA_DU_DIEM", GradeManager.CHUA_DU_DIEM.equals(statusLT101), "Status = " + statusLT101);
        assertTrue("D10 - LT101 không có điểm tổng kết", rLT101.getFinalGrade() == null, "FinalScore = null");

        // Bảng điểm An: lúc này chỉ có TH101 đủ điểm (QT 8.0, CK 6.0 -> TK 7.00)
        Transcript t = new Transcript(an, gm.getRecordsByStudent(an.getStudentId()));
        assertTrue("D10 - Đủ đúng 1/3 môn", t.getCompletedCourseCount() == 1 && t.getTotalCourseCount() == 3, "1/3 môn");
        String formattedGPA = String.format(Locale.US, "%.2f", t.calculateAverage());
        assertTrue("D10 - GPA trung bình 7.00", "7.00".equals(formattedGPA), "GPA formatted: " + formattedGPA);
    }
}

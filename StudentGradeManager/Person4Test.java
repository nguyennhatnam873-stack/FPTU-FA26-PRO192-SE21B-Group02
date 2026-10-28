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
        assertTrue("validateScore(10.01)", caughtOver, "Từ chối số lớn hơn 10: 10.01");

        // 6. Double.NaN (từ chối)
        boolean caughtNaN = false;
        try {
            GradeManager.validateScore(Double.NaN);
        } catch (IllegalArgumentException e) {
            caughtNaN = true;
        }
        assertTrue("validateScore(NaN)", caughtNaN, "Từ chối giá trị Double.NaN");

        // 7. Double.POSITIVE_INFINITY (từ chối)
        boolean caughtInf = false;
        try {
            GradeManager.validateScore(Double.POSITIVE_INFINITY);
        } catch (IllegalArgumentException e) {
            caughtInf = true;
        }
        assertTrue("validateScore(Infinity)", caughtInf, "Từ chối giá trị vô cực dương");

        // 8. Double.NEGATIVE_INFINITY (từ chối)
        boolean caughtNegInf = false;
        try {
            GradeManager.validateScore(Double.NEGATIVE_INFINITY);
        } catch (IllegalArgumentException e) {
            caughtNegInf = true;
        }
        assertTrue("validateScore(-Infinity)", caughtNegInf, "Từ chối giá trị vô cực âm");
    }

    /**
     * Kiểm tra hàm parseScore(String):
     * Chuỗi rỗng, "-", "EMPTY" -> null; "0" -> 0.0; "6,83" -> 6.83; "abc", "NaN" -> Exception.
     */
    private static void testScoreParsing_ValidAndInvalidStrings() {
        System.out.println("\n--- Test 2: Parse chuỗi điểm và phân biệt điểm 0 với điểm thiếu ---");

        assertTrue("parseScore(null) -> null", GradeManager.parseScore(null) == null, "Trả về null");
        assertTrue("parseScore(\"\") -> null", GradeManager.parseScore("") == null, "Chuỗi rỗng trả về null");
        assertTrue("parseScore(\"  \") -> null", GradeManager.parseScore("   ") == null, "Khoảng trắng trả về null");
        assertTrue("parseScore(\"-\") -> null", GradeManager.parseScore("-") == null, "Dấu '-' trả về null (thiếu)");
        assertTrue("parseScore(\"EMPTY\") -> null", GradeManager.parseScore("EMPTY") == null, "'EMPTY' trả về null");
        assertTrue("parseScore(\"null\") -> null", GradeManager.parseScore("null") == null, "'null' trả về null");

        // Điểm 0 là hợp lệ và KHÔNG ĐƯỢC biến thành null
        Double zero = GradeManager.parseScore("0");
        assertTrue("parseScore(\"0\") -> 0.0", zero != null && zero == 0.0, "Điểm 0 hợp lệ, khác null");

        Double zeroFloat = GradeManager.parseScore("0.0");
        assertTrue("parseScore(\"0.0\") -> 0.0", zeroFloat != null && zeroFloat == 0.0, "Điểm 0.0 hợp lệ");

        // Hỗ trợ cả dấu phẩy lẫn dấu chấm
        Double valComma = GradeManager.parseScore("6,83");
        assertTrue("parseScore(\"6,83\")", valComma != null && Math.abs(valComma - 6.83) < 0.0001, "Parse dấu phẩy thành công");

        Double valDot = GradeManager.parseScore("6.83");
        assertTrue("parseScore(\"6.83\")", valDot != null && Math.abs(valDot - 6.83) < 0.0001, "Parse dấu chấm thành công");

        // Từ chối chuỗi chữ sai kiểu
        boolean caughtAbc = false;
        try {
            GradeManager.parseScore("abc");
        } catch (IllegalArgumentException e) {
            caughtAbc = true;
        }
        assertTrue("parseScore(\"abc\")", caughtAbc, "Ném ngoại lệ rõ ràng khi chuỗi không phải số");

        // Từ chối chuỗi "NaN"
        boolean caughtStrNaN = false;
        try {
            GradeManager.parseScore("NaN");
        } catch (IllegalArgumentException e) {
            caughtStrNaN = true;
        }
        assertTrue("parseScore(\"NaN\")", caughtStrNaN, "Ném ngoại lệ khi nhập chuỗi 'NaN'");

        // Từ chối chuỗi "Infinity"
        boolean caughtStrInf = false;
        try {
            GradeManager.parseScore("Infinity");
        } catch (IllegalArgumentException e) {
            caughtStrInf = true;
        }
        assertTrue("parseScore(\"Infinity\")", caughtStrInf, "Ném ngoại lệ khi nhập chuỗi 'Infinity'");
    }

    /**
     * Kiểm tra tính nguyên tử (Atomic Check-Before-Assign):
     * Nếu 1 trong 2 điểm sai, không điểm nào bị gán dở dang vào bản ghi cũ.
     */
    private static void testAtomicCheckBeforeAssign() {
        System.out.println("\n--- Test 3: Tính nguyên tử (Kiểm tra tất cả điều kiện TRƯỚC khi gán) ---");

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
     * Bình ra CHUA_CO_DIEM_TRUNG_BINH, 0/0 môn, 0/0 tín chỉ.
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
        assertTrue("D03 - An LT102 chưa hoàn thành", !rLT102.isComplete(), "isComplete() == false");
        assertTrue("D03 - An LT102 trạng thái CHUA_DU_DIEM", 
                GradeManager.CHUA_DU_DIEM.equals(rLT102.getStatus()), "Status = " + rLT102.getStatus());
        assertTrue("D03 - An LT102 không có điểm tổng kết", rLT102.getFinalScore() == null, "getFinalScore() == null");

        // Kiểm tra Bình (chưa có điểm)
        Transcript transcriptBinh = new Transcript(binh, gm.getRecordsByStudent(binh.getStudentId()));
        assertTrue("D03 - Bình 0/0 môn", transcriptBinh.getCompletedCourses() == 0 && transcriptBinh.getTotalCourses() == 0, "0/0 môn");
        assertTrue("D03 - Bình 0/0 tín chỉ", transcriptBinh.getCompletedCredits() == 0 && transcriptBinh.getTotalCredits() == 0, "0/0 tín chỉ");
        assertTrue("D03 - Bình không có điểm TB thô", transcriptBinh.calculateWeightedGPA() == null, "raw GPA == null");
        assertTrue("D03 - Bình hiển thị CHUA_CO_DIEM_TRUNG_BINH", 
                Transcript.CHUA_CO_DIEM_TRUNG_BINH.equals(transcriptBinh.getFormattedGPA()), 
                "Formatted GPA: " + transcriptBinh.getFormattedGPA());
    }

    /**
     * Ca kiểm thử D04:
     * Đặt LT102 của An thành QT 0, CK 10:
     * Tổng kết LT102 = 0*0.3 + 10*0.7 = 7.00;
     * GPA = 47.8 / 7 = 6.83, đủ 3/3 môn, 7/7 tín chỉ.
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
        assertTrue("D04 - LT102 đủ điểm", rLT102.isComplete(), "isComplete() == true");
        assertTrue("D04 - QT là 0.0 hợp lệ", rLT102.getProcessScore() == 0.0, "QT = 0.0");
        assertTrue("D04 - CK là 10.0", rLT102.getExamScore() == 10.0, "CK = 10.0");

        Double finalScoreLT102 = rLT102.getFinalScore();
        boolean finalOk = finalScoreLT102 != null && Math.abs(finalScoreLT102 - 7.00) < 0.0001;
        assertTrue("D04 - Tổng kết LT102 là 7.00", finalOk, String.format(Locale.US, "Điểm TK = %.2f", finalScoreLT102));

        // Kiểm tra GPA của An: 47.8 / 7 = 6.82857 -> hiển thị 6.83
        Transcript transcriptAn = new Transcript(an, gm.getRecordsByStudent(an.getStudentId()));
        assertTrue("D04 - Đủ 3/3 môn", transcriptAn.getCompletedCourses() == 3 && transcriptAn.getTotalCourses() == 3, "3/3 môn");
        assertTrue("D04 - Đủ 7/7 tín chỉ", transcriptAn.getCompletedCredits() == 7 && transcriptAn.getTotalCredits() == 7, "7/7 tín chỉ");

        Double rawGPA = transcriptAn.calculateWeightedGPA();
        boolean gpaRawOk = rawGPA != null && Math.abs(rawGPA - (47.8 / 7.0)) < 0.0001;
        assertTrue("D04 - GPA thô = 47.8/7", gpaRawOk, String.format(Locale.US, "Raw GPA = %.5f", rawGPA));
        assertTrue("D04 - GPA làm tròn hiển thị 6.83", "6.83".equals(transcriptAn.getFormattedGPA()), 
                "Formatted GPA: " + transcriptAn.getFormattedGPA());
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
        assertTrue("D05 - GPA An giữ nguyên 6.76", "6.76".equals(t.getFormattedGPA()), "GPA formatted: " + t.getFormattedGPA());
        assertTrue("D05 - Đủ 2/3 môn", t.getCompletedCourses() == 2 && t.getTotalCourses() == 3, "2/3 môn");
        assertTrue("D05 - Đủ 5/7 tín chỉ", t.getCompletedCredits() == 5 && t.getTotalCredits() == 7, "5/7 tín chỉ");
    }

    /**
     * Ca kiểm thử D10:
     * Đặt LT101 của An thành QT 0, CK thiếu (null).
     * Kiểm tra: QT vẫn là 0.0, CK vẫn là thiếu; chỉ TH101 đủ điểm,
     * GPA = 7.00, đủ 1/3 môn, 2/7 tín chỉ.
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
        assertTrue("D10 - LT101 trạng thái CHUA_DU_DIEM", GradeManager.CHUA_DU_DIEM.equals(rLT101.getStatus()), "Status = " + rLT101.getStatus());
        assertTrue("D10 - LT101 không có điểm tổng kết", rLT101.getFinalScore() == null, "FinalScore = null");

        // Bảng điểm An: lúc này chỉ có TH101 đủ điểm (QT 8.0, CK 6.0 -> TK 7.00)
        Transcript t = new Transcript(an, gm.getRecordsByStudent(an.getStudentId()));
        assertTrue("D10 - Đủ đúng 1/3 môn", t.getCompletedCourses() == 1 && t.getTotalCourses() == 3, "1/3 môn");
        assertTrue("D10 - Đủ đúng 2/7 tín chỉ", t.getCompletedCredits() == 2 && t.getTotalCredits() == 7, "2/7 tín chỉ");
        assertTrue("D10 - GPA trung bình 7.00", "7.00".equals(t.getFormattedGPA()), "GPA formatted: " + t.getFormattedGPA());
    }
}


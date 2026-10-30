package studentgrademanager;

import java.util.ArrayList;
import java.util.List;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.PracticeCourse;
import studentgrademanager.models.Student;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.models.Transcript;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;
import studentgrademanager.services.TranscriptService;
import studentgrademanager.storage.FileDataStore;

/**
 * Bộ kiểm thử tự động cho Người 1 — Trưởng nhóm / Bảng điểm.
 * Phụ trách các ca kiểm thử: D02, D04, D06, D07, và các ca liên đới (D03, Immutability, Boundary).
 */
public class Person1Test {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" BẮT ĐẦU KIỂM THỬ CHO NGƯỜI 1: TRƯỞNG NHÓM / BẢNG ĐIỂM");
        System.out.println("================================================================\n");

        testD02_AnSampleDataTranscript();
        testD03_BinhEmptyTranscriptAndNoGpa();
        testD03_AllCoursesIncomplete();
        testD04_AnCompletesLT102WithZeroScore();
        testD06_AnUpdateFinalExamAndPreventDuplicates();
        testD07_BinhPrecisionAndRawCalculation();
        testEncapsulationAndImmutability();
        testTranscriptServiceIntegration();

        System.out.println("\n================================================================");
        System.out.printf(" KẾT QUẢ KIỂM THỬ NGƯỜI 1: %d/%d PASS (%.1f%%)\n", 
                passedTests, totalTests, (passedTests * 100.0 / totalTests));
        System.out.println("================================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> TẤT CẢ CÁC CA TEST CỦA NGƯỜI 1 ĐÃ ĐẠT YÊU CẦU 100%! <<<");
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
     * Ca D02: Xem trung bình của An theo dữ liệu mẫu
     * - An có 3 môn:
     *   + LT101 (3 tín chỉ, Lý thuyết): QT = 8, CK = 6 -> Điểm tổng kết: 6.60
     *   + TH101 (2 tín chỉ, Thực hành): QT = 8, CK = 6 -> Điểm tổng kết: 7.00
     *   + LT102 (2 tín chỉ, Lý thuyết): QT = 9, CK = null -> Chưa đủ điểm (CHUA_DU_DIEM)
     * - Kỳ vọng:
     *   + Đủ 2/3 môn
     *   + Tín chỉ tích lũy: 5/7
     *   + Điểm trung bình tạm tính: (6.6 * 3 + 7.0 * 2) / 5 = 33.8 / 5 = 6.76
     *   + Hiển thị GPA: "6.76"
     */
    private static void testD02_AnSampleDataTranscript() {
        System.out.println("--- Kiểm thử Ca D02: Bảng điểm mẫu & Điểm TB tạm tính của An ---");

        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        TranscriptService ts = new TranscriptService(sm, gm);
        Transcript transcriptAn = ts.getTranscript("QE210001");

        assertTrue("D02 - Tìm thấy bảng điểm An", transcriptAn != null, "Transcript An không null");
        assertTrue("D02 - Đúng đối tượng sinh viên An", "Nguyễn An".equals(transcriptAn.getStudent().getFullName()), 
                "Họ tên: " + transcriptAn.getStudent().getFullName());

        List<GradeRecord> records = transcriptAn.getRecords();
        assertTrue("D02 - Tổng số môn ghi nhận là 3", records.size() == 3, "Số môn đăng ký: " + records.size());

        List<GradeRecord> completeRecords = transcriptAn.getCompleteRecords();
        assertTrue("D02 - Số môn đủ điểm là 2", completeRecords.size() == 2, "Số môn hoàn thành: " + completeRecords.size());

        assertTrue("D02 - Tổng tín chỉ ghi nhận là 7", transcriptAn.getTotalEnrolledCredits() == 7, 
                "Tổng tín chỉ đăng ký: " + transcriptAn.getTotalEnrolledCredits());
        assertTrue("D02 - Tín chỉ tích lũy đủ điểm là 5", transcriptAn.getCompletedCredits() == 5, 
                "Tín chỉ tích lũy: " + transcriptAn.getCompletedCredits());

        assertTrue("D02 - Có điểm trung bình", transcriptAn.hasAverageScore(), "hasAverageScore() == true");

        Double rawGpa = transcriptAn.calculateRawWeightedGpa();
        boolean gpaMatch = (rawGpa != null) && (Math.abs(rawGpa - 6.76) < 0.0001);
        assertTrue("D02 - Điểm trung bình thô chính xác 6.76", gpaMatch, 
                String.format("Kỳ vọng: 6.7600 | Thực tế: %.4f", rawGpa));

        String formattedGpa = transcriptAn.getFormattedGpa();
        assertTrue("D02 - Định dạng hiển thị GPA là '6.76'", "6.76".equals(formattedGpa), 
                "Formatted GPA: " + formattedGpa);

        // Kiểm tra chi tiết từng môn
        GradeRecord rLT101 = gm.findRecord("QE210001", "LT101");
        GradeRecord rTH101 = gm.findRecord("QE210001", "TH101");
        GradeRecord rLT102 = gm.findRecord("QE210001", "LT102");

        assertTrue("D02 - LT101 đủ điểm (6.60)", rLT101.isComplete() && Math.abs(rLT101.getFinalScore() - 6.60) < 0.0001,
                "LT101 TK: " + rLT101.getFinalScore());
        assertTrue("D02 - TH101 đủ điểm (7.00)", rTH101.isComplete() && Math.abs(rTH101.getFinalScore() - 7.00) < 0.0001,
                "TH101 TK: " + rTH101.getFinalScore());
        assertTrue("D02 - LT102 chưa đủ điểm (null)", !rLT102.isComplete() && rLT102.getFinalScore() == null,
                "LT102 Trạng thái: " + rLT102.getStatus() + ", TK: " + rLT102.getFinalScore());
    }

    /**
     * Ca D03 (Phần Bảng điểm): Xem bảng điểm của Bình (chưa có điểm)
     * - Bình (QE210002) chưa có kết quả nào.
     * - Kỳ vọng:
     *   + 0/0 môn, 0/0 tín chỉ
     *   + hasAverageScore() == false
     *   + calculateRawWeightedGpa() == null
     *   + getFormattedGpa() == "CHUA_CO_DIEM_TRUNG_BINH"
     *   + Không chia cho 0, không hiển thị trung bình 0.00
     */
    private static void testD03_BinhEmptyTranscriptAndNoGpa() {
        System.out.println("--- Kiểm thử Ca D03: Bảng điểm sinh viên chưa có kết quả (Bình) ---");

        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        TranscriptService ts = new TranscriptService(sm, gm);
        Transcript transcriptBinh = ts.getTranscript("QE210002");

        assertTrue("D03 - Tìm thấy bảng điểm Bình", transcriptBinh != null, "Transcript Bình tồn tại");
        assertTrue("D03 - Tổng số môn đăng ký là 0", transcriptBinh.getTotalCourses() == 0, 
                "Số môn: " + transcriptBinh.getTotalCourses());
        assertTrue("D03 - Số môn hoàn thành là 0", transcriptBinh.getCompletedCourses() == 0, 
                "Môn hoàn thành: " + transcriptBinh.getCompletedCourses());
        assertTrue("D03 - Tổng tín chỉ là 0", transcriptBinh.getTotalCredits() == 0, 
                "Tổng TC: " + transcriptBinh.getTotalCredits());
        assertTrue("D03 - Tín chỉ tích lũy là 0", transcriptBinh.getCompletedCredits() == 0, 
                "Tín chỉ tích lũy: " + transcriptBinh.getCompletedCredits());

        assertTrue("D03 - hasAverageScore là false", !transcriptBinh.hasAverageScore(), "Không có điểm TB");
        assertTrue("D03 - calculateRawWeightedGpa trả về null", transcriptBinh.calculateRawWeightedGpa() == null, 
                "Raw GPA: null");
        assertTrue("D03 - Hiển thị CHUA_CO_DIEM_TRUNG_BINH", 
                Transcript.CHUA_CO_DIEM_TB.equals(transcriptBinh.getFormattedGpa()), 
                "Formatted GPA: " + transcriptBinh.getFormattedGpa());
    }

    /**
     * Ca D03 (Bổ sung): Sinh viên có đăng ký môn nhưng tất cả các môn đều thiếu điểm (CHUA_DU_DIEM).
     */
    private static void testD03_AllCoursesIncomplete() {
        System.out.println("--- Kiểm thử Ca D03 (Bổ sung): Sinh viên có môn nhưng tất cả đều chưa đủ điểm ---");

        Student s = new Student("QE210999", "Lê Văn Chưa Điểm");
        Course c1 = new TheoryCourse("CS101", "CS Khái luận", 3);
        Course c2 = new PracticeCourse("PR101", "Lập trình C", 2);

        GradeRecord r1 = new GradeRecord(s, c1, 8.0, null); // Thiếu cuối kỳ
        GradeRecord r2 = new GradeRecord(s, c2, null, 7.5); // Thiếu quá trình

        List<GradeRecord> recs = new ArrayList<GradeRecord>();
        recs.add(r1);
        recs.add(r2);

        Transcript t = new Transcript(s, recs);

        assertTrue("D03 bổ sung - Đăng ký 2 môn", t.getTotalCourses() == 2, "Số môn: " + t.getTotalCourses());
        assertTrue("D03 bổ sung - Đăng ký 5 tín chỉ", t.getTotalEnrolledCredits() == 5, "Tổng TC: " + t.getTotalEnrolledCredits());
        assertTrue("D03 bổ sung - Hoàn thành 0 môn", t.getCompletedCourses() == 0, "Môn đủ điểm: 0");
        assertTrue("D03 bổ sung - Tích lũy 0 tín chỉ", t.getCompletedCredits() == 0, "TC tích lũy: 0");
        assertTrue("D03 bổ sung - hasAverageScore là false", !t.hasAverageScore(), "hasAverageScore: false");
        assertTrue("D03 bổ sung - GPA thô là null", t.calculateRawWeightedGpa() == null, "Raw GPA: null");
        assertTrue("D03 bổ sung - Hiển thị CHUA_CO_DIEM_TRUNG_BINH", 
                Transcript.NO_GPA_MESSAGE.equals(t.getFormattedGpa()), "Formatted: " + t.getFormattedGpa());
    }

    /**
     * Ca D04: Đặt LT102 của An thành quá trình 0, cuối kỳ 10
     * - LT102: Lý thuyết 2 tín chỉ: 0.3 * 0.0 + 0.7 * 10.0 = 7.00
     * - Môn đủ điểm, được đưa vào tính trung bình:
     *   Tử số = (6.6 * 3) + (7.0 * 2) + (7.0 * 2) = 19.8 + 14.0 + 14.0 = 47.8
     *   Mẫu số = 3 + 2 + 2 = 7 tín chỉ
     *   GPA thô = 47.8 / 7 = 6.8285714...
     *   Làm tròn HALF_UP 2 chữ số thập phân: "6.83"
     *   Đủ 3/3 môn, 7/7 tín chỉ.
     */
    private static void testD04_AnCompletesLT102WithZeroScore() {
        System.out.println("--- Kiểm thử Ca D04: An hoàn thành LT102 (QT=0, CK=10) -> GPA 6.83 ---");

        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        Student an = sm.findStudent("QE210001");
        Course lt102 = cm.findCourse("LT102");

        // Đặt QT=0.0, CK=10.0
        gm.createOrUpdateRecord(an, lt102, 0.0, 10.0);

        TranscriptService ts = new TranscriptService(sm, gm);
        Transcript transcriptAn = ts.getTranscript("QE210001");

        GradeRecord rLT102 = gm.findRecord("QE210001", "LT102");
        assertTrue("D04 - LT102 có QT = 0.0", rLT102.getProcessScore() != null && rLT102.getProcessScore() == 0.0, 
                "QT: " + rLT102.getProcessScore());
        assertTrue("D04 - LT102 có CK = 10.0", rLT102.getExamScore() != null && rLT102.getExamScore() == 10.0, 
                "CK: " + rLT102.getExamScore());
        assertTrue("D04 - LT102 điểm tổng kết là 7.00", Math.abs(rLT102.getFinalScore() - 7.00) < 0.0001, 
                "LT102 TK: " + rLT102.getFinalScore());
        assertTrue("D04 - LT102 ở trạng thái DU_DIEM", rLT102.isComplete(), "Trạng thái: " + rLT102.getStatus());

        assertTrue("D04 - Đủ 3/3 môn", transcriptAn.getCompletedCourses() == 3 && transcriptAn.getTotalCourses() == 3, 
                String.format("Môn hoàn thành: %d/%d", transcriptAn.getCompletedCourses(), transcriptAn.getTotalCourses()));
        assertTrue("D04 - Đủ 7/7 tín chỉ", transcriptAn.getCompletedCredits() == 7 && transcriptAn.getTotalEnrolledCredits() == 7, 
                String.format("Tín chỉ tích lũy: %d/%d", transcriptAn.getCompletedCredits(), transcriptAn.getTotalEnrolledCredits()));

        Double rawGpa = transcriptAn.calculateRawWeightedGpa();
        double expectedRaw = 47.8 / 7.0;
        assertTrue("D04 - GPA thô = 47.8 / 7 (~6.8286)", Math.abs(rawGpa - expectedRaw) < 0.0001, 
                String.format("Kỳ vọng: %.6f | Thực tế: %.6f", expectedRaw, rawGpa));

        String formattedGpa = transcriptAn.getFormattedGpa();
        assertTrue("D04 - Làm tròn hiển thị thành '6.83'", "6.83".equals(formattedGpa), 
                "Formatted GPA: " + formattedGpa);
    }

    /**
     * Ca D06: Thêm lại cặp An–LT101, cập nhật cuối kỳ thành 8.0
     * - Chống tạo dòng trùng (vẫn 3 môn)
     * - Sau cập nhật:
     *   + LT101: QT = 8.0, CK = 8.0 -> Tổng kết: 8.00 (3 tín chỉ)
     *   + TH101: 7.00 (2 tín chỉ)
     *   + LT102: Chưa đủ điểm (2 tín chỉ)
     *   + Điểm trung bình tạm tính: (8.00 * 3 + 7.00 * 2) / 5 = 38.0 / 5 = 7.60
     *   + Đủ 2/3 môn, 5/7 tín chỉ
     */
    private static void testD06_AnUpdateFinalExamAndPreventDuplicates() {
        System.out.println("--- Kiểm thử Ca D06: Cập nhật CK LT101 thành 8.0 -> GPA tạm tính 7.60 ---");

        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        Student an = sm.findStudent("QE210001");
        Course lt101 = cm.findCourse("LT101");

        // Nhập lại cặp An - LT101 với CK = 8.0
        gm.createOrUpdateRecord(an, lt101, 8.0, 8.0);

        TranscriptService ts = new TranscriptService(sm, gm);
        Transcript transcriptAn = ts.getTranscript("QE210001");

        assertTrue("D06 - Không tạo dòng trùng, vẫn có 3 môn", transcriptAn.getTotalCourses() == 3, 
                "Số môn của An: " + transcriptAn.getTotalCourses());

        GradeRecord rLT101 = gm.findRecord("QE210001", "LT101");
        assertTrue("D06 - Điểm tổng kết LT101 là 8.00", Math.abs(rLT101.getFinalScore() - 8.00) < 0.0001, 
                "LT101 TK mới: " + rLT101.getFinalScore());

        assertTrue("D06 - Số môn đủ điểm là 2/3", transcriptAn.getCompletedCourses() == 2, 
                "Môn đủ: " + transcriptAn.getCompletedCourses());
        assertTrue("D06 - Số tín chỉ tích lũy là 5/7", transcriptAn.getCompletedCredits() == 5, 
                "TC đủ: " + transcriptAn.getCompletedCredits());

        Double rawGpa = transcriptAn.calculateRawWeightedGpa();
        assertTrue("D06 - GPA tạm tính thô = 7.60", Math.abs(rawGpa - 7.60) < 0.0001, 
                String.format("Raw GPA: %.4f", rawGpa));
        assertTrue("D06 - Hiển thị GPA là '7.60'", "7.60".equals(transcriptAn.getFormattedGpa()), 
                "Formatted GPA: " + transcriptAn.getFormattedGpa());
    }

    /**
     * Ca D07: Kiểm thử độ chính xác số học & Tính từ số thô chưa làm tròn (Bình)
     * - Tạo riêng cho Bình 2 môn Lý thuyết mới, mỗi môn 1 tín chỉ:
     *   + Môn A (1 TC, Lý thuyết): QT = 8.01, CK = 8.00
     *     Tổng kết thô = 8.01 * 0.3 + 8.00 * 0.7 = 2.403 + 5.600 = 8.003 -> Hiển thị: 8.00
     *   + Môn B (1 TC, Lý thuyết): QT = 8.02, CK = 8.00
     *     Tổng kết thô = 8.02 * 0.3 + 8.00 * 0.7 = 2.406 + 5.600 = 8.006 -> Hiển thị: 8.01
     * - Điểm TB tính từ số thô:
     *   GPA thô = (8.003 * 1 + 8.006 * 1) / 2 = 16.009 / 2 = 8.0045
     *   Làm tròn nửa lên hiển thị: "8.00" (vì 8.0045 < 8.005)
     *   (Nếu tính từ số đã format 8.00 và 8.01 thì (8.00 + 8.01) / 2 = 8.005 -> làm tròn ra 8.01: SAI)
     * - Đủ 2/2 môn, 2/2 tín chỉ.
     */
    private static void testD07_BinhPrecisionAndRawCalculation() {
        System.out.println("--- Kiểm thử Ca D07: Tính GPA từ số thô chưa làm tròn (Bình: 8.0045 -> 8.00) ---");

        Student binh = new Student("QE210002", "Trần Bình");
        Course courseA = new TheoryCourse("CS_A", "Môn A Khái Luận", 1);
        Course courseB = new TheoryCourse("CS_B", "Môn B Chuyên Đề", 1);

        GradeRecord recA = new GradeRecord(binh, courseA, 8.01, 8.00);
        GradeRecord recB = new GradeRecord(binh, courseB, 8.02, 8.00);

        List<GradeRecord> records = new ArrayList<GradeRecord>();
        records.add(recA);
        records.add(recB);

        Transcript transcript = new Transcript(binh, records);

        // Kiểm tra điểm tổng kết thô từng môn
        Double rawA = recA.getFinalScore();
        Double rawB = recB.getFinalScore();
        assertTrue("D07 - Tổng kết thô môn A là 8.003", Math.abs(rawA - 8.003) < 0.0001, 
                String.format("Raw score A: %.6f", rawA));
        assertTrue("D07 - Tổng kết thô môn B là 8.006", Math.abs(rawB - 8.006) < 0.0001, 
                String.format("Raw score B: %.6f", rawB));

        // Kiểm tra GPA tính từ số thô
        Double rawGpa = transcript.calculateRawWeightedGpa();
        assertTrue("D07 - GPA thô = (8.003 + 8.006)/2 = 8.0045", Math.abs(rawGpa - 8.0045) < 0.0001, 
                String.format("Kỳ vọng: 8.0045 | Thực tế: %.6f", rawGpa));

        // Kiểm tra hiển thị làm tròn: 8.0045 làm tròn thành 8.00
        String formattedGpa = transcript.getFormattedGpa();
        assertTrue("D07 - GPA hiển thị chính xác là '8.00' (không bị làm tròn sai thành 8.01)", 
                "8.00".equals(formattedGpa), "Formatted GPA: " + formattedGpa);

        assertTrue("D07 - Đủ 2/2 môn", transcript.getCompletedCourses() == 2 && transcript.getTotalCourses() == 2, 
                String.format("Môn: %d/%d", transcript.getCompletedCourses(), transcript.getTotalCourses()));
        assertTrue("D07 - Đủ 2/2 tín chỉ", transcript.getCompletedCredits() == 2 && transcript.getTotalCredits() == 2, 
                String.format("Tín chỉ: %d/%d", transcript.getCompletedCredits(), transcript.getTotalCredits()));
    }

    /**
     * Kiểm thử Tính Đóng Gói (Encapsulation) & Tính Bất Biến (Immutability):
     * - Bảng điểm không cho sửa đổi danh sách bản ghi trả về từ bên ngoài.
     * - Bắt lỗi khi truyền student == null vào constructor Transcript.
     */
    private static void testEncapsulationAndImmutability() {
        System.out.println("--- Kiểm thử Tính Đóng Gói & Bất Biến của Transcript ---");

        Student s = new Student("QE210003", "Phạm Văn C");
        Course c = new TheoryCourse("CS100", "Nhập môn Tin học", 3);
        List<GradeRecord> recs = new ArrayList<GradeRecord>();
        recs.add(new GradeRecord(s, c, 7.0, 8.0));

        Transcript t = new Transcript(s, recs);

        // Kiểm tra danh sách getRecords() không thể chỉnh sửa
        boolean immutabilityRecords = false;
        try {
            t.getRecords().add(new GradeRecord(s, c, 5.0, 5.0));
        } catch (UnsupportedOperationException e) {
            immutabilityRecords = true;
        }
        assertTrue("Encapsulation - getRecords() là UnmodifiableList", immutabilityRecords, 
                "Chặn thêm phần tử vào getRecords()");

        // Kiểm tra danh sách getCompleteRecords() không thể chỉnh sửa
        boolean immutabilityComplete = false;
        try {
            t.getCompleteRecords().clear();
        } catch (UnsupportedOperationException e) {
            immutabilityComplete = true;
        }
        assertTrue("Encapsulation - getCompleteRecords() là UnmodifiableList", immutabilityComplete, 
                "Chặn xóa phần tử trong getCompleteRecords()");

        // Kiểm tra student không được null
        boolean nullStudentBlocked = false;
        try {
            new Transcript(null, recs);
        } catch (IllegalArgumentException e) {
            nullStudentBlocked = true;
        }
        assertTrue("Validation - Chặn student null trong constructor", nullStudentBlocked, 
                "Ném IllegalArgumentException khi student == null");
    }

    /**
     * Kiểm thử Tích hợp Dịch vụ TranscriptService:
     * - Tạo bảng điểm từ StudentManager & GradeManager
     * - Lấy tất cả bảng điểm
     * - Tra cứu sinh viên không tồn tại trả về null
     * - In bảng điểm console không bị lỗi ngoại lệ
     */
    private static void testTranscriptServiceIntegration() {
        System.out.println("--- Kiểm thử Tích hợp TranscriptService ---");

        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        TranscriptService ts = new TranscriptService(sm, gm);

        // Tra cứu sinh viên hợp lệ
        Transcript tAn = ts.getTranscript("QE210001");
        assertTrue("Service - getTranscript tìm thấy An", tAn != null, "Tìm thấy QE210001");

        // Tra cứu sinh viên không tồn tại
        Transcript tNotFound = ts.getTranscript("UNKNOWN_999");
        assertTrue("Service - getTranscript trả về null cho mã không tồn tại", tNotFound == null, 
                "Trả về null an toàn");

        // Lấy tất cả bảng điểm
        List<Transcript> allTranscripts = ts.getAllTranscripts();
        assertTrue("Service - getAllTranscripts trả về đủ 2 bảng điểm", allTranscripts.size() == 2, 
                "Số bảng điểm: " + allTranscripts.size());

        // Kiểm tra in console (không crash)
        System.out.println(">>> Demo in bảng điểm console qua TranscriptService:");
        ts.printTranscript("QE210001");
        ts.printTranscript("QE210002");
        ts.printTranscript("UNKNOWN_999"); // In thông báo không tìm thấy
        assertTrue("Service - In bảng điểm console thành công không ngoại lệ", true, 
                "In console hoàn tất an toàn");
    }
}

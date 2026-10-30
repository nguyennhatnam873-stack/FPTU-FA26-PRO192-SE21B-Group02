package studentgrademanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;
import studentgrademanager.services.TranscriptService;
import studentgrademanager.storage.CsvExporter;
import studentgrademanager.storage.FileDataStore;

/**
 * Bộ kiểm thử tự động cho Người 5 — Lưu/đọc file & CSV.
 * Phụ trách các ca kiểm thử: D08, D09, D10 và các ca mở rộng.
 */
public class Person5Test {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" BẮT ĐẦU KIỂM THỬ CHO NGƯỜI 5: LƯU/ĐỌC FILE & CSV");
        System.out.println("================================================================\n");

        testD08_ExportAnTranscriptToCsv();
        testD08_CsvEscapingAndFormatting();
        testD09_SaveAndReloadDataPersistence();
        testD10_ZeroScoreVsMissingScorePersistence();
        testCorruptedFileProtection();
        testRelativePathAndDirectoryCreation();

        System.out.println("\n================================================================");
        System.out.printf(" KẾT QUẢ KIỂM THỬ NGƯỜI 5: %d/%d PASS (%.1f%%)\n", 
                passedTests, totalTests, (passedTests * 100.0 / totalTests));
        System.out.println("================================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> TẤT CẢ CÁC CA TEST CỦA NGƯỜI 5 ĐÃ ĐẠT YÊU CẦU 100%! <<<");
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
     * Ca D08: Xuất CSV bảng điểm An (dữ liệu mẫu)
     * - CSV header + 3 dòng chi tiết
     * - LT101: 8.00, 6.00, 6.60, DU_DIEM
     * - TH101: 8.00, 6.00, 7.00, DU_DIEM
     * - LT102: 9.00, trống, trống, CHUA_DU_DIEM
     * - Dấu phẩy phân cách, dấu chấm thập phân.
     */
    private static void testD08_ExportAnTranscriptToCsv() {
        System.out.println("--- Kiểm thử Ca D08: Xuất CSV bảng điểm mẫu của An ---");

        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        TranscriptService ts = new TranscriptService(sm, gm);
        Transcript transcriptAn = ts.getTranscript("QE210001");

        File csvFile = new File("test_output/an_transcript_test.csv");
        if (csvFile.exists()) {
            csvFile.delete();
        }

        try {
            CsvExporter.exportTranscriptToCsv(transcriptAn, csvFile);
            assertTrue("D08 - File CSV được tạo", csvFile.exists() && csvFile.length() > 0, 
                    "File " + csvFile.getPath() + " tạo thành công, dung lượng: " + csvFile.length() + " bytes");

            List<String> lines = readLinesUtf8(csvFile);
            assertTrue("D08 - Header đúng chuẩn", lines.size() >= 4, "Số dòng đọc được: " + lines.size());

            String header = lines.get(0).replace("\uFEFF", ""); // bỏ BOM nếu có
            boolean headerOk = header.contains("MSSV") && header.contains("Diem qua trinh") && header.contains("Trang thai");
            assertTrue("D08 - Nội dung Header hợp lệ", headerOk, header);

            // Kiểm tra dòng 1: LT101 (8.00, 6.00, 6.60, DU_DIEM)
            String lineLT101 = findLineContaining(lines, "LT101");
            boolean lt101Ok = lineLT101 != null 
                    && lineLT101.contains("8.00") 
                    && lineLT101.contains("6.00") 
                    && lineLT101.contains("6.60") 
                    && lineLT101.contains(CsvExporter.STATUS_COMPLETE);
            assertTrue("D08 - Dòng LT101 đầy đủ điểm", lt101Ok, lineLT101);

            // Kiểm tra dòng 2: TH101 (8.00, 6.00, 7.00, DU_DIEM)
            String lineTH101 = findLineContaining(lines, "TH101");
            boolean th101Ok = lineTH101 != null 
                    && lineTH101.contains("8.00") 
                    && lineTH101.contains("6.00") 
                    && lineTH101.contains("7.00") 
                    && lineTH101.contains(CsvExporter.STATUS_COMPLETE);
            assertTrue("D08 - Dòng TH101 đầy đủ điểm", th101Ok, lineTH101);

            // Kiểm tra dòng 3: LT102 (9.00, ô trống, ô trống, CHUA_DU_DIEM)
            String lineLT102 = findLineContaining(lines, "LT102");
            boolean lt102Ok = lineLT102 != null 
                    && lineLT102.contains("9.00") 
                    && lineLT102.contains(",,") // ô CK và tổng kết trống
                    && lineLT102.contains(CsvExporter.STATUS_INCOMPLETE);
            assertTrue("D08 - Dòng LT102 trống cuối kỳ & tổng kết", lt102Ok, lineLT102);

        } catch (Exception e) {
            assertTrue("D08 - Không ném ngoại lệ", false, "Lỗi: " + e.getMessage());
        } finally {
            if (csvFile.exists()) {
                csvFile.delete();
            }
        }
    }

    /**
     * Ca D08 (Bổ sung): Quote và escape chuẩn RFC 4180 cho chuỗi có dấu phẩy hoặc ngoặc kép
     */
    private static void testD08_CsvEscapingAndFormatting() {
        System.out.println("--- Kiểm thử Ca D08 (Bổ sung): Quoting & Escaping RFC 4180 ---");

        String escapedSimple = CsvExporter.escapeCsvCell("Nguyen An");
        assertTrue("Escape chuỗi thường", "Nguyen An".equals(escapedSimple), escapedSimple);

        String escapedComma = CsvExporter.escapeCsvCell("Ha Noi, Viet Nam");
        assertTrue("Escape chuỗi có dấu phẩy", "\"Ha Noi, Viet Nam\"".equals(escapedComma), escapedComma);

        String escapedQuote = CsvExporter.escapeCsvCell("Khoa \"CNTT\"");
        assertTrue("Escape chuỗi có dấu ngoặc kép", "\"Khoa \"\"CNTT\"\"\"".equals(escapedQuote), escapedQuote);

        String formattedScore0 = CsvExporter.formatScoreForCsv(0.0);
        assertTrue("Điểm 0 xuất 0.00", "0.00".equals(formattedScore0), formattedScore0);

        String formattedNull = CsvExporter.formatScoreForCsv(null);
        assertTrue("Điểm null xuất chuỗi rỗng", "".equals(formattedNull), formattedNull);
    }

    /**
     * Ca D09: Lưu -> thoát -> mở lại -> xuất bảng điểm
     * Dữ liệu phục hồi đúng, TB An = 6.76.
     */
    private static void testD09_SaveAndReloadDataPersistence() {
        System.out.println("--- Kiểm thử Ca D09: Lưu và Phục hồi toàn vẹn dữ liệu ---");

        String testDataPath = "test_output/data_d09_test.txt";
        File testFile = new File(testDataPath);
        if (testFile.exists()) {
            testFile.delete();
        }

        FileDataStore store = new FileDataStore(testDataPath);

        // 1. Chuẩn bị dữ liệu ban đầu
        StudentManager sm1 = new StudentManager();
        CourseManager cm1 = new CourseManager();
        GradeManager gm1 = new GradeManager();
        FileDataStore.initializeSampleData(sm1, cm1, gm1);

        try {
            // Lưu dữ liệu ra file
            store.saveData(sm1, cm1, gm1);
            assertTrue("D09 - File được lưu thành công", testFile.exists() && testFile.length() > 0, "Dung lượng: " + testFile.length());

            // 2. Mô phỏng khởi động lại ứng dụng với các Manager hoàn toàn mới
            StudentManager sm2 = new StudentManager();
            CourseManager cm2 = new CourseManager();
            GradeManager gm2 = new GradeManager();

            assertTrue("D09 - Dữ liệu mới ban đầu rỗng", sm2.getStudentCount() == 0 && cm2.getCourseCount() == 0, "Managers rỗng");

            // Nạp lại từ file
            boolean loaded = store.loadData(sm2, cm2, gm2);
            assertTrue("D09 - Load dữ liệu thành công", loaded, "loadData trả về true");

            assertTrue("D09 - Phục hồi đủ 2 sinh viên", sm2.getStudentCount() == 2, "SV count: " + sm2.getStudentCount());
            assertTrue("D09 - Phục hồi đủ 3 môn học", cm2.getCourseCount() == 3, "Course count: " + cm2.getCourseCount());
            assertTrue("D09 - Phục hồi đủ 3 bản ghi điểm", gm2.getRecordCount() == 3, "Grade count: " + gm2.getRecordCount());

            // Kiểm tra đối tượng môn học được tạo đúng lớp con (Lý thuyết / Thực hành)
            Course lt101 = cm2.findCourseById("LT101");
            Course th101 = cm2.findCourseById("TH101");
            assertTrue("D09 - Phục hồi đúng loại môn Lý thuyết", "Lý thuyết".equals(lt101.getCourseType()), lt101.toString());
            assertTrue("D09 - Phục hồi đúng loại môn Thực hành", "Thực hành".equals(th101.getCourseType()), th101.toString());

            // Kiểm tra tính bảng điểm An sau phục hồi: TB = 6.76
            TranscriptService ts2 = new TranscriptService(sm2, gm2);
            Transcript anTranscript = ts2.getTranscript("QE210001");
            Double gpaAn = anTranscript.calculateWeightedGPA();

            boolean gpaOk = (gpaAn != null) && Math.abs(gpaAn - 6.76) < 0.01;
            assertTrue("D09 - TB An phục hồi chính xác 6.76", gpaOk, String.format("Thực tế: %.4f (formatted: %s)", gpaAn, anTranscript.getFormattedGPA()));
            assertTrue("D09 - Số môn đủ 2/3", anTranscript.getCompletedCourses() == 2 && anTranscript.getTotalCourses() == 3, "2/3 môn");
            assertTrue("D09 - Số tín chỉ đủ 5/7", anTranscript.getCompletedCredits() == 5 && anTranscript.getTotalCredits() == 7, "5/7 TC");

            // Kiểm tra sinh viên Bình: chưa có kết quả -> CHUA_CO_DIEM_TRUNG_BINH
            Transcript binhTranscript = ts2.getTranscript("QE210002");
            assertTrue("D09 - Bình chưa có điểm TB", binhTranscript.calculateWeightedGPA() == null, binhTranscript.getFormattedGPA());

        } catch (Exception e) {
            assertTrue("D09 - Không gặp lỗi", false, "Lỗi: " + e.getMessage());
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    /**
     * Ca D10: An LT101: QT=0, CK=thiếu -> lưu -> mở lại
     * QT vẫn 0, CK thiếu; TB = 7.00, đủ 1/3 môn, 2/7 TC.
     */
    private static void testD10_ZeroScoreVsMissingScorePersistence() {
        System.out.println("--- Kiểm thử Ca D10: Phân biệt chính xác giữa điểm 0 và điểm thiếu ---");

        String testDataPath = "test_output/data_d10_test.txt";
        File testFile = new File(testDataPath);
        if (testFile.exists()) {
            testFile.delete();
        }

        FileDataStore store = new FileDataStore(testDataPath);

        StudentManager sm1 = new StudentManager();
        CourseManager cm1 = new CourseManager();
        GradeManager gm1 = new GradeManager();
        FileDataStore.initializeSampleData(sm1, cm1, gm1);

        // Cập nhật LT101 của An: QT = 0.0, CK = thiếu (null)
        Student an = sm1.findStudent("QE210001");
        Course lt101 = cm1.findCourseById("LT101");
        gm1.createOrUpdateRecord(an, lt101, 0.0, null);

        try {
            // Lưu ra file
            store.saveData(sm1, cm1, gm1);

            // Khởi động lại và nạp vào Managers mới
            StudentManager sm2 = new StudentManager();
            CourseManager cm2 = new CourseManager();
            GradeManager gm2 = new GradeManager();

            store.loadData(sm2, cm2, gm2);

            GradeRecord reloadedLT101 = gm2.findRecord("QE210001", "LT101");
            assertTrue("D10 - Điểm QT của LT101 vẫn là 0.0", 
                    reloadedLT101.getProcessScore() != null && Math.abs(reloadedLT101.getProcessScore() - 0.0) < 0.0001, 
                    "QT = " + reloadedLT101.getProcessScore());
            assertTrue("D10 - Điểm CK của LT101 vẫn là null (thiếu)", 
                    reloadedLT101.getExamScore() == null, 
                    "CK = " + reloadedLT101.getExamScore());
            assertTrue("D10 - LT101 chưa đủ điểm", !reloadedLT101.isComplete(), "isComplete() == false");

            // Bảng điểm An lúc này:
            // - LT101: QT=0, CK=null -> CHƯA ĐỦ ĐIỂM
            // - TH101: QT=8, CK=6 -> ĐỦ ĐIỂM (tổng kết 7.00, 2 TC)
            // - LT102: QT=9, CK=null -> CHƯA ĐỦ ĐIỂM
            // -> Duy nhất TH101 đủ điểm!
            // -> TB = 7.00, đủ 1/3 môn, 2/7 tín chỉ
            TranscriptService ts2 = new TranscriptService(sm2, gm2);
            Transcript anTranscript = ts2.getTranscript("QE210001");
            Double gpaAn = anTranscript.calculateWeightedGPA();

            boolean gpaOk = (gpaAn != null) && Math.abs(gpaAn - 7.00) < 0.0001;
            assertTrue("D10 - TB An chính xác 7.00", gpaOk, String.format("Thực tế: %.2f", gpaAn));
            assertTrue("D10 - Đủ đúng 1/3 môn", anTranscript.getCompletedCourses() == 1 && anTranscript.getTotalCourses() == 3, "1/3 môn");
            assertTrue("D10 - Đủ đúng 2/7 tín chỉ", anTranscript.getCompletedCredits() == 2 && anTranscript.getTotalCredits() == 7, "2/7 tín chỉ");

        } catch (Exception e) {
            assertTrue("D10 - Không gặp lỗi", false, "Lỗi: " + e.getMessage());
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    /**
     * Kiểm thử bảo vệ: File hỏng thì báo lỗi rõ ràng và KHÔNG làm mất dữ liệu hiện tại
     */
    private static void testCorruptedFileProtection() {
        System.out.println("--- Kiểm thử Bảo vệ Dữ liệu: File hỏng ném lỗi và không xóa Manager ---");

        String corruptPath = "test_output/corrupt_test.txt";
        File corruptFile = new File(corruptPath);
        File parent = corruptFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try {
            // Tạo 1 file hỏng định dạng (điểm sai ký tự hoặc tín chỉ không hợp lệ)
            FileOutputStream fos = new FileOutputStream(corruptFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            osw.write("[STUDENTS]\nQE210001|An\n\n[COURSES]\nTHEORY|LT101|Mon hoc|INVALID_CREDIT\n");
            osw.close();

            StudentManager sm = new StudentManager();
            CourseManager cm = new CourseManager();
            GradeManager gm = new GradeManager();
            sm.addStudent(new Student("EXISTING", "Du lieu cu"));

            FileDataStore store = new FileDataStore(corruptPath);
            boolean threwException = false;
            try {
                store.loadData(sm, cm, gm);
            } catch (Exception e) {
                threwException = true;
            }

            assertTrue("File hỏng phát hiện và ném ngoại lệ", threwException, "Bắt được lỗi định dạng file");
            assertTrue("Dữ liệu cũ trong Manager vẫn được bảo vệ", sm.findStudent("EXISTING") != null, "Manager không bị xóa");

        } catch (Exception e) {
            assertTrue("Xử lý file hỏng an toàn", false, e.getMessage());
        } finally {
            if (corruptFile.exists()) {
                corruptFile.delete();
            }
        }
    }

    /**
     * Kiểm thử đường dẫn tương đối và tự động tạo thư mục con
     */
    private static void testRelativePathAndDirectoryCreation() {
        System.out.println("--- Kiểm thử Đường dẫn tương đối & Tự tạo thư mục ---");

        String deepPath = "test_output/sub1/sub2/data_deep.txt";
        File deepFile = new File(deepPath);
        if (deepFile.exists()) {
            deepFile.delete();
        }

        FileDataStore store = new FileDataStore(deepPath);
        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        try {
            store.saveData(sm, cm, gm);
            assertTrue("Tự tạo thư mục cha khi lưu", deepFile.exists(), "File tồn tại tại: " + deepPath);
        } catch (Exception e) {
            assertTrue("Lưu đường dẫn sâu thành công", false, e.getMessage());
        } finally {
            if (deepFile.exists()) {
                deepFile.delete();
            }
        }
    }

    // ==================== TIỆN ÍCH HỖ TRỢ ====================

    private static List<String> readLinesUtf8(File file) throws Exception {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }

    private static String findLineContaining(List<String> lines, String keyword) {
        for (String l : lines) {
            if (l.contains(keyword)) {
                return l;
            }
        }
        return null;
    }
}

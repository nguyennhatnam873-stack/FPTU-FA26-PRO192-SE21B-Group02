package studentgrademanager;

import studentgrademanager.models.Course;
import studentgrademanager.models.PracticeCourse;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.services.CourseManager;

/**
 * Kiểm thử tự động cho Người 2 — Môn học & đánh giá.
 * Phụ trách các ca kiểm thử: D01, D07, D11.
 */
public class Person2Test {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" BẮT ĐẦU KIỂM THỬ CHO NGƯỜI 2: MÔN HỌC & ĐÁNH GIÁ");
        System.out.println("================================================================\n");

        testD01_GradingFormulasAndPolymorphism();
        testD01_NullComponents();
        testD07_TranscriptWeightedAverageContribution();
        testD11_InvalidCreditsZero();
        testD11_InvalidCreditsDecimal();
        testD11_DuplicateCourseCode();
        testD11_EmptyCourseNameAndId();
        testGuard_PreventModifyingCreditsAfterGrades();
        testFactory_CreateCourseCoordinationWithPerson5();

        System.out.println("\n================================================================");
        System.out.printf(" KẾT QUẢ KIỂM THỬ: %d/%d PASS (%.1f%%)\n", 
                passedTests, totalTests, (passedTests * 100.0 / totalTests));
        System.out.println("================================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> TẤT CẢ CÁC CA TEST CỦA NGƯỜI 2 ĐÃ ĐẠT YÊU CẦU 100%! <<<");
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
     * Ca D01: Cài đặt hai công thức: lý thuyết 0.30/0.70 và thực hành 0.50/0.50
     * Với đầu vào (quá trình 8.0, cuối kỳ 6.0):
     * - Môn lý thuyết LT101: 8.0 * 0.3 + 6.0 * 0.7 = 6.60
     * - Môn thực hành TH101: 8.0 * 0.5 + 6.0 * 0.5 = 7.00
     */
    private static void testD01_GradingFormulasAndPolymorphism() {
        System.out.println("--- Kiểm thử Ca D01: Đa hình tính điểm Lý thuyết & Thực hành ---");
        Course ltCourse = new TheoryCourse("LT101", "Lý thuyết Hướng đối tượng", 3);
        Course thCourse = new PracticeCourse("TH101", "Thực hành Java", 2);

        Double ltGrade = ltCourse.calculateFinalGrade(8.0, 6.0);
        Double thGrade = thCourse.calculateFinalGrade(8.0, 6.0);

        boolean passLT = (ltGrade != null) && (Math.abs(ltGrade - 6.60) < 0.0001);
        boolean passTH = (thGrade != null) && (Math.abs(thGrade - 7.00) < 0.0001);

        assertTrue("D01 - Lý thuyết (8.0, 6.0)", passLT, 
                String.format("Kỳ vọng: 6.60 | Thực tế: %.2f", ltGrade));
        assertTrue("D01 - Thực hành (8.0, 6.0)", passTH, 
                String.format("Kỳ vọng: 7.00 | Thực tế: %.2f", thGrade));
    }

    /**
     * Ca D01 (Bổ sung): Trả về null khi thiếu một thành phần (Double null)
     */
    private static void testD01_NullComponents() {
        System.out.println("--- Kiểm thử Ca D01 (Bổ sung): Xử lý thiếu điểm thành phần ---");
        Course lt = new TheoryCourse("LT102", "Cơ sở dữ liệu", 3);
        Course th = new PracticeCourse("TH102", "Thực hành CSDL", 2);

        assertTrue("LT thiếu cuối kỳ", lt.calculateFinalGrade(7.5, null) == null, "Trả về null khi CK null");
        assertTrue("LT thiếu quá trình", lt.calculateFinalGrade(null, 8.0) == null, "Trả về null khi QT null");
        assertTrue("LT thiếu cả hai", lt.calculateFinalGrade(null, null) == null, "Trả về null khi cả 2 null");

        assertTrue("TH thiếu cuối kỳ", th.calculateFinalGrade(9.0, null) == null, "Trả về null khi CK null");
        assertTrue("TH thiếu quá trình", th.calculateFinalGrade(null, 7.0) == null, "Trả về null khi QT null");
        assertTrue("TH thiếu cả hai", th.calculateFinalGrade(null, null) == null, "Trả về null khi cả 2 null");
    }

    /**
     * Ca D07: Đóng góp của Course vào luồng tính bảng điểm trung bình có trọng số tín chỉ.
     * Kiểm tra tính đa hình khi duyệt qua danh sách Course và lấy tín chỉ * điểm tổng kết.
     */
    private static void testD07_TranscriptWeightedAverageContribution() {
        System.out.println("--- Kiểm thử Ca D07: Tính đa hình trong GPA có trọng số tín chỉ ---");
        Course c1 = new TheoryCourse("PRO192", "Object-Oriented Programming", 3);
        Course c2 = new PracticeCourse("LAB211", "OOP Lab", 2);

        // Giả lập điểm:
        // c1 (LT): QT 8.5, CK 7.8 -> 8.5*0.3 + 7.8*0.7 = 2.55 + 5.46 = 8.01
        // c2 (TH): QT 8.0, CK 8.0 -> 8.0*0.5 + 8.0*0.5 = 8.00
        // Weighted sum = (8.01 * 3) + (8.00 * 2) = 24.03 + 16.00 = 40.03
        // Total credits = 3 + 2 = 5
        // GPA = 40.03 / 5 = 8.006 -> hiển thị làm tròn 8.01
        Double g1 = c1.calculateFinalGrade(8.5, 7.8);
        Double g2 = c2.calculateFinalGrade(8.0, 8.0);

        double totalWeightedGrade = (g1 * c1.getCredits()) + (g2 * c2.getCredits());
        int totalCredits = c1.getCredits() + c2.getCredits();
        double gpa = totalWeightedGrade / totalCredits;

        boolean pass = Math.abs(gpa - 8.006) < 0.0001;
        assertTrue("D07 - GPA có trọng số tín chỉ", pass, 
                String.format("GPA thực tế: %.4f | Tổng tín chỉ: %d", gpa, totalCredits));
    }

    /**
     * Ca D11: Kiểm tra môn học - Tín chỉ = 0 (phải bị từ chối)
     */
    private static void testD11_InvalidCreditsZero() {
        System.out.println("--- Kiểm thử Ca D11: Từ chối tín chỉ = 0 ---");
        boolean caughtInParser = false;
        try {
            CourseManager.parseAndValidateCredits("0");
        } catch (IllegalArgumentException e) {
            caughtInParser = true;
        }
        assertTrue("D11 - Parser từ chối tín chỉ 0", caughtInParser, "Bắt ngoại lệ IllegalArgumentException");

        boolean caughtInConstructor = false;
        try {
            new TheoryCourse("TEST01", "Mon Test", 0);
        } catch (IllegalArgumentException e) {
            caughtInConstructor = true;
        }
        assertTrue("D11 - Constructor từ chối tín chỉ 0", caughtInConstructor, "Bắt ngoại lệ IllegalArgumentException");
    }

    /**
     * Ca D11: Kiểm tra môn học - Tín chỉ = 1.5 (không phải số nguyên 1-6, phải bị từ chối)
     */
    private static void testD11_InvalidCreditsDecimal() {
        System.out.println("--- Kiểm thử Ca D11: Từ chối tín chỉ là số thập phân 1.5 ---");
        boolean caughtDecimalDot = false;
        try {
            CourseManager.parseAndValidateCredits("1.5");
        } catch (IllegalArgumentException e) {
            caughtDecimalDot = true;
        }
        assertTrue("D11 - Từ chối tín chỉ 1.5", caughtDecimalDot, "Bắt ngoại lệ số thập phân");

        boolean caughtDecimalComma = false;
        try {
            CourseManager.parseAndValidateCredits("1,5");
        } catch (IllegalArgumentException e) {
            caughtDecimalComma = true;
        }
        assertTrue("D11 - Từ chối tín chỉ 1,5", caughtDecimalComma, "Bắt ngoại lệ số thập phân dấu phẩy");
    }

    /**
     * Ca D11: Kiểm tra môn học - Trùng mã LT101 (phải bị từ chối)
     */
    private static void testD11_DuplicateCourseCode() {
        System.out.println("--- Kiểm thử Ca D11: Từ chối trùng mã môn LT101 ---");
        CourseManager manager = new CourseManager();
        Course c1 = new TheoryCourse("LT101", "Lý thuyết Lập trình C", 3);
        manager.addCourse(c1);

        boolean caughtDuplicate = false;
        try {
            Course c2 = new TheoryCourse("LT101", "Lý thuyết Lập trình C Nâng Cao", 3);
            manager.addCourse(c2);
        } catch (IllegalArgumentException e) {
            caughtDuplicate = true;
        }
        assertTrue("D11 - Từ chối trùng mã LT101", caughtDuplicate, "Bắt ngoại lệ trùng mã");

        boolean caughtDuplicateCase = false;
        try {
            Course c3 = new PracticeCourse("lt101", "Thực hành C", 2);
            manager.addCourse(c3);
        } catch (IllegalArgumentException e) {
            caughtDuplicateCase = true;
        }
        assertTrue("D11 - Từ chối trùng mã lt101 (case-insensitive)", caughtDuplicateCase, "Bắt ngoại lệ trùng mã viết thường");
    }

    /**
     * Ca D11 (Bổ sung): Tên môn rỗng hoặc mã rỗng, trim khoảng trắng
     */
    private static void testD11_EmptyCourseNameAndId() {
        System.out.println("--- Kiểm thử Ca D11 (Bổ sung): Rỗng và Trim khoảng trắng ---");
        boolean caughtEmptyName = false;
        try {
            new TheoryCourse("PRN211", "   ", 3);
        } catch (IllegalArgumentException e) {
            caughtEmptyName = true;
        }
        assertTrue("D11 - Từ chối tên môn rỗng", caughtEmptyName, "Bắt ngoại lệ tên rỗng");

        Course trimmedCourse = new TheoryCourse("  csd201  ", "  Data Structures  ", 3);
        assertTrue("D11 - Trim mã môn và tên môn", 
                "CSD201".equals(trimmedCourse.getCourseId()) && "Data Structures".equals(trimmedCourse.getCourseName()),
                "Mã môn chuẩn hóa UPPERCASE, tên môn được trim khoảng trắng");
    }

    /**
     * Bảo vệ: Không đổi loại môn hoặc tín chỉ sau khi đã có kết quả
     */
    private static void testGuard_PreventModifyingCreditsAfterGrades() {
        System.out.println("--- Kiểm thử Bảo vệ: Không cho đổi tín chỉ khi đã có kết quả ---");
        CourseManager manager = new CourseManager();
        Course course = new TheoryCourse("MAE101", "Toán cao cấp", 3);
        manager.addCourse(course);

        // Giả lập checker: MAE101 đã có điểm sinh viên ghi nhận
        manager.setUsageChecker(new CourseManager.CourseUsageChecker() {
            @Override
            public boolean hasGrades(String courseId) {
                return "MAE101".equalsIgnoreCase(courseId);
            }
        });

        boolean caughtBlocked = false;
        try {
            manager.updateCourseCredits("MAE101", 4);
        } catch (IllegalStateException e) {
            caughtBlocked = true;
        }

        assertTrue("Chặn đổi tín chỉ khi đã có kết quả", caughtBlocked, 
                "Bắt ngoại lệ IllegalStateException chặn thay đổi");
        assertTrue("Tín chỉ vẫn giữ nguyên là 3", course.getCredits() == 3, "Tín chỉ không bị đổi");
    }

    /**
     * Phối hợp Người 5: Tạo Course từ loại chuỗi (THEORY / PRACTICE)
     */
    private static void testFactory_CreateCourseCoordinationWithPerson5() {
        System.out.println("--- Kiểm thử Phối hợp Người 5: Factory method createCourse ---");
        Course lt = CourseManager.createCourse("THEORY", "MAS291", "Xác suất thống kê", 3);
        Course th = CourseManager.createCourse("PRACTICE", "DBI202", "Hệ quản trị CSDL", 3);

        assertTrue("Tạo đúng kiểu TheoryCourse", lt instanceof TheoryCourse, "Loại: " + lt.getCourseType());
        assertTrue("Tạo đúng kiểu PracticeCourse", th instanceof PracticeCourse, "Loại: " + th.getCourseType());
    }
}

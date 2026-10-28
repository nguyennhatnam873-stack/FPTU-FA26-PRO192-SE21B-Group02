package studentgrademanager.ui;

<<<<<<< HEAD
=======
import java.io.File;
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
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

<<<<<<< HEAD
public class MainMenu {

    private CourseManager courseManager;
    private StudentManager studentManager;
    private GradeManager gradeManager;
    private TranscriptService transcriptService;
    private FileDataStore fileDataStore;
    private Scanner scanner;

    public MainMenu(CourseManager courseManager, StudentManager studentManager, GradeManager gradeManager,
                    TranscriptService transcriptService, FileDataStore fileDataStore) {
        this.courseManager = courseManager;
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.transcriptService = transcriptService;
        this.fileDataStore = fileDataStore;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = prompt("Nhập lựa chọn của bạn (0-5): ");
            switch (choice) {
                case "1":
                    handleCourseSubMenu();
                    break;
                case "2":
                    handleStudentSubMenu();
                    break;
                case "3":
                    handleGradeSubMenu();
                    break;
                case "4":
                    handleTranscriptSubMenu();
                    break;
                case "5":
                    handleStorageSubMenu();
                    break;
                case "0":
                    System.out.println("Cảm ơn bạn đã sử dụng chương trình! Tạm biệt.");
                    running = false;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng chọn từ 0 đến 5.");
                    break;
            }
            if (running) {
                prompt("\nNhấn Enter để tiếp tục...");
=======
/**
 * Menu điều khiển Console chính của chương trình.
 * Phụ trách: Người 5 — Menu console & Giao diện người dùng
 */
public class MainMenu {

    private final StudentManager studentManager;
    private final CourseManager courseManager;
    private final GradeManager gradeManager;
    private final TranscriptService transcriptService;
    private final FileDataStore dataStore;
    private final Scanner scanner;

    private boolean isModified = false;

    public MainMenu(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager, FileDataStore dataStore) {
        this.studentManager = studentManager;
        this.courseManager = courseManager;
        this.gradeManager = gradeManager;
        this.dataStore = (dataStore != null) ? dataStore : new FileDataStore();
        this.transcriptService = new TranscriptService(this.studentManager, this.gradeManager);
        this.scanner = new Scanner(System.in);

        // Đăng ký usage checker để bảo vệ không đổi tín chỉ khi môn đã có điểm
        this.courseManager.setUsageChecker(new CourseManager.CourseUsageChecker() {
            @Override
            public boolean hasGrades(String courseId) {
                return MainMenu.this.gradeManager.hasGrades(courseId);
            }
        });
    }

    /**
     * Vòng lặp giao diện chính của console.
     */
    public void start() {
        System.out.println("======================================================================");
        System.out.println("   CHƯƠNG TRÌNH QUẢN LÝ SINH VIÊN VÀ BẢNG ĐIỂM (PRO192 - P05)");
        System.out.println("======================================================================");

        // Khởi động: Kiểm tra file lưu trữ
        if (this.dataStore.exists()) {
            System.out.println(">>> Đang nạp dữ liệu từ file: " + this.dataStore.getDataFilePath());
            try {
                this.dataStore.loadData(this.studentManager, this.courseManager, this.gradeManager);
                System.out.printf(">>> Nạp thành công: %d sinh viên, %d môn học, %d kết quả điểm.\n",
                        this.studentManager.getStudentCount(),
                        this.courseManager.getCourseCount(),
                        this.gradeManager.getRecordCount());
            } catch (Exception e) {
                System.err.println(">>> CẢNH BÁO: Lỗi khi đọc file lưu trữ (" + e.getMessage() + ")!");
                System.out.println(">>> Hệ thống giữ nguyên trạng thái ban đầu để bảo vệ dữ liệu.");
            }
        } else {
            System.out.println(">>> Chưa phát hiện file dữ liệu cũ. Khởi tạo dữ liệu mẫu ban đầu...");
            FileDataStore.initializeSampleData(this.studentManager, this.courseManager, this.gradeManager);
            this.isModified = true;
            System.out.println(">>> Đã nạp dữ liệu mẫu ban đầu (An & Bình, 3 môn, 3 kết quả của An).");
        }

        boolean running = true;
        while (running) {
            this.printMenu();
            String choice = this.readLine("Nhập lựa chọn của bạn (0-8): ").trim();
            switch (choice) {
                case "1":
                    this.handleStudentManagement();
                    break;
                case "2":
                    this.handleCourseManagement();
                    break;
                case "3":
                    this.handleCreateOrUpdateGrade();
                    break;
                case "4":
                    this.handleViewCourseGrades();
                    break;
                case "5":
                    this.handleViewStudentTranscript();
                    break;
                case "6":
                    this.handleViewWeightedGPA();
                    break;
                case "7":
                    this.handleExportCsv();
                    break;
                case "8":
                    this.handleFileStorageMenu();
                    break;
                case "0":
                    running = this.handleExit();
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn từ 0 đến 8.");
            }
            if (running) {
                System.out.println();
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
            }
        }
    }

<<<<<<< HEAD
    private void printMainMenu() {
        System.out.println("\n========================================================");
        System.out.println("   HỆ THỐNG QUẢN LÝ SINH VIÊN VÀ BẢNG ĐIỂM (ĐỀ P05)   ");
        System.out.println("========================================================");
        System.out.println("  1. Quản lý môn học");
        System.out.println("  2. Quản lý sinh viên");
        System.out.println("  3. Quản lý điểm số (Nhập & Cập nhật)");
        System.out.println("  4. Bảng điểm & Tính điểm trung bình (GPA)");
        System.out.println("  5. Lưu trữ & Đọc file");
        System.out.println("  0. Thoát chương trình");
        System.out.println("--------------------------------------------------------");
    }

    private void handleCourseSubMenu() {
        System.out.println("\n--- [1] QUẢN LÝ MÔN HỌC ---");
        System.out.println("1. Xem danh sách môn học");
        System.out.println("2. Thêm môn học mới");
        System.out.println("3. Cập nhật số tín chỉ môn học");
        System.out.println("4. Tìm kiếm môn học theo mã");
        System.out.println("0. Quay lại menu chính");

        String choice = prompt("Chọn thao tác: ");
        switch (choice) {
            case "1":
                displayAllCourses();
                break;
            case "2":
                addNewCourse();
                break;
            case "3":
                updateCredits();
                break;
            case "4":
                searchCourse();
=======
    private void printMenu() {
        System.out.println("\n----------------------------- MENU CHÍNH -----------------------------");
        System.out.println("1. Quản lý Sinh viên (Thêm, tìm, xem danh sách)");
        System.out.println("2. Quản lý Môn học (Thêm LT/TH, tìm, xem danh sách)");
        System.out.println("3. Tạo / Cập nhật kết quả (MSSV + Mã môn + Điểm QT, CK)");
        System.out.println("4. Xem kết quả môn học (Trạng thái đủ / chưa đủ điểm)");
        System.out.println("5. Xem bảng điểm sinh viên (Chi tiết các môn, điểm tổng kết)");
        System.out.println("6. Xem điểm trung bình có trọng số & thống kê tín chỉ");
        System.out.println("7. Xuất bảng điểm sinh viên ra file CSV (Chuẩn RFC 4180)");
        System.out.println("8. Quản lý lưu trữ file (Lưu, Tải lại, Khôi phục mẫu)");
        System.out.println("0. Thoát chương trình");
        System.out.println("----------------------------------------------------------------------");
    }

    // ==================== 1. QUẢN LÝ SINH VIÊN ====================

    private void handleStudentManagement() {
        System.out.println("\n--- [1] QUẢN LÝ SINH VIÊN ---");
        System.out.println("1. Thêm sinh viên mới");
        System.out.println("2. Tìm sinh viên theo MSSV");
        System.out.println("3. Xem danh sách toàn bộ sinh viên");
        System.out.println("0. Quay lại menu chính");

        String sub = this.readLine("Chọn: ").trim();
        switch (sub) {
            case "1":
                String id = this.readLine("Nhập MSSV (ví dụ: QE210003): ");
                String name = this.readLine("Nhập Họ và tên sinh viên: ");
                try {
                    boolean ok = this.studentManager.addStudent(id, name);
                    if (ok) {
                        this.isModified = true;
                        System.out.println("Thêm sinh viên thành công!");
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi: " + e.getMessage());
                }
                break;
            case "2":
                String searchId = this.readLine("Nhập MSSV cần tìm: ");
                Student s = this.studentManager.findStudent(searchId);
                if (s != null) {
                    System.out.printf("=> Tìm thấy: MSSV = %s | Họ tên = %s\n", s.getStudentId(), s.getFullName());
                } else {
                    System.out.println("=> Không tìm thấy sinh viên có MSSV: " + searchId);
                }
                break;
            case "3":
                List<Student> list = this.studentManager.getAllStudents();
                System.out.println("\nDANH SÁCH SINH VIÊN (" + list.size() + " sinh viên):");
                System.out.printf("%-12s | %-30s\n", "MSSV", "Họ và tên");
                System.out.println("--------------------------------------------------");
                for (Student st : list) {
                    System.out.printf("%-12s | %-30s\n", st.getStudentId(), st.getFullName());
                }
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
<<<<<<< HEAD
                break;
        }
    }

    private void displayAllCourses() {
        List<Course> courses = this.courseManager.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("Danh sách môn học đang trống.");
            return;
        }
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.printf("%-10s | %-30s | %-12s | %-8s\n", "MÃ MÔN", "TÊN MÔN HỌC", "LOẠI MÔN", "TÍN CHỈ");
        System.out.println("-------------------------------------------------------------------------");
        for (Course c : courses) {
            System.out.printf("%-10s | %-30s | %-12s | %-8d\n",
                    c.getCourseId(), c.getCourseName(), c.getCourseType(), c.getCredits());
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("Tổng số môn học: %d\n", courses.size());
    }

    private void addNewCourse() {
        System.out.println("\n--- Thêm môn học mới ---");
        String type = prompt("Loại môn (1. THEORY / Lý thuyết | 2. PRACTICE / Thực hành): ");
        String courseType = "1".equals(type) || "THEORY".equalsIgnoreCase(type) || "LÝ THUYẾT".equalsIgnoreCase(type)
                ? "THEORY" : "PRACTICE";

        String id = prompt("Mã môn học (ví dụ: LT101, TH101): ");
        String name = prompt("Tên môn học: ");
        String creditsStr = prompt("Số tín chỉ (nguyên từ 1 đến 6): ");

        try {
            int credits = CourseManager.parseAndValidateCredits(creditsStr);
            Course course = CourseManager.createCourse(courseType, id, name, credits);
            this.courseManager.addCourse(course);
            System.out.println("Thêm môn học thành công: " + course);
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void updateCredits() {
        System.out.println("\n--- Cập nhật số tín chỉ ---");
        String id = prompt("Nhập mã môn cần cập nhật: ");
        Course c = this.courseManager.findCourse(id);
        if (c == null) {
            System.out.println("Không tìm thấy môn học có mã: " + id);
            return;
        }
        System.out.printf("Môn học hiện tại: %s (Tín chỉ hiện tại: %d)\n", c.getCourseName(), c.getCredits());
        String creditsStr = prompt("Nhập số tín chỉ mới (1-6): ");
        try {
            int newCredits = CourseManager.parseAndValidateCredits(creditsStr);
            this.courseManager.updateCourseCredits(c.getCourseId(), newCredits);
            System.out.println("Cập nhật số tín chỉ thành công!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void searchCourse() {
        String id = prompt("Nhập mã môn cần tìm: ");
        Course c = this.courseManager.findCourse(id);
        if (c == null) {
            System.out.println("Không tìm thấy môn học có mã: " + id);
        } else {
            System.out.println("Kết quả tìm kiếm: " + c);
        }
    }

    private void handleStudentSubMenu() {
        System.out.println("\n--- [2] QUẢN LÝ SINH VIÊN ---");
        System.out.println("1. Xem danh sách sinh viên");
        System.out.println("2. Thêm sinh viên mới");
        System.out.println("3. Tìm kiếm sinh viên theo MSSV");
        System.out.println("4. Xóa sinh viên");
        System.out.println("0. Quay lại menu chính");

        String choice = prompt("Chọn thao tác: ");
        switch (choice) {
            case "1":
                displayAllStudents();
                break;
            case "2":
                addNewStudent();
                break;
            case "3":
                searchStudent();
                break;
            case "4":
                deleteStudent();
=======
        }
    }

    // ==================== 2. QUẢN LÝ MÔN HỌC ====================

    private void handleCourseManagement() {
        System.out.println("\n--- [2] QUẢN LÝ MÔN HỌC ---");
        System.out.println("1. Thêm môn học mới");
        System.out.println("2. Tìm môn học theo mã môn");
        System.out.println("3. Xem danh sách môn học");
        System.out.println("0. Quay lại menu chính");

        String sub = this.readLine("Chọn: ").trim();
        switch (sub) {
            case "1":
                System.out.println("Chọn loại môn học:");
                System.out.println("1. Lý thuyết (Lý thuyết: 0.30 QT + 0.70 CK)");
                System.out.println("2. Thực hành (Thực hành: 0.50 QT + 0.50 CK)");
                String typeChoice = this.readLine("Chọn loại (1 hoặc 2): ").trim();
                String type = "1".equals(typeChoice) ? "THEORY" : ("2".equals(typeChoice) ? "PRACTICE" : null);
                if (type == null) {
                    System.out.println("Loại môn học không hợp lệ!");
                    return;
                }

                String cId = this.readLine("Nhập mã môn học (ví dụ: LT103, PRF192): ");
                String cName = this.readLine("Nhập tên môn học: ");
                String crStr = this.readLine("Nhập số tín chỉ (nguyên từ 1 đến 6): ");

                try {
                    int credits = CourseManager.parseAndValidateCredits(crStr);
                    this.courseManager.addCourse(type, cId, cName, credits);
                    this.isModified = true;
                    System.out.println("Thêm môn học thành công!");
                } catch (Exception e) {
                    System.out.println("Lỗi: " + e.getMessage());
                }
                break;
            case "2":
                String sId = this.readLine("Nhập mã môn cần tìm: ");
                Course c = this.courseManager.findCourseById(sId);
                if (c != null) {
                    System.out.printf("=> Tìm thấy: [%s] %s - %s (%d tín chỉ)\n", 
                            c.getCourseType(), c.getCourseId(), c.getCourseName(), c.getCredits());
                } else {
                    System.out.println("=> Không tìm thấy môn học có mã: " + sId);
                }
                break;
            case "3":
                List<Course> cList = this.courseManager.getAllCourses();
                System.out.println("\nDANH SÁCH MÔN HỌC (" + cList.size() + " môn):");
                System.out.printf("%-10s | %-12s | %-32s | %-8s\n", "Mã môn", "Loại", "Tên môn học", "Tín chỉ");
                System.out.println("------------------------------------------------------------------------");
                for (Course item : cList) {
                    System.out.printf("%-10s | %-12s | %-32s | %-8d\n", 
                            item.getCourseId(), item.getCourseType(), item.getCourseName(), item.getCredits());
                }
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
<<<<<<< HEAD
                break;
        }
    }

    private void displayAllStudents() {
        List<Student> students = this.studentManager.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("Danh sách sinh viên đang trống.");
            return;
        }
        System.out.println("\n---------------------------------------------------------");
        System.out.printf("%-12s | %-35s\n", "MSSV", "HỌ VÀ TÊN");
        System.out.println("---------------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-12s | %-35s\n", s.getStudentId(), s.getFullName());
        }
        System.out.println("---------------------------------------------------------");
        System.out.printf("Tổng số sinh viên: %d\n", students.size());
    }

    private void addNewStudent() {
        System.out.println("\n--- Thêm sinh viên mới ---");
        String id = prompt("Nhập MSSV (ví dụ: SE180001): ");
        String name = prompt("Nhập họ và tên: ");
        try {
            this.studentManager.addStudent(id, name);
            System.out.println("Thêm sinh viên thành công!");
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void searchStudent() {
        String id = prompt("Nhập MSSV cần tìm: ");
        Student s = this.studentManager.findStudent(id);
        if (s == null) {
            System.out.println("Không tìm thấy sinh viên có MSSV: " + id);
        } else {
            System.out.printf("Tìm thấy sinh viên: %s - %s\n", s.getStudentId(), s.getFullName());
        }
    }

    private void deleteStudent() {
        System.out.println("\n--- Xóa sinh viên ---");
        String id = prompt("Nhập MSSV cần xóa: ");
        try {
            boolean ok = this.studentManager.deleteStudent(id, this.gradeManager.getAllRecords());
            if (ok) {
                System.out.println("Xóa sinh viên thành công!");
            } else {
                System.out.println("Không tìm thấy sinh viên để xóa.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void handleGradeSubMenu() {
        System.out.println("\n--- [3] QUẢN LÝ ĐIỂM SỐ ---");
        System.out.println("1. Nhập / Cập nhật điểm cho cặp Sinh viên - Môn học");
        System.out.println("2. Xem danh sách kết quả theo môn học");
        System.out.println("3. Xem danh sách tất cả các kết quả điểm");
        System.out.println("0. Quay lại menu chính");

        String choice = prompt("Chọn thao tác: ");
        switch (choice) {
            case "1":
                inputOrUpdateGrade();
                break;
            case "2":
                viewGradesByCourse();
                break;
            case "3":
                viewAllGrades();
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
                break;
        }
    }

    private void inputOrUpdateGrade() {
        System.out.println("\n--- Nhập / Cập nhật điểm ---");
        String sId = prompt("Nhập MSSV: ");
        Student student = this.studentManager.findStudent(sId);
        if (student == null) {
            System.out.println("Lỗi: Sinh viên chưa tồn tại trong hệ thống. Vui lòng thêm sinh viên trước!");
            return;
        }

        String cId = prompt("Nhập mã môn học: ");
        Course course = this.courseManager.findCourse(cId);
        if (course == null) {
            System.out.println("Lỗi: Môn học chưa tồn tại trong hệ thống. Vui lòng thêm môn học trước!");
            return;
        }

        GradeRecord current = this.gradeManager.findRecord(student.getStudentId(), course.getCourseId());
        if (current != null) {
            System.out.printf(Locale.US, "Bản ghi điểm đã có: QT=%s, CK=%s, TK=%s\n",
                    current.getProcessScore() == null ? "CHƯA CÓ" : String.format(Locale.US, "%.2f", current.getProcessScore()),
                    current.getExamScore() == null ? "CHƯA CÓ" : String.format(Locale.US, "%.2f", current.getExamScore()),
                    current.getFinalScore() == null ? GradeManager.CHUA_DU_DIEM : String.format(Locale.US, "%.2f", current.getFinalScore()));
            System.out.println("(*) Hướng dẫn: Nhập điểm mới [0-10], gõ 'K' để giữ điểm cũ, để trống hoặc nhập '-'/'EMPTY' để đặt lại thành thiếu.");
        } else {
            System.out.println("(*) Nhập điểm (0.0 đến 10.0), để trống / gõ 'EMPTY' nếu chưa có điểm:");
        }

        String pPrompt = (current != null && current.getProcessScore() != null)
                ? String.format(Locale.US, "Nhập điểm quá trình [Hiện tại: %.2f | 'K' giữ nguyên | 'EMPTY'/'-' để trống]: ", current.getProcessScore())
                : "Nhập điểm quá trình (0.0 - 10.0, để trống nếu chưa có): ";
        String ePrompt = (current != null && current.getExamScore() != null)
                ? String.format(Locale.US, "Nhập điểm cuối kỳ [Hiện tại: %.2f | 'K' giữ nguyên | 'EMPTY'/'-' để trống]: ", current.getExamScore())
                : "Nhập điểm cuối kỳ (0.0 - 10.0, để trống nếu chưa có): ";

        String pStr = prompt(pPrompt).trim();
        String eStr = prompt(ePrompt).trim();

        try {
            Double pScore = (current != null && "K".equalsIgnoreCase(pStr)) ? current.getProcessScore() : GradeManager.parseScore(pStr);
            Double eScore = (current != null && "K".equalsIgnoreCase(eStr)) ? current.getExamScore() : GradeManager.parseScore(eStr);

            GradeRecord saved = this.gradeManager.addOrUpdateGrade(student, course, pScore, eScore);
            System.out.println("Ghi nhận điểm thành công: " + saved);
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void viewGradesByCourse() {
        String cId = prompt("Nhập mã môn học: ");
        Course course = this.courseManager.findCourse(cId);
        if (course == null) {
            System.out.println("Không tìm thấy môn học có mã: " + cId);
=======
        }
    }

    // ==================== 3. TẠO / CẬP NHẬT KẾT QUẢ ====================

    private void handleCreateOrUpdateGrade() {
        System.out.println("\n--- [3] TẠO / CẬP NHẬT KẾT QUẢ HỌC TẬP ---");
        String sId = this.readLine("Nhập MSSV: ");
        Student student = this.studentManager.findStudent(sId);
        if (student == null) {
            System.out.println("Lỗi: Không tìm thấy sinh viên có MSSV '" + sId + "'!");
            return;
        }

        String cId = this.readLine("Nhập Mã môn học: ");
        Course course = this.courseManager.findCourseById(cId);
        if (course == null) {
            System.out.println("Lỗi: Không tìm thấy môn học có mã '" + cId + "'!");
            return;
        }

        System.out.printf("Sinh viên: %s - %s\n", student.getStudentId(), student.getFullName());
        System.out.printf("Môn học: [%s] %s - %s (%d TC)\n", course.getCourseType(), course.getCourseId(), course.getCourseName(), course.getCredits());

        GradeRecord current = this.gradeManager.findRecord(student.getStudentId(), course.getCourseId());
        if (current != null) {
            System.out.println("(*) Cặp SV-Môn này ĐÃ CÓ KẾT QUẢ:");
            System.out.printf("    Điểm quá trình hiện tại: %s\n", 
                    (current.getProcessScore() != null ? current.getProcessScore() : "CHƯA CÓ"));
            System.out.printf("    Điểm cuối kỳ hiện tại:   %s\n", 
                    (current.getExamScore() != null ? current.getExamScore() : "CHƯA CÓ"));
            System.out.println("(*) Nhập điểm mới, hoặc gõ 'K' để giữ điểm cũ, hoặc gõ 'EMPTY' để đặt lại thành thiếu:");
        } else {
            System.out.println("(*) Nhập điểm (0.0 đến 10.0), hoặc để trống / gõ 'EMPTY' nếu chưa có điểm:");
        }

        Double newProcessScore = this.promptForScore("Điểm quá trình", (current != null ? current.getProcessScore() : null));
        Double newExamScore = this.promptForScore("Điểm cuối kỳ", (current != null ? current.getExamScore() : null));

        try {
            this.gradeManager.createOrUpdateRecord(student, course, newProcessScore, newExamScore);
            this.isModified = true;
            System.out.println("Cập nhật kết quả thành công (không tạo dòng trùng lặp)!");
        } catch (Exception e) {
            System.out.println("Lỗi khi lưu kết quả: " + e.getMessage());
        }
    }

    private Double promptForScore(String label, Double currentVal) {
        while (true) {
            String prompt = (currentVal != null) 
                    ? String.format("%s [Hiện tại: %.2f | 'K' giữ nguyên | 'EMPTY' để trống]: ", label, currentVal)
                    : String.format("%s [Enter hoặc 'EMPTY' nếu thiếu]: ", label);
            String input = this.readLine(prompt).trim();

            if (input.equalsIgnoreCase("K") && currentVal != null) {
                return currentVal;
            }
            if (input.isEmpty() || input.equalsIgnoreCase("EMPTY") || input.equalsIgnoreCase("NULL") || input.equals("-")) {
                return null;
            }

            try {
                return GradeManager.parseScore(input);
            } catch (Exception e) {
                System.out.println("  => Lỗi: " + e.getMessage() + ". Vui lòng nhập lại!");
            }
        }
    }

    // ==================== 4. XEM KẾT QUẢ MÔN HỌC ====================

    private void handleViewCourseGrades() {
        System.out.println("\n--- [4] XEM KẾT QUẢ MÔN HỌC ---");
        String cId = this.readLine("Nhập mã môn học: ");
        Course course = this.courseManager.findCourseById(cId);
        if (course == null) {
            System.out.println("Lỗi: Không tìm thấy môn học có mã '" + cId + "'!");
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
            return;
        }

        List<GradeRecord> records = this.gradeManager.getRecordsByCourse(course.getCourseId());
<<<<<<< HEAD
        System.out.printf("\n--- Kết quả môn học: %s - %s (%s) ---\n",
                course.getCourseId(), course.getCourseName(), course.getCourseType());
        if (records.isEmpty()) {
            System.out.println("Chưa có sinh viên nào có kết quả môn học này.");
            return;
        }

        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-25s | %-10s | %-10s | %-15s | %-15s\n",
                "MSSV", "HỌ VÀ TÊN", "ĐIỂM QT", "ĐIỂM CK", "ĐIỂM TK", "TRẠNG THÁI");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (GradeRecord r : records) {
            String pStr = r.getProcessScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getProcessScore());
            String eStr = r.getExamScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getExamScore());
            Double fScore = r.getFinalScore();
            String fStr = fScore == null ? GradeManager.CHUA_DU_DIEM : String.format(Locale.US, "%.2f", fScore);
            System.out.printf("%-12s | %-25s | %-10s | %-10s | %-15s | %-15s\n",
                    r.getStudent().getStudentId(), r.getStudent().getFullName(), pStr, eStr, fStr, r.getStatus());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    private void viewAllGrades() {
        List<GradeRecord> records = this.gradeManager.getAllRecords();
        if (records.isEmpty()) {
            System.out.println("Chưa có kết quả điểm nào trong hệ thống.");
            return;
        }
        System.out.println("\n---------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-22s | %-10s | %-7s | %-7s | %-7s | %-12s\n",
                "MSSV", "HỌ TÊN", "MÃ MÔN", "ĐIỂM QT", "ĐIỂM CK", "ĐIỂM TK", "TRẠNG THÁI");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (GradeRecord r : records) {
            String pStr = r.getProcessScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getProcessScore());
            String eStr = r.getExamScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getExamScore());
            Double fScore = r.getFinalScore();
            String fStr = fScore == null ? "-" : String.format(Locale.US, "%.2f", fScore);
            System.out.printf("%-12s | %-22s | %-10s | %-7s | %-7s | %-7s | %-12s\n",
                    r.getStudent().getStudentId(), r.getStudent().getFullName(),
                    r.getCourse().getCourseId(), pStr, eStr, fStr, r.getStatus());
        }
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("Tổng số kết quả: %d\n", records.size());
    }

    private void handleTranscriptSubMenu() {
        System.out.println("\n--- [4] BẢNG ĐIỂM & ĐIỂM TRUNG BÌNH (GPA) ---");
        System.out.println("1. Xem bảng điểm chi tiết của sinh viên");
        System.out.println("2. Xuất bảng điểm sinh viên ra file CSV");
        System.out.println("3. Xuất toàn bộ kết quả điểm hệ thống ra CSV");
        System.out.println("0. Quay lại menu chính");

        String choice = prompt("Chọn thao tác: ");
        switch (choice) {
            case "1":
                viewStudentTranscript();
                break;
            case "2":
                exportStudentTranscriptCsv();
                break;
            case "3":
                exportAllCsv();
=======
        System.out.printf("\nKẾT QUẢ MÔN HỌC: [%s] %s - %s (%d tín chỉ)\n",
                course.getCourseType(), course.getCourseId(), course.getCourseName(), course.getCredits());
        if (records.isEmpty()) {
            System.out.println("Chưa có sinh viên nào có kết quả môn học này.");
            return;
        }
        System.out.println("Tổng số sinh viên có kết quả: " + records.size());
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-24s | %-10s | %-10s | %-15s | %-15s\n",
                "MSSV", "Họ và tên", "Quá trình", "Cuối kỳ", "Tổng kết", "Trạng thái");
        System.out.println("-------------------------------------------------------------------------------------------------");

        for (GradeRecord r : records) {
            Student s = r.getStudent();
            String pStr = (r.getProcessScore() != null) ? String.format(Locale.US, "%.2f", r.getProcessScore()) : "--";
            String eStr = (r.getExamScore() != null) ? String.format(Locale.US, "%.2f", r.getExamScore()) : "--";
            Double fScore = r.getFinalScore();
            String fStr = (fScore != null) ? String.format(Locale.US, "%.2f", fScore) : GradeManager.CHUA_DU_DIEM;
            String status = r.isComplete() ? "ĐỦ ĐIỂM" : "CHƯA ĐỦ ĐIỂM";

            System.out.printf("%-10s | %-24s | %-10s | %-10s | %-15s | %-15s\n",
                    s.getStudentId(), s.getFullName(), pStr, eStr, fStr, status);
        }
    }

    // ==================== 5. XEM BẢNG ĐIỂM SINH VIÊN ====================

    private void handleViewStudentTranscript() {
        System.out.println("\n--- [5] XEM BẢNG ĐIỂM SINH VIÊN ---");
        String sId = this.readLine("Nhập MSSV: ");
        Transcript transcript = this.transcriptService.getTranscript(sId);
        if (transcript == null) {
            System.out.println("Lỗi: Không tìm thấy sinh viên có MSSV '" + sId + "'!");
            return;
        }

        Student s = transcript.getStudent();
        System.out.println("\n================================ BẢNG ĐIỂM ================================");
        System.out.printf("Sinh viên: %s - %s\n", s.getStudentId(), s.getFullName());
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("%-8s | %-24s | %-4s | %-10s | %-6s | %-6s | %-8s\n",
                "Mã môn", "Tên môn học", "TC", "Loại", "QT", "CK", "Tổng kết");
        System.out.println("---------------------------------------------------------------------------");

        for (GradeRecord r : transcript.getRecords()) {
            Course c = r.getCourse();
            String pStr = (r.getProcessScore() != null) ? String.format(Locale.US, "%.2f", r.getProcessScore()) : "--";
            String eStr = (r.getExamScore() != null) ? String.format(Locale.US, "%.2f", r.getExamScore()) : "--";
            Double fScore = r.getFinalScore();
            String fStr = (fScore != null) ? String.format(Locale.US, "%.2f", fScore) : Transcript.CHUA_DU_DIEM;

            System.out.printf("%-8s | %-24s | %-4d | %-10s | %-6s | %-6s | %-8s\n",
                    c.getCourseId(), c.getCourseName(), c.getCredits(), c.getCourseType(), pStr, eStr, fStr);
        }
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("Điểm trung bình tích lũy: %s\n", transcript.getFormattedGPA());
        System.out.printf("Số môn hoàn thành đủ điểm: %d/%d môn\n",
                transcript.getCompletedCourses(), transcript.getTotalCourses());
        System.out.printf("Số tín chỉ hoàn thành:     %d/%d tín chỉ\n",
                transcript.getCompletedCredits(), transcript.getTotalCredits());
        System.out.println("===========================================================================");
    }

    // ==================== 6. XEM ĐIỂM TRUNG BÌNH CÓ TRỌNG SỐ ====================

    private void handleViewWeightedGPA() {
        System.out.println("\n--- [6] XEM ĐIỂM TRUNG BÌNH CÓ TRỌNG SỐ (GPA) ---");
        String sId = this.readLine("Nhập MSSV: ");
        Transcript transcript = this.transcriptService.getTranscript(sId);
        if (transcript == null) {
            System.out.println("Lỗi: Không tìm thấy sinh viên có MSSV '" + sId + "'!");
            return;
        }

        Student s = transcript.getStudent();
        Double rawGpa = transcript.calculateWeightedGPA();

        System.out.println("\nKẾT QUẢ TÍNH ĐIỂM TRUNG BÌNH:");
        System.out.printf("Sinh viên: %s - %s\n", s.getStudentId(), s.getFullName());
        if (rawGpa == null) {
            System.out.println("Điểm trung bình tích lũy: " + Transcript.CHUA_CO_DIEM_TB);
            System.out.println("(Lý do: Sinh viên chưa có môn học nào hoàn thành đủ cả 2 điểm thành phần)");
        } else {
            System.out.printf(Locale.US, "Điểm trung bình (làm tròn 2 chữ số): %.2f (Điểm thô: %.4f)\n", rawGpa, rawGpa);
        }
        System.out.printf("Số môn đạt đủ điểm: %d/%d môn\n", transcript.getCompletedCourses(), transcript.getTotalCourses());
        System.out.printf("Số tín chỉ hoàn thành: %d/%d tín chỉ\n", transcript.getCompletedCredits(), transcript.getTotalCredits());
    }

    // ==================== 7. XUẤT CSV ====================

    private void handleExportCsv() {
        System.out.println("\n--- [7] XUẤT BẢNG ĐIỂM RA FILE CSV (RFC 4180) ---");
        String sId = this.readLine("Nhập MSSV cần xuất bảng điểm: ");
        Transcript transcript = this.transcriptService.getTranscript(sId);
        if (transcript == null) {
            System.out.println("Lỗi: Không tìm thấy sinh viên có MSSV '" + sId + "'!");
            return;
        }

        String defaultFileName = "bang_diem_" + transcript.getStudent().getStudentId().toUpperCase() + ".csv";
        String prompt = "Nhập tên file đích [Enter để dùng '" + defaultFileName + "']: ";
        String pathInput = this.readLine(prompt).trim();
        String targetPath = pathInput.isEmpty() ? defaultFileName : pathInput;

        try {
            File file = new File(targetPath);
            CsvExporter.exportTranscriptToCsv(transcript, file);
            System.out.println("Xuất file CSV thành công!");
            System.out.println("Đường dẫn file: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Lỗi khi xuất file CSV: " + e.getMessage());
        }
    }

    // ==================== 8. QUẢN LÝ LƯU TRỮ ====================

    private void handleFileStorageMenu() {
        System.out.println("\n--- [8] QUẢN LÝ LƯU TRỮ DỮ LIỆU ---");
        System.out.println("1. Lưu toàn bộ dữ liệu ra file (" + this.dataStore.getDataFilePath() + ")");
        System.out.println("2. Tải lại toàn bộ dữ liệu từ file");
        System.out.println("3. Khôi phục dữ liệu mẫu ban đầu (Đề bài P05)");
        System.out.println("0. Quay lại menu chính");

        String sub = this.readLine("Chọn: ").trim();
        switch (sub) {
            case "1":
                try {
                    this.dataStore.saveData(this.studentManager, this.courseManager, this.gradeManager);
                    this.isModified = false;
                    System.out.println("Lưu dữ liệu thành công ra file: " + this.dataStore.getDataFilePath());
                } catch (IOException e) {
                    System.err.println("LỖI LƯU FILE: " + e.getMessage());
                    System.out.println("Dữ liệu trong phiên làm việc hiện tại vẫn an toàn, bạn có thể thử lưu lại!");
                }
                break;
            case "2":
                try {
                    boolean ok = this.dataStore.loadData(this.studentManager, this.courseManager, this.gradeManager);
                    if (ok) {
                        this.isModified = false;
                        System.out.printf("Tải dữ liệu thành công: %d sinh viên, %d môn học, %d kết quả điểm.\n",
                                this.studentManager.getStudentCount(),
                                this.courseManager.getCourseCount(),
                                this.gradeManager.getRecordCount());
                    } else {
                        System.out.println("File dữ liệu '" + this.dataStore.getDataFilePath() + "' chưa tồn tại.");
                    }
                } catch (Exception e) {
                    System.err.println("LỖI NẠP FILE: " + e.getMessage());
                    System.out.println("Hệ thống đã hủy bỏ thao tác nạp để bảo vệ dữ liệu bộ nhớ.");
                }
                break;
            case "3":
                String confirm = this.readLine("Bạn có chắc chắn muốn nạp lại dữ liệu mẫu ban đầu? (y/n): ").trim();
                if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
                    FileDataStore.initializeSampleData(this.studentManager, this.courseManager, this.gradeManager);
                    this.isModified = true;
                    System.out.println("Đã khôi phục dữ liệu mẫu ban đầu (An & Bình, 3 môn, 3 kết quả của An).");
                }
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
<<<<<<< HEAD
                break;
        }
    }

    private void viewStudentTranscript() {
        String sId = prompt("Nhập MSSV cần xem bảng điểm: ");
        this.transcriptService.printTranscript(sId);
    }

    private void exportStudentTranscriptCsv() {
        String sId = prompt("Nhập MSSV cần xuất bảng điểm: ");
        Transcript t = this.transcriptService.generateTranscript(sId);
        if (t == null) {
            System.out.println("Không tìm thấy sinh viên có mã: " + sId);
            return;
        }
        String path = prompt("Nhập đường dẫn lưu file CSV (ví dụ: data/bang_diem_" + sId + ".csv): ");
        if (path.trim().isEmpty()) {
            path = "data/bang_diem_" + sId + ".csv";
        }
        try {
            CsvExporter.exportTranscriptToCsv(t, path);
            System.out.println("Xuất file CSV thành công tại: " + path);
        } catch (IOException e) {
            System.out.println("Lỗi khi xuất file: " + e.getMessage());
        }
    }

    private void exportAllCsv() {
        String path = prompt("Nhập đường dẫn file CSV (mặc định: data/toan_bo_diem.csv): ");
        if (path.trim().isEmpty()) {
            path = "data/toan_bo_diem.csv";
        }
        try {
            CsvExporter.exportAllRecordsToCsv(this.gradeManager.getAllRecords(), path);
            System.out.println("Xuất file CSV thành công tại: " + path);
        } catch (IOException e) {
            System.out.println("Lỗi khi xuất file: " + e.getMessage());
        }
    }

    private void handleStorageSubMenu() {
        System.out.println("\n--- [5] LƯU TRỮ & ĐỌC DỮ LIỆU ---");
        System.out.println("1. Lưu dữ liệu hiện tại vào file");
        System.out.println("2. Đọc lại dữ liệu từ file");
        System.out.println("3. Nạp dữ liệu mẫu ban đầu (Sample Data)");
        System.out.println("0. Quay lại menu chính");

        String choice = prompt("Chọn thao tác: ");
        switch (choice) {
            case "1":
                saveDataToFile();
                break;
            case "2":
                loadDataFromFile();
                break;
            case "3":
                loadSampleData();
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
                break;
        }
    }

    private void saveDataToFile() {
        try {
            this.fileDataStore.saveData(this.courseManager, this.studentManager, this.gradeManager);
            System.out.println("Lưu dữ liệu vào thư mục 'data/' thành công!");
        } catch (IOException e) {
            System.out.println("Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }

    private void loadDataFromFile() {
        try {
            this.fileDataStore.loadData(this.courseManager, this.studentManager, this.gradeManager);
            System.out.println("Đọc dữ liệu từ file thành công!");
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file dữ liệu: " + e.getMessage());
        }
    }

    private void loadSampleData() {
        this.fileDataStore.loadSampleData(this.courseManager, this.studentManager, this.gradeManager);
        System.out.println("Nạp dữ liệu mẫu thành công (2 sinh viên, 3 môn học, 3 kết quả)!");
    }

    private String prompt(String message) {
        System.out.print(message);
        return this.scanner.nextLine();
=======
        }
    }

    // ==================== 0. THOÁT ====================

    private boolean handleExit() {
        if (this.isModified) {
            String answer = this.readLine("Dữ liệu có thay đổi chưa lưu. Bạn có muốn lưu trước khi thoát? (y/n): ").trim();
            if ("y".equalsIgnoreCase(answer) || "yes".equalsIgnoreCase(answer)) {
                try {
                    this.dataStore.saveData(this.studentManager, this.courseManager, this.gradeManager);
                    System.out.println("Đã lưu dữ liệu thành công.");
                } catch (IOException e) {
                    System.err.println("Lỗi lưu file: " + e.getMessage());
                    String force = this.readLine("Vẫn muốn thoát không lưu? (y/n): ").trim();
                    if (!"y".equalsIgnoreCase(force)) {
                        return true; // Không thoát, ở lại
                    }
                }
            }
        }
        System.out.println("Cảm ơn bạn đã sử dụng chương trình. Tạm biệt!");
        return false; // Dừng vòng lặp
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (this.scanner.hasNextLine()) {
            return this.scanner.nextLine();
        }
        return "";
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
    }
}

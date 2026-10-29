package studentgrademanager.ui;

import java.io.File;
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

    public MainMenu(CourseManager courseManager, StudentManager studentManager, GradeManager gradeManager, TranscriptService transcriptService, FileDataStore dataStore) {
        this(studentManager, courseManager, gradeManager, dataStore);
    }

    public MainMenu(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager) {
        this(studentManager, courseManager, gradeManager, new FileDataStore());
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
            }
        }
    }

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
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
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
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
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
            if (input.isEmpty() || input.equalsIgnoreCase("EMPTY") || input.equalsIgnoreCase("NULL")) {
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
            return;
        }

        List<GradeRecord> records = this.gradeManager.getRecordsByCourse(course.getCourseId());
        System.out.printf("\nKẾT QUẢ MÔN HỌC: [%s] %s - %s (%d tín chỉ)\n",
                course.getCourseType(), course.getCourseId(), course.getCourseName(), course.getCredits());
        System.out.println("Tổng số sinh viên có kết quả: " + records.size());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-24s | %-10s | %-10s | %-10s | %-15s\n",
                "MSSV", "Họ và tên", "Quá trình", "Cuối kỳ", "Tổng kết", "Trạng thái");
        System.out.println("------------------------------------------------------------------------------------------");

        for (GradeRecord r : records) {
            Student s = r.getStudent();
            String pStr = (r.getProcessScore() != null) ? String.format(Locale.US, "%.2f", r.getProcessScore()) : "--";
            String eStr = (r.getExamScore() != null) ? String.format(Locale.US, "%.2f", r.getExamScore()) : "--";
            Double fScore = r.getFinalScore();
            String fStr = (fScore != null) ? String.format(Locale.US, "%.2f", fScore) : Transcript.CHUA_DU_DIEM;
            String status = r.isComplete() ? "ĐỦ ĐIỂM" : "CHƯA ĐỦ ĐIỂM";

            System.out.printf("%-10s | %-24s | %-10s | %-10s | %-10s | %-15s\n",
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
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
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
    }
}

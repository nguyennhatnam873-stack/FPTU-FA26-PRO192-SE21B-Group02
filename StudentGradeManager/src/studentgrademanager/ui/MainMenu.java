package studentgrademanager.ui;

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
            }
        }
    }

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
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
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
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
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
            System.out.printf("Bản ghi điểm đã có: QT=%s, CK=%s, TK=%s\n",
                    current.getProcessScore() == null ? "-" : current.getProcessScore(),
                    current.getExamScore() == null ? "-" : current.getExamScore(),
                    current.getFinalScore() == null ? "-" : current.getFinalScore());
            System.out.println("(Để trống hoặc nhập '-' để giữ nguyên/đặt thành thiếu điểm)");
        }

        String pStr = prompt("Nhập điểm quá trình (0.0 - 10.0, để trống nếu chưa có): ");
        String eStr = prompt("Nhập điểm cuối kỳ (0.0 - 10.0, để trống nếu chưa có): ");

        try {
            Double pScore = GradeManager.parseAndValidateScore(pStr);
            Double eScore = GradeManager.parseAndValidateScore(eStr);

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
            return;
        }

        List<GradeRecord> records = this.gradeManager.getRecordsByCourse(course.getCourseId());
        System.out.printf("\n--- Kết quả môn học: %s - %s (%s) ---\n",
                course.getCourseId(), course.getCourseName(), course.getCourseType());
        if (records.isEmpty()) {
            System.out.println("Chưa có sinh viên nào có kết quả môn học này.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-25s | %-7s | %-7s | %-7s | %-12s\n",
                "MSSV", "HỌ VÀ TÊN", "ĐIỂM QT", "ĐIỂM CK", "ĐIỂM TK", "TRẠNG THÁI");
        System.out.println("---------------------------------------------------------------------------------");
        for (GradeRecord r : records) {
            String pStr = r.getProcessScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getProcessScore());
            String eStr = r.getExamScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getExamScore());
            Double fScore = r.getFinalScore();
            String fStr = fScore == null ? "-" : String.format(Locale.US, "%.2f", fScore);
            System.out.printf("%-12s | %-25s | %-7s | %-7s | %-7s | %-12s\n",
                    r.getStudent().getStudentId(), r.getStudent().getFullName(), pStr, eStr, fStr, r.getStatus());
        }
        System.out.println("---------------------------------------------------------------------------------");
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
                break;
            case "0":
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
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
    }
}

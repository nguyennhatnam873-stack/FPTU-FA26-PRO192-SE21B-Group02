/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentgrademanager.ui;

<<<<<<< HEAD
=======
import java.io.File;
import java.io.IOException;
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
import java.util.List;
import java.util.Scanner;

import studentgrademanager.models.Course;
import studentgrademanager.models.Student;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.StudentManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.TranscriptService;
import studentgrademanager.storage.CsvExporter;

import java.io.File;
import java.io.IOException;

import studentgrademanager.storage.AppData;
import studentgrademanager.storage.FileDataStore;

<<<<<<< HEAD
=======

>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final StudentManager studentManager;
    private final CourseManager courseManager;
    private final GradeManager gradeManager;
    private final TranscriptService transcriptService;
    private final CsvExporter csvExporter = new CsvExporter();

    private final FileDataStore dataStore = new FileDataStore();
    private final String dataPath = "data/student-grade-data.dat";

    public MainMenu(StudentManager studentManager,
            CourseManager courseManager,
            GradeManager gradeManager) {
        this.studentManager = studentManager;
        this.courseManager = courseManager;
        this.gradeManager = gradeManager;
<<<<<<< HEAD
        this.transcriptService
                = new TranscriptService(gradeManager);
    }

    public void start() {
        loadData();
=======
        this.dataStore = (dataStore != null) ? dataStore : new FileDataStore();
        this.transcriptService = new TranscriptService(this.studentManager, this.gradeManager);
        this.scanner = new Scanner(System.in);


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


    public void start() {
        System.out.println("======================================================================");
        System.out.println("   CHƯƠNG TRÌNH QUẢN LÝ SINH VIÊN VÀ BẢNG ĐIỂM (PRO192 - P05)");
        System.out.println("======================================================================");


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

>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
        boolean running = true;

        while (running) {
<<<<<<< HEAD
            System.out.println("\n===== QUAN LY SINH VIEN VA BANG DIEM =====");
            System.out.println("1. Them sinh vien");
            System.out.println("2. Xem danh sach sinh vien");
            System.out.println("3. Them mon hoc");
            System.out.println("4. Xem danh sach mon hoc");
            System.out.println("5. Nhap / cap nhat diem");
            System.out.println("6. Xem bang diem sinh vien");
            System.out.println("7. Xuat diem ra CSV");
            System.out.println("0. Thoat");
            System.out.print("Chon: ");

            String choice = scanner.nextLine().trim();
=======
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
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04

            try {
                switch (choice) {
                    case "1":
                        addStudent();
                        break;
                    case "2":
                        showStudents();
                        break;
                    case "3":
                        addCourse();
                        break;
                    case "4":
                        showCourses();
                        break;
                    case "5":
                        enterGrade();
                        break;
                    case "6":
                        showTranscript();
                        break;
                    case "7":
                        exportCsv();
                        break;

                    case "0":
                        saveData();
                        running = false;
                        System.out.println("Da thoat chuong trinh.");
                        break;

                    default:
                        System.out.println("Lua chon khong hop le.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Loi: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Khong the thuc hien: "
                        + e.getMessage());
            }
        }
    }

<<<<<<< HEAD
    private void addStudent() {
        System.out.print("Ma sinh vien: ");
        String id = scanner.nextLine();

        System.out.print("Ho ten: ");
        String name = scanner.nextLine();

        studentManager.addStudent(new Student(id, name));
        System.out.println("Them sinh vien thanh cong.");
    }

    private void showStudents() {
        List<Student> students = studentManager.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("Chua co sinh vien.");
            return;
        }

        for (Student student : students) {
            System.out.println(student);
        }
    }

    private void addCourse() {
        System.out.print("Ma mon hoc: ");
        String code = scanner.nextLine();
=======


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


>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04

        System.out.print("Ten mon hoc: ");
        String name = scanner.nextLine();

        int credits = readInt("So tin chi: ");

        System.out.println("1. Ly thuyet");
        System.out.println("2. Thuc hanh");
        System.out.print("Loai mon: ");
        String type = scanner.nextLine().trim();

<<<<<<< HEAD
        if ("1".equals(type)) {
            courseManager.addTheoryCourse(code, name, credits);
        } else if ("2".equals(type)) {
            courseManager.addPracticeCourse(code, name, credits);
        } else {
            System.out.println("Loai mon khong hop le.");
=======
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



    private void handleExportCsv() {
        System.out.println("\n--- [7] XUẤT BẢNG ĐIỂM RA FILE CSV (RFC 4180) ---");
        String sId = this.readLine("Nhập MSSV cần xuất bảng điểm: ");
        Transcript transcript = this.transcriptService.getTranscript(sId);
        if (transcript == null) {
            System.out.println("Lỗi: Không tìm thấy sinh viên có MSSV '" + sId + "'!");
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
            return;
        }

        System.out.println("Them mon hoc thanh cong.");
    }

<<<<<<< HEAD
    private void showCourses() {
        List<Course> courses = courseManager.getAllCourses();

        if (courses.isEmpty()) {
            System.out.println("Chua co mon hoc.");
            return;
        }

        for (Course course : courses) {
            System.out.println(
                    course.getCode() + " | "
                    + course.getName() + " | "
                    + course.getCredits() + " TC | "
                    + course.getCourseType());
        }
    }

    private void enterGrade() {
        System.out.print("Ma sinh vien: ");
        String studentId = scanner.nextLine().trim();

        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("Khong tim thay sinh vien.");
            return;
        }

        System.out.print("Ma mon hoc: ");
        String courseCode = scanner.nextLine().trim();

        Course course = courseManager.findCourse(courseCode);

        if (course == null) {
            System.out.println("Khong tim thay mon hoc.");
            return;
        }

        Double process = readOptionalScore("Diem qua trinh (bo trong neu chua co): ");
        Double exam = readOptionalScore("Diem cuoi ky (bo trong neu chua co): ");

        GradeRecord record = gradeManager.addOrUpdateGrade(
                student, course, process, exam);

        System.out.println("Da cap nhat diem.");
        System.out.println(record);
    }

    private void showTranscript() {
        System.out.print("Ma sinh vien: ");
        String id = scanner.nextLine().trim();

        transcriptService.displayTranscript(id);
    }

    private void exportCsv() throws Exception {
        String path = "data/grades.csv";

        csvExporter.exportGrades(
                gradeManager.getAllRecords(), path);

        System.out.println("Da xuat file: " + path);
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
=======


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
                        return true; 
                    }
                }
            }
        }
        System.out.println("Cảm ơn bạn đã sử dụng chương trình. Tạm biệt!");
        return false; 
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (this.scanner.hasNextLine()) {
            return this.scanner.nextLine();
        }
        return "";
>>>>>>> 3334af96dba2628c53fb827495ab5c6fe1d14b04
    }

    private Double readOptionalScore(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();

        if (value.isEmpty()) {
            return null;
        }

        return Double.parseDouble(value);
    }

    private void saveData() {
        AppData data = new AppData();

        data.getStudents().addAll(studentManager.getAllStudents());
        data.getCourses().addAll(courseManager.getAllCourses());
        data.getGrades().addAll(gradeManager.getAllRecords());

        try {
            dataStore.save(data, dataPath);
            System.out.println("Da luu du lieu thanh cong.");
        } catch (IOException e) {
            System.out.println("Loi khi luu du lieu: " + e.getMessage());
        }
    }

    private void loadData() {
        if (!dataStore.exists(dataPath)) {
            System.out.println("Chua co du lieu cu. Bat dau du lieu moi.");
            return;
        }

        try {
            AppData data = (AppData) dataStore.load(dataPath);

            studentManager.loadStudents(data.getStudents());
            courseManager.loadCourses(data.getCourses());
            gradeManager.loadGrades(data.getGrades());

            System.out.println("Da doc du lieu cu thanh cong.");
        } catch (IOException | ClassNotFoundException
                | ClassCastException e) {
            System.out.println("Loi khi doc du lieu: " + e.getMessage());
        }
    }

}

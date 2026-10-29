/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentgrademanager.ui;

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
        this.transcriptService
                = new TranscriptService(gradeManager);
    }

    public void start() {
        loadData();
        boolean running = true;

        while (running) {
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

        System.out.print("Ten mon hoc: ");
        String name = scanner.nextLine();

        int credits = readInt("So tin chi: ");

        System.out.println("1. Ly thuyet");
        System.out.println("2. Thuc hanh");
        System.out.print("Loai mon: ");
        String type = scanner.nextLine().trim();

        if ("1".equals(type)) {
            courseManager.addTheoryCourse(code, name, credits);
        } else if ("2".equals(type)) {
            courseManager.addPracticeCourse(code, name, credits);
        } else {
            System.out.println("Loai mon khong hop le.");
            return;
        }

        System.out.println("Them mon hoc thanh cong.");
    }

    private void showCourses() {
        List<Course> courses = courseManager.getAllCourses();

        if (courses.isEmpty()) {
            System.out.println("Chua co mon hoc.");
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

    private void enterGrade() {
        System.out.print("Ma sinh vien: ");
        String studentId = scanner.nextLine().trim();

        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("Khong tim thay sinh vien.");
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

        Course course = courseManager.findCourse(courseCode);

        if (course == null) {
            System.out.println("Khong tim thay mon hoc.");
            return;
        }

        List<GradeRecord> records = this.gradeManager.getRecordsByCourse(course.getCourseId());
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

        GradeRecord record = gradeManager.addOrUpdateGrade(
                student, course, process, exam);

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

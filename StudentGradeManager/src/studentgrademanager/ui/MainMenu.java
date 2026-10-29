/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentgrademanager.ui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import studentgrademanager.models.Course;
import studentgrademanager.models.Student;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.StudentManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.TranscriptService;
import studentgrademanager.storage.AppData;
import studentgrademanager.storage.CsvExporter;
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
        this.transcriptService = new TranscriptService(gradeManager);
    }

    public void start() {
        this.loadData();
        boolean running = true;

        while (running) {
            System.out.println("\n===== QUAN LY SINH VIEN VA BANG DIEM =====");
            System.out.println("1. Them sinh vien");
            System.out.println("2. Xem danh sach sinh vien");
            System.out.println("3. Them mon hoc");
            System.out.println("4. Xem danh sach mon hoc");
            System.out.println("5. Nhap / cap nhat diem");
            System.out.println("6. Xem ket qua theo mon hoc");
            System.out.println("7. Xem bang diem sinh vien");
            System.out.println("8. Xuat diem ra CSV");
            System.out.println("0. Thoat");
            System.out.print("Chon: ");

            String choice = this.scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        this.addStudent();
                        break;
                    case "2":
                        this.showStudents();
                        break;
                    case "3":
                        this.addCourse();
                        break;
                    case "4":
                        this.showCourses();
                        break;
                    case "5":
                        this.enterGrade();
                        break;
                    case "6":
                        this.viewGradesByCourse();
                        break;
                    case "7":
                        this.showTranscript();
                        break;
                    case "8":
                        this.exportCsv();
                        break;

                    case "0":
                        this.saveData();
                        running = false;
                        System.out.println("Da thoat chuong trinh.");
                        break;

                    default:
                        System.out.println("Lua chon khong hop le.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Loi: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Khong the thuc hien: " + e.getMessage());
            }
        }
    }

    private void addStudent() {
        System.out.print("Ma sinh vien: ");
        String id = this.scanner.nextLine();

        System.out.print("Ho ten: ");
        String name = this.scanner.nextLine();

        this.studentManager.addStudent(new Student(id, name));
        System.out.println("Them sinh vien thanh cong.");
    }

    private void showStudents() {
        List<Student> students = this.studentManager.getAllStudents();

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
        String code = this.scanner.nextLine();

        System.out.print("Ten mon hoc: ");
        String name = this.scanner.nextLine();

        int credits = this.readInt("So tin chi: ");

        System.out.println("1. Ly thuyet");
        System.out.println("2. Thuc hanh");
        System.out.print("Loai mon: ");
        String type = this.scanner.nextLine().trim();

        if ("1".equals(type)) {
            this.courseManager.addTheoryCourse(code, name, credits);
        } else if ("2".equals(type)) {
            this.courseManager.addPracticeCourse(code, name, credits);
        } else {
            System.out.println("Loai mon khong hop le.");
            return;
        }

        System.out.println("Them mon hoc thanh cong.");
    }

    private void showCourses() {
        List<Course> courses = this.courseManager.getAllCourses();

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
        String studentId = this.scanner.nextLine().trim();

        Student student = this.studentManager.findStudent(studentId);
        if (student == null) {
            System.out.println("Khong tim thay sinh vien.");
            return;
        }

        System.out.print("Ma mon hoc: ");
        String courseCode = this.scanner.nextLine().trim();

        Course course = this.courseManager.findCourse(courseCode);
        if (course == null) {
            System.out.println("Khong tim thay mon hoc.");
            return;
        }

        GradeRecord current = this.gradeManager.findRecord(student.getStudentId(), course.getCode());
        if (current != null) {
            System.out.printf(Locale.US, "Ban ghi diem da co: QT=%s, CK=%s, TK=%s\n",
                    current.getProcessScore() == null ? "CHUA CO" : String.format(Locale.US, "%.2f", current.getProcessScore()),
                    current.getExamScore() == null ? "CHUA CO" : String.format(Locale.US, "%.2f", current.getExamScore()),
                    current.getFinalGrade() == null ? GradeManager.CHUA_DU_DIEM : String.format(Locale.US, "%.2f", current.getFinalGrade()));
            System.out.println("Huong dan: Nhap diem moi [0-10], go 'K' de giu diem cu, de trong hoac nhap '-' hoac 'EMPTY' de dat lai thanh thieu.");
        } else {
            System.out.println("Nhap diem (0.0 den 10.0), de trong hoac go 'EMPTY' neu chua co diem:");
        }

        String pPrompt = (current != null && current.getProcessScore() != null)
                ? String.format(Locale.US, "Nhap diem qua trinh [Hien tai: %.2f | 'K' giu nguyen | 'EMPTY'/'-' de trong]: ", current.getProcessScore())
                : "Nhap diem qua trinh (0.0 - 10.0, de trong neu chua co): ";
        String ePrompt = (current != null && current.getExamScore() != null)
                ? String.format(Locale.US, "Nhap diem cuoi ky [Hien tai: %.2f | 'K' giu nguyen | 'EMPTY'/'-' de trong]: ", current.getExamScore())
                : "Nhap diem cuoi ky (0.0 - 10.0, de trong neu chua co): ";

        System.out.print(pPrompt);
        String pStr = this.scanner.nextLine().trim();
        System.out.print(ePrompt);
        String eStr = this.scanner.nextLine().trim();

        try {
            Double pScore = (current != null && "K".equalsIgnoreCase(pStr)) ? current.getProcessScore() : GradeManager.parseScore(pStr);
            Double eScore = (current != null && "K".equalsIgnoreCase(eStr)) ? current.getExamScore() : GradeManager.parseScore(eStr);

            GradeRecord saved = this.gradeManager.createOrUpdateRecord(student, course, pScore, eScore);
            System.out.println("Ghi nhan diem thanh cong: " + saved);
        } catch (IllegalArgumentException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }

    private void viewGradesByCourse() {
        System.out.print("Ma mon hoc: ");
        String courseCode = this.scanner.nextLine().trim();

        Course course = this.courseManager.findCourse(courseCode);
        if (course == null) {
            System.out.println("Khong tim thay mon hoc.");
            return;
        }

        List<GradeRecord> records = this.gradeManager.getRecordsByCourse(course.getCode());
        System.out.printf("\n--- Ket qua mon hoc: %s - %s (%s) ---\n",
                course.getCode(), course.getName(), course.getCourseType());
        if (records.isEmpty()) {
            System.out.println("Chua co sinh vien nao co ket qua mon hoc nay.");
            return;
        }

        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-25s | %-10s | %-10s | %-15s | %-15s\n",
                "MSSV", "HO VA TEN", "DIEM QT", "DIEM CK", "DIEM TK", "TRANG THAI");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (GradeRecord r : records) {
            String pStr = r.getProcessScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getProcessScore());
            String eStr = r.getExamScore() == null ? "-" : String.format(Locale.US, "%.2f", r.getExamScore());
            Double fScore = r.getFinalGrade();
            String fStr = fScore == null ? GradeManager.CHUA_DU_DIEM : String.format(Locale.US, "%.2f", fScore);
            String status = r.hasCompleteScores() ? GradeManager.DU_DIEM : GradeManager.CHUA_DU_DIEM;
            System.out.printf("%-12s | %-25s | %-10s | %-10s | %-15s | %-15s\n",
                    r.getStudent().getStudentId(), r.getStudent().getFullName(), pStr, eStr, fStr, status);
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    private void showTranscript() {
        System.out.print("Ma sinh vien: ");
        String id = this.scanner.nextLine().trim();

        this.transcriptService.displayTranscript(id);
    }

    private void exportCsv() throws Exception {
        String path = "data/grades.csv";

        this.csvExporter.exportGrades(
                this.gradeManager.getAllRecords(), path);

        System.out.println("Da xuat file: " + path);
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(this.scanner.nextLine().trim());
    }

    private Double readOptionalScore(String prompt) {
        System.out.print(prompt);
        String value = this.scanner.nextLine().trim();

        if (value.isEmpty()) {
            return null;
        }

        return Double.parseDouble(value);
    }

    private void saveData() {
        AppData data = new AppData();

        data.getStudents().addAll(this.studentManager.getAllStudents());
        data.getCourses().addAll(this.courseManager.getAllCourses());
        data.getGrades().addAll(this.gradeManager.getAllRecords());

        try {
            this.dataStore.save(data, this.dataPath);
            System.out.println("Da luu du lieu thanh cong.");
        } catch (IOException e) {
            System.out.println("Loi khi luu du lieu: " + e.getMessage());
        }
    }

    private void loadData() {
        if (!this.dataStore.exists(this.dataPath)) {
            System.out.println("Chua co du lieu cu. Bat dau du lieu moi.");
            return;
        }

        try {
            AppData data = (AppData) this.dataStore.load(this.dataPath);

            this.studentManager.loadStudents(data.getStudents());
            this.courseManager.loadCourses(data.getCourses());
            this.gradeManager.loadGrades(data.getGrades());

            System.out.println("Da doc du lieu cu thanh cong.");
        } catch (IOException | ClassNotFoundException
                | ClassCastException e) {
            System.out.println("Loi khi doc du lieu: " + e.getMessage());
        }
    }
}

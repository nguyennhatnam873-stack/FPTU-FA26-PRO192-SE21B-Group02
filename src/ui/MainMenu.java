package ui;

import java.util.List;
import java.util.Scanner;
import manager.CourseManager;
import manager.GradeManager;
import manager.StudentManager;
import model.Course;
import model.GradeRecord;
import model.PracticeCourse;
import model.Student;
import model.TheoryCourse;
import model.Transcript;
import service.FileDataStore;
import service.TranscriptService;

/**
 * Lop MainMenu: Giao dien Console dieu huong chuong trinh.
 * Phu trach chinh: Nguoi 5 (Menu Console) va tich hop luong cua ca 5 thanh vien.
 */
public class MainMenu {
    private StudentManager studentManager;
    private CourseManager courseManager;
    private GradeManager gradeManager;
    private TranscriptService transcriptService;
    private FileDataStore fileDataStore;
    private Scanner scanner;

    public MainMenu(StudentManager sm, CourseManager cm, GradeManager gm, TranscriptService ts, FileDataStore fds) {
        this.studentManager = sm;
        this.courseManager = cm;
        this.gradeManager = gm;
        this.transcriptService = ts;
        this.fileDataStore = fds;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("\n=======================================================");
        System.out.println("   HE THONG QUAN LY SINH VIEN VA BANG DIEM (PRO192)   ");
        System.out.println("=======================================================");
        System.out.println("1. Xem danh sach sinh vien");
        System.out.println("2. Them sinh vien moi");
        System.out.println("3. Xem danh sach mon hoc");
        System.out.println("4. Them mon hoc moi");
        System.out.println("5. Ghi nhan / Cap nhat diem cho cap SV - Mon");
        System.out.println("6. Xem bang diem sinh vien");
        System.out.println("7. Xuat bang diem sinh vien ra file CSV");
        System.out.println("8. Luu du lieu vao file");
        System.out.println("0. Thoat chuong trinh");
        System.out.print("Vui long chon chuc nang (0-8): ");
    }

    public void run() {
        boolean running = true;
        while (running) {
            this.displayMenu();
            String choice = this.scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    this.showAllStudents();
                    break;
                case "2":
                    this.addStudent();
                    break;
                case "3":
                    this.showAllCourses();
                    break;
                case "4":
                    this.addCourse();
                    break;
                case "5":
                    this.addOrUpdateGrade();
                    break;
                case "6":
                    this.viewTranscript();
                    break;
                case "7":
                    this.exportTranscriptCSV();
                    break;
                case "8":
                    this.saveData();
                    break;
                case "0":
                    this.saveData();
                    System.out.println("Cam on ban da su dung chuong trinh. Tam biet!");
                    running = false;
                    break;
                default:
                    System.err.println("Lua chon khong hop le. Vui long chon tu 0 den 8.");
            }
        }
    }

    private void showAllStudents() {
        List<Student> list = this.studentManager.getAllStudents();
        System.out.println("\n--- DANH SACH SINH VIEN (" + list.size() + " SV) ---");
        if (list.isEmpty()) {
            System.out.println("Chua co sinh vien nao trong he thong.");
            return;
        }
        for (Student s : list) {
            System.out.println(s);
        }
    }

    private void addStudent() {
        System.out.println("\n--- THEM SINH VIEN MOI ---");
        try {
            System.out.print("Nhap MSSV: ");
            String id = this.scanner.nextLine();
            System.out.print("Nhap ho ten: ");
            String name = this.scanner.nextLine();
            this.studentManager.addStudent(new Student(id, name));
            System.out.println("✅ Them sinh vien thanh cong!");
        } catch (Exception e) {
            System.err.println("❌ Loi: " + e.getMessage());
        }
    }

    private void showAllCourses() {
        List<Course> list = this.courseManager.getAllCourses();
        System.out.println("\n--- DANH SACH MON HOC (" + list.size() + " MON) ---");
        if (list.isEmpty()) {
            System.out.println("Chua co mon hoc nao trong he thong.");
            return;
        }
        for (Course c : list) {
            System.out.println(c.getCourseId() + " - " + c.getCourseName() + " (" + c.getCredits() + " TC, " + c.getCourseType() + ")");
        }
    }

    private void addCourse() {
        System.out.println("\n--- THEM MON HOC MOI ---");
        try {
            System.out.print("Nhap ma mon: ");
            String id = this.scanner.nextLine();
            System.out.print("Nhap ten mon: ");
            String name = this.scanner.nextLine();
            System.out.print("Nhap so tin chi (1-6): ");
            int credits = Integer.parseInt(this.scanner.nextLine().trim());
            System.out.print("Chon loai mon (1: Ly thuyet, 2: Thuc hanh): ");
            String type = this.scanner.nextLine().trim();

            Course course;
            if (type.equals("2")) {
                course = new PracticeCourse(id, name, credits);
            } else {
                course = new TheoryCourse(id, name, credits);
            }
            this.courseManager.addCourse(course);
            System.out.println("✅ Them mon hoc thanh cong!");
        } catch (Exception e) {
            System.err.println("❌ Loi: " + e.getMessage());
        }
    }

    private void addOrUpdateGrade() {
        System.out.println("\n--- GHI NHAN / CAP NHAT DIEM ---");
        try {
            System.out.print("Nhap MSSV: ");
            String studentId = this.scanner.nextLine();
            System.out.print("Nhap ma mon hoc: ");
            String courseId = this.scanner.nextLine();

            Course course = this.courseManager.findCourse(courseId);
            if (course == null) {
                System.err.println("❌ Loi: Khong tim thay mon hoc voi ma [" + courseId + "]. Khong tu tao ma!");
                return;
            }

            GradeRecord existing = this.gradeManager.findRecord(studentId, courseId);
            if (existing != null) {
                System.out.println("Cap SV - Mon da co ket qua cu: " + existing);
                System.out.println("(Nhan Enter de giu nguyen diem cu, go 'null' de dat lai thanh thieu, hoac nhap diem moi)");
            }

            System.out.print("Nhap diem qua trinh [0; 10]: ");
            String pStr = this.scanner.nextLine().trim();
            Double pScore = parseScoreInput(pStr, existing != null ? existing.getProcessScore() : null);

            System.out.print("Nhap diem cuoi ky [0; 10]: ");
            String eStr = this.scanner.nextLine().trim();
            Double eScore = parseScoreInput(eStr, existing != null ? existing.getExamScore() : null);

            GradeRecord res = this.gradeManager.addOrUpdateGrade(this.studentManager, studentId, course, pScore, eScore);
            System.out.println("✅ Ghi nhan ket qua thanh cong: " + res);
        } catch (Exception e) {
            System.err.println("❌ Loi: " + e.getMessage());
        }
    }

    private Double parseScoreInput(String input, Double oldValue) {
        if (input.isEmpty()) {
            return oldValue; // Giu nguyen diem cu
        }
        if (input.equalsIgnoreCase("null") || input.equalsIgnoreCase("thieu")) {
            return null; // Dat lai thanh thieu
        }
        return Double.parseDouble(input);
    }

    private void viewTranscript() {
        System.out.print("\nNhap MSSV can xem bang diem: ");
        String studentId = this.scanner.nextLine().trim();
        Student student = this.studentManager.findStudent(studentId);
        if (student == null) {
            System.err.println("❌ Khong tim thay sinh vien voi MSSV: " + studentId);
            return;
        }

        Transcript transcript = this.transcriptService.buildTranscript(student, this.gradeManager.getAllRecords());
        System.out.println("\n=======================================================");
        System.out.println("           BANG DIEM SINH VIEN: " + student.getFullName() + " (" + student.getStudentId() + ")");
        System.out.println("=======================================================");
        System.out.printf("%-10s %-25s %-12s %-6s %-8s %-8s %-10s %-15s\n",
                "Ma mon", "Ten mon", "Loai mon", "TC", "Diem QT", "Diem CK", "Tong ket", "Trang thai");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (GradeRecord r : transcript.getRecords()) {
            String pStr = (r.getProcessScore() == null) ? "-" : String.format("%.2f", r.getProcessScore());
            String eStr = (r.getExamScore() == null) ? "-" : String.format("%.2f", r.getExamScore());
            String fStr = (r.getFinalScore() == null) ? "-" : String.format("%.2f", r.getFinalScore());

            System.out.printf("%-10s %-25s %-12s %-6d %-8s %-8s %-10s %-15s\n",
                    r.getCourse().getCourseId(), r.getCourse().getCourseName(), r.getCourse().getCourseType(),
                    r.getCourse().getCredits(), pStr, eStr, fStr, r.getStatus());
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Diem trung binh co trong so : " + transcript.getFormattedAverage());
        System.out.println("So mon da du diem           : " + transcript.getCompletedCourseCount() + " / " + transcript.getTotalCourseCount() + " mon");
        System.out.println("So tin chi da tich luy      : " + transcript.getCompletedCredits() + " / " + transcript.getTotalCredits() + " tin chi");
    }

    private void exportTranscriptCSV() {
        System.out.print("\nNhap MSSV can xuat CSV: ");
        String studentId = this.scanner.nextLine().trim();
        Student student = this.studentManager.findStudent(studentId);
        if (student == null) {
            System.err.println("❌ Khong tim thay sinh vien voi MSSV: " + studentId);
            return;
        }

        Transcript transcript = this.transcriptService.buildTranscript(student, this.gradeManager.getAllRecords());
        String defaultPath = "data/transcript_" + studentId + ".csv";
        System.out.print("Nhap duong dan file CSV (Enter de dung mac dinh: " + defaultPath + "): ");
        String path = this.scanner.nextLine().trim();
        if (path.isEmpty()) path = defaultPath;

        try {
            this.transcriptService.exportTranscriptCSV(transcript, path);
            System.out.println("✅ Xuat file CSV thanh cong: " + path);
        } catch (Exception e) {
            System.err.println("❌ Loi khi xuat CSV: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            this.fileDataStore.saveAll(this.studentManager, this.courseManager, this.gradeManager);
            System.out.println("✅ Da luu toan bo du lieu vao file: " + this.fileDataStore.getFilePath());
        } catch (Exception e) {
            System.err.println("❌ Loi khi luu du lieu: " + e.getMessage());
        }
    }
}

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

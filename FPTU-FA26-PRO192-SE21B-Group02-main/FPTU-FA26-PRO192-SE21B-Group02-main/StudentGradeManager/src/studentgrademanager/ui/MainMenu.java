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


public class MainMenu {

    private final StudentManager studentManager;
    private final CourseManager courseManager;
    private final GradeManager gradeManager;
    private final TranscriptService transcriptService;
    private final FileDataStore dataStore;
    private final CsvExporter csvExporter;
    private final Scanner scanner;


    private boolean isModified = false;

    public MainMenu(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager, FileDataStore dataStore) {
        this.studentManager = studentManager;
        this.courseManager = courseManager;
        this.gradeManager = gradeManager;
        this.dataStore = (dataStore != null) ? dataStore : new FileDataStore();
        this.csvExporter = new CsvExporter();
        this.transcriptService = new TranscriptService(this.studentManager, this.gradeManager);
        this.scanner = new Scanner(System.in);


        this.courseManager.setUsageChecker(new CourseManager.CourseUsageChecker() {
            @Override
            public boolean hasGrades(String courseId) {
                return MainMenu.this.gradeManager.hasGrades(courseId);
            }
        });
    }

    public MainMenu(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager) {
        this(studentManager, courseManager, gradeManager, new FileDataStore());
    }


    public void start() {
        System.out.println("======================================================================");
        System.out.println("   CHUONG TRINH QUAN LY SINH VIEN VA BANG DIEM");
        System.out.println("======================================================================");

        if (this.dataStore.exists()) {
            System.out.println(">>> Dang nap du lieu tu file: " + this.dataStore.getDataFilePath());
            try {
                this.dataStore.loadData(this.studentManager, this.courseManager, this.gradeManager);
                System.out.printf(">>> Nap thanh cong: %d sinh vien, %d mon hoc, %d ket qua diem.\n",
                        this.studentManager.getStudentCount(),
                        this.courseManager.getCourseCount(),
                        this.gradeManager.getRecordCount());
            } catch (Exception e) {
                System.err.println(">>> CANH BAO: Loi khi doc file (" + e.getMessage() + ")!");
                System.out.println(">>> He thong giu nguyen trang thai de bao ve du lieu.");
            }
        } else {
            System.out.println(">>> Chua co file du lieu. Dang nap du lieu mau ban dau...");
            FileDataStore.initializeSampleData(this.studentManager, this.courseManager, this.gradeManager);
            this.isModified = true;
            System.out.println(">>> Da nap du lieu mau (An, Binh, 3 mon, 3 ket qua cua An).");
        }

        boolean running = true;
        while (running) {
            this.printMenu();
            String choice = this.readLine("Nhap lua chon cua ban (0-8): ").trim();
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
                    System.out.println("Lua chon khong hop le! Vui long chon tu 0 den 8.");
            }
            if (running) {
                System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("\n----------------------------- MENU CHINH -----------------------------");
        System.out.println("1. Quan ly Sinh vien (Them, tim, xem danh sach)");
        System.out.println("2. Quan ly Mon hoc (Them LT/TH, tim, xem danh sach)");
        System.out.println("3. Tao / Cap nhat ket qua (MSSV + Ma mon + Diem QT, CK)");
        System.out.println("4. Xem ket qua mon hoc (Trang thai du / chua du diem)");
        System.out.println("5. Xem bang diem sinh vien (Chi tiet cac mon, diem tong ket)");
        System.out.println("6. Xem diem trung binh co trong so (GPA) & tin chi");
        System.out.println("7. Xuat bang diem sinh vien ra file CSV (RFC 4180)");
        System.out.println("8. Quan ly luu tru file (Luu, Tai lai, Khoi phuc mau)");
        System.out.println("0. Thoat chuong trinh");
        System.out.println("----------------------------------------------------------------------");
    }


    private void handleStudentManagement() {
        System.out.println("\n--- [1] QUAN LY SINH VIEN ---");
        System.out.println("1. Them sinh vien moi");
        System.out.println("2. Tim sinh vien theo MSSV");
        System.out.println("3. Xem danh sach toan bo sinh vien");
        System.out.println("0. Quay lai menu chinh");

        String sub = this.readLine("Chon: ").trim();
        switch (sub) {
            case "1":
                String id = this.readLine("Nhap MSSV (vi du: QE210003): ");
                String name = this.readLine("Nhap Ho va ten sinh vien: ");
                try {
                    boolean ok = this.studentManager.addStudent(id, name);
                    if (ok) {
                        this.isModified = true;
                        System.out.println("Them sinh vien thanh cong!");
                    }
                } catch (Exception e) {
                    System.out.println("Loi: " + e.getMessage());
                }
                break;
            case "2":
                String searchId = this.readLine("Nhap MSSV can tim: ");
                Student s = this.studentManager.findStudent(searchId);
                if (s != null) {
                    System.out.printf("=> Tim thay: MSSV = %s | Ho ten = %s\n", s.getStudentId(), s.getFullName());
                } else {
                    System.out.println("=> Khong tim thay sinh vien co MSSV: " + searchId);
                }
                break;
            case "3":
                List<Student> list = this.studentManager.getAllStudents();
                System.out.println("\nDANH SACH SINH VIEN (" + list.size() + " sinh vien):");
                System.out.printf("%-12s | %-30s\n", "MSSV", "Ho va ten");
                System.out.println("--------------------------------------------------");
                for (Student st : list) {
                    System.out.printf("%-12s | %-30s\n", st.getStudentId(), st.getFullName());
                }
                break;
            case "0":
                break;
            default:
                System.out.println("Lua chon khong hop le.");
        }
    }

  
    private void handleCourseManagement() {
        System.out.println("\n--- [2] QUAN LY MON HOC ---");
        System.out.println("1. Them mon hoc moi");
        System.out.println("2. Tim mon hoc theo ma mon");
        System.out.println("3. Xem danh sach mon hoc");
        System.out.println("0. Quay lai menu chinh");

        String sub = this.readLine("Chon: ").trim();
        switch (sub) {
            case "1":
                System.out.println("Chon loai mon hoc:");
                System.out.println("1. Ly thuyet (0.30 QT + 0.70 CK)");
                System.out.println("2. Thuc hanh (0.40 QT + 0.60 CK)");
                String typeChoice = this.readLine("Chon loai (1 hoac 2): ").trim();
                String type = "1".equals(typeChoice) ? "THEORY" : ("2".equals(typeChoice) ? "PRACTICE" : null);
                if (type == null) {
                    System.out.println("Loai mon hoc khong hop le!");
                    return;
                }

                String cId = this.readLine("Nhap ma mon hoc (vi du: LT103, PRF192): ");
                String cName = this.readLine("Nhap ten mon hoc: ");
                String crStr = this.readLine("Nhap so tin chi (tu 1 den 6): ");

                try {
                    int credits = CourseManager.parseAndValidateCredits(crStr);
                    this.courseManager.addCourse(type, cId, cName, credits);
                    this.isModified = true;
                    System.out.println("Them mon hoc thanh cong!");
                } catch (Exception e) {
                    System.out.println("Loi: " + e.getMessage());
                }
                break;
            case "2":
                String sId = this.readLine("Nhap ma mon can tim: ");
                Course c = this.courseManager.findCourseById(sId);
                if (c != null) {
                    System.out.printf("=> Tim thay: [%s] %s - %s (%d tin chi)\n", 
                            c.getCourseType(), c.getCourseId(), c.getCourseName(), c.getCredits());
                } else {
                    System.out.println("=> Khong tim thay mon hoc co ma: " + sId);
                }
                break;
            case "3":
                List<Course> cList = this.courseManager.getAllCourses();
                System.out.println("\nDANH SACH MON HOC (" + cList.size() + " mon):");
                System.out.printf("%-10s | %-12s | %-32s | %-8s\n", "Ma mon", "Loai", "Ten mon hoc", "Tin chi");
                System.out.println("------------------------------------------------------------------------");
                for (Course item : cList) {
                    System.out.printf("%-10s | %-12s | %-32s | %-8d\n", 
                            item.getCourseId(), item.getCourseType(), item.getCourseName(), item.getCredits());
                }
                break;
            case "0":
                break;
            default:
                System.out.println("Lua chon khong hop le.");
        }
    }


    private void handleCreateOrUpdateGrade() {
        System.out.println("\n--- [3] TAO / CAP NHAT KET QUA HOC TAP ---");
        String sId = this.readLine("Nhap MSSV: ");
        Student student = this.studentManager.findStudent(sId);
        if (student == null) {
            System.out.println("Loi: Khong tim thay sinh vien co MSSV '" + sId + "'!");
            return;
        }

        String cId = this.readLine("Nhap Ma mon hoc: ");
        Course course = this.courseManager.findCourseById(cId);
        if (course == null) {
            System.out.println("Loi: Khong tim thay mon hoc co ma '" + cId + "'!");
            return;
        }

        System.out.printf("Sinh vien: %s - %s\n", student.getStudentId(), student.getFullName());
        System.out.printf("Mon hoc: [%s] %s - %s (%d TC)\n", course.getCourseType(), course.getCourseId(), course.getCourseName(), course.getCredits());

        GradeRecord current = this.gradeManager.findRecord(student.getStudentId(), course.getCourseId());
        if (current != null) {
            System.out.println("(*) Cap SV-Mon nay DA CO KET QUA:");
            System.out.printf("    Diem qua trinh hien tai: %s\n", 
                    (current.getProcessScore() != null ? current.getProcessScore() : "CHUA CO"));
            System.out.printf("    Diem cuoi ky hien tai:   %s\n", 
                    (current.getExamScore() != null ? current.getExamScore() : "CHUA CO"));
            System.out.println("(*) Nhap diem moi, hoac go 'K' de giu nguyen, hoac go 'EMPTY' de de trong:");
        } else {
            System.out.println("(*) Nhap diem (0.0 den 10.0), hoac de trong / go 'EMPTY' neu chua co diem:");
        }

        Double newProcessScore = this.promptForScore("Diem qua trinh", (current != null ? current.getProcessScore() : null));
        Double newExamScore = this.promptForScore("Diem cuoi ky", (current != null ? current.getExamScore() : null));

        try {
            this.gradeManager.createOrUpdateRecord(student, course, newProcessScore, newExamScore);
            this.isModified = true;
            System.out.println("Cap nhat ket qua thanh cong (khong tao dong trung lap)!");
        } catch (Exception e) {
            System.out.println("Loi khi luu ket qua: " + e.getMessage());
        }
    }

    private Double promptForScore(String label, Double currentVal) {
        while (true) {
            String prompt = (currentVal != null) 
                    ? String.format("%s [Hien tai: %.2f | 'K' giu nguyen | 'EMPTY' de trong]: ", label, currentVal)
                    : String.format("%s [Enter hoac 'EMPTY' neu thieu]: ", label);
            String input = this.readLine(prompt).trim();

            if (input.equalsIgnoreCase("K") && currentVal != null) {
                return currentVal;
            }
            if (input.isEmpty() || input.equalsIgnoreCase("EMPTY") || input.equalsIgnoreCase("NULL")) {
                return null;
            }

            try {
                return this.gradeManager.parseScore(input);
            } catch (Exception e) {
                System.out.println("  => Loi: " + e.getMessage() + ". Vui long nhap lai!");
            }
        }
    }


    private void handleViewCourseGrades() {
        System.out.println("\n--- [4] XEM KET QUA MON HOC ---");
        String cId = this.readLine("Nhap ma mon hoc: ");
        Course course = this.courseManager.findCourseById(cId);
        if (course == null) {
            System.out.println("Loi: Khong tim thay mon hoc co ma '" + cId + "'!");
            return;
        }

        List<GradeRecord> records = this.gradeManager.getRecordsByCourse(course.getCourseId());
        System.out.printf("\nKET QUA MON HOC: [%s] %s - %s (%d tin chi)\n",
                course.getCourseType(), course.getCourseId(), course.getCourseName(), course.getCredits());
        System.out.println("Tong so sinh vien co ket qua: " + records.size());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-24s | %-10s | %-10s | %-10s | %-15s\n",
                "MSSV", "Ho va ten", "Qua trinh", "Cuoi ky", "Tong ket", "Trang thai");
        System.out.println("------------------------------------------------------------------------------------------");

        for (GradeRecord r : records) {
            Student s = r.getStudent();
            String pStr = (r.getProcessScore() != null) ? String.format(Locale.US, "%.2f", r.getProcessScore()) : "--";
            String eStr = (r.getExamScore() != null) ? String.format(Locale.US, "%.2f", r.getExamScore()) : "--";
            Double fScore = r.getFinalScore();
            String fStr = (fScore != null) ? String.format(Locale.US, "%.2f", fScore) : Transcript.CHUA_DU_DIEM;
            String status = r.isComplete() ? "DU DIEM" : "CHUA DU DIEM";

            System.out.printf("%-10s | %-24s | %-10s | %-10s | %-10s | %-15s\n",
                    s.getStudentId(), s.getFullName(), pStr, eStr, fStr, status);
        }
    }


    private void handleViewStudentTranscript() {
        System.out.println("\n--- [5] XEM BANG DIEM SINH VIEN ---");
        String sId = this.readLine("Nhap MSSV: ");
        Transcript transcript = this.transcriptService.getTranscript(sId);
        if (transcript == null) {
            System.out.println("Loi: Khong tim thay sinh vien co MSSV '" + sId + "'!");
            return;
        }

        Student s = transcript.getStudent();
        System.out.println("\n================================ BANG DIEM ================================");
        System.out.printf("Sinh vien: %s - %s\n", s.getStudentId(), s.getFullName());
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("%-8s | %-24s | %-4s | %-10s | %-6s | %-6s | %-8s\n",
                "Ma mon", "Ten mon hoc", "TC", "Loai", "QT", "CK", "Tong ket");
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
        System.out.printf("Diem trung binh tich luy: %s\n", transcript.getFormattedGPA());
        System.out.printf("So mon hoan thanh du diem: %d/%d mon\n",
                transcript.getCompletedCourses(), transcript.getTotalCourses());
        System.out.printf("So tin chi hoan thanh:     %d/%d tin chi\n",
                transcript.getCompletedCredits(), transcript.getTotalCredits());
        System.out.println("===========================================================================");
    }

  
    private void handleViewWeightedGPA() {
        System.out.println("\n--- [6] XEM DIEM TRUNG BINH CO TRONG SO (GPA) ---");
        String sId = this.readLine("Nhap MSSV: ");
        Transcript transcript = this.transcriptService.getTranscript(sId);
        if (transcript == null) {
            System.out.println("Loi: Khong tim thay sinh vien co MSSV '" + sId + "'!");
            return;
        }

        Student s = transcript.getStudent();
        Double rawGpa = transcript.calculateWeightedGPA();

        System.out.println("\nKET QUA TINH DIEM TRUNG BINH:");
        System.out.printf("Sinh vien: %s - %s\n", s.getStudentId(), s.getFullName());
        if (rawGpa == null) {
            System.out.println("Diem trung binh tich luy: " + Transcript.CHUA_CO_DIEM_TB);
            System.out.println("(Ly do: Sinh vien chua co mon nao hoan thanh du ca 2 diem thanh phan)");
        } else {
            System.out.printf(Locale.US, "Diem trung binh (lam tron 2 chu so): %.2f (Diem tho: %.4f)\n", rawGpa, rawGpa);
        }
        System.out.printf("So mon dat du diem:    %d/%d mon\n", transcript.getCompletedCourses(), transcript.getTotalCourses());
        System.out.printf("So tin chi hoan thanh: %d/%d tin chi\n", transcript.getCompletedCredits(), transcript.getTotalCredits());
    }


    private void handleExportCsv() {
        System.out.println("\n--- [7] XUAT BANG DIEM RA FILE CSV (RFC 4180) ---");
        String sId = this.readLine("Nhap MSSV can xuat bang diem: ");
        Transcript transcript = this.transcriptService.getTranscript(sId);
        if (transcript == null) {
            System.out.println("Loi: Khong tim thay sinh vien co MSSV '" + sId + "'!");
            return;
        }

        String defaultFileName = "bang_diem_" + transcript.getStudent().getStudentId().toUpperCase() + ".csv";
        String prompt = "Nhap ten file dich [Enter de dung '" + defaultFileName + "']: ";
        String pathInput = this.readLine(prompt).trim();
        String targetPath = pathInput.isEmpty() ? defaultFileName : pathInput;

        try {
            File file = new File(targetPath);
            this.csvExporter.exportTranscript(transcript, file);
            System.out.println("Xuat file CSV thanh cong!");
            System.out.println("Duong dan file: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Loi khi xuat file CSV: " + e.getMessage());
        }
    }

 
    private void handleFileStorageMenu() {
        System.out.println("\n--- [8] QUAN LY LUU TRU DU LIEU ---");
        System.out.println("1. Luu toan bo du lieu ra file (" + this.dataStore.getDataFilePath() + ")");
        System.out.println("2. Tai lai toan bo du lieu tu file");
        System.out.println("3. Khoi phuc du lieu mau ban dau (De bai P05)");
        System.out.println("0. Quay lai menu chinh");

        String sub = this.readLine("Chon: ").trim();
        switch (sub) {
            case "1":
                try {
                    this.dataStore.saveData(this.studentManager, this.courseManager, this.gradeManager);
                    this.isModified = false;
                    System.out.println("Luu du lieu thanh cong ra file: " + this.dataStore.getDataFilePath());
                } catch (IOException e) {
                    System.err.println("LOI LUU FILE: " + e.getMessage());
                    System.out.println("Du lieu trong phien hien tai van an toan!");
                }
                break;
            case "2":
                try {
                    boolean ok = this.dataStore.loadData(this.studentManager, this.courseManager, this.gradeManager);
                    if (ok) {
                        this.isModified = false;
                        System.out.printf("Tai du lieu thanh cong: %d sinh vien, %d mon hoc, %d ket qua diem.\n",
                                this.studentManager.getStudentCount(),
                                this.courseManager.getCourseCount(),
                                this.gradeManager.getRecordCount());
                    } else {
                        System.out.println("File du lieu '" + this.dataStore.getDataFilePath() + "' chua ton tai.");
                    }
                } catch (Exception e) {
                    System.err.println("LOI NAP FILE: " + e.getMessage());
                    System.out.println("He thong da huy bo de bao ve du lieu trong RAM.");
                }
                break;
            case "3":
                String confirm = this.readLine("Ban co chac muon nap lai du lieu mau ban dau? (y/n): ").trim();
                if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
                    FileDataStore.initializeSampleData(this.studentManager, this.courseManager, this.gradeManager);
                    this.isModified = true;
                    System.out.println("Da khoi phuc du lieu mau ban dau.");
                }
                break;
            case "0":
                break;
            default:
                System.out.println("Lua chon khong hop le.");
        }
    }


    private boolean handleExit() {
        if (this.isModified) {
            String answer = this.readLine("Du lieu co thay doi chua luu. Ban co muon luu truoc khi thoat? (y/n): ").trim();
            if ("y".equalsIgnoreCase(answer) || "yes".equalsIgnoreCase(answer)) {
                try {
                    this.dataStore.saveData(this.studentManager, this.courseManager, this.gradeManager);
                    System.out.println("Da luu du lieu thanh cong.");
                } catch (IOException e) {
                    System.err.println("Loi luu file: " + e.getMessage());
                    String force = this.readLine("Van muon thoat ma khong luu? (y/n): ").trim();
                    if (!"y".equalsIgnoreCase(force)) {
                        return true; // Khong thoat
                    }
                }
            }
        }
        System.out.println("Cam on ban da su dung chuong trinh. Tam biet!");
        return false; 
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (this.scanner.hasNextLine()) {
            return this.scanner.nextLine();
        }
        return "";
    }
}

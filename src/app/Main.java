package app;

import manager.CourseManager;
import manager.GradeManager;
import manager.StudentManager;
import model.Course;
import model.PracticeCourse;
import model.Student;
import model.TheoryCourse;
import service.FileDataStore;
import service.TranscriptService;
import ui.MainMenu;

/**
 * Lop Main: Diem khoi chay chinh cua ung dung Java Console PRO192 FA26 P05.
 * Tich hop hoan chinh cong viec cua ca 5 thanh vien trong nhom.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Dang khoi dong he thong Quan ly sinh vien va bang diem...");

        // 1. Khoi tao cac lop quan ly va dich vu
        StudentManager studentManager = new StudentManager();
        CourseManager courseManager = new CourseManager();
        GradeManager gradeManager = new GradeManager();
        TranscriptService transcriptService = new TranscriptService();
        FileDataStore fileDataStore = new FileDataStore("data/app_data.txt");

        // 2. Nap du lieu tu file neu da co
        try {
            fileDataStore.loadAll(studentManager, courseManager, gradeManager);
        } catch (Exception e) {
            System.err.println("Thong bao: Khong the doc du lieu cu (" + e.getMessage() + ").");
        }

        // 3. Neu la lan chay dau (chua co du lieu), nap du lieu mau theo de bai
        if (studentManager.getStudentCount() == 0 && courseManager.getCourseCount() == 0) {
            initSampleData(studentManager, courseManager, gradeManager);
        }

        // 4. Khoi dong Menu dieu huong
        MainMenu menu = new MainMenu(studentManager, courseManager, gradeManager, transcriptService, fileDataStore);
        menu.run();
    }

    /**
     * Nap bo du lieu mau ban dau (2 SV, 3 mon, 3 ket qua) theo yeu cau muc 5 trong de bai.
     */
    private static void initSampleData(StudentManager sm, CourseManager cm, GradeManager gm) {
        try {
            // 2 Sinh vien
            Student an = new Student("QE210001", "Nguyen An");
            Student binh = new Student("QE210002", "Tran Binh");
            sm.addStudent(an);
            sm.addStudent(binh);

            // 3 Mon hoc
            Course lt101 = new TheoryCourse("LT101", "Nhap mon ly thuyet", 3);
            Course th101 = new PracticeCourse("TH101", "Thuc hanh co ban", 2);
            Course lt102 = new TheoryCourse("LT102", "Chuyen de co so", 2);
            cm.addCourse(lt101);
            cm.addCourse(th101);
            cm.addCourse(lt102);

            // 3 Ket qua cua An
            gm.addOrUpdateGrade(sm, "QE210001", lt101, 8.0, 6.0);
            gm.addOrUpdateGrade(sm, "QE210001", th101, 8.0, 6.0);
            gm.addOrUpdateGrade(sm, "QE210001", lt102, 9.0, null); // Diem cuoi ky bi thieu

            System.out.println("✅ Da nap thanh cong bo du lieu mau khoi dau!");
        } catch (Exception e) {
            System.err.println("Loi khi nap du lieu mau: " + e.getMessage());
        }
    }
}

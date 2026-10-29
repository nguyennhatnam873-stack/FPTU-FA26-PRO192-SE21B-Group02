package studentgrademanager;

import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;
import studentgrademanager.storage.FileDataStore;
import studentgrademanager.ui.MainMenu;

/**
 * Điểm khởi chạy chính của ứng dụng Quản lý sinh viên và bảng điểm (PRO192 FA26 - P05).
 */
public class StudentGradeManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        StudentManager studentManager = new StudentManager();
        CourseManager courseManager = new CourseManager();
        GradeManager gradeManager = new GradeManager();
        FileDataStore dataStore = new FileDataStore("data/data_store.txt");

        MainMenu menu = new MainMenu(studentManager, courseManager, gradeManager, dataStore);
        menu.start();
    }
}

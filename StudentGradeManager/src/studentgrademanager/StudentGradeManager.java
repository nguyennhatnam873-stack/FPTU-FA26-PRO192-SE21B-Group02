package studentgrademanager;

import java.io.IOException;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;
import studentgrademanager.services.TranscriptService;
import studentgrademanager.storage.FileDataStore;
import studentgrademanager.ui.MainMenu;

public class StudentGradeManager {

    public static void main(String[] args) {
        CourseManager courseManager = new CourseManager();
        StudentManager studentManager = new StudentManager();
        final GradeManager gradeManager = new GradeManager();

        courseManager.setUsageChecker(new CourseManager.CourseUsageChecker() {
            @Override
            public boolean hasGrades(String courseId) {
                return gradeManager.hasGradesForCourse(courseId);
            }
        });

        TranscriptService transcriptService = new TranscriptService(studentManager, gradeManager);
        FileDataStore fileDataStore = new FileDataStore("data");

        try {
            fileDataStore.loadData(courseManager, studentManager, gradeManager);
        } catch (IOException e) {
            System.err.println("Không thể nạp dữ liệu từ file. Khởi tạo dữ liệu mẫu.");
            fileDataStore.loadSampleData(courseManager, studentManager, gradeManager);
        }

        MainMenu menu = new MainMenu(courseManager, studentManager, gradeManager, transcriptService, fileDataStore);
        menu.start();
    }
}

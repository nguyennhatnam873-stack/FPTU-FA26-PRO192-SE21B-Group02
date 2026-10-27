package studentgrademanager.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;

public class FileDataStore {

    private String dataDirPath;
    private String coursesFilePath;
    private String studentsFilePath;
    private String gradesFilePath;

    public FileDataStore() {
        this("data");
    }

    public FileDataStore(String dataDirPath) {
        this.dataDirPath = dataDirPath;
        this.coursesFilePath = dataDirPath + File.separator + "courses.txt";
        this.studentsFilePath = dataDirPath + File.separator + "students.txt";
        this.gradesFilePath = dataDirPath + File.separator + "grades.txt";
    }

    public boolean hasExistingData() {
        File cFile = new File(this.coursesFilePath);
        File sFile = new File(this.studentsFilePath);
        File gFile = new File(this.gradesFilePath);
        return cFile.exists() || sFile.exists() || gFile.exists();
    }

    public void loadData(CourseManager courseManager, StudentManager studentManager, GradeManager gradeManager) throws IOException {
        File dataDir = new File(this.dataDirPath);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        if (!this.hasExistingData()) {
            this.loadSampleData(courseManager, studentManager, gradeManager);
            this.saveData(courseManager, studentManager, gradeManager);
            return;
        }

        courseManager.clear();
        studentManager.clear();
        gradeManager.clear();

        File cFile = new File(this.coursesFilePath);
        if (cFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cFile), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(";", -1);
                    if (parts.length >= 4) {
                        String type = parts[0].trim();
                        String id = parts[1].trim();
                        String name = parts[2].trim();
                        int credits = Integer.parseInt(parts[3].trim());
                        Course course = CourseManager.createCourse(type, id, name, credits);
                        courseManager.addCourse(course);
                    }
                }
            }
        }

        File sFile = new File(this.studentsFilePath);
        if (sFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sFile), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(";", -1);
                    if (parts.length >= 2) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        studentManager.addStudent(id, name);
                    }
                }
            }
        }

        File gFile = new File(this.gradesFilePath);
        if (gFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(gFile), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(";", -1);
                    if (parts.length >= 4) {
                        String sId = parts[0].trim();
                        String cId = parts[1].trim();
                        String pStr = parts[2].trim();
                        String eStr = parts[3].trim();

                        Student s = studentManager.findStudent(sId);
                        Course c = courseManager.findCourse(cId);

                        if (s != null && c != null) {
                            Double pScore = pStr.isEmpty() || "-".equals(pStr) ? null : Double.parseDouble(pStr);
                            Double eScore = eStr.isEmpty() || "-".equals(eStr) ? null : Double.parseDouble(eStr);
                            gradeManager.addOrUpdateGrade(s, c, pScore, eScore);
                        }
                    }
                }
            }
        }
    }

    public void saveData(CourseManager courseManager, StudentManager studentManager, GradeManager gradeManager) throws IOException {
        File dataDir = new File(this.dataDirPath);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File tempC = new File(this.coursesFilePath + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempC), StandardCharsets.UTF_8))) {
            for (Course c : courseManager.getAllCourses()) {
                writer.write(String.format(Locale.US, "%s;%s;%s;%d\n",
                        c.getTypeCode(), c.getCourseId(), c.getCourseName(), c.getCredits()));
            }
        }

        File tempS = new File(this.studentsFilePath + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempS), StandardCharsets.UTF_8))) {
            for (Student s : studentManager.getAllStudents()) {
                writer.write(String.format(Locale.US, "%s;%s\n",
                        s.getStudentId(), s.getFullName()));
            }
        }

        File tempG = new File(this.gradesFilePath + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempG), StandardCharsets.UTF_8))) {
            for (GradeRecord r : gradeManager.getAllRecords()) {
                String pStr = r.getProcessScore() == null ? "" : String.format(Locale.US, "%.2f", r.getProcessScore());
                String eStr = r.getExamScore() == null ? "" : String.format(Locale.US, "%.2f", r.getExamScore());
                writer.write(String.format(Locale.US, "%s;%s;%s;%s\n",
                        r.getStudent().getStudentId(),
                        r.getCourse().getCourseId(),
                        pStr,
                        eStr));
            }
        }

        replaceFile(tempC, new File(this.coursesFilePath));
        replaceFile(tempS, new File(this.studentsFilePath));
        replaceFile(tempG, new File(this.gradesFilePath));
    }

    private static void replaceFile(File source, File destination) throws IOException {
        if (destination.exists() && !destination.delete()) {
            throw new IOException("Không thể xóa file cũ: " + destination.getAbsolutePath());
        }
        if (!source.renameTo(destination)) {
            throw new IOException("Không thể đổi tên file tạm: " + source.getAbsolutePath());
        }
    }

    public void loadSampleData(CourseManager courseManager, StudentManager studentManager, GradeManager gradeManager) {
        courseManager.clear();
        studentManager.clear();
        gradeManager.clear();

        Course c1 = CourseManager.createCourse("THEORY", "LT101", "Lập trình Hướng đối tượng", 3);
        Course c2 = CourseManager.createCourse("PRACTICE", "TH101", "Thực hành Java cơ bản", 2);
        Course c3 = CourseManager.createCourse("THEORY", "LT102", "Cơ sở dữ liệu", 3);

        courseManager.addCourse(c1);
        courseManager.addCourse(c2);
        courseManager.addCourse(c3);

        Student s1 = new Student("SE180001", "Nguyễn Tuấn Kiệt");
        Student s2 = new Student("SE180002", "Trần Ngọc Cưng");

        studentManager.addStudent(s1);
        studentManager.addStudent(s2);

        gradeManager.addOrUpdateGrade(s1, c1, 8.0, 6.0);
        gradeManager.addOrUpdateGrade(s1, c2, 8.0, 6.0);
        gradeManager.addOrUpdateGrade(s2, c1, 0.0, null);
    }
}

package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import manager.CourseManager;
import manager.GradeManager;
import manager.StudentManager;
import model.Course;
import model.GradeRecord;
import model.PracticeCourse;
import model.Student;
import model.TheoryCourse;

/**
 * Lop FileDataStore: Quan ly luu va doc toan bo du lieu chuong trinh vao file cuc bo.
 * Phu trach chinh: Nguoi 5 (Luu/doc file & CSV) - Ca test: D08, D09, D10.
 * 
 * Quy tac:
 * - Luu va phuc hoi dung loai mon (Ly thuyet / Thuc hanh) de bao toan tinh da hinh.
 * - Diem thieu giu nguyen null, diem 0.0 van la 0.0.
 * - Lan dau chay chua co file thi co the nap mau. Khi da co du lieu thi khong nap de len.
 */
public class FileDataStore {
    private String filePath;

    public FileDataStore() {
        this.filePath = "data/app_data.txt";
    }

    public FileDataStore(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Luu toan bo du lieu tu cac quan ly xuong file.
     */
    public void saveAll(StudentManager sm, CourseManager cm, GradeManager gm) throws IOException {
        File file = new File(this.filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // 1. Luu Sinh vien
            writer.write("#STUDENTS");
            writer.newLine();
            for (Student s : sm.getAllStudents()) {
                writer.write(s.getStudentId() + "|" + s.getFullName());
                writer.newLine();
            }

            // 2. Luu Mon hoc (ghi ro loai mon de phuc hoi dung lop con)
            writer.write("#COURSES");
            writer.newLine();
            for (Course c : cm.getAllCourses()) {
                writer.write(c.getCourseId() + "|" + c.getCourseName() + "|" + c.getCredits() + "|" + c.getCourseType());
                writer.newLine();
            }

            // 3. Luu Ket qua diem
            writer.write("#GRADES");
            writer.newLine();
            for (GradeRecord r : gm.getAllRecords()) {
                String processStr = (r.getProcessScore() == null) ? "null" : r.getProcessScore().toString();
                String examStr = (r.getExamScore() == null) ? "null" : r.getExamScore().toString();
                writer.write(r.getStudent().getStudentId() + "|" + r.getCourse().getCourseId() + "|" + processStr + "|" + examStr);
                writer.newLine();
            }

            writer.flush();
        }
    }

    /**
     * Doc toan bo du lieu tu file vao cac manager.
     */
    public void loadAll(StudentManager sm, CourseManager cm, GradeManager gm) throws IOException {
        File file = new File(this.filePath);
        if (!file.exists()) {
            return; // File chua ton tai, bat dau trang thai rong
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            String currentSection = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                if (line.startsWith("#")) {
                    currentSection = line;
                    continue;
                }

                String[] parts = line.split("\\|");
                if (currentSection.equals("#STUDENTS") && parts.length >= 2) {
                    try {
                        sm.addStudent(new Student(parts[0], parts[1]));
                    } catch (Exception ignored) {}
                } else if (currentSection.equals("#COURSES") && parts.length >= 4) {
                    try {
                        String id = parts[0];
                        String name = parts[1];
                        int credits = Integer.parseInt(parts[2]);
                        String type = parts[3];

                        Course c;
                        if (type.equalsIgnoreCase("Thuc hanh")) {
                            c = new PracticeCourse(id, name, credits);
                        } else {
                            c = new TheoryCourse(id, name, credits);
                        }
                        cm.addCourse(c);
                    } catch (Exception ignored) {}
                } else if (currentSection.equals("#GRADES") && parts.length >= 4) {
                    try {
                        String studentId = parts[0];
                        String courseId = parts[1];
                        Double process = parts[2].equalsIgnoreCase("null") ? null : Double.parseDouble(parts[2]);
                        Double exam = parts[3].equalsIgnoreCase("null") ? null : Double.parseDouble(parts[3]);

                        Course course = cm.findCourse(courseId);
                        if (course != null) {
                            gm.addOrUpdateGrade(sm, studentId, course, process, exam);
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
    }
}

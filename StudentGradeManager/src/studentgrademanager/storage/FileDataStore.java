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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;


public class FileDataStore{
    public static final String DEFAULT_DATA_PATH = "data/data_store.txt";
    
    public static final String SECTION_STUDENTS = "[STUDENTS]";
    public static final String SECTION_COURSES = "[COURSES]";
    public static final String SECTION_GRADES = "[GRADES]";
    public static final String VALUE_EMPTY = "EMPTY";
    
    private final String dataFilePath;
    
    public FileDataStore(){
        this(DEFAULT_DATA_PATH);
    }
    
    public FileDataStore(String dataFilePath){
        if (dataFilePath == null || dataFilePath.trim().isEmpty()){
            this.dataFilePath = DEFAULT_DATA_PATH;
        } else {
            this.dataFilePath = dataFilePath.trim();
        }
    }
    
    public String getDataFilePath(){
        return this.dataFilePath;
    }
    
    public boolean exists(){
        File file = new File(this.dataFilePath);
        return file.exists() && file.isFile() && file.length() > 0;
    }
    
    public boolean hasExistingData(){
        return this.exists();
    }
    
    public synchronized void saveData(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager) throws IOException{
        if (studentManager == null || courseManager == null || gradeManager == null){
            throw new IllegalArgumentException ("Cac service quan li khong duoc null.");
        }
        
        File targetFile = new File(this.dataFilePath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()){
            if (!parentDir.mkdir()){
                throw new IOException("Khong the tao thu muc luu tru: " + parentDir.getAbsolutePath());
            }
        }
        
        File tempFile = new File(this.dataFilePath + ",tmp");
        
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            
            writer.write("# HE THONG QUAN LI DIEM SINH VIEN");
            writer.newLine();
            writer.write("# Luu tru tu dong boi FileDataStore");
            writer.newLine();
            writer.newLine();
            
            writer.write(SECTION_STUDENTS);
            writer.newLine();
            for (Student s : studentManager.getAllStudents()){
                writer.write(s.getStudentId() + "|" + s.getFullName());
                writer.newLine();
            }
            writer.newLine();
            
            writer.write(SECTION_COURSES);
            writer.newLine();
            for (Course c : courseManager.getAllCourses()){
                writer.write(String.format(Locale.US, "%s|%s|%s|%d", c.getTypeCode(), c.getCourseId(), c.getCourseName(), c.getCredits()));
                writer.newLine();
            }
            writer.newLine();
            
            writer.write(SECTION_GRADES);
            writer.newLine();
            for (GradeRecord r : gradeManager.getAllRecords()){
                if (r.getStudent() == null || r.getCourse() == null){
                    continue;
                }
                String pScore = (r.getProcessScore() != null) ? String.format(Locale.US, "%.2f", r.getProcessScore()) : VALUE_EMPTY;
                String eScore = (r.getExamScore() != null) ? String.format(Locale.US, "%.2f", r.getExamScore()) : VALUE_EMPTY;
                
                writer.write(String.format(Locale.US, "%s|%s|%s|%s", r.getStudent().getStudentId(), r.getCourse().getCourseId(), pScore, eScore));
                writer.newLine();
            }
            
            writer.flush();
        } catch (IOException e){
            if (tempFile.exists()){
                tempFile.delete();
            }
            throw new IOException("Loi khi ghi ra file tam: " + e.getMessage(),e);
        } finally {
            if (writer != null){
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
        
        if (targetFile.exists()){
            if (!targetFile.delete()){
                File backupFile = new File(this.dataFilePath + ".bak");
                if (backupFile.exists()){
                    backupFile.delete();
                }
                if (!targetFile.renameTo(backupFile)){
                    throw new IOException("Khong the ghi de file du lieu cu: " + targetFile.getAbsolutePath());
                }
            }
        }
        
        if (!tempFile.renameTo(targetFile)){
            throw new IOException("Khong the doi ten file tam sang file chinh: " + targetFile.getAbsolutePath());
        }
    }
    
    public synchronized void saveData(CourseManager courseManager, StudentManager studentManager, GradeManager gradeManger) throws IOException {
        this.saveData(studentManager, courseManager, gradeManger);
    }
    
    public synchronized boolean loadData(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager) throws IOException {
        File file = new File(this.dataFilePath);
        if (!file.exists() || !file.isFile()){
            return false;
        }
        
        List<Student> tempStudents = new ArrayList<Student>();
        List<Course> tempCourses = new ArrayList<Course>();
        List<TempGradeEntry> tempGrades = new ArrayList<TempGradeEntry>();
        
        BufferedReader reader = null;
        int lineNumber = 0;
        try{
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            
            String currentSection = "";
            String line;
            
            while ((line = reader.readLine()) != null){
                lineNumber++;
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")){
                    continue;
                }
                
                if (line.equalsIgnoreCase(SECTION_STUDENTS) || line.equalsIgnoreCase(SECTION_COURSES) || line.equalsIgnoreCase(SECTION_GRADES)){
                    currentSection = line.toUpperCase();
                    continue;
                }
                
                String[] parts = line.split("\\|", -1);
                
                if (SECTION_STUDENTS.equals(currentSection)){
                    if (parts.length < 2){
                        throw new IOException("Du lieu sinh vien sai dinh dang tai dong " + lineNumber + ": " + line);
                    }
                    String studentId = parts[0].trim();
                    String fullName = parts[1].trim();
                    if(studentId.isEmpty() || fullName.isEmpty()){
                        throw new IOException("MSSV hoac Ho ten bi trong tai dong " + lineNumber);
                    }
                    tempStudents.add(new Student(studentId, fullName));
                } else if (SECTION_COURSES.equals(currentSection)){
                    if (parts.length < 4){
                        throw new IOException("Du lieu mon hoc sai dinh dang tai dong " + lineNumber + ": " + line);
                    }
                    String type = parts[0].trim();
                    String courseId = parts[1].trim();
                    String courseName = parts[2].trim();
                    int credits = CourseManager.parseAndValidateCredits(parts[3].trim());
                    Course course = CourseManager.createCourse(type, courseId, courseName, credits);
                    tempCourses.add(course);
                } else if (SECTION_GRADES.equals(currentSection)){
                    if (parts.length < 4){
                        throw new IOException("Du lieu diem sai dinh dang tai dong " + lineNumber + ": " + line);
                    }
                    
                    String studentId = parts[0].trim();
                    String courseId = parts[1].trim();
                    String pScoreStr = parts[2].trim();
                    String eScoreStr = parts[3].trim();
                    
                    Double pScore = parseScoreValue(pScoreStr, lineNumber);
                    Double eScore = parseScoreValue(eScoreStr, lineNumber);
                    
                    tempGrades.add(new TempGradeEntry(studentId, courseId, pScore, eScore));
                }
            }
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException ignored){
                }
            }
        }
        
        studentManager.clear();
        for (Student s : tempStudents){
            studentManager.addStudent(s);
        }
        
        courseManager.clear();
        for (Course c : tempCourses){
            courseManager.addCourse(c);
        }
        
        gradeManager.clear();
        for (TempGradeEntry entry : tempGrades){
            Student s = studentManager.findStudent(entry.studentId);
            Course c = courseManager.findCourseById(entry.courseId);
            if (s != null && c != null){
                gradeManager.createOrUpdateRecord(s, c, entry.processScore, entry.examScore);
            }
        }
        return true;
    }
    
    public synchronized boolean loadData(CourseManager courseManager, StudentManager studentManager, GradeManager gradeManager) throws IOException{
        return this.loadData(studentManager, courseManager, gradeManager);
    }
    
    private static Double parseScoreValue(String str, int line) throws IOException{
        if (str.isEmpty() || VALUE_EMPTY.equalsIgnoreCase(str) || "NULL".equalsIgnoreCase(str)){
            return null;
        }
        try {
            double val = Double.parseDouble(str);
            GradeManager.validateScore(val);
            return val;
        } catch (Exception e){
            throw new IOException("Diem khong hop le tai dong " + line + ": '" + str + "' (" + e.getMessage() + ")");
        }
    }
    
    public static void initializeSampleData(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager){
        studentManager.clear();
        courseManager.clear();
        gradeManager.clear();
        
        Student an = new Student("QE210001", "Nguyen An");
        Student binh = new Student("QE210002", "Tran Binh");
        studentManager.addStudent(an);
        studentManager.addStudent(binh);
        
        Course lt101 = CourseManager.createCourse("THEORY", "LT101", "Nhap mon ly thuyet", 3);
        Course th101 = CourseManager.createCourse("PRACTICE", "TH101", "Thuc hanh co ban", 2);
        Course lt102 = CourseManager.createCourse("THEORY", "LT102", "Chuyen de co so", 2);
        
        courseManager.addCourse(lt101);
        courseManager.addCourse(th101);
        courseManager.addCourse(lt102);
        
        gradeManager.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gradeManager.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gradeManager.createOrUpdateRecord(an, lt102, 9.0, null);
    }
    
    public void loadSampleData(CourseManager courseManager, StudentManager studentManager, GradeManager gradeManager){
        initializeSampleData(studentManager, courseManager, gradeManager);
    }
    
    private static class TempGradeEntry{
        String studentId;
        String courseId;
        Double processScore;
        Double examScore;
    
        TempGradeEntry(String studentId, String courseId, Double processScore, Double examScore){
            this.studentId = studentId;
            this.courseId = courseId;
            this.processScore = processScore;
            this.examScore = examScore;
        }
    }
}

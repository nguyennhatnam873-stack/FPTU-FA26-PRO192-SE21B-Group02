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
<<<<<<< HEAD
=======
import java.util.ArrayList;
import java.util.List;
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
import java.util.Locale;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;

<<<<<<< HEAD
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
=======
/**
 * Lớp phụ trách Lưu và Phục hồi toàn bộ dữ liệu hệ thống (Sinh viên, Môn học, Điểm).
 * Phụ trách: Người 5 — Lưu/đọc file & CSV
 * 
 * Các tiêu chuẩn đáp ứng:
 * - Lưu trữ file văn bản UTF-8 rõ ràng, có phân mục cấu trúc ([STUDENTS], [COURSES], [GRADES]).
 * - Phân biệt chính xác giữa điểm 0 (0.00) và điểm thiếu (EMPTY / null).
 * - Cơ chế ghi an toàn (Atomic Save): Ghi vào file tạm (.tmp) trước, chỉ đổi tên khi thành công 100%. Tránh mất dữ liệu khi có sự cố.
 * - Xử lý lỗi file: Khi file bị hỏng (corrupted), phát hiện lỗi và KHÔNG ghi đè dữ liệu rỗng lên file cũ.
 * - Quy ước đường dẫn tương đối (mặc định: data/data_store.txt).
 * - Lần đầu chạy nếu chưa có file dữ liệu thì có thể nạp dữ liệu mẫu ban đầu; khi đã có file thì nạp từ file.
 */
public class FileDataStore {

    public static final String DEFAULT_DATA_PATH = "data/data_store.txt";

    public static final String SECTION_STUDENTS = "[STUDENTS]";
    public static final String SECTION_COURSES = "[COURSES]";
    public static final String SECTION_GRADES = "[GRADES]";
    public static final String VALUE_EMPTY = "EMPTY";

    private final String dataFilePath;

    public FileDataStore() {
        this(DEFAULT_DATA_PATH);
    }

    public FileDataStore(String dataFilePath) {
        if (dataFilePath == null || dataFilePath.trim().isEmpty()) {
            this.dataFilePath = DEFAULT_DATA_PATH;
        } else {
            this.dataFilePath = dataFilePath.trim();
        }
    }

    public String getDataFilePath() {
        return this.dataFilePath;
    }

    /**
     * Kiểm tra file dữ liệu đã tồn tại và có kích thước lớn hơn 0 hay chưa.
     */
    public boolean exists() {
        File file = new File(this.dataFilePath);
        return file.exists() && file.isFile() && file.length() > 0;
    }

    // ==================== LƯU DỮ LIỆU AN TOÀN (ATOMIC SAVE) ====================

    /**
     * Lưu toàn bộ dữ liệu hệ thống ra file.
     * Áp dụng Atomic write: ghi ra file tạm (.tmp) rồi mới ghi đè file chính.
     * 
     * @param studentManager Quản lý sinh viên
     * @param courseManager Quản lý môn học
     * @param gradeManager Quản lý điểm
     * @throws IOException nếu xảy ra lỗi ghi file
     */
    public synchronized void saveData(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager) throws IOException {
        if (studentManager == null || courseManager == null || gradeManager == null) {
            throw new IllegalArgumentException("Các đối tượng dịch vụ quản lý không được null khi lưu.");
        }

        File targetFile = new File(this.dataFilePath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Không thể tạo thư mục lưu trữ: " + parentDir.getAbsolutePath());
            }
        }

        File tempFile = new File(this.dataFilePath + ".tmp");

        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));

            writer.write("# HỆ THỐNG QUẢN LÝ SINH VIÊN VÀ BẢNG ĐIỂM - PRO192 FA26");
            writer.newLine();
            writer.write("# Lưu trữ tự động bởi FileDataStore (Người 5)");
            writer.newLine();
            writer.newLine();

            // 1. Lưu danh sách sinh viên: MSSV|Họ và tên
            writer.write(SECTION_STUDENTS);
            writer.newLine();
            for (Student s : studentManager.getAllStudents()) {
                writer.write(s.getStudentId() + "|" + s.getFullName());
                writer.newLine();
            }
            writer.newLine();

            // 2. Lưu danh sách môn học: LOAI_MON|Mã môn|Tên môn|Số tín chỉ
            writer.write(SECTION_COURSES);
            writer.newLine();
            for (Course c : courseManager.getAllCourses()) {
                writer.write(String.format(Locale.US, "%s|%s|%s|%d", 
                        c.getTypeCode(), c.getCourseId(), c.getCourseName(), c.getCredits()));
                writer.newLine();
            }
            writer.newLine();

            // 3. Lưu kết quả học tập: MSSV|Mã môn|Điểm quá trình|Điểm cuối kỳ
            writer.write(SECTION_GRADES);
            writer.newLine();
            for (GradeRecord r : gradeManager.getAllRecords()) {
                if (r.getStudent() == null || r.getCourse() == null) {
                    continue;
                }
                String pScore = (r.getProcessScore() != null) 
                        ? String.format(Locale.US, "%.2f", r.getProcessScore()) 
                        : VALUE_EMPTY;
                String eScore = (r.getExamScore() != null) 
                        ? String.format(Locale.US, "%.2f", r.getExamScore()) 
                        : VALUE_EMPTY;

                writer.write(String.format(Locale.US, "%s|%s|%s|%s",
                        r.getStudent().getStudentId(),
                        r.getCourse().getCourseId(),
                        pScore,
                        eScore));
                writer.newLine();
            }

            writer.flush();
        } catch (IOException e) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            throw new IOException("Lỗi trong quá trình ghi dữ liệu ra file tạm: " + e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }

        // Đổi tên file tạm thành file chính (Atomic replace)
        if (targetFile.exists()) {
            if (!targetFile.delete()) {
                // Nếu không xóa được trực tiếp, thử backup hoặc ném lỗi
                File backupFile = new File(this.dataFilePath + ".bak");
                if (backupFile.exists()) {
                    backupFile.delete();
                }
                if (!targetFile.renameTo(backupFile)) {
                    throw new IOException("Không thể ghi đè file dữ liệu cũ: " + targetFile.getAbsolutePath());
                }
            }
        }

        if (!tempFile.renameTo(targetFile)) {
            throw new IOException("Không thể hoàn tất lưu file từ file tạm sang: " + targetFile.getAbsolutePath());
        }
    }

    // ==================== ĐỌC DỮ LIỆU TỪ FILE ====================

    /**
     * Nạp toàn bộ dữ liệu từ file vào hệ thống.
     * Cơ chế an toàn: Đọc và parse toàn bộ dữ liệu vào bộ nhớ đệm tạm thời trước.
     * Nếu xảy ra bất kỳ lỗi định dạng/hỏng file nào, ném ngoại lệ và GIỮ NGUYÊN trạng thái dữ liệu cũ của các Manager.
     * 
     * @return true nếu đọc thành công, false nếu file chưa tồn tại
     * @throws IOException nếu có lỗi đọc file hoặc dữ liệu bị hỏng
     */
    public synchronized boolean loadData(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager) throws IOException {
        File file = new File(this.dataFilePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        List<Student> tempStudents = new ArrayList<Student>();
        List<Course> tempCourses = new ArrayList<Course>();
        List<TempGradeEntry> tempGrades = new ArrayList<TempGradeEntry>();

        BufferedReader reader = null;
        int lineNumber = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));

            String currentSection = "";
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Bỏ qua dòng trống và dòng ghi chú
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.equalsIgnoreCase(SECTION_STUDENTS) 
                        || line.equalsIgnoreCase(SECTION_COURSES) 
                        || line.equalsIgnoreCase(SECTION_GRADES)) {
                    currentSection = line.toUpperCase();
                    continue;
                }

                String[] parts = line.split("\\|", -1);

                if (SECTION_STUDENTS.equals(currentSection)) {
                    if (parts.length < 2) {
                        throw new IOException("Dữ liệu sinh viên sai định dạng tại dòng " + lineNumber + ": " + line);
                    }
                    String studentId = parts[0].trim();
                    String fullName = parts[1].trim();
                    if (studentId.isEmpty() || fullName.isEmpty()) {
                        throw new IOException("Mã SV hoặc Họ tên trống tại dòng " + lineNumber);
                    }
                    tempStudents.add(new Student(studentId, fullName));

                } else if (SECTION_COURSES.equals(currentSection)) {
                    if (parts.length < 4) {
                        throw new IOException("Dữ liệu môn học sai định dạng tại dòng " + lineNumber + ": " + line);
                    }
                    String type = parts[0].trim();
                    String courseId = parts[1].trim();
                    String courseName = parts[2].trim();
                    int credits = CourseManager.parseAndValidateCredits(parts[3].trim());

                    Course course = CourseManager.createCourse(type, courseId, courseName, credits);
                    tempCourses.add(course);

                } else if (SECTION_GRADES.equals(currentSection)) {
                    if (parts.length < 4) {
                        throw new IOException("Dữ liệu kết quả sai định dạng tại dòng " + lineNumber + ": " + line);
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
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }

        // Toàn bộ dữ liệu trong file đã parse hợp lệ -> Áp dụng an toàn vào các Manager
        studentManager.clear();
        for (Student s : tempStudents) {
            studentManager.addStudent(s);
        }

        courseManager.clear();
        for (Course c : tempCourses) {
            courseManager.addCourse(c);
        }

        gradeManager.clear();
        for (TempGradeEntry entry : tempGrades) {
            Student s = studentManager.findStudent(entry.studentId);
            Course c = courseManager.findCourseById(entry.courseId);
            if (s != null && c != null) {
                gradeManager.createOrUpdateRecord(s, c, entry.processScore, entry.examScore);
            }
        }

        return true;
    }

    private static Double parseScoreValue(String str, int line) throws IOException {
        if (str.isEmpty() || VALUE_EMPTY.equalsIgnoreCase(str) || "NULL".equalsIgnoreCase(str)) {
            return null; // Điểm thiếu
        }
        try {
            double val = Double.parseDouble(str);
            GradeManager.validateScore(val);
            return val;
        } catch (Exception e) {
            throw new IOException("Giá trị điểm số không hợp lệ tại dòng " + line + ": '" + str + "' (" + e.getMessage() + ")");
        }
    }

    // ==================== KHỞI TẠO DỮ LIỆU MẪU BAN ĐẦU ====================

    /**
     * Nạp dữ liệu mẫu ban đầu theo chuẩn đề bài P05:
     * - Sinh viên: QE210001 (Nguyễn An), QE210002 (Trần Bình - chưa có điểm)
     * - Môn học:
     *   + LT101: Nhập môn lý thuyết (Lý thuyết, 3 TC)
     *   + TH101: Thực hành cơ bản (Thực hành, 2 TC)
     *   + LT102: Chuyên đề cơ sở (Lý thuyết, 2 TC)
     * - Kết quả học tập của An:
     *   + LT101: QT = 8.0, CK = 6.0 (Tổng kết: 6.60)
     *   + TH101: QT = 8.0, CK = 6.0 (Tổng kết: 7.00)
     *   + LT102: QT = 9.0, CK = null (Thiếu điểm cuối kỳ -> CHUA_DU_DIEM)
     */
    public static void initializeSampleData(StudentManager studentManager, CourseManager courseManager, GradeManager gradeManager) {
        studentManager.clear();
        courseManager.clear();
        gradeManager.clear();

        // 1. Thêm sinh viên mẫu
        Student an = new Student("QE210001", "Nguyễn An");
        Student binh = new Student("QE210002", "Trần Bình");
        studentManager.addStudent(an);
        studentManager.addStudent(binh);

        // 2. Thêm môn học mẫu
        Course lt101 = CourseManager.createCourse("THEORY", "LT101", "Nhập môn lý thuyết", 3);
        Course th101 = CourseManager.createCourse("PRACTICE", "TH101", "Thực hành cơ bản", 2);
        Course lt102 = CourseManager.createCourse("THEORY", "LT102", "Chuyên đề cơ sở", 2);

        courseManager.addCourse(lt101);
        courseManager.addCourse(th101);
        courseManager.addCourse(lt102);

        // 3. Thêm điểm mẫu cho An
        gradeManager.createOrUpdateRecord(an, lt101, 8.0, 6.0);
        gradeManager.createOrUpdateRecord(an, th101, 8.0, 6.0);
        gradeManager.createOrUpdateRecord(an, lt102, 9.0, null);
    }

    private static class TempGradeEntry {
        String studentId;
        String courseId;
        Double processScore;
        Double examScore;

        TempGradeEntry(String studentId, String courseId, Double processScore, Double examScore) {
            this.studentId = studentId;
            this.courseId = courseId;
            this.processScore = processScore;
            this.examScore = examScore;
        }
>>>>>>> d6e062820c9d101bd618de4c953b90d314bcbc27
    }
}

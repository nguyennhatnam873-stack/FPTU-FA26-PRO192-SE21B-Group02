package studentgrademanager.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;

/**
 * Lớp phụ trách xuất dữ liệu bảng điểm ra định dạng CSV chuẩn RFC 4180.
 * Phụ trách: Người 5 — Lưu/đọc file & CSV
 */
public class CsvExporter {

    public static final String STATUS_COMPLETE = "DU_DIEM";
    public static final String STATUS_INCOMPLETE = "CHUA_DU_DIEM";

    public static final String CSV_HEADER = "MSSV,Ho va ten,Ma mon,Ten mon,So tin chi,Loai mon,Diem qua trinh,Diem cuoi ky,Diem tong ket,Trang thai";

    /**
     * Chuẩn hóa và escape một ô (cell) dữ liệu theo chuẩn RFC 4180.
     */
    public static String escapeCsvCell(String value) {
        if (value == null) {
            return "";
        }
        String text = value.trim();
        boolean mustQuote = text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r");
        if (mustQuote) {
            text = text.replace("\"", "\"\"");
            return "\"" + text + "\"";
        }
        return text;
    }

    /**
     * Định dạng điểm số cho CSV:
     * - null -> để trống ("")
     * - số thực -> "%.2f" với dấu chấm thập phân theo chuẩn Locale.US
     */
    public static String formatScoreForCsv(Double score) {
        if (score == null) {
            return "";
        }
        return String.format(Locale.US, "%.2f", score);
    }

    /**
     * Tạo dòng CSV cho một bản ghi GradeRecord.
     */
    public static String formatRecordToCsvLine(Student student, GradeRecord record) {
        if (record == null || record.getCourse() == null) {
            return "";
        }
        Course course = record.getCourse();
        String studentId = (student != null) ? student.getStudentId() : "";
        String fullName = (student != null) ? student.getFullName() : "";

        String processStr = formatScoreForCsv(record.getProcessScore());
        String examStr = formatScoreForCsv(record.getExamScore());
        Double finalScore = record.getFinalScore();
        String finalStr = formatScoreForCsv(finalScore);

        String status = record.isComplete() ? STATUS_COMPLETE : STATUS_INCOMPLETE;

        StringBuilder sb = new StringBuilder();
        sb.append(escapeCsvCell(studentId)).append(",");
        sb.append(escapeCsvCell(fullName)).append(",");
        sb.append(escapeCsvCell(course.getCourseId())).append(",");
        sb.append(escapeCsvCell(course.getCourseName())).append(",");
        sb.append(course.getCredits()).append(",");
        sb.append(escapeCsvCell(course.getCourseType())).append(",");
        sb.append(escapeCsvCell(processStr)).append(",");
        sb.append(escapeCsvCell(examStr)).append(",");
        sb.append(escapeCsvCell(finalStr)).append(",");
        sb.append(escapeCsvCell(status));

        return sb.toString();
    }

    /**
     * Xuất bảng điểm của một sinh viên ra file CSV (Ca kiểm thử D08).
     */
    public static void exportTranscriptToCsv(Transcript transcript, File targetFile) throws IOException {
        if (transcript == null) {
            throw new IllegalArgumentException("Bảng điểm không được null.");
        }
        if (targetFile == null) {
            throw new IllegalArgumentException("File đích không được null.");
        }

        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        BufferedWriter writer = null;
        try {
            // Sử dụng UTF-8 kèm UTF-8 BOM (\uFEFF) để Microsoft Excel hiển thị tiếng Việt có dấu chuẩn xác
            FileOutputStream fos = new FileOutputStream(targetFile);
            writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.write('\uFEFF');

            // 1. Dòng Tiêu đề (Header)
            writer.write(CSV_HEADER);
            writer.newLine();

            // 2. Các dòng chi tiết kết quả môn học
            Student student = transcript.getStudent();
            List<GradeRecord> records = transcript.getRecords();
            for (GradeRecord r : records) {
                String line = formatRecordToCsvLine(student, r);
                writer.write(line);
                writer.newLine();
            }

            // 3. Dòng thống kê tổng hợp ở chân bảng điểm
            writer.newLine();
            writer.write("# THONG KE TONG HOP");
            writer.newLine();
            writer.write("Sinh vien," + escapeCsvCell(student.getStudentId()) + " - " + escapeCsvCell(student.getFullName()));
            writer.newLine();
            writer.write("Diem trung binh tich luy (GPA)," + escapeCsvCell(transcript.getFormattedGPA()));
            writer.newLine();
            writer.write(String.format("So mon dat du diem,%d/%d mon", 
                    transcript.getCompletedCourses(), transcript.getTotalCourses()));
            writer.newLine();
            writer.write(String.format("So tin chi hoan thanh,%d/%d tin chi", 
                    transcript.getCompletedCredits(), transcript.getTotalCredits()));
            writer.newLine();

            writer.flush();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Xuất bảng điểm sinh viên theo đường dẫn file (String).
     */
    public static void exportTranscriptToCsv(Transcript transcript, String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Đường dẫn file không được để trống.");
        }
        exportTranscriptToCsv(transcript, new File(filePath.trim()));
    }

    /**
     * Xuất toàn bộ danh sách điểm của tất cả sinh viên ra CSV.
     */
    public static void exportAllRecordsToCsv(List<GradeRecord> records, String targetFilePath) throws IOException {
        if (records == null || targetFilePath == null) {
            throw new IllegalArgumentException("Danh sách kết quả và đường dẫn không được null.");
        }

        File file = new File(targetFilePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write('\uFEFF');
            writer.write(CSV_HEADER);
            writer.newLine();

            for (GradeRecord r : records) {
                writer.write(formatRecordToCsvLine(r.getStudent(), r));
                writer.newLine();
            }
        }
    }
}

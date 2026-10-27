package studentgrademanager.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;

public class CsvExporter {

    public static void exportTranscriptToCsv(Transcript transcript, String targetFilePath) throws IOException {
        if (transcript == null || targetFilePath == null) {
            throw new IllegalArgumentException("Dữ liệu bảng điểm và đường dẫn không được null.");
        }

        File file = new File(targetFilePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("\uFEFF");

            Student s = transcript.getStudent();
            writer.write(String.format("BẢNG ĐIỂM SINH VIÊN,%s - %s\n", escapeCsv(s.getStudentId()), escapeCsv(s.getFullName())));
            writer.write("Mã môn,Tên môn học,Loại môn,Số tín chỉ,Điểm quá trình,Điểm cuối kỳ,Điểm tổng kết,Trạng thái\n");

            for (GradeRecord r : transcript.getRecords()) {
                String pStr = r.getProcessScore() == null ? "" : String.format(Locale.US, "%.2f", r.getProcessScore());
                String eStr = r.getExamScore() == null ? "" : String.format(Locale.US, "%.2f", r.getExamScore());
                Double finalScore = r.getFinalScore();
                String fStr = finalScore == null ? "" : String.format(Locale.US, "%.2f", finalScore);

                writer.write(String.format("%s,%s,%s,%d,%s,%s,%s,%s\n",
                        escapeCsv(r.getCourse().getCourseId()),
                        escapeCsv(r.getCourse().getCourseName()),
                        escapeCsv(r.getCourse().getCourseType()),
                        r.getCourse().getCredits(),
                        pStr,
                        eStr,
                        fStr,
                        escapeCsv(r.getStatus())));
            }

            writer.write(String.format("Tổng số môn,%d\n", transcript.getRecords().size()));
            writer.write(String.format("Số môn hoàn thành,%d\n", transcript.getCompleteRecords().size()));
            writer.write(String.format("Tín chỉ tích lũy,%d/%d\n", transcript.getCompletedCredits(), transcript.getTotalEnrolledCredits()));
            writer.write(String.format("Điểm trung bình (GPA),%s\n", transcript.getFormattedGpa()));
        }
    }

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
            writer.write("\uFEFF");
            writer.write("MSSV,Họ tên,Mã môn,Tên môn học,Loại môn,Số tín chỉ,Điểm quá trình,Điểm cuối kỳ,Điểm tổng kết,Trạng thái\n");

            for (GradeRecord r : records) {
                String pStr = r.getProcessScore() == null ? "" : String.format(Locale.US, "%.2f", r.getProcessScore());
                String eStr = r.getExamScore() == null ? "" : String.format(Locale.US, "%.2f", r.getExamScore());
                Double finalScore = r.getFinalScore();
                String fStr = finalScore == null ? "" : String.format(Locale.US, "%.2f", finalScore);

                writer.write(String.format("%s,%s,%s,%s,%s,%d,%s,%s,%s,%s\n",
                        escapeCsv(r.getStudent().getStudentId()),
                        escapeCsv(r.getStudent().getFullName()),
                        escapeCsv(r.getCourse().getCourseId()),
                        escapeCsv(r.getCourse().getCourseName()),
                        escapeCsv(r.getCourse().getCourseType()),
                        r.getCourse().getCredits(),
                        pStr,
                        eStr,
                        fStr,
                        escapeCsv(r.getStatus())));
            }
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

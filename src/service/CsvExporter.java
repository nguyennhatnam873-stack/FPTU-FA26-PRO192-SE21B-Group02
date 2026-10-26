package service;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import model.GradeRecord;
import model.Transcript;

/**
 * Lop CsvExporter: Xuat bang diem ra file dinh dang CSV.
 * Phu trach chinh: Nguoi 5 (Luu/doc file & CSV) - Ca test: D08.
 * 
 * Quy tac CSV theo de bai PRO192 P05:
 * - Co dong header ro rang.
 * - Phan cach bang dau phay (,), phan thap phan dung dau cham (.).
 * - Thanh phan con thieu de trong o kem trang thai CHUA_DU_DIEM.
 * - Diem 0.0 phai xuat ro 0.00.
 * - Escape/Quote chuoi co chua dau phay hoac dau nhay kep.
 */
public class CsvExporter {

    public void export(Transcript transcript, String filePath) {
        if (transcript == null) {
            throw new IllegalArgumentException("Transcript khong duoc null.");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Duong dan file xuat CSV khong duoc rong.");
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            // Header theo yeu cau de bai
            writer.write("MSSV,HoTen,MaMon,TenMon,LoaiMon,TinChi,DiemQuaTrinh,DiemCuoiKy,DiemTongKet,TrangThai");
            writer.newLine();

            String studentId = transcript.getStudent().getStudentId();
            String fullName = escapeCsv(transcript.getStudent().getFullName());

            for (GradeRecord r : transcript.getRecords()) {
                String courseId = r.getCourse().getCourseId();
                String courseName = escapeCsv(r.getCourse().getCourseName());
                String courseType = r.getCourse().getCourseType();
                int credits = r.getCourse().getCredits();

                // Xu ly diem thieu de trong, diem 0 xuat 0.00
                String processStr = (r.getProcessScore() == null) ? "" : String.format(java.util.Locale.US, "%.2f", r.getProcessScore());
                String examStr = (r.getExamScore() == null) ? "" : String.format(java.util.Locale.US, "%.2f", r.getExamScore());

                Double finalScore = r.getFinalScore();
                String finalStr = (finalScore == null) ? "" : String.format(java.util.Locale.US, "%.2f", finalScore);
                String status = r.getStatus();

                writer.write(String.format("%s,%s,%s,%s,%s,%d,%s,%s,%s,%s",
                        studentId, fullName, courseId, courseName, courseType, credits,
                        processStr, examStr, finalStr, status));
                writer.newLine();
            }

            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Loi khi ghi file CSV: " + e.getMessage(), e);
        }
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentgrademanager.storage;

import studentgrademanager.models.GradeRecord;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author ASUS-PRO
 */
public class CsvExporter {

    public void exportGrades(List<GradeRecord> records, String filePath)
            throws IOException {

        File file = new File(filePath);

        // Tạo thư mục cha nếu chưa tồn tại
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file),
                        StandardCharsets.UTF_8))) {

            writer.write("StudentID,StudentName,CourseCode,CourseName,Credits,ProcessScore,ExamScore,FinalGrade");
            writer.newLine();

            for (GradeRecord record : records) {
                writer.write(
                    record.getStudent().getStudentId() + "," +
                    record.getStudent().getFullName() + "," +
                    record.getCourse().getCode() + "," +
                    record.getCourse().getName() + "," +
                    record.getCourse().getCredits() + "," +
                    (record.getProcessScore() == null ? "" : record.getProcessScore()) + "," +
                    (record.getExamScore() == null ? "" : record.getExamScore()) + "," +
                    (record.getFinalGrade() == null ? "" : record.getFinalGrade())
                );
                writer.newLine();
            }
        }
    }
}


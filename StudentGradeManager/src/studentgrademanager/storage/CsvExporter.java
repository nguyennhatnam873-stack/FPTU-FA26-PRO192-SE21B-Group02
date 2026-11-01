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

public class CsvExporter {
    public static final String STATUS_COMPLETE = "DU_DIEM";
    public static final String STATUS_INCOMPLETE = "CHUA_DU_DIEM";
    public static final String CSV_HEADER = "MSSV,Ho va ten,Ma mon,Ten mon,So tin chi,Loai mon,Diem qua trinh,Diem cuoi ky,Diem tong ket,Trang thai";
    
    public static String escapeCsvCell(String value){
        if (value == null){
            return "";
        }
        String text = value.trim();
        boolean mustQuote = text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r");
        if (mustQuote){
            text = text.replace("\"", "\"\"");
            return "\"" + text + "\"";
        }
        return text;
    }
    
    public static String formatScoreForCsv(Double score){
        if (score == null){
            return "";
        }
        return String.format(Locale.US, "%.2f", score);
    }
    
    public static String formatRecordToCsvLine(Student student, GradeRecord record){
        if (record == null || record.getCourse() == null){
            return "";
        }
        Course course = record.getCourse();
        String studentId = (student != null) ? student.getStudentId(): "";
        String fullName = (student != null) ? student.getFullName(): "";
        
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
        sb.append(escapeCsvCell(status)).append(",");
        
        return sb.toString();
    }
    
    public static void exportTranscriptToCsv(Transcript transcript, File targetFile) throws IOException{
        if (transcript == null || targetFile == null){
            throw new IllegalArgumentException("Du lieu bang diem va file dich khong duoc null.");
        }
        
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()){
            parentDir.mkdir();
        }
        
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(targetFile);
            writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            
            writer.write('\uFEFF');
            writer.write(CSV_HEADER);
            writer.newLine();
            
            Student student = transcript.getStudent();
            List<GradeRecord> records = transcript.getRecords();
            for (GradeRecord r : records){
                writer.write(formatRecordToCsvLine(student, r));
                writer.newLine();
            }
            
            writer.newLine();
            writer.write("# THONG KE TONG HOP");
            writer.newLine();
            writer.write("Sinh vien," + escapeCsvCell(student.getStudentId()) + " - " + escapeCsvCell(student.getFullName()));
            writer.newLine();
            writer.write("Diem trung binh tich luy (GPA)," + escapeCsvCell(transcript.getFormattedGPA()));
            writer.newLine();
            writer.write(String.format("So tin chi hoan thanh,%d/%d tin chi", transcript.getCompletedCredits(), transcript.getTotalCredits()));
            writer.newLine();
            
            writer.flush();
        } finally {
            if (writer != null){
                try {
                    writer.close();
                } catch (IOException ignored){
                }
            } 
        }
    }
    
    public static void exportTranscriptToCsv(Transcript transcript, String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()){
            throw new IllegalArgumentException("Duong dan file khong duoc de trong.");
        }
        exportTranscriptToCsv(transcript, new File(filePath.trim()));
    }
    
    public static void exportAllRecordToCsv(List<GradeRecord> records, String targetFilePath) throws IOException {
        if (records == null || targetFilePath == null){
            throw new IllegalArgumentException("Danh sach ket qua va duong dan khong duoc null.");
        }
        
        File file = new File(targetFilePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()){
            parent.mkdir();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))){
            writer.write('\uFEFF');
            writer.write(CSV_HEADER);
            writer.newLine();
            
            for (GradeRecord r : records){
                writer.write(formatRecordToCsvLine(r.getStudent(), r));
                writer.newLine();
            }
        }
    }    
}

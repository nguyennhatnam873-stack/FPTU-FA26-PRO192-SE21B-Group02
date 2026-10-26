package service;

import java.util.ArrayList;
import java.util.List;
import model.GradeRecord;
import model.Student;
import model.Transcript;

/**
 * Lop TranscriptService: Dich vu tong hop bang diem va xuat bao cao.
 * Phu trach chinh: Nguoi 1 (Truong nhom / Bang diem).
 * 
 * Nghiep vu:
 * - Loc ra danh sach cac mon hoc cua sinh vien can xem.
 * - Tao doi tuong Transcript de tinh toan trung binh co trong so tin chi.
 * - Ket noi voi CsvExporter de xuat file.
 */
public class TranscriptService {
    private CsvExporter csvExporter;

    public TranscriptService() {
        this.csvExporter = new CsvExporter();
    }

    public TranscriptService(CsvExporter csvExporter) {
        this.csvExporter = csvExporter;
    }

    /**
     * Tong hop bang diem cho mot sinh vien tu toan bo danh sach ket qua.
     */
    public Transcript buildTranscript(Student student, List<GradeRecord> allRecords) {
        if (student == null) {
            throw new IllegalArgumentException("Sinh vien khong duoc null.");
        }

        List<GradeRecord> studentRecords = new ArrayList<>();
        if (allRecords != null) {
            for (GradeRecord record : allRecords) {
                if (record.getStudent().getStudentId().equalsIgnoreCase(student.getStudentId())) {
                    studentRecords.add(record);
                }
            }
        }

        return new Transcript(student, studentRecords);
    }

    /**
     * Xuat bang diem sinh vien ra file CSV.
     */
    public void exportTranscriptCSV(Transcript transcript, String filePath) {
        if (this.csvExporter != null) {
            this.csvExporter.export(transcript, filePath);
        }
    }
}

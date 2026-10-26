package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Course;
import model.GradeRecord;
import model.Student;

/**
 * Lop quan ly ket qua hoc tap cua cac cap Sinh vien - Mon hoc (GradeManager).
 * Chua nghiep vu cot loi cua Thanh vien 3 (Nguoi 3):
 * 
 * 1. Chi tao ket qua khi ca Sinh vien va Mon hoc da ton tai hop le.
 * 2. Moi cap (MSSV - Ma mon) chi co duy nhat mot ket qua.
 * 3. Nhap lai cap da co thi chuyen sang che do CAP NHAT ro rang, khong tao dong thu hai.
 * 4. Kiem tra tinh hop le truoc khi cap nhat (neu loi thi giu nguyen du lieu cu).
 */
public class GradeManager {
    private List<GradeRecord> records;

    public GradeManager() {
        this.records = new ArrayList<>();
    }

    /**
     * Tim ban ghi diem theo cap MSSV va Ma mon.
     */
    public GradeRecord findRecord(String studentId, String courseId) {
        if (studentId == null || courseId == null) {
            return null;
        }
        String cleanStudentId = studentId.trim();
        String cleanCourseId = courseId.trim();

        for (GradeRecord record : this.records) {
            if (record.getStudent().getStudentId().equalsIgnoreCase(cleanStudentId)
                    && record.getCourse().getCourseId().equalsIgnoreCase(cleanCourseId)) {
                return record;
            }
        }
        return null;
    }

    /**
     * Ghi nhan ket qua cho mot cap SV - Mon hoc.
     * Neu cap da ton tai -> Chuyen sang cap nhat diem (khong tao dong moi).
     * Neu chua ton tai -> Tao ban ghi GradeRecord moi.
     * 
     * @param studentManager Quan ly sinh vien de kiem tra ton tai.
     * @param studentId MSSV can ghi nhan.
     * @param course Doi tuong Mon hoc da ton tai hop le.
     * @param processScore Diem qua trinh (co the null neu chua co).
     * @param examScore Diem cuoi ky (co the null neu chua co).
     * @return GradeRecord da duoc tao moi hoac cap nhat.
     */
    public GradeRecord addOrUpdateGrade(StudentManager studentManager, String studentId, Course course,
                                        Double processScore, Double examScore) {
        // 1. Kiem tra sinh vien co ton tai trong he thong khong
        if (studentManager == null) {
            throw new IllegalArgumentException("StudentManager khong duoc null.");
        }
        Student student = studentManager.findStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Loi nghiep vu: Khong tim thay sinh vien voi MSSV [" + studentId + "]. Khong tu tao ma!");
        }

        // 2. Kiem tra mon hoc co ton tai khong
        if (course == null) {
            throw new IllegalArgumentException("Loi nghiep vu: Mon hoc khong ton tai. Khong tu tao ma mon!");
        }

        // 3. Kiem tra cap MSSV - Ma mon da ton tai chua
        GradeRecord existing = this.findRecord(student.getStudentId(), course.getCourseId());

        if (existing != null) {
            // Cap nhat tren ban ghi da co (khong tao dong thu hai)
            existing.setProcessScore(processScore);
            existing.setExamScore(examScore);
            return existing;
        } else {
            // Tao moi ban ghi GradeRecord
            GradeRecord newRecord = new GradeRecord(student, course, processScore, examScore);
            this.records.add(newRecord);
            return newRecord;
        }
    }

    /**
     * Lay danh sach tat ca cac ban ghi diem cua mot sinh vien.
     */
    public List<GradeRecord> getRecordsByStudent(String studentId) {
        List<GradeRecord> result = new ArrayList<>();
        if (studentId == null || studentId.trim().isEmpty()) {
            return result;
        }
        String cleanId = studentId.trim();
        for (GradeRecord record : this.records) {
            if (record.getStudent().getStudentId().equalsIgnoreCase(cleanId)) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * Lay toan bo danh sach ket qua.
     */
    public List<GradeRecord> getAllRecords() {
        return Collections.unmodifiableList(this.records);
    }

    /**
     * Tong so ban ghi ket qua hien co.
     */
    public int getRecordCount() {
        return this.records.size();
    }
}

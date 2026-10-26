package test;

import manager.GradeManager;
import manager.StudentManager;
import model.Course;
import model.GradeRecord;
import model.Student;
import model.TheoryCourse;

/**
 * Driver kiem thu rieng cho Thanh vien 3 (Nguoi 3) - PRO192 FA26 P05.
 * Chay va xac minh 2 ca kiem thu bat buoc: D05 va D06.
 */
public class Member3TestDriver {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   KIEM THU PHAN VIEC THANH VIEN 3 (PRO192 P05)   ");
        System.out.println("==================================================\n");

        // 1. Khoi tao moi truong va du lieu mau ban dau
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager();

        Student an = new Student("QE210001", "Nguyen An");
        Student binh = new Student("QE210002", "Tran Binh");
        studentManager.addStudent(an);
        studentManager.addStudent(binh);

        Course lt101 = new TheoryCourse("LT101", "Nhap mon ly thuyet", 3);

        // Nap ket qua ban dau cua An (QT: 8, CK: 6)
        gradeManager.addOrUpdateGrade(studentManager, "QE210001", lt101, 8.0, 6.0);
        int initialRecordCount = gradeManager.getRecordCount();

        // -------------------------------------------------------------
        // CA KIEM THU D05: Thu cac gia tri sai & ma khong ton tai
        // -------------------------------------------------------------
        System.out.println("--- [TEST CASE D05] Kiem tra tu choi du lieu sai & ma khong ton tai ---");
        boolean d05Passed = true;

        // Thu 1: Ma SV khong ton tai
        try {
            gradeManager.addOrUpdateGrade(studentManager, "SV_KHONG_TON_TAI", lt101, 8.0, 7.0);
            System.err.println("❌ THAT BAI: He thong khong tu choi ma SV khong ton tai!");
            d05Passed = false;
        } catch (IllegalArgumentException e) {
            System.out.println("✅ DAT: Tu choi ma SV khong ton tai -> " + e.getMessage());
        }

        // Thu 2: Mon hoc khong ton tai (null)
        try {
            gradeManager.addOrUpdateGrade(studentManager, "QE210001", null, 8.0, 7.0);
            System.err.println("❌ THAT BAI: He thong khong tu choi mon hoc null!");
            d05Passed = false;
        } catch (IllegalArgumentException e) {
            System.out.println("✅ DAT: Tu choi mon hoc khong ton tai -> " + e.getMessage());
        }

        // Thu 3: Diem am (-0.01)
        try {
            gradeManager.addOrUpdateGrade(studentManager, "QE210001", lt101, -0.01, 6.0);
            System.err.println("❌ THAT BAI: He thong khong tu choi diem am (-0.01)!");
            d05Passed = false;
        } catch (IllegalArgumentException e) {
            System.out.println("✅ DAT: Tu choi diem am (-0.01) -> " + e.getMessage());
        }

        // Thu 4: Diem qua 10 (10.01)
        try {
            gradeManager.addOrUpdateGrade(studentManager, "QE210001", lt101, 8.0, 10.01);
            System.err.println("❌ THAT BAI: He thong khong tu choi diem > 10 (10.01)!");
            d05Passed = false;
        } catch (IllegalArgumentException e) {
            System.out.println("✅ DAT: Tu choi diem > 10 (10.01) -> " + e.getMessage());
        }

        // Thu 5: Diem NaN
        try {
            gradeManager.addOrUpdateGrade(studentManager, "QE210001", lt101, Double.NaN, 6.0);
            System.err.println("❌ THAT BAI: He thong khong tu choi diem NaN!");
            d05Passed = false;
        } catch (IllegalArgumentException e) {
            System.out.println("✅ DAT: Tu choi diem NaN -> " + e.getMessage());
        }

        // Kiem tra du lieu mau co giu nguyen ven khong
        if (gradeManager.getRecordCount() == initialRecordCount) {
            System.out.println("✅ DAT: So luong ban ghi giu nguyen ven (" + initialRecordCount + " ban ghi).");
        } else {
            System.err.println("❌ THAT BAI: Du lieu bi bien dong sau cac thao tac sai!");
            d05Passed = false;
        }

        System.out.println("=> KET LUAN CA D05: " + (d05Passed ? "DAT (PASSED)" : "KHONG DAT (FAILED)") + "\n");

        // -------------------------------------------------------------
        // CA KIEM THU D06: Them lai cap da co -> Chuyen sang cap nhat
        // -------------------------------------------------------------
        System.out.println("--- [TEST CASE D06] Them lai cap An - LT101 va cap nhat cuoi ky thanh 8 ---");
        boolean d06Passed = true;

        System.out.println("Truoc khi cap nhat: " + gradeManager.findRecord("QE210001", "LT101"));

        // Thao tac: Them lai cap An-LT101 voi diem CK moi la 8.0 (QT giu nguyen 8.0)
        gradeManager.addOrUpdateGrade(studentManager, "QE210001", lt101, 8.0, 8.0);

        // Kiem tra 1: Khong duoc tang so dong (khong tao dong trung)
        if (gradeManager.getRecordCount() == initialRecordCount) {
            System.out.println("✅ DAT: Khong co dong trung, tong so ban ghi van la: " + gradeManager.getRecordCount());
        } else {
            System.err.println("❌ THAT BAI: Bi tao them dong trung!");
            d06Passed = false;
        }

        // Kiem tra 2: Diem tong ket moi phai la 8.00 (0.3*8 + 0.7*8 = 8.0)
        GradeRecord updatedRecord = gradeManager.findRecord("QE210001", "LT101");
        if (updatedRecord != null && updatedRecord.getFinalScore() != null && Math.abs(updatedRecord.getFinalScore() - 8.0) < 0.001) {
            System.out.println("✅ DAT: Diem tong ket sau cap nhat = " + String.format("%.2f", updatedRecord.getFinalScore()));
        } else {
            System.err.println("❌ THAT BAI: Diem tong ket sau cap nhat khong dung!");
            d06Passed = false;
        }

        System.out.println("Sau khi cap nhat: " + updatedRecord);
        System.out.println("=> KET LUAN CA D06: " + (d06Passed ? "DAT (PASSED)" : "KHONG DAT (FAILED)") + "\n");

        // -------------------------------------------------------------
        // KIEM THU PHU: Khong cho xoa sinh vien da co ket qua
        // -------------------------------------------------------------
        System.out.println("--- [KIEM THU RANG BUOC] Khong cho xoa sinh vien da co diem ---");
        try {
            studentManager.deleteStudent("QE210001", gradeManager.getAllRecords());
            System.err.println("❌ THAT BAI: He thong cho phep xoa sinh vien da co ket qua!");
        } catch (IllegalStateException e) {
            System.out.println("✅ DAT: Tu choi xoa sinh vien da co diem -> " + e.getMessage());
        }

        // Thu xoa sinh vien chua co ket qua (Tran Binh)
        try {
            boolean deleted = studentManager.deleteStudent("QE210002", gradeManager.getAllRecords());
            if (deleted) {
                System.out.println("✅ DAT: Cho phep xoa sinh vien chua co ket qua (Tran Binh).");
            }
        } catch (Exception e) {
            System.err.println("❌ THAT BAI khi xoa sinh vien chua co ket qua: " + e.getMessage());
        }
    }
}

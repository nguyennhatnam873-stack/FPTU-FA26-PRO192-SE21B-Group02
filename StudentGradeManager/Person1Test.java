package studentgrademanager;

import java.util.List;
import studentgrademanager.models.*;
import studentgrademanager.services.*;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.models.PracticeCourse;
import studentgrademanager.models.Transcript;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;
import studentgrademanager.services.TranscriptService;

/**
 * Bo kiem thu tu dong cho Nguoi 1: Tráncript and TranscriptService.
 * Ca kiem thu: D02, D04, D06, D07.
 */
public class Person1Test{
    private static int passCount = 0;
    private static int failCount = 0;
    public static void main(String[] args){
        System.out.println("============================================================");
        System.out.println(" KIEM THU MEMBER 1(BANG DIEM VA TRUONG NHOM) ");
        System.out.println("============================================================");
        StudentManager sm = new StudentManager();
        GradeManager gm = new GradeManager();
        TranscriptService ts = new TranscriptService(sm, gm);
        
        sm.addStudent("QE210001","Nguyen Van An");
        sm.addStudent("QE210002", "Tran Binh");
        
        Course lt101 = new TheoryCourse("LT101", "Lap trinh huong doi tuong", 3);
        Course th101 = new PracticeCourse("TH101", "Thuc hanh OOP", 2);
        
        //An: LT101(7.0 va 8.0 -> TK: 7.70) | TH101 (8.0 va 9.0 -> TK: 8.50)
        gm.createOrUpdateRecord(sm.findStudent("QE210001"), lt101, 7.0, 8.0);
        gm.createOrUpdateRecord(sm.findStudent("QE210001"), th101, 8.0, 9.0);
        
        //Binh: LT101(chua co diem)
        gm.createOrUpdateRecord(sm.findStudent("QE210002"), lt101, null, null);
        
        //Ca D02: Tinh diem trung binh tich luy (GPA) co trong so theo tin chi
        System.out.println("\n--- [CA D02] Tinh GPA co trong so ---");
        Transcript transcriptAn = ts.getTranscript("QE210001");
        // CT: (7.70 * 3 + 8.50 * 2) / (3 +2) = 40.10 / 5 = 8.02
        if("8.02".equals(transcriptAn.getFormattedGpa())){
            inKetQuaPass("D02: Tinh chinh xac GPA co trong so va lam tron 2 chu so (8.02)");
        }else{
            inKetQuaFail("D02: Tinh sai GPA, ket qua thuc te: "+transcriptAn.getFormattedGpa());
        }
        //Ca D04: Xu ly khi chua co diem hoac mon hoc bi thieu diem
        Transcript transcriptBinh = ts.getTranscript("QE210002");
        if(Transcript.NO_GPA_MESSAGE.equals(transcriptBinh.getFormattedGpa())
                &&! transcriptBinh.hasAverageScore()
                && transcriptBinh.calculateRawWeightedGpa() == null){
            inKetQuaPass("D04: Khong tinh GPA cho mon thieu diem, bao dung thong diep CHUA_CO_DIEM_TUNG_BINH");
        }else{
            inKetQuaFail("D04: Xu ly sai khi chua co diem");
        }
        //Ca D06: Bao ve tinh dong goi, khong cho phep sua danh sach tu ben ngoai
        System.out.println("\n--- [CA D06] Tinh bat bien doi cua du lieu ---");
        boolean daChanSuaDanhSach = false;
        try{
            List<GradeRecord> records = transcriptAn.getRecords();
            records.add(null);
        }catch(UnsupportedOperationException e){
            daChanSuaDanhSach = true;
        }
        if(daChanSuaDanhSach){
            inKetQuaPass("D06: Danh sach ban ghi diem la chi doc (UnmodifiableList), bao ve an toan du lieu");
        }else{
            inKetQuaFail("D06: Danh sach bi sua doi truc tiep tu ben ngoai ma khong co bao ve");
        }
        //Ca D07: Kiem tra rang buoc hop le (validate) khi khoi tao
        System.out.println("\n--- [CA D07] Kiem tra chan du lieu null hop le ---");
        boolean daChanNull = false;
        try{
            new Transcript(null, null);
        }catch(IllegalArgumentException e){
            daChanNull = true;
        }
        if(daChanNull){
            inKetQuaPass("D07: Tu choi khoi tao Transcript khi sinh vien truyen vao la null");
        }else{
            inKetQuaFail("D07: Cho phep khoi tao Transcript voi sinh vien null");
        }
        //TONG KET
        System.out.println("\n==========================================================");
        System.out.printf(" KET QUA: %d/4 PASS | %d/4 FAIL%n", passCount, failCount);
        System.out.println("============================================================");
    }
        private static void inKetQuaPass(String thongBao){
            System.out.println("[PASS] "+thongBao);
            passCount++;
        }
        private static void inKetQuaFail(String thongBao){
            System.out.println("[FAIL] "+thongBao);
            failCount++;
        }
    
}

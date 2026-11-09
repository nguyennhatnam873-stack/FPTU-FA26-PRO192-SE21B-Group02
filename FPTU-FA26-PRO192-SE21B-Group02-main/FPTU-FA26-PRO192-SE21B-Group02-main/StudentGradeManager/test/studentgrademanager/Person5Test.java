package studentgrademanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import studentgrademanager.models.Course;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.models.PracticeCourse;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.services.StudentManager;
import studentgrademanager.services.TranscriptService;
import studentgrademanager.storage.CsvExporter;
import studentgrademanager.storage.FileDataStore;

public class Person5Test {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" Báº®T Äáº¦U KIá»‚M THá»¬ CHO NGÆ¯á»œI 5: LÆ¯U/Äá»ŒC FILE & CSV");
        System.out.println("================================================================\n");

        testD08_ExportAnTranscriptToCsv();
        testD08_CsvEscapingAndFormatting();
        testD09_SaveAndReloadDataPersistence();
        testD10_ZeroScoreVsMissingScorePersistence();
        testCorruptedFileProtection();
        testRelativePathAndDirectoryCreation();

        System.out.println("\n================================================================");
        System.out.printf(" Káº¾T QUáº¢ KIá»‚M THá»¬ NGÆ¯á»œI 5: %d/%d PASS (%.1f%%)\n", 
                passedTests, totalTests, (passedTests * 100.0 / totalTests));
        System.out.println("================================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> Táº¤T Cáº¢ CÃC CA TEST Cá»¦A NGÆ¯á»œI 5 ÄÃƒ Äáº T YÃŠU Cáº¦U 100%! <<<");
        } else {
            System.err.println(">>> CÃ“ CA TEST CHÆ¯A Äáº T! <<<");
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition, String details) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.printf("[PASS] %s: %s\n", testName, details);
        } else {
            System.err.printf("[FAIL] %s: %s\n", testName, details);
        }
    }

    private static void testD08_ExportAnTranscriptToCsv() {
        System.out.println("--- Kiá»ƒm thá»­ Ca D08: Xuáº¥t CSV báº£ng Ä‘iá»ƒm máº«u cá»§a An ---");

        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        TranscriptService ts = new TranscriptService(sm, gm);
        Transcript transcriptAn = ts.getTranscript("QE210001");

        File csvFile = new File("test_output/an_transcript_test.csv");
        if (csvFile.exists()) {
            csvFile.delete();
        }

        try {
            CsvExporter exporter = new CsvExporter();
            exporter.exportTranscript(transcriptAn, csvFile);
            assertTrue("D08 - File CSV Ä‘Æ°á»£c táº¡o", csvFile.exists() && csvFile.length() > 0, 
                    "File " + csvFile.getPath() + " táº¡o thÃ nh cÃ´ng, dung lÆ°á»£ng: " + csvFile.length() + " bytes");

            List<String> lines = readLinesUtf8(csvFile);
            assertTrue("D08 - Header Ä‘Ãºng chuáº©n", lines.size() >= 4, "Sá»‘ dÃ²ng Ä‘á»c Ä‘Æ°á»£c: " + lines.size());

            String header = lines.get(0).replace("\uFEFF", ""); 
            boolean headerOk = header.contains("MSSV") && header.contains("Diem qua trinh") && header.contains("Trang thai");
            assertTrue("D08 - Ná»™i dung Header há»£p lá»‡", headerOk, header);

            String lineLT101 = findLineContaining(lines, "LT101");
            boolean lt101Ok = lineLT101 != null  && lineLT101.contains("8.00") && lineLT101.contains("6.00") && lineLT101.contains("6.60") && lineLT101.contains(CsvExporter.STATUS_COMPLETE);
            assertTrue("D08 - DÃ²ng LT101 Ä‘áº§y Ä‘á»§ Ä‘iá»ƒm", lt101Ok, lineLT101);

        
            String lineTH101 = findLineContaining(lines, "TH101");
            boolean th101Ok = lineTH101 != null && lineTH101.contains("8.00") && lineTH101.contains("6.00") && lineTH101.contains("7.00") && lineTH101.contains(CsvExporter.STATUS_COMPLETE);
            assertTrue("D08 - DÃ²ng TH101 Ä‘áº§y Ä‘á»§ Ä‘iá»ƒm", th101Ok, lineTH101);

            String lineLT102 = findLineContaining(lines, "LT102");
            boolean lt102Ok = lineLT102 != null && lineLT102.contains("9.00") && lineLT102.contains(",,") && lineLT102.contains(CsvExporter.STATUS_INCOMPLETE);
            assertTrue("D08 - DÃ²ng LT102 trá»‘ng cuá»‘i ká»³ & tá»•ng káº¿t", lt102Ok, lineLT102);

        } catch (Exception e) {
            assertTrue("D08 - KhÃ´ng nÃ©m ngoáº¡i lá»‡", false, "Lá»—i: " + e.getMessage());
        } finally {
            if (csvFile.exists()) {
                csvFile.delete();
            }
        }
    }

    private static void testD08_CsvEscapingAndFormatting() {
        System.out.println("--- Kiá»ƒm thá»­ Ca D08 (Bá»• sung): Quoting & Escaping RFC 4180 ---");

        CsvExporter exporter = new CsvExporter();

        String escapedSimple = exporter.escapeCsvCell("Nguyen An");
        assertTrue("Escape chuá»—i thÆ°á»ng", "Nguyen An".equals(escapedSimple), escapedSimple);

        String escapedComma = exporter.escapeCsvCell("Ha Noi, Viet Nam");
        assertTrue("Escape chuá»—i cÃ³ dáº¥u pháº©y", "\"Ha Noi, Viet Nam\"".equals(escapedComma), escapedComma);

        String escapedQuote = exporter.escapeCsvCell("Khoa \"CNTT\"");
        assertTrue("Escape chuá»—i cÃ³ dáº¥u ngoáº·c kÃ©p", "\"Khoa \"\"CNTT\"\"\"".equals(escapedQuote), escapedQuote);

        String formattedScore0 = exporter.formatScoreForCsv(0.0);
        assertTrue("Äiá»ƒm 0 xuáº¥t 0.00", "0.00".equals(formattedScore0), formattedScore0);

        String formattedNull = exporter.formatScoreForCsv(null);
        assertTrue("Äiá»ƒm null xuáº¥t chuá»—i rá»—ng", "".equals(formattedNull), formattedNull);
    }

    private static void testD09_SaveAndReloadDataPersistence() {
        System.out.println("--- Kiá»ƒm thá»­ Ca D09: LÆ°u vÃ  Phá»¥c há»“i toÃ n váº¹n dá»¯ liá»‡u ---");

        String testDataPath = "test_output/data_d09_test.txt";
        File testFile = new File(testDataPath);
        if (testFile.exists()) {
            testFile.delete();
        }

        FileDataStore store = new FileDataStore(testDataPath);

        StudentManager sm1 = new StudentManager();
        CourseManager cm1 = new CourseManager();
        GradeManager gm1 = new GradeManager();
        FileDataStore.initializeSampleData(sm1, cm1, gm1);

        try {
            store.saveData(sm1, cm1, gm1);
            assertTrue("D09 - File Ä‘Æ°á»£c lÆ°u thÃ nh cÃ´ng", testFile.exists() && testFile.length() > 0, "Dung lÆ°á»£ng: " + testFile.length());
            StudentManager sm2 = new StudentManager();
            CourseManager cm2 = new CourseManager();
            GradeManager gm2 = new GradeManager();

            assertTrue("D09 - Dá»¯ liá»‡u má»›i ban Ä‘áº§u rá»—ng", sm2.getStudentCount() == 0 && cm2.getCourseCount() == 0, "Managers rá»—ng");


            boolean loaded = store.loadData(sm2, cm2, gm2);
            assertTrue("D09 - Load dá»¯ liá»‡u thÃ nh cÃ´ng", loaded, "loadData tráº£ vá» true");

            assertTrue("D09 - Phục hồi đúng loại môn Lý thuyết", cm2.findCourseById("LT101") instanceof TheoryCourse, cm2.findCourseById("LT101").toString());
            assertTrue("D09 - Phục hồi đúng loại môn Thực hành", cm2.findCourseById("TH101") instanceof PracticeCourse, cm2.findCourseById("TH101").toString());
            TranscriptService ts2 = new TranscriptService(sm2, gm2);
            Transcript anTranscript = ts2.getTranscript("QE210001");
            Double gpaAn = anTranscript.calculateWeightedGPA();

            boolean gpaOk = (gpaAn != null) && Math.abs(gpaAn - 6.76) < 0.01;
            assertTrue("D09 - TB An phá»¥c há»“i chÃ­nh xÃ¡c 6.76", gpaOk, String.format("Thá»±c táº¿: %.4f (formatted: %s)", gpaAn, anTranscript.getFormattedGPA()));
            assertTrue("D09 - Sá»‘ mÃ´n Ä‘á»§ 2/3", anTranscript.getCompletedCourses() == 2 && anTranscript.getTotalCourses() == 3, "2/3 mÃ´n");
            assertTrue("D09 - Sá»‘ tÃ­n chá»‰ Ä‘á»§ 5/7", anTranscript.getCompletedCredits() == 5 && anTranscript.getTotalCredits() == 7, "5/7 TC");

         
            Transcript binhTranscript = ts2.getTranscript("QE210002");
            assertTrue("D09 - BÃ¬nh chÆ°a cÃ³ Ä‘iá»ƒm TB", binhTranscript.calculateWeightedGPA() == null, binhTranscript.getFormattedGPA());

        } catch (Exception e) {
            assertTrue("D09 - KhÃ´ng gáº·p lá»—i", false, "Lá»—i: " + e.getMessage());
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    private static void testD10_ZeroScoreVsMissingScorePersistence() {
        System.out.println("--- Kiá»ƒm thá»­ Ca D10: PhÃ¢n biá»‡t chÃ­nh xÃ¡c giá»¯a Ä‘iá»ƒm 0 vÃ  Ä‘iá»ƒm thiáº¿u ---");

        String testDataPath = "test_output/data_d10_test.txt";
        File testFile = new File(testDataPath);
        if (testFile.exists()) {
            testFile.delete();
        }

        FileDataStore store = new FileDataStore(testDataPath);

        StudentManager sm1 = new StudentManager();
        CourseManager cm1 = new CourseManager();
        GradeManager gm1 = new GradeManager();
        FileDataStore.initializeSampleData(sm1, cm1, gm1);

       
        Student an = sm1.findStudent("QE210001");
        Course lt101 = cm1.findCourseById("LT101");
        gm1.createOrUpdateRecord(an, lt101, 0.0, null);

        try {

            store.saveData(sm1, cm1, gm1);


            StudentManager sm2 = new StudentManager();
            CourseManager cm2 = new CourseManager();
            GradeManager gm2 = new GradeManager();

            store.loadData(sm2, cm2, gm2);

            GradeRecord reloadedLT101 = gm2.findRecord("QE210001", "LT101");
            assertTrue("D10 - Äiá»ƒm QT cá»§a LT101 váº«n lÃ  0.0", 
                    reloadedLT101.getProcessScore() != null && Math.abs(reloadedLT101.getProcessScore() - 0.0) < 0.0001, 
                    "QT = " + reloadedLT101.getProcessScore());
            assertTrue("D10 - Äiá»ƒm CK cá»§a LT101 váº«n lÃ  null (thiáº¿u)", 
                    reloadedLT101.getExamScore() == null, 
                    "CK = " + reloadedLT101.getExamScore());
            assertTrue("D10 - LT101 chÆ°a Ä‘á»§ Ä‘iá»ƒm", !reloadedLT101.isComplete(), "isComplete() == false");

            TranscriptService ts2 = new TranscriptService(sm2, gm2);
            Transcript anTranscript = ts2.getTranscript("QE210001");
            Double gpaAn = anTranscript.calculateWeightedGPA();

            boolean gpaOk = (gpaAn != null) && Math.abs(gpaAn - 7.00) < 0.0001;
            assertTrue("D10 - TB An chÃ­nh xÃ¡c 7.00", gpaOk, String.format("Thá»±c táº¿: %.2f", gpaAn));
            assertTrue("D10 - Äá»§ Ä‘Ãºng 1/3 mÃ´n", anTranscript.getCompletedCourses() == 1 && anTranscript.getTotalCourses() == 3, "1/3 mÃ´n");
            assertTrue("D10 - Äá»§ Ä‘Ãºng 2/7 tÃ­n chá»‰", anTranscript.getCompletedCredits() == 2 && anTranscript.getTotalCredits() == 7, "2/7 tÃ­n chá»‰");

        } catch (Exception e) {
            assertTrue("D10 - KhÃ´ng gáº·p lá»—i", false, "Lá»—i: " + e.getMessage());
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    private static void testCorruptedFileProtection() {
        System.out.println("--- Kiá»ƒm thá»­ Báº£o vá»‡ Dá»¯ liá»‡u: File há»ng nÃ©m lá»—i vÃ  khÃ´ng xÃ³a Manager ---");

        String corruptPath = "test_output/corrupt_test.txt";
        File corruptFile = new File(corruptPath);
        File parent = corruptFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(corruptFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            osw.write("[STUDENTS]\nQE210001|An\n\n[COURSES]\nTHEORY|LT101|Mon hoc|INVALID_CREDIT\n");
            osw.close();

            StudentManager sm = new StudentManager();
            CourseManager cm = new CourseManager();
            GradeManager gm = new GradeManager();
            sm.addStudent(new Student("EXISTING", "Du lieu cu"));

            FileDataStore store = new FileDataStore(corruptPath);
            boolean threwException = false;
            try {
                store.loadData(sm, cm, gm);
            } catch (Exception e) {
                threwException = true;
            }

            assertTrue("File há»ng phÃ¡t hiá»‡n vÃ  nÃ©m ngoáº¡i lá»‡", threwException, "Báº¯t Ä‘Æ°á»£c lá»—i Ä‘á»‹nh dáº¡ng file");
            assertTrue("Dá»¯ liá»‡u cÅ© trong Manager váº«n Ä‘Æ°á»£c báº£o vá»‡", sm.findStudent("EXISTING") != null, "Manager khÃ´ng bá»‹ xÃ³a");

        } catch (Exception e) {
            assertTrue("Xá»­ lÃ½ file há»ng an toÃ n", false, e.getMessage());
        } finally {
            if (corruptFile.exists()) {
                corruptFile.delete();
            }
        }
    }

    private static void testRelativePathAndDirectoryCreation() {
        System.out.println("--- Kiá»ƒm thá»­ ÄÆ°á»ng dáº«n tÆ°Æ¡ng Ä‘á»‘i & Tá»± táº¡o thÆ° má»¥c ---");

        String deepPath = "test_output/sub1/sub2/data_deep.txt";
        File deepFile = new File(deepPath);
        if (deepFile.exists()) {
            deepFile.delete();
        }

        FileDataStore store = new FileDataStore(deepPath);
        StudentManager sm = new StudentManager();
        CourseManager cm = new CourseManager();
        GradeManager gm = new GradeManager();
        FileDataStore.initializeSampleData(sm, cm, gm);

        try {
            store.saveData(sm, cm, gm);
            assertTrue("Tá»± táº¡o thÆ° má»¥c cha khi lÆ°u", deepFile.exists(), "File tá»“n táº¡i táº¡i: " + deepPath);
        } catch (Exception e) {
            assertTrue("LÆ°u Ä‘Æ°á»ng dáº«n sÃ¢u thÃ nh cÃ´ng", false, e.getMessage());
        } finally {
            if (deepFile.exists()) {
                deepFile.delete();
            }
        }
    }
    
    private static List<String> readLinesUtf8(File file) throws Exception {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }

    private static String findLineContaining(List<String> lines, String keyword) {
        for (String l : lines) {
            if (l.contains(keyword)) {
                return l;
            }
        }
        return null;
    }
}

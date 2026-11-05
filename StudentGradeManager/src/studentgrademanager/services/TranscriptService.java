/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;

public class TranscriptService implements Serializable{
    private static final long serialVersionUID = 1L;
    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    
    public TranscriptService(StudentManager studentManager, GradeManager gradeManager){
        if(studentManager == null || gradeManager == null){
            throw new IllegalArgumentException("StudentManager va GradeManager khong duoc null.");
        }
        this.gradeManager = gradeManager;
        this.studentManager = studentManager;
    }
    public Transcript getTranscript(String studentId){
        Student student = this.studentManager.findStudent(studentId);
        if(student == null){
            return null;
        }
        List<GradeRecord> records = this.gradeManager.getRecordsByStudent(student.getStudentId());
        return new Transcript(student, records);
    }
    public Transcript generateTranscript(String studentId){
        return getTranscript(studentId);
    }
    public List<Transcript> getAllTranscript(){
        List<Transcript> list = new ArrayList<Transcript>();
        for(Student s : this.studentManager.getAllStudents()){
            list.add(getTranscript(s.getStudentId()));
        }
        return list;
    }
    public void printTranscript(String studentId){
        Transcript transcript = getTranscript(studentId);
        if(transcript == null){
            System.out.println("Khong tim thay sinh vien co ma: "+studentId);
            return;
        }
        Student s = transcript.getStudent();
        System.out.println("============================================================");
        System.out.printf("BANG DIEM: %s - %s%n", s.getStudentId(),s.getFullName());
        System.out.println("============================================================");
        System.out.printf("%-10s | %-25s | %-10s | %-4s | %-7s | %-7s | %-7s | %-12s%n", "MA MON", "TEN MON HOC", "LOAI", "TC", "DIEM QT",
                "DIEM CK", "DIEM TK", "TRANG THAI");
        System.out.println("-------------------------------------------------------------");
        List<GradeRecord> records = transcript.getRecords();
        if(records.isEmpty()){
            System.out.println(" Sinh vien chua dang ky mon hoc nao.");
        }else{
            for(GradeRecord r : records){
                String processStr = (r.getProcessScore() == null) ? "-"
                        : String.format(Locale.US, "%.2f", r.getProcessScore());
                String examStr = (r.getExamScore() == null) ? "-"
                        : String.format(Locale.US, "%.2f", r.getExamScore());
                Double finalScore = r.getFinalScore();
                String finalStr = (finalScore == null) ? "-"
                        : String.format(Locale.US, "%.2f", finalScore);
                System.out.printf("%-10s | %-25s | %-10s | %-4d | %-7s | %-7s | %-7s | %-12s%n", r.getCourse().getCourseId(),
                        truncate(r.getCourse().getCourseName(), 25),
                        r.getCourse().getCourseType(),
                        r.getCourse().getCredits(),
                        processStr, examStr, finalStr, r.getStatus());
            }
        }
        System.out.println("--------------------------------------------------------------");
        System.out.printf("Tong so mon: %d | So mon hoan thanh: %d | Tin chi tich luy: %d/%d%n", records.size(),
                transcript.getCompletedCourses(),
                transcript.getCompletedCredits(),
                transcript.getTotalEnrolledCredits());
        System.out.printf("Diem trung binh tich luy (GPA): %s%n", transcript.getFormattedGpa());
        System.out.println("============================================================\n");
    }
    private String truncate(String text, int maxLength){
        if(text == null){
            return "";
        }
        if(text.length() <= maxLength){
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}

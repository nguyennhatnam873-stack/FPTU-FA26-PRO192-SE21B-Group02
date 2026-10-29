/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.services;

import java.util.List;

import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;
import studentgrademanager.models.Transcript;

public class TranscriptService {

    private final GradeManager gradeManager;

    public TranscriptService(GradeManager gradeManager) {
        if (gradeManager == null) {
            throw new IllegalArgumentException(
                    "GradeManager khong duoc null.");
        }

        this.gradeManager = gradeManager;
    }

    public Transcript createTranscript(String studentId) {
        Student student = null;
        List<GradeRecord> records =
                gradeManager.getGradesByStudent(studentId);

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma sinh vien khong duoc rong.");
        }

        if (records.isEmpty()) {
            throw new IllegalArgumentException(
                    "Khong tim thay bang diem cho sinh vien nay.");
        }

        student = records.get(0).getStudent();

        return new Transcript(student, records);
    }

    public void displayTranscript(String studentId) {
        Transcript transcript = createTranscript(studentId);
        transcript.display();
    }

    public double calculateAverage(String studentId) {
        Transcript transcript = createTranscript(studentId);
        return transcript.calculateAverage();
    }
}

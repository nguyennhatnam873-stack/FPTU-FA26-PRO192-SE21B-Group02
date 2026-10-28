/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;
import studentgrademanager.models.Student;

public class GradeManager {

    private final List<GradeRecord> records =
            new ArrayList<GradeRecord>();

    public GradeRecord addOrUpdateGrade(
            Student student,
            Course course,
            Double processScore,
            Double examScore) {

        if (student == null || course == null) {
            throw new IllegalArgumentException(
                    "Sinh vien va mon hoc khong duoc null.");
        }

        GradeRecord record = findGradeRecord(
                student.getStudentId(), course.getCode());

        if (record == null) {
            record = new GradeRecord(student, course);
            records.add(record);
        }

        record.updateProcessScore(processScore);
        record.updateExamScore(examScore);

        return record;
    }

    public GradeRecord findGradeRecord(
            String studentId, String courseCode) {

        if (studentId == null || courseCode == null) {
            return null;
        }

        for (GradeRecord record : records) {
            boolean sameStudent = record.getStudent()
                    .getStudentId()
                    .equalsIgnoreCase(studentId.trim());

            boolean sameCourse = record.getCourse()
                    .getCode()
                    .equalsIgnoreCase(courseCode.trim());

            if (sameStudent && sameCourse) {
                return record;
            }
        }

        return null;
    }

    public List<GradeRecord> getGradesByStudent(
            String studentId) {

        List<GradeRecord> result =
                new ArrayList<GradeRecord>();

        if (studentId == null) {
            return result;
        }

        for (GradeRecord record : records) {
            if (record.getStudent().getStudentId()
                    .equalsIgnoreCase(studentId.trim())) {
                result.add(record);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public List<GradeRecord> getAllRecords() {
        return Collections.unmodifiableList(
                new ArrayList<GradeRecord>(records));
    }

    public int getRecordCount() {
        return records.size();
    }
    
public void loadGrades(List<GradeRecord> data) {
    records.clear();

    for (GradeRecord record : data) {
        if (record != null) {
            records.add(record);
        }
    }
}
}
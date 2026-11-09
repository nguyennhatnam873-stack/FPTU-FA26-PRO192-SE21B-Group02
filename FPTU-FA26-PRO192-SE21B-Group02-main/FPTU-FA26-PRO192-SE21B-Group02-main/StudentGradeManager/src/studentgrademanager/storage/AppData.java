/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentgrademanager.storage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import studentgrademanager.models.Student;
import studentgrademanager.models.Course;
import studentgrademanager.models.GradeRecord;

public class AppData implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Student> students;
    private List<Course> courses;
    private List<GradeRecord> grades;

    public AppData() {
        this.students = new ArrayList<Student>();
        this.courses = new ArrayList<Course>();
        this.grades = new ArrayList<GradeRecord>();
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<GradeRecord> getGrades() {
        return grades;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import studentgrademanager.services.StudentManager;
import studentgrademanager.services.CourseManager;
import studentgrademanager.services.GradeManager;
import studentgrademanager.ui.MainMenu;

public class Main {

    public static void main(String[] args) {
        StudentManager studentManager = new StudentManager();
        CourseManager courseManager = new CourseManager();
        GradeManager gradeManager = new GradeManager();

        MainMenu menu = new MainMenu(
                studentManager,
                courseManager,
                gradeManager);

        menu.start();
    }
}

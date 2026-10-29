/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ASUS-PRO
 */

import studentgrademanager.models.Course;
import studentgrademanager.models.TheoryCourse;
import studentgrademanager.models.PracticeCourse;

public class CourseTest {

    public static void main(String[] args) {
        Course theory =
                new TheoryCourse("PRO192", "Java OOP", 3);

        Course practice =
                new PracticeCourse("LAB211", "Java Lab", 2);

        System.out.println("=== KIEM THU D01 ===");
        test("Ly thuyet", theory.calculateFinalGrade(8.0, 6.0), 6.6);
        test("Thuc hanh", practice.calculateFinalGrade(8.0, 6.0), 7.0);

        System.out.println("\n=== KIEM THU D11 ===");
        try {
            new TheoryCourse("TEST", "Mon thu", 0);
            System.out.println("FAIL: Tin chi 0");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Tu choi tin chi 0");
        }

        try {
            new TheoryCourse("TEST2", "Mon thu", 7);
            System.out.println("FAIL: Tin chi 7");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Tu choi tin chi 7");
        }

        System.out.println("\n=== KIEM THU DIEM SAI ===");
        try {
            theory.calculateFinalGrade(11.0, 6.0);
            System.out.println("FAIL: Diem vuot 10");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Tu choi diem vuot 10");
        }
    }

    private static void test(
            String name, double actual, double expected) {
        if (Math.abs(actual - expected) < 0.000001) {
            System.out.printf(
                    "PASS: %s = %.2f%n", name, actual);
        } else {
            System.out.printf(
                    "FAIL: %s, mong doi %.2f, thuc te %.2f%n",
                    name, expected, actual);
        }
    }
}

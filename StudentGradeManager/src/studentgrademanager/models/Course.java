/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.models;

import java.io.Serializable;
import java.util.Locale;

public abstract class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String code;
    private final String name;
    private final int credits;

    public Course(String code, String name, int credits) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ma mon khong duoc rong."
            );
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ten mon khong duoc rong."
            );
        }

        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException(
                "Tin chi phai tu 1 den 6."
            );
        }

        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.name = name.trim();
        this.credits = credits;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public int getCredits() {
        return this.credits;
    }

    public abstract double calculateFinalGrade(
        double processScore,
        double examScore
    );

    public abstract String getCourseType();
}

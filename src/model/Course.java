package model;

/**
 * Lop truu tuong Mon hoc (Course <<abstract>>).
 * Dinh nghia cac thuoc tinh va phuong thuc tinh diem da hinh.
 */
public abstract class Course {
    private String courseId;
    private String courseName;
    private int credits;

    public Course() {
    }

    public Course(String courseId, String courseName, int credits) {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma mon hoc khong duoc de trong.");
        }
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten mon hoc khong duoc de trong.");
        }
        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException("Tin chi phai la so nguyen tu 1 den 6.");
        }
        this.courseId = courseId.trim();
        this.courseName = courseName.trim();
        this.credits = credits;
    }

    public String getCourseId() {
        return this.courseId;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public int getCredits() {
        return this.credits;
    }

    /**
     * Phuong thuc truu tuong tinh diem tong ket (da hinh giua Ly thuyet va Thuc hanh).
     */
    public abstract Double calculateFinalScore(Double process, Double exam);

    /**
     * Tra ve loai mon hoc (Ly thuyet hoac Thuc hanh).
     */
    public abstract String getCourseType();

    @Override
    public String toString() {
        return String.format("%s (%s, %d tin chi)", this.courseName, this.courseId, this.credits);
    }
}
